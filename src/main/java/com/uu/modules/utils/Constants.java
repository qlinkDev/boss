/** 
 * @Package com.uu.modules.utils 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月23日 下午2:59:37 
 * @version V1.0 
 */ 
package com.uu.modules.utils;

import com.uu.common.utils.PropertiesLoader;

/** 
 * @Description 系统常量
 * @author yifang.huang
 * @date 2016年3月23日 下午2:59:37 
 */
public class Constants {

	private Constants() {
	}
	

	public static final String SUPERUSR_ID = "1";
	
	/**
	 * 字符
	 */
	public static final String COLON = ":";
	public static final String VERTICAL_LINE = "|";
	public static final String EQUALS_SIGN = "=";
	public static final String MINUS = "-";
	public static final String AND_SIGN = "&";
	public static final String POINT = ".";
	public static final String COMMA = ",";
	public static final String ASTERISK = "*";
	public static final String SINGLE_QUOTES = "'";
	public static final String DOUBLE_QUOTES = "\"";
	public static final String BRACKETS_MIDDLE_FRONT = "[";
	public static final String BRACKETS_MIDDLE_BACK = "]";
	public static final String BRACE_BACK = "}";
	public static final String SEPARATOR = "/";
	public static final String QUESTION_MARK = "?";
	public static final String PERCENT_SIGN = "%";
	
	/**
	 * 命名用常量
	 */
	public static final String MCC = "mcc";
	public static final String MCCS = "mccs";
	public static final String COUNTRY_CODE = "country_code";
	public static final String COUNTRY_NAME_CN = "country_name_cn";
	public static final String COUNTRY_NAME_EN = "country_name_en";
	public static final String COUNTRYNAME = "countryName";
	public static final String USEDCNT = "usedCnt";
	public static final String FREECNT = "freeCnt";
	public static final String BLOCK = "block";
	public static final String REFUSE = "refuse";
	public static final String EQUCNT = "equCnt";
	public static final String STARTDATE = "startDate";
	public static final String ENDDATE = "endDate";
	public static final String DATELIST = "dateList";
	public static final String ROWS = "rows";
	public static final String MCCLIST = "mccList";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String CONTENT_TYPE_XML = "text/xml; charset=utf-8";
	public static final String UTF8 = "UTF-8";
	
	/**
	 * Vodafone卡级别常量
	 */
	public static final String VODAFONE_CARD_LEVEL_5 = "YYM_MIFI2.0_CTS_PAYG";
	public static final String VODAFONE_CARD_LEVEL_3 = "MIFI2.0_CTS_PAYG_Tier3";
	
	/**
	 * app.properties配置项名称
	 */
	public static final String VODAFONE_API_GETDEVICEDETAILS = "vodafone.api.getDeviceDetails";
	public static final String VODAFONE_API_SETDEVICEDETAILS = "vodafone.api.setDeviceDetails";
	public static final String VODAFONE_API_GETFILTEREDDEVICELIST = "vodafone.api.getFilteredDeviceList";
	public static final String VODAFONE_GDSPCREDENTIAL_USERID = "vodafone.gdspCredential.userId";
	public static final String VODAFONE_GDSPCREDENTIAL_PASSWORD = "vodafone.gdspCredential.password";
	public static final String VODAFONE_GDSPCREDENTIAL_CUSTOMID = "vodafone.gdspCredential.customId";
	public static final String VODAFONE_CERTIFICATE_PASSWORD = "vodafone.certificate.password";
	
	/**
	 * 默认渠道
	 */
	public static final String CHANNEL_DEFAULT_VALUE = "YOUYOUMOB";
	
	public static final String MIFI_TYPE_DEFAULT_VALUE = "YOUYOUMIFI";//默认设备类型
	
	public static final String MIFI_MODEL_DEFAULT_VALUE = "YOUYOUMIFI";//默认设备型号
	
	public static final String CHANNEL_YOUYOUYATTO_VALUE = "youyouauto";

	/**
	 * 正则表达式
	 */
	public static final String REG_DATE_YYYY_MM_DD = "\\d{4}-\\d{2}-\\d{2}";
	
