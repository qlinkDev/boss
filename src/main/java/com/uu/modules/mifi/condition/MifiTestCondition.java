/** 
 * @Package com.uu.modules.mifi.condition 
 * @Description 
 * @author yifang.huang
 * @date 2016年5月24日 下午4:26:01 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.condition;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.uu.common.utils.DateUtils;

/** 
 * @Description 测试设备与卡绑定 查询条件类
 * @author yifang.huang
 * @date 2016年5月24日 下午4:26:01 
 */
public class MifiTestCondition {
	
	private Integer neId;							// id ne 查询值
	
	private String eqImei;							// 设备编号 eq 查询值
	
	private String eqSimBankId;						// 卡槽编号 eq 查询值
	
	private String eqSimId;							// 卡槽位置 eq 查询值

	private String likeImei;						// 设备编号 like 查询值
	
	private String geCreateDate;					// 创建时间  ge 查询值

	private String leCreateDate;					// 创建时间  le 查询值
	
	public void build(DetachedCriteria dc) {
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		if (neId != null) {
			dc.add(Restrictions.ne("id", neId));
		}
		
		if (StringUtils.isNotBlank(eqImei)) {
			dc.add(Restrictions.eq("imei", eqImei));
		}
		
		if (StringUtils.isNotBlank(eqSimBankId)) {
			dc.add(Restrictions.eq("simBankId", eqSimBankId));
		}
		
		if (StringUtils.isNotBlank(eqSimId)) {
			dc.add(Restrictions.eq("simId", eqSimId));
		}
		
		if (StringUtils.isNotBlank(likeImei)) {
			dc.add(Restrictions.like("imei", likeImei, MatchMode.ANYWHERE));
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
	
		// 按创建时间降序排序
		dc.addOrder(Order.desc("createDate"));
		
	}

	/** 
	 * @return neId
	 */
	public Integer getNeId() {
		return neId;
	}

	/** 
	 * @param neId
	 */
	public void setNeId(Integer neId) {
		this.neId = neId;
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
	 * @return eqSimBankId
	 */
	public String getEqSimBankId() {
		return eqSimBankId;
	}

	/** 
	 * @param eqSimBankId
	 */
	public void setEqSimBankId(String eqSimBankId) {
		this.eqSimBankId = eqSimBankId;
	}

	/** 
	 * @return eqSimId
	 */
	public String getEqSimId() {
		return eqSimId;
	}

	/** 
	 * @param eqSimId
	 */
	public void setEqSimId(String eqSimId) {
		this.eqSimId = eqSimId;
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
	
}
