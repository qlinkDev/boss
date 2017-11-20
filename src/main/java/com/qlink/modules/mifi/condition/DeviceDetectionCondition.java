/** 
 * @Package com.uu.modules.mifi.condition 
 * @Description 
 * @author yifang.huang
 * @date 2016年11月9日 下午3:16:28 
 * @version V1.0 
 */ 
package com.qlink.modules.mifi.condition;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.qlink.common.persistence.BaseEntity;
import com.qlink.modules.sys.entity.User;
import com.qlink.modules.sys.utils.UserUtils;

/** 
 * @Description 设备检查  查询条件类
 * @author yifang.huang
 * @date 2016年11月9日 下午3:16:28 
 */
public class DeviceDetectionCondition {
	
	private String eqImei;							// 设备编号 eq 查询值
	
	private String likeImei;						// 设备编号 like 查询值
	
	private String eqUseFlag;						// 可否使用 eq 查询值
	
	private String eqSourceType;					// 渠道编号 eq 查询值

	public DeviceDetectionCondition() {
		super();
		// 初始化渠道查询值，渠道数据权限控制
		initDataControl();
	}
	
	public void build(DetachedCriteria dc) {
		
		if (StringUtils.isNotBlank(eqImei)) {
			dc.add(Restrictions.eq("imei", eqImei));
		}
		
		if (StringUtils.isNotBlank(likeImei)) {
			dc.add(Restrictions.like("imei", likeImei, MatchMode.ANYWHERE));
		}
		
		if (StringUtils.isNotBlank(eqUseFlag)) {
			dc.add(Restrictions.eq("useFlag", eqUseFlag));
		}
		
		if (StringUtils.isNotBlank(eqSourceType)) {
			dc.add(Restrictions.eq("sourceType", eqSourceType));
		}
		
		// 查询标记为0（正常）的数据
		dc.add(Restrictions.eq(BaseEntity.FIELD_DEL_FLAG, BaseEntity.DEL_FLAG_NORMAL));
		
		// 按创建时间降序排序
		dc.addOrder(Order.desc("createDate"));
		
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
	 * @return eqUseFlag
	 */
	public String getEqUseFlag() {
		return eqUseFlag;
	}

	/** 
	 * @param eqUseFlag
	 */
	public void setEqUseFlag(String eqUseFlag) {
		this.eqUseFlag = eqUseFlag;
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
	 * 
	 * @Description  根据当前登录用户，初始化渠道查询值
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年4月12日 下午5:36:37
	 */
	private void initDataControl() {
		User user = UserUtils.getUser();
		boolean isChannelAdmin = user.getChannelAdmin();
		if (isChannelAdmin)
			eqSourceType = user.getChannelNameEn();
	}
}
