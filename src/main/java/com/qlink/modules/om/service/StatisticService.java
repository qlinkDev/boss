package main.java.com.qlink.modules.om.service;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.service.BaseService;
import com.uu.modules.om.condition.ConsumeRecordCondition;
import com.uu.modules.om.dao.StatisticDao;

/**
 * 订单统计
 * 
 * @Description
 * @author yuxiaoyu
 * @date 2016年3月31日 上午11:52:26
 */
@Service
public class StatisticService extends BaseService {

	@Autowired
	private StatisticDao statisticDao;

	public String sumMoney(ConsumeRecordCondition condition) {

		DetachedCriteria detachedCriteria = statisticDao.createDetachedCriteria();

		// build 查询条件
		condition.build(detachedCriteria);

		return statisticDao.sumMoney(detachedCriteria);
	}

	//@Transactional(readOnly = false)
	public int updateBalanceByConditions(ConsumeRecordCondition condition) {
		return statisticDao.balanceByConditions(condition);
	}
}
