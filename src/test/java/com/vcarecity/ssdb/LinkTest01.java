package com.vcarecity.ssdb;

public class LinkTest01 {
    public static void main(String[] args) throws Exception {
        try {
            Link link=new Link("103.239.204.34",15272);
            new Thread() {
                int count = 0;
                public void run() {
                    while (true) {
                        count++;
                        try {
                            Response resp = link.request("info", "");
                            if(resp.ok()){
                                System.out.println(resp.status);
                            }else{
                                System.out.println(resp.status);
                            }
                            Thread.sleep(100);
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
}
