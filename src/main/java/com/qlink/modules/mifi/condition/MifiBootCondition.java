/** 
 * @Package com.uu.modules.mifi.condition 
 * @Description 
 * @author yifang.huang
 * @date 2016年12月7日 上午11:02:17 
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

/** 
 * @Description MIFI开机(一天一条MIFI状态为4记录) 查询条件类
 * @author yifang.huang
 * @date 2016年12月7日 上午11:02:17 
 */
public class MifiBootCondition {
	
	private String eqStatus;						// 状态 eq 查询值
	
	private String geCreateDate;					// 创建时间  ge 查询值

	private String leCreateDate;					// 创建时间  le 查询值
	
	public void build(DetachedCriteria dc) {
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		if (StringUtils.isNotBlank(eqStatus)) {
			dc.add(Restrictions.eq("status", eqStatus));
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

}
