/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package main.java.com.qlink.common.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

/**
 * 字符串工具类, 继承org.apache.commons.lang3.StringUtils类
 * @author admin
 * @version 2013-05-22
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
	
	public static String lowerFirst(String str){
		if(StringUtils.isBlank(str)) {
			return "";
		} else {
			return str.substring(0,1).toLowerCase() + str.substring(1);
		}
	}
	
	public static String upperFirst(String str){
		if(StringUtils.isBlank(str)) {
			return "";
		} else {
			return str.substring(0,1).toUpperCase() + str.substring(1);
		}
	}

	/**
	 * 替换掉HTML标签方法
	 */
	public static String replaceHtml(String html) {
		if (isBlank(html)){
			return "";
		}
		String regEx = "<.+?>";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(html);
		String s = m.replaceAll("");
		return s;
	}

	/**
	 * 缩略字符串（不区分中英文字符）
	 * @param str 目标字符串
	 * @param length 截取长度
	 * @return
	 */
	public static String abbr(String str, int length) {
		if (str == null) {
			return "";
		}
		try {
			StringBuilder sb = new StringBuilder();
			int currentLength = 0;
			for (char c : replaceHtml(StringEscapeUtils.unescapeHtml4(str)).toCharArray()) {
				currentLength += String.valueOf(c).getBytes("GBK").length;
				if (currentLength <= length - 3) {
					sb.append(c);
				} else {
					sb.append("...");
					break;
				}
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 缩略字符串（替换html）
	 * @param str 目标字符串
	 * @param length 截取长度
	 * @return
	 */
	public static String rabbr(String str, int length) {
        return abbr(replaceHtml(str), length);
	}
		
	
	/**
	 * 转换为Double类型
	 */
	public static Double toDouble(Object val){
		if (val == null){
			return 0D;
		}
		try {
			return Double.valueOf(trim(val.toString()));
		} catch (Exception e) {
			return 0D;
		}
	}

	/**
	 * 转换为Float类型
	 */
	public static Float toFloat(Object val){
		return toDouble(val).floatValue();
	}

	/**
	 * 转换为Long类型
	 */
	public static Long toLong(Object val){
		return toDouble(val).longValue();
	}

	/**
	 * 转换为Integer类型
	 */
	public static Integer toInteger(Object val){
		return toLong(val).intValue();
	}
	
	/**
	 * 获得i18n字符串
	 */
	public static String getMessage(String code, Object[] args) {
		LocaleResolver localLocaleResolver = SpringContextHolder.getBean(LocaleResolver.class);
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();  
		Locale localLocale = localLocaleResolver.resolveLocale(request);
		return SpringContextHolder.getApplicationContext().getMessage(code, args, localLocale);
	}
	
	/**
	 * 获得用户远程地址
	 */
	public static String getRemoteAddr(HttpServletRequest request){
		String remoteAddr = request.getHeader("X-Real-IP");
        if (isNotBlank(remoteAddr)) {
        	remoteAddr = request.getHeader("X-Forwarded-For");
        }else if (isNotBlank(remoteAddr)) {
        	remoteAddr = request.getHeader("Proxy-Client-IP");
        }else if (isNotBlank(remoteAddr)) {
        	remoteAddr = request.getHeader("WL-Proxy-Client-IP");
        }
        return remoteAddr != null ? remoteAddr : request.getRemoteAddr();
	}
	
	/**
	 * 检查是不是邮箱
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email){
		Pattern regex = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"); 
		Matcher matcher = regex.matcher(email);  
		return matcher.matches();  
	}
	
	/**
	 * 字符串为null时取默认值
	 * @Description 
	 * @author yuxiaoyu
	 * @date 2016年4月14日 下午4:36:24
	 */
	public static String defaultIfNull(String sourceStr, String defaultStr) {
		if (null == defaultStr) {
			return EMPTY;
		}
		if (null == sourceStr) {
			return defaultStr;
		}
		return sourceStr;
	}

	/**
	 * 字符串为null时取空字符串
	 * @Description 
	 * @author yuxiaoyu
	 * @date 2016年4月14日 下午4:36:24
	 */
	public static String emptyIfNull(String sourceStr) {
		if (null == sourceStr) {
			return EMPTY;
		}
		return sourceStr;
	}
	
	/**
	 * 对象判空
	 * @Description 
	 * @author yuxiaoyu
	 * @date 2016年5月4日 上午10:25:00
	 */
	public static boolean isEmpty(Object sourceObj) {
		if (null == sourceObj) {
			return true;
		}
		if(sourceObj instanceof String){
			String source = sourceObj.toString();
			return 0 == source.trim().length();
		}
		if(sourceObj instanceof Collection){
			Collection<?> source = (Collection<?>)sourceObj;
			return 0 == source.size();
		}
		return false;
	}
	
	/**
	 * 集合列表转换成字符串
	 * @athor shuxin
	 * @date 2016年7月19日下午4:15:13
	 * @param coutryCodeList
	 * @return
	 * String 
	 */
	public static String convertListtoString(List<String> list) {
		if(list == null || list.isEmpty()){
			return "";
		}
		StringBuffer buffer = new StringBuffer();
		for (String str : list) {
			buffer.append(str).append(",");
		}
		return buffer.length() == 0 ? null : buffer.substring(0, buffer.lastIndexOf(",")).toString();
	}
	
	/**
	 * 字符串转换成列表
	 * @athor shuxin
	 * @date 2016年7月21日下午1:23:33
	 * @param str
	 * @param split
	 * @return
	 * List<String> 
	 */
	public static List<String> convertStringToList(String str, String split){
		List<String> list =new ArrayList<String>();
		if(str.indexOf(split) != -1){
			String[] tempStr = str.split(split);
			for (int i = 0; i < tempStr.length; i++) {
				list.add(tempStr[i]);
			}
		} else {
			list.add(str);
		}
		return list;
	}
	
	/**
	 * 
	 * @Description 判断两个以','分隔的mcc字符串是否有相同的mcc
	 * @param mccesOne
	 * @param mccesTwo
	 * @return boolean  
	 * @author yifang.huang
	 * @date 2017年2月24日 下午2:26:25
	 */
	public static boolean mccInclude(String mccesOne, String mccesTwo) {
		
		if (StringUtils.isBlank(mccesOne) || StringUtils.isBlank(mccesTwo))
			return false;
		
		String[] mccOneArr = mccesOne.split(",");
		String[] mccTwoArr = mccesTwo.split(",");
		for (String mccOne : mccOneArr) {
			for (String mccTow : mccTwoArr) {
				if (mccOne.equals(mccTow)) {
					return true;
				}
			}
		}
		
		return false;
	}
}
