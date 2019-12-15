package com.vcarecity.utils;

import org.apache.log4j.PropertyConfigurator;

public class Log4jUtil {
    public static void InitLog4jConfig() {
        try {
            PropertyConfigurator.configure("config/log4j.properties");//装入log4j配置信息
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}
