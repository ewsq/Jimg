package com.vcarecity.utils;

import java.io.Serializable;

/**
 * Http请求返回的结果对象信息
* @author comven
* @date 2016/10/22 11:29
*/
public class HttpResult implements Serializable{
    private Integer statusCode;
    private String content;
 
    public HttpResult(Integer statusCode, String content) {
        this.statusCode = statusCode;
        this.content = content;
    }
    public Integer getStatusCode() {
        return statusCode;
    }
 
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
 
    public String getContent() {
        return content;
    }
 
    public void setContent(String content) {
        this.content = content;
    }
 
    @Override
    public String toString() {
        return "HttpResult{" +
                "statusCode=" + statusCode +
                ", content='" + content + '\'' +
                '}';
    }
}
