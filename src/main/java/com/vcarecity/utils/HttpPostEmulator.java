package com.vcarecity.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HttpPostEmulator {
	// 每个post参数之间的分隔。随意设定，只要不会和其他的字符串重复即可。
	private static final String BOUNDARY = "----------HV2ymHFg03ehbqgZCaKO6jyH";
	
	public static void main(String args[]) throws Exception {  
        // 设定服务地址  
        String serverUrl = "http://192.168.10.81:9000/01/upload";//上传地址  
          
        // 设定要上传的普通Form Field及其对应的value  
        ArrayList<FormFieldKeyValuePair> ffkvp = new ArrayList<FormFieldKeyValuePair>();  
        ffkvp.add(new FormFieldKeyValuePair("phone", "123456789"));//其他参数  
        ffkvp.add(new FormFieldKeyValuePair("type", "jpg"));  
  
        // 设定要上传的文件  
        ArrayList<UploadFileItem> ufi = new ArrayList<UploadFileItem>();  
        ufi.add(new UploadFileItem("userfile1", "D:\\temp\\232618250858230522_1.jpg"));  
        HttpPostEmulator hpe = new HttpPostEmulator();  
        String response = hpe.sendHttpPostRequest(serverUrl, ffkvp, ufi);  
        System.out.println("Responsefrom server is: " + response);
    }
	
	/**
	 * sendHttpPostRequest 发送post请求
	 * @param serverUrl 文件接收服务的URL
	 * @param generalFormFields 表单字本或数字字段
	 * @param filesToBeUploaded 表单文件字段
	 * @return
	 * @throws Exception
	 */
	public String sendHttpPostRequest(String serverUrl, ArrayList<FormFieldKeyValuePair> generalFormFields,
			ArrayList<UploadFileItem> filesToBeUploaded) throws Exception {

		// 向服务器发送post请求
		URL url = new URL(serverUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// 发送POST请求必须设置如下两行
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestProperty("Charset", "UTF-8");
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

		// 头
		String boundary = BOUNDARY;
		// 传输内容
		StringBuffer contentBody = new StringBuffer("--" + BOUNDARY);
		// 尾
		String endBoundary = "\r\n--" + boundary + "--\r\n";

		OutputStream out = connection.getOutputStream();
		
		// 1. 处理文字形式的POST请求
		for (FormFieldKeyValuePair ffkvp : generalFormFields){
			contentBody.append("\r\n")
					.append("Content-Disposition: form-data; name=\"")
					.append(ffkvp.getKey() + "\"")
					.append("\r\n")
					.append("\r\n")
					.append(ffkvp.getValue())
					.append("\r\n")
					.append("--")
					.append(boundary);
		}

		String boundaryMessage1 = contentBody.toString();
		out.write(boundaryMessage1.getBytes("utf-8"));

		// 2. 处理文件上传
		for (UploadFileItem ufi : filesToBeUploaded){
			contentBody = new StringBuffer();
			contentBody.append("\r\n")
					.append("Content-Disposition:form-data; name=\"")
					.append(ufi.getFormFieldName() + "\"; ") // form中field的名称
					.append("filename=\"")
					.append(ufi.getFileName() + "\"") // 上传文件的文件名，包括目录
					.append("\r\n")
					.append("Content-Type:application/octet-stream")
					.append("\r\n\r\n");
			
			String boundaryMessage2 = contentBody.toString();
			out.write(boundaryMessage2.getBytes("utf-8"));

			// 开始真正向服务器写文件
			File file = new File(ufi.getFileName());
			DataInputStream dis = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[(int) file.length()];
			bytes = dis.read(bufferOut);
			out.write(bufferOut, 0, bytes);
			dis.close();
			
			contentBody.append("------------HV2ymHFg03ehbqgZCaKO6jyH");
			String boundaryMessage = contentBody.toString();
			out.write(boundaryMessage.getBytes("utf-8"));

		}

		out.write("------------HV2ymHFg03ehbqgZCaKO6jyH--\r\n".getBytes("UTF-8"));
		
		// 3. 写结尾
		out.write(endBoundary.getBytes("utf-8"));
		out.flush();
		out.close();

		// 4. 从服务器获得回答的内容
		String strLine = "";
		String strResponse = "";
		InputStream in = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		while ((strLine = reader.readLine()) != null){
			strResponse += strLine + "\n";
		}		
		return strResponse;

	}

}