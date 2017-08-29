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
import com.uu.modules.mifi.condition.FlowSummaryCondition;
import com.uu.modules.mifi.dao.FlowSummaryDao;
import com.uu.modules.mifi.dao.FlowSummaryItemDao;
import com.uu.modules.mifi.entity.FlowSummary;

/** 
 * @Description 流量统计汇总(设备数量、订单数量) 业务处理类
 * @author yifang.huang
 * @date 2017年5月23日 下午3:30:11 
 */
@Service
public class FlowSummaryService extends BaseService {
	
	@Autowired
	private FlowSummaryDao flowSummaryDao;
	
	@Autowired
	private FlowSummaryItemDao flowSummaryItemDao;
	
	/**
	 * 
	 * @Description 根据ID取数据
	 * @param id
	 * @return FlowSummary  
	 * @author yifang.huang
	 * @date 2017年5月23日 下午3:38:36
	 */
	public FlowSummary get(String id) {
		FlowSummary oldBean = flowSummaryDao.get(id);
		if (oldBean != null) {
			FlowSummary newBean = new FlowSummary();
			BeanUtils.copyProperties(oldBean, newBean);
			
			// 清除指定对象缓存
			flowSummaryDao.getSession().evict(oldBean);
			
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
	public void save(FlowSummary bean) {
		
		flowSummaryDao.save(bean);
		
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
		flowSummaryDao.deleteById(id);
	}

	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<FlowSummary>  
	 * @author yifang.huang
	 * @date 2017年5月23日 下午3:47:05
	 */
	public List<FlowSummary> findList(FlowSummaryCondition condition) {
		
		List<FlowSummary> list = flowSummaryDao.findList(condition);
		
		if (list==null || list.size()==0)
			return list;
		else {// 取量统计汇总项(国家总流量)
			List<FlowSummary> result = new ArrayList<FlowSummary>();
			for (FlowSummary bean : list) {
				bean.setItemList(flowSummaryItemDao.findList(bean.getId()));
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
	 * @return Page<FlowSummary>  
	 * @author yifang.huang
	 * @date 2017年5月23日 下午3:47:14
	 */
	public Page<FlowSummary> find(Page<FlowSummary> page, FlowSummaryCondition condition) {
		
		Page<FlowSummary> result = flowSummaryDao.findPage(page, condition);
		List<FlowSummary> list = result.getList();
		
		if (list==null || list.size()==0)
			return result;
		else {// 取量统计汇总项(国家总流量)
			List<FlowSummary> tempList = new ArrayList<FlowSummary>();
			for (FlowSummary bean : list) {
				bean.setItemList(flowSummaryItemDao.findList(bean.getId()));
				tempList.add(bean);
			}
			result.setList(tempList);
			return result;
		}
	}

}
