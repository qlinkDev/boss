package main.java.com.qlink.common.utils.phone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uu.common.utils.PropertiesLoader;
import com.uu.common.utils.dianji.SmsClientSend;

/**
 * 
 * @Description 手机短信发送线程
 * @author yifang.huang
 * @date 2016年10月18日 下午1:45:18
 */
public class PhoneMessageThread extends Thread {
	
	public static Logger logger = LoggerFactory.getLogger(PhoneMessageThread.class);
	
	private String phone;				// 信息接收手机(多个号码以','分隔)
	
	private String content;				// 短信内容
	
	public PhoneMessageThread(String phone, String content) {
		super();
		this.phone = phone;
		this.content = content;
	}

	public void run() {
		sendMessage();
	}  
	
	/**
	 * 
	 * @Description 发送
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年10月18日 下午1:56:19
	 */
	private void sendMessage() {
		
    	try {
    		// 取短信发送配置信息
			PropertiesLoader propertiesLoader = new PropertiesLoader("phoneMessage.properties");
    		String url = propertiesLoader.getProperty("url");
			String account = propertiesLoader.getProperty("account");
			String password = propertiesLoader.getProperty("password");
			
			// 短信发送
			String sendResult = SmsClientSend.sendSms(url, account, password, phone, content);

			logger.info("发送短信手机:{},短信内容：{},短信结果：{}", phone, content, sendResult);
			
		} catch (Exception e) {
			logger.info("发送短信手机:{},短信内容：{},短信结果：{}", phone, content, "信息发送失败!");
			e.printStackTrace();
		}
	}
	
}
