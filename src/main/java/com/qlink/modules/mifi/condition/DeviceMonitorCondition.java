/** 
 * @Package com.uu.modules.mifi.condition 
 * @Description 
 * @author yifang.huang
 * @date 2016年5月20日 下午2:53:11 
 * @version V1.0 
 */ 
package com.qlink.modules.mifi.condition;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.qlink.common.utils.DateUtils;
import com.qlink.modules.om.entity.ConsumeRecord;

/** 
 * @Description 设备监控主体信息 查询条件类
 * @author yifang.huang
 * @date 2016年5月20日 下午2:53:11 
 */
public class DeviceMonitorCondition {

	private String likeCode;						// 监控编号 like 查询值
	
	private String geStartDate;						// 监控开始时间  ge 查询值

	private String leStartDate;						// 监控开始时间  le 查询值
	
	public void build(DetachedCriteria dc) {
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		if (StringUtils.isNotBlank(likeCode)) {
			dc.add(Restrictions.like("code", likeCode, MatchMode.ANYWHERE));
		}
		
		if (StringUtils.isNotBlank(geStartDate)) {
			try {
				dc.add(Restrictions.ge("startDate", DateUtils.getDateStart(df.parse(geStartDate))));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		if (StringUtils.isNotBlank(leStartDate)) {
			try {
				dc.add(Restrictions.le("startDate", DateUtils.getDateEnd(df.parse(leStartDate))));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	
		// 查询标记为0（正常）的数据
		dc.add(Restrictions.eq(ConsumeRecord.FIELD_DEL_FLAG, ConsumeRecord.DEL_FLAG_NORMAL));
		
		// 按创建时间降序排序
		dc.addOrder(Order.desc("updateDate"));
		
	}

	/** 
	 * @return likeCode
	 */
	public String getLikeCode() {
		return likeCode;
	}

	/** 
	 * @param likeCode
	 */
	public void setLikeCode(String likeCode) {
		this.likeCode = likeCode;
	}

	/** 
	 * @return geStartDate
	 */
	public String getGeStartDate() {
		return geStartDate;
	}

	/** 
	 * @param geStartDate
	 */
	public void setGeStartDate(String geStartDate) {
		this.geStartDate = geStartDate;
	}

	/** 
	 * @return leStartDate
	 */
	public String getLeStartDate() {
		return leStartDate;
	}

	/** 
	 * @param leStartDate
	 */
	public void setLeStartDate(String leStartDate) {
		this.leStartDate = leStartDate;
	}

}
