package main.java.com.qlink.common.utils;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailUtils {
	
	public static Logger logger = LoggerFactory.getLogger(MailUtils.class);
	
	/**
	 * 发送邮件
	 * @param subject 邮件标题
	 * @param nickName 发送人昵称
	 * @param sendTo 收件人地址(多个收件邮箱以','分隔)
	 * @param content 发送内容
	 * @throws MessagingException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void sendMail(String subject, String nickName, String content, String sendTos) throws MessagingException, UnsupportedEncodingException{

    	try {
			PropertiesLoader propertiesLoader = new PropertiesLoader("mail.properties");
			// smtp
			String smtp = propertiesLoader.getProperty("smtp");
			// account
			String account = propertiesLoader.getProperty("account");
			// password
			String password = propertiesLoader.getProperty("password");
			// sendFrom
			String sendFrom = account;
			if(account.indexOf("@") == -1){
				sendFrom = account + StringUtils.substringAfter(smtp, ".");
			}
			
			Properties props = new Properties();
			// 设置发送邮件的邮件服务器的属性（这里使用网易的smtp服务器）
			props.put("mail.smtp.host", smtp);
			// 需要经过授权，也就是有户名和密码的校验，这样才能通过验证（一定要有这一条）
			props.put("mail.smtp.auth", "true");
			// 用刚刚设置好的props对象构建一个session
			Session session = Session.getDefaultInstance(props);
			// 有了这句便可以在发送邮件的过程中在console处显示过程信息，供调试使
			// 用（你可以在控制台（console)上看到发送邮件的过程）
			session.setDebug(false);
			// 用session为参数定义消息对象
			MimeMessage message = new MimeMessage(session);
			
			// 加载发件人地址
			message.setFrom(new InternetAddress(sendFrom, MimeUtility.encodeText(nickName)));
			// TO表示主要接收人，CC表示抄送人，BCC表示秘密抄送人
			// 加载主要收件人地址
			message.setRecipient(Message.RecipientType.TO, getTO(sendTos));
			// 加载抄送人地址
			InternetAddress[] mailCC = getCC(sendTos);
			if (mailCC != null)
				message.setRecipients(Message.RecipientType.CC, mailCC);
			// 加载标题
			message.setSubject(subject);
			
			// 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
			Multipart multipart = new MimeMultipart();
			
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setText(content, "utf-8", "html");
			// 设置邮件的文本内容
			multipart.addBodyPart(mimeBodyPart);

			// 添加附件
			// BodyPart messageBodyPart = new MimeBodyPart();
			// DataSource source = new FileDataSource(affix);
			// 添加附件的内容
			// messageBodyPart.setDataHandler(new DataHandler(source));
			// 添加附件的标题
			// 这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
			// sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
			// messageBodyPart.setFileName("=?GBK?B?"+
			// enc.encode(affixName.getBytes()) + "?=");
			// multipart.addBodyPart(messageBodyPart);

			// 将multipart对象放到message中
			message.setContent(multipart);
			// 保存邮件
			message.saveChanges();
			// 发送邮件
			Transport transport = session.getTransport("smtp");
			// 连接服务器的邮箱
			transport.connect(smtp, account, password);
			// 把邮件发送出去
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			logger.info( subject + ":邮件发送失败！");
			e.printStackTrace();
		}
		logger.info( subject + ":邮件发送成功！");
	}
	
	// 主要接收人(第一个)
	private static InternetAddress getTO(String sendTos) throws AddressException {
		
		String[] mailArr = sendTos.split(",");
		
		return new InternetAddress(mailArr[0]);
		
	}
	
	// 抄送人
	private static InternetAddress[] getCC(String sendTos) throws AddressException {
		
		String[] mailArr = sendTos.split(",");
		int len = mailArr.length;
		if (len == 1) {
			return null;
		}
		
		InternetAddress[] addArr = new InternetAddress[len-1];
		for (int i=1; i<len; i++) {
			addArr[i-1] = new InternetAddress(mailArr[i]);
		}

		return addArr;
	}
	
}
