/** 
 * @Package com.uu.common.scheduled 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月15日 下午2:11:29 
 * @version V1.0 
 */
package main.java.com.qlink.common.scheduled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.uu.common.utils.MailBody;
import com.uu.common.utils.mail.MailThread;
import com.uu.common.utils.phone.PhoneMessageBody;
import com.uu.common.utils.phone.PhoneMessageThread;
import com.uu.modules.mifi.condition.CardMonitorCondition;
import com.uu.modules.mifi.entity.CardMonitor;
import com.uu.modules.mifi.service.CardMonitorService;
import com.uu.modules.sys.condition.NoticeReceiveCondition;
import com.uu.modules.sys.entity.NoticeReceive;
import com.uu.modules.sys.service.NoticeReceiveService;
import com.uu.modules.utils.Constants;

/**
 * @Description 卡监控信息 定时处理
 * @author yifang.huang 
 * @date 2016年4月15日 下午2:11:29
 */
@Component
public class CardMonitorScheduled {

	public static Logger logger = LoggerFactory.getLogger(CardMonitorScheduled.class);

	@Autowired
	private CardMonitorService cardMonitorService;

	@Autowired
	private NoticeReceiveService noticeReceiveService;
	
	// @Scheduled(cron = "0 0/10 * * * ?")//秒、分、时、日、月、年
	@Scheduled(fixedRate = 5 * 60 * 1000)
	// 五分钟扫一次
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void monitor() {

		logger.info("卡监控定时处理开始！");
		
		try {
			CardMonitorCondition condition = new CardMonitorCondition();
			condition.setEqStatus("NEW");
			List<CardMonitor> list = cardMonitorService.findListByCondition(condition);
			if (list != null && list.size() > 0) {
				// 完善国家信息
				setCountry(list);				

				// 修改状态为（SENT_MSG）
				//if (success) // 有卡监控通知接收配置，就为信息发送成功
				setStatus(list);
				
				logger.info("卡监控信息 定时处理,异常记录[" + list.size() + "]条!");
				
				// 阿联酋  菲律宾  印尼的无可用卡报警取消[暂时]
				removeBean(list);

				// 发信息
				sendMessage(list);
			}
		} catch (Exception e) {
			logger.info("卡监控信息 定时处理失败！");
			e.printStackTrace();
		}

		logger.info("卡监控定时处理结束！");

	}

	/**
	 * 
	 * @Description 发信息
	 * @return void
	 * @author wangsai
	 * @date 2016-5-7
	 */
	private boolean sendMessage(List<CardMonitor> cmList) {
		// 卡监控信息接收
		NoticeReceiveCondition condition = new NoticeReceiveCondition();
		condition.setEqType("CARD_MONITOR");
		List<NoticeReceive> list = noticeReceiveService.findListByCondition(condition);
		if (list != null && list.size() > 0) {
			List<CardMonitor> temp = null;
			for (NoticeReceive nr : list) {
				if (Constants.CHANNEL_DEFAULT_VALUE.equals(nr.getSourceType())) {	// YOUYOUMOB
					
					temp = new ArrayList<CardMonitor>();
					
					if ("ALL".equals(nr.getFaultCodes())) {
						temp = cmList;
					} else {
						String[] faultCodeArr = nr.getFaultCodes().split(",");
						for (CardMonitor monitor : cmList) {
							if (include(faultCodeArr, monitor.getFaultCode()))
								temp.add(monitor);
						}
					}
					
					if (temp.size() > 0) {
						// 手机短信发送
						String mobile = nr.getPhones(); // 手机号码
						if (StringUtils.isNotBlank(mobile)) {
							String content = PhoneMessageBody.cardMonitor(temp);	// 短信内容
							PhoneMessageThread pmThread = new PhoneMessageThread(mobile, content);
							pmThread.start();
						}
			
						// 邮件发送
						String emails = nr.getEmails(); // 系统配置邮箱
						if (StringUtils.isNotBlank(emails)) {
							String msgText = MailBody.cardMonitor(temp);	// 邮件内容
							MailThread mThread = new MailThread("卡监控定时处理", "[游友移动]", msgText, emails);
							mThread.start();
						}
					}
					
					temp = null;
				} else { // 其它渠道
					sendMailToChannel(cmList, nr);
				}
			}

		}

		return false;
	}

