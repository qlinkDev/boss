/** 
 * @Package com.uu.modules.mifi.service 
 * @Description 
 * @author yifang.huang
 * @date 2017年5月23日 下午3:30:11 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.mifi.condition.FlowCountCondition;
import com.uu.modules.mifi.dao.FlowCountDao;
import com.uu.modules.mifi.dao.FlowCountItemDao;
import com.uu.modules.mifi.entity.FlowCount;

/** 
 * @Description 流量统计(设备、订单、时间段)   业务处理类
 * @author yifang.huang
 * @date 2017年5月23日 下午3:30:11 
 */
@Service
public class FlowCountService extends BaseService {
	
	@Autowired
	private FlowCountDao flowCountDao;
	
	@Autowired
	private FlowCountItemDao flowCountItemDao;
	
	/**
	 * 
	 * @Description 根据ID取数据
	 * @param id
	 * @return FlowCount  
	 * @author yifang.huang
	 * @date 2017年5月23日 下午3:38:36
	 */
	public FlowCount get(String id) {
		FlowCount oldBean = flowCountDao.get(id);
		if (oldBean != null) {
			FlowCount newBean = new FlowCount();
			BeanUtils.copyProperties(oldBean, newBean);
			
			// 清除指定对象缓存
			flowCountDao.getSession().evict(oldBean);
			
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
	 * @date 2017年5月23日 下午3:46:48
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void save(FlowCount bean) {
		
		flowCountDao.save(bean);
		
	}
	
	/**
	 * 
	 * @Description 根据ID删除数据
	 * @param id 
	 * @return void  
	 * @author yifang.huang
	 * @date 2017年5月23日 下午3:46:57
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void delete(String id) {
		flowCountDao.deleteById(id);
	}

	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<FlowCount>  
	 * @author yifang.huang
	 * @date 2017年5月23日 下午3:47:05
	 */
	public List<FlowCount> findList(FlowCountCondition condition) {
		
		List<FlowCount> list = flowCountDao.findList(condition);
		
		if (list==null || list.size()==0)
			return list;
		else {// 取流量统计项(国家流量)
			List<FlowCount> result = new ArrayList<FlowCount>();
			for (FlowCount bean : list) {
				bean.setItemList(flowCountItemDao.findList(bean.getId()));
				result.add(bean);
			}
			return result;
		}
		
	}
	
	/**
	 * 
	 * @Description 根据查询参数取分页数据
	 * @param page
	 * @param condition
	 * @return Page<FlowCount>  
	 * @author yifang.huang
	 * @date 2017年5月23日 下午3:47:14
	 */
	public Page<FlowCount> findPage(Page<FlowCount> page, FlowCountCondition condition) {
		
		Page<FlowCount> result = flowCountDao.findPage(page, condition);
		List<FlowCount> list = result.getList();
		
		if (list==null || list.size()==0)
			return result;
		else {// 取流量统计项(国家流量)
			List<FlowCount> tempList = new ArrayList<FlowCount>();
			for (FlowCount bean : list) {
				bean.setItemList(flowCountItemDao.findList(bean.getId()));
				tempList.add(bean);
			}
			result.setList(tempList);
			return result;
		}
	}

}
