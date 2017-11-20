/** 
 * @Package com.uu.modules.mifi.condition 
 * @Description 
 * @author yuxiaoyu
 * @date 2017年3月3日 下午4:13:04 
 * @version V1.0 
 */ 
package com.qlink.modules.mifi.condition;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.qlink.common.utils.DateUtils;
import com.qlink.common.utils.StringUtils;

/** 
 * @Description 
 * @author yuxiaoyu
 * @date 2017年3月3日 下午4:13:04 
 */
public class MifiUsageRecordSegmentLogCondition {
	private Integer result;

	private String beginDate;

	private String endDate;

	public void build(DetachedCriteria dc) {
		if (result != null) {
			dc.add(Restrictions.eq("result", result));
		}

		if (StringUtils.isNotBlank(beginDate)) {
			dc.add(Restrictions.ge("stampCreated", DateUtils.getDateStart(DateUtils.parseDate(beginDate))));
		}

		if (StringUtils.isNotBlank(endDate)) {
			dc.add(Restrictions.le("stampCreated", DateUtils.getDateEnd(DateUtils.parseDate(endDate))));
		}
		dc.addOrder(Order.desc("updateDate"));
		dc.addOrder(Order.desc("stampCreated"));
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
}
