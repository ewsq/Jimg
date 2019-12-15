package com.vcarecity.utils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Nksocket extends Thread{
    public String ip=null;//连接服务器的IP
    public Integer port=null;//连接服务器的端口
    private Socket socket=null;//套节字对象
    private boolean close = false; // 关闭连接标志位，true表示关闭，false表示连接
    private Integer sotimeout=1*1*10;//超时时间，以毫秒为单位
    //------------------------------------------------------------------------------
    public Nksocket(){
        init();
    }
    public Nksocket(String ip,Integer port){
        setIp(ip);
        setPort(port);
        init();
    }

    /**
     * 初始化socket对象
     */
    public void init(){
        try {
            InetAddress address = InetAddress.getByName(getIp());
            socket = new Socket(address,getPort());
            socket.setKeepAlive(true);//开启保持活动状态的套接字
            socket.setSoTimeout(sotimeout);//设置超时时间
            close=!Send(socket,"2");//发送初始数据，发送成功则表示已经连接上，发送失败表示已经断开
        }catch(UnknownHostException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * 读数据线程
     */
    public void run() {
        while(true){
            //---------读数据---------------------------
            close = isServerClose(socket);//判断是否断开
            if(!close){//没有断开，开始读数据
                String readtext = ReadText(socket);
                if(readtext!=null && readtext.trim().length()>0){
                    System.out.println("读取数据："+readtext);
                }
            }
            //---------创建连接-------------------------
            while(close){//已经断开，重新建立连接
                try{
                    System.out.println("重新建立连接："+getIp()+":"+getPort());
                    InetAddress address = InetAddress.getByName(getIp());
                    socket = new Socket(address,getPort());
                    socket.setKeepAlive(true);
                    socket.setSoTimeout(sotimeout);
                    close = !Send(socket,"2");
                    System.out.println("建立连接成功："+getIp()+":"+getPort());
                }catch(Exception se){
                    System.out.println("创建连接失败:"+getIp()+":"+getPort());
                    close=true;
                }
            }
        }
    }
    /**
     * 发送数据，发送失败返回false,发送成功返回true
     * @param csocket
     * @param message
     * @return
     */
    public Boolean Send(Socket csocket,String message){
        try{
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
            return true;
        }catch(Exception se){
            se.printStackTrace();
            return false;
        }
    }
    /**
     * 读取数据，返回字符串类型
     * @param csocket
     * @return
     */
    public String ReadText(Socket csocket){
        try{
            csocket.setSoTimeout(sotimeout);
            InputStream input = csocket.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            char[] sn = new char[1000];
            in.read(sn);
            String sc = new String(sn);
            return sc;
        }catch(IOException se){
            return null;
        }
    }
    /**
     * 判断是否断开连接，断开返回true,没有返回false
     * @param socket
     * @return
     */
    public Boolean isServerClose(Socket socket){
        try{
            socket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        }catch(Exception se){
            return true;
        }
    }

    //------------------------------------------------------------------------------
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public Integer getPort() {
        return port;
    }
    public void setPort(Integer port) {
        this.port = port;
    }


    /**
     * 测试
     * @param ags
     */
    public static void main(String[] ags){
        Nksocket nksocket = new Nksocket("127.0.0.1",60000);
        nksocket.start();
    }
}