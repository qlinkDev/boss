package com.uu.modules.mifi.condition;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.uu.common.utils.DateUtils;
import com.uu.common.utils.StringUtils;

/**
 * 使用记录查询条件
 * @Description 
 * @author yuxiaoyu
 * @date 2017年3月3日 下午3:39:27
 */
public class MifiUsageRecordSegmentCondition {
	/**
	 * 设备编号
	 */
	private String imei;

	/**
	 * 设备批次号
	 */
	private String bath;

	/**
	 * 代理商
	 */
	private String sourceType;
	
	private String eqCardSourceType;

	private String beginDate;

	private String endDate;
	
	private String eqCountryCode;
	
	private Long gtDatainfo;

	public void build(DetachedCriteria dc) {
		if (StringUtils.isNotBlank(imei)) {
			dc.add(Restrictions.eq("imei", imei));
		}

		if (StringUtils.isNotBlank(bath)) {
			dc.add(Restrictions.eq("bath", bath));
		}

		if (StringUtils.isNotBlank(sourceType)) {
			dc.add(Restrictions.eq("sourceType", sourceType));
		}

		if (StringUtils.isNotBlank(eqCardSourceType)) {
			dc.add(Restrictions.eq("cardSourceType", eqCardSourceType));
		}

		if (StringUtils.isNotBlank(beginDate)) {
			dc.add(Restrictions.ge("stampCreated", DateUtils.getDateStart(DateUtils.parseDate(beginDate))));
		}

		if (StringUtils.isNotBlank(endDate)) {
			dc.add(Restrictions.le("stampCreated", DateUtils.getDateEnd(DateUtils.parseDate(endDate))));
		}
		
		if (StringUtils.isNotBlank(eqCountryCode)) {
			dc.add(Restrictions.eq("countryCode", eqCountryCode));
		}
		
		if (gtDatainfo != null) {
			dc.add(Restrictions.gt("datainfo", gtDatainfo));
		}
		
		dc.addOrder(Order.desc("stampCreated"));
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei == null ? null : imei.trim();
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	/** 
	 * @return eqCardSourceType
	 */
	public String getEqCardSourceType() {
		return eqCardSourceType;
	}

	/** 
	 * @param eqCardSourceType
	 */
	public void setEqCardSourceType(String eqCardSourceType) {
		this.eqCardSourceType = eqCardSourceType;
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

	public String getBath() {
		return bath;
	}

	public void setBath(String bath) {
		this.bath = bath;
	}

	/** 
	 * @return eqCountryCode
	 */
	public String getEqCountryCode() {
		return eqCountryCode;
	}

	/** 
	 * @param eqCountryCode
	 */
	public void setEqCountryCode(String eqCountryCode) {
		this.eqCountryCode = eqCountryCode;
	}

	/** 
	 * @return gtDatainfo
	 */
	public Long getGtDatainfo() {
		return gtDatainfo;
	}

	/** 
	 * @param gtDatainfo
	 */
	public void setGtDatainfo(Long gtDatainfo) {
		this.gtDatainfo = gtDatainfo;
	}

}