	/**
	 * 
	 * @Description 完善国家信息
	 * @param list
	 * @return void
	 * @author yifang.huang
	 * @date 2016年4月15日 下午5:43:43
	 */
	private void setCountry(List<CardMonitor> list) {

		HashMap<String, String> map = null; // 国家信息
		for (CardMonitor bean : list) {
			map = cardMonitorService.findCountryByMcc(bean.getMcc());
			if (map != null) {

				bean.setCountryCode(map.get("countryCode"));
				bean.setCountryName(map.get("countryName"));
				cardMonitorService.save(bean);

				map = null;
			}
		}
	}

	/**
	 * 
	 * @Description 设置状态为SENT_MSG
	 * @param list
	 * @return void
	 * @author yifang.huang
	 * @date 2016年4月28日 下午3:56:42
	 */
	private void setStatus(List<CardMonitor> list) {
		for (CardMonitor bean : list) {
			bean.setStatus("SENT_MSG");
			cardMonitorService.save(bean);
		}
	}

	/**
	 * 
	 * @Description 给渠道商发送指定故障编码的信息邮箱
	 * @param list
	 * @param nr 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年12月9日 下午4:53:52
	 */
	private void sendMailToChannel(List<CardMonitor> list, NoticeReceive nr) {
		
		String sourceType = nr.getSourceType();
		String faultCodes = nr.getFaultCodes();
		String[] faultCodeArr = "ALL".equals(faultCodes) ? null : faultCodes.split(",");

		List<CardMonitor> temp = new ArrayList<CardMonitor>();
		for (CardMonitor monitor : list) {
			if ("ALL".equals(faultCodes)) {
				if (sourceType.equals(monitor.getSourceType()))
					temp.add(monitor);
			} else {
				if (sourceType.equals(monitor.getSourceType()) && include(faultCodeArr, monitor.getFaultCode()))
					temp.add(monitor);
			}
		}
		
		if (temp.size() > 0) {
			// 手机短信发送
			String mobile = nr.getPhones(); // 手机号码
			if (StringUtils.isNotBlank(mobile)) {
				String content = PhoneMessageBody.cardMonitor(temp);	// 短信内容
				PhoneMessageThread pmThread = new PhoneMessageThread(mobile, content);
				pmThread.start();
			}
	
			// 邮件发送
			String emails = nr.getEmails(); // 系统配置邮箱
			if (StringUtils.isNotBlank(emails)) {
				String msgText = MailBody.cardMonitor(temp);	// 邮件内容
				MailThread mThread = new MailThread("监控定时处理", "[游友移动]", msgText, emails);
				mThread.start();
			}
		}
		
	}
	
	private boolean include(String[] faultCodeArr, String faultCode) {
		
		if (faultCodeArr == null)
			return false;
		
		for (String str : faultCodeArr) {
			if (str.equals(faultCode))
				return true;
		}
		
		return false;
	}
	
	// 阿联酋  菲律宾  印尼的无可用卡报警取消
	private void removeBean(List<CardMonitor> list) {
		String mcces = "424,430,431,515,510";
		String[] mccArr = mcces.split(",");
		String code = "F3";
		List<CardMonitor> temp = new ArrayList<CardMonitor>();
		for (CardMonitor bean : list) {
			if (code.equals(bean.getFaultCode()) && include(mccArr, bean.getMcc()))
				temp.add(bean);
		}
		
		if (temp.size() > 0)
			list.removeAll(temp);
	}
}
