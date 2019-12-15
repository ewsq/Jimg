package com.vcarecity.utils;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtils {
	private static final String MQ_CONFIG_FILE_PATH = "config/mq.properties";
	private static final String WEB_CONFIG_FILE_PATH = "config/web.properties";
	private static final String DB_CONFIG_FILE_PATH = "config/db.properties";
	private static final String REDIS_CONFIG_FILE_PATH = "config/redis.properties";
	private static final String QUARTZ_CONFIG_FILE_PATH = "config/quartz.properties";
	private static final String MAIL_CONFIG_FILE_PATH = "config/mail.properties";
	private static final String ZIMG_CONFIG_FILE_PATH = "config/zimg.properties";
	private static final String NIMG_CONFIG_FILE_PATH = "config/nimg.properties";
	private static final String SSDB_CONFIG_FILE_PATH = "config/ssdb.properties";
	private static final String JETTY_CONFIG_FILE_PATH = "config/jetty.properties";

	private static Properties mq_properties;
	private static Properties web_properties;
	private static Properties db_properties;
	private static Properties redis_properties;
	private static Properties quartz_properties;
	private static Properties mail_properties;
	private static Properties zimg_properties;
	private static Properties nimg_properties;
	private static Properties ssdb_properties;
	private static Properties jetty_properties;
	
	static {
		try {
			db_properties = Util.loadProperties(DB_CONFIG_FILE_PATH);
			zimg_properties = Util.loadProperties(ZIMG_CONFIG_FILE_PATH);
			nimg_properties = Util.loadProperties(NIMG_CONFIG_FILE_PATH);
			ssdb_properties = Util.loadProperties(SSDB_CONFIG_FILE_PATH);
			jetty_properties = Util.loadProperties(JETTY_CONFIG_FILE_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static String getProperty(String config,String key) {
		return getString(config, key);
	}

	private static String getString(String config, String key) {
		Properties properties=new Properties();
		if("db".equalsIgnoreCase(config)){
			properties=db_properties;
		}else if("mq".equalsIgnoreCase(config)){
			properties=mq_properties;
		}else if("db".equalsIgnoreCase(config)){
			properties=db_properties;
		}else if("web".equalsIgnoreCase(config)){
			properties=web_properties;
		}else if("redis".equalsIgnoreCase(config)){
			properties=redis_properties;
		}else if("quartz".equalsIgnoreCase(config)){
			properties=quartz_properties;
		}else if("mail".equalsIgnoreCase(config)){
			properties=mail_properties;
		}else if("zimg".equalsIgnoreCase(config)){
			properties=zimg_properties;
		}else if("nimg".equalsIgnoreCase(config)){
			properties=db_properties;
		}else if("ssdb".equalsIgnoreCase(config)){
			properties=ssdb_properties;
		}else if("jetty".equalsIgnoreCase(config)){
			properties=ssdb_properties;
		}
		return properties.getProperty(key);
	}

	public static String getProperty(String config,String key,String defult) {
		String str=getString(config, key);
		if(str==null || "null".equalsIgnoreCase(str)){
			str=defult;
		}
		return str;
	}
	public static Properties getMqProperty() {
		return mq_properties;
	}

	public static String getMqProperty(String key) {
		return mq_properties.getProperty(key);
	}

	public static Properties getWebProperty() {
		return web_properties;
	}
	
	public static String getWebProperty(String key) {
		return web_properties.getProperty(key);
	}
	public static String getJettyProperty(String key) {
		return jetty_properties.getProperty(key);
	}
	public static Properties getDBProperties() {
		return db_properties;
	}
	
	public static String getDBProperties(String key) {
		return db_properties.getProperty(key);
	}

	public static Properties getRedisProperties() {
		return redis_properties;
	}

	public static String getRedisProperties(String key) {
		return redis_properties.getProperty(key);
	}
	
	public static Properties getQuartzProperties() {
		return quartz_properties;
	}
	
	public static String getQuartzProperties(String key) {
		return quartz_properties.getProperty(key);
	}
	
	public static Properties getMailProperties() {
		return mail_properties;
	}
	
	public static String getMailProperties(String key) {
		return mail_properties.getProperty(key);
	}

	public static String getZimgProperties(String key) {
		return zimg_properties.getProperty(key);
	}

	public static String getNimgProperties(String key) {
		return nimg_properties.getProperty(key);
	}
}