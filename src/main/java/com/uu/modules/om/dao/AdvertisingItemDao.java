/** 
 * @Package com.uu.modules.om.dao 
 * @Description 
 * @author yifang.huang
 * @date 2016年12月16日 下午1:33:06 
 * @version V1.0 
 */ 
package com.uu.modules.om.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.modules.om.entity.AdvertisingItem;

/** 
 * @Description 广告们    DAO接口
 * @author yifang.huang
 * @date 2016年12月16日 下午1:33:06 
 */
@Repository
public class AdvertisingItemDao extends BaseDao<AdvertisingItem> {

	/**
	 * 
	 * @Description 列表查询
	 * @param advertisingId	广告ID
	 * @return List<AdvertisingItem>  
	 * @author yifang.huang
	 * @date 2016年12月16日 下午1:34:42
	 */
	public List<AdvertisingItem> findList(String advertisingId) {
		
		if (StringUtils.isBlank(advertisingId))
			return null;
		
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("advertisingId", advertisingId));
		dc.addOrder(Order.asc("sequence"));   // 按序号升序排序
		
		return find(dc);
	}
	
	/**
	 * 
	 * @Description 批量删除
	 * @param advertisingId
	 * @return int  
	 * @author yifang.huang
	 * @date 2016年12月16日 下午1:35:33
	 */
	public int deleteByAdvertisingId(String advertisingId) {
		
		if (StringUtils.isBlank(advertisingId))
			return 0;
		
		String sql = "delete from AdvertisingItem where advertisingId='" + advertisingId + "'";
		
		return update(sql);
		
	}

}
