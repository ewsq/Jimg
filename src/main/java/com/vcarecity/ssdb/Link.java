package com.vcarecity.ssdb;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Link {
	private Socket sock;
	private MemoryStream input = new MemoryStream();
	private boolean closed= false; // 是否已关闭的d连接标志位，true表示关闭，false表示连接
	private boolean check=true;//是否启动连接检查
	public Link(){
		
	}

	public static void main(String[] args) {
		try {
			Link link=new Link("10.239.204.34",15272);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Link(String host, int port) throws Exception{
		this(host, port, 0);
	}

	/**
	 * 发送数据，发送失败返回false,发送成功返回true
	 * @param csocket
	 * @param message
	 * @return
	 */
	public Boolean Send(Socket csocket,String message){
		try{
			PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
			out.println(message);
			return true;
		}catch(Exception se){
			se.printStackTrace();
			closed=true;
			return false;
		}
	}

	/**
	 * 判断是否断开连接，断开返回true,没有返回false
	 * @param socket
	 * @return
	 */
	public Boolean isServerClose(Socket socket){
		try{
			request("info", "");
			return false;
		}catch(Exception se){
			return true;
		}
	}

	public Link(String host, int port, int timeout_ms) throws Exception{
		sock = new Socket(host, port);
		if(timeout_ms > 0){
			sock.setSoTimeout(timeout_ms);
		}
		sock.setTcpNoDelay(true);
		closed=false;
		//方式1：相当于继承了Thread类，作为子类重写run()实现
		/*new Thread() {
			int count=0;
			public void run() {
				while (check) {
					count++;
					//System.out.println("累计检查次数："+count);
					try {
						closed = isServerClose(sock);//判断是否断开
						//System.out.println("是否是关闭状态："+closed);
						//System.out.println("isClosed()："+isClosed());
						//System.out.println("isConnected()："+isConnected());
						if(closed){
							try {
								if(sock!=null) {
									sock.close();
								}
								System.out.println("重新偿试连接 host："+host+" port："+port);
								sock = new Socket(host, port);
								sock.setKeepAlive(true);//开启保持活动状态的套接字
								if(timeout_ms > 0){
									sock.setSoTimeout(timeout_ms);
								}
								sock.setTcpNoDelay(true);
								System.out.println("已连接");
								closed=false;
							} catch (Exception e) {
								System.out.println("连接异常");
								closed=true;
							}
						}
						Thread.sleep(100000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
		}.start();*/

		//方式2:实现Runnable,Runnable作为匿名内部类
		/*new Thread(new Runnable() {
			public void run() {
				System.out.println("匿名内部类创建线程方式2...");
				while (true) {
					try {
						if(!sock.isConnected() || sock.isClosed()){
							sock = new Socket(host, port);
						}
						Thread.sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();*/

	}

	public Response request(String cmd, byte[]...params) throws Exception{
		ArrayList<byte[]> list = new ArrayList<byte[]>();
		for(byte[] s : params){
			list.add(s);
		}
		return this.request(cmd, list);
	}
	
	public Response request(String cmd, String...params) throws Exception{
		ArrayList<byte[]> list = new ArrayList<byte[]>();
		for(String s : params){
			list.add(s.getBytes());
		}
		return this.request(cmd, list);
	}

	public Response request(String cmd, List<byte[]> params) throws Exception{
		MemoryStream buf = new MemoryStream(4096);
		Integer len = cmd.length();
		buf.write(len.toString());
		buf.write('\n');
		buf.write(cmd);
		buf.write('\n');
		for(byte[] bs : params){
			len = bs.length;
			buf.write(len.toString());
			buf.write('\n');
			buf.write(bs);
			buf.write('\n');
		}
		buf.write('\n');
		send(buf);
		
		List<byte[]> list = recv();
		return new Response(list);
	}
	
	private void send(MemoryStream buf) throws Exception{
		try {
			OutputStream os = sock.getOutputStream();
			os.write(buf.toArray());
			os.flush();
		}catch (Exception e){
			closed=true;
			throw new Exception(e.getMessage());
		}
	}
	
	private List<byte[]> recv() throws Exception{
		input.nice();
		try {
			InputStream is = sock.getInputStream();
			while(true){
				List<byte[]> ret = parse();
				if(ret != null){
					return ret;
				}
				byte[] bs = new byte[8192];
				int len = is.read(bs);
				//System.out.println("<< " + (new MemoryStream(bs, 0, len)).printable());
				input.write(bs, 0, len);
			}
		}catch (Exception e){
			closed=true;
			throw new Exception(e.getMessage());
		}
	}

	public void testRead(byte[] data) throws Exception{
		input.write(data, 0, data.length);
		System.out.println("<< " +input.repr());

		List<byte[]> ret = parse();
		if(ret != null){
			System.out.println("---------------------");
			for (byte[] bs : ret) {
				System.out.println(String.format("%-15s", MemoryStream.repr(bs)));
			}
		}
	}

	private List<byte[]> parse() throws Exception{
		ArrayList<byte[]> list = new ArrayList<byte[]>();
		
		int idx = 0;
		// ignore leading empty lines
		while(idx < input.size && (input.chatAt(idx) == '\r' || input.chatAt(idx) == '\n')){
			idx ++;
		}
		System.out.println("idx:"+idx);

		while(idx < input.size){
			int data_idx = input.memchr('\n', idx);
			if(data_idx == -1){
				break;
			}
			data_idx += 1;
			
			int head_len = data_idx - idx;
			if(head_len == 1 || (head_len == 2 && input.chatAt(idx) == '\r')){
				input.decr(data_idx);
				return list;
			}
			String str = new String(input.copyOfRange(idx, data_idx));
			str = str.trim();
			int size;
			try{
				size = Integer.parseInt(str, 10);
			}catch(Exception e){
				throw new Exception("Parse body_len error");
			}
			
			idx = data_idx + size;

			int left = input.size - idx;
			if(left >= 1 && input.chatAt(idx) == '\n'){
				idx += 1;
			}else if(left >= 2 && input.chatAt(idx) == '\r' && input.chatAt(idx+1) == '\n'){
				idx += 2;
			}else if(left >= 2){
				throw new Exception("bad format");
			}else{
				break;
			}
			// System.out.println("size: " + size + " idx: " + idx + " left: " + (input.size - idx));
			
			byte[] data = input.copyOfRange(data_idx, data_idx + size);
			//System.out.println("size: " + size + " data: " + data.length);
			list.add(data);
		}
		return null;		
	}

	public void close(){
		check=false;
		closed=true;
		try{
			sock.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void release(){
		check=false;
		closed=true;
		try{
			sock.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public boolean isConnected() {
		return !closed;
	}

	public boolean isClosed() {
		return closed;
	}

	public String info() {
		return host() + ":" + port() + ":" + timeout();
	}
	public String host() {
		return sock.getInetAddress().getHostAddress();
	}
	public int port() {
		return sock.getPort();
	}
	public int timeout() {
		try {
			return sock.getSoTimeout();
		} catch (SocketException e) {
			return -1;
		}
	}
}
