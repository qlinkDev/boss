/** 
 * @Package com.uu.common.utils.thread 
 * @Description 
 * @author yifang.huang
 * @date 2017年2月24日 上午10:45:07 
 * @version V1.0 
 */ 
package com.qlink.common.utils.thread;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qlink.common.utils.VodafoneUtils;

/** 
 * @Description Vodafone级别修改 线程
 * @author yifang.huang
 * @date 2017年2月24日 上午10:45:07 
 */
public class VodafoneThread extends Thread {
	
	public static Logger logger = LoggerFactory.getLogger(VodafoneThread.class);
	
	private String iccIds;				// 需要修改的设备VFICCID
	
	private String level;				// 修改到级别
	
	public VodafoneThread(String iccIds, String level) {
		super();
		this.iccIds = iccIds;
		this.level = level;
	}

	public void run() {
		if (StringUtils.isNotBlank(iccIds)) {
			String[] iccIdArr = iccIds.split(",");
			logger.info("Vodafone级别修改线程开始：{},设备数量：{}", level, iccIdArr.length);
			for (String iccId : iccIdArr) {
				logger.info("设备["+iccId+"]Vodafone卡级别更新到:" + level);
				VodafoneUtils.setDeviceDetails(iccId, level);
			}
			logger.info("Vodafone级别修改线程结束：" + level);
		}
	}  
	
}
