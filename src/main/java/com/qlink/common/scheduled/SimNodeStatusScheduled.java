/** 
 * @Package com.uu.common.scheduled 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月19日 上午9:51:04 
 * @version V1.0 
 */ 
package com.qlink.common.scheduled;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import com.qlink.common.utils.Socekt;
import com.qlink.modules.mifi.service.CardManageService;
import com.qlink.modules.sys.utils.DictUtils;
import com.qlink.modules.utils.Constants;

/**
 * 
 * @Description 卡状态修改定时调度
 * @author yifang.huang
 * @date 2016年9月19日 上午11:19:10
 */
public class SimNodeStatusScheduled {
	
	public static Logger logger = LoggerFactory.getLogger(SimNodeStatusScheduled.class);

	@Autowired
	private CardManageService cardManageService;
	
	// 每个小时第5分钟执行一次
	@Scheduled(cron = "0 05 * * * ?")//秒、分、时、日、月、年
	//@Scheduled(fixedRate = 5 * 60 * 1000)//1个小时钟扫一次
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void simNodeStatus(){
		
		logger.info("SimNode状态修改定时处理开始！");
		
		try {
			statusUpdateOne();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			statusUpdateTwo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			statusUpdateThree();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logger.info("SimNode状态修改定时处理结束！");
		
	}
	
	// keepGo卡，状态为6不超流量改为2
	private void statusUpdateOne() {
		
		List<Map<String, Object>> listMap = cardManageService.findSimNode("EU-KG-1", 6, false);
		if (listMap!=null && listMap.size()>0) {
			Socekt.simStatusBatchController(listMap, 2, "statusUpdateOne(SimNodeStatusScheduled)");
		}
	}
	
	// 1.simNode中状态为3的卡，通过ueId，simbankId，simId这3个字段到mifiNode中去匹配，没匹配到的把状态改为2
	// 2.simNode表中stamp_updated 3小时没变，状态又为3的话，就回收，把这张卡改为2
	private void statusUpdateTwo() {
		
		List<Map<String, Object>> listMap = cardManageService.findSimNode(null, 3, null);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (listMap!=null && listMap.size()>0) {
			for (Map<String, Object> map : listMap) {
				try {
					String ueId = ObjectUtils.toString(map.get("UEID"));
					Integer simBankId = Integer.valueOf(ObjectUtils.toString(map.get("SIMBANKID")));
					Integer simId = Integer.valueOf(ObjectUtils.toString(map.get("SIMID")));
					
					// 判断mifiNode中是否有数据，有数据则不通信
					List<Map<String, Object>> tempMap = cardManageService.findMifiNode(ueId, simBankId, simId);
					if (tempMap!=null && tempMap.size()>0)
						continue;
					
					result.add(map);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		// 回收状态为3且stamp_updated为三个小时之前
		String simNodeStampUpdatedStr = DictUtils.getDictValue(Constants.DICT_SIMNODE_STAMP_UPDATED_LABEL, Constants.DICT_SIMNODE_STAMP_UPDATED_TYPE, "3");
		int simNodeStampUpdated = Integer.valueOf(simNodeStampUpdatedStr);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, -simNodeStampUpdated);
		List<Map<String, Object>> temp = cardManageService.findSimNode(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime()));
		if (temp!=null && temp.size()>0)
			result.addAll(temp);
		
		// 对结果通信
		if (result.size()>0) {
			Socekt.simStatusBatchController(result, 2, "statusUpdateTwo(SimNodeStatusScheduled)");
		}
		
	}
	
	// 长期卡超流量状态制成6，短期卡超流量的没有重置时间的制成4、有重置时间制成6，
	private void statusUpdateThree() {

		// 需要将状态制成6的卡集合
		List<Map<String, Object>> cardSixList = new ArrayList<Map<String, Object>>();
		// 需要将状态制成4的卡集合
		List<Map<String, Object>> cardFourList = new ArrayList<Map<String, Object>>();
		
		// 长期卡、超流量状态制成6
		List<Map<String, Object>> cardList = cardManageService.findSimNode(true, true, null);
		if (cardList!=null && cardList.size()>0)
			cardSixList.addAll(cardList);
		
		// 短期卡、超流量、有重置时间状态制成6
		cardList = cardManageService.findSimNode(true, false, true);
		if (cardList!=null && cardList.size()>0)
			cardSixList.addAll(cardList);
		
		// 短期卡、超流量、没有重置时间状态制成4
		cardFourList = cardManageService.findSimNode(true, false, false);
		
		// 对结果通信
		if (cardSixList.size() > 0) {
			Socekt.simStatusBatchController(cardSixList, 6, "statusUpdateThree(SimNodeStatusScheduled)");
		}
		if (cardFourList.size() > 0) {
			Socekt.simStatusBatchController(cardFourList, 4, "statusUpdateThree(SimNodeStatusScheduled)");
		}
			
		
	}
	
}
