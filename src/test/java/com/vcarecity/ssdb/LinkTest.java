package com.vcarecity.ssdb;

import org.junit.Test;

/**
 * SSDB Java client SDK demo.
 */
public class LinkTest {
	public static void main(String[] args) throws Exception {
		String s = "3\r\nget\n1\r\nk\r\n\r\n";
		s += s;
		s += s;
		byte[] bytes = s.getBytes();
		Link link = new Link();
		link.testRead(bytes);
		for(int i=0; i<bytes.length; i++){
			byte[] bs = {bytes[i]};
			link.testRead(bs);
		}
	}

	@Test
	public void Test01(){
		try {
			new Thread() {
				int count = 0;
				Link link=new Link("10.239.204.34",15272);
				public void run() {
					while (true) {
						count++;
						try {
							Response resp = link.request("info", "");
							if(resp.ok()){
								System.out.println(resp.status);
							}
							Thread.sleep(10000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void Test02(){
		try {
			Link link=new Link("10.239.204.34",15272);
			new Thread() {
				int count = 0;
				public void run() {
					while (true) {
						count++;
						try {
							Response resp = link.request("info", "");
							if(resp.ok()){
								System.out.println(resp.status);
							}
							Thread.sleep(10000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
