/** 
 * @Package com.uu.modules.mifi.service 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月8日 下午1:56:32 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.mifi.condition.TestDeviceCondition;
import com.uu.modules.mifi.dao.MifiManageDao;
import com.uu.modules.mifi.dao.TestDeviceDao;
import com.uu.modules.mifi.entity.TestDevice;
import com.uu.modules.utils.Constants;

/** 
 * @Description 测试设备 业务处理
 * @author yifang.huang
 * @date 2016年4月8日 下午1:56:32 
 */
@Service
public class TestDeviceService extends BaseService {
	
	@Autowired
	private TestDeviceDao testDeviceDao;
	
	@Autowired
	private MifiManageDao mifiManageDao;
	
	/**
	 * 
	 * @Description 根据ID取数据
	 * @param id
	 * @return TestDevice  
	 * @author yifang.huang
	 * @date 2016年4月8日 下午1:59:18
	 */
	public TestDevice get(String id) {
		return testDeviceDao.get(id);
	}
	
	/**
	 * 
	 * @Description 保存数据
	 * @param bean 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年4月8日 下午1:59:22
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void save(TestDevice bean) {
		
		testDeviceDao.save(bean);
		
		if (StringUtils.isBlank(bean.getReturnUserName())) {
			// 修改设备可用状态
			mifiManageDao.updateMifilistUeAllowed(Constants.ueAllowed_y, bean.getImei());
			
			// 修改设备ueallowedmcc为‘ALL’
			mifiManageDao.updateMifilistUeAllowedMcc("ALL", bean.getImei());
		} else {
			// 修改设备不可用状态
			mifiManageDao.updateMifilistUeAllowed(Constants.ueAllowed_n, bean.getImei());
			
			// 修改设备ueallowedmcc为‘’
			mifiManageDao.updateMifilistUeAllowedMcc("", bean.getImei());
		}
		
	}
	
	/**
	 * 
	 * @Description 根据ID删除数据
	 * @param id 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年4月8日 下午1:59:26
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void delete(String id) {
		testDeviceDao.deleteById(id);
	}

	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<TestDevice>  
	 * @author yifang.huang
	 * @date 2016年4月8日 下午2:00:18
	 */
	public List<TestDevice> findListByCondition(TestDeviceCondition condition) {
		
		DetachedCriteria dc = testDeviceDao.createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return testDeviceDao.find(dc);
	}
	
	/**
	 * 
	 * @Description 根据查询参数取分页数据
	 * @param page
	 * @param condition
	 * @return Page<TestDevice>  
	 * @author yifang.huang
	 * @date 2016年4月8日 下午2:00:27
	 */
	public Page<TestDevice> find(Page<TestDevice> page, TestDeviceCondition condition) {
		
		DetachedCriteria dc = testDeviceDao.createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return testDeviceDao.find(page, dc);
	}
}
