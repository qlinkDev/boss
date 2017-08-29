/** 
 * @Package com.uu.modules.mifi.service 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月15日 上午10:18:20 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.common.utils.excel.ExportExcel;
import com.uu.modules.mifi.condition.CardMonitorCondition;
import com.uu.modules.mifi.dao.CardMonitorDao;
import com.uu.modules.mifi.entity.CardMonitor;

/** 
 * @Description 监控信息 业务处理类
 * @author yifang.huang
 * @date 2016年4月15日 上午10:18:20 
 */
@Service
public class CardMonitorService extends BaseService {
	
	@Autowired
	private CardMonitorDao cardMonitorDao;
	
	/**
	 * 
	 * @Description 根据ID取数据
	 * @param id
	 * @return CardMonitor  
	 * @author yifang.huang
	 * @date 2016年4月15日 上午10:21:54
	 */
	public CardMonitor get(String id) {
		return cardMonitorDao.get(id);
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
	public void save(CardMonitor bean) {
		
		cardMonitorDao.save(bean);
		
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
	public void saveOrUpdate(CardMonitor bean) {
		
		cardMonitorDao.getSession().saveOrUpdate(bean);
		
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
		cardMonitorDao.deleteById(id);
	}

	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<CardMonitor>  
	 * @author yifang.huang
	 * @date 2016年4月15日 上午10:21:30
	 */
	public List<CardMonitor> findListByCondition(CardMonitorCondition condition) {
		
		DetachedCriteria dc = cardMonitorDao.createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return cardMonitorDao.find(dc);
	}
	
	/**
	 * 
	 * @Description 根据查询参数取分页数据
	 * @param page
	 * @param condition
	 * @return Page<CardMonitor>  
	 * @author yifang.huang
	 * @date 2016年4月15日 上午10:21:21
	 */
	public Page<CardMonitor> find(Page<CardMonitor> page, CardMonitorCondition condition) {
		DetachedCriteria dc = cardMonitorDao.createDetachedCriteria();
		// build 查询条件
		condition.build(dc);
		return cardMonitorDao.find(page, dc);
	}
	
	/**
	 * 监控信息导出
	 * @Description 
	 * @param condition
	 * @return 
	 * @return ExportExcel  
	 * @author yuxiaoyu
	 * @date 2016年12月13日 下午2:54:52
	 */
	public ExportExcel excelExport(CardMonitorCondition condition) {
		DetachedCriteria detachedCriteria = cardMonitorDao.createDetachedCriteria();
		condition.build(detachedCriteria);
		
		List<CardMonitor> dataList = cardMonitorDao.find(detachedCriteria);
		ExportExcel exportExcel = new ExportExcel("监控信息列表", CardMonitor.class, 0);
		exportExcel.setDataList(dataList);
		return exportExcel;
	}
	
	/**
	 * 
	 * @Description 根据mcc取国家编号
	 * @param mcc
	 * @return HashMap<String,String>  
	 * @author yifang.huang
	 * @date 2016年4月15日 上午10:50:41
	 */
	public HashMap<String, String> findCountryByMcc(String mcc) {
		return cardMonitorDao.findCountryByMcc(mcc);
	}
	
	/**
	 * 
	 * @Description 按imei分组取最新记录列表
	 * @param imei	编号
	 * @param faultCode	卡监控错误编号
	 * @param startDate	开始时间
	 * @param endDate	结束时间
	 * @return List<Map<String,String>>  
	 * @author yifang.huang
	 * @date 2017年1月12日 下午1:59:58
	 */
	public List<Map<String, String>> findList(String imei, String faultCode, String startDate, String endDate) {
		
		return cardMonitorDao.findList(imei, faultCode, startDate, endDate);
	}
}
