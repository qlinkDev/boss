package com.uu.common.utils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

/**
 * 加密工具类
 * <p>对Map生成消息摘要主要用于对客户端/服务器端来回传递的参数生成消息摘要。
 * @author liaowu
 *
 */
public class HmacSHA256Utils {
	/**
	 * 使用指定的密码对内容生成消息摘要（散列值）  
	 * @param key
	 * @param content
	 * @return
	 */
	public static String digest(String key, String content) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			byte[] secretByte = key.getBytes("utf-8");
			byte[] dataBytes = content.getBytes("utf-8");

			SecretKey secret = new SecretKeySpec(secretByte, "HMACSHA256");
			mac.init(secret);

			byte[] doFinal = mac.doFinal(dataBytes);
			byte[] hexB = new Hex().encode(doFinal);
			return new String(hexB, "utf-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 使用指定的密码对整个Map的内容生成消息摘要（散列值）  
	 * @param key
	 * @param map
	 * @return
	 */
	public static String digest(String key, Object map) {
		return digest(key, map.toString());
	}
}
