/** 
 * @Package com.uu.modules.om.condition 
 * @Description 
 * @author yifang.huang
 * @date 2016年12月16日 下午1:19:55 
 * @version V1.0 
 */ 
package com.uu.modules.om.condition;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.uu.modules.om.entity.Price;

/** 
 * @Description 广告    条件查询类
 * @author yifang.huang
 * @date 2016年12月16日 下午1:19:55 
 */
public class AdvertisingCondition {
	
	private String likeName;				// 广告名称 like 查询值
	
	private String likeCountryCodes;		// 国家编号 like 查询值

	private String eqType;					// 广告类型  eq 查询值
	
	private String likeSourceType;			// 所属渠道 like 查询值
	
	private String eqShelfUpDown;			// 上下架 eq 查询值

	
	public void build(DetachedCriteria dc) {
		
		if (StringUtils.isNotBlank(likeName))
			dc.add(Restrictions.like("name", likeName, MatchMode.ANYWHERE));
		
		if (StringUtils.isNotBlank(likeCountryCodes))
			dc.add(Restrictions.like("countryCodes", likeCountryCodes, MatchMode.ANYWHERE));
		
		if (StringUtils.isNotBlank(eqType))
			dc.add(Restrictions.eq("type", eqType));
		
		if (StringUtils.isNotBlank(likeSourceType))
			dc.add(Restrictions.like("sourceTypes", likeSourceType, MatchMode.ANYWHERE));
		
		if (StringUtils.isNotBlank(eqShelfUpDown))
			dc.add(Restrictions.eq("shelfUpDown", eqShelfUpDown));
		
		// 查询标记为0（正常）的数据
		dc.add(Restrictions.eq(Price.FIELD_DEL_FLAG, Price.DEL_FLAG_NORMAL));
		
		// 按创建时间降序排序
		dc.addOrder(Order.desc("createDate"));

	}
	
	/** 
	 * @return likeName
	 */
	public String getLikeName() {
		return likeName;
	}

	/** 
	 * @param likeName
	 */
	public void setLikeName(String likeName) {
		this.likeName = likeName;
	}

	/** 
	 * @return likeCountryCodes
	 */
	public String getLikeCountryCodes() {
		return likeCountryCodes;
	}

	/** 
	 * @param likeCountryCodes
	 */
	public void setLikeCountryCodes(String likeCountryCodes) {
		this.likeCountryCodes = likeCountryCodes;
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
	 * @return likeSourceType
	 */
	public String getLikeSourceType() {
		return likeSourceType;
	}

	/** 
	 * @param likeSourceType
	 */
	public void setLikeSourceType(String likeSourceType) {
		this.likeSourceType = likeSourceType;
	}

	/** 
	 * @return eqShelfUpDown
	 */
	public String getEqShelfUpDown() {
		return eqShelfUpDown;
	}

	/** 
	 * @param eqShelfUpDown
	 */
	public void setEqShelfUpDown(String eqShelfUpDown) {
		this.eqShelfUpDown = eqShelfUpDown;
	}

}
