package com.vcarecity.ssdb;

import com.vcarecity.utils.Logger;
import com.vcarecity.utils.MD5Util;
import com.vcarecity.utils.StringUtils;
import com.vcarecity.utils.YamlUtils;
import redis.clients.jedis.Protocol;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SSDBClient {
    ConcurrentHashMap<String, String> oldClusters=new ConcurrentHashMap<String,String>();
    ConcurrentHashMap<String, Cluster> readableCluster=new ConcurrentHashMap<String,Cluster>();
    ConcurrentHashMap<String, Cluster> writableCluster=new ConcurrentHashMap<String,Cluster>();

    Logger logger = null; // 公共日志类log4j，所以异常日志记录必须由这个类完成

    public SSDBClient() {
        logger = Logger.getLogger();
        try {
            PoolableObjectFactoryManager(YamlUtils.getValue("alpha.servers"));
            new Thread() {
                SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
                File file=new File("config/jimg.yaml");
                //获取修改时间
                long lastModifitime = file.lastModified();
                public void run() {
                    while (true) {
                        try {
                            //logger.writeDebugInfo("readableCluster.size():" +readableCluster.size());
                            //logger.writeDebugInfo("writableMasters.size():" +writableCluster.size());
                            long lastModifitime2 = file.lastModified();
                            if(lastModifitime!=lastModifitime2)
                            {
                                logger.writeDebugInfo(file.getName()+"在"+format.format(lastModifitime2)+"被修改过");
                                lastModifitime=lastModifitime2;
                                String clusters=YamlUtils.getValue("alpha.servers",true);
                                logger.writeDebugInfo("clusters:" +clusters);
                                clusters=clusters.replace("[","").replace("]","");
                                String[] allCluster=clusters.split(", ",1000);
                                int db=0;
                                for(String cluster : allCluster) {
                                    if (StringUtils.isNotBlank(cluster)) {
                                        db++;
                                        String clusterid=String.valueOf(db);
                                        if(oldClusters.containsKey(clusterid)) {
                                            String oldcluster=oldClusters.get(clusterid);
                                            if(!oldcluster.equals(cluster)){
                                                modifyCluster(clusterid,oldcluster,cluster);
                                            }
                                        }else{
                                            addCluster(clusterid,cluster);
                                        }
                                    }
                                }

                                logger.writeDebugInfo("变化后的 readableCluster:");
                                showInfo(readableCluster);
                                logger.writeDebugInfo("变化后的 writableMasters:");
                                showInfo(writableCluster);
                            }
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showInfo(ConcurrentHashMap<String, Cluster> clusters){
        for(Map.Entry<String, Cluster> entry : clusters.entrySet()){
            String mapKey = entry.getKey();
            Cluster cluster = entry.getValue();
            //System.out.println("mapKey:"+mapKey);
            System.out.println("cluster.getId():"+cluster.getId());
            SSDB ssdb=cluster.getMaster();
            System.out.println("masterid:"+ssdb.getId());
            ConcurrentHashMap<String, SSDB> readableSlaver=cluster.getSlaver();
            for(Map.Entry<String, SSDB> e : readableSlaver.entrySet()){
                String Key = e.getKey();
                SSDB slaverdb = e.getValue();
                System.out.println("slaverid:"+slaverdb.getId());
            }
        }
    }
    private boolean modifyCluster(String clusterid,String oldCluster,String newCluster) {
        oldClusters.put(clusterid,newCluster);
        String[] oldHosts = oldCluster.trim().split(" ");
        String[] newHosts = newCluster.trim().split(" ");
        ConcurrentHashMap<String, String> oldSlavers=new ConcurrentHashMap<String,String>();
        for(String slaver:oldHosts){
            oldSlavers.put(slaver,slaver);
        }
        ConcurrentHashMap<String, String> newSlavers=new ConcurrentHashMap<String,String>();
        for(String slaver:newHosts){
            newSlavers.put(slaver,slaver);
        }

        if(oldHosts.length<2||newHosts.length<2){
            return false;
        }

        //根据ID取出集群
        Cluster mycluster=readableCluster.get(clusterid);
        String oldMaster=oldHosts[1];
        String newMaster=newHosts[1];

        boolean writable=true;
        if("1".equals(newHosts[0])){
            writable=true;
        }else{
            writable=false;
        }
        if(!oldMaster.equals(newMaster)){
            mycluster.getMaster().release();
            mycluster.setMaster(addSsdb(clusterid,newMaster,writable));
        }

        //处理集群中的从节点
        ConcurrentHashMap<String, SSDB> readableSlaver=mycluster.getSlaver();
        //如果新的slaver组有,而老的slaver组没有则加入
        for(int i=2;i<newHosts.length;i++) {
            String slaver = newHosts[i];
            if(!oldSlavers.containsKey(slaver)){
                readableSlaver.put(slaver, addSsdb(clusterid,slaver,false));
            }
        }
        //如果老的slaver组有，而新的slaver组没有则删除
        for(int i=2;i<oldHosts.length;i++) {
            String slaver = oldHosts[i];
            if(!newSlavers.containsKey(slaver)){
                SSDB ssdb=readableSlaver.get(slaver);
                ssdb.release();
                readableSlaver.remove(slaver);
            }
        }
        mycluster.setSlaver(readableSlaver);

        if(readableCluster.containsKey(clusterid)){
            readableCluster.put(clusterid,mycluster);
        }
        if(writableCluster.containsKey(clusterid)){
            writableCluster.put(clusterid,mycluster);
        }

        if("1".equals(oldHosts[0])&&"0".equals(newHosts[0])){//如果只是变成只读集群，则直接从可写集群组移除即可
            writableCluster.remove(clusterid);
        }else if("0".equals(oldHosts[0])&&"1".equals(newHosts[0])){//如果变成可写集群，则加入可写集群组
            mycluster=readableCluster.get(clusterid);
            writableCluster.put(clusterid,mycluster);
        }

        return true;
    }

    /**
     * 创建ssdb对象，并连接
     * @param clusterid
     * @param master
     * @param writable
     * @return
     */
    private SSDB addSsdb(String clusterid,String master,boolean writable) {
        SSDB ssdb = null;
        //处理 Master 开始
        //192.168.50.47:8888:4500  SSDB所在服务器地址:端口号:是否可写:超时时间
        String[] master_host_port_time = master.trim().split(":");
        String host = master_host_port_time[0];
        if(StringUtils.isBlank(host)) {
            return ssdb;
        }
        //设置端口；默认端口8888
        int port = getIntValue(master_host_port_time[1], Protocol.DEFAULT_PORT);
        //设置超时，默认4500毫秒
        int timeout = getIntValue(master_host_port_time[2], Protocol.DEFAULT_TIMEOUT);
        try {
            ssdb = new SSDB(master,host, port,writable,timeout);
        } catch (Exception e) {}
        //处理 Master 结束
        return ssdb;
    }

    private boolean addCluster(String clusterid,String cluster) {
        oldClusters.put(clusterid,cluster);
        System.out.println("cluster:"+cluster);

        ConcurrentHashMap<String, SSDB> readableSlaver=new ConcurrentHashMap<String,SSDB>();
        String[] hosts = cluster.trim().split(" ");
        if(hosts.length<2){
            return false;
        }
        //1 192.168.10.211:8888:4500 192.168.10.211:8889:4500
        //是否只读集群 Master(地址:端口号:超时时间) Slaver(地址:端口号:超时时间)
        Cluster myCluster=new Cluster();
        myCluster.setId(clusterid);
        boolean writable=true;
        if("1".equals(hosts[0])){
            writable=true;
        }else{
            writable=false;
        }

        //处理 Master 开始
        String master=hosts[1];
        myCluster.setMaster(addSsdb(clusterid,master,writable));
        //处理 Master 结束

        if(hosts.length<3){
            return false;
        }

        //处理 Slaver 开始
        //192.168.50.47:8889:1:4500  SSDB所在服务器地址:端口号:是否可写:超时时间
        for(int i=2;i<hosts.length;i++) {
            String slaver = hosts[i];
            readableSlaver.put(slaver, addSsdb(clusterid,slaver,false));
        }
        myCluster.setSlaver(readableSlaver);
        //处理 Slaver 结束
        readableCluster.put(clusterid,myCluster);
        if(writable){
            writableCluster.put(clusterid,myCluster);
        }
        return true;
    }
    /**
     * 构造函数
     * @param clusters [ip1:port1:timeout1 ip2:port2:timeout2,ip3:port3:timeout3 ip4:port4:timeout4;
     */
    private void PoolableObjectFactoryManager(String clusters) {
        clusters=clusters.replace("[","").replace("]","");
        String[] allCluster=clusters.split(", ",1000);
        int db=0;
        for(String cluster : allCluster) {
            if(StringUtils.isNotBlank(cluster)) {
                db++;
                String clusterid=String.valueOf(db);
                addCluster(clusterid,cluster);
            }
        }
        logger.writeDebugInfo("readableCluster.size():" +readableCluster.size());
        showInfo(readableCluster);
        logger.writeDebugInfo("writableMasters.size():" +writableCluster.size());
        showInfo(writableCluster);

    }

    private int getIntValue(String v, int dft) {
        try {
            if(StringUtils.isNumeric(v.trim())) {
                return Integer.parseInt(v);
            }
        } catch (Exception e) {

        }
        return dft;
    }

    public static int getRandomKey(Map<String, Cluster> map) {
        Integer[] keys = map.keySet().toArray(new Integer[0]);
        Random random = new Random();
        Integer randomKey = keys[random.nextInt(keys.length)];
        System.out.println(randomKey);
        return randomKey;
    }

    public static Cluster getRandomMaster(Map<String, Cluster> map) {
        String[] keys = map.keySet().toArray(new String[0]);
        //System.out.println("keys.length:"+keys.length);
        //System.out.println("keys.toString():"+keys.toString());
        Random random = new Random();
        String randomKey = keys[random.nextInt(keys.length)];
        //System.out.println("randomKey:"+randomKey);
        Cluster cluster=map.get(randomKey);
        //System.out.println("cluster.getId():"+cluster.getId());
        return cluster;
    }

    public static SSDB getRandomSlaver(Map<String, SSDB> map) {
        String[] keys = map.keySet().toArray(new String[0]);
        Random random = new Random();
        String randomKey = keys[random.nextInt(keys.length)];
        //System.out.println(randomKey);
        SSDB ssdb=map.get(randomKey);
        return ssdb;
    }

    /**
     * 此方法专门用于jzmg的文件存储,即将文件的二进制放到ssdb中，返回文件存放的集群ID和MD5值，返回值为:集群ID+MD5
     * @param val 文件的二进制数据体
     * @return 集群ID+MD5
     * @throws Exception
     */
    public String set(byte[] val) throws Exception{
        Cluster cluster=getRandomMaster(writableCluster);
        SSDB ssdb=cluster.getMaster();
        //System.out.println("set cluster.id:"+cluster.getId());
        String key=StringUtils.leftAppendZero(cluster.getId(),4)+ MD5Util.getMD5String(val);
        //System.out.println("set key01:"+key);
        if(ssdb.isOpen()){
             ssdb.set(key, val);
        }else{
            key = set(val);//如果前面取出的不可用，则再回调重新选一个可写服务器
        }
        //System.out.println("set key02:"+key);
        return key;
    }

    public boolean del(byte[] key) throws Exception{
        String str = new String(key);
        String db=str.substring(0,4);
        int id=Integer.parseInt(db);
        Cluster cluster=readableCluster.get(String.valueOf(id));
        boolean bl=false;
        SSDB ssdb=cluster.getMaster();
        if(ssdb.isOpen()){
            ssdb.del(key);
            bl=true;
        }
        return bl;
    }

    public void del(String key) throws Exception{
        del(key.getBytes());
    }

    /***
     *
     * @param key
     * @return null if not found
     * @throws Exception
     */
    public byte[] get(byte[] key) throws Exception{
        String str = new String(key);
        String db=str.substring(0,4);
        int id=Integer.parseInt(db);
        Cluster cluster=readableCluster.get(String.valueOf(id));
        SSDB ssdb=cluster.getMaster();
        byte[] bt=null;
        if(ssdb.isOpen()){
            bt=ssdb.get(key);
        }else{
            ConcurrentHashMap<String, SSDB> readableSlaver=cluster.getSlaver();
            ssdb=getRandomSlaver(readableSlaver);
            if(ssdb.isOpen()) {
                bt=ssdb.get(key);
            }else{
                bt=get(key);//如果主和随机选择的复都没有打开，则重新随机选择一个
            }
        }
        return bt;
    }

    /***
     *
     * @param key
     * @return null if not found
     * @throws Exception
     */
    public byte[] get(String key) throws Exception{
        return get(key.getBytes());
    }

    /**
     * 自定义key的传值方法，即存储按自定义的key,存储，访问时需要提供对应的集群ID，返回结果中已包含集群ID
     * @param key
     * @param val
     * @return
     * @throws Exception
     */
    public Result set(String key,byte[] val) throws Exception{
        Cluster cluster=getRandomMaster(writableCluster);
        SSDB ssdb=cluster.getMaster();
        Result rs=new Result(cluster.getId(),key);
        if(ssdb.isOpen()){
            ssdb.set(key, val);
        }else{
            rs = set(key,val);//如果前面取出的不可用，则再回调重新选一个可写服务器
        }
        return rs;
    }
    /**
     * 自定义key的传值方法，即存储按自定义的key,存储，访问时需要提供对应的集群ID，返回结果中已包含集群ID
     * @param key
     * @param val
     * @return
     * @throws Exception
     */
    public String put(String key,byte[] val) throws Exception{
        Cluster cluster=getRandomMaster(writableCluster);
        SSDB ssdb=cluster.getMaster();
        String clusterid=cluster.getId();
        if(ssdb.isOpen()){
            ssdb.set(key, val);
        }else{
            clusterid = put(key,val);//如果前面取出的不可用，则再回调重新选一个可写服务器
        }
        return clusterid;
    }
    /***
     *
     * @param rs
     * @return null if not found
     * @throws Exception
     */
    public byte[] get(Result rs) throws Exception{
        String key=rs.getKey();
        int id=Integer.parseInt(rs.getClusterid());
        Cluster cluster=readableCluster.get(String.valueOf(id));
        SSDB ssdb=cluster.getMaster();
        byte[] bt=null;
        if(ssdb.isOpen()){
            bt=ssdb.get(key);
        }else{
            ConcurrentHashMap<String, SSDB> readableSlaver=cluster.getSlaver();
            ssdb=getRandomSlaver(readableSlaver);
            if(ssdb.isOpen()) {
                bt=ssdb.get(key);
            }else{
                bt=get(key);//如果主和随机选择的复都没有打开，则重新随机选择一个
            }
        }
        return bt;
    }

    /***
     *
     * @param clusterid
     * @param key
     * @return null if not found
     * @throws Exception
     */
    public byte[] get(String clusterid,String key) throws Exception{
        Cluster cluster=readableCluster.get(clusterid);
        SSDB ssdb=cluster.getMaster();
        byte[] bt=null;
        if(ssdb.isOpen()){
            bt=ssdb.get(key);
        }else{
            ConcurrentHashMap<String, SSDB> readableSlaver=cluster.getSlaver();
            ssdb=getRandomSlaver(readableSlaver);
            if(ssdb.isOpen()) {
                bt=ssdb.get(key);
            }else{
                bt=get(key);//如果主和随机选择的复都没有打开，则重新随机选择一个
            }
        }
        return bt;
    }

    /**
     * 根据当时返回的结果进行删除
     * @param rs
     * @return
     * @throws Exception
     */
    public boolean del(Result rs) throws Exception{
        Cluster cluster=readableCluster.get(rs.getClusterid());
        boolean bl=false;
        SSDB ssdb=cluster.getMaster();
        if(ssdb.isOpen()){
            ssdb.del(rs.getKey());
            bl=true;
        }
        return bl;
    }

    /**
     * 根据集群ID和对应的key进行删除
     * @param clusterid
     * @param key
     * @return
     * @throws Exception
     */
    public boolean del(String clusterid,byte[] key) throws Exception{
        Cluster cluster=readableCluster.get(clusterid);
        boolean bl=false;
        SSDB ssdb=cluster.getMaster();
        if(ssdb.isOpen()){
            ssdb.del(key);
            bl=true;
        }
        return bl;
    }

    public static void main(String[] args) {
        System.out.println(YamlUtils.getValue("alpha.servers"));
        SSDBClient ssdb=new SSDBClient();
    }
}
