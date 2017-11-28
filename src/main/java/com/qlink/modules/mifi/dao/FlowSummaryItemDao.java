/** 
 * @Package com.uu.modules.mifi.dao 
 * @Description 
 * @author yifang.huang
 * @date 2017年5月23日 下午3:21:49 
 * @version V1.0 
 */ 
package com.qlink.modules.mifi.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.qlink.common.persistence.BaseDao;
import com.qlink.modules.mifi.entity.FlowSummaryItem;

/** 
 * @Description 流量统计汇总项(国家总流量) DAO接口类
 * @author yifang.huang
 * @date 2017年5月23日 下午3:21:49 
 */
@Repository
public class FlowSummaryItemDao extends BaseDao<FlowSummaryItem> {

	/**
	 * 
	 * @Description 列表查询
	 * @param flowSummaryId
	 * @return List<FlowSummaryItem>  
	 * @author yifang.huang
	 * @date 2017年5月23日 下午3:40:13
	 */
	public List<FlowSummaryItem> findList(String flowSummaryId) {
		
		if (StringUtils.isBlank(flowSummaryId))
			return null;
		
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("flowSummary.id", flowSummaryId));
		
		return find(dc);
	}
	
	/**
	 * 
	 * @Description 批量删除
	 * @param flowSummaryId 
	 * @return void  
	 * @author yifang.huang
	 * @date 2017年5月23日 下午3:40:58
	 */
	public void deleteByFlowSummaryId(String flowSummaryId) {
		
		if (StringUtils.isBlank(flowSummaryId))
			return;
		
		String sql = "delete from FlowSummaryItem where flowSummaryId='" + flowSummaryId + "'";
		update(sql);
		
	}

}
