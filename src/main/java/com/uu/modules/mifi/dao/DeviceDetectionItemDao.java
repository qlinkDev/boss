/** 
 * @Package com.uu.modules.mifi.dao 
 * @Description 
 * @author yifang.huang
 * @date 2016年11月9日 下午3:30:08 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.modules.mifi.entity.DeviceDetectionItem;

/** 
 * @Description 设备检查项  DAO接口
 * @author yifang.huang
 * @date 2016年11月9日 下午3:30:08 
 */
@Repository
public class DeviceDetectionItemDao extends BaseDao<DeviceDetectionItem> {

	/**
	 * 
	 * @Description 列表查询
	 * @param detectionId	设备检查ID
	 * @return List<DeviceDetectionItem>  
	 * @author yifang.huang
	 * @date 2016年11月9日 下午3:31:37
	 */
	public List<DeviceDetectionItem> findList(String detectionId) {
		
		if (StringUtils.isBlank(detectionId))
			return null;
		
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("deviceDetectionId", detectionId));
		dc.addOrder(Order.asc("sequence"));   // 按序号升序排序
		
		return find(dc);
	}
	
	/**
	 * 
	 * @Description 批量删除
	 * @param itemList 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年11月10日 上午10:41:36
	 */
	public void deleteByDeviceDetectionId(String detectionId) {
		
		if (StringUtils.isBlank(detectionId))
			return;
		
		String sql = "delete from DeviceDetectionItem where deviceDetectionId='" + detectionId + "'";
		update(sql);
		
	}
	
}
