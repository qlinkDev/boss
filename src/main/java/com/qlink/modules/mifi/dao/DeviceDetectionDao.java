/** 
 * @Package com.uu.modules.mifi.dao 
 * @Description 
 * @author yifang.huang
 * @date 2016年11月9日 下午3:27:09 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Page;
import com.uu.modules.mifi.condition.DeviceDetectionCondition;
import com.uu.modules.mifi.entity.DeviceDetection;

/** 
 * @Description 设备检票 DAO接口
 * @author yifang.huang
 * @date 2016年11月9日 下午3:27:09 
 */
@Repository
public class DeviceDetectionDao extends BaseDao<DeviceDetection> {

	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<DeviceDetection>  
	 * @author yifang.huang
	 * @date 2016年11月9日 下午3:29:15
	 */
	public List<DeviceDetection> findList(DeviceDetectionCondition condition) {
		
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
	 * @return Page<DeviceDetection>  
	 * @author yifang.huang
	 * @date 2016年11月9日 下午3:29:28
	 */
	public Page<DeviceDetection> findPage(Page<DeviceDetection> page, DeviceDetectionCondition condition) {
		
		DetachedCriteria dc = createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return find(page, dc);
	}
	
}
