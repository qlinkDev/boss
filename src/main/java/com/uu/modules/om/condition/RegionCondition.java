/** 
 * @Package com.uu.modules.om.condition 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月18日 下午5:20:02 
 * @version V1.0 
 */ 
package com.uu.modules.om.condition;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.uu.modules.om.entity.Region;

/** 
 * @Description 区域 查询条件类
 * @author yifang.huang
 * @date 2016年3月18日 下午5:20:02 
 */
public class RegionCondition {
	
	private String neId;
	
	private String eqCode;
	
	private String eqName;
	
	private String likeName;
	
	private String likeMcc;
	
	
	public void build(DetachedCriteria dc) {
		
		// id ne 查询值
		if (StringUtils.isNotBlank(neId))
			dc.add(Restrictions.ne("id", neId));
		
		// 编码 eq 查询值
		if (StringUtils.isNotBlank(eqCode))
			dc.add(Restrictions.eq("code", eqCode));
		
		// 名称 eq 查询值
		if (StringUtils.isNotBlank(eqName))
			dc.add(Restrictions.eq("name", eqName));
		
		// 名称 like 查询值
		if (StringUtils.isNotBlank(likeName)) {
			dc.add(Restrictions.like("name", likeName, MatchMode.ANYWHERE));
		}
		
		// 按用区域MCCES查询
		if (StringUtils.isNotBlank(likeMcc)) {
			dc.add(Restrictions.like("mcces", likeMcc, MatchMode.ANYWHERE));
		}

		// 查询标记为0（正常）的数据
		dc.add(Restrictions.eq(Region.FIELD_DEL_FLAG, Region.DEL_FLAG_NORMAL));
		
		// 按创建时间降序排序
		dc.addOrder(Order.desc("createDate"));

	}


	/** 
	 * @return neId
	 */
	public String getNeId() {
		return neId;
	}


	/** 
	 * @param neId
	 */
	public void setNeId(String neId) {
		this.neId = neId;
	}


	/** 
	 * @return eqCode
	 */
	public String getEqCode() {
		return eqCode;
	}


	/** 
	 * @param eqCode
	 */
	public void setEqCode(String eqCode) {
		this.eqCode = eqCode;
	}


	/** 
	 * @return eqName
	 */
	public String getEqName() {
		return eqName;
	}


	/** 
	 * @param eqName
	 */
	public void setEqName(String eqName) {
		this.eqName = eqName;
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
	 * @return likeMcc
	 */
	public String getLikeMcc() {
		return likeMcc;
	}


	/** 
	 * @param likeMcc
	 */
	public void setLikeMcc(String likeMcc) {
		this.likeMcc = likeMcc;
	}

}
