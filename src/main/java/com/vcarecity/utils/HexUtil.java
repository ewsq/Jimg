package com.vcarecity.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexUtil {
	private static String hexString = "0123456789ABCDEF";//16进制数字字符集
	private static String dexString = "0123456789";
	private static char[] hexChar = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D','E', 'F' };
	
	/**
	 * 获取安全字节
	 * @param ch
	 * @return
	 */
	public static byte getHalfByte(byte ch) {
		byte hb = 0x1a;
		byte cA = (byte) ('A' & 0xff);
		byte cF = (byte) ('F' & 0xff);
		byte ca = (byte) ('a' & 0xff);
		byte cf = (byte) ('f' & 0xff);
		if (ch >= '0' && ch <= '9') {
			hb = (byte) (ch - '0');
		} else if (ch >= ca && ch <= cf) {
			hb = (byte) (ch - ca + 0x0a);
		} else if (ch >= cA && ch <= cF) {
			hb = (byte) (ch - cA + 0x0a);
		}
		return hb;
	}
	/**
	 * 整数转字符串
	 * @param ch
	 * @return
	 */
	public static String IntToStr4(int ch) {
		StringBuffer dStr = new StringBuffer("");
		dStr.append(hexString.charAt((ch / 1000) % 10));
		dStr.append(hexString.charAt((ch / 100) % 10));
		dStr.append(hexString.charAt((ch / 10) % 10));
		dStr.append(hexString.charAt((ch / 1) % 10));
		return dStr.toString();
	}
	
	/**
	 * 十六进制表示的字符串转换为可视化显示的字符串
	 * @param str
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String HexStrToStr(String str) throws UnsupportedEncodingException {
		if (str == null || str.trim().length() <= 0) {
			return "";
		}
		System.out.println("HexStrToStr str:"+str );
		byte[] strByte = StrToHex(str.trim());
		String reStr="";
		if(strByte!=null){
			reStr=new String(strByte, 0, strByte.length, "GBK");
		}
		return reStr;
	}
	
	/**
	 * 字符串转16进制
	 * @param str
	 * @return
	 */
	public static byte[] StrToHex(String str) {
		byte[] strByte = str.getBytes();
		int length = strByte.length;
		byte[] temp = new byte[length];
		int index = 0;
		int i;
		byte[] b = new byte[2];
		byte bindex = 0;
		for (i = 0; i < length; i++) {
			byte bu = strByte[i];
			byte hb = getHalfByte(bu);
			if (hb == 0x1a)
				continue;
			b[bindex++] = hb;
			if (bindex == 2) {
				temp[index++] = (byte) ((((byte) b[0]) << 4) + (byte) b[1]);
				bindex = 0;
			}
		}
		if (index == 0)
			return null;
		byte[] ret = new byte[index];
		System.arraycopy(temp, 0, ret, 0, index);
		return ret;
	}
	/**
	 * 十进制转字符串
	 * @param data
	 * @param offset
	 * @param length
	 * @param split
	 * @return
	 */
	public static String HexToStr(byte[] data, int offset, int length, char split) {
		int i;
		if (length == 0)
			return "";
		byte sByte;
		char[] ch = new char[length * 3];
		int index = 0;
		int tLength = length + offset - 1;
		for (i = offset; i < tLength; i++) {
			sByte = data[i];
			ch[index++] = (hexChar[(sByte >> 4) & 0xf]);
			ch[index++] = (hexChar[sByte & 0xf]);
			ch[index++] = (split);
		}
		sByte = data[i];
		ch[index++] = (hexChar[(sByte >> 4) & 0xf]);
		ch[index++] = (hexChar[sByte & 0xf]);
		return String.valueOf(ch);
	}
	/**
	 * 十六进制转字符串
	 * @param data
	 * @param offset
	 * @param length
	 * @return
	 */
	public static String HexToStr(byte[] data, int offset, int length) {
		int i;
		if (length == 0)
			return "";
		char[] ch = new char[length * 2];
		try {
			byte sByte;
			int tLength = length + offset;
			int index = 0;
			for (i = offset; i < tLength; i++) {
				sByte = data[i];
				ch[index++] = (hexChar[(sByte >> 4) & 0xf]);
				ch[index++] = (hexChar[sByte & 0xf]);
			}
		} catch (Exception e) {
			PrintUtil.pritnfDebugInfo(">>>>>>>>>>>>>>>>>>:"+HexUtil.Bytes2HexString(data));
			e.printStackTrace();
		}
		return String.valueOf(ch);
	}
	/**
	 * 测试十六进制转字符串的转换速度
	 * @param hex
	 * @return
	 */
	public static long testHexToStrnew(byte[] hex) {
		int i;
		int count = 100000;
		long starTime, endTime;
		starTime = System.currentTimeMillis();
		for (i = 0; i < count; i++) {
			String str = HexToStr(hex, 0, 700, '-');
		}
		endTime = System.currentTimeMillis();
		return endTime - starTime;
	}
	/**
	 * 字符串正则匹配
	 * @param dest
	 * @param regexp
	 * @return
	 */
	public static boolean strCompile(String dest,String regexp)
	{
		if(dest==null)
			return false;
		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(dest);
		return m.find();
	}
	
	/**
	 * 判断字符串是否为空
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str)
	{
		return (str!=null&&!str.isEmpty());
	}
	
	
	/**
	 * 数字字符串转ASCII码字符串
	 * 
	 * @param content
	 *            字符串
	 * @return ASCII字符串
	 */
	public static String StringToAsciiString(String content) {
		String result = "";
		int max = content.length();
		for (int i = 0; i < max; i++) {
			char c = content.charAt(i);
			String b = Integer.toHexString(c);
			result = result + b;
		}
		return result;
	}

	/**
	 * 十六进制转字符串
	 * 
	 * @param hexString
	 *            十六进制字符串
	 * @param encodeType
	 *            编码类型4：Unicode，2：普通编码
	 * @return 字符串
	 */
	public static String hexStringToString(String hexString, int encodeType) {
		String result = "";
		int max = hexString.length() / encodeType;
		for (int i = 0; i < max; i++) {
			char c = (char) HexUtil.hexStringToAlgorism(hexString
					.substring(i * encodeType, (i + 1) * encodeType));
			result += c;
		}
		return result;
	}

	/**
	 * 十六进制字符串转十进制
	 * 
	 * @param hex
	 *            十六进制字符串
	 * @return 十进制数值
	 */
	public static int hexStringToAlgorism(String hex) {
		hex = hex.toUpperCase();
		int max = hex.length();
		int result = 0;
		for (int i = max; i > 0; i--) {
			char c = hex.charAt(i - 1);
			int algorism = 0;
			if (c >= '0' && c <= '9') {
				algorism = c - '0';
			} else {
				algorism = c - 55;
			}
			result += Math.pow(16, max - i) * algorism;
		}
		return result;
	}

	/**
	 * 十六转二进制
	 * 
	 * @param hex
	 *            十六进制字符串
	 * @return 二进制字符串
	 */
	public static String hexStringToBinary(String hex) {
		hex = hex.toUpperCase();
		String result = "";
		int max = hex.length();
		for (int i = 0; i < max; i++) {
			char c = hex.charAt(i);
			switch (c) {
			case '0':
				result += "0000";
				break;
			case '1':
				result += "0001";
				break;
			case '2':
				result += "0010";
				break;
			case '3':
				result += "0011";
				break;
			case '4':
				result += "0100";
				break;
			case '5':
				result += "0101";
				break;
			case '6':
				result += "0110";
				break;
			case '7':
				result += "0111";
				break;
			case '8':
				result += "1000";
				break;
			case '9':
				result += "1001";
				break;
			case 'A':
				result += "1010";
				break;
			case 'B':
				result += "1011";
				break;
			case 'C':
				result += "1100";
				break;
			case 'D':
				result += "1101";
				break;
			case 'E':
				result += "1110";
				break;
			case 'F':
				result += "1111";
				break;
			}
		}
		return result;
	}

	/**
	 * ASCII码字符串转数字字符串
	 * 
	 * @param content
	 *            ASCII字符串
	 * @return 字符串
	 */
	public static String AsciiStringToString(String content) {
		String result = "";
		int length = content.length() / 2;
		for (int i = 0; i < length; i++) {
			String c = content.substring(i * 2, i * 2 + 2);
			int a = hexStringToAlgorism(c);
			char b = (char) a;
			String d = String.valueOf(b);
			result += d;
		}
		return result;
	}

	/**
	 * 将十进制转换为指定长度的十六进制字符串
	 * 
	 * @param algorism
	 *            int 十进制数字
	 * @param maxLength
	 *            int 转换后的十六进制字符串长度
	 * @return String 转换后的十六进制字符串
	 */
	public static String algorismToHEXString(int algorism, int maxLength) {
		String result = "";
		result = Integer.toHexString(algorism);

		if (result.length() % 2 == 1) {
			result = "0" + result;
		}
		return patchHexString(result.toUpperCase(), maxLength);
	}

	/**
	 * 字节数组转为普通字符串（ASCII对应的字符）
	 * 
	 * @param bytearray
	 *            byte[]
	 * @return String
	 */
	public static String bytetoString(byte[] bytearray) {
		String result = "";
		char temp;

		int length = bytearray.length;
		for (int i = 0; i < length; i++) {
			temp = (char) bytearray[i];
			result += temp;
		}
		return result;
	}

	/**
	 * 二进制字符串转十进制
	 * 
	 * @param binary
	 *            二进制字符串
	 * @return 十进制数值
	 */
	public static int binaryToAlgorism(String binary) {
		int max = binary.length();
		int result = 0;
		for (int i = max; i > 0; i--) {
			char c = binary.charAt(i - 1);
			int algorism = c - '0';
			result += Math.pow(2, max - i) * algorism;
		}
		return result;
	}

	/**
	 * 十进制转换为十六进制字符串
	 * 
	 * @param algorism
	 *            int 十进制的数字
	 * @return String 对应的十六进制字符串
	 */
	public static String algorismToHEXString(int algorism) {
		String result = "";
		result = Integer.toHexString(algorism);

		if (result.length() % 2 == 1) {
			result = "0" + result;

		}
		result = result.toUpperCase();

		return result;
	}

	/**
	 * HEX字符串前补0，主要用于长度位数不足。
	 * 
	 * @param str
	 *            String 需要补充长度的十六进制字符串
	 * @param maxLength
	 *            int 补充后十六进制字符串的长度
	 * @return 补充结果
	 */
	static public String patchHexString(String str, int maxLength) {
		String temp = "";
		for (int i = 0; i < maxLength - str.length(); i++) {
			temp = "0" + temp;
		}
		str = (temp + str).substring(0, maxLength);
		return str;
	}

	/**
	 * 将一个字符串转换为int
	 * 
	 * @param s
	 *            String 要转换的字符串
	 * @param defaultInt
	 *            int 如果出现异常,默认返回的数字
	 * @param radix
	 *            int 要转换的字符串是什么进制的,如16 8 10.
	 * @return int 转换后的数字
	 */
	public static int parseToInt(String s, int defaultInt, int radix) {
		int i = 0;
		try {
			i = Integer.parseInt(s, radix);
		} catch (NumberFormatException ex) {
			i = defaultInt;
		}
		return i;
	}

	/**
	 * 将一个十进制形式的数字字符串转换为int
	 * 
	 * @param s
	 *            String 要转换的字符串
	 * @param defaultInt
	 *            int 如果出现异常,默认返回的数字
	 * @return int 转换后的数字
	 */
	public static int parseToInt(String s, int defaultInt) {
		int i = 0;
		try {
			i = Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			i = defaultInt;
		}
		return i;
	}

	/**
	 * 十六进制字符串转为Byte数组,每两个十六进制字符转为一个Byte
	 * 
	 * @param hex
	 *            十六进制字符串
	 * @return byte 转换结果
	 */
	public static byte[] hexStringToByte(String hex) {
		int max = hex.length() / 2;
		byte[] bytes = new byte[max];
		String binarys = HexUtil.hexStringToBinary(hex);
		for (int i = 0; i < max; i++) {
			bytes[i] = (byte) HexUtil.binaryToAlgorism(binarys.substring(
					i * 8 + 1, (i + 1) * 8));
			if (binarys.charAt(8 * i) == '1') {
				bytes[i] = (byte) (0 - bytes[i]);
			}
		}
		return bytes;
	}

	/**
	 * 十六进制串转化为byte数组
	 * 
	 * @return the array of byte
	 */
	public static final byte[] hex2byte(String hex)
			throws IllegalArgumentException {
		hex=hex.replaceAll(" ", "");
		if (hex.length() % 2 != 0) {
			throw new IllegalArgumentException();
		}
		char[] arr = hex.toCharArray();
		byte[] b = new byte[hex.length() / 2];
		for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
			String swap = "" + arr[i++] + arr[i];
			int byteint = Integer.parseInt(swap, 16) & 0xFF;
			b[j] = new Integer(byteint).byteValue();
		}
		return b;
	}

	/**
	 * 字节数组转换为十六进制字符串
	 * 
	 * @param b
	 *            byte[] 需要转换的字节数组
	 * @return String 十六进制字符串
	 */
	public static final String byte2hex(byte b[]) {
		if (b == null) {
			throw new IllegalArgumentException(
					"Argument b ( byte array ) is null! ");
		}
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0xff);
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}

	// 转化字符串为十六进制编码
	public static String toHexString(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}
		return str;
	}
	
	/**
	 * 
	 * <b>功能说明 : </b>转化十六进制编码为字符串为指定编码的字符串，适合中文转码. <br/>
	 * <b>使用示例 : </b>System.out.println(hexStringToEncodeString("B8B4CEBB","GBK"));.<br/>
	 * @param s
	 * @param Encoding 可以是："utf-8"、"GBK"、"GB2312"等
	 * @return String 说明:TODO(这里描述返回对象是什么 – 可选,如果不需说明则删除此句).
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年3月9日 下午6:12:59<br/>
	 */
	public static String hexStringToEncodeString(String s,String Encoding) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(
						s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, Encoding);// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

	// 转化十六进制编码为字符串
	public static String toStringHex2(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(
						s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "GBK");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

	/*
	 * 将字符串编码成16进制数字,适用于所有字符（包括中文）
	 */
	public static String encode(String str) {
		// 根据默认编码获取字节数组
		byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		// 将字节数组中每个字节拆解成2位16进制整数
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		}
		return sb.toString();
	}

	/*
	 * 将16进制数字解码成字符串,适用于所有字符（包括中文）
	 */
	public static String decode(String bytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				bytes.length() / 2);
		// 将每2位16进制整数组装成一个字节
		for (int i = 0; i < bytes.length(); i += 2)
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
					.indexOf(bytes.charAt(i + 1))));
		return new String(baos.toByteArray());
	}

	/**
	 * 将指定byte数组以16进制的形式打印到控制台
	 * 
	 * @param hint
	 *            String
	 * @param b
	 *            byte[]
	 * @return void
	 */
	public static void printHexString(String hint, byte[] b) {
		System.out.print(hint);
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex.toUpperCase() + " ");
		}
		System.out.println("");
	}

	/**
	 * @param b
	 *            byte[]
	 * @return String
	 */
	public static String Bytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	/**
	 * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
	 * 
	 * @param src0
	 *            byte
	 * @param src1
	 *            byte
	 * @return byte
	 */
	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	/**
	 * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
	 * 0xD9}
	 * 
	 * @param src
	 *            String
	 * @return byte[]
	 */
	public static byte[] HexString2Bytes(String src) {
		byte[] ret = new byte[8];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < 8; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}
	
	/**
	 * stringSplit
	 * <b>功能说明 : </b>字符串分隔函数，将以特定字符分隔的字符串转化成数组. <br/>
	 * @param srcStr
	 * @return String[] 说明:转换后的数组.
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年3月10日 下午6:42:38<br/>
	 */
	public static String[] stringSplit(String srcStr) {
		String str = "打印机*钟表//自行车**雨伞%%收音机??电脑 水杯 风扇";
        str = "03/06 12:36 09-008 故障一层2区烟感03/06 12:36 09-008 故障一层2区烟感";
        //利用+表示一个或多个
        String temp[] = srcStr.split("%%|\\*+|\\//|\\?+|\\ +|\\+");
        for(String word : temp)
        {
            System.out.println(word);
        }
        return temp;
	}

	/**
	 * stringSplit
	 * <b>功能说明 : </b>字符串分隔函数，将以特定字符分隔的字符串转化成数组. <br/>
	 * @param srcStr
	 * @param pattern 正则表达式
	 * @return String[] 说明:转换后的数组.
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年3月10日 下午6:42:38<br/>
	 */
	public static String[] stringSplit(String srcStr,String pattern) {
		String str = "打印机*钟表//自行车**雨伞%%收音机??电脑 水杯 风扇";
        str = "03/06 12:36 09-008 故障一层2区烟感03/06 12:36 09-008 故障一层2区烟感";
        //利用+表示一个或多个
        String temp[] = srcStr.split(pattern);
        for(String word : temp)
        {
            System.out.println(word);
        }
        return temp;
	}

	public static String replaceBlank(String str) {
		Pattern p = Pattern.compile("\r|\n|\t");
		//System.out.println("before:" + str);
		Matcher m = p.matcher(str);
		String after = m.replaceAll("");
		///System.out.println("after:" + after);
		return after;
	}

	/**
	 * 移除特定字节串
	 * @param hexStr
	 * @param regexp
	 * @return
	 */
	public static String removeSpecificBytes(String hexStr,String regexp) {
		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(hexStr);
		String after = m.replaceAll("");
		return after;
	}
	
	/**
	 * 移除特定字节串
	 * @param src
	 * @param regexp
	 * @return
	 */
	public static byte[] removeSpecificBytes(byte[] src,String regexp) {
		byte[] bt=src;
		String[] tmpArr=regexp.split("\\|");
		for(String s:tmpArr){
			byte[] bty=hex2byte(s);
			bt=filterSpecificBytes(bt,bty);
		}
		return bt;
	}
	
	/**
	 * 移除特定字节串
	 * @param src
	 * @param specificBytes
	 * @return
	 */
	public static byte[] removeSpecificBytes(byte[] src,byte[] specificBytes) {
		List<Byte> list=new ArrayList<Byte>();
		int specificBytesLen=specificBytes.length;
		byte[] startByte=new byte[specificBytesLen];
		int len=src.length;
		for(int i=0; i<src.length;i++) {
			if(len-i>=specificBytesLen){
				System.arraycopy(src,i,startByte,0,startByte.length);
				if (Arrays.equals(startByte, specificBytes)) {
					i+=specificBytesLen-1;
				}else{
					list.add(src[i]);
				}
			}else{
				list.add(src[i]);
			}
		}
	    byte[] array = listTobyte(list);	    
		return array;
	}
	
	public static String  specificBytes(){
		StringBuffer regexp=new StringBuffer();
		
		regexp.append("1B 40 1B 63 00 1B 38 04 |".replace(" ", ""));
		regexp.append("00 01 00 01 00 00 00 20 20 |".replace(" ", ""));
		regexp.append("1B 66 00 00 |".replace(" ", ""));
		regexp.append("1C 2E 1B 40 |".replace(" ", ""));
		regexp.append("1B 40 1C 26 |".replace(" ", ""));
		
		
		//========================北京利达华信 JB-QG-LD28E(Q)II 、JB-QG-LD128E(Q)I  特别包 开始=======================================//
		regexp.append("40 1B 31 05 1B 36 1B 39 |".replace(" ", ""));
		regexp.append("1B 4A 01 1B 39 1B |".replace(" ", ""));
		regexp.append("1B 4A 01 1B 39 |".replace(" ", ""));
		//========================北京利达华信 JB-QG-LD28E(Q)II 、JB-QG-LD128E(Q)I  特别包结束=======================================//
		
		
		//========================安吉斯JB-LB-CA2000/SZ  特别包 开始=======================================//
		regexp.append("1B 40 1B 63 00 1B 38 04 |".replace(" ", ""));
		regexp.append("A1 A1 A1 A1 A1 A1 0D 0D 20 0D |".replace(" ", ""));
		regexp.append("0D 20 20 A1 A1 A1 A1 A1 A1 A1 A1 A1 A1 |".replace(" ", ""));
		regexp.append("1B 40 1B 63 00 1B 38 04 0E |".replace(" ", ""));
		regexp.append("20 0D 20 0D 20 0D 20 0D 20 0D|".replace(" ", ""));
		//========================安吉斯JB-LB-CA2000/SZ  特别包结束=======================================//
		
		//========================营口天成 JB-QB-TC3020  特别包 开始=======================================//
		regexp.append("1B 38 00 |".replace(" ", ""));
		regexp.append("1B 4A 05 |".replace(" ", ""));
		//========================营口天成 JB-QB-TC3020  特别包结束=======================================//
		
		//========================国泰怡安GK601（JB-TBZL-GK601） 串口  4800 不需要字库 代表: 4442835D7C3C开始=======================================//
		regexp.append("7E 00 03 00 6F DE |".replace(" ", ""));
		regexp.append("F3 03 03 00 6F DE |".replace(" ", ""));
		regexp.append("7E 00 07 02 DB 1B 4A 20 67 BC |".replace(" ", ""));		
		//========================国泰怡安GK601（JB-TBZL-GK601） 串口  4800 不需要字库 代表: 4442835D7C3C 特别包结束=======================================//

		//========================安吉斯JB-LB-GEC2000  特别包 开始=======================================//
		regexp.append("1B 40 1B 63 00 0E 1B 38 |".replace(" ", ""));
		regexp.append("1B 40 1B 63 00 1B 38 |".replace(" ", ""));
		regexp.append("14 20 |".replace(" ", ""));
		//========================安吉斯JB-LB-GEC2000  特别包结束=======================================//
		
		//========================南京盛华SH2112A 并口 1EA483C92E30 不需要字库  特别包 开始=======================================//
		regexp.append("1B 40 1B 31 03 1B 38 04 |".replace(" ", ""));
		//========================南京盛华SH2112A 并口 1EA483C92E30 不需要字库  特别包结束=======================================//
		

		//========================1EA483C42830  特别包 开始=======================================//
		regexp.append("1C 26 00 |".replace(" ", ""));
		//regexp.append("0D 0A 0A 0D |".replace(" ", ""));
		//========================1EA483C42830 特别包结束=======================================//
		
		//========================营口天成TC3020 并口 1EA4830C502E 不需要字库  特别包 开始=======================================//
		regexp.append("1B 38 00 |".replace(" ", ""));//头
		regexp.append("1B 4A 05 |".replace(" ", ""));//尾
		regexp.append("00 00 00 00 00 00 00 00 |".replace(" ", ""));//特别数据
		//========================营口天成TC3020 并口 1EA4830C502E 不需要字库  特别包结束=======================================//
				
		//========================奥瑞纳0ZH200 串口 9600 不需字库  44428376773C 特别包 开始=======================================//
		regexp.append("1B 4A 10 00|".replace(" ", ""));
		regexp.append("1B 4A 10|".replace(" ", ""));
		//========================奥瑞纳0ZH200 串口 9600 不需字库  44428376773C 特别包 结束=======================================//
		
		
		//========================营口新山鹰YBZ127 并口 不需要字库 1EA483613B30  特别包 开始=======================================//
		regexp.append("1B 66 00 02 |".replace(" ", "")); 
		//========================营口新山鹰YBZ127并口 不需要字库 1EA483613B30  特别包 结束=======================================//
		
		//========================杭州云安1506 并口 不需要字库 1EA483703E30  特别包 开始=======================================//
		regexp.append("1B 66 00 |".replace(" ", ""));
		regexp.append("1C 2E |".replace(" ", ""));
		regexp.append("1C 26 |".replace(" ", ""));
		regexp.append("1B 4A 1E |".replace(" ", ""));
		//========================杭州云安1506 并口 不需要字库 1EA483703E30  特别包 结束=======================================//
		
		
		//========================依爱EI-2000G 并口 无字库 1EA483C72830  特别包 开始=======================================//
		regexp.append("1C 26 |".replace(" ", ""));
		regexp.append("1C 69 00 |".replace(" ", ""));
		//========================依爱EI-2000G 并口 无字库 1EA483C72830  特别包 结束=======================================//
		
		//========================利达华信，JB-QB/LD128  并口 无字库 1EA4838B2930  特别包 开始=======================================//
		regexp.append("1B 40 |".replace(" ", ""));
		regexp.append("1B 31 05 |".replace(" ", ""));
		regexp.append("1B 36 |".replace(" ", ""));
		regexp.append("1B 39 |".replace(" ", ""));
		regexp.append("1B 4A 00 |".replace(" ", ""));
		//========================利达华信，JB-QB/LD128  并口 无字库 1EA4838B2930  特别包 结束=======================================//
		
		//========================利达华信，JB-QB/LD128  并口 无字库 1EA4838B2930  特别包 开始=======================================//
		regexp.append("1C 76 00 |".replace(" ", ""));
		//========================利达华信，JB-QB/LD128  并口 无字库 1EA4838B2930  特别包 结束=======================================//		

		//========================秦皇岛尼特 JB-QB-FT8003  并口 无字库 1EA483F32E30  特别包 开始=======================================//
		regexp.append("1B 38 |".replace(" ", ""));
		//========================秦皇岛尼特 JB-QB-FT8003  并口 无字库 1EA483F32E30  特别包 结束=======================================//		
				
		

		//========================杭州云安 YA1506 并口 不需要字库 1EA483703E30  特别包 开始=======================================//
		regexp.append("1B 66 00 01  |".replace(" ", ""));
		regexp.append("1C 2E  |".replace(" ", ""));
		regexp.append("1C 26  |".replace(" ", ""));
		regexp.append("1B 66 00 00  |".replace(" ", ""));
		regexp.append("1B 66 00 02  |".replace(" ", ""));
		regexp.append("1B 66 00 03  |".replace(" ", ""));
		regexp.append("1B 66 00 04  |".replace(" ", ""));
		regexp.append("1B 66 00 05  |".replace(" ", ""));
		regexp.append("1B 66 00 06  |".replace(" ", ""));
		regexp.append("1B 66 00 07  |".replace(" ", ""));
		//========================杭州云安 YA1506 并口 不需要字库 1EA483703E30  特别包 结束=======================================//

		
		regexp.append("1B 40 1B 63 00 1B 38 04 ".replace(" ", ""));
		
		return regexp.toString();
	}
	
	/**
	 * checkSpecificBytes
	 * 检查是否有指定的特征字符串，有特征字符串的数据将不进行后续处理
	 * @param hexStr
	 * @return
	 */
	public static boolean  checkSpecificBytes(String hexStr){
		StringBuffer regexp=new StringBuffer();
		
		//========================上海松江 JB-3208G 并口不需字库  代表：A0A384916B2D  特别包 开始=======================================//
		regexp.append("0A 1C 26 |".replaceAll(" ", ""));
		//========================上海松江 JB-3208G 并口不需字库  代表：A0A384916B2D  特别包结束=======================================//

		//========================成都安吉斯JB-LB-CA2000/SZ 并口不需字库 代表：A0A384FC6F2D  特别包 开始=======================================//
		regexp.append("1B 40 1B 63 00 1B 38 04 |".replaceAll(" ", ""));
		//========================成都安吉斯JB-LB-CA2000/SZ 并口不需字库  代表：A0A384FC6F2D  特别包结束=======================================//

		//========================成都安吉斯 JB-LG-GEC2000 并口不需字库  代表：A0A3846D6F2D  特别包 开始=======================================//
		regexp.append("1B 40 1B 63 00 1B 38 04 |".replaceAll(" ", ""));
		//========================成都安吉斯 JB-LG-GEC2000 并口不需字库  代表：A0A3846D6F2D  特别包结束=======================================//
		
		//========================成都安吉斯 JB-LB-GEC2000 并口不需字库  代表：A0A3848B6B2D  特别包 开始=======================================//
		//regexp.append("1B 40 1B 63 00 1B 38 |".replaceAll(" ", ""));
		//regexp.append("1B 40 1B 63 00 0E 1B 38 |".replaceAll(" ", ""));
		//========================成都安吉斯 JB-LB-GEC2000 并口不需字库  代表：A0A3848B6B2D  特别包结束=======================================//

		//========================北京利达华信 JB-QG-LD128E(Q)I 接口：串口，波特率:9600  代表：1EA483EE2C30  特别包 开始=======================================//
		regexp.append("1B 39 |".replaceAll(" ", ""));
		//========================北京利达华信 JB-QG-LD128E(Q)I 接口：串口，波特率:9600  代表：1EA483EE2C30  特别包结束=======================================//
		
		//========================北京狮岛 SD2200 并口需要字库 接口：并口， 代表：444283D77F3C  特别包 开始=======================================//
		regexp.append("1B 31 04 |".replaceAll(" ", ""));
		//========================北京狮岛 SD2200 并口需要字库 接口：并口， 代表：444283D77F3C  特别包结束=======================================//
		
		
		//========================海湾JB-QT-GST9000  并口   接口：并口， 代表：1EA48341502E  特别包 开始=======================================//
		regexp.append("2D 2B 2D 2B 2D 2B 2D 2B 2D 2B 2D 2B 2D 2B 2D 2B 2D 2B 2D 2B 2D 2B 2D 2B |".replaceAll(" ", ""));
		//========================海湾JB-QT-GST9000  并口   接口：并口， 代表：1EA48341502E  特别包结束=======================================//
		
		//========================贵州东宏福源国区 依爱JB-TTL-EI6002T并口 1EA483262F30  特别包 开始=======================================//
		regexp.append("1C 26 20 0A 0D |".replaceAll(" ", ""));
		//========================贵州东宏福源国区 依爱JB-TTL-EI6002T并口 1EA483262F30  特别包结束=======================================//


		//========================北大青鸟JB-QB/LN1010型  并口  需要字库 代表：1EA483F22830 特别包 开始=======================================//
		regexp.append("1B 40 1B 31 00 0A |".replaceAll(" ", ""));
		//========================北大青鸟JB-QB/LN1010型  并口  需要字库 代表：1EA483F22830  特别包结束=======================================//
		
		//========================北京利达华信 JB-QG-LD188EL 并口 不需要字库 代表: 1EA483C6352E 特别包 开始=======================================//
		regexp.append("1B 40 1C 69 00 1B 43 06 1B 78 00 1C 26 0A 00 00 |".replaceAll(" ", ""));
		//========================北京利达华信 JB-QG-LD188EL 并口 不需要字库 代表: 1EA483C6352E 特别包结束=======================================//
		
		//========================山鹰LTZ-YBZ3032 并口   不需要字库 代表: A0A3841E6D2D 特别包 开始=======================================//
		regexp.append("1B 66 00 14 |".replaceAll(" ", ""));
		//========================山鹰LTZ-YBZ3032 并口  不需要字库 代表: A0A3841E6D2D 特别包结束=======================================//

		
		//========================营口赛得福JB-QB-YS4810 并口   不需要字库 代表: 444283F8703C 特别包 开始=======================================//
		regexp.append("1B 66 00 15 |".replaceAll(" ", ""));
		//regexp.append("1B 66 00 02 |".replaceAll(" ", ""));
		//========================营口赛得福JB-QB-YS4810 并口  不需要字库 代表: 444283F8703C 特别包结束=======================================//
		
		
		//========================国泰怡安GK601（JB-TBZL-GK601） 串口  4800 不需要字库 代表: 4442835D7C3C  特别包 开始=======================================//
		regexp.append("7E 00 07 02 DB 1B 4A 20 67 BC |".replaceAll(" ", ""));
		//========================国泰怡安GK601（JB-TBZL-GK601） 串口  4800 不需要字库 代表: 4442835D7C3C  特别包结束=======================================//
		
	
		//========================西核彩桥JB-QB-CH8000 并口 不需要字库 1EA483862C30  特别包 开始=======================================//
		regexp.append("1C 2E 0A 1B 4A 02 1C 26 |".replace(" ", ""));
		regexp.append("1C 2E 1B 4A 08 1C 26 |".replace(" ", ""));
		//========================西核彩桥JB-QB-CH8000 并口 不需要字库 1EA483862C30  特别包 结束=======================================//
		
		//========================西核彩桥JB-QB-CH8000 并口 不需要字库 1EA483862C30  特别包 开始=======================================//
		regexp.append("7E001502DB1B |".replace(" ", ""));
		regexp.append("7E001802DB1B |".replace(" ", ""));
		regexp.append("7E001D02DB1B |".replace(" ", ""));
		regexp.append("7E001702DB1B |".replace(" ", ""));
		regexp.append("7E000602DB1B |".replace(" ", ""));
		//========================西核彩桥JB-QB-CH8000 并口 不需要字库 1EA483862C30  特别包 结束=======================================//
		
		//========================依爱EI-2000G 并口 不需要字库 1EA483C72830  特别包 开始=======================================//
		regexp.append("1C 26 1C 69 00 1C 26 1C 69 00  |".replace(" ", ""));
		//========================依爱EI-2000G 并口 不需要字库 1EA483C72830  特别包 结束=======================================//
		
		regexp.append("0A 0A 0A 0A 0A 0A 0A 0A ".replaceAll(" ", ""));
		
		Pattern p = Pattern.compile(regexp.toString()); 
		Matcher m = p.matcher(hexStr);		
		String tmp= m.replaceAll("").trim();
		
		boolean bl=true;
		if(tmp.length()==hexStr.length()){
			bl=false;
		}
		return bl;
	}
	
	/**
	 * checkSpecificBytes
	 * 检查是否有指定的特征字符串，有特征字符串的数据将不进行后续处理
	 * @param hexStr
	 * @return
	 */
	public static boolean  checkSpecificBytes(String hexStr,String hasBytes){
		StringBuffer regexp=new StringBuffer();		
		Pattern p = Pattern.compile(hasBytes); 
		Matcher m = p.matcher(hexStr);		
		String tmp= m.replaceAll("").trim();
		
		boolean bl=true;
		if(tmp.length()==hexStr.length()){
			bl=false;
		}
		return bl;
	}
	
	/**
	 * 过滤特定字节
	 * @param src
	 * @return
	 */
	public static byte[] filterSpecificBytes(byte[] src) {
		String tmpStr=HexToStr(src,0,src.length);
		byte[] bty= StrToHex(tmpStr.replaceAll(specificBytes(), ""));
		return bty;
	}
	/**
	 * 过滤特定字节
	 * @param src
	 * @return
	 */
	public static byte[] filterSpecificBytes(byte[] src,String specificBytes) {
		String tmpStr=HexToStr(src,0,src.length);
		byte[] bty= StrToHex(tmpStr.replaceAll(specificBytes, ""));
		return bty;
	}
	/**
	 * 过滤指定字节
	 * @param src
	 * @param specificBytes
	 * @return
	 */
	public static byte[] filterSpecificBytes(byte[] src,int specificBytes) {
		List<Byte> list=new ArrayList<Byte>();
		for(int i=0; i<src.length;i++) {
			if(src[i]!=specificBytes){
				list.add(src[i]);
			}
		}
	    byte[] array = listTobyte(list);	    
		return array;
	}
	
	/**
	 * 过滤指定字节
	 * @param src
	 * @param specificBytes
	 * @return
	 */
	public static byte[] filterSpecificBytes(byte[] src,byte specificBytes) {
		List<Byte> list=new ArrayList<Byte>();
		for(int i=0; i<src.length;i++) {
			if(src[i]!=specificBytes){
				list.add(src[i]);
			}
		}
	    byte[] array = listTobyte(list);	    
		return array;
	}
	
	/**
	 * 过滤指定字节
	 * @param hexStr
	 * @param specificBytes
	 * @return
	 */
	public static String filterSpecificBytes(String hexStr,byte specificBytes) {
		byte[] src=hex2byte(hexStr);
		List<Byte> list=new ArrayList<Byte>();
		for(int i=0; i<src.length;i++) {
			if(src[i]!=specificBytes){
				list.add(src[i]);
			}
		}
	    byte[] array = listTobyte(list);	    
		return byte2hex(array);
	}
	

	
	
	/**
	 * 过滤指定字节
	 * @param src
	 * @param specificBytes
	 * @return
	 */
	public static byte[] filterSpecificBytes(byte[] src,byte[] specificBytes) {
		List<Byte> list=new ArrayList<Byte>();
		int specificBytesLen=specificBytes.length;
		byte[] startByte=new byte[specificBytesLen];
		int len=src.length;
		for(int i=0; i<src.length;i++) {
			if(len-i>=specificBytesLen){
				System.arraycopy(src,i,startByte,0,startByte.length);
				if (Arrays.equals(startByte, specificBytes)) {
					i+=specificBytesLen-1;
				}else{
					list.add(src[i]);
				}
			}else{
				list.add(src[i]);
			}
		}
	    byte[] array = listTobyte(list);	    
		return array;
	}
		
	/**
	 * 替换特定字符，将原来的hexStr中的特定字节替换为某个字节
	 * @param src
	 * @param oldBytes
	 * @param newBytes
	 * @return
	 */
	public static byte[] replaceSpecificBytes(byte[] src,byte oldBytes,byte newBytes) {
		List<Byte> list=new ArrayList<Byte>();
		for(int i=0; i<src.length;i++) {
			if(src[i]==oldBytes){
				list.add(newBytes);
			}else{
				list.add(src[i]);
			}
		}
	    byte[] array = listTobyte(list);	    
		return array;
	}	
	
	/**
	 * 替换特定字符，将原来的hexStr中的特定字节替换为某个字节
	 * @param hexStr
	 * @param oldBytes
	 * @param newBytes
	 * @return
	 */
	public static String replaceSpecificBytes(String hexStr,byte oldBytes,byte newBytes) {
		byte[] src=hex2byte(hexStr);
		List<Byte> list=new ArrayList<Byte>();
		for(int i=0; i<src.length;i++) {
			if(src[i]==oldBytes){
				list.add(newBytes);
			}else{
				list.add(src[i]);
			}
		}
	    byte[] array = listTobyte(list);	    
		return byte2hex(array);
	}	
	
	
	/**
	 * 过滤指定字节
	 * @param srcHexString
	 * @param specificHexString
	 * @return
	 */
	public static String filterSpecificBytes(String srcHexString,String specificHexString) {
		byte[] src=hex2byte(srcHexString);
		byte[] specificBytes=hex2byte(specificHexString);
	    byte[] array = filterSpecificBytes(src,specificBytes);	    
		return byte2hex(array);
	}
	
	
	public static byte[] listTobyte(List<Byte> list) {
		if (list == null || list.size() < 0)
			return null;

		byte[] bytes = new byte[list.size()];
		int i = 0;

		Iterator<Byte> iterator = list.iterator();
		while (iterator.hasNext()) {
			bytes[i] = iterator.next();
			i++;
		}
		return bytes;
	}
	
	public static byte[] listToBytes(List<byte[]> list) {
		if (list == null || list.size() < 0)
			return null;

		List<Byte> ls=new ArrayList<Byte>();
		Iterator<byte[]> iterator = list.iterator();
		while (iterator.hasNext()) {
			byte[] bt= iterator.next();
			for(byte b:bt){
				ls.add(b);
			}
		}
		byte[] bytes = listTobyte(ls);
		return bytes;
	}	
	/**
	 * escape
	 * 转义 
	 * 采用0x7e 表示，若校验码、消息头以及消息体中出现0x7e，则要进行转义处理，转义
		规则定义如下：
		0x7e <————> 0x7d 后紧跟一个0x02；
		0x7d <————> 0x7d 后紧跟一个0x01。
		转义处理过程如下：
		发送消息时：消息封装——>计算并填充校验码——>转义；
		接收消息时：转义还原——>验证校验码——>解析消息。
		示例：
		发送一包内容为0x30 0x7e 0x08 0x7d 0x55 的数据包，则经过封装如下：0x7e 0x30 7d 0x02 0x08 0x7d
		0x01 0x55 0x7e。
	 * @param src
	 * @return
	 */
	public static byte[] escape(byte[] src) {
		List<Byte> list=new ArrayList<Byte>();
		for(int i=0; i<src.length;i++) {
			if (src[i] == 0x0D) {
				list.add(src[i]);
				list.add((byte)0x7E);
			}else{
				list.add(src[i]);
			}
		}		 
	    byte[] array = listTobyte(list);	    
		return array;
	}
	/**
	 * 将一个字节数据中的某一个字节按指定长度重复，比如0A改成0A0A0A
	 * @param src 原来的字节数组
	 * @param target 需要替换的字节
	 * @param targetLen 变替换的字节的重复次数
	 * @return
	 */
	public static byte[] escape(byte[] src,byte target,int targetLen) {
		List<Byte> list=new ArrayList<Byte>();
		for(int i=0; i<src.length;i++) {
			if (src[i] == target) {
				for(int j=0;j<targetLen;j++){
					list.add(src[i]);
				}
			}else{
				list.add(src[i]);
			}
		}
	    byte[] array = listTobyte(list);	    
		return array;
	}

	
	/**
	 * reverseEscape 反转义
	 * @param src
	 * @return
	 */
	public static byte[] reverseEscape(byte[] src) {
		List<Byte> list=new ArrayList<Byte>();
		for(int i=1; i<src.length -1;i++) {
			if ((src[i] == 0x0D)&&(src[i] == 0x01)) {
				list.add(src[i]);
				i++;
			}else{
				list.add(src[i]);
			}
		}		 
	    byte[] array = listTobyte(list);	    
		return array;
	}
	/**
	 * 十六进制字符串转转义
	 * @param src
	 * @return
	 */
	public static String escapeString(String src) {
		byte[] bty=hex2byte(src);
		byte[] b=escape(bty);
		String reStr=byte2hex(b);
		return reStr;
	}
	
	/**
	 * 判断是否有特定字节组
	 * @param src
	 * @param specificBytes
	 * @return
	 */
	public static boolean haveSpecificBytes(byte[] src,byte[] specificBytes) {
		boolean bl= false;
		if(src==null || specificBytes==null){
			return bl;
		}
		int startLen=specificBytes.length;
		byte[] startByte=new byte[startLen];
		int len=src.length;
		
		for(int i=0; i<src.length;i++) {
			if(len-i<startByte.length){
				break;
			}
			System.arraycopy(src,i,startByte,0,startLen);
			if (Arrays.equals(startByte, specificBytes)) {
				bl=true;
				break;
			}
		}
		
		return bl;
	}
	
	/**
	 * 判断是否有特定字节组
	 * @param src
	 * @param specificBytes
	 * @return
	 */
	public static boolean haveSpecificBytes(byte[] src,List<byte[]> specificBytes) {		
		boolean bl= false;
		for(byte[] bty:specificBytes){
			bl=bl||haveSpecificBytes(src,bty);
		}
		return bl;
	}
	/**
	 * 判断是否有特定字节组
	 * @param src
	 * @param specificBytes
	 * @return
	 */
	public static boolean haveSpecificByteStr(byte[] src,List<String> specificBytes) {		
		boolean bl= false;
		for(String bty:specificBytes){
			bl=bl||haveSpecificBytes(src,hex2byte(bty));
		}
		return bl;
	}	
	
	/**
	 * 指定字节组在字节组中的位置并抛弃前面无用的字节(用于取完整帧数据)
	 * @param src 待检测的字符串
	 * @param start  需要查找的字符串
	 * @return
	 */
	public static byte[] findStartWidth(byte[] src,byte[] start) {
		int startLen=start.length;
		int len=src.length;
		byte[] startByte=new byte[startLen];
		int i=0; 
		for(i=0; i<src.length;i++) {
			if(len-i<startByte.length){
				break;
			}			
			System.arraycopy(src,i,startByte,0,startLen);
			if (Arrays.equals(startByte, start)) {
				break;
			}else{
				continue;
			}
		}
		byte[] reByte=new byte[len-i];
		System.arraycopy(src,i,reByte,0,len-i);
		return reByte;
	}
	
	/**
	 * 取得从指定位置开始的剩余字节
	 * @param src
	 * @param startIndex
	 * @return
	 */
	public static byte[] getOverplusBytes(byte[] src,int startIndex) {
		int len=src.length;
		byte[] overplusBytes=new byte[len-startIndex];
		System.arraycopy(src,startIndex,overplusBytes,0,overplusBytes.length);
		return overplusBytes;
	}
	/**
	 * 取得索引位置开始的指定长度的字节
	 * @param src
	 * @param startIndex
	 * @param specifiedLength
	 * @return
	 */
	public static byte[] getSpecifiedLengthBytes(byte[] src,int startIndex,int specifiedLength) {
		byte[] bty=new byte[specifiedLength];
		System.arraycopy(src,startIndex,bty,0,bty.length);
		return bty;
	}
	/**
	 * 合并两个字节数组
	 * @param head
	 * @param end
	 * @return
	 */
	public static byte[] mergeTwoSyteArray(byte[] head,byte[] end) {
		byte[] bty=new byte[head.length+end.length];
		System.arraycopy(head,0,bty,0,head.length);
		System.arraycopy(end,0,bty,head.length,end.length);
		return bty;
	}
	
	/**
	 * 找出特征字节在指定字节数组中的索引位置
	 * @param src
	 * @param start
	 * @return
	 */
	public static int findByteIndex(byte[] src,byte[] start) {
		int startLen=start.length;
		byte[] startByte=new byte[startLen];
		int i=0;
		int len=src.length;
		for(i=0; i<src.length;i++) {
			if(len-i<startByte.length){
				break;
			}			
			System.arraycopy(src,i,startByte,0,startLen);
			if (Arrays.equals(startByte, start)) {
				break;
			}else{
				continue;
			}
		}
		return i;
	}

	/**
	 * 截取需要的字节(即取指定位置开始到特定字节结束的字节组)
	 * @param src
	 * @param startIndex
	 * @param specificBytes
	 * @return
	 */
	public static byte[] getBytesBySpecificBytesEnd(byte[] src,int startIndex,byte[] specificBytes) {
		List<Byte> list=new ArrayList<Byte>();
		int specificBytesLen=specificBytes.length;
		byte[] startByte=new byte[specificBytesLen];		
		int len=src.length;
		for(int i=startIndex; i<len;i++) {
			if(len-i>=specificBytesLen){
				System.arraycopy(src,i,startByte,0,startByte.length);
				if (Arrays.equals(startByte, specificBytes)) {
					break;
				}else{
					list.add(src[i]);
				}
			}else{
				list.add(src[i]);
			}
		}
	    byte[] array = listTobyte(list);	    
		return array;
	}
	/**
	 * 将指定字节组变成另一个字节组
	 * @param src
	 * @param specificBytes
	 * @return
	 */
	public static byte[] replaceSpecificBytes(byte[] src,byte[] specificBytes,byte[] targetByte) {
		List<Byte> list=new ArrayList<Byte>();
		int specificBytesLen=specificBytes.length;
		byte[] startByte=new byte[specificBytesLen];
		int len=src.length;
		for(int i=0; i<src.length;i++) {
			if(len-i>=specificBytesLen){
				System.arraycopy(src,i,startByte,0,startByte.length);
				if (Arrays.equals(startByte, specificBytes)) {
					i+=specificBytesLen-1;
					for(byte byt:targetByte){
						list.add(byt);
					}
				}else{
					list.add(src[i]);
				}
			}else{
				list.add(src[i]);
			}
		}
	    byte[] array = listTobyte(list);	    
		return array;
	}	
	/**
	 * 指定字节串在字节串中的位置并抛弃前面无用的字节(返回的数据是去掉了指定的头和尾中间的实际数据的)
	 * @param src 待检测的字符串
	 * @param head  需要查找的头字符串
	 * @param end  需要查找的尾字符串
	 * @return
	 */
	public static List<byte[]> splitPackage(byte[] src,byte[] head,byte[] end) {
		List<byte[]> reList=new ArrayList<byte[]>();
		byte[] startByte=new byte[head.length];
		byte[] endByte=new byte[end.length];
		int len=src.length;
		int startIndex=-1;
		int endIndex=-1;
		for(int i=0; i<src.length;i++) {
			if(len-i<startByte.length && startIndex==-1){
				break;
			}else{
				if(startIndex==-1){
					//判断头
					System.arraycopy(src,i,startByte,0,startByte.length);
					if (Arrays.equals(startByte, head)) {
						startIndex=i;
					}
				}
			}

			if(len-i<endByte.length){
				break;
			}
			//判断尾
			System.arraycopy(src,i,endByte,0,endByte.length);
			if (Arrays.equals(endByte, end)) {
				endIndex=i;
			}
			if (startIndex>-1 && endIndex>-1 && endIndex-startIndex>0){
				byte[] reByte=new byte[endIndex-startIndex];
				System.arraycopy(src,startIndex,reByte,0,reByte.length);
				reList.add(filterSpecificBytes(filterSpecificBytes(reByte,head),0x00));
				startIndex=-1;
				endIndex=-1;
			}
		}
		return reList;
	}
	/**
	 * 指定字节串在字节串中的位置并抛弃前面无用的字节(返回的数据是去掉了指定的头和尾中间的实际数据的)
	 * @param src 待检测的字符串
	 * @param head  需要查找的字符串头
	 * @param end  需要查找的字符串尾
	 * @return
	 */
	public static List<byte[]> splitPackageNoFliterZero(byte[] src,byte[] head,byte[] end) {
		List<byte[]> reList=new ArrayList<byte[]>();
		byte[] startByte=new byte[head.length];
		byte[] endByte=new byte[end.length];
		int len=src.length;
		int startIndex=-1;
		int endIndex=-1;
		for(int i=0; i<src.length;i++) {
			if(len-i<startByte.length && startIndex==-1){
				break;
			}else{
				if(startIndex==-1){
					//判断头
					System.arraycopy(src,i,startByte,0,startByte.length);
					if (Arrays.equals(startByte, head)) {
						startIndex=i;
					}
				}
			}

			if(len-i<endByte.length){
				break;
			}
			//判断尾
			System.arraycopy(src,i,endByte,0,endByte.length);
			if (Arrays.equals(endByte, end)) {
				endIndex=i;
			}
			if (startIndex>-1 && endIndex>-1){
				byte[] reByte=new byte[endIndex-startIndex];
				System.arraycopy(src,startIndex,reByte,0,reByte.length);
				reList.add(filterSpecificBytes(reByte,head));
				startIndex=-1;
				endIndex=-1;
			}
		}
		return reList;
	}

	/**
	 * 按特定字节对字节数组进行分割(返回的数据中不含特定字节组(即用来做分隔的那些字节)，但包含特定字节前的所有字节，即特定字节在做为尾标识的情况)
	 * @param src 待检测的字符串
	 * @param splitBytes  用户分隔数据的字节组
	 * @return
	 */
	public static List<byte[]> splitPackageByBytes(byte[] src,byte[] splitBytes) {
		int splitLen=splitBytes.length;
		int len=src.length;
		byte[] endBytes=new byte[splitLen];
		List<byte[]> reList=new ArrayList<byte[]>();
		int i=0,startIndex=0,endIndex=0; 
		for(i=0; i<src.length;i++) {
			if(len-i<endBytes.length){
				break;
			}			
			System.arraycopy(src,i,endBytes,0,splitLen);
			if (Arrays.equals(endBytes, splitBytes)) {
				byte[] reByte=new byte[i-startIndex];
				System.arraycopy(src,startIndex,reByte,0,reByte.length);
				reList.add(reByte);
				startIndex=i+splitLen;
			}else{
				continue;
			}
		}
		endIndex=len;
		if((startIndex>-1)&&(endIndex>-1)&&((endIndex-startIndex)>0)){
			byte[] reByte=new byte[endIndex-startIndex];
			System.arraycopy(src,startIndex,reByte,0,reByte.length);
			reList.add(reByte);
			startIndex=endIndex;
			endIndex=-1;
		}
		return reList;
	}
	
	/**
	 * 按特定字节对字节数组进行分割(返回的数据中包含特定字节组，但不包含特定字节前的所有字节，即特定字节在做为头标识的情况)
	 * @param src 待检测的字符串
	 * @param splitBytes  用户分隔数据的字节组
	 * @return
	 */
	public static List<byte[]> splitPackageByStartBytes(byte[] src,byte[] splitBytes) {
		int splitLen=splitBytes.length;
		int len=src.length;
		byte[] endBytes=new byte[splitLen];
		List<byte[]> reList=new ArrayList<byte[]>();
		int i=0,startIndex=-1,endIndex=-1;
		for(i=0; i<len;i++) {
			if(len-i<endBytes.length){
				break;
			}
			System.arraycopy(src,i,endBytes,0,splitLen);
			if (Arrays.equals(endBytes, splitBytes)) {
				if(startIndex==-1){
					startIndex=i;
				}else{
					if(endIndex==-1){
						endIndex=i;
					}
				}
				if((startIndex>-1)&&(endIndex>-1)){
					byte[] reByte=new byte[endIndex-startIndex];
					System.arraycopy(src,startIndex,reByte,0,reByte.length);
					reList.add(reByte);
					startIndex=endIndex;
					endIndex=-1;
				}
			}else{
				continue;
			}
		}
		endIndex=len;
		if((startIndex>-1)&&(endIndex>-1)&&((endIndex-startIndex)>0)){
			byte[] reByte=new byte[endIndex-startIndex];
			System.arraycopy(src,startIndex,reByte,0,reByte.length);
			reList.add(reByte);
			startIndex=endIndex;
			endIndex=-1;
		}
		return reList;
	}
	/**
	 * 按特定字节对字节数组进行分割(返回的数据中包含特定字节组，但不包含特定字节前的所有字节，即特定字节在做为尾标识的情况)
	 * @param src 待检测的字符串
	 * @param splitBytes  用户分隔数据的字节组
	 * @return
	 */
	public static List<byte[]> splitPackageByEndBytes(byte[] src,byte[] splitBytes) {
		int splitLen=splitBytes.length;
		int len=src.length;
		byte[] endBytes=new byte[splitLen];
		List<byte[]> reList=new ArrayList<byte[]>();
		int i=0,startIndex=0;
		byte[] reByte=null;
		for(i=0;len-i>=endBytes.length;i++) {
			System.arraycopy(src,i,endBytes,0,splitLen);
			if (Arrays.equals(endBytes, splitBytes)) {
				reByte=new byte[i-startIndex];
				System.arraycopy(src,startIndex,reByte,0,reByte.length);
				reList.add(reByte);
				startIndex=i+splitLen;
			}else{
				continue;
			}
		}
		if((len-startIndex)>0){
			reByte=new byte[len-startIndex];
			System.arraycopy(src,startIndex,reByte,0,reByte.length);
			reList.add(reByte);
		}
		return reList;
	}
	
	/**
	 * 按特定字节对字节数组进行分割(返回的数据中包含特定字节组，但不包含特定字节前的所有字节，即特定字节在做为开始标识的情况)
	 * 此分隔 函数比较特别的是以字节数组的倒序方式处理
	 * @param src 待检测的字符串
	 * @param splitBytes  用户分隔数据的字节组
	 * @return
	 */
	public static List<byte[]> splitPackageByBytesDesc(byte[] src,byte[] splitBytes) {
		int splitLen=splitBytes.length;
		int len=src.length;
		byte[] endBytes=new byte[splitLen];
		
		List<byte[]> reList=new ArrayList<byte[]>();
		
		int i=0,start=0,end=0;
		
		byte[] reByte=null;
		
		for(i=len;i>splitLen;i--) {
			System.arraycopy(src,i-splitLen,endBytes,0,splitLen);
			if (Arrays.equals(endBytes, splitBytes)) {
				if(end==0){
					end=i;
					reByte=new byte[len-end];
					System.arraycopy(src,i,reByte,0,reByte.length);
					reList.add(reByte);
				}else{
					start=i;
					reByte=new byte[end-start];
					System.arraycopy(src,i,reByte,0,reByte.length);
					reList.add(reByte);
					end=i;
				}
			}else{
				continue;
			}
		}
		if((end-0)>0){
			reByte=new byte[end-0];
			System.arraycopy(src,0,reByte,0,reByte.length);
			reList.add(reByte);
		}
		return reList;
	}
	
	/**
	 * 将字节组按正则表达式分隔处理
	 * @param src 待分折处理的字节组
	 * @param splitByte  分隔标识正则表达式，形如：1B 40 |20 20 20 |20 20 |1C 2E 1B 69 00 1B 69 
	 * @return
	 */
	public static List<byte[]> splitPackage(byte[] src,String splitByte) {
		List<byte[]> reList=new ArrayList<byte[]>();
		String srcStr=byte2hex(src);
		String temp[] = srcStr.split(splitByte.replace(" ", "").replace("0x", ""));
        for(String word : temp)
        {
        	reList.add(hex2byte(word));
        }
		return reList;
	}
	
	/**
	 * 按指定长度分隔将字节进行分组处理(如果不够指定长度则抛弃)
	 * @param src 待分折处理的字节组
	 * @param splitLen  要分的字节长度
	 * @return
	 */
	public static List<byte[]> splitPackage(byte[] src,int splitLen) {
		List<byte[]> reList=new ArrayList<byte[]>();
		int len=src.length;
		byte[] startByte=null;
		for(int i=0;len-i>splitLen;i+=splitLen){
			startByte=new byte[splitLen];
			System.arraycopy(src,i,startByte,0,startByte.length);
			reList.add(startByte);
		}
		return reList;
	}
	
	/**
	 * 按指定长度分隔将字节进行分组处理(如果不够指定长度则抛弃)
	 * @param src 待分折处理的字节组
	 * @param splitLen  要分的字节长度
	 * @return
	 */
	public static List<byte[]> splitPackage(byte[] src,int startIndex,int splitLen) {
		List<byte[]> reList=new ArrayList<byte[]>();
		int len=src.length;
		byte[] startByte=null;
		for(int i=startIndex;i<len-splitLen;i+=splitLen){
			startByte=new byte[splitLen];
			System.arraycopy(src,i,startByte,0,startByte.length);
			reList.add(startByte);
		}
		return reList;
	}
	
	public static byte[] getRealBtyes(byte[] src,int startIndex,int endIdex) {
		byte[] realBytes=new byte[endIdex-startIndex];
		System.arraycopy(src,startIndex,realBytes,0,realBytes.length);
		return realBytes;		
	}
	
	/**
	 * 从字节数组取得真正需要的字节
	 * @param src
	 * @param startIndex
	 * @param realLen
	 * @return
	 */
	public static byte[] getRealBtye(byte[] src,int startIndex,int realLen) {
		byte[] realBytes=new byte[realLen];
		System.arraycopy(src,startIndex,realBytes,0,realBytes.length);
		return realBytes;		
	}

	
	/**
	 * 返回的数据是去掉了指定的头和尾中间的实际数据(用于从字节数组中取想要的数据)
	 * @param src 待检测的字符串
	 * @param head  需要查找的字符串
	 * @param end  需要查找的字符串
	 * @return
	 */
	public static byte[] getRealDataBtyes(byte[] src,byte[] head,byte[] end) {
		byte[] reByte=null;
		byte[] startByte=new byte[head.length];
		byte[] endByte=new byte[end.length];
		int len=src.length;
		int startIndex=-1;
		int endIndex=-1;
		for(int i=0; i<src.length;i++) {
			if(len-i<startByte.length){
				if(startIndex<0){
					break;
				}
			}else{
				//判断头
				System.arraycopy(src,i,startByte,0,startByte.length);
				if (Arrays.equals(startByte, head)) {
					startIndex=i;
				}
			}

			if(len-i<endByte.length){
				break;
			}
			//判断尾
			System.arraycopy(src,i,endByte,0,endByte.length);
			if (Arrays.equals(endByte, end)) {
				endIndex=i;
			}
			if (startIndex>-1 && endIndex>-1 && (endIndex-startIndex)>0){
				reByte=new byte[endIndex-startIndex];
				System.arraycopy(src,startIndex,reByte,0,reByte.length);
				reByte=filterSpecificBytes(filterSpecificBytes(reByte,head),0x00);
				startIndex=-1;
				endIndex=-1;
			}else{
				//System.out.println("a");
			}
		}
		return reByte;
	}
	/**
	 * 返回的数据是去掉了指定的头和尾中间的实际数据(用于从字节数组中取想要的数据)
	 * @param src 待检测的字符串
	 * @param head  需要查找的字符串头
	 * @param end  需要查找的字符串尾
	 * @return
	 */
	public static byte[] getRealDataBtyesNoFilter(byte[] src,byte[] head,byte[] end) {
		byte[] reByte=null;
		byte[] startByte=new byte[head.length];
		byte[] endByte=new byte[end.length];
		int len=src.length;
		int startIndex=-1;
		int endIndex=-1;
		for(int i=0; i<src.length;i++) {
			if(len-i<startByte.length){
				if(startIndex<0){
					break;
				}
			}else{				
				//判断头
				System.arraycopy(src,i,startByte,0,startByte.length);
				if (Arrays.equals(startByte, head)) {
					startIndex=i;
				}
			}

			if(len-i<endByte.length){
				break;
			}
			//判断尾
			System.arraycopy(src,i,endByte,0,endByte.length);
			if (Arrays.equals(endByte, end)) {
				endIndex=i;
			}
			if (startIndex>-1 && endIndex>-1){
				reByte=new byte[endIndex-startIndex-head.length];
				System.arraycopy(src,startIndex+head.length,reByte,0,reByte.length);
				//reByte=filterSpecificBytes(reByte,head);
				startIndex=-1;
				endIndex=-1;
			}
		}
		return reByte;
	}

	/**
	 * 指定字节串在字节串中的位置并抛弃前面无用的字节
	 * @param src 待检测的字符串
	 * @param head  需要查找的字符串
	 * @return
	 */
	public static List<byte[]> splitPackage(byte[] src,byte[] head) {
		List<byte[]> reList=new ArrayList<byte[]>();
		byte[] startByte=new byte[head.length];
		byte[] endByte=new byte[head.length];
		int len=src.length;
		int startIndex=-1;
		int endIndex=-1;
		for(int i=0; i<len;i++) {
			//判断头
			if(startIndex==-1){
				if(len-i<startByte.length){
					break;
				}
				System.arraycopy(src,i,startByte,0,startByte.length);
				if (Arrays.equals(startByte, head)) {
					startIndex=i;
				}
			}

			//判断尾
			if(len-i-1>=endByte.length && i>=startIndex+1){
				System.arraycopy(src,i+1,endByte,0,endByte.length);
				if (Arrays.equals(endByte, head)) {
					endIndex=i+1;
				}
			}
			
			if(i==(len-1)){
				endIndex=len;
			}
			
			if (startIndex>-1 && endIndex>-1){
				if(startIndex!=endIndex){
					byte[] reByte=new byte[endIndex-startIndex];
					System.arraycopy(src,startIndex,reByte,0,reByte.length);
					reList.add(filterSpecificBytes(filterSpecificBytes(reByte,head),0x00));
					startIndex=-1;
					endIndex=-1;
				}
			}
		}
		return reList;
	}
	
	/**
	 * 指定字节串在字节串中的位置并抛弃前面无用的字节
	 * @param src 待检测的字符串
	 * @param start  需要查找的字符串
	 * @return
	 */
	public static List<byte[]> splitPackageByStart(byte[] src,byte[] start) {
		List<byte[]> reList=new ArrayList<byte[]>();
		byte[] startByte=new byte[start.length];
		byte[] endByte=new byte[start.length];
		int len=src.length;
		int startIndex=-1;
		int endIndex=-1;
		for(int i=0; i<len;i++) {
			//判断头
			if(startIndex==-1){
				if(len-i<startByte.length){
					break;
				}
				System.arraycopy(src,i,startByte,0,startByte.length);
				if (Arrays.equals(startByte, start)) {
					startIndex=i;
				}
			}

			//判断尾
			if(len-i-1>=endByte.length && i>=startIndex+1){
				System.arraycopy(src,i+1,endByte,0,endByte.length);
				if (Arrays.equals(endByte, start)) {
					endIndex=i+1;
				}
			}
			
			if(i==(len-1)){
				endIndex=len;
			}
			
			if (startIndex>-1 && endIndex>-1){
				if(startIndex!=endIndex){
					byte[] reByte=new byte[endIndex-startIndex];
					System.arraycopy(src,startIndex,reByte,0,reByte.length);
					reList.add(filterSpecificBytes(reByte,start));
					startIndex=-1;
					endIndex=-1;
				}
			}
		}
		return reList;
	}
	
	/**
	 * 指定字节串在字节串中的位置并抛弃前面无用的字节
	 * @param src 待检测的字符串
	 * @param end  需要查找的字符串
	 * @return
	 */
	public static List<byte[]> splitPackageByEnd(byte[] src,byte[] end) {
		List<byte[]> reList=new ArrayList<byte[]>();
		byte[] endByte=new byte[end.length];
		int endLen=endByte.length;
		int len=src.length;
		int startIndex=0;
		int endIndex=0;
		byte[] reByte= null;
		//PrintUtil.pritnfDebugInfo("wsq src:"+HexUtil.byte2hex(src));
		for(int i=0; i<len;i++) {
			if(i<len-endLen){
				System.arraycopy(src,i,endByte,0,endLen);
				if (Arrays.equals(endByte, end)) {	
					endIndex++;
					//PrintUtil.pritnfDebugInfo("endIndex-startIndex:"+(endIndex-startIndex));
					if(endIndex-startIndex<0) break;
					reByte=new byte[endIndex-startIndex];
					System.arraycopy(src,startIndex,reByte,0,reByte.length);
					//PrintUtil.pritnfDebugInfo("wsq reByte:"+HexUtil.byte2hex(reByte));
					reList.add(filterSpecificBytes(reByte,end));					
					startIndex=endIndex+endLen;
				}else{
					endIndex=i;
				}
			}
		}
		if(endIndex>0 && endIndex<=len){
			reByte=new byte[len-startIndex];
			System.arraycopy(src,startIndex,reByte,0,reByte.length);
			//PrintUtil.pritnfDebugInfo("wsq reByte:"+HexUtil.byte2hex(reByte));
			reList.add(filterSpecificBytes(reByte,end));
		}
		return reList;
	}
	
	/**
	 * 将list转换为字节数组
	 * @param list 要转的的字节list
	 * @return
	 */
	public static byte[] getListBytes(List<byte[]> list) {
		List<Byte> ls=new ArrayList<Byte>();
		for(byte[] bty:list){
			for(byte e:bty){
				ls.add(e);
			}
		}
	    byte[] array = listTobyte(ls);	    
		return array;
	}
	
	/**
	 * 将list以倒序的方式转换为字节数组
	 * @param list 要转的的字节list
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static byte[] getListDescBytes(List<byte[]> list) throws UnsupportedEncodingException {
		List<Byte> ls=new ArrayList<Byte>();
		int index=list.size()-1;
		for(int i=index;i>-1;i--){
			byte[] bty1=list.get(i);
			String oInfo = new String(bty1, 0, bty1.length, "GBK");
			for(byte e:list.get(i)){
				ls.add(e);
			}
		}
	    byte[] array = listTobyte(ls);	    
		return array;
	}
	
	/**
	 * 取指定字节值
	 * @param data
	 * @param offset
	 * @return
	 */
	public static int ByteToInt(byte[] data, int offset) {// big
		return (int)data[offset];
	}
	
	/**
	 * 大端模式两字节转型（高字节在前,EA00=59904）
	 * @param data
	 * @param offset
	 * @return
	 */
	public static int Byte2ToInt(byte[] data, int offset) {// big
		return (int) (((data[offset] & 0xff) << 8) + (data[offset + 1] & 0xff));
	}
	/**
	 * 小端模式两字节转整（低字节在前,EA00=234）
	 * @param data
	 * @param offset
	 * @return
	 */
	public static int Byte2SToInt(byte[] data, int offset) {// small
		return (int) (((data[offset + 1] & 0xff) << 8) + (data[offset] & 0xff));
	}
	/**
	 * 大端模式3字节转整
	 * @param data
	 * @param offset
	 * @return
	 */
	public static int Byte3ToInt(byte[] data, int offset) {// big
		return (int) (((data[offset] & 0xff) << 16) + ((data[offset + 1] & 0xff) << 8) + (data[offset + 2] & 0xff));
	}
	/**
	 * 大端模式4字节转整
	 * @param data
	 * @param offset
	 * @return
	 */
	public static int Byte4ToInt(byte[] data, int offset) {// big
		return (int) (((data[offset] & 0xff) << 24) + ((data[offset + 1] & 0xff) << 16)
				+ ((data[offset + 2] & 0xff) << 8) + (data[offset + 3] & 0xff));
	}
	/**
	 * 整型数据转两字节
	 * @param data
	 * @param offset
	 * @param val
	 */
	public static void IntToByte2(byte[] data, int offset, int val) {
		data[offset] = (byte) ((val >> 8) & 0xff);
		data[offset + 1] = (byte) (val & 0xff);
	}
	/**
	 * 整型数据转4字节
	 * @param data
	 * @param offset
	 * @param val
	 */
	public static void IntToByte4(byte[] data, int offset, int val) {
		data[offset] = (byte) ((val >> 24) & 0xff);
		data[offset + 1] = (byte) ((val >> 16) & 0xff);
		data[offset + 2] = (byte) ((val >> 8) & 0xff);
		data[offset + 3] = (byte) (val & 0xff);
	}
	//byte 与 int 的相互转换  
	public static byte intToByte(int x) {  
	    return (byte) x;  
	}  
	  
	public static int byteToInt(byte b) {  
	    //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值  
	    return b & 0xFF;  
	}  
	
	//byte 数组与 int 的相互转换  
	public static int byteArrayToInt(byte[] b) {  
	    return   b[3] & 0xFF |  
	            (b[2] & 0xFF) << 8 |  
	            (b[1] & 0xFF) << 16 |  
	            (b[0] & 0xFF) << 24;  
	}  
	  
	public static byte[] intToByteArray(int a) {  
	    return new byte[] {  
	        (byte) ((a >> 24) & 0xFF),  
	        (byte) ((a >> 16) & 0xFF),     
	        (byte) ((a >> 8) & 0xFF),     
	        (byte) (a & 0xFF)  
	    };  
	}  
	
	private static ByteBuffer buffer = ByteBuffer.allocate(8);

	// byte 数组与 long 的相互转换
	public static byte[] longToBytes(long x) {
		buffer.putLong(0, x);
		return buffer.array();
	}

	public static long bytesToLong(byte[] bytes) {
		buffer.put(bytes, 0, bytes.length);
		buffer.flip();// need flip
		return buffer.getLong();
	}

	/**
	 * 生成异或检验码
	 * @param data
	 * @param offset
	 * @param len
	 * @return
	 */
	public static byte GetXor(byte[] data, int offset, int len) {
		int i;
		byte code = 0;
		code = (byte) (data[3 + offset] ^ data[4 + offset]);
		for (i = 5; i < len; i++) {
			code ^= data[i + offset];
		}
		return code;
	}

	public static int getFrameCRC16(byte[] pdata, int offset, int len) {
		int[] g_McRctable_16 = new int[]// MODBUS CRC-16表 8005 逆序
		{ 0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241, 0xC601, 0x06C0, 0x0780, 0xC741, 0x0500,
				0xC5C1, 0xC481, 0x0440, 0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40, 0x0A00, 0xCAC1,
				0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841, 0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81,
				0x1A40, 0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41, 0x1400, 0xD4C1, 0xD581, 0x1540,
				0xD701, 0x17C0, 0x1680, 0xD641, 0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040, 0xF001,
				0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240, 0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0,
				0x3480, 0xF441, 0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41, 0xFA01, 0x3AC0, 0x3B80,
				0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840, 0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
				0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40, 0xE401, 0x24C0, 0x2580, 0xE541, 0x2700,
				0xE7C1, 0xE681, 0x2640, 0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041, 0xA001, 0x60C0,
				0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240, 0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480,
				0xA441, 0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41, 0xAA01, 0x6AC0, 0x6B80, 0xAB41,
				0x6900, 0xA9C1, 0xA881, 0x6840, 0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41, 0xBE01,
				0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40, 0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1,
				0xB681, 0x7640, 0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041, 0x5000, 0x90C1, 0x9181,
				0x5140, 0x9301, 0x53C0, 0x5280, 0x9241, 0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
				0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40, 0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901,
				0x59C0, 0x5880, 0x9841, 0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40, 0x4E00, 0x8EC1,
				0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41, 0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680,
				0x8641, 0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040 };

		return GetRevCrc_16(pdata, offset, len, 0xFFFF, g_McRctable_16);
	}

	public static int GetRevCrc_16(byte[] pData, int offset, int nLength, int init, int[] ptable) {
		int cRc_16 = init;
		byte temp;
		int i = 0;
		while (nLength-- > 0) {
			temp = (byte) (cRc_16 & 0xFF);
			cRc_16 = ((int) ((cRc_16 >> 8) ^ ptable[(temp ^ pData[i + offset]) & 0xFF])) & 0xffff;
			i++;
		}
		return cRc_16 & 0xFFFF;
	}
	
	public static String removeDuplicateChar(String s) {
        StringBuffer sb=new StringBuffer();
        int len=s.length();
        int i=0;
        boolean flag=false;
        for(i=0; i<len;i++){
            char c=s.charAt(i);
            if(s.indexOf(c)!=s.lastIndexOf(c)){
                flag=false;
            }else{
                flag=true;
            }
            if(i==s.indexOf(c))
                flag=true;
            if(flag){
                sb.append(c);
            }
        }
        return sb.toString();
    }
	/**
	 * 去除字节数组中的重复字节
	 * @param bytes
	 * @return
	 */
	public static byte[] removeDuplicateByte(byte[] bytes) {
        ArrayList<Byte> blist = new ArrayList<Byte>();
        for (byte val : bytes) {
        	if(!blist.contains(val)){
        		blist.add(val);
        	}
        }
        return listTobyte(blist);
    }
	/**
	 * 去除hex串中的重复字节
	 * @param hex
	 * @return
	 */
	public static String removeDuplicateByte(String hex) {
		byte[] bty=HexUtil.hex2byte(hex);
        ArrayList<Byte> blist = new ArrayList<Byte>();
        for (byte val : bty) {
        	if(!blist.contains(val)){
        		blist.add(val);
        	}
        }        
        return HexUtil.byte2hex(listTobyte(blist));
    }
	/**
     * 
     * 获取字符串字节流中有效字节个数
     * 
     * @param buf
     * 
     * @return
     */
 
    public static int getEffectiveByteLength(byte[] buf){
        byte[] b=removeDuplicateByte(buf);
        return b.length;
    }	
	/**
     * 
     * 获取字符串字节流中有效字节个数
     * 
     * @param buf
     * 
     * @return
     */
 
    public static int getVirtualValueLength(byte[] buf){ 
        int i = 0; 
        for (; i < buf.length; i++){ 
            if (buf[i] == (byte) 0){ 
                break; 
            } 
        } 
        return i;
    }
    //去掉字符串中的重复字符函数
    public static String removeRepeatedChar(String s) {
        if (s == null)
            return s;
        StringBuilder sb = new StringBuilder();
        int i = 0, len = s.length();
        while (i < len) {
            char c = s.charAt(i);
            sb.append(c);
            i++;
            while (i < len && s.charAt(i) == c) {
                i++;
            }
        }
        return sb.toString();
    }
	

	//十进制
	public static boolean isOctNumber(String str) {
		boolean flag = false;
		for(int i=0,n=str.length();i<n;i++){
			char c = str.charAt(i);
			if(c=='0'|c=='1'|c=='2'|c=='3'|c=='4'|c=='5'|c=='6'|c=='7'|c=='8'|c=='9'){
				flag =true;
			}
		}
		return flag;
	}
	//十六进制
	public static boolean isHexNumber(String str){
		boolean flag = false;
		for(int i=0;i<str.length();i++){
			char cc = str.charAt(i);
			if(cc=='0'||cc=='1'||cc=='2'||cc=='3'||cc=='4'||cc=='5'||cc=='6'||cc=='7'||cc=='8'||cc=='9'||cc=='A'||cc=='B'||cc=='C'||
					cc=='D'||cc=='E'||cc=='F'||cc=='a'||cc=='b'||cc=='c'||cc=='c'||cc=='d'||cc=='e'||cc=='f'){
				flag = true;
			}else{
				break;
			}
		}
		return flag;
	}
	
	public static boolean isOctNumberRex(String str){
		String validate = "\\d+";
		return str.matches(validate);
	}
	/**
	 * 判断是否是十六进制字符串
	 * @param str 待检测字符串
	 * @return 如果是则返回:true,如果不是则返回:false
	 */
	public static boolean isHexNumberRex(String str){
		String validate = "(?i)[0-9a-f]+";
		return str.matches(validate)&&(str.length()%2==0);
	}
    
	/**
	     * 转换字节数组为十六进制字符串
	     * 
	     * @param 字节数组
	     * @return 十六进制字符串
	     */
	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}
	 
	      // 十六进制下数字到字符的映射数组
	public final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
				"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	 
		/** 将一个字节转化成十六进制形式的字符串 */
	public static String byteToHexString(byte b) {
			int n = b;
			if (n < 0)
				n = 256 + n;
			int d1 = n / 16;
			int d2 = n % 16;
			return hexDigits[d1] + hexDigits[d2];
	}
	
	/** 
	* 通信格式转换 
	* 
	* Java和一些windows编程语言如c、c++、delphi所写的网络程序进行通讯时，需要进行相应的转换 
	* 高、低字节之间的转换 
	* windows的字节序为低字节开头 
	* linux,unix的字节序为高字节开头 
	* java则无论平台变化，都是高字节开头 
	  */   
	  
	/** 
	  * 将int转为低字节在前，高字节在后的byte数组 
	  * @param n int 
	  * @return byte[] 
	  */  
	public static byte[] toLH(int n) {  
	  byte[] b = new byte[4];  
	  b[0] = (byte) (n & 0xff);  
	  b[1] = (byte) (n >> 8 & 0xff);  
	  b[2] = (byte) (n >> 16 & 0xff);  
	  b[3] = (byte) (n >> 24 & 0xff);  
	  return b;  
	}   
	  
	/** 
	  * 将int转为高字节在前，低字节在后的byte数组 
	  * @param n int 
	  * @return byte[] 
	  */  
	public static byte[] toHH(int n) {  
	  byte[] b = new byte[4];  
	  b[3] = (byte) (n & 0xff);  
	  b[2] = (byte) (n >> 8 & 0xff);  
	  b[1] = (byte) (n >> 16 & 0xff);  
	  b[0] = (byte) (n >> 24 & 0xff);  
	  return b;  
	}   
	  
	/** 
	  * 将short转为低字节在前，高字节在后的byte数组 
	  * @param n short 
	  * @return byte[] 
	  */  
	public static byte[] toLH(short n) {  
	  byte[] b = new byte[2];  
	  b[0] = (byte) (n & 0xff);  
	  b[1] = (byte) (n >> 8 & 0xff);  
	  return b;  
	}   
	  
	/** 
	  * 将short转为高字节在前，低字节在后的byte数组 
	  * @param n short 
	  * @return byte[] 
	  */  
	public static byte[] toHH(short n) {  
	  byte[] b = new byte[2];  
	  b[1] = (byte) (n & 0xff);  
	  b[0] = (byte) (n >> 8 & 0xff);  
	  return b;  
	}   
	  
	  
	  
	/** 
	  * 将将int转为高字节在前，低字节在后的byte数组  
	 
	public static byte[] toHH(int number) { 
	  int temp = number; 
	  byte[] b = new byte[4]; 
	  for (int i = b.length - 1; i > -1; i--) { 
	    b = new Integer(temp & 0xff).byteValue(); 
	    temp = temp >> 8; 
	  } 
	  return b; 
	}  
	 
	public static byte[] IntToByteArray(int i) { 
	    byte[] abyte0 = new byte[4]; 
	    abyte0[3] = (byte) (0xff & i); 
	    abyte0[2] = (byte) ((0xff00 & i) >> 8); 
	    abyte0[1] = (byte) ((0xff0000 & i) >> 16); 
	    abyte0[0] = (byte) ((0xff000000 & i) >> 24); 
	    return abyte0; 
	}  
	 
	 
	*/   
	  
	/** 
	  * 将float转为低字节在前，高字节在后的byte数组 
	  */  
	public static byte[] toLH(float f) {  
	  return toLH(Float.floatToRawIntBits(f));  
	}   
	  
	/** 
	  * 将float转为高字节在前，低字节在后的byte数组 
	  */  
	public static byte[] toHH(float f) {  
	  return toHH(Float.floatToRawIntBits(f));  
	}   
	  
	/** 
	  * 将String转为byte数组 
	  */  
	public static byte[] stringToBytes(String s, int length) {  
	  while (s.getBytes().length < length) {  
	    s += " ";  
	  }  
	  return s.getBytes();  
	}
	  
	/** 
	  * 将字符串转换为byte数组 
	  * @param s String 
	  * @return byte[] 
	  */  
	public static byte[] stringToBytes(String s) {  
	  return s.getBytes();  
	}
	  
	/** 
	  * 将低字节数组转换为int 
	  * @param b byte[] 
	  * @return int 
	  */  
	public static int lBytesToInt(byte[] b) {  
	  int s = 0;  
	  for (int i = 0; i < 3; i++) {  
	    if (b[3-i] >= 0) {  
	    s = s + b[3-i];  
	    } else {  
	    s = s + 256 + b[3-i];  
	    }  
	    s = s * 256;  
	  }  
	  if (b[0] >= 0) {  
	    s = s + b[0];  
	  } else {  
	    s = s + 256 + b[0];  
	  }  
	  return s;  
	}   
	  
	  
	/** 
	  * 高字节数组到short的转换 
	  * @param b byte[] 
	  * @return short 
	  */  
	public static short hBytesToShort(byte[] b) {  
	  int s = 0;  
	  if (b[0] >= 0) {  
	    s = s + b[0];  
	    } else {  
	    s = s + 256 + b[0];  
	    }  
	    s = s * 256;  
	  if (b[1] >= 0) {  
	    s = s + b[1];  
	  } else {  
	    s = s + 256 + b[1];  
	  }  
	  short result = (short)s;  
	  return result;  
	}   
	  
	/** 
	  * 低字节数组到short的转换 
	  * @param b byte[] 
	  * @return short 
	  */  
	public static short lBytesToShort(byte[] b) {  
	  int s = 0;  
	  if (b[1] >= 0) {  
	    s = s + b[1];  
	    } else {  
	    s = s + 256 + b[1];  
	    }  
	    s = s * 256;  
	  if (b[0] >= 0) {  
	    s = s + b[0];  
	  } else {  
	    s = s + 256 + b[0];  
	  }  
	  short result = (short)s;  
	  return result;  
	}   
	  
	/** 
	  * 高字节数组转换为float 
	  * @param b byte[] 
	  * @return float 
	  */  
	public static float hBytesToFloat(byte[] b) {  
	  int i = 0;  
	  Float F = new Float(0.0);  
	  i = ((((b[0]&0xff)<<8 | (b[1]&0xff))<<8) | (b[2]&0xff))<<8 | (b[3]&0xff);  
	  return F.intBitsToFloat(i);  
	}   
	  
	/** 
	  * 低字节数组转换为float 
	  * @param b byte[] 
	  * @return float 
	  */  
	public static float lBytesToFloat(byte[] b) {  
	  int i = 0;  
	  Float F = new Float(0.0);  
	  i = ((((b[3]&0xff)<<8 | (b[2]&0xff))<<8) | (b[1]&0xff))<<8 | (b[0]&0xff);  
	  return F.intBitsToFloat(i);  
	}   
	  
	/** 
	  * 将byte数组中的元素倒序排列 
	  */  
	public static byte[] bytesReverseOrder(byte[] b) {  
	  int length = b.length;  
	  byte[] result = new byte[length];  
	  for(int i=0; i<length; i++) {  
	    result[length-i-1] = b[i];  
	  }
	  return result;  
	}   
	  
	/** 
	  * 打印byte数组 
	  */  
	public static void printBytes(byte[] bb) {  
	  int length = bb.length;  
	  for (int i=0; i<length; i++) {  
	    System.out.print(bb + " ");  
	  }  
	  System.out.println("");  
	}   
	  
	public static void logBytes(byte[] bb) {  
	  int length = bb.length;  
	  String out = "";  
	  for (int i=0; i<length; i++) {  
	    out = out + bb + " ";  
	  }
	}   
	/** 
	  * 将short类型的值转换为字节序颠倒过来对应的short值 
	  * @param s short 
	  * @return short 
	  */  
	public static short reverseShort(short s) {  
	  short result = hBytesToShort(toLH(s));  
	  return result;  
	}   
	  
	/** 
	  * 将float类型的值转换为字节序颠倒过来对应的float值 
	  * @param f float 
	  * @return float 
	  */  
	public static float reverseFloat(float f) {  
	  float result = hBytesToFloat(toLH(f));  
	  return result;  
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


	public static void main(String[] arg) {
		
		System.out.println(isHexNumberRex("7F3F3"));
		
		System.out.println(removeDuplicateByte("7F3F38383F7F3F3F3F0000000000000000007F3F7F3F3B3A3F7F7F000000000000007F3F3F3F3F7F7F3F3F3F38383F7F383F3F3F3F3F3F3F383F383F380000007F7F3F383F7F3F3F3F3838383F383F383F383F383F383F38383F38000000000000003F3F7F3F7F3F38000000007F3F3F7F3F38383F3F3F3F3F383F383F3F383F383F38383F000000003F3C3E3F7F3F3F3F3F38383F3F3F3F3F383F383F383F000000003F3F7F7F3F3F38383F00003F3F7F7F3F3F3F383800003F3F3F3F7F3F38383F00003F3E3F3F7F3F3F7F3F3800007F3F3F38383F7F3F3F3F7F7F3F7F3F3F7F383F3F7F3F3F3F3F3838387F3F7F3F3F7F383F383F3C3C7F3F3F3F7F3F387F3A7F3F3F7F3A3F7F3F3F7F3F7F3F3F7F387F3A3C3F3F3F3F3F0000003F3C383C3F3C383F3F3F7F3F7F387F3F3F3F383F3A3F7F383F383F3C3F7F7F3F7F7F3F7F387F3F7F3F3F3F383F38387F3F3F3F7F3F383F387F3F3F3F3F3F7F383F3B3F3F7F3F383F3F7F3F7F3F3F383F383F383F3F3F7F383F00003F3F3F383F7F7F3F7F7F3F3F387F3A3F7F383F383F3B00003F3F383F3F3F7F3F3F3F7F3A3F3B3F383F3F3A3F383F000000003F7F3F3F387F383F387F3F7F3A7F383F387F3F3F7F0000003F3F3F3A383F7F7F3F383F3A3F387F3F7F383F383F3F387F00000000007F3F3F3F3F7F7F3F3F3F3F3F3F383F383F383F3F383F383F000000000000003F3F7F7F3F3F3F3A38383F38383F000000007F3F3F3F3F000000000000000000000000000000000000000000007F7F3F7F383F387F387F387F3F3F7F3800000000000000007F7F38383F7F3F3F7F3F3F3F000000000000000000000000000000000000000000003F3F7F3F7F3A7F3F7F383F383F3F3F7F383F3F3F00000000000000000000000000000000000000007F3F383F7F3F7F7F7F7F3F3F3F3F7F3F3F7F7F3F3F00000000000000007F3F3F383F7F3F3F7F380000000000000000000000000000000000003F3F3F3F7F3F3F3F7F3A7F383F383F3F3F7F383F3C000000000000000000000000000000007F3F3F7F7F3F3F7F387F387F3B3F7F383F3B3F3F3C0000000000000000000000000000000000003B3F3C3F7F3F7F3F38386F3F7F7F3F3F3F3838387F3F7F7F000000000000007F7F7F3F7F3F3F3F383F383F383F7F383F383F3F7F3F3F3F7F3F7F3A3F383F387F7F3F3F383F7F3F3F38383F383F383F387F383F383F3F383F7F3F3F3F7F3F7F3F3F7F383F3B7F3F3A3F383F38007F3F7F3F3F3F3F3F3A3F3F3F7F3B7F3F3F3F3F3F7F383F383F3F00007F3F3F3F3F3F3F3F3F3F3F383F383F7F383F383F000000003F7F3F3F7F3F3A387F3A3F383F383F383F387F3F3F7F3800000000003F3F7F3F3A3F3F7F3F7F3F3F3F3F3F3F3F7F383F387F00000000007F3F3F3F3F3A38383F383F383F383F383F383F3F383F00000000007F3F3F3F7F3F7F3F3F3F3F7F7F383F387F3F3F3F3F3F7F383F383F3F0000000000003F3F3F3F3A3F3F7F3F3F383F383F3A7F387F383F383F3F383F0000000000007F3F3F3F3F3F3A3F7F3F3F3F3F383F383F387F3F7F3F7F383F3F3F7F38000000000000000000000000000000000000007F3F3F7F3F3B3A3F7F3F3F7F387F3F3F3F383F3F3A3F383F383F00000000000000000000000000000000003F3F3C7F7F3F3F3F3F3F3F3F7F3F3F383F383F383F3F3F7F383F0000000000000000000000007F3F3F383F7F7F3F3F7F3F3F3F7F383F383F383F3F383F383F000000000000000000000000000000000000007F3F3F3F3F38383F0000000000000000003F3F7F3F3F7F3F38383A7F3A7F387F387F383F383F3F383F00000000000000000000000000003F3F3F7F3F3F7F3F38387F383F387F387F387F383F3F3F3F000000000000000000007F7F7F3F3800000000000000000000007F3F3F3F3F3A3F387F3F7F3F3F3F7F383F383F7F0000000000000000000000000000000000000000000000000000000000000000000000000000007F7F3F3F3F7F3A3F387F3A3F383F3F3F7F0000000000000000000000000000003F3F3F3F3F7F387F387F3A7F383F383F7F383F3F000000000000000000000000000000003F3F3F3F3F7F3F7F3F3F3F3F3F3F383F383F3F3C380000000000000000007F3C000000000000000000000000000000000000007F3F3F3F7F3B3F3F7F3F3F3F7F383F383F3F3F38000000000000000000000000000000007F3F7F7F3F7F3B3F3A7F3A7F383F3F3F7F387F7F3F000000000000000000000000000000000000003F7F3F3F3F3A3F383F3F7F3F3F3F7F383F383F3F000000007F3F3F383F7F7F3F3F3F7F383F387F383F383F3F3F7F3800007F3F3F3F3F7F387F3F3F3F3F7F3F3F7F383F387F3F00007F3F3F3F3F3F7F3A7F387F3B7F3F3F3F7F383F3A3F3F7F3F3F3F3F3F3F7F3A3F3F3F7F3F7F3F3F3F383F38383C3F7F3F7F3F7F387F3A7F3F7F383F383F7F387F3F387F3F3F3800"));
		
		
		StringBuffer sb=new StringBuffer();
		sb.append("0A1B4B5800E0F0188818F0E0001018080808F8F0003060C08000000000E0F0188818F0E000000078F8800000000000000000000000000808F8F8080800F0F8080808F8F00000000060600000001018080808F8F0000008080818F0E0000D1B4B5800070F1811180F070008181111111F0E000000000103060C00070F1811180F070018181010111F1E00000000000000000000040C1F1F000000070F1911110100000000000C0C00000008181111111F0E000E1F1111111F0F00");
		byte[] start=new byte[]{0x1B,0x4B};
		List<byte[]> fontBytes=HexUtil.splitPackageByStart(hex2byte(sb.toString().replaceAll(" ", "")),start);	
    	for(byte[] fontByte:fontBytes){
    		PrintUtil.pritnfDebugInfo("fontByte:"+HexUtil.byte2hex(fontByte));
    	}
		
/*		System.out.println(hexStringToEncodeString("B8B4CEBB","GBK"));
		
		System.out.println(hexStringToEncodeString("00 B8 B4 CE BB 20 31 36 2D 30 33 2D 32 34 20 31 36 3A 31 31 20 0D 0A".replace(" ", ""),"GBK").replace("%%|\\*+|\\//|\\?+|\\ +|\\+", ""));
		
		StringBuffer sb=new StringBuffer();
		sb.append("30 33 2F 30 36 20 31 32 3A 33 36 20 30 39 2D 30 30 38 20 B9 CA D5 CF 0A D2 BB B2 E3 32 C7 F8 D1 CC B8 D0");
		sb.append(" 0D ");
		sb.append("30 33 2F 30 36 20 31 32 3A 33 36 20 30 39 2D 30 30 38 20 B9 CA D5 CF 0A D2 BB B2 E3 32 C7 F8 D1 CC B8 D0");
		
		String tmpStr= hexStringToEncodeString(sb.toString().replace(" ", ""),"GBK");
		System.out.println(tmpStr);
		
		 String temp[] = tmpStr.split("%%|\\*+|\\//|\\?+|\\ +|\\+");
	        for(String word : temp)
	        {
	            System.out.println(word);
	        }
		
		System.out.println(encode("中文"));
		System.out.println(decode(encode("中文")));*/
		
		
        String s = "02 30 33 2F 30 37 20 30 35 3A 31 35 20 30 39 2D 30 30 38 20 B9 CA D5 CF BB D6 B8 B4 0A D2 BB B2 E3 32 C7 F8 D1 CC B8 D0 0D 02 30 33 2F 30 37 20 30 35 3A 31 35 20 30 39 2D 30 30 38 20 B9 CA D5 CF BB D6 B8 B4 0A D2 BB B2 E3 32 C7 F8 D1 CC B8 D0 0D 02 30 33 2F 30 37 20 30 35 3A 31 35 20 30 39 2D 30 30 38 20 B9 CA D5 CF BB D6 B8 B4 0A D2 BB B2 E3 32 C7 F8 D1 CC B8 D0 0D";
        s="30332F30372030353A31352030392D30303820B9CAD5CFBBD6B8B40AD2BBB2E332C7F8D1CCB8D00D";
        String tmpArr[] = s.split("0D");
/*        System.out.println(tmpArr.length);
        for(String word : tmpArr)
        {
            System.out.println(word.trim());
            String tmpStr1= hexStringToEncodeString(word.trim().replace(" ", ""),"GBK");
            stringSplit(tmpStr1.trim());
        }        
*/
        s="3030322D30353220B9E2B5E7B8D0D1CC20CDA8D1B6B9CAD5CF2031362D30332D33302031393A343120BCD0B2E3C9CCB3A1D1CCB8D00D";
        String tmp= hexStringToEncodeString(s.trim(),"GBK");
        
        //System.out.println("=================");
        
        System.out.println("tmp:"+replaceBlank(tmp));
        
        //StringUtils.dealData(tmp);
        
        System.out.println("=================");
        
/*        String tmp1= hexStringToEncodeString(s.trim().replaceAll("0D", "").replaceAll("0A", ""),"GBK");
        System.out.println("tmp1:"+tmp1);
        
        String tmp2= hexStringToEncodeString(s.trim().replaceAll("0D", ""),"GBK");
        System.out.println("tmp2:"+tmp2);
        
        tmpArr = s.split("0D");
        System.out.println(tmpArr.length);
        for(String word : tmpArr)
        {
            System.out.println(word.trim());
            String tmpStr1= hexStringToEncodeString(word.trim().replace(" ", ""),"GBK");
            stringSplit(tmpStr1.trim());
        } */
        
        
/*        s="C6 F4 2A 2A 2A 2A B6 AF BE AF 00 C1 E5 2A 2A 20 20 CA D6 B1 A8 31 32 39 20 20 30 35 2D 30 36 20 30 38 3A 35 34 00C6 F4 2A 2A 2A 2A B6 AF BE AF C1 E5 2A 2A 20 20 CA D6 B1 A8 31 32 39 20 20 30 35 2D 30 36 20 30 38 3A 35 34 00";
        tmp= hexStringToEncodeString(s.trim().replace(" ", ""),"GBK");        
        System.out.println("tmp:"+tmp);
        try {
        	s=s.trim().replace(" ", "");
        	System.out.println("s.length():"+s.length());
            byte[] b=hex2byte(s);
        	System.out.println("b.length:"+b.length);
			String oInfo = new String(b, 0, b.length, "GBK");
			System.out.println("oInfo:"+oInfo);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
                
        System.out.println("tmp:"+StringUtils.filterInvisibleCharacter(tmp));
        
        
        s="1B 40 1B 63 00 1B 38 04 "+
			"BB F0 BE AF 20 32 30 31 37 2F 30 35 2F 31 33 20 30 32 3A 31 39 3A 31 30 0D A1 A1 30 32 35 35 C7 F8 31 2D 30 36 37 BA C5 B8 D0 D1 CC 0D 20 20 A1 A1 A1 A1 A1 A1 A1 A1 A1 A1 A1 A1 A1 A1 A1 A1 0D 0D 20 0D"+ 
			"1B 40 1B 63 00 1B 38 04 "+
			"C6 F4 B6 AF 20 32 30 31 37 2F 30 35 2F 31 33 20 30 32 3A 31 39 3A 31 32 0D A1 A1 30 30 30 30 D7 E9 30 2D 30 30 30 BA C5 C9 F9 B9 E2 0D 20 20 A1 A1 A1 A1 A1 A1 A1 A1 A1 A1 A1 A1 A1 A1 A1 A1 0D 0D 20 0D";
        
        System.out.println("s:"+s.replaceAll("1B 40 1B 63 00 1B 38 04 ", ""));
        
        byte[] bty=StrToHex(s.replaceAll(" ", ""));
        
        byte[] bt=filterSpecificBytes(bty);
        System.out.println("bt:"+HexToStr(bt,0,bt.length));
        
        s="38 00 32 30 31 37 2F 30 36 2F 00 31 39 20 31 35 3A 35 39 0D B9 CA D5 CF 20 30 31 2D 31 00 32 30 20 A3 B1 A3 B2 A3 B3 A3 B8 B7 BF 20 20 0D 1B 4A";
        System.out.println("s:"+hexStringToEncodeString(s.replaceAll(" ", ""),"GBK"));
        
        
        s="00 1B 0A 38 00 32 30 31 37 2F 30 36 2F 00 31 39 20 31 36 3A 31 38 0D B9 CA D5 CF 20 30 31 2D 31 00 32 30 20 A3 B1 A3 B2 A3 B3 A3 B8 B7 BF 20 20 0D 1B 4A ";
        s+="00 1B 0A 38 00 32 30 31 37 2F 30 36 2F 00 31 39 20 31 36 3A 31 38 0D B9 CA D5 CF 20 30 31 2D 31 00 32 30 20 A3 B1 A3 B2 A3 B3 A3 B8 B7 BF 20 20 0D 1B 4A ";
        s+="00 1B 0A 38 00 32 30 31 37 2F 30 36 2F 00 31 39 20 31 36 3A 31 38 0D B9 CA D5 CF 20 30 31 2D 31 00 32 30 20 A3 B1 A3 B2 A3 B3 A3 B8 B7 BF 20 20 0D 1B 4A ";
        
        start=new byte[]{0x38,0x00};
        s=byte2hex(findStartWidth(hex2byte(s.replaceAll(" ", "")),start));
        System.out.println("s_1:"+s);   
        

        System.out.println("s_2:"+byte2hex(filterSpecificBytes(hex2byte(s.replaceAll(" ", "")),start))); 
        
        
        List<byte[]> reList=new ArrayList<byte[]>();        
        byte[] head=new byte[]{0x38,0x00};
        byte[] end=new byte[]{0x20,0x20,0x0D,0x1B,0x4A};        
        reList=splitPackage(hex2byte(s.replaceAll(" ", "")),head,end);
        
        for(byte[] b:reList){
        	System.out.println("s:"+byte2hex(b));
        }
        
        
        s="323320300000000000000000";
        s="1B401C26B5E7CCBDB2E2C6F7200D7EB6FEC2A5B2E2CAD4BFCEC5E4B5E7C7F8B6AB20B9E20D7E393A303220303031303130313520D2BBC6DAB3B5BCE40D7E1C2E1B69011C26C0FACAB7BBF0BEAF1C2E1B69001B6901303030311B69001B6C0C1C2631372F30372F32332030";
        bty=new byte[8];
		bty[0]=0x0000000000000000;
        System.out.println("s_2:"+byte2hex(filterSpecificBytes(hex2byte(s.replaceAll(" ", "")),bty)));
        
        bty=new byte[3];
		bty=new byte[]{0x20,0x20,0x20};
        s="1B 40 32 30 31 37 2E 30 37 2E 32 36 20 20 20 31 35 3A 31 30 3A 31 39 20 20 20 30 31 30 34 32 33 38 0A 1C 26 B7 C0 BB F0 B7 D6 C7 F8 20 20 B9 E2 B5 E7 CC BD B2 E2 C6 F7 0D 1C 2E 1B 69 01 1C 26 BB F0 BE AF 1C 2E 1B 69 00 1B 69 01 30 30 31 1B 69 00 1B 6C 0C 1C 26 B5 D8 CF C2 CA D2 B5 DA B6 FE 1C 2E 0D 1B 6C 00 0D 0A";
        System.out.println("s_2:"+byte2hex(getBytesBySpecificBytesEnd(hex2byte(s.replaceAll(" ", "")),15,bty)));
        
        
        end=new byte[]{0x0D,0x04,0x03,0x0D};
        s="30388D30378E31313A343220203031323139320D04000F6080403704146404142464041720C00000404C4A4948484B7E4848484A4C6B40000443300772828A93B482E1122464070040300F1020FF50484641424458FF0000000000FF2018878041320F3241C140000020908846472A32122A4642808080000D0F600000C04040407F48484848E84C080000202222FE2222FE232200FC0000FF000000FC14D4545454D414FF048566040000E000FF2010FE424242FA424242FF0200807000FF102808E81F08E808088C080020100C03FAAAAAAAAAAAAAFA030200000D04030D30388D30378E31313A343320203032323035330D04000F6040300F000000000000000000000000000242817F0001FE9292FF9292FE01000080804040212214081422214040C040002062231212124141211718244280E000000000FF2018878041320F3241C140000020908846472A32122A4642808080000D0F600000FC04040404050604040404060400101010FF9030AA7222FE2171A9ACA00008084848C848487F4848C848080C0800002030AC6320302020FF909294D08000807000FF102808E81F08E808088C080020100C03FAAAAAAAAAAAAAFA030200000D04030D30388D30378E31313A343320203032323035340D04000F600000007F201101FF0101010F30407000001F08081F00FF000F08084F807F00000808FC8C8A8AF900F98A8A8CFC08080000000000000000000000000000000000000000FF2018878041320F3241C140000020908846472A32122A4642808080000D0F60404244CC000202FA020202FF0200000000FC0404FC00F808CC4A49C808FC080080809F9191919FE09F91B1D19F80800000000000000000000000000000000000807000FF102808E81F08E808088C080020100C03FAAAAAAAAAAAAAFA030200000D04030D";
        reList=splitPackageByBytes(hex2byte(s.replaceAll(" ", "")),end);
        for(byte[] b:reList){
        	System.out.println("s:"+byte2hex(b));
        }
        
        start=new byte[]{0x1B,0x4B};
        s="1B 4B 3F 00 20 C2 0C E0 4E 41 51 C9 2D 41 87 48 24 26 E0 00 02 0C F0 08 04 FF 0A 12 62 82 42 22 FF 00 00 40 42 81 FE 00 00 84 88 90 A0 FF A0 90 8C 88 00 20 20 7F 80 02 E4 08 F0 08 E6 00 E2 01 FE 00 00 0A 1B 4B 3F 00 02 02 7F 02 02 01 3F 02 02 FF 04 04 0F 04 00 00 08 08 08 FF 08 18 08 1F 00 00 FF 04 04 0C 04 00 08 08 08 FF 08 18 0A 04 0B F8 08 08 0F 18 08 00 00 7F 44 5A 61 08 4B 6A 5A CA 5A 6A 4F 08 00 20 1B 4B 3F 00 00 3F 28 2B 2A 2A 2A 2B 28 FF 20 A1 66 20 00 00 07 00 FF 04 08 7F 42 42 42 5F 42 42 42 FF 40 00 08 08 08 FF 09 60 44 48 50 43 50 48 55 60 00 08 44 36 00 01 3F 20 2F 20 7F 20 1F 00 7F 00 00 0A 1B 4B 2F 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1B 4B 3F 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 E0 30 10 30 E0 C0 00 70 F0 90 10 10 30 30 00 E0 F0 10 10 10 F0 E0 00 E0 F0 10 10 10 F0 E0 00 20 0A 1B 4B 2F 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1B 4B 3F 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0F 1F 30 23 30 1F 0F 00 10 30 21 23 26 3C 18 00 0F 1F 32 22 22 03 01 00 1D 3F 22 22 22 3F 1D 00 20 ";
        reList=splitPackageByStartBytes(hex2byte(s.replaceAll(" ", "")),start);
        for(byte[] b:reList){
        	System.out.println("1s:"+byte2hex(b));
        }
        
        start=new byte[]{0x1B,0x31,0x04};
        end=new byte[]{0x1B,0x31,0x00};
        s="1B 31 04 31 37 2F 30 37 2F 33 31 20 20 31 35 3A 35 31 0A 5A 3D 30 30 20 4C 50 3D 30 32 20 41 52 3D 30 35 35 20 46 4C 3D 30 32 0A 1B 31 00 1B 4B 3F 00 20 C2 0C E0 4E 41 51 C9 2D 41 87 48 24 26 E0 00 02 0C F0 08 04 FF 0A 12 62 82 42 22 FF 00 00 40 42 81 FE 00 00 84 88 90 A0 FF A0 90 8C 88 00 20 20 7F 80 02 E4 08 F0 08 E6 00 E2 01 FE 00 00 0A 1B 4B 3F 00 02 02 7F 02 02 01 3F 02 02 FF 04 04 0F 04 00 00 08 08 08 FF 08 18 08 1F 00 00 FF 04 04 0C 04 00 08 08 08 FF 08 18 0A 04 0B F8 08 08 0F 18 08 00 00 7F 44 5A 61 08 4B 6A 5A CA 5A 6A 4F 08 00 20 1B 4B 3F 00 00 3F 28 2B 2A 2A 2A 2B 28 FF 20 A1 66 20 00 00 07 00 FF 04 08 7F 42 42 42 5F 42 42 42 FF 40 00 08 08 08 FF 09 60 44 48 50 43 50 48 55 60 00 08 44 36 00 01 3F 20 2F 20 7F 20 1F 00 7F 00 00 0A 1B 4B 2F 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1B 4B 3F 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 E0 30 10 30 E0 C0 00 70 F0 90 10 10 30 30 00 E0 F0 10 10 10 F0 E0 00 E0 F0 10 10 10 F0 E0 00 20 0A 1B 4B 2F 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1B 4B 3F 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0F 1F 30 23 30 1F 0F 00 10 30 21 23 26 3C 18 00 0F 1F 32 22 22 03 01 00 1D 3F 22 22 22 3F 1D 00 20 ";
        bty=getRealDataBtyes(hex2byte(s.replaceAll(" ", "")),start,end);
        System.out.println("getRealDataBtyes:"+byte2hex(bty));
        System.out.println("getOverplusBytes:"+byte2hex(getOverplusBytes(hex2byte(s.replaceAll(" ", "")),start.length+bty.length+end.length)));
        
        
        System.out.println((54+1)/8);
        
        System.out.println("mergeTwoSyteArray:"+byte2hex(mergeTwoSyteArray(start,end)));
        
        s="00 02 02 7F 02 02 01 3F 02 02 FF 04 04 0F 04 00 00 08 08 08 FF 08 18 08 1F 00 00 FF 04 04 0C 04 00 01 0E 00 FF 08 14 10 17 F8 10 17 10 10 31 10 00 04 08 30 C0 5F 55 55 55 55 55 55 5F C0 40 00 ";
        s="00 0F 1F 32 22 22 03 01 00 1D 3F 22 22 22 3F 1D ";
        byte[] startBytes=hex2byte(s.replaceAll(" ", ""));
        s="00 04 06 FC 08 08 00 FC 02 02 FA 22 12 E2 02 0E 00 04 06 04 FC 08 0A 12 FE 02 02 FE 02 02 06 02 00 00 00 00 FF 04 18 E1 01 82 4C F0 4C 82 83 02 00 00 04 09 11 61 E2 54 48 48 54 62 42 01 01 01 ";
        s="00 E0 F0 10 10 10 F0 E0 00 E0 F0 10 10 10 F0 E0 ";
        byte[] endBytes=hex2byte(s.replaceAll(" ", ""));
        int fontBytesLen=startBytes.length;
		for(int k=0;k<(fontBytesLen/16);k++){
			//00 02 02 7F 02 02 01 3F 02 02 FF 04 04 0F 04 00
			byte[] fontArraySatrt=HexUtil.getSpecifiedLengthBytes(startBytes, 16 * k, 16);
			System.out.println("fontArraySatrt:"+byte2hex(fontArraySatrt));
			//00 04 06 FC 08 08 00 FC 02 02 FA 22 12 E2 02 0E 
			byte[] fontArrayEnd=HexUtil.getSpecifiedLengthBytes(endBytes, 16 * k, 16);
			System.out.println("  fontArrayEnd:"+byte2hex(fontArrayEnd));
			byte[] fontArray = HexUtil.mergeTwoSyteArray(fontArraySatrt,fontArrayEnd);
			System.out.println("     fontArray:"+byte2hex(fontArray));
			
			fontArray = HexUtil.mergeTwoSyteArray(
					HexUtil.getSpecifiedLengthBytes(fontArraySatrt, 0, 8),
					HexUtil.getSpecifiedLengthBytes(fontArrayEnd, 0, 8));
			System.out.println("     fontArray:"+byte2hex(fontArray));
			fontArray = HexUtil.mergeTwoSyteArray(
					HexUtil.getSpecifiedLengthBytes(fontArraySatrt, 8, 8),
					HexUtil.getSpecifiedLengthBytes(fontArrayEnd, 8, 8));
			System.out.println("     fontArray:"+byte2hex(fontArray));
			
		}
*/		
       
        
        ArrayList<Byte> byteList = new ArrayList<Byte>();
        ArrayList<Byte> blist = new ArrayList<Byte>();
        byteList.add((byte)1);
        byteList.add((byte)2);
        byteList.add((byte)3);
        byteList.add((byte)2);
        byteList.add((byte)3);
        byteList.add((byte)2);
        byteList.add((byte)3);
        byteList.add((byte) 4);
        System.out.println(byteList);
        System.out.println("==============");
        for (byte val : byteList) {
        	if(blist.contains(val)){
        		System.out.println("存在："+val);
        	}else{
        		System.out.println("不存在："+val);
        		blist.add(val);
        	}
        }
        
        Byte[] bytes = new Byte[byteList.size()];
        byteList.toArray(bytes);
        System.out.println(Arrays.toString(bytes));
        
        bytes = new Byte[blist.size()];
        blist.toArray(bytes);
        System.out.println(Arrays.toString(bytes));
        
        
        String hex="CFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCFCF";
        byte[] b=HexUtil.hex2byte(hex);
        blist = new ArrayList<Byte>();
        for (byte val : b) {
        	if(!blist.contains(val)){
        		blist.add(val);
        	}
        }
        System.out.println(HexUtil.byte2hex(listTobyte(blist)));
        
	}
}
