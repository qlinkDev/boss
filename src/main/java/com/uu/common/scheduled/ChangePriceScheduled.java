/** 
 * @Package com.uu.common.scheduled 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月19日 上午9:51:04 
 * @version V1.0 
 */ 
package com.uu.common.scheduled;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import com.uu.modules.om.condition.PriceCondition;
import com.uu.modules.om.entity.Price;
import com.uu.modules.om.service.PriceService;

/**
 * 
 * @Description 更新价格
 * @author yifang.huang
 * @date 2016年5月12日 下午6:22:37
 */
public class ChangePriceScheduled {
	
	public static Logger logger = LoggerFactory.getLogger(ChangePriceScheduled.class);

	@Autowired
	private PriceService priceService;
	
	@Scheduled(cron = "0 02 * * * ?")//秒、分、时、日、月、年
	//@Scheduled(fixedRate = 1 * 60 * 1000)//1个小时钟扫一次
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void createCsvFiles(){
		
		logger.info("更新价格定时处理开始！");
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH");
		// 取需要更新价格的价格列表
		PriceCondition condition = new PriceCondition();
		condition.setNeedChangePrice("yes");
		condition.setEqNewPriceStartDate(df.format(new Date()));
		List<Price> priceList = priceService.findListByCondition(condition);
		if (priceList!=null && priceList.size()>0) {
			for (Price price : priceList) {
				price.setPrice(price.getNewPrice());
				price.setNewPrice(null);
				price.setNewPriceStartDate(null);
				priceService.save(price);
			}
		}
		
		logger.info("更新价格定时处理结束！");
		
	}
	
}
