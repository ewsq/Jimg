package com.vcarecity.jimg;

import com.google.gson.Gson;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Properties;

public class JimgClient {
    private static JimgClient client;
    public static String JimgUrl = "http://192.168.10.80:48691/";
    public static String JimgShareUrl = "http://192.168.10.80:48691/";
    public static String tmpPath = "/var/logs/tmp";
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        JimgClient Jimg=JimgClient.getJimgClient();

        //JimgResult ret0 =Jimg.deleteImg("ea4a74467e81967ed9773467a81db88a");
        //System.out.println(ret0.isRet() + "\r\n" + ret0.getError().getMessage());

        for(int i=0;i<5;i++) {
            new Thread(new Runnable() {
                public void run() {
                    JimgResult ret0 = null;
                    System.out.println("匿名内部类创建线程方式2...");
                    while (true) {
                        try {
                            // 从文件上传图片
                            ret0 = Jimg.uploadImg("F:\\test\\01.jpg");
                            System.out.println("ret0:" + ret0);
                            System.out.println(ret0.isRet() + "\r\n" + ret0.getImageUrl());

/*                        // 从文件上传图片
                        ret0 = Jimg.uploadImg("H:\\照片\\test\\1 (12).JPG");
                        System.out.println(ret0.isRet() + "\r\n" + ret0.getImageUrl());

                        // 从URL上传图片
                        //JimgResult ret = Jimg.uploadImgFromUrl("https://camo.githubusercontent.com/d18c387d4c8617aadeba7397a5bfa8a194ba05f4/687474703a2f2f7777322e73696e61696d672e636e2f6c617267652f34633432326530336a7731656a6a646b34766463636a32306b66306d6f6d7a642e6a7067");
                        JimgResult ret = Jimg.uploadImgFromUrl("https://zkres1.myzaker.com/img_upload/cms/article_img/ckeditor/up_ckeditor_14839571963218.jpg");
                        System.out.println(ret.isRet() + "\r\n" + ret.getImageUrl());*/

                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        // Send("http://192.168.1.221:4869/upload",
        // "c:/4c422e03jw1ejoqm5ghm0j20nl0fb76x.jpg", "jpg");

    }

    public static JimgClient getJimgClient() {
        try {
            if (client == null) {
                client = new JimgClient();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return client;
    }


    protected JimgClient() {
        InitLog4jConfig();
        InputStream is = null;
        Properties properties = new Properties();
        try {
            is = new FileInputStream("config/JimgClient.properties");
            properties.load(is);
            JimgUrl = properties.getProperty("JimgUrl", "http://192.168.10.80:4869/");
            System.err.println("JimgUrl:"+JimgUrl);
            JimgShareUrl = properties.getProperty("JimgShareUrl", "http://192.168.10.80:4869/");
            System.err.println("JimgShareUrl:"+JimgShareUrl);
            tmpPath = properties.getProperty("tmpPath", "/var/logs/tmp");
            System.err.println("tmpPath:"+tmpPath);
        } catch (IOException ex) {
            System.err.println("不能读取配置文件.请确保Jimg.properties在CLASSPATH指定的路径中");
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

    private void InitLog4jConfig() {
        try {
            PropertyConfigurator.configure("config/log4j.properties");//装入log4j配置信息
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * 从页面提交图片，上传到Jimg
     *
     * @param request
     * @param fileTag
     * @return
     */
    public String uploadImgToJimg(HttpServletRequest request, String fileTag) {
        String imgUrl = "";
        MultipartHttpServletRequest mhs = (MultipartHttpServletRequest) request;
        List<MultipartFile> files = mhs.getFiles(fileTag);
        if (files != null && files.size() > 0) {
            // 上传到图片服务器
            MultipartFile f = files.get(0);
            if (f.getSize() == 0)
                return "";
            String tmpFileName = JimgClient.tmpPath + "/"
                    + f.getOriginalFilename();
            // mkdir("./tmp")
            File tmp = new File(JimgClient.tmpPath);
            tmp.mkdir();
            tmp = new File(tmpFileName);
            try {
                // tmp.delete();
                f.transferTo(tmp);
            } catch (Exception e) {
                e.printStackTrace();
            }

            JimgResult ret = this.uploadImg(tmpFileName);
            logger.debug(new Gson().toJson(ret));
            if (ret != null && ret.isRet())
                imgUrl = ret.getImageUrl();

            // 删除文件
            if (tmp != null) {
                tmp.setWritable(true);
                // try {
                // new FileOutputStream(tmp).close();
                // } catch (Exception e) {
                // e.printStackTrace();
                // }
                System.gc();// java'bug，must be gc before delete
                tmp.delete();
            }
        }

        return imgUrl;
    }

    /**
     * 指定文件名，上传到Jimg
     *
     * @param fileName
     * @return
     */
    public JimgResult uploadImg(String fileName) {
        String ext = "jpeg";
        int inx = fileName.lastIndexOf(".");
        if (inx > 0)
            ext = fileName.substring(inx + 1);
        String resp = this.Send(JimgClient.JimgUrl + "upload", fileName, ext);
        return new Gson().fromJson(resp, JimgResult.class);
    }
    /**
     * 指定文件名，上传到Jimg
     *
     * @param file
     * @param ext
     * @return
     */
    public JimgResult uploadImg(byte[] file,String ext) {
        String resp = this.Send(JimgClient.JimgUrl + "upload", file, ext);
        return new Gson().fromJson(resp, JimgResult.class);
    }
    /**
     * 指定文件名，上传到Jimg
     *
     * @param file
     * @param ext
     * @return
     */
    public String uploadImgBytes(byte[] file,String ext) {
        String resp = this.Send(JimgClient.JimgUrl + "upload", file, ext);
        return resp;
    }

    public JimgResult deleteImg(String fileName) {
        String resp = this.delete(JimgClient.JimgUrl + "admin?md5="+fileName+"&t=1");
        return new Gson().fromJson(resp, JimgResult.class);
    }

    public JimgResult uploadImgFromUrl(String url) {
        String resp = this.SendFromUrl(url);
        return new Gson().fromJson(resp, JimgResult.class);
    }

    /**
     * 从指定的URL下载图片并上传到Jimg服务器
     *
     * @param imgUrl
     * @return
     */
    protected String SendFromUrl(String imgUrl) {

        // 设置文件类型默认值
        String ext = "jpeg";
        String reBody = "";
        try {
            // 获得connection对象
            logger.debug("Jimg server url:" + JimgClient.JimgUrl);
            URL JimgUL = new URL(JimgClient.JimgUrl+"upload");
            URLConnection JimgConnection = JimgUL.openConnection();
            JimgConnection.setReadTimeout(50000);
            JimgConnection.setConnectTimeout(25000);
            HttpURLConnection JimgUC = (HttpURLConnection) JimgConnection;

            // 设置HTTP协议的消息头
            logger.debug("Jimg set header");
            JimgUC.setRequestMethod("POST");
            JimgUC.setRequestProperty("Connection", "Keep-Alive");
            JimgUC.setRequestProperty("Cache-Control", "no-cache");
            JimgUC.setRequestProperty("Content-Type", ext.toLowerCase());// "jpeg");//
            JimgUC.setRequestProperty("COOKIE", "william");
            JimgUC.setDoOutput(true);
            JimgUC.setDoInput(true);

            logger.debug("Jimg connect server.");
            // 与建立服务器连接
            JimgUC.connect();
            // 设置传输模式为二进制
            logger.debug("Jimg upload image in binary.");
            OutputStream om = JimgUC.getOutputStream();
            // 循环读取图片，发送到Jimg服务器

            ext = this.writeImage(imgUrl, om);
            logger.debug("image type=" + ext);
            // byte[] buf = new byte[8192];
            // while (true) {
            // int len = in.read(buf);
            // if (len <= 0)
            // break;
            // om.write(buf, 0, len);
            // }

            // 打开输入（返回信息）流
            InputStreamReader im = new InputStreamReader(JimgUC.getInputStream(), "UTF-8");
            // 循环读取，直到结束，获取返回信息
            logger.debug("Jimg get response text.");
            char[] bb = new char[8192];
            while (true) {
                int length = im.read(bb);
                if (length == -1) {
                    break;
                }
                char[] bc = new char[length];
                for (int i = 0; i < length; i++) {
                    bc[i] = bb[i];
                }
                reBody += new String(bc);
            }
            logger.debug("Jimg response:" + reBody);
            // 关闭上下行
            im.close();
            JimgUC.disconnect();
        } catch (Exception e) {
            logger.debug("Jimg exception :" + e.getMessage());
            e.printStackTrace();
        }

        return reBody;

    }

    /**
     * 返货图片类型
     *
     * @param data
     * @return
     */
    protected String getImageType(byte[] data) {
        String type = null;
        // Png test:
        if (data[1] == 'P' && data[2] == 'N' && data[3] == 'G') {
            type = "PNG";
            return type;
        }
        // Gif test:
        if (data[0] == 'G' && data[1] == 'I' && data[2] == 'F') {
            type = "GIF";
            return type;
        }
        // JPG test:
        if (data[6] == 'J' && data[7] == 'F' && data[8] == 'I'
                && data[9] == 'F') {
            type = "JPG";
            return type;
        }
        return type;
    }

    /**
     * 获取URL的输入流
     *
     * @param imgUrl
     * @return
     */
    private String writeImage(String imgUrl, OutputStream om) {
        long totalBytes = 0;
        String imgType = "jpeg";
        try {
            // 获得connection对象
            URL imgUL = new URL(imgUrl);
            URLConnection imgConnection = imgUL.openConnection();
            imgConnection.setReadTimeout(50000);
            imgConnection.setConnectTimeout(25000);
            HttpURLConnection imgUC = (HttpURLConnection) imgConnection;

            // 设置HTTP协议的消息头
            logger.debug("set header");
            imgUC.setRequestMethod("GET");
            imgUC.setRequestProperty("Connection", "Keep-Alive");
            imgUC.setRequestProperty("Cache-Control", "no-cache");
            //imgUC.setRequestProperty("Content-Type", ext.toLowerCase());//
            // "jpeg");//
            imgUC.setRequestProperty("COOKIE", "william");
            imgUC.setDoOutput(true);
            imgUC.setDoInput(true);
            InputStream in = imgUC.getInputStream();

            byte[] buf = new byte[8192];
            boolean GotType = false;
            while (true) {
                int len = in.read(buf);
                if (len <= 0)
                    break;
                if (!GotType) {
                    imgType = this.getImageType(buf);
                    GotType = true;
                }
                totalBytes += len;
                om.write(buf, 0, len);
            }
            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (totalBytes > 0)
            return imgType;
        else
            return "";
    }

    /**
     * 将图片文件上传到Jimg服务器
     *
     * @param url
     * @param fileName
     * @param ext
     * @return
     */
    protected String Send(String url, String fileName, String ext) {

        if (ext.toLowerCase().compareTo("jpg") == 0)
            ext = "jpeg";
        String reBody = "";
        try {
            // 获得connection对象
            logger.debug("Jimg server url:" + url);
            URL ul = new URL(url);
            URLConnection connection = ul.openConnection();
            connection.setReadTimeout(5000000);
            connection.setConnectTimeout(2500000);
            HttpURLConnection uc = (HttpURLConnection) connection;

            // 设置HTTP协议的消息头
            logger.debug("Jimg set header");
            uc.setRequestMethod("POST");
            uc.setRequestProperty("Connection", "Keep-Alive");
            uc.setRequestProperty("Cache-Control", "no-cache");
            uc.setRequestProperty("Content-Type", ext.toLowerCase());// "jpeg");//
            uc.setRequestProperty("COOKIE", "william");
            uc.setDoOutput(true);
            uc.setDoInput(true);

            logger.debug("Jimg connect server.");
            // 与建立服务器连接
            uc.connect();
            // 设置传输模式为二进制
            logger.debug("Jimg upload image in binary.");
            OutputStream om = uc.getOutputStream();
            // 循环读取图片，发送到Jimg服务器
            FileInputStream in = new FileInputStream(fileName);
            byte[] buf = new byte[8192];
            while (true) {
                int len = in.read(buf);
                if (len <= 0)
                    break;
                om.write(buf, 0, len);
            }

            // 打开输入（返回信息）流
            InputStreamReader im = new InputStreamReader(uc.getInputStream(),"UTF-8");
            // 循环读取，直到结束，获取返回信息
            logger.debug("Jimg get response text.");
            char[] bb = new char[8192];
            while (true) {
                int length = im.read(bb);
                if (length == -1)
                    break;
                char[] bc = new char[length];
                for (int i = 0; i < length; i++)
                    bc[i] = bb[i];
                reBody += new String(bc);
            }
            logger.debug("Jimg response:" + reBody);
            // 关闭上下行
            im.close();
            uc.disconnect();
        } catch (Exception e) {
            logger.debug("Jimg exception :" + e.getMessage());
            e.printStackTrace();
        }

        return reBody;
    }

    /**
     * 将图片文件上传到Jimg服务器
     *
     * @param url
     * @param file
     * @param ext
     * @return
     */
    protected String Send(String url, byte[] file, String ext) {

        if (ext.toLowerCase().compareTo("jpg") == 0)
            ext = "jpeg";
        String reBody = "";
        try {
            // 获得connection对象
            logger.debug("Jimg server url:" + url);
            URL ul = new URL(url);
            URLConnection connection = ul.openConnection();
            connection.setReadTimeout(5000000);
            connection.setConnectTimeout(2500000);
            HttpURLConnection uc = (HttpURLConnection) connection;

            // 设置HTTP协议的消息头
            logger.debug("Jimg set header");
            uc.setRequestMethod("POST");
            uc.setRequestProperty("Connection", "Keep-Alive");
            uc.setRequestProperty("Cache-Control", "no-cache");
            uc.setRequestProperty("Content-Type", ext.toLowerCase());// "jpeg");//
            uc.setRequestProperty("COOKIE", "william");
            uc.setDoOutput(true);
            uc.setDoInput(true);

            logger.debug("Jimg connect server.");
            // 与建立服务器连接
            uc.connect();
            // 设置传输模式为二进制
            logger.debug("Jimg upload image in binary.");
            OutputStream om = uc.getOutputStream();
            // 发送到Jimg服务器(可以考虑循环发送)
            om.write(file, 0, file.length);


            // 打开输入（返回信息）流
            InputStreamReader im = new InputStreamReader(uc.getInputStream(),"UTF-8");
            // 循环读取，直到结束，获取返回信息
            logger.debug("Jimg get response text.");
            char[] bb = new char[8192];
            while (true) {
                int length = im.read(bb);
                if (length == -1)
                    break;
                char[] bc = new char[length];
                for (int i = 0; i < length; i++)
                    bc[i] = bb[i];
                reBody += new String(bc);
            }
            logger.debug("Jimg response:" + reBody);
            // 关闭上下行
            im.close();
            uc.disconnect();
        } catch (Exception e) {
            logger.debug("Jimg exception :" + e.getMessage());
            e.printStackTrace();
        }

        return reBody;
    }

    private String delete(String imgUrl) {
        String reBody = "";
        try {
            logger.debug("Jimg server url:" + imgUrl);
            URL ul = new URL(imgUrl);
            HttpURLConnection uc =  (HttpURLConnection) ul.openConnection();
            uc.setReadTimeout(50000);
            uc.setConnectTimeout(25000);

            // 设置HTTP协议的消息头
            logger.debug("Jimg set header");
            uc.setRequestMethod("GET");
            uc.setRequestProperty("Connection", "Keep-Alive");
            uc.setRequestProperty("Cache-Control", "no-cache");
            uc.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            uc.setRequestProperty("COOKIE", "william");
            uc.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            uc.setDoOutput(true);
            uc.setDoInput(true);
            // 打开输入（返回信息）流
            InputStreamReader im = new InputStreamReader(uc.getInputStream(),"UTF-8");
            // 循环读取，直到结束，获取返回信息
            logger.debug("Jimg get response text.");
            char[] bb = new char[8192];
            while (true) {
                int length = im.read(bb);
                if (length == -1)
                    break;
                char[] bc = new char[length];
                for (int i = 0; i < length; i++)
                    bc[i] = bb[i];
                reBody += new String(bc);
            }
            logger.debug("Jimg response:" + reBody);
            // 关闭上下行
            im.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reBody;
    }

    /********** Jimg 服务器返回消息定义 ***********************************/
    public class JimgError {
        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

    public class JimgInfo {
        private String md5;

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        private int size;

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }

    public class JimgResult {
        private boolean ret;
        private JimgInfo info;
        private JimgError error;

        public JimgError getError() {
            return error;
        }

        public void setError(JimgError error) {
            this.error = error;
        }

        public String getImageUrl() {
            if (this.isRet()) {
                return JimgClient.JimgShareUrl + this.info.getMd5();
            }
            return "";
        }

        public boolean isRet() {
            return ret;
        }

        public void setRet(boolean ret) {
            this.ret = ret;
        }

        public JimgInfo getInfo() {
            return info;
        }

        public void setInfo(JimgInfo info) {
            this.info = info;
        }

    }
}
