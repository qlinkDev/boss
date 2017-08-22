package com.uu.common.utils.phone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.uu.common.utils.StringUtils;
import com.uu.modules.mifi.entity.CardMonitor;
import com.uu.modules.sys.utils.DictUtils;

/**
 * 
 * @Description 手机短信内容工具类
 * @author yifang.huang
 * @date 2016年10月18日 下午2:07:24
 */
public class PhoneMessageBody {

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
		StringBuffer msgText = new StringBuffer("有[" + list.size() + "]条用户设备开机不能正常使用记录，请尽快处理。{");

		for (CardMonitor monitor : list) {
			msgText.append("["
					+ DictUtils.getDictLabel(monitor.getFaultCode(), "card_monitor_fault_code", null)
					+ ","
					+ monitor.getImei()
					+ ","
					+ (StringUtils.isBlank(monitor.getCountryName()) ? "" : monitor.getCountryName())
					+ ","
					+ df.format((monitor.getCreateDate() == null) ? new Date() : monitor.getCreateDate()) + "]");
		}

		msgText.append("}【游友移动】");

		return msgText.toString();
	}

}
