package com.uu.modules.om.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Parameter;
import com.uu.common.utils.DateUtils;
import com.uu.modules.om.condition.ConsumeRecordCondition;
import com.uu.modules.om.entity.ConsumeRecord;

/**
 * 订单统计
 * 
 * @Description
 * @author yuxiaoyu
 * @date 2016年3月31日 下午12:00:12
 */
@Repository
public class StatisticDao extends BaseDao<ConsumeRecord> {
	/**
	 * 计算总金额
	 * 
	 * @Description
	 * @param detachedCriteria
	 * @return
	 * @return String
	 * @author yuxiaoyu
	 * @date 2016年4月1日 上午11:58:56
	 */
	public String sumMoney(DetachedCriteria detachedCriteria) {
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
		criteria.setProjection(Projections.sum("money"));
		Double result = (Double) criteria.uniqueResult();
		if (null == result) {
			return String.valueOf(new Double(0));
		}
		return String.valueOf(result);
	}

	/**
	 * 更新结算状态
	 * 
	 * @Description
	 * @param condition
	 * @return
	 * @return int
	 * @author yuxiaoyu
	 * @date 2016年4月1日 下午3:08:50
	 */
	public int balanceByConditions(ConsumeRecordCondition condition) {
		Parameter parameter = new Parameter();
		StringBuilder stringBuild = new StringBuilder();
		stringBuild.append("update om_consume_record rec ");
		if (StringUtils.isNotBlank(condition.getEqChannelId())) {
			stringBuild
					.append(", om_channel chl set rec.balance_status = '1' where chl.id = :p1 and rec.channel_id = chl.id and rec.balance_status = 0 ");
			parameter.put("p1", condition.getEqChannelId());
		} else {
			stringBuild.append(" set rec.balance_status = '1' where rec.balance_status = 0 ");
		}

		if (null != condition.getEqRecordType()) {
			stringBuild.append(" and rec.record_type = :p2 ");
			parameter.put("p2", condition.getEqRecordType());
		}
		if (null != condition.getEqStatus()) {
			stringBuild.append(" and rec.status = :p3 ");
			parameter.put("p3", condition.getEqStatus());
		}
		if (StringUtils.isNotBlank(condition.getGeCreateDate())) {
			stringBuild.append(" and rec.create_date >= :p4 ");
			parameter.put("p4", DateUtils.parseDate(condition.getGeCreateDate() + " 00:00:00"));
		}
		if (StringUtils.isNotBlank(condition.getLeCreateDate())) {
			stringBuild.append(" and rec.create_date <= :p5 ");
			parameter.put("p5", DateUtils.parseDate(condition.getLeCreateDate() + " 23:59:59"));
		}
		return this.updateBySql(stringBuild.toString(), parameter);
	}
}
