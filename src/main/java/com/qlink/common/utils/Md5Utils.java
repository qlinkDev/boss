package main.java.com.qlink.common.utils;

import java.security.MessageDigest;

/**
 * md5加密
 * @author shuxin
 * @date 2016年6月1日	
 */
public class Md5Utils {
	
	/**
	 * 字符串MD5加密
	 * @athor shuxin
	 * @date 2016年6月1日下午12:43:13
	 * @param plainText
	 * @return
	 * String 
	 */
	public static String toMD5(String plainText) {
	     try {
	        //生成实现指定摘要算法的 MessageDigest 对象。
	        MessageDigest md = MessageDigest.getInstance("MD5");  
	        //使用指定的字节数组更新摘要。
	        md.update(plainText.getBytes());
	        //通过执行诸如填充之类的最终操作完成哈希计算。
	        byte b[] = md.digest();
	        //生成具体的md5密码到buf数组
	        int i;
	        StringBuffer buf = new StringBuffer("");
	        for (int offset = 0; offset < b.length; offset++) {
	          i = b[offset];
	          if (i < 0)
	            i += 256;
	          if (i < 16)
	            buf.append("0");
	          buf.append(Integer.toHexString(i));
	        }
	        return buf.toString();
	     } 
	     catch (Exception e) {
	       e.printStackTrace();
	       return "";
	     }
	   }

}
