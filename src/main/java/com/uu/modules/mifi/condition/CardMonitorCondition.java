/** 
 * @Package com.uu.modules.mifi.condition 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月15日 上午10:11:09 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.condition;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.uu.common.utils.DateUtils;
import com.uu.modules.om.entity.ConsumeRecord;

/** 
 * @Description 卡监控信息 查询条件类
 * @author yifang.huang
 * @date 2016年4月15日 上午10:11:09 
 */
public class CardMonitorCondition {
	private String eqImei;
	private String likeImei;								// 设备编号 like 查询值
	
	private String eqCountryCode;							// 国家编号 eq 查询值
	
	private String eqStatus;								// 状态 eq 查询值
	
	private String geCreateDate;							// 发生时间  ge 查询值

	private String leCreateDate;							// 发生时间  le 查询值
	
	private String eqType;									// 类型 eq 查询值
	
	private String eqFaultCode;								// 故障代码 eq 查询值
	
	private String mcc;										// MCC

	
	public void build(DetachedCriteria dc) {
		
		if (StringUtils.isNotBlank(eqImei)) {
			dc.add(Restrictions.eq("imei", eqImei));
		}
		if (StringUtils.isNotBlank(likeImei)) {
			dc.add(Restrictions.like("imei", likeImei, MatchMode.ANYWHERE));
		}
		
		if (StringUtils.isNotBlank(mcc)){
			String [] str=mcc.split(",");
			dc.add(Restrictions.in("mcc",str));
		}
		
		if (StringUtils.isNotBlank(eqStatus)){
			dc.add(Restrictions.eq("status", eqStatus));
		}
		
		Date date;
		if (StringUtils.isNotBlank(geCreateDate)) {
			date = DateUtils.parseDate(geCreateDate);
			dc.add(Restrictions.ge("createDate", date));
		}
		
		if (StringUtils.isNotBlank(leCreateDate)) {
			date = DateUtils.parseDate(leCreateDate);
			dc.add(Restrictions.le("createDate", date));
		}
		
		if (StringUtils.isNotBlank(eqType)){
			dc.add(Restrictions.eq("type", eqType));
		}
		
		if (StringUtils.isNotBlank(eqFaultCode)){
			dc.add(Restrictions.eq("faultCode", eqFaultCode));
		}
		
		// 查询标记为0（正常）的数据
		dc.add(Restrictions.eq(ConsumeRecord.FIELD_DEL_FLAG, ConsumeRecord.DEL_FLAG_NORMAL));
		
		// 按创建时间降序排序
		dc.addOrder(Order.desc("imei"));
		dc.addOrder(Order.desc("createDate"));

	}

	public String getEqImei() {
		return eqImei;
	}

	public void setEqImei(String eqImei) {
		this.eqImei = eqImei;
	}

	/** 
	 * @return likeImei
	 */
	public String getLikeImei() {
		return likeImei;
	}

	/** 
	 * @param likeImei
	 */
	public void setLikeImei(String likeImei) {
		this.likeImei = likeImei;
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
	 * @return geCreateDate
	 */
	public String getGeCreateDate() {
		return geCreateDate;
	}

	/** 
	 * @param geCreateDate
	 */
	public void setGeCreateDate(String geCreateDate) {
		this.geCreateDate = geCreateDate;
	}

	/** 
	 * @return leCreateDate
	 */
	public String getLeCreateDate() {
		return leCreateDate;
	}

	/** 
	 * @param leCreateDate
	 */
	public void setLeCreateDate(String leCreateDate) {
		this.leCreateDate = leCreateDate;
	}

	/** 
	 * @return eqType
	 */
	public String getEqType() {
		return eqType;
	}

	/** 
	 * @param eqType
	 */
	public void setEqType(String eqType) {
		this.eqType = eqType;
	}

	/** 
	 * @return eqFaultCode
	 */
	public String getEqFaultCode() {
		return eqFaultCode;
	}

	/** 
	 * @param eqFaultCode
	 */
	public void setEqFaultCode(String eqFaultCode) {
		this.eqFaultCode = eqFaultCode;
	}

	public String getMcc() {
		return mcc;
	}

	public void setMcc(String mcc) {
		this.mcc = mcc;
	}

}
