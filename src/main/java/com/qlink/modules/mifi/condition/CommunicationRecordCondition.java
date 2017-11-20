/** 
 * @Package com.uu.modules.om.condition 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月18日 下午5:20:02 
 * @version V1.0 
 */
package com.qlink.modules.mifi.condition;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.qlink.common.utils.DateUtils;
import com.qlink.modules.om.entity.Region;

/**
 * 通信记录 查询条件类
 * 
 * @author shuxin
 * @date 2016年7月20日
 */
public class CommunicationRecordCondition {

	private String deviceSn;

	private Integer type;

	private Integer result;

	private String startDate;

	private String endDate;

	public void build(DetachedCriteria dc) {

		// 按照设备 查询值
		if (StringUtils.isNotBlank(deviceSn))
			dc.add(Restrictions.like("deviceSn", deviceSn.trim(), MatchMode.ANYWHERE));

		// 按照通信类型查询
		if (type != null)
			dc.add(Restrictions.eq("type", type));

		// 按照通信结果查询
		if (result != null)
			dc.add(Restrictions.eq("result", result));

		// 查询标记为0（正常）的数据
		dc.add(Restrictions.eq(Region.FIELD_DEL_FLAG, Region.DEL_FLAG_NORMAL));

		//按照时间查询
		if(StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)){
			dc.add(Restrictions.between("createDate", DateUtils.getDateStart(DateUtils.parseDate(startDate)), DateUtils.getDateEnd(DateUtils.parseDate(endDate))));
		} else {
			if(StringUtils.isNotBlank(startDate))
				dc.add(Restrictions.lt("createDate", DateUtils.getDateStart(DateUtils.parseDate(startDate))));
			if(StringUtils.isNotBlank(endDate))
				dc.add(Restrictions.gt("createDate", DateUtils.getDateEnd(DateUtils.parseDate(endDate))));
		}
		
		// 按创建时间降序排序
		dc.addOrder(Order.desc("createDate"));
	}

	public String getDeviceSn() {
		return deviceSn;
	}

	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}
