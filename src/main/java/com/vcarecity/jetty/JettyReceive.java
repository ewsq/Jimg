package com.vcarecity.jetty;

import com.vcarecity.mode.FileMeta;
import com.vcarecity.ssdb.SSDBClient;
import com.vcarecity.ssdb.SSDBNoPoolClient;
import com.vcarecity.utils.*;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.*;

public class JettyReceive extends AbstractHandler {
    Logger log = Logger.getLogger();
    SSDBClient ssdb=new SSDBClient();
    //SSDBNoPoolClient ssdb=new SSDBNoPoolClient(); //应急用的，由于SSDBClient还没有解决多线程并发，所以暂时使用这种方式
    LinkedList<FileMeta> files = new LinkedList<FileMeta>();
    public int socketTimeout=2500000;
    public int connectionTimeout=5000000;
    public String tmpPath = "/var/logs/tmp";
    private MultipartConfigElement MULTI_PART_CONFIG = null;
    public String JimgUrl = "http://192.168.10.80:48691/";
    public String JimgShareUrl = "http://192.168.10.80:48691/";

    public JettyReceive(){
        try {
            JimgUrl = YamlUtils.getValue("alpha.JimgUrl");
            System.err.println("JimgUrl:"+JimgUrl);
            JimgShareUrl = YamlUtils.getValue("alpha.JimgShareUrl");
            System.err.println("JimgShareUrl:"+JimgShareUrl);
            tmpPath = YamlUtils.getValue("alpha.tmpPath");
            System.err.println("tmpPath:"+tmpPath);
            MULTI_PART_CONFIG = new MultipartConfigElement(tmpPath);
        } catch (Exception ex) {

        }finally{
        }
    }

