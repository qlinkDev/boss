/** 
 * @Package com.uu.modules.mifi.condition 
 * @Description 
 * @author yifang.huang
 * @date 2016年11月22日 上午10:17:49 
 * @version V1.0 
 */ 
package com.qlink.modules.mifi.condition;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.qlink.common.utils.DateUtils;
import com.qlink.modules.sys.entity.User;
import com.qlink.modules.sys.utils.UserUtils;

/** 
 * @Description 设备开机   查询条件类
 * @author yifang.huang
 * @date 2016年11月22日 上午10:17:49 
 */
public class DeviceBootCondition {
	
	private String eqType;							// 类型 eq 查询值
	
	private String eqImei;							// 设备编号 eq 查询值
	
	private String eqMac;							// MAC地址  eq 查询值
	
	private String likeImei;						// 设备编号 like 查询值
	
	private String eqSourceType;					// 渠道编号 eq 查询值
	
	private Date geFullCreateDate;					// 创建时间  ge 查询值
	
	private String geCreateDate;					// 创建时间  ge 查询值

	private String leCreateDate;					// 创建时间  le 查询值
	
	private String eqAdvertisingId;					// 广告ID eq 查询值

	public DeviceBootCondition() {
		super();
		// 初始化渠道查询值，渠道数据权限控制
		initDataControl();
	}
	
	public void build(DetachedCriteria dc) {
		
		if (StringUtils.isNotBlank(eqType)) {
			dc.add(Restrictions.eq("type", eqType));
		}
		
		if (StringUtils.isNotBlank(eqImei)) {
			dc.add(Restrictions.eq("imei", eqImei));
		}
		
		if (StringUtils.isNotBlank(eqMac)) {
			dc.add(Restrictions.eq("mac", eqMac));
		}
		
		if (StringUtils.isNotBlank(likeImei)) {
			dc.add(Restrictions.like("imei", likeImei, MatchMode.ANYWHERE));
		}
		
		if (StringUtils.isNotBlank(eqSourceType)) {
			dc.add(Restrictions.eq("sourceType", eqSourceType));
		}

		if (geFullCreateDate != null) {
			dc.add(Restrictions.ge("createDate", geFullCreateDate));
		}

		if (StringUtils.isNotBlank(geCreateDate)) {
			dc.add(Restrictions.ge("createDate", DateUtils.parseDate(geCreateDate)));
		}
		
		if (StringUtils.isNotBlank(leCreateDate)) {
			dc.add(Restrictions.le("createDate", DateUtils.parseDate(leCreateDate)));
		}
		
		if (StringUtils.isNotBlank(eqAdvertisingId)) {
			dc.add(Restrictions.eq("advertising.id", eqAdvertisingId));
		}
		
		// 按创建时间降序排序
		dc.addOrder(Order.desc("createDate"));
		
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
	 * @return eqImei
	 */
	public String getEqImei() {
		return eqImei;
	}

	/** 
	 * @param eqImei
	 */
	public void setEqImei(String eqImei) {
		this.eqImei = eqImei;
	}

	/** 
	 * @return eqMac
	 */
	public String getEqMac() {
		return eqMac;
	}

	/** 
	 * @param eqMac
	 */
	public void setEqMac(String eqMac) {
		this.eqMac = eqMac;
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
	 * @return eqAdvertisingId
	 */
	public String getEqAdvertisingId() {
		return eqAdvertisingId;
	}

	/** 
	 * @param eqAdvertisingId
	 */
	public void setEqAdvertisingId(String eqAdvertisingId) {
		this.eqAdvertisingId = eqAdvertisingId;
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
