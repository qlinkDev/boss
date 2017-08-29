/** 
 * @Package com.uu.common.scheduled 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月19日 上午9:51:04 
 * @version V1.0 
 */ 
package main.java.com.qlink.common.scheduled;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import com.uu.common.utils.Socekt;
import com.uu.modules.mifi.service.CardManageService;

/**
 * 
 * @Description 卡过期定时处理
 * @author yifang.huang
 * @date 2016年9月20日 上午10:19:56
 */
public class SimNodeOverdueScheduled {
	
	public static Logger logger = LoggerFactory.getLogger(SimNodeOverdueScheduled.class);

	@Autowired
	private CardManageService cardManageService;
	
	// 每个小时第5分钟执行一次
	@Scheduled(cron = "0 0 0 * * ?")//秒、分、时、日、月、年
	//@Scheduled(fixedRate = 5 * 60 * 1000)//1个小时钟扫一次
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void simNodeOverdue(){
		
		logger.info("卡过期定时处理定时处理开始！");
	
		List<Map<String, Object>> listMap = cardManageService.findOverdueCard();
		if (listMap!=null && listMap.size()>0) {
			Socekt.simStatusBatchController(listMap, 4, "simNodeOverdue(SimNodeOverdueScheduled)");
		}
		
		logger.info("卡过期定时处理定时处理结束！");
		
	}
	
}
