package com.vcarecity.utils;

import java.io.*;
import java.util.Properties;
import java.text.SimpleDateFormat;

public class Logger {
    private static Logger logger;
    private static String logPath;
    private static String eventPath;
    private static String website;
    private FileWriter fwlogger =null;
    private StackTraceElement elements[];
    private static String ClassMethodName="";
    private static String level="info";
    protected Logger() {
        level="debug";
    	InputStream is = null;
        Properties properties = new Properties();
        String filePath = "config/logger.properties";
        try {
        	is = new FileInputStream(filePath);
        	properties.load(is);
	        logPath = properties.getProperty("logPath", "./logs/");
	        website = properties.getProperty("website", "");
        } catch (IOException ex) {
            System.err.println("不能读取属性文件.请确保logger.properties在resources指定的路径中");
        }finally{
        	try {
        		if(is!=null){
        			is.close();
        		}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }

    public static Logger getLogger() {
        try {
            if (logger == null) {
                logger = new Logger();
            }
        } catch (Exception e) {
            System.out.println("创建日志文件失败:" + e.toString());
            e.printStackTrace();
        }
        return logger;
    }

    //获得日志文件目录的上一级目录
    public String getLogParentPath() {
        int ss = logPath.length();
        String strPath = logPath.substring(0, ss - 1);
        ss = strPath.lastIndexOf('\\');
        String retPath = strPath.substring(0, ss + 1);
        return retPath;
    }

    public void setLogPath(String path) {
        if (path == null) {
            logPath = "";
        } else {
            logPath = path;
        }
    }

    public synchronized void writeLog(String msg) {
        StringBuffer fileName = new StringBuffer();

        if (logPath != null) {
            fileName.append(logPath);
        } else {
            System.out.println("logpath is null!");
        }
        elements = new Throwable().getStackTrace();
        ClassMethodName=elements[1].getClassName() + "." + elements[1].getMethodName();
        if("".equalsIgnoreCase(website)){
            fileName.append("Log_");
        }else{
            fileName.append("Log_" + website + "_" +ClassMethodName+"_");
        }

        java.sql.Timestamp nowDate = new java.sql.Timestamp(System.currentTimeMillis());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        String aa = sdf.format(nowDate);
        fileName.append(aa);
        fileName.append(".txt");
        try {
            fwlogger = new FileWriter(fileName.toString(), true);
            StringBuffer sbMsg = new StringBuffer();
            sbMsg.append(getLongTimeString() + ":\r\n");
            sbMsg.append(msg);
            sbMsg.append("\r\n");
            fwlogger.write(sbMsg.toString());
            writeConsle(sbMsg.toString());
        } catch (Exception e) {
            System.out.println("writer log error:" + e.toString());
        }finally{
        	try {
        		if(fwlogger!=null){
                    fwlogger.close();
        		}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }


    public synchronized void writeErrorLog(String msg) {
        StringBuffer fileName = new StringBuffer();

        if (logPath != null) {
            fileName.append(logPath);
        } else {
            System.out.println("logpath is null!");
        }

        elements = new Throwable().getStackTrace();
        ClassMethodName=elements[1].getClassName() + "." + elements[1].getMethodName();
        if("".equalsIgnoreCase(website)){
            fileName.append("ErrorLog_");
        }else{
            fileName.append("ErrorLog_" + website + "_" +ClassMethodName+"_");
        }

        java.sql.Timestamp nowDate = new java.sql.Timestamp(System.currentTimeMillis());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        
        
        String aa = sdf.format(nowDate);
        fileName.append(aa);
        fileName.append(".txt");
        try {
            fwlogger = new FileWriter(fileName.toString(), true);
            StringBuffer sbMsg = new StringBuffer();
            sbMsg.append(getLongTimeString() + ":\r\n");
            sbMsg.append("Error:"+msg);
            sbMsg.append("\r\n");
            fwlogger.write(sbMsg.toString());
            writeConsle(sbMsg.toString());
        } catch (Exception e) {
            System.out.println("writer log error:" + e.toString());
        }finally{
        	try {
        		if(fwlogger!=null){
                    fwlogger.close();
        		}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }

    public synchronized void writeDebugLog(String msg) {
        StringBuffer fileName = new StringBuffer();

        if (logPath != null) {
            fileName.append(logPath);
        } else {
            System.out.println("logpath is null!");
        }

        elements = new Throwable().getStackTrace();
        ClassMethodName=elements[1].getClassName() + "." + elements[1].getMethodName();
        if("".equalsIgnoreCase(website)){
            fileName.append("DebugLog_");
        }else{
            fileName.append("DebugLog_" + website + "_" +ClassMethodName+"_");
        }

        java.sql.Timestamp nowDate = new java.sql.Timestamp(System.currentTimeMillis());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        
        
        String aa = sdf.format(nowDate);
        fileName.append(aa);
        fileName.append(".txt");
        try {
            fwlogger = new FileWriter(fileName.toString(), true);
            StringBuffer sbMsg = new StringBuffer();
            sbMsg.append(getLongTimeString() + ":\r\n");
            sbMsg.append("Debug:"+msg);
            sbMsg.append("\r\n");
            fwlogger.write(sbMsg.toString());
            writeConsle(sbMsg.toString());
        } catch (Exception e) {
            System.out.println("writer log error:" + e.toString());
        }finally{
        	try {
        		if(fwlogger!=null){
                    fwlogger.close();
        		}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    public synchronized void writeDebugInfo(String msg) {
        StringBuffer fileName = new StringBuffer();

        if (logPath != null) {
            fileName.append(logPath);
        } else {
            System.out.println("logpath is null!");
        }

        elements = new Throwable().getStackTrace();
        ClassMethodName=elements[1].getClassName() + "." + elements[1].getMethodName();
        if("".equalsIgnoreCase(website)){
            fileName.append("DebugInfo_");
        }else{
            fileName.append("DebugInfo_" + website + "_" +ClassMethodName+"_");
        }

        java.sql.Timestamp nowDate = new java.sql.Timestamp(System.currentTimeMillis());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        
        
        String aa = sdf.format(nowDate);
        fileName.append(aa);
        fileName.append(".txt");
        try {
            fwlogger = new FileWriter(fileName.toString(), true);
            StringBuffer sbMsg = new StringBuffer();
            sbMsg.append(getLongTimeString() + ":\r\n");
            sbMsg.append("Debug:"+msg);
            sbMsg.append("\r\n");
            fwlogger.write(sbMsg.toString());
            writeConsle(sbMsg.toString());
        } catch (Exception e) {
            System.out.println("writer log error:" + e.toString());
        }finally{
        	try {
        		if(fwlogger!=null){
                    fwlogger.close();
        		}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }


    public synchronized void writeConsle(String msg) {
        if (level.equalsIgnoreCase("debug")) {
            System.out.println(msg);
        } else {

        }
    }
    //////////////////系统日志
    public String getEventParentPath() {
        int ss = eventPath.length();
        String strPath = eventPath.substring(0, ss - 1);
        ss = strPath.lastIndexOf('\\');
        String retPath = strPath.substring(0, ss + 1);
        return retPath;
    }

    public void setEventPath(String path) {
        if (path == null) {
            eventPath = "";
        } else {
            eventPath = path;
        }
    }

    public synchronized void writeEvent(String msg) {
        StringBuffer fileName = new StringBuffer();
        if (eventPath != null) {
            fileName.append(eventPath);
        } else {
            System.out.println("Eventpath is null ");
        }

        elements = new Throwable().getStackTrace();
        ClassMethodName=elements[1].getClassName() + "." + elements[1].getMethodName();
        if("".equalsIgnoreCase(website)){
            fileName.append("System_Event_");
        }else{
            fileName.append("System_Event_" + website + "_" +ClassMethodName+"_");
        }
        java.sql.Timestamp nowDate = new java.sql.Timestamp(System.currentTimeMillis());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        String aa = sdf.format(nowDate);
        fileName.append(aa);
        fileName.append(".txt");
        try {
        	fwlogger = new FileWriter(fileName.toString(), true);
            StringBuffer sbMsg = new StringBuffer();
            sbMsg.append(getLongTimeString() + ":\r\n");
            sbMsg.append(msg);
            sbMsg.append("\r\n");
            fwlogger.write(sbMsg.toString());
            writeConsle(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
        	try {
        		if(fwlogger!=null){
                    fwlogger.close();
        		}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    /**
     * writeDebugEvent
     * 写专有日志标记，主要用于数据刷新较快的情况下处理专门的日志便于查看
     * @param eventFlag 日志事件标签，给一个易于标识和查询的标识，关键易于查找
     * @param msg 要写入的消息内容
     */
    public synchronized void writeDebugEvent(String eventFlag,String msg) {
        StringBuffer fileName = new StringBuffer();
        if (eventPath != null) {
            fileName.append(eventPath);
        } else {
            System.out.println("Eventpath is null ");
        }

        elements = new Throwable().getStackTrace();
        ClassMethodName=elements[1].getClassName() + "." + elements[1].getMethodName();
        if("".equalsIgnoreCase(website)){
            fileName.append(eventFlag+"_");
        }else{
            fileName.append(eventFlag+"_" + website + "_" +ClassMethodName+"_");
        }
        java.sql.Timestamp nowDate = new java.sql.Timestamp(System.currentTimeMillis());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        String aa = sdf.format(nowDate);
        fileName.append(aa);
        fileName.append(".txt");
        try {
        	fwlogger = new FileWriter(fileName.toString(), true);
            StringBuffer sbMsg = new StringBuffer();
            sbMsg.append(getLongTimeString() + ":\r\n");
            sbMsg.append(msg);
            sbMsg.append("\r\n");
            fwlogger.write(sbMsg.toString());
            writeConsle(sbMsg.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
        	try {
        		if(fwlogger!=null){
                    fwlogger.close();
        		}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    public synchronized void writeExceptionInfo(Exception ex) {
        StringBuffer fileName = new StringBuffer();
        StringBuffer sb = new StringBuffer();
        
        if (logPath != null) {
            fileName.append(logPath);
        } else {
            System.out.println("logpath is null!");
        }

        elements = ex.getStackTrace();
        ClassMethodName=elements[1].getClassName() + "." + elements[1].getMethodName();
        
        for(int i = 0; i < elements.length; i++) {  
            StackTraceElement element = elements[i];  
            sb.append(element.toString() + "\n");  
        }  
        
        if("".equalsIgnoreCase(website)){
            fileName.append("DebugInfo_");
        }else{
            fileName.append("DebugInfo_" + website + "_" +ClassMethodName+"_");
        }

        java.sql.Timestamp nowDate = new java.sql.Timestamp(System.currentTimeMillis());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        
        
        String aa = sdf.format(nowDate);
        fileName.append(aa);
        fileName.append(".txt");
        try {
            fwlogger = new FileWriter(fileName.toString(), true);
            StringBuffer sbMsg = new StringBuffer();
            sbMsg.append(getLongTimeString() + ":\r\n");
            sbMsg.append("Exception:"+sb.toString());
            sbMsg.append("\r\n");
            fwlogger.write(sbMsg.toString());
            writeConsle(sbMsg.toString());
        } catch (Exception e) {
            System.out.println("writer Exception error:" + e.toString());
        }finally{
        	try {
        		if(fwlogger!=null){
                    fwlogger.close();
        		}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    
    public synchronized void writeThrowableInfo(Throwable ex) {
        StringBuffer fileName = new StringBuffer();
        StringBuffer sb = new StringBuffer();
        
        if (logPath != null) {
            fileName.append(logPath);
        } else {
            System.out.println("logpath is null!");
        }

        elements = ex.getStackTrace();
        ClassMethodName=elements[1].getClassName() + "." + elements[1].getMethodName();
        
        for(int i = 0; i < elements.length; i++) {  
            StackTraceElement element = elements[i];  
            sb.append(element.toString() + "\n");  
        }  
        
        if("".equalsIgnoreCase(website)){
            fileName.append("DebugInfo_");
        }else{
            fileName.append("DebugInfo_" + website + "_" +ClassMethodName+"_");
        }

        java.sql.Timestamp nowDate = new java.sql.Timestamp(System.currentTimeMillis());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        
        
        String aa = sdf.format(nowDate);
        fileName.append(aa);
        fileName.append(".txt");
        try {
            fwlogger = new FileWriter(fileName.toString(), true);
            StringBuffer sbMsg = new StringBuffer();
            sbMsg.append(getLongTimeString() + ":\r\n");
            sbMsg.append("Exception:"+sb.toString());
            sbMsg.append("\r\n");
            fwlogger.write(sbMsg.toString());
            writeConsle(sbMsg.toString());
        } catch (Exception e) {
            System.out.println("writer Exception error:" + e.toString());
        }finally{
        	try {
        		if(fwlogger!=null){
                    fwlogger.close();
        		}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    public static String getEncodeString(String s) {
        String retStr = "";
        try {
            if (s != null) {
                retStr = new String(s.getBytes("ISO-8859-1"), "GBK");
            }
        } catch (Exception ex) {
            System.out.println(s + " getEncodeString error:" + ex.toString());
        }
        return retStr;
    }

    //生成长时间字符串，形如：2005-01-01 01:01:01
    public static String getLongTimeString() {
        java.util.Date myDate = new java.util.Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //long myTime = (myDate.getTime() / 1000) - 60 * 60 * 24 * 365;
        //myDate.setTime(myTime * 1000);
        String mDate = formatter.format(myDate);
        return mDate;
    }
    
    public static void main(String[] args) {
    	Logger log=Logger.getLogger();
    	log.writeLog("111111111111111");
    }
}
