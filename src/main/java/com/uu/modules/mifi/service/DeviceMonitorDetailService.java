/** 
 * @Package com.uu.modules.mifi.service 
 * @Description 
 * @author yifang.huang
 * @date 2016年5月20日 下午3:07:01 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.mifi.condition.DeviceMonitorDetailCondition;
import com.uu.modules.mifi.dao.DeviceMonitorDetailDao;
import com.uu.modules.mifi.entity.DeviceMonitorDetail;

/** 
 * @Description 设备监控详细信息 业务处理类
 * @author yifang.huang
 * @date 2016年5月20日 下午3:07:01 
 */
@Service
public class DeviceMonitorDetailService extends BaseService {
	
	@Autowired
	private DeviceMonitorDetailDao deviceMonitorDetailDao;
	
	/**
	 * 
	 * @Description 根据ID取数据
	 * @param id
	 * @return DeviceMonitorDetail  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午3:09:37
	 */
	public DeviceMonitorDetail get(String id) {
		return deviceMonitorDetailDao.get(id);
	}
	
	/**
	 * 
	 * @Description 保存数据
	 * @param bean 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午3:09:45
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void save(DeviceMonitorDetail bean) {
		
		deviceMonitorDetailDao.save(bean);
		
	}
	
	/**
	 * 
	 * @Description 保存数据
	 * @param bean 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午3:09:58
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveOrUpdate(DeviceMonitorDetail bean) {
		
		deviceMonitorDetailDao.getSession().saveOrUpdate(bean);
		
	}
	
	/**
	 * 
	 * @Description 根据ID删除数据
	 * @param id 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午3:10:05
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void delete(String id) {
		deviceMonitorDetailDao.deleteById(id);
	}

	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<DeviceMonitorDetail>  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午3:10:14
	 */
	public List<DeviceMonitorDetail> findListByCondition(DeviceMonitorDetailCondition condition) {
		
		DetachedCriteria dc = deviceMonitorDetailDao.createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return deviceMonitorDetailDao.find(dc);
	}
	
	/**
	 * 
	 * @Description 根据查询参数取分页数据
	 * @param page
	 * @param condition
	 * @return Page<DeviceMonitorDetail>  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午3:10:24
	 */
	public Page<DeviceMonitorDetail> find(Page<DeviceMonitorDetail> page, DeviceMonitorDetailCondition condition) {
		
		DetachedCriteria dc = deviceMonitorDetailDao.createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return deviceMonitorDetailDao.find(page, dc);
	}
	
}
