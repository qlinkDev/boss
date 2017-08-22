/** 
 * @Package com.uu.modules.mifi.dao 
 * @Description 
 * @author yifang.huang
 * @date 2017年5月23日 下午3:21:49 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.modules.mifi.entity.FlowCountItem;

/** 
 * @Description 流量统计项(国家流量) DAO接口类
 * @author yifang.huang
 * @date 2017年5月23日 下午3:21:49 
 */
@Repository
public class FlowCountItemDao extends BaseDao<FlowCountItem> {

	/**
	 * 
	 * @Description 列表查询
	 * @param flowCountId
	 * @return List<FlowCountItem>  
	 * @author yifang.huang
	 * @date 2017年5月23日 下午3:40:13
	 */
	public List<FlowCountItem> findList(String flowCountId) {
		
		if (StringUtils.isBlank(flowCountId))
			return null;
		
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("flowCount.id", flowCountId));
		
		return find(dc);
	}
	
	/**
	 * 
	 * @Description 批量删除
	 * @param flowCountId 
	 * @return void  
	 * @author yifang.huang
	 * @date 2017年5月23日 下午3:40:58
	 */
	public void deleteByFlowCountId(String flowCountId) {
		
		if (StringUtils.isBlank(flowCountId))
			return;
		
		String sql = "delete from FlowCountItem where flowCountId='" + flowCountId + "'";
		update(sql);
		
	}

}
