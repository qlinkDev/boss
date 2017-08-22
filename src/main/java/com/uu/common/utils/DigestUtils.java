package com.uu.common.utils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
/**
 * Created by liaowu on 2016/2/24.
 */
public class DigestUtils {
    private static Logger logger = LoggerFactory.getLogger(DigestUtils.class);

    /**
     *
     * @param text
     * @param key
     * @param charset
     * @return
     */
    public static String getSignature(String text, String key, String charset) {
        text = text + key;
       /* String mysign = DigestUtils.md5Hex(getContentBytes(text, charset));
        if (mysign.equals(sign)) {
            return true;
        } else {
            return false;
        }*/
        // 使用MD5对待签名串求签
        byte[] bytes = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            bytes = md5.digest(text.getBytes(charset));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // 将MD5输出的二进制结果转换为小写的十六进制
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex);
        }
        return sign.toString().toLowerCase();
    }

    /**
     *
     * @param params
     * @param key
     * @param charset
     * @return
     * @throws Exception
     */
    public static String getSignature(JSONObject params, String key, String charset){
        return getSignature(getSortQueryString(params, charset, false, false), key, charset);
    }
    
    public static void main(String[] args) throws Exception{
    	String key = "password";
    	JSONObject obj = new JSONObject();
    	obj.put("appid", "402880e7509276fa01509282e6a40005");
    	obj.put("timestamp", "1345678990");
    	JSONObject params = new JSONObject();
    	params.put("osn", "FC23583485552503");
    	params.put("start_date", "2015-12-25 00:00:00");
    	params.put("end_date", "2016-01-12 00:00:00");
    	params.put("dsn", "990000706436195");
    	params.put("mccs", "310,311,312,313,314,315,316");
    	obj.put("params", params);
    	System.out.println("params:"+params);
    	System.out.println(getSignature(obj,key,"UTF-8"));
    }

    /**
     *
     * @param params
     * @param key
     * @param charset
     * @param allow
     * @return
     * @throws Exception
     */
    public static String getSignature(JSONObject params, String key, String charset, boolean allow){
        return getSignature(getSortQueryString(params, charset, allow, false), key, charset);
    }

    /**
     *
     * @param params
     * @param allow
     * @param encode
     * @return
     * @throws Exception
     */
    public static String getSortQueryString(JSONObject params, String charset, boolean allow, boolean encode){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // 先将参数以其参数名的字典序升序进行排序
            Map<String, Object> sortedParams = new TreeMap<String, Object>(params);
            Set<Entry<String, Object>> entrys = sortedParams.entrySet();
            // 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
            for (Entry<String, Object> param : entrys) {
                if(!allow && StringUtils.isBlank(param.getValue().toString()))
                    continue;
                if (stringBuilder.length() > 0) {
                    stringBuilder.append("&");
                }
                //stringBuilder.append(param.getKey()).append("=").append(param.getValue());

                stringBuilder.append((param.getKey() != null ? (encode ? URLEncoder.encode(param.getKey(), charset) : param.getKey()) : ""));
                stringBuilder.append("=");
                stringBuilder.append(param.getValue() != null ? (encode ? URLEncoder.encode(param.getValue().toString(), charset) : param.getValue()) : "");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        logger.debug("getSignature({})",stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     *
     * @param params
     * @return
     * @throws Exception
     */
    public static String getSortQueryString(JSONObject params){
        return getSortQueryString(params,"UTF-8",true,false);
    }
}
