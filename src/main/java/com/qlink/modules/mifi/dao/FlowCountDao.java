/** 
 * @Package com.uu.modules.mifi.dao 
 * @Description 
 * @author yifang.huang
 * @date 2017年5月23日 下午3:21:49 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Page;
import com.uu.modules.mifi.condition.FlowCountCondition;
import com.uu.modules.mifi.entity.FlowCount;

/** 
 * @Description 流量统计(设备、订单、时间段) DAO接口类
 * @author yifang.huang
 * @date 2017年5月23日 下午3:21:49 
 */
@Repository
public class FlowCountDao extends BaseDao<FlowCount> {

	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<FlowCount>  
	 * @author yifang.huang
	 * @date 2016年11月22日 上午10:17:04
	 */
	public List<FlowCount> findList(FlowCountCondition condition) {
		
		DetachedCriteria dc = createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return find(dc);
	}
	
	/**
	 * 
	 * @Description 根据查询参数取分页数据
	 * @param page
	 * @param condition
	 * @return Page<FlowCount>  
	 * @author yifang.huang
	 * @date 2016年11月22日 上午10:17:13
	 */
	public Page<FlowCount> findPage(Page<FlowCount> page, FlowCountCondition condition) {
		
		DetachedCriteria dc = createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return find(page, dc);
	}

}
