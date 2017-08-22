/** 
 * @Package com.uu.common.scheduled 
 * @Description 
 * @author yifang.huang
 * @date 2017年2月23日 下午2:36:00 
 * @version V1.0 
 */ 
package com.uu.common.scheduled;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.uu.common.utils.DateUtils;
import com.uu.common.utils.thread.VodafoneThread;
import com.uu.modules.mifi.service.MifiManageService;
import com.uu.modules.mifi.service.MifiOrderService;
import com.uu.modules.sys.utils.DictUtils;
import com.uu.modules.utils.Constants;

/** 
 * @Description 开始订单 定时处理
 * @author yifang.huang
 * @date 2017年2月23日 下午2:36:00 
 */
@Component
public class StartOrderScheduled {
	
	public static Logger logger = LoggerFactory.getLogger(StartOrderScheduled.class);

	@Autowired
	private MifiOrderService orderService;
	
	@Autowired
	private MifiManageService mifiManageService;
	
	//@Scheduled(fixedRate = 5 * 60 * 1000)		// 30分钟执行一次
	@Scheduled(cron = "0 30 0 * * ?")           // 每天凌晨30执行一次
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void startOrder(){
		
		logger.info("开始订单定时处理开始！");
		
		// 如果执行时间是2016-11-11 00:30:00，则date=2016-11-11 00:00:00
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = df.format(DateUtils.getDateStart(new Date()));
	
		try {
			// 执行开始订单
			Map<String, String> map = orderService.updateStartOrder(date);
			String status = map.get("status");
			if ("success".equals(status))
				logger.info("开始订单定时调试执行成功,修改订单数：{},修改设备数：{}", map.get("orderCount"), map.get("imeiCount"));
			else
				logger.info("开始订单定时调试执行失败,原因：{}", map.get("message"));
		} catch (Exception e) {
			logger.info("开始订单定时调试执行失败");
			e.printStackTrace();
		}
		logger.info("开始订单定时处理结束！");

		String iccIds = "";
		try {
			// 塞班关岛，马代，马来西亚，澳门，澳洲，印度的订单对应设备副卡升到5
			String mccForViceCard5 = DictUtils.getDictValue(Constants.DICT_MCC_FOR_VODAFONE_CARD_5_LABEL, Constants.DICT_MCC_FOR_VODAFONE_CARD_5_TYPE, "");
			if (StringUtils.isNotBlank(mccForViceCard5)) {
				List<Map<String, Object>> listMap = orderService.getStartOrderList(date);
				if (listMap!=null && listMap.size()>0) {
					String mcces = null;
					String id = null;
					String imeis = null;
					Map<String, String> mifiMap = null;
					// 遍历开始的订单列表
					for (Map<String, Object> map : listMap) {
						mcces = ObjectUtils.toString(map.get("mcces"));
						// 订单allowed_mcc不为空
						if (StringUtils.isNotBlank(mcces)) {
							// 订单allowed_mcc包含需要副卡升级的国家mcc
							if (com.uu.common.utils.StringUtils.mccInclude(mcces, mccForViceCard5)) {
								id = ObjectUtils.toString(map.get("id"));
								imeis = orderService.getDsnByOrderId(id);
								if (StringUtils.isNotBlank(imeis)) {
									for (String imei : imeis.split(",")) {
										mifiMap = mifiManageService.getMifilistBySn(imei);
										if (mifiMap != null) {
											if (StringUtils.isNotBlank(mifiMap.get("vfIccId"))) {
												iccIds += "," + mifiMap.get("vfIccId");
											}
											mifiMap = null;
										}
									}
									imeis = null;
								}
								id =  null;
							}
							mcces = null;
						}
					}
				}
			}
			if (StringUtils.isNotBlank(iccIds)) {
				VodafoneThread thread = new VodafoneThread(iccIds.substring(1), Constants.VODAFONE_CARD_LEVEL_5);
				thread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Vodafone卡升级失败！");
		}
		
	}
	
}
