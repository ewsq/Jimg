package com.vcarecity.ssdb;

import com.vcarecity.utils.MD5Util;
import com.vcarecity.utils.StringUtils;

public class SSDBNoPoolClient {
    public SSDBNoPoolClient() {

    }
    public String set(byte[] val) throws Exception{
        SSDB ssdb= null;
        //ssdb= new SSDB("10.239.204.34", 15264);
        ssdb= new SSDB("192.168.10.210", 8888);
        //System.out.println("set cluster.id:"+cluster.getId());
        String key= MD5Util.getMD5String(val);
        boolean isConnected=ssdb.isConnected();
        System.out.println("set key01:"+key);
        System.out.println("isConnected:"+isConnected);
        if(isConnected){
            ssdb.set(key, val);
        }else{
            key = set(val);//如果前面取出的不可用，则再回调重新选一个可写服务器
        }
        ssdb.close();
        System.out.println("set key02:"+key);
        return key;
    }
    /***
     * 均衡性的获取对象
     *根据传入的带集群ID的KEY，进行对象查找，如果Master失效，则找Slaver
     * @param key
     * @return null if not found
     * @throws Exception
     */
    public byte[] get(byte[] key) throws Exception{
        return ergodic(key);
    }

    public byte[] ergodic(byte[] key) throws Exception{
        SSDB ssdb01= null;
        //ssdb01= new SSDB("10.239.204.34", 15264);
        ssdb01= new SSDB("192.168.10.210", 8888);
        byte[] respBytes=ssdb01.get(new String(key));
        if(respBytes!=null) {
            System.out.println("respBytes:" + respBytes);
            System.out.println(key + " ssdb01 存在！");
            ssdb01.close();
            return respBytes;
        }else{
            ssdb01.close();
            System.out.println(key + " ssdb01 不存在！");
        }

        SSDB ssdb02= null;
        //ssdb02= new SSDB("10.239.204.34", 15265);
        ssdb02= new SSDB("192.168.10.211", 8888);
        respBytes=ssdb02.get(key);
        if(respBytes!=null) {
            System.out.println("respBytes:" + respBytes);
            System.out.println(key + " ssdb02 存在！");
            ssdb02.close();
            return respBytes;
        }else{
            ssdb02.close();
            System.out.println(key + " ssdb02 不存在！");
        }

        return respBytes;
    }
    /***
     *
     * @param key
     * @return null if not found
     * @throws Exception
     */
    public byte[] get(String key) throws Exception{
        byte[] byteArray=null;
        String db=key.substring(0,4);//得到集群编号
        if(key.length()>32 && StringUtils.isAllNumeric(db)) {
            //正常集群使用
            byteArray = get(key.getBytes());
        }else {
            //解决原有的没有带集群ID的情况
            byteArray = ergodic(key.getBytes());
        }
        return byteArray;
    }

    public void release(){
    }
}
