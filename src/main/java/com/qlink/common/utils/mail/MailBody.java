package main.java.com.qlink.common.utils.mail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;

import com.uu.common.utils.StringUtils;
import com.uu.modules.mifi.entity.CardMonitor;
import com.uu.modules.mifi.entity.DeviceMonitorDetail;
import com.uu.modules.sys.utils.DictUtils;

/**
 * 系统所有模块的邮件内容
 */
public class MailBody {

	/**
	 * 
	 * @Description 卡监控
	 * @param list 监控数据
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年5月18日 上午11:49:03
	 */
	public static String cardMonitor(List<CardMonitor> list){
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		StringBuffer sbuffer = new StringBuffer();
		
		sbuffer.append("<style>.headTd {border-bottom: #ccc 1px solid; border-left: #ccc 1px solid; border-top: #ccc 1px solid; border-right: #ccc 1px solid}");
		sbuffer.append(".bodyTd {border-bottom: #ddd 1px solid; border-left: #ddd 1px solid; border-top: medium none; border-right: #ddd 1px solid}");
		sbuffer.append("</style>");
		
		sbuffer.append("<div>设备开机不能正常使用</div><div><br /></div>");
		sbuffer.append("<div><table border='0' cellpadding='0' cellspacing='0' style='text-align: center; line-height: 1; border-spacing: 0; border-collapse: collapse; font-size: 13px' width='60%'>");
		sbuffer.append("<thead><tr><td height='28' width='120' class='headTd'>故障编码</td><td width='120' class='headTd'>设备编号</td><td width='120' class='headTd'>MCC</td><td width='120' class='headTd'>发生时间</td></tr></thead><tbody>");
		
		for (CardMonitor bean : list) {
			sbuffer.append("<tr><td align='middle' height='30' valign='center' class='bodyTd'>"+DictUtils.getDictLabel(bean.getFaultCode(), "card_monitor_fault_code", null)+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+bean.getImei()+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ (StringUtils.isBlank(bean.getCountryName()) ? "" : bean.getCountryName()) +"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+df.format((bean.getCreateDate()==null) ? new Date() : bean.getCreateDate())+"</td></tr>");
		}
		
		sbuffer.append("</tbody></table></div><div><br /></div>");
		
		sbuffer.append("<div><font color='red' style='font-weight:bold'>游友移动</font></div><div><br /></div>");	

		return sbuffer.toString();
	}

	/**
	 * 
	 * @Description 卡预警
	 * @param validityList 卡有效期预警
	 * @param rateOfFlowList 卡流量预警
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年5月18日 上午11:49:20
	 */
	public static String cardEarlyWarning(List<Map<String, Object>> validityList, List<Map<String, Object>> rateOfFlowList) {
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		StringBuffer sbuffer = new StringBuffer();
		
		sbuffer.append("<style>.headTd {border-bottom: #ccc 1px solid; border-left: #ccc 1px solid; border-top: #ccc 1px solid; border-right: #ccc 1px solid}");
		sbuffer.append(".bodyTd {border-bottom: #ddd 1px solid; border-left: #ddd 1px solid; border-top: medium none; border-right: #ddd 1px solid}");
		sbuffer.append("</style>");
		
		// 卡有效期预警
		sbuffer.append("<div>卡有效期预警信息</div>");
		sbuffer.append("<div><table border='0' cellpadding='0' cellspacing='0' style='text-align: center; line-height: 1; border-spacing: 0; border-collapse: collapse; font-size: 13px' width='60%'>");
		sbuffer.append("<thead><tr><td height='28' width='140' class='headTd'>ICCID</td><td width='80' class='headTd'>SIMBACKID</td><td width='80' class='headTd'>SIMID</td><td width='100' class='headTd'>卡类型</td><td width='80' class='headTd'>有效天数</td><td width='120' class='headTd'>首次激活时间</td></tr></thead><tbody>");
		for (Map<String, Object> map : validityList) {
			sbuffer.append("<tr><td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(map.get("ICCID"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(map.get("SIMBANKID"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(map.get("SIMID"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(map.get("type"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(map.get("SIMCARDVALIDDAY"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+(StringUtils.isNotBlank(ObjectUtils.toString(map.get("FIRSTACTIVE"))) ? df.format(map.get("FIRSTACTIVE")) : "")+"</td></tr>");
		}
		sbuffer.append("</tbody></table></div><div><br /></div>");
		
		// 卡流量预警
		sbuffer.append("<div>流量预警信息</div>");
		sbuffer.append("<div><table border='0' cellpadding='0' cellspacing='0' style='text-align: center; line-height: 1; border-spacing: 0; border-collapse: collapse; font-size: 13px' width='60%'>");
		sbuffer.append("<thead><tr><td height='28' width='140' class='headTd'>ICCID</td><td width='80' class='headTd'>SIMBACKID</td><td width='80' class='headTd'>SIMID</td><td width='100' class='headTd'>卡类型</td><td width='120' class='headTd'>总流量(M)</td><td width='120' class='headTd'>已使用流量(M)</td></tr></thead><tbody>");
		for (Map<String, Object> map : rateOfFlowList) {
			sbuffer.append("<tr><td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(map.get("ICCID"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(map.get("SIMBANKID"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(map.get("SIMID"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(map.get("type"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(map.get("DATACAP"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(map.get("DATAUSED"))+"</td></tr>");
		}
		sbuffer.append("</tbody></table></div><div><br /></div>");
		
		sbuffer.append("<div><font color='red' style='font-weight:bold'>游友移动</font></div><div><br /></div>");	

		return sbuffer.toString();
	}

	/**
	 * 
	 * @Description 设备状态监控
	 * @param detailList
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年5月23日 下午1:41:36
	 */
	public static String deviceMonitor(List<DeviceMonitorDetail> detailList) {
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		StringBuffer sbuffer = new StringBuffer();
		
		sbuffer.append("<style>.headTd {border-bottom: #ccc 1px solid; border-left: #ccc 1px solid; border-top: #ccc 1px solid; border-right: #ccc 1px solid}");
		sbuffer.append(".bodyTd {border-bottom: #ddd 1px solid; border-left: #ddd 1px solid; border-top: medium none; border-right: #ddd 1px solid}");
		sbuffer.append("</style>");
		
		// 卡有效期预警
		sbuffer.append("<div>设备运行状态监控信息</div>");
		sbuffer.append("<div><table border='0' cellpadding='0' cellspacing='0' style='text-align: center; line-height: 1; border-spacing: 0; border-collapse: collapse; font-size: 13px' width='90%'>");
		sbuffer.append("<thead><tr><td height='28' width='120' class='headTd'>设备编码</td><td width='120' class='headTd'>上一记录(状态[RSSI_9215,RSSI_6200])</td><td width='120' class='headTd'>上一记录发生时间</td><td width='120' class='headTd'>下一记录(状态[RSSI_9215,RSSI_6200])</td><td width='120' class='headTd'>下一记录发生时间</td><td width='120' class='headTd'>国家中文名</td><td width='120' class='headTd'>国家英文名</td></tr></thead><tbody>");
		for (DeviceMonitorDetail detail : detailList) {
			sbuffer.append("<tr><td align='middle' height='30' valign='center' class='bodyTd'>"+detail.getImei()+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+detail.getPreStatus()+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+df.format(detail.getPreHappenDate())+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+detail.getNextStatus()+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+df.format(detail.getNextHappenDate())+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+detail.getCountryName()+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+detail.getCountryNameEn()+"</td></tr>");
		}
		sbuffer.append("</tbody></table></div><div><br /></div>");
		
		sbuffer.append("<div><font color='red' style='font-weight:bold'>游友移动</font></div><div><br /></div>");	

		return sbuffer.toString();
	}

	/**
	 * 
	 * @Description 设备状态统计
	 * @param detailList
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年5月23日 下午1:41:36
	 */
	public static String deviceCount(List<Map<String, Object>> result) {
		
		StringBuffer sbuffer = new StringBuffer();
		
		sbuffer.append("<style>.headTd {border-bottom: #ccc 1px solid; border-left: #ccc 1px solid; border-top: #ccc 1px solid; border-right: #ccc 1px solid}");
		sbuffer.append(".bodyTd {border-bottom: #ddd 1px solid; border-left: #ddd 1px solid; border-top: medium none; border-right: #ddd 1px solid}");
		sbuffer.append("</style>");
		
		// 卡有效期预警
		sbuffer.append("<div>设备运行状态统计信息</div>");
		sbuffer.append("<div><table border='0' cellpadding='0' cellspacing='0' style='text-align: center; line-height: 1; border-spacing: 0; border-collapse: collapse; font-size: 13px' width='100%'>");
		sbuffer.append("<thead><tr><td height='28' width='180' class='headTd'>国家</td><td width='80' class='headTd'>使用流量(M)</td><td width='100' class='headTd'>联网时长</td><td width='100' class='headTd'>链接终端数</td><td width='140' class='headTd'>设备编码</td><td width='140' class='headTd'>关机时间</td><td width='400' class='headTd'>异常信息(设备状态[RSSI_9215,RSSI_6200][发生时间]~设备状态[RSSI_9215,RSSI_6200][发生时间])</td></tr></thead><tbody>");
		for (Map<String, Object> detail : result) {
			sbuffer.append("<tr><td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(detail.get("country"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(detail.get("dataTotal"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(detail.get("onlineTime"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(detail.get("devices"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(detail.get("imei"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(detail.get("shutDownTime"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(detail.get("warningInfo"))+"</td></tr>");
		}
		sbuffer.append("</tbody></table></div><div><br /></div>");
		
		sbuffer.append("<div><font color='red' style='font-weight:bold'>游友移动</font></div><div><br /></div>");	

		return sbuffer.toString();
	}

	/**
	 * 
	 * @Description 设备状态统计
	 * @param detailList
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年5月23日 下午1:41:36
	 */
	public static String deviceCountForChannel(List<Map<String, Object>> result) {
		
		StringBuffer sbuffer = new StringBuffer();
		
		sbuffer.append("<style>.headTd {border-bottom: #ccc 1px solid; border-left: #ccc 1px solid; border-top: #ccc 1px solid; border-right: #ccc 1px solid}");
		sbuffer.append(".bodyTd {border-bottom: #ddd 1px solid; border-left: #ddd 1px solid; border-top: medium none; border-right: #ddd 1px solid}");
		sbuffer.append("</style>");
		
		// 卡有效期预警
		sbuffer.append("<div>设备运行状态统计信息</div>");
		sbuffer.append("<div><table border='0' cellpadding='0' cellspacing='0' style='text-align: center; line-height: 1; border-spacing: 0; border-collapse: collapse; font-size: 13px' width='60%'>");
		sbuffer.append("<thead><tr><td height='28' width='180' class='headTd'>国家</td><td width='80' class='headTd'>使用流量(M)</td><td width='100' class='headTd'>联网时长</td><td width='140' class='headTd'>设备编码</td><td width='140' class='headTd'>关机时间</td></tr></thead><tbody>");
		for (Map<String, Object> detail : result) {
			sbuffer.append("<tr><td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(detail.get("country"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(detail.get("dataTotal"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(detail.get("onlineTime"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(detail.get("imei"))+"</td>"
							+ "<td align='middle' height='30' valign='center' class='bodyTd'>"+ObjectUtils.toString(detail.get("shutDownTime"))+"</td></tr>");
		}
		sbuffer.append("</tbody></table></div><div><br /></div>");
		
		sbuffer.append("<div><font color='red' style='font-weight:bold'>游友移动</font></div><div><br /></div>");	

		return sbuffer.toString();
	}
	
}
