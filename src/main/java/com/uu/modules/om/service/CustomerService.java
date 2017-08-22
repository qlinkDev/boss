/** 
 * @Package com.uu.modules.om.service 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月29日 下午4:35:20 
 * @version V1.0 
 */ 
package com.uu.modules.om.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.om.condition.CustomerCondition;
import com.uu.modules.om.dao.CustomerDao;
import com.uu.modules.om.entity.Customer;

/** 
 * @Description 客户 业务处理类
 * @author yifang.huang
 * @date 2016年4月29日 下午4:35:20 
 */
@Service
public class CustomerService extends BaseService {
	
	@Autowired
	private CustomerDao customerDao;
	/**
	 * 
	 * @Description 根据ID取数据
	 * @param id
	 * @return Customer  
	 * @author yifang.huang
	 * @date 2016年4月15日 上午10:21:54
	 */
	public Customer get(String id) {
		Customer oldBean = customerDao.get(id);
		if (oldBean != null) {
			Customer newBean = new Customer();
			BeanUtils.copyProperties(oldBean, newBean);
			// 清除指定对象缓存
			customerDao.getSession().evict(oldBean);
			return newBean;
		}
		return null;
	}
	
	/**
	 * 
	 * @Description 保存数据
	 * @param bean 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年4月15日 上午10:21:47
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void save(Customer bean) {
		
		customerDao.save(bean);
		
	}
	
	/**
	 * 
	 * @Description 根据ID删除数据
	 * @param id 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年4月15日 上午10:21:39
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void delete(String id) {
		customerDao.deleteById(id);
	}

	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<Customer>  
	 * @author yifang.huang
	 * @date 2016年4月15日 上午10:21:30
	 */
	public List<Customer> findListByCondition(CustomerCondition condition) {
		
		DetachedCriteria dc = customerDao.createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return customerDao.find(dc);
	}
	
	/**
	 * 
	 * @Description 根据查询参数取分页数据
	 * @param page
	 * @param condition
	 * @return Page<Customer>  
	 * @author yifang.huang
	 * @date 2016年4月15日 上午10:21:21
	 */
	public Page<Customer> find(Page<Customer> page, CustomerCondition condition) {
		
		DetachedCriteria dc = customerDao.createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return customerDao.find(page, dc);
	}
	
}
