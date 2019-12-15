package com.vcarecity.appender;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;

/*
最近做的项目，遇到了的情境：

1、要每个日志按固定大小生成，超过设定大大小就生成新的日志文件
2、同时在文件名字后面加上日期，并自动按照设置的保留天数保留日志，过期的日志自动删除。
3、设置同一日期最多生成日志数，超过这个数量，则删除今天最老的日志，滚动排序，新生成的日志永远编号最大。
然而，log4j自带的生成日志的几个方法，可以按照日期时间生成日志，也可以按照设置的大小滚动生成日志，就是没有即按照大小生成，又在日志名字后面加上日期，同时又清除过期日志的方法。看了源码，决定综合重新写一个类，实现这些需求。
第一，准备jar  :log4j-1.2.17.jar,commons-logging-1.1.1.jar，这2个就可以了，其他关于日志的jar包就不要加进来了，在优先级上会有冲突。
第二，定义一个类，继承RollingFileAppender类，这个类是按照日志大小滚动生成日志，并把日志编号。我就在这个类基础上重新写了其中的一些方法，加上日期和删除功能，稍加改动就行了。
 */

public class RoolingAndDateFileAppender extends RollingFileAppender{
    private String datePattern;
    private String dateStr="";//文件后面的日期
    private String expirDays="1";//保留最近几天
    private String isCleanLog="true";
    private String maxIndex="100";
    private File rootDir;
    public void setDatePattern(String datePattern){
        if(null!=datePattern&&!"".equals(datePattern)){
            this.datePattern=datePattern;
        }
    }
    public String getDatePattern(){
        return this.datePattern;
    }
    public void rollOver(){
        dateStr=new SimpleDateFormat(this.datePattern).format(new Date(System.currentTimeMillis()));
        File target = null;
        File file=null;
        if(qw!=null){
            long size=((CountingQuietWriter)this.qw).getCount();
            LogLog.debug("rolling over count="+size);
        }
        LogLog.debug("maxBackupIndex="+this.maxBackupIndex);
        //如果maxIndex<=0则不需命名
        if(maxIndex!=null&&Integer.parseInt(maxIndex)>0){
            //删除旧文件
            file=new File(this.fileName+'.'+dateStr+'.'+Integer.parseInt(this.maxIndex));
            if(file.exists()){
                //如果当天日志达到最大设置数量，则删除当天第一个日志，其他日志为尾号减一
                Boolean boo = reLogNum();
                if(!boo){
                    LogLog.debug("日志滚动重命名失败！");
                }
            }
        }
        //获取当天日期文件个数
        int count=cleanLog();
        //生成新文件
        target=new File(fileName+"."+dateStr+"."+(count+1));
        this.closeFile();
        file=new File(fileName);
        LogLog.debug("Renaming file"+file+"to"+target);
        file.renameTo(target);
        try{
            setFile(this.fileName,false,this.bufferedIO,this.bufferSize);
        }catch(IOException e){
            LogLog.error("setFile("+this.fileName+",false)call failed.",e);
        }
    }
    public int cleanLog(){
        int count=0;//记录当天文件个数
        if(Boolean.parseBoolean(isCleanLog)){
            File f=new File(fileName);
            rootDir=f.getParentFile();
            File[] listFiles = rootDir.listFiles();
            for(File file:listFiles){
                if(file.getName().contains(dateStr)){
                    count=count+1;//是当天日志，则+1
                }else{
                    if(Boolean.parseBoolean(isCleanLog)){
                        //清除过期日志
                        String[] split=file.getName().split("\\\\")[0].split("\\.");
                        //校验日志名字，并取出日期，判断过期时间
                        if(split.length==4 && isExpTime(split[2])){
                            file.delete();
                        }
                    }
                }
            }
        }
        return count;
    }
    public Boolean isExpTime(String time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Date logTime=format.parse(time);
            Date nowTime=format.parse(format.format(new Date()));
            //算出日志与当前日期相差几天
            int days=(int)(nowTime.getTime()-logTime.getTime())/(1000*3600*24);
            if(Math.abs(days)>=Integer.parseInt(expirDays)){
                return true;
            }else{
                return false;
            }
        }catch(Exception e){
            LogLog.error(e.toString());
            return false;
        }
    }
    /**
     * 如果当天日志达到最大设置数量，则每次删除尾号为1的日志，
     * 其他日志编号依次减去1，重命名
     * @return
     */
    public Boolean reLogNum(){
        boolean renameTo=false;
        File startFile = new File(this.fileName+'.'+dateStr+'.'+"1");
        if(startFile.exists()&&startFile.delete()){
            for(int i=2;i<=Integer.parseInt(maxIndex);i++){
                File target = new File(this.fileName+'.'+dateStr+'.'+(i-1));
                this.closeFile();
                File file = new File(this.fileName+'.'+dateStr+'.'+i);
                renameTo=file.renameTo(target);
            }
        }
        return renameTo;
    }
    public String getDateStr() {
        return dateStr;
    }
    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
    public String getExpirDays() {
        return expirDays;
    }
    public void setExpirDays(String expirDays) {
        this.expirDays = expirDays;
    }
    public String getIsCleanLog() {
        return isCleanLog;
    }
    public void setIsCleanLog(String isCleanLog) {
        this.isCleanLog = isCleanLog;
    }
    public String getMaxIndex() {
        return maxIndex;
    }
    public void setMaxIndex(String maxIndex) {
        this.maxIndex = maxIndex;
    }

}
