/** 
 * @Package com.uu.common.utils 
 * @Description 
 * @author yuxiaoyu
 * @date 2016年11月8日 上午10:42:31 
 * @version V1.0 
 */
package com..common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uu.common.config.Global;
import com.uu.modules.utils.Constants;

/** 
 * @Description 沃达丰接口
 * @author yuxiaoyu
 * @date 2016年11月8日 上午10:42:31 
 */
@SuppressWarnings("deprecation")
public class VodafoneUtils {
	private static final String CERT_LOCATION = SpringContextHolder.getRootRealPath() + File.separator + "cert" + File.separator
			+ "youyoumobile-m2m-prd.pfx";
	private static final String KEY_STORE_TYPE = "PKCS12";

	private static Logger logger = LoggerFactory.getLogger(VodafoneUtils.class);

	private static boolean BATCH_PROCESSING = false;

	/**
	 * 根据iccId设置消费级别
	 * @Description 
	 * @param iccId
	 * @param customerServiceProfile
	 * @return 
	 * @return String  
	 * @author yuxiaoyu
	 * @date 2017年2月24日 下午2:00:38
	 */
	public static String setDeviceDetails(String iccId, String customerServiceProfile) {
		String logId = IdGen.uuid();
		String url = Global.getConfig(Constants.VODAFONE_API_SETDEVICEDETAILS);
		String mainBody = "<ws:setDeviceDetailsv4><deviceId>" + iccId + "</deviceId><customerServiceProfile>" + customerServiceProfile
				+ "</customerServiceProfile></ws:setDeviceDetailsv4>";
		String result = sendHttps(url, mainBody, logId);
		try {
			Document document = DocumentHelper.parseText(result);
			Element e = document.getRootElement();
			e = e.element("Body").element("setDeviceDetailsv4Response").element("return").element("returnCode");
			String majorReturnCode = e.element("majorReturnCode").getTextTrim();
			String minorReturnCode = e.element("minorReturnCode").getTextTrim();
			logger.info(logId + " majorReturnCode: " + majorReturnCode + " minorReturnCode: " + minorReturnCode);
			return minorReturnCode;
		} catch (Exception ex) {
			ex.printStackTrace();
			return StringUtils.EMPTY;
		}
	}

	/**
	 * 根据iccId获取设备消费级别
	 * @Description 
	 * @param iccId
	 * @return 
	 * @return String  
	 * @author yuxiaoyu
	 * @date 2017年2月24日 下午2:00:46
	 */
	public static String getDeviceDetails(String iccId) {
		String logId = IdGen.uuid();
		String url = Global.getConfig(Constants.VODAFONE_API_GETDEVICEDETAILS);
		String mainBody = "<ws:getDeviceDetailsv2><deviceId>" + iccId + "</deviceId></ws:getDeviceDetailsv2>";
		String result = sendHttps(url, mainBody, logId);
		try {
			Document document = DocumentHelper.parseText(result);
			Element e = document.getRootElement();
			e = e.element("Body").element("getDeviceDetailsv2Response").element("return");
			String customerServiceProfile = e.element("customerServiceProfile").getTextTrim();
			if (StringUtils.isNotBlank(customerServiceProfile)) {
				return customerServiceProfile;
			}
			e = e.element("returnCode");
			String majorReturnCode = e.element("majorReturnCode").getTextTrim();
			String minorReturnCode = e.element("minorReturnCode").getTextTrim();
			logger.info(logId + " majorReturnCode: " + majorReturnCode + " minorReturnCode: " + minorReturnCode);
			return minorReturnCode;
		} catch (Exception ex) {
			ex.printStackTrace();
			return StringUtils.EMPTY;
		}
	}

	/**
	 * 获取指定消费级别的设备
	 * @Description 
	 * @param customerServiceProfile
	 * @return 
	 * @return String  
	 * @author yuxiaoyu
	 * @date 2017年2月27日 下午4:33:41
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getFilteredDeviceList(String customerServiceProfile) {
		String logId = IdGen.uuid();
		String url = Global.getConfig(Constants.VODAFONE_API_GETFILTEREDDEVICELIST);
		String mainBody = "<ws:getFilteredDeviceListv4><matchCustomerServiceProfiles><customerServiceProfileName>" + customerServiceProfile
				+ "</customerServiceProfileName></matchCustomerServiceProfiles></ws:getFilteredDeviceListv4>";
		String result = sendHttps(url, mainBody, logId);
		try {
			Document document = DocumentHelper.parseText(result);
			Element e = document.getRootElement();
			e = e.element("Body").element("getFilteredDeviceListv4Response").element("return");
			String matchedResults = e.element("matchedResults").getTextTrim();
			if (StringUtils.isBlank(matchedResults) || 0 == Integer.valueOf(matchedResults)) {
				return null;
			}
			List<Element> deviceList = e.element("deviceList").elements();
			if (null == deviceList || 0 == deviceList.size()) {
				return null;
			}
			List<String> iccIdList = new ArrayList<String>();
			String iccId;
			for (Element device : deviceList) {
				iccId = device.element("iccid").getTextTrim();
				iccIdList.add(iccId);
			}
			return iccIdList;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 发送https请求
	 * @Description 
	 * @param url
	 * @param mainBody
	 * @return 
	 * @return String  
	 * @author yuxiaoyu
	 * @date 2017年2月23日 下午3:04:51
	 */
	private static String sendHttps(String url, String mainBody, String logId) {
		String result = StringUtils.EMPTY;
		char[] password = Global.getConfig(Constants.VODAFONE_CERTIFICATE_PASSWORD).toCharArray();
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		try {
			KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
			FileInputStream instream = new FileInputStream(new File(CERT_LOCATION));
			try {
				keyStore.load(instream, password);
			} finally {
				instream.close();
			}
			SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, password).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null,
					SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.gdsp.vodafone.com/\"><soapenv:Header><ws:gdspHeader><gdspCredentials><customerId>"
					+ Global.getConfig(Constants.VODAFONE_GDSPCREDENTIAL_CUSTOMID).trim()
					+ "</customerId><password>"
					+ Global.getConfig(Constants.VODAFONE_GDSPCREDENTIAL_PASSWORD)
					+ "</password><userId>"
					+ Global.getConfig(Constants.VODAFONE_GDSPCREDENTIAL_USERID)
					+ "</userId></gdspCredentials></ws:gdspHeader></soapenv:Header><soapenv:Body>" + mainBody + "</soapenv:Body></soapenv:Envelope>";
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_XML);
			httpPost.setEntity(new StringEntity(requestBody, Constants.UTF8));
			logger.info(logId + " requestUri: " + url);
			logger.info(logId + " mainBody: " + mainBody);

			long startTime = Calendar.getInstance().getTimeInMillis();
			// 发送请求
			response = httpclient.execute(httpPost);
			long endTime = Calendar.getInstance().getTimeInMillis();
			long timeCosts = endTime - startTime;
			logger.info(logId + " timeCosts: " + timeCosts + "ms");
			result = EntityUtils.toString(response.getEntity(), Constants.UTF8);

			logger.info(logId + " statusCode: " + response.getStatusLine().getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (httpclient != null) {
					httpclient.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * 判断系统当前是否正在批量修改设备消费级别
	 * @Description 
	 * @return 
	 * @return boolean  
	 * @author yuxiaoyu
	 * @date 2017年2月27日 下午5:11:16
	 */
	public static boolean isBatchProcessing() {
		return BATCH_PROCESSING;
	}

	public static void setBatchProcessing(boolean isBatchProcessing) {
		BATCH_PROCESSING = isBatchProcessing;
	}
}
