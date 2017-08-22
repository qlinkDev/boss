/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.uu.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 * @author admin
 * @version 2013-3-15
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	public static final String YYYYMMDD = "yyyyMMdd";
	public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	private static String[] parsePatterns = { YYYY_MM_DD, YYYY_MM_DD_HH_MM_SS, "yyyy-MM-dd HH:mm", 
		"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" };

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}
	
	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}
	
	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String formatDate(Date date, Object... pattern) {
		String formatDate = null;
		if (pattern != null && pattern.length > 0) {
			formatDate = DateFormatUtils.format(date, pattern[0].toString());
		} else {
			formatDate = DateFormatUtils.format(date, parsePatterns[0]);
		}
		return formatDate;
	}
	
	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String formatDateTime(Date date) {
		return formatDate(date, parsePatterns[1]);
	}

	/**
	 * 得到当前时间字符串 格式（HH:mm:ss）
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串 格式（MM）
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * 得到当天字符串 格式（dd）
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}
	
	/**
	 * 日期型字符串转化为日期 格式
	 * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", 
	 *   "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" }
	 */
	public static Date parseDate(Object str) {
		if (str == null){
			return null;
		}
		try {
			return parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取过去的天数
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/(24*60*60*1000);
	}
	
    
	public static Date getDateStart(Date date) {
		if(date==null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date= sdf.parse(formatDate(date, "yyyy-MM-dd")+" 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static Date getDateEnd(Date date) {
		if(date==null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date= sdf.parse(formatDate(date, "yyyy-MM-dd") +" 23:59:59");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
		

	/** 
	 * 取得当前时间戳（精确到秒） 
	 * @athor shuxin
	 * @date 2016年6月1日上午11:29:24
	 * @return
	 */
	public static String timeStamp(){  
       long time = System.currentTimeMillis();  
       String t = String.valueOf(time/1000);  
       return t;  
    }  

	/**
	 * 
	 * @Description 取前一天
	 * @return
	 * @return String
	 * @author yifang.huang
	 * @date 2016年5月20日 下午5:28:45
	 */
	public static String getPreDate() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, -1);
		return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
	}
	
	/**
	 * 计算两个日期之间相差的天数 
	 * @athor shuxin
	 * @date 2016年8月25日上午10:19:10
	 * @param smdate 较小的时间 
	 * @param bdate  较大的时间 
	 * @return 相差天数 
	 * @throws ParseException
	 * int 
	 */
	public static int daysBetween(Date smdate,Date bdate) throws ParseException {    
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
        smdate=sdf.parse(sdf.format(smdate));  
        bdate=sdf.parse(sdf.format(bdate));  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(smdate);    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(bdate);    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));           
    }
	
	 /**
	  * 计算两个字符串格式日期的间隔天数
	 * @athor shuxin
	 * @date 2016年8月25日上午10:20:31
	 * @param smdate 较小的时间 
	 * @param bdate 较大的时间 
	 * @return  相差天数 
	 * @throws ParseException
	 * int 
	 */
	public static int daysBetween(String smdate,String bdate) throws ParseException{  
	    SimpleDateFormat sdf=new SimpleDateFormat(parsePatterns[0]);  
	    Calendar cal = Calendar.getInstance();    
	    cal.setTime(sdf.parse(smdate));    
	    long time1 = cal.getTimeInMillis();                 
	    cal.setTime(sdf.parse(bdate));    
	    long time2 = cal.getTimeInMillis();         
	    long between_days=(time2-time1)/(1000*3600*24);  
	    return Integer.parseInt(String.valueOf(between_days));     
	}  
	
	/**
	 *  根据年 月 获取对应的月份 天数
	 * @athor shuxin
	 * @date 2016年8月25日上午10:44:59
	 * @param year
	 * @param month
	 * @return
	 * int 
	 */
	public static int getDaysByYearMonth(int year, int month) {  
        Calendar a = Calendar.getInstance();  
        a.set(Calendar.YEAR, year);  
        a.set(Calendar.MONTH, month - 1);  
        a.set(Calendar.DATE, 1);  
        a.roll(Calendar.DATE, -1);  
        int maxDate = a.get(Calendar.DATE);  
        return maxDate;  
    }  
	
	/**
	 * 获取一年多少天
	 * @athor shuxin
	 * @date 2016年8月29日下午5:38:20
	 * @param year
	 * @return
	 * int 
	 */
	public static int getDaysByYear(int year){
		Calendar d = Calendar.getInstance();
		d.set(Calendar.YEAR, year);
		return d.getActualMaximum(Calendar.DAY_OF_YEAR);
	}
	
	/**
	 * 
	 * @Description 根据订单开始时间判断订单是否开始
	 * @param startDate
	 * @return boolean  
	 * @author yifang.huang
	 * @date 2017年5月8日 上午10:38:31
	 */
	public static boolean orderIsBegin(Date startDate) {

		Date nowDate = DateUtils.getDateEnd(new Date());
		
		// 当前时间取当天23点59分59秒
		if (startDate.getTime()<=nowDate.getTime()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String getAfterDate(String dateOneStr, String dateTwoStr) {
		
		if (StringUtils.isBlank(dateOneStr) || StringUtils.isBlank(dateTwoStr))
			return null;
		
		SimpleDateFormat df = new SimpleDateFormat(YYYY_MM_DD); 
		try {
			Date dateOne = df.parse(dateOneStr);
			Date dateTwo = df.parse(dateTwoStr);
			if (dateOne.after(dateTwo))
				return df.format(dateOne);
			return df.format(dateTwo);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getBeforeDate(String dateOneStr, String dateTwoStr) {
		
		if (StringUtils.isBlank(dateOneStr) || StringUtils.isBlank(dateTwoStr))
			return null;
		
		SimpleDateFormat df = new SimpleDateFormat(YYYY_MM_DD); 
		try {
			Date dateOne = df.parse(dateOneStr);
			Date dateTwo = df.parse(dateTwoStr);
			if (dateOne.before(dateTwo))
				return df.format(dateOne);
			return df.format(dateTwo);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		//System.out.println(formatDate(parseDate("2010/3/6")));
//		System.out.println(getDate("yyyy年MM月dd日 E"));
//		long time = new Date().getTime()-parseDate("2012-11-19").getTime();
//		System.out.println(time/(24*60*60*1000));
//		System.out.println(DateUtils.orderIsBegin("2017-05-15"));
		
		//SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
		String str1 = "2017-05-19 10:09:09";
		String str2 = "2017-05-20 11:09:09";
		System.out.println(getAfterDate(str1, str2));
		System.out.println(getBeforeDate(str1, str2));
		
	}
}
