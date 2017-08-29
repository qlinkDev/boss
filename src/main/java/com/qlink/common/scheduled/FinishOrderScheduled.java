/** 
 * @Package com.uu.common.scheduled 
 * @Description 
 * @author yifang.huang
 * @date 2016年11月11日 下午3:55:53
 * @version V1.0 
 */ 
package main.java.com.qlink.common.scheduled;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

import com.uu.common.utils.thread.VodafoneThread;
import com.uu.modules.mifi.service.MifiManageService;
import com.uu.modules.mifi.service.MifiOrderService;
import com.uu.modules.sys.utils.DictUtils;
import com.uu.modules.utils.Constants;

/**
 * 
 * @Description 完成订单 定时处理
 * @author yifang.huang
 * @date 2016年11月11日 下午3:55:53
 */
@Component
public class FinishOrderScheduled {
	
	public static Logger logger = LoggerFactory.getLogger(FinishOrderScheduled.class);

	@Autowired
	private MifiOrderService orderService;
	
	@Autowired
	private MifiManageService mifiManageService;
	
	// 每天凌晨过一刻执行一次
	@Scheduled(cron = "0 01 0 * * ?")//秒、分、时、日、月、年
	//@Scheduled(fixedRate = 5 * 60 * 1000)//1个小时钟扫一次
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void finishOrder(){
		
		logger.info("完成订单定时处理开始！");
		
		// 如果执行时间是2016-11-11 00:15:00，则startDate=2016-11-10 00:00:00，endDate=2016-11-11 00:00:00
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		String startDate = df.format(calendar.getTime()) + " 00:00:00";
		String endDate = df.format(new Date()) + " 00:00:00";
	
		List<Map<String, Object>> listMap = orderService.getFinishOrderList(startDate, endDate);
		
		// 执行
		Map<String, String> map = orderService.updateFinishOrder(startDate, endDate);
		String status = map.get("status");
		if ("success".equals(status))
			logger.info("完成订单定时调试执行成功,修改订单数：{},修改设备数：{},删除设备卡绑定关系记录数：{}", map.get("orderCount"), map.get("imeiCount"), map.get("testCount"));
		else
			logger.info("完成订单定时调试执行失败,原因：{}", map.get("message"));
		
		logger.info("完成订单定时处理结束！");
		
		String iccIds = "";
		try {
			// 塞班关岛，马代，马来西亚，澳门，澳洲，印度的订单对应设备副卡订单开始时已经升到5，订单结束进需要降级到3
			String mccForViceCard5 = DictUtils.getDictValue(Constants.DICT_MCC_FOR_VODAFONE_CARD_5_LABEL, Constants.DICT_MCC_FOR_VODAFONE_CARD_5_TYPE, "");
			if (StringUtils.isNotBlank(mccForViceCard5)) {
				if (listMap!=null && listMap.size()>0) {
					String mcces = null;
					String id = null;
					String imeis = null;
					Map<String, String> mifiMap = null;
					// 遍历开始的订单列表
					for (Map<String, Object> tempMap : listMap) {
						mcces = ObjectUtils.toString(tempMap.get("mcces"));
						// 订单allowed_mcc不为空
						if (StringUtils.isNotBlank(mcces)) {
							// 订单allowed_mcc包含需要副卡升级的国家mcc
							if (com.uu.common.utils.StringUtils.mccInclude(mcces, mccForViceCard5)) {
								id = ObjectUtils.toString(tempMap.get("id"));
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
				VodafoneThread thread = new VodafoneThread(iccIds.substring(1), Constants.VODAFONE_CARD_LEVEL_3);
				thread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Vodafone卡降级失败！");
		}
		
	}
	
}
