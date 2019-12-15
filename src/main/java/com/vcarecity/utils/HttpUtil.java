package com.vcarecity.utils;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    public static int socketTimeout=50000;
    public static int connectionRequestTimeout=50000;
    public static String post(String url,Map<String,String> params) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        if (params != null)
        {
            for (Map.Entry<String, String> e : params.entrySet())
            {
                String value = e.getValue();
                if (value != null && !"".equals(value) && !"null".equals(value))
                {
                    paramList.add(new BasicNameValuePair(e.getKey(), e.getValue()));
                }
            }
        }
        UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(paramList, Charset.forName("UTF-8"));
        httpPost.setEntity(reqEntity);

        String result = httpclient.execute(httpPost, new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException
            {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300)
                {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                }
                else
                {
                    throw new IOException("Unexpected response status: " + status);
                }
            }
        });
        //System.out.println(result);
        return result;
    }

    /**
     *
     * @param url 汇报地址
     * @param jsonBody 汇报内容
     * @return true表示汇报成功，false代表失败
     */
    public static String post(String url, String jsonBody) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String body="{\"code\":100,\"info\":\"提交失败！\"}";
        try {
            // 创建发送端
            httpClient = HttpClients.createDefault();
            // 设置request的超时时间
            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(socketTimeout).build();
            // 创建post请求
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);

            if(StringUtils.isNotBlank(jsonBody)){
                // 设置数据体
                httpPost.setEntity(new StringEntity(jsonBody));
            }

            // 发送请求
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != 200) {
                return body;
            }

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                body=EntityUtils.toString(entity, "UTF-8");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(httpClient, response);
        }
        return body;
    }

    private static void close(CloseableHttpClient httpClient, CloseableHttpResponse response) {
        try {
            if (response != null) {
                response.close();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        try {
            if (httpClient != null) {
                httpClient.close();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /**
     *
     * @param url 汇报地址
     * @param nvps 汇报内容
     * @return true表示汇报成功，false代表失败
     */
    public static String post(String url, List<NameValuePair> nvps) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String body="{\"code\":100,\"info\":\"提交失败！\"}";
        try {
            // 创建发送端
            httpClient = HttpClients.createDefault();
            // 设置request的超时时间
            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(socketTimeout).build();
            // 创建post请求
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);

            if(nvps!=null && !nvps.isEmpty()){
                //转码  封装成请求实体
                HttpEntity reqEntity = new UrlEncodedFormEntity(nvps, Consts.UTF_8);
                httpPost.setEntity(reqEntity);
            }

            // 发送请求
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != 200) {
                return body;
            }

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                body = EntityUtils.toString(entity, "UTF-8");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(httpClient, response);
        }
        return body;
    }

    /**
     * 检查某个链接是否可用
     * @param url
     * @return
     */
    public static boolean checkUrlIsValid(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectionRequestTimeout)
                .build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = null;

        boolean isValid = false;

        try {
            response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 200) {
                isValid = true;
            }else{
                System.out.println("statusCode:" + statusCode);
            }
        } catch (Exception e) {

        } finally {
            if(response != null) {
                try {
                    response.close();
                } catch (IOException e) {

                }
            }
        }
        return isValid;
    }
}