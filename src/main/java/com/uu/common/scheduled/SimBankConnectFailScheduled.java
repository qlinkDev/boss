/** 
 * @Package com.uu.common.scheduled 
 * @Description 
 * @author yifang.huang
 * @date 2017年1月12日 上午11:48:16 
 * @version V1.0 
 */ 
package com.uu.common.scheduled;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.uu.common.utils.IdGen;
import com.uu.modules.mifi.entity.CardMonitor;
import com.uu.modules.mifi.service.CardMonitorService;

/** 
 * @Description 卡箱与服务器连接失败  定时处理
 * @author yifang.huang
 * @date 2017年1月12日 上午11:48:16 
 */
@Component
public class SimBankConnectFailScheduled {

	public static Logger logger = LoggerFactory.getLogger(CardMonitorScheduled.class);
	
	@Autowired
	private CardMonitorService cardMonitorService;
	
	// @Scheduled(cron = "0 0/10 * * * ?")//秒、分、时、日、月、年
	@Scheduled(fixedRate = 30 * 60 * 1000)
	// 30分钟扫一次
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void connectFail() {

		logger.info("卡箱与服务器连接失败定时处理开始！");
		
		// 取一个小时前到半个小时前时间段内，卡箱与服务器断开连接的最新的一条监控记录
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR, -1);
		Date startDate = c.getTime(); 	// 一小时前
		c = Calendar.getInstance();
		c.add(Calendar.MINUTE, -30);
		Date endDate = c.getTime();		// 半小时前
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 错误编码为'SimbankClose'的数据列表
		List<Map<String, String>> mapList = cardMonitorService.findList(null, "SimbankClose", df.format(startDate), df.format(endDate));
		
		// 循环记录，如果没有找到对应的卡箱与服务器连接成功记录，则添加一条卡箱与服务器连接失败的监控记录
		if (mapList!=null && mapList.size()>0) {
			String imei = null;
			String createDate = null;
			List<Map<String, String>> tempList = null;
			CardMonitor monitor = null;
			for (Map<String, String> map : mapList) {
				imei = map.get("imei");
				createDate = map.get("createDate");
				tempList = cardMonitorService.findList(imei, "SimBankConnect", createDate, null);
				// 如果没有当前卡箱与服务器连接成功的记录，则写一条卡箱与服务器连接失败记录
				if (tempList==null || tempList.size()==0) {
					monitor = new CardMonitor();
					monitor.setId(IdGen.uuid());
					monitor.setImei(imei);
					monitor.setStatus("NEW");
					monitor.setFaultCode("SimBankConnectFail");
					monitor.setType("1");
					monitor.setCreateDate(new Date());
					cardMonitorService.save(monitor);
						
					monitor = null;
					logger.info("卡箱[{}]未在规定时间内恢复,断开连接的时间[{}]", imei, createDate);
				}
				
				imei = null;
				createDate = null;
				tempList = null;
			}
		}
		
		logger.info("卡箱与服务器连接失败定时处理结束！");
		
	}
	
}
