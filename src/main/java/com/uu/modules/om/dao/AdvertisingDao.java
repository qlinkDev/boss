/** 
 * @Package com.uu.modules.om.dao 
 * @Description 
 * @author yifang.huang
 * @date 2016年12月16日 下午1:30:59 
 * @version V1.0 
 */ 
package com.uu.modules.om.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Page;
import com.uu.modules.om.condition.AdvertisingCondition;
import com.uu.modules.om.entity.Advertising;

/** 
 * @Description 广告   DAO接口
 * @author yifang.huang
 * @date 2016年12月16日 下午1:30:59 
 */
@Repository
public class AdvertisingDao extends BaseDao<Advertising> {

	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<Advertising>  
	 * @author yifang.huang
	 * @date 2016年12月16日 下午1:32:07
	 */
	public List<Advertising> findList(AdvertisingCondition condition) {
		
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
	 * @return Page<Advertising>  
	 * @author yifang.huang
	 * @date 2016年12月16日 下午1:32:15
	 */
	public Page<Advertising> findPage(Page<Advertising> page, AdvertisingCondition condition) {
		
		DetachedCriteria dc = createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return find(page, dc);
	}

}
