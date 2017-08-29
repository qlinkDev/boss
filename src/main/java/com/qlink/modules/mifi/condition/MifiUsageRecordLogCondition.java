package main.java.com.qlink.modules.mifi.condition;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.uu.common.utils.DateUtils;
import com.uu.common.utils.StringUtils;

/**
 * 设备使用调度日志
 * 
 * @author shuxin
 * @date 2016年8月4日
 */
public class MifiUsageRecordLogCondition {

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
