/** 
 * @Package com.uu.common.scheduled 
 * @Description 
 * @author yifang.huang
 * @date 2016年5月18日 上午9:32:14
 * @version V1.0 
 */
package main.java.com.qlink.common.scheduled;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.uu.common.utils.MailBody;
import com.uu.common.utils.mail.MailThread;
import com.uu.modules.mifi.service.CardManageService;
import com.uu.modules.sys.condition.NoticeReceiveCondition;
import com.uu.modules.sys.entity.NoticeReceive;
import com.uu.modules.sys.service.NoticeReceiveService;
import com.uu.modules.sys.utils.DictUtils;
import com.uu.modules.utils.Constants;

/**
 * 
 * @Description 卡预警 定时处理
 * @author yifang.huang
 * @date 2016年5月18日 上午9:32:14
 */
@Component
public class CardEarlyWarningScheduled {

	public static Logger logger = LoggerFactory.getLogger(CardEarlyWarningScheduled.class);

	@Autowired
	private CardManageService cardManageService;
	
	@Autowired
	private NoticeReceiveService noticeReceiveService;

	@Scheduled(cron = "0 0 01 * * ?")//秒、分、时、日、月、年
	//@Scheduled(fixedRate = 1 * 60 * 1000)
	// 凌晨一点执行
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void monitor() {

		logger.info("卡预警定时处理开始！");
		
		// 有效期卡到期预警
		List<Map<String, Object>> validityList = cardManageService.findListForValidityEarlyWarning();
		
		// 剩余流量小于等500M（参数配置）时预警
		String limitData = DictUtils.getDictValue(Constants.DICT_LIMIT_DATA_LABEL, Constants.DICT_LIMIT_DATA_TYPE, "524288000");
		List<Map<String, Object>> rateOfFlowList = cardManageService.findListForRateOfFlowEarlyWarning(Long.valueOf(limitData));
		
		// 发送信息
		sendMessage(validityList, rateOfFlowList);
		
		logger.info("卡预警定时处理结束！");

	}

	/**
	 * 
	 * @Description 发送信息
	 * @param validityList
	 * @param rateOfFlowList 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年5月18日 上午11:44:56
	 */
	private void sendMessage(List<Map<String, Object>> validityList, List<Map<String, Object>> rateOfFlowList) {
		
		if ((validityList==null||validityList.size()==0) && (rateOfFlowList==null||rateOfFlowList.size()==0))
			return;
		
		// 卡监控信息接收
		NoticeReceiveCondition condition = new NoticeReceiveCondition();
		condition.setEqType("CARD_EARLY_WARNING");
		List<NoticeReceive> list = noticeReceiveService.findListByCondition(condition);
		if (list != null && list.size() > 0) {
			
			NoticeReceive nr = list.get(0);
			// 邮件发送
			String emails = nr.getEmails();// 系统配置邮箱
			if (StringUtils.isNotBlank(emails)) {
				String msgText = MailBody.cardEarlyWarning(validityList, rateOfFlowList);	// 邮件内容
				MailThread mThread = new MailThread("卡预警信息", "[游友移动]", msgText, emails);
				mThread.start();
			}
			
		}
	}

}
