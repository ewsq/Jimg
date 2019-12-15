package com.vcarecity.appender;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class TextLog {
    private static Logger logger = Logger.getLogger(TextLog.class);
    public static void main(String[] args) {
        PropertyConfigurator.configure ("config/log4j.properties");
        for(int i=0;i<=500000;i++){
            System.out.println("循环"+"--"+i);
            try{
                System.out.println(1%0);
            }catch(Exception e){
                logger.info("异常信息"+i+"："+e);
            }
        }
    }
}