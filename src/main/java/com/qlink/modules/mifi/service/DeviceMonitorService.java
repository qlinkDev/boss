/** 
 * @Package com.uu.modules.mifi.service 
 * @Description 
 * @author yifang.huang
 * @date 2016年5月20日 下午3:07:01 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.mifi.condition.DeviceMonitorCondition;
import com.uu.modules.mifi.dao.DeviceMonitorDao;
import com.uu.modules.mifi.entity.DeviceMonitor;

/** 
 * @Description 设备监控主体信息 业务处理类
 * @author yifang.huang
 * @date 2016年5月20日 下午3:07:01 
 */
@Service
public class DeviceMonitorService extends BaseService {
	
	@Autowired
	private DeviceMonitorDao deviceMonitorDao;
	
	/**
	 * 
	 * @Description 根据ID取数据
	 * @param id
	 * @return DeviceMonitor  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午3:09:37
	 */
	public DeviceMonitor get(String id) {
		return deviceMonitorDao.get(id);
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
	public void save(DeviceMonitor bean) {
		
		deviceMonitorDao.save(bean);
		
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
	public void saveOrUpdate(DeviceMonitor bean) {
		
		deviceMonitorDao.getSession().saveOrUpdate(bean);
		
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
		deviceMonitorDao.deleteById(id);
	}

	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<DeviceMonitor>  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午3:10:14
	 */
	public List<DeviceMonitor> findListByCondition(DeviceMonitorCondition condition) {
		
		DetachedCriteria dc = deviceMonitorDao.createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return deviceMonitorDao.find(dc);
	}
	
	/**
	 * 
	 * @Description 根据查询参数取分页数据
	 * @param page
	 * @param condition
	 * @return Page<DeviceMonitor>  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午3:10:24
	 */
	public Page<DeviceMonitor> find(Page<DeviceMonitor> page, DeviceMonitorCondition condition) {
		
		DetachedCriteria dc = deviceMonitorDao.createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return deviceMonitorDao.find(page, dc);
	}
	
}