    @Override
    public void handle(String url, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String ip=getIpAdrress(request);
        log.writeLog("ip:"+ip);
        log.writeLog("url:"+url);
        String contentType = request.getHeader("Content-Type");
        System.out.println("contentType:"+contentType);
        //System.out.println(getBodyData(request));
        //JSONObject body=getRequestData(request);
        //log.writeLog("JettyReceive body:"+body);
        url=url.replace("favicon.ico","");
        ServletOutputStream outputStream =null;
        String key ="8f706633755b57af6b4ca0eb5d9581b4";
        try {
            //通过URL获取图片
            if(url!=null && url.length()>2 && url.indexOf("favicon.ico")<1 && url.indexOf("upload")<0 ){
                key=url.substring(1);
                System.out.println("key:"+key);
                byte[] byteArray= ssdb.get(key);
                if(byteArray!=null) {
                    response.setStatus(200);
                    response.setContentType("image/jpeg");
                    response.setContentLength(byteArray.length);
                    //response.getWriter().println(new String(respBytes, "UTF-8"));
                    //response.getWriter().print(respBytes);
                    outputStream = response.getOutputStream();
                    outputStream.write(byteArray);
                    outputStream.flush();
                }
            //通过表单上传图片
            }else if(url!=null && url.length()>1 && url.indexOf("upload")>0 && contentType!=null && contentType.indexOf("form-data")<0){
                //System.out.println("aaaaaaaaaaaaaaaaaa");

                byte[] fileBody=HexUtil.inputStream2Byte(request.getInputStream());
                //System.out.println("bt:"+ HexUtil.HexToStr(fileBody,0,fileBody.length));
                String md5=ssdb.set(fileBody);
                System.out.println("md5:"+md5);
                String imgUrl=JimgUrl+md5;

                JSONObject reBody=new JSONObject();
                reBody.put("ret",true);
                JSONObject info=new JSONObject();
                info.put("md5",md5);
                info.put("size",fileBody.length);
                info.put("JimgUrl",JimgUrl+md5);
                info.put("JimgShareUrl",JimgShareUrl+md5);
                reBody.put("info",info);

                System.out.println("reBody:"+reBody);
                byte[] reByte=reBody.toString().getBytes("UTF-8");
                response.setStatus(200);
                response.setContentLength(reByte.length);
                outputStream = response.getOutputStream();
                outputStream.write(reByte);
                outputStream.flush();

                //System.out.println("bbbbbbbbbbbbbbbbbbb");
            }else if(url!=null && url.length()>1 && url.indexOf("upload")>0 && contentType!=null && contentType.indexOf("form-data")>0){
                //上传文件
                request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);

                for(Part part: request.getParts()) {
                    String filename=part.getSubmittedFileName();
                    System.out.println("filename:"+filename);
                }

                Part part = request.getPart("userfile");
                String disposition = part.getHeader("Content-Disposition");
                System.out.println("disposition:"+disposition);
                String suffix = disposition.substring(disposition.lastIndexOf(".")+1,disposition.length()-1);
                //随机的生存一个32的字符串
                //String filename = UUID.randomUUID()+suffix;
                if (suffix.toLowerCase().compareTo("jpg") == 0)
                    suffix = "jpeg";
                byte[] fileBody=HexUtil.inputStream2Byte(part.getInputStream());
                //System.out.println("bt:"+ HexUtil.HexToStr(fileBody,0,fileBody.length));

                String imgUrl=JimgUrl+ssdb.set(fileBody);
                //System.out.println("reBody:"+reBody);

                StringBuffer sb=new StringBuffer();
                response.setStatus(200);
                response.setContentType("text/html;charset=utf-8");
                outputStream = response.getOutputStream();

                sb.append("<html>");
                sb.append("<head>");
                sb.append("<title>图片</title>");
                sb.append("</head>");
                sb.append("<body>");
                sb.append("<h1><a href=\""+imgUrl+"\" target=\"_blank\">"+imgUrl+"</a></h1>");
                sb.append("</body>");
                sb.append("</html>");

                outputStream.write(sb.toString().getBytes("UTF-8"));

                outputStream.flush();

                //System.out.println("dddddddddddddddddddddd");
            }else if("/".equalsIgnoreCase(url)){
                //默认显示上传界面，用于上传测试
                StringBuffer sb=new StringBuffer();
                sb.append(" <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />                           ");
                sb.append(" <html>                                                                                          ");
                sb.append("     <head>                                                                                      ");
                sb.append("         <script>                                                                                ");
                sb.append("             function changetext()                                                               ");
                sb.append("             {                                                                                   ");
                sb.append("                 var newItemText=document.createTextNode(\"Choose file:\")                         ");
                sb.append("                 var newItemInput=document.createElement(\"input\")                                ");
                sb.append("                 newItemInput.name=\"userfile\"                                                    ");
                sb.append("                 newItemInput.type=\"file\"                                                        ");
                sb.append("                 var addfile=document.getElementById(\"bt_addfile\")                               ");
                sb.append("                 var submit=document.getElementById(\"bt_submit\")                                 ");
                sb.append("                 var newItemBr=document.createElement(\"br\")                                      ");
                sb.append("                                                                                                 ");
                sb.append("                 var myform=document.getElementById(\"upform\")                                    ");
                sb.append("                 myform.appendChild(newItemText);                                                ");
                sb.append("                 myform.appendChild(newItemInput);                                               ");
                sb.append("                 myform.appendChild(addfile);                                                    ");
                sb.append("                 myform.appendChild(newItemBr);                                                  ");
                sb.append("                 myform.appendChild(submit);                                                     ");
                sb.append("             }                                                                                   ");
                sb.append("         </script>                                                                               ");
                sb.append("     </head>                                                                                     ");
                sb.append("     <h1>Welcome to jimg world!</h1>                                                             ");
                sb.append("     <p>Upload image(s) to jimg:</p>                                                             ");
                sb.append("     <form enctype=\"multipart/form-data\" action=\"upload\" method=post target=_blank id=\"upform\">  ");
                sb.append("         Choose file:<input name=\"userfile\" type=\"file\">                                         ");
                sb.append("         <input type=\"button\" value=\"+\" onclick=\"changetext()\" id=\"bt_addfile\">                  ");
                sb.append("         </br>                                                                                   ");
                sb.append("         <input type=\"submit\" value=\"upload\" id=\"bt_submit\">                                     ");
                sb.append("     </form>                                                                                     ");
                sb.append("     <p>More infomation: <a href=\"http://jimg.buaa.us\">jimg.buaa.us</a></p>                      ");
                sb.append(" </html>                                                                                         ");

                response.setStatus(200);
                response.setContentType("text/html;charset=utf-8");
                outputStream = response.getOutputStream();
                outputStream.write(sb.toString().getBytes("UTF-8"));
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(outputStream!=null){
                outputStream.close();
            }
        }
        //response.setContentType("text/html;charset=utf-8");
        baseRequest.setHandled(true);
    }
    /**
     * 将字节数组转换为输入流
     * @param buf
     * @return
     */
    public static final InputStream byte2InputStream(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

    /**
     * 从输入流中取出字节
     * @param inStream
     * @return
     * @throws IOException
     */
    public static final byte[] inputStream2Byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    /**
     * 从输入流中取出字节
     * @param request
     * @return
     * @throws IOException
     */
    public static final byte[] inputStream2Byte(HttpServletRequest request)
            throws IOException {
        //获取请求文本字节长度
        int formDataLength = request.getContentLength();
        //取得ServletInputStream输入流对象
        DataInputStream dataStream = new DataInputStream(request.getInputStream());
        byte[] filebody = new byte[formDataLength];
        while (true) {
            int length = dataStream.read(filebody);
            if (length == -1)
                break;
        }
        dataStream.close();
        System.out.println("bt:"+ HexUtil.HexToStr(filebody,0,filebody.length));
        return filebody;
    }

    public static String writeFileName(HttpServletRequest request,String filePath,String fileName) throws IOException {
        //File file = new File("F:\\test\\", key + ".jpg");// 可以是任何图片格式.jpg,.png等
        File file = new File(filePath, fileName);// 可以是任何图片格式.jpg,.png等
        FileOutputStream fos = new FileOutputStream(file);
        byte[] b = new byte[1024];
        int nRead = 0;
        InputStream in=request.getInputStream();
        while ((nRead = in.read(b)) != -1) {
            fos.write(b, 0, nRead);
        }
        fos.flush();
        fos.close();
        in.close();
        return filePath+fileName;
    }

    public static String getFileName(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        int count = 0;
        while (true) {
            int a = is.read();
            sb.append((char) a);
            if (a == '\r') {
                count++;
            }
            if (count == 4) {
                is.read();
                break;
            }
        }
        String title = sb.toString();
        System.out.println(title);
        String[] titles = title.split("\r\n");
        String fileName = titles[1].split(";")[2].split("=")[1].replace("\"", "");
        return fileName;
    }


    public JSONObject getRequestData(HttpServletRequest request)
    {
        InputStream is = null;
        String contentStr = "";
        JSONObject json = null;

/*        Enumeration en = request.getParameterNames();
        while (en.hasMoreElements()) {
            String k = (String) en.nextElement();
            String v = request.getParameter(k);
            System.out.println("k="+k+"    v="+v);
        }*/

        Map<?, ?> properties = request.getParameterMap();
        Map returnMap = new HashMap();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String name = "";
        String value = "";
        while (entries.hasNext())
        {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if (null == valueObj)
            {
                value = "";
            }
            else if (valueObj instanceof String[])
            {
                String[] values = (String[]) valueObj;
                for (int i = 0; i < values.length; i++)
                {
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            }
            else
            {
                value = valueObj.toString();
            }
            returnMap.put(name, value);
            if (json == null)
            {
                json = new JSONObject();
                json.put(name, value);
            }
            else
            {
                json.put(name, value);
            }
        }


        try
        {
            is = request.getInputStream();
            contentStr = IOUtils.toString(is, "utf-8");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if (contentStr != null && contentStr.length() > 0)
        {
            json = JSONObject.fromObject(contentStr);
        }

        return json;
    }

    // 从请求中获取数据
    public String getBodyData(HttpServletRequest request) {
        String bodyStr = null;
        BufferedReader br = null;

        try {
            // 设置编码
            request.setCharacterEncoding("UTF-8");
            // 创建输入流
            br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            // 获取body内容
            String line = "";
            StringBuffer buf = new StringBuffer();
            while ((line = br.readLine()) != null) {
                buf.append(line);
            }
            bodyStr = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bodyStr;
    }

    /**
     * 获取request的客户端IP地址
     *
     * @param request
     * @return
     */
    private static String getIpAdrress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public void destroy() {
        ssdb.release();
    }
}