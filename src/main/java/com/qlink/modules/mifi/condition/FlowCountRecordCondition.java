/** 
 * @Package com.uu.modules.mifi.condition 
 * @Description 
 * @author yifang.huang
 * @date 2017年5月23日 下午3:08:05 
 * @version V1.0 
 */ 
package com.qlink.modules.mifi.condition;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.qlink.common.utils.DateUtils;
import com.qlink.modules.om.entity.Price;

/** 
 * @Description 流量统计记录  查询条件类
 * @author yifang.huang
 * @date 2017年5月23日 下午3:08:05 
 */
public class FlowCountRecordCondition {
	
	private String eqId;								// id eq 查询值
	
	private String eqStatus;							// 状态 eq 查询值
	
	private String eqCreateById;						// 统计执行者 eq 查询值
	
	private String eqRegionId;							// 统计区域 eq 查询值
	
	private String geStartDate;							// 开始时间  ge 查询值

	private String leStartDate;							// 开始时间  le 查询值
	
	private String geEndDate;							// 结束时间  ge 查询值

	private String leEndDate;							// 结束时间  le 查询值
	
	public void build(DetachedCriteria dc) {
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		if (StringUtils.isNotBlank(eqId))
			dc.add(Restrictions.eq("id", eqId));
		
		if (StringUtils.isNotBlank(eqStatus))
			dc.add(Restrictions.eq("status", eqStatus));
		
		if (StringUtils.isNotBlank(eqCreateById))
			dc.add(Restrictions.eq("createBy.id", eqCreateById));
		
		if (StringUtils.isNotBlank(eqRegionId))
			dc.add(Restrictions.eq("region.id", eqRegionId));
		
		if (StringUtils.isNotBlank(geStartDate)) {
			try {
				dc.add(Restrictions.ge("startDate", DateUtils.getDateStart(df.parse(geStartDate))));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		if (StringUtils.isNotBlank(leStartDate)) {
			try {
				dc.add(Restrictions.le("startDate", DateUtils.getDateEnd(df.parse(leStartDate))));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		if (StringUtils.isNotBlank(geEndDate)) {
			try {
				dc.add(Restrictions.ge("endDate", DateUtils.getDateEnd(df.parse(geEndDate))));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		if (StringUtils.isNotBlank(leEndDate)) {
			try {
				dc.add(Restrictions.le("endDate", DateUtils.getDateEnd(df.parse(leEndDate))));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		// 查询标记为0（正常）的数据
		dc.add(Restrictions.eq(Price.FIELD_DEL_FLAG, Price.DEL_FLAG_NORMAL));

		// 按创建时间降序排序
		dc.addOrder(Order.desc("createDate"));
		
	}

	/** 
	 * @return eqId
	 */
	public String getEqId() {
		return eqId;
	}

	/** 
	 * @param eqId
	 */
	public void setEqId(String eqId) {
		this.eqId = eqId;
	}

	/** 
	 * @return eqStatus
	 */
	public String getEqStatus() {
		return eqStatus;
	}

	/** 
	 * @param eqStatus
	 */
	public void setEqStatus(String eqStatus) {
		this.eqStatus = eqStatus;
	}

	/** 
	 * @return eqCreateById
	 */
	public String getEqCreateById() {
		return eqCreateById;
	}

	/** 
	 * @param eqCreateById
	 */
	public void setEqCreateById(String eqCreateById) {
		this.eqCreateById = eqCreateById;
	}

	/** 
	 * @return eqRegionId
	 */
	public String getEqRegionId() {
		return eqRegionId;
	}

	/** 
	 * @param eqRegionId
	 */
	public void setEqRegionId(String eqRegionId) {
		this.eqRegionId = eqRegionId;
	}

	/** 
	 * @return geStartDate
	 */
	public String getGeStartDate() {
		return geStartDate;
	}

	/** 
	 * @param geStartDate
	 */
	public void setGeStartDate(String geStartDate) {
		this.geStartDate = geStartDate;
	}

	/** 
	 * @return leStartDate
	 */
	public String getLeStartDate() {
		return leStartDate;
	}

	/** 
	 * @param leStartDate
	 */
	public void setLeStartDate(String leStartDate) {
		this.leStartDate = leStartDate;
	}

	/** 
	 * @return geEndDate
	 */
	public String getGeEndDate() {
		return geEndDate;
	}

	/** 
	 * @param geEndDate
	 */
	public void setGeEndDate(String geEndDate) {
		this.geEndDate = geEndDate;
	}

	/** 
	 * @return leEndDate
	 */
	public String getLeEndDate() {
		return leEndDate;
	}

	/** 
	 * @param leEndDate
	 */
	public void setLeEndDate(String leEndDate) {
		this.leEndDate = leEndDate;
	}
	
}
