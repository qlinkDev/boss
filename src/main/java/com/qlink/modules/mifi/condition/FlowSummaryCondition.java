/** 
 * @Package com.uu.modules.mifi.condition 
 * @Description 
 * @author yifang.huang
 * @date 2017年5月23日 下午3:18:19 
 * @version V1.0 
 */ 
package com.qlink.modules.mifi.condition;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

/** 
 * @Description 流量统计汇总(设备数量、订单数量) 查询条件类
 * @author yifang.huang
 * @date 2017年5月23日 下午3:18:19 
 */
public class FlowSummaryCondition {

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
