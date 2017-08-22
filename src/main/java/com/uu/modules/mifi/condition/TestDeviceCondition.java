/** 
 * @Package com.uu.modules.mifi.condition 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月8日 上午11:52:33 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.condition;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.uu.common.utils.DateUtils;
import com.uu.modules.mifi.entity.TestDevice.Status;
import com.uu.modules.om.entity.ConsumeRecord;

/** 
 * @Description 测试设备 查询条件类
 * @author yifang.huang
 * @date 2016年4月8日 上午11:52:33 
 */
public class TestDeviceCondition {
	
	private String likeImei;								// 设备编号 like 查询值
	
	private String eqImei;									// 设备编号 eq 查询值
	
	private String likeLendUserName;						// 借出人姓名 like 查询值
	
	private String geLendDate;								// 借出时间  ge 查询值

	private String leLendDate;								// 借出时间  le 查询值
	
	private Status eqStatus;								// 状态 eq 查询值
	
	
	public void build(DetachedCriteria dc) {
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		if (StringUtils.isNotBlank(likeImei)) {
			dc.add(Restrictions.like("imei",likeImei, MatchMode.ANYWHERE));
		}
		
		if (StringUtils.isNotBlank(eqImei)) {
			dc.add(Restrictions.eq("imei", eqImei));
		}
		
		if (StringUtils.isNotBlank(likeLendUserName)) {
			dc.add(Restrictions.like("lendUserName",likeLendUserName, MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(geLendDate)) {
			try {
				dc.add(Restrictions.ge("lendDate", DateUtils.getDateStart(df.parse(geLendDate))));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		if (StringUtils.isNotBlank(leLendDate)) {
			try {
				dc.add(Restrictions.le("lendDate", DateUtils.getDateEnd(df.parse(leLendDate))));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		if (null != eqStatus){
			dc.add(Restrictions.eq("status", eqStatus));
		}
		
		// 查询标记为0（正常）的数据
		dc.add(Restrictions.eq(ConsumeRecord.FIELD_DEL_FLAG, ConsumeRecord.DEL_FLAG_NORMAL));
		
		// 按创建时间降序排序
		dc.addOrder(Order.desc("createDate"));

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
	 * @return likeLendUserName
	 */
	public String getLikeLendUserName() {
		return likeLendUserName;
	}

	/** 
	 * @param likeLendUserName
	 */
	public void setLikeLendUserName(String likeLendUserName) {
		this.likeLendUserName = likeLendUserName;
	}

	/** 
	 * @return geLendDate
	 */
	public String getGeLendDate() {
		return geLendDate;
	}

	/** 
	 * @param geLendDate
	 */
	public void setGeLendDate(String geLendDate) {
		this.geLendDate = geLendDate;
	}

	/** 
	 * @return leLendDate
	 */
	public String getLeLendDate() {
		return leLendDate;
	}

	/** 
	 * @param leLendDate
	 */
	public void setLeLendDate(String leLendDate) {
		this.leLendDate = leLendDate;
	}

	/** 
	 * @return eqStatus
	 */
	public Status getEqStatus() {
		return eqStatus;
	}

	/** 
	 * @param eqStatus
	 */
	public void setEqStatus(Status eqStatus) {
		this.eqStatus = eqStatus;
	}
	
}
