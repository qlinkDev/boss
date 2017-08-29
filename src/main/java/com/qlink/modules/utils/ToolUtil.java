package main.java.com.qlink.modules.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


public class ToolUtil {
	
	/**
	 * 把对象转换成字符串
	 * @param obj
	 * @return String 转换成字符串,若对象为null,则返回空字符串.
	 */
	public static String toString(Object obj) {
		if(obj == null)
			return "";
		
		return obj.toString();
	}
	
	/**
	 * 把对象转换为int数值.
	 * 
	 * @param obj
	 *            包含数字的对象.
	 * @return int 转换后的数值,对不能转换的对象返回0。
	 */
	public static int toInt(Object obj) {
		int a = 0;
		try {
			if (obj != null)
				a = Integer.parseInt(obj.toString());
		} catch (Exception e) {

		}
		return a;
	}
	
	/**
	 * 获取当前时间 yyyyMMddHHmmss
	 * @return String
	 */ 
	public static String getCurrTime() {
		Date now = new Date();
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String s = outFormat.format(now);
		return s;
	}
	
	/**
	 * 获取当前日期 yyyyMMdd
	 * @param date
	 * @return String
	 */
	public static String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String strDate = formatter.format(date);
		return strDate;
	}
	
	/**
	 * 取出一个指定长度大小的随机正整数.
	 * 
	 * @param length
	 *            int 设定所取出随机数的长度。length小于11
	 * @return int 返回生成的随机数。
	 */
	public static int buildRandom(int length) {
		int num = 1;
		double random = Math.random();
		if (random < 0.1) {
			random = random + 0.1;
		}
		for (int i = 0; i < length; i++) {
			num = num * 10;
		}
		return (int) ((random * num));
	}
	
	/**
	 * 获取编码字符集
	 * @param request
	 * @param response
	 * @return String
	 */
	public static String getCharacterEncoding(HttpServletRequest request,
			HttpServletResponse response) {
		
		if(null == request || null == response) {
			return "gbk";
		}
		
		String enc = request.getCharacterEncoding();
		if(null == enc || "".equals(enc)) {
			enc = response.getCharacterEncoding();
		}
		
		if(null == enc || "".equals(enc)) {
			enc = "gbk";
		}
		
		return enc;
	}
	
	/**
	 * 获取unix时间，从1970-01-01 00:00:00开始的秒数
	 * @param date
	 * @return long
	 */
	public static long getUnixTime(Date date) {
		if( null == date ) {
			return 0;
		}
		
		return date.getTime()/1000;
	}
		
	/**
	 * 时间转换成字符串
	 * @param date 时间
	 * @param formatType 格式化类型
	 * @return String
	 */
	public static String date2String(Date date, String formatType) {
		SimpleDateFormat sdf = new SimpleDateFormat(formatType);
		return sdf.format(date);
	}
	
	public static String loadJSON (String url,SortedMap params,String reqhash) {
        StringBuilder json = new StringBuilder();
        try {
            URL jUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)jUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type","application/x-www-form-urlencoded");
            OutputStream out = conn.getOutputStream();
           
            StringBuffer sb = new StringBuffer();
            params.put("reqhash", reqhash);
            Set es = params.entrySet();
    		Iterator it = es.iterator();
    		while(it.hasNext()) {
    			Map.Entry entry = (Map.Entry)it.next();
    			String k = (String)entry.getKey();
    			String v = (String)entry.getValue();
    			if(null != v && !"".equals(v)) {
    				sb.append(k + "=" + v + "&");
    			}
    		}
    		String reqPars = sb.substring(0, sb.lastIndexOf("&"));
    		System.out.println("---Post url: " + url);
    		System.out.println("---Post params: " + reqPars);
            out.write(reqPars.getBytes());
            out.flush(); //清空缓冲区，发送数据
            
            System.out.println("Response Code:" + conn.getResponseCode());
            BufferedReader in = new BufferedReader(new InputStreamReader( conn.getInputStream(),"utf-8"));
            String inputLine = null;
            while ( (inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (Exception e) {
        	e.printStackTrace();
        } 
        return json.toString();
    }
	
	public static String postMethod(String url,String jsonString) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		String response = "";
		try {
			StringEntity s = new StringEntity(jsonString, "UTF-8");   // 中文乱码在此解决
			s.setContentType("application/json");
			post.setEntity(s);

			HttpResponse res = client.execute(post);
			//if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = res.getEntity();
				response = EntityUtils.toString(entity, "UTF-8");
				System.out.println(response);
			//}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	//替换JSONObject里值为NULL的为空字符串
	public static JSONObject jsonReplaceNull(JSONObject json) {
		Iterator it = json.keys();  
        while (it.hasNext()) {  
            String key = (String) it.next();  
            String value = json.getString(key);
            if(null == value || "null".equalsIgnoreCase(value)) {
            	json.put(key, "");
            }
        }
		return json;
	}
	
	//将JSONObject对象里的key都转换成小写
	public static JSONObject transObject(JSONObject o1){
        JSONObject o2=new JSONObject();
         Iterator it = o1.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                Object object = o1.get(key);
                if(object.getClass().toString().endsWith("String")){
                    o2.accumulate(key.toLowerCase(), object);
                }else if(object.getClass().toString().endsWith("JSONObject")){
                    o2.accumulate(key.toLowerCase(), ToolUtil.transObject((JSONObject)object));
                }else if(object.getClass().toString().endsWith("JSONArray")){
                    o2.accumulate(key.toLowerCase(), ToolUtil.transArray(o1.getJSONArray(key)));
                } else {
                	o2.accumulate(key.toLowerCase(), object);
                }
            }
            return o2;
    }
    public static JSONArray transArray(JSONArray o1){
        JSONArray o2 = new JSONArray();
        for (int i = 0; i < o1.size(); i++) {
            Object jArray=o1.getJSONObject(i);
            if(jArray.getClass().toString().endsWith("JSONObject")){
                o2.add((ToolUtil.transObject((JSONObject)jArray)));
            }else if(jArray.getClass().toString().endsWith("JSONArray")){
                o2.add(ToolUtil.transArray((JSONArray)jArray));
            }
        }
        return o2;
    }
	

}
