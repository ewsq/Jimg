package com.vcarecity.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

public class Util {
	/**
	 * 加载属性文件
	 */
	public static Properties loadProperties(String fileName) throws IOException{
		Properties properties = new Properties();
		InputStream is = null;
		try {
			is = new FileInputStream(fileName);
			properties.load(is);
			return properties;
		}finally{
			IOUtil.closeIS(is);
		}
	}
	
	 /**
	  * 通过properties初始化been
	  */
	public static void initObjectByProperties(Object obj, Properties properties){
		Iterator<Entry<Object, Object>> it = properties.entrySet().iterator();
		String methodName;
		while(it.hasNext()){
			Entry<Object, Object> entry = it.next();
			Object key = entry.getKey();
			methodName = "set" + key;
			Method method = searchMethod(obj, methodName);
			if(method != null){
				Class<?> ptype = method.getParameterTypes()[0];				
				try {
					method.invoke(obj, transValueByType(entry.getValue(), ptype));
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("exception when init method:" + methodName);
				}
			}else{
				System.out.println("no such method:" + methodName);
			}
		}
	}
		
	private static Object transValueByType(Object value, Class<?> ptype){
		if(ptype == boolean.class || ptype == Boolean.class){
			return Boolean.valueOf(value.toString());
		}
		if(ptype == int.class || ptype == Integer.class){
			return Integer.valueOf(value.toString());
		}
		if(ptype == float.class || ptype == Float.class){
			return Float.valueOf(value.toString());
		}
		if(ptype == double.class || ptype == Double.class){
			return Double.valueOf(value.toString());
		}
		if(ptype == String.class){
			return String.valueOf(value.toString());
		}
		return value;
	}
		
	private static Method searchMethod(Object obj, String name){
		Method[] methods = obj.getClass().getMethods();
		for(Method method: methods){
			if(method.getName().equalsIgnoreCase(name)){
				return method;
			}
		}		
		return null;
	}
}