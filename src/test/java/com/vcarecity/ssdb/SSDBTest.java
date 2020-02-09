package com.vcarecity.ssdb;

import org.junit.Test;

public class SSDBTest {
    @Test
    public void Test01(){
        try {
            SSDB ssdb=new SSDB("10.239.204.34",15264,18000);
            ssdb.set("test01","Test01");
            byte[] value=ssdb.get("test01");
            System.out.println("value:"+new String(value,"UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Test02(){
        try {
            SSDB ssdb=new SSDB("10.239.204.34",15265,18000);
            ssdb.set("test02","Test02");
            byte[] value=ssdb.get("test02");
            System.out.println("value:"+new String(value,"UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Test03(){
        try {
            SSDB ssdb=new SSDB("10.239.204.34",15272,18000);
            ssdb.set("test03","Test03");
            byte[] value=ssdb.get("test03");
            System.out.println("value:"+new String(value,"UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
