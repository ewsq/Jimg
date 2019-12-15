package com.vcarecity.utils;

import java.io.Serializable;

public class UploadFileItem implements Serializable{  
    private static final long serialVersionUID = 1L;  
    private String formFieldName;  
    private String fileName;  
  
    public UploadFileItem(String formFieldName, String fileName){
        this.formFieldName = formFieldName;  
        this.fileName = fileName;  
    }  
  
    public String getFormFieldName(){  
        return formFieldName;  
    }  
  
    public void setFormFieldName(String formFieldName){  
        this.formFieldName = formFieldName;  
    }  
  
    public String getFileName(){
        return fileName;
    }  
  
    public void setFileName(String fileName){
        this.fileName = fileName;
    }  
}