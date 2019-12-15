package com.vcarecity.utils;

import org.junit.Test;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SSDBClientTest {
    private Socket sock;

    @Test
    public void test01(){
        int i=0;
        while(i<100) {
            i++;
            Map<Integer, Integer> map = new HashMap<Integer, Integer>();
            map.put(33, 333);
            map.put(123, 1234);
            map.put(321, 4321);
            map.put(555, 5555);
            Integer[] keys = map.keySet().toArray(new Integer[0]);
            Random random = new Random();
            Integer randomKey = keys[random.nextInt(keys.length)];
            System.out.println(randomKey);
        }
    }


    @Test
    public void test02(){
        new Thread() {
            public void run() {
                try {
                    sock = new Socket("192.168.10.104", 60000);
                    while (true) {
                        //System.out.println("匿名内部类创建线程方式1...");

                            if(!sock.isConnected() || sock.isClosed()){
                                try {
                                    sock = new Socket("192.168.10.104", 60000);
                                } catch (Exception e) {
                                    System.out.println("连接异常");
                                }
                            }
                            Thread.sleep(100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }

    @Test
    public void Test03(){
        try {
            sock = new Socket("192.168.10.104", 60000);
            while (true) {
                //System.out.println("匿名内部类创建线程方式1...");
                if(!sock.isConnected() || sock.isClosed()){
                    try {
                        sock = new Socket("192.168.10.104", 60000);
                    } catch (Exception e) {
                        System.out.println("连接异常");
                    }
                }
                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
