package com.qlink.common.utils.dianji;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
/**
 * 
* @ClassName: SmsClientSend 

* @Description: 短信发送 
 */
public class SmsClientSend {



	
	/**
	 * @param url
	 *            ：必填--发送连接地址URL--比如>http://139.129.128.71:8086/msgHttp/json/mt
	 * 
	 * @param account
	 *            ：必填--用户帐号
	 * @param password
	 *            ：必填--数字签名：(接口密码、时间戳32位MD5加密生成)
	 * @param mobile
	 *            ：必填--发送的手机号码，多个可以用逗号隔比如>13512345678,13612345678
	 * @param content
	 *            ：必填--实际发送内容
	 * @return 返回发送之后收到的信息
	 */
	public static String sendSms(String url,String account,
			String password, String mobile, String content) {
		String resultContent = "";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		
		HttpPost httpPost = new HttpPost(url);
		List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();  
		long timestamps = System.currentTimeMillis();
		
		formparams.add(new BasicNameValuePair("account", account));
		formparams.add(new BasicNameValuePair("password", SecurityUtil.getMD532Str(password+mobile+timestamps)));
		formparams.add(new BasicNameValuePair("mobile", mobile));
		formparams.add(new BasicNameValuePair("content", content));
		formparams.add(new BasicNameValuePair("timestamps", timestamps+""));
		
		

	    UrlEncodedFormEntity uefEntity;
		try {
			long start = System.currentTimeMillis();
			uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
		    httpPost.setEntity(uefEntity);   
		    System.out.println("1 "+EntityUtils.toString(httpPost.getEntity()));
		    System.out.println("2 "+"httpPost.-->"+httpPost.toString());
		    CloseableHttpResponse httpResponse = httpclient.execute(httpPost);
		    System.out.println("3 "+"httpResponse-->"+httpResponse.toString());
		    HttpEntity entity = httpResponse.getEntity();
		    System.out.println("4 "+httpResponse.getStatusLine().toString());
			if (entity != null) {
				resultContent = EntityUtils.toString(entity, "UTF-8");
				System.out.println("5\n "+resultContent);
			}
			long end = System.currentTimeMillis();
			System.out.println("cost -->"+(end -start));
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			// 关闭连接,释放资源
			try {
				if(response != null){
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultContent;
	}
}
