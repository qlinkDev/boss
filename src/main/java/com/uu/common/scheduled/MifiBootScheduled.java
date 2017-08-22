/** 
 * @Package com.uu.common.scheduled 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月15日 下午2:11:29 
 * @version V1.0 
 */
package com.uu.common.scheduled;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.uu.common.utils.HttpRequest;
import com.uu.modules.mifi.service.MifiBootService;
import com.uu.modules.sys.utils.DictUtils;
import com.uu.modules.user.condition.DayPassRecordCondition;
import com.uu.modules.user.entity.DayPassRecord;
import com.uu.modules.user.service.DayPassRecordService;
import com.uu.modules.utils.Constants;

/**
 * @Description MIFI开机(一天一条MIFI状态为4记录),用户消费记录回调渠道商接口通知    定时处理
 * @author yifang.huang 
 * @date 2016年4月15日 下午2:11:29
 */
@Component
public class MifiBootScheduled {

	public static Logger logger = LoggerFactory.getLogger(MifiBootScheduled.class);

	@Autowired
	private MifiBootService mifiBootService;
	
	@Autowired
	private DayPassRecordService dayPassRecordService;

	// @Scheduled(cron = "0 0/10 * * * ?")//秒、分、时、日、月、年
	@Scheduled(fixedRate = 15 * 60 * 1000)
	// 十五分钟扫一次
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void monitor() {

		logger.info("MIFI开机(一天一条MIFI状态为4记录)定时处理开始！");
		
		try {
			
			// 扫描MifiBoot,处理status=NEW的记录
			mifiBootService.saveAndHandleMifiBoot();
			
			// 扫描DayPassRecord,处理一个小时内消费记录status=FAIL（通知状态）
			handleDayPassRecord();
			
		} catch (Exception e) {
			logger.info("MIFI开机(一天一条MIFI状态为4记录)定时处理失败！");
			e.printStackTrace();
		}

		logger.info("MIFI开机(一天一条MIFI状态为4记录)定时处理结束！");

	}

	/**
	 * 
	 * @Description DayPassRecord处理
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年12月7日 下午4:41:18
	 */
	private synchronized void handleDayPassRecord() {
		// 查询一个小时内状态为'FAIL'的DayPassRecord消费记录
		DayPassRecordCondition condition = new DayPassRecordCondition(true);
		condition.setEqType(DayPassRecord.DAY_PASS_RECORD_CONSUME);
		condition.setEqStatus(Constants.CONSTANTS_STATUS_FAIL);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, -1);
		condition.setGeFullCreateDate(c.getTime());
		List<DayPassRecord> recordList = dayPassRecordService.findList(condition);
		
		if (recordList!=null && recordList.size()>0) {
			// 渠道商回调接口地址
			String callbackUrl = null;
			String result = "";
			JSONObject json = null;
			for (DayPassRecord record : recordList) {
				
				callbackUrl = DictUtils.getDictValue(record.getSourceType() + "_" + Constants.DICT_CALLBACK_URL_LABEL, record.getSourceType() + "_" + Constants.DICT_CALLBACK_URL_TYPE, "https://www.geefi.co/members/api/notifyDaypassUsage.php");
				logger.info("DayPassRecord回调地址:" + callbackUrl);
				
				StringBuffer param = new StringBuffer("mifiId=" + record.getImei());
				param.append("&mcc=" + record.getMcc());
				param.append("&uuid=" + record.getId());
				param.append("&loginName=" + record.getLoginName());
				param.append("&countryName=" + record.getCountryNameEn());
				
				result = HttpRequest.sendURLPost(callbackUrl, param.toString());
				logger.info("DayPassRecord[" + record.getId() + "],回调结果:" + result);
				
				json = new JSONObject(result); 
				if ("200".equals(json.get("code"))) { // 如果回调成功，将状态修改成'已处理'
					record.setStatus(Constants.CONSTANTS_STATUS_SUCCESS);
				}
				record.setCallbackDate(new Date());
				dayPassRecordService.save(record);

				callbackUrl = null;
				result = "";
				json = null;
			}
		}
	}
	
}
