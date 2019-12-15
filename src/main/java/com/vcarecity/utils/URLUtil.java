package com.vcarecity.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLUtil {
    private static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024]; //创建一个Buffer字符串
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while( (len=inStream.read(buffer)) != -1 ){
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        inStream.close();   //关闭输入流
        return outStream.toByteArray();  //把outStream里的数据写入内存
    }

    public static byte[] getFile(String url) {
        byte[] byteArray=null;
        HttpURLConnection connection = null;
        try {
            if(StringUtils.isNotEmpty(url)){
                URL geturl = new URL(url);
                connection = (HttpURLConnection) geturl.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5 * 1000);
                InputStream in = connection.getInputStream();
                try {
                    byteArray = readInputStream(in);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            connection.disconnect();
        }
        return byteArray;
    }
}
