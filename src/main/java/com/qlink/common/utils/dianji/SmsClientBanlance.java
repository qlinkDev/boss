package main.java.com.qlink.common.utils.dianji;
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
* @ClassName: SmsClientBanlance 

* @Description: 余额查询 

 */
public class SmsClientBanlance {

	/**
	 * @param url
	 *            ：必填--发送连接地址URL--比如>http://139.129.128.71:8086/msgHttp/json/balance
	 * @param account
	 *            ：必填--用户帐号
	 * @param password
	 *            ：必填--数字签名：(接口密码、时间戳32位MD5加密生成)
	 * @return 返回余额查询字符串
	 */
	public static String queryBanlance(String url, String account,
			String password) {

		String resultContent = "";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		
		HttpPost httpPost = new HttpPost(url);
		List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();  
		long timestamps = System.currentTimeMillis();
		
		formparams.add(new BasicNameValuePair("account", account));
		formparams.add(new BasicNameValuePair("password", SecurityUtil.getMD532Str(password+timestamps)));
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
