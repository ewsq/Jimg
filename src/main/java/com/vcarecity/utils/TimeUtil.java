package com.vcarecity.utils;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {
	public static final long MS_ONE_DAY = 86400000L;
	
	public static final long MS_DAY_FROM = 28800000L;
	
	public static long clearTime(long time){
		return (time + MS_DAY_FROM) / MS_ONE_DAY * MS_ONE_DAY - MS_DAY_FROM;
	}
	
	public static Date clearTime(Date d){
		return new Date((d.getTime() + MS_DAY_FROM) / MS_ONE_DAY * MS_ONE_DAY - MS_DAY_FROM);
	}
	
	public static Date getDate(Date date, Time time){
		return new Date(clearTime(date.getTime()) + time.getTime() + MS_DAY_FROM);
	}
	
	public static long getDate(long date, long time){
		return clearTime(date) + time + MS_DAY_FROM;
	}
	public static Timestamp getTimestamp(){
		Timestamp d = new Timestamp(System.currentTimeMillis()); 
		return d;
	}
	
	public static Timestamp getTimestamp(Date date, Time time){
		return new Timestamp(clearTime(date.getTime()) + time.getTime() + MS_DAY_FROM);
	}
	
	public static Timestamp getTimestamp(Timestamp stamp, Time time){
		return new Timestamp(clearTime(stamp.getTime()) + time.getTime() + MS_DAY_FROM);
	}
	
	public static Timestamp getTimestamp(String dateStr){
		return getTimestamp(dateStr,"yyyy-MM-dd HH:mm:ss");
	}
	
	public static Timestamp getTimestamp(String dateStr, String fmt){
		SimpleDateFormat sf = new SimpleDateFormat(fmt);
		Timestamp tm=null;
		try {
			dateStr=dateStr.replace("0A", "10");
			java.util.Date date = sf.parse(dateStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			tm=new Timestamp(cal.getTimeInMillis());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return tm;
	}
	
	private static String conversion(int field) {
		String str;
		if (field < 10) {
			str = "0" + field;
		} else {
			str = field + "";
		}
		return str;
	}	
	private static String AM_MPTime(int field, int isRest) {
		String str;
		if (isRest == 1) {
			str = field + 12 + "";
		} else {
			str = field + "";
		}
		return str;
	}
	
	public static String getSystemTime() {
		Calendar date = new GregorianCalendar();
		StringBuffer str = new StringBuffer();
		int year = date.get(Calendar.YEAR);
		String month = conversion(date.get(Calendar.MONTH) + 1);
		String day = conversion(date.get(Calendar.DATE));
		String hour = AM_MPTime(date.get(Calendar.HOUR),date.get(Calendar.AM_PM));
		String minute = conversion(date.get(Calendar.MINUTE));
		String second = conversion(date.get(Calendar.SECOND));
		str.append(year);
		str.append(month);
		str.append(day);
		str.append(hour);
		str.append(minute);
		str.append(second);
		return str.toString();
	}
	
	public static String getLocalTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new java.util.Date());
	}
	
	public static String getNoYearTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		return sdf.format(new java.util.Date());
	}	
}