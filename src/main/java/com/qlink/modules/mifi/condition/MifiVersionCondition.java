/** 
 * @Package com.uu.modules.mifi.condition 
 * @Description 
 * @author yifang.huang
 * @date 2016年5月20日 下午2:53:11 
 * @version V1.0 
 */ 
package com.qlink.modules.mifi.condition;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * 设备版本信息查 询条件类
 * @Description 
 * @author yifang.huang
 * @date 2016年10月26日 上午11:17:48
 */
public class MifiVersionCondition {
	
	private String eqImei;									// 设备编号 eq 查询值
	
	private String likeImei;								// 设备编号 like 查询值

	public void build(DetachedCriteria dc) {
		
		if (StringUtils.isNotBlank(eqImei)){
			dc.add(Restrictions.eq("IMEI_6200", eqImei));
		}
		
		if (StringUtils.isNotBlank(likeImei)) {
			dc.add(Restrictions.like("IMEI_6200", likeImei, MatchMode.ANYWHERE));
		}
		
		// 按创建时间降序排序
		dc.addOrder(Order.desc("stamp_created"));
		
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

}