	public static final String REG_INTEGER_LIMIT_10 = "0|([1-9]\\d{0,9})";
	
	/**
	 * 平台编号（sourceType）
	 */
	public static final String SOURCE_TYPE_MIFI = "MIFI";
	
	public static final String SOURCE_TYPE_BOOT = "BOOT";
	
	public static final String ueAllowed_n = "0";// 设备不允许使用
	
	public static final String ueAllowed_y = "1";// 设备允许使用
	
	/**
	 * 系统通用状态常量
	 */
	public static final String CONSTANTS_STATUS_SUCCESS = "SUCCESS";	// 成功
	public static final String CONSTANTS_STATUS_FAIL = "FAIL";			// 失败
	
	/**
	 * 字典常量(label,type)
	 */
	public static final String DICT_USABLE_DATA_LABEL = "sim卡可用流量";		// 下订单时使用
	public static final String DICT_USABLE_DATA_TYPE = "usableData";
	public static final String DICT_LIMIT_DATA_LABEL = "sim卡可用流量";		// 卡预警时使用
	public static final String DICT_LIMIT_DATA_TYPE = "limitData";
	public static final String DICT_ORDER_NOCHECK_DAYS_LABEL = "不检测sim卡的订单开始天数";
	public static final String DICT_ORDER_NOCHECK_DAYS_TYPE = "orderNoCheckDays";
	public static final String DICT_CANORDER_SIMNUM_LABEL = "可以下订单的sim卡张数";
	public static final String DICT_CANORDER_SIMNUM_TYPE = "canOrderSimNum";
	
	public static final String DICT_DEVICE_MONITOR_STATUS_EQ0_LABEL = "设备状态监控状态为0切换时间";
	public static final String DICT_DEVICE_MONITOR_STATUS_EQ0_TYPE = "deviceMonitroStatusEq0";
	public static final String DICT_DEVICE_MONITOR_STATUS_GT0_LABEL = "设备状态监控状态大于0切换时间";
	public static final String DICT_DEVICE_MONITOR_STATUS_GT0_TYPE = "deviceMonitroStatusGt0";

	public static final String DICT_CARD_USE_PEOPLE_TYPE_LABEL = "卡使用用户类型有限制的国家";
	public static final String DICT_CARD_USE_PEOPLE_TYPE_TYPE = "simUsePeopleTypeCountry";
	
	public static final String DICT_MIFI_BASE_VERSION_LABEL = "设备基础版本";
	public static final String DICT_MIFI_BASE_VERSION_TYPE = "mifiBaseVersion";
	
	public static final String DICT_SIMNODE_STAMP_UPDATED_LABEL = "simNode表stamp_updated字段超时时间";
	public static final String DICT_SIMNODE_STAMP_UPDATED_TYPE = "simNodeStampUpdated";

	public static final String DICT_CARD_MONITOR_LABEL = "卡监控故障编码";
	public static final String DICT_CARD_MONITOR_TYPE = "card_monitor_fault_code";
	
	public static final String DICT_CALLBACK_URL_LABEL = "渠道回调接口"; 	// 数据库中为[sourceType_渠道回调接口]
	public static final String DICT_CALLBACK_URL_TYPE = "CallbackUrl";	// 数据库中为[sourceType_CallbackUrl]
	
	public static final String DICT_DEVICE_USED_FLOW_DATA_LABEL = "设备使用流量"; 		
	public static final String DICT_DEVICE_USED_FLOW_DATA_TYPE = "deviceUsedFlowDate";
	
	public static final String DICT_MCC_FOR_VODAFONE_CARD_5_LABEL = "Vodafone卡需要升级到5的MCC";
	public static final String DICT_MCC_FOR_VODAFONE_CARD_5_TYPE = "mccForVodafoneCard5";
	
	// 定时执行任务接口服务地址
	public static final String SCHEDULED_SERVICE_URL;
	static {
		PropertiesLoader propertiesLoader = new PropertiesLoader("app.properties");
		SCHEDULED_SERVICE_URL = propertiesLoader.getProperty("scheduled.service.url");
		
	}
}
