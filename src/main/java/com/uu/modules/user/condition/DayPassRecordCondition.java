/** 
 * @Package com.uu.modules.user.condition 
 * @Description 
 * @author yifang.huang
 * @date 2016年12月6日 上午11:34:33 
 * @version V1.0 
 */ 
package com.uu.modules.user.condition;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.uu.common.utils.DateUtils;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.utils.UserUtils;

/** 
 * @Description 用户开通天数记录(增加减少) 查询条件类
 * @author yifang.huang
 * @date 2016年12月6日 上午11:34:33 
 */
public class DayPassRecordCondition {
	
	private String likeLoginName;					// 用户登录名 like 查询值
	
	private String eqType;							// 记录类型 eq 查询值
	
	private String eqOrderId;						// 订单ID eq 查询值
	
	private String likeImei;						// 设备编号 like 查询值
	
	private String eqStatus;						// 状态 eq 查询值
	
	private String eqSourceType;					// 渠道编号 eq 查询值
	
	private String geCreateDate;					// 创建时间  ge 查询值

	private String leCreateDate;					// 创建时间  le 查询值
	
	private Date geFullCreateDate;					// 创建时间  ge 查询值

	public DayPassRecordCondition() {
		super();
		// 初始化渠道查询值，渠道数据权限控制
		initDataControl();
	}

	public DayPassRecordCondition(boolean isRecordQuery) {
		super();
	}
	
	public void build(DetachedCriteria dc) {
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		if (StringUtils.isNotBlank(likeLoginName)) {
			dc.add(Restrictions.like("loginName", likeLoginName, MatchMode.ANYWHERE));
		}
		
		if (StringUtils.isNotBlank(eqType)) {
			dc.add(Restrictions.eq("type", eqType));
		}
		
		if (StringUtils.isNotBlank(eqOrderId)) {
			dc.add(Restrictions.eq("orderId", eqOrderId));
		}
		
		if (StringUtils.isNotBlank(likeImei)) {
			dc.add(Restrictions.like("imei", likeImei, MatchMode.ANYWHERE));
		}
		
		if (StringUtils.isNotBlank(eqStatus)) {
			dc.add(Restrictions.eq("status", eqStatus));
		}
		
		if (StringUtils.isNotBlank(eqSourceType)) {
			dc.add(Restrictions.eq("sourceType", eqSourceType));
		}

		if (StringUtils.isNotBlank(geCreateDate)) {
			try {
				dc.add(Restrictions.ge("createDate", DateUtils.getDateStart(df.parse(geCreateDate))));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		if (StringUtils.isNotBlank(leCreateDate)) {
			try {
				dc.add(Restrictions.le("createDate", DateUtils.getDateEnd(df.parse(leCreateDate))));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		if (geFullCreateDate != null) {
			dc.add(Restrictions.ge("createDate", geFullCreateDate));
		}
		
		// 按创建时间降序排序
		dc.addOrder(Order.desc("createDate"));
		
	}

	/** 
	 * @return likeLoginName
	 */
	public String getLikeLoginName() {
		return likeLoginName;
	}

	/** 
	 * @param likeLoginName
	 */
	public void setLikeLoginName(String likeLoginName) {
		this.likeLoginName = likeLoginName;
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
	 * @return eqOrderId
	 */
	public String getEqOrderId() {
		return eqOrderId;
	}

	/** 
	 * @param eqOrderId
	 */
	public void setEqOrderId(String eqOrderId) {
		this.eqOrderId = eqOrderId;
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
	 * @return eqSourceType
	 */
	public String getEqSourceType() {
		return eqSourceType;
	}

	/** 
	 * @param eqSourceType
	 */
	public void setEqSourceType(String eqSourceType) {
		this.eqSourceType = eqSourceType;
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
	 * @return geFullCreateDate
	 */
	public Date getGeFullCreateDate() {
		return geFullCreateDate;
	}

	/** 
	 * @param geFullCreateDate
	 */
	public void setGeFullCreateDate(Date geFullCreateDate) {
		this.geFullCreateDate = geFullCreateDate;
	}

	/**
	 * 
	 * @Description  根据当前登录用户，初始化渠道查询值
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年11月22日 上午10:24:41
	 */
	private void initDataControl() {
		User user = UserUtils.getUser();
		boolean isChannelAdmin = user.getChannelAdmin();
		if (isChannelAdmin)
			eqSourceType = user.getChannelNameEn();
	}

}
