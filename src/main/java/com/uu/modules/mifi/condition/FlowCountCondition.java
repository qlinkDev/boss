/** 
 * @Package com.uu.modules.mifi.condition 
 * @Description 
 * @author yifang.huang
 * @date 2017年5月23日 下午3:14:02 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.condition;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

/** 
 * @Description 流量统计(设备、订单、时间段) 查询条件类
 * @author yifang.huang
 * @date 2017年5月23日 下午3:14:02 
 */
public class FlowCountCondition {

	private String eqFlowCountRecordId;
	
	public void build(DetachedCriteria dc) {
		
		if (StringUtils.isNotBlank(eqFlowCountRecordId))
			dc.add(Restrictions.eq("flowCountRecord.id", eqFlowCountRecordId));
		
	}

	/** 
	 * @return eqFlowCountRecordId
	 */
	public String getEqFlowCountRecordId() {
		return eqFlowCountRecordId;
	}

	/** 
	 * @param eqFlowCountRecordId
	 */
	public void setEqFlowCountRecordId(String eqFlowCountRecordId) {
		this.eqFlowCountRecordId = eqFlowCountRecordId;
	}
	
}
