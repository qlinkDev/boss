/** 
 * @Package com.uu.modules.mifi.service 
 * @Description 
 * @author yifang.huang
 * @date 2017年5月23日 下午3:30:11 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.mifi.condition.FlowCountRecordCondition;
import com.uu.modules.mifi.dao.FlowCountDao;
import com.uu.modules.mifi.dao.FlowCountItemDao;
import com.uu.modules.mifi.dao.FlowCountRecordDao;
import com.uu.modules.mifi.dao.FlowSummaryDao;
import com.uu.modules.mifi.dao.FlowSummaryItemDao;
import com.uu.modules.mifi.dao.MifiManageDao;
import com.uu.modules.mifi.dao.MifiOrderDao;
import com.uu.modules.mifi.dao.MifiOrderDetailDao;
import com.uu.modules.mifi.dao.MifiTrafficDao;
import com.uu.modules.mifi.entity.FlowCount;
import com.uu.modules.mifi.entity.FlowCountItem;
import com.uu.modules.mifi.entity.FlowCountRecord;
import com.uu.modules.mifi.entity.FlowSummary;
import com.uu.modules.mifi.entity.FlowSummaryItem;
import com.uu.modules.om.entity.Region;

/** 
 * @Description 流量统计记录   业务处理类
 * @author yifang.huang
 * @date 2017年5月23日 下午3:30:11 
 */
@Service
public class FlowCountRecordService extends BaseService {
	
	@Autowired
	private FlowCountRecordDao flowCountRecordDao;
	
	@Autowired
	private FlowCountDao flowCountDao;
	
	@Autowired
	private FlowCountItemDao flowCountItemDao;
	
	@Autowired
	private FlowSummaryDao flowSummaryDao;
	
	@Autowired
	private FlowSummaryItemDao flowSummaryItemDao;
	
	@Autowired
	private MifiOrderDao mifiOrderDao;
	
	@Autowired
	private MifiOrderDetailDao mifiOrderDetailDao;
	
	@Autowired
	private MifiManageDao mifiManageDao;
	
	@Autowired
	private MifiTrafficDao mifiTrafficDao;
	
	
	/**
	 * 
	 * @Description 根据ID取数据
	 * @param id
	 * @return FlowCountRecord  
	 * @author yifang.huang
	 * @date 2017年5月23日 下午3:38:36
	 */
	public FlowCountRecord get(String id) {
		FlowCountRecord oldBean = flowCountRecordDao.get(id);
		if (oldBean != null) {
			FlowCountRecord newBean = new FlowCountRecord();
			BeanUtils.copyProperties(oldBean, newBean);
			
			// 清除指定对象缓存
			flowCountRecordDao.getSession().evict(oldBean);
			
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
	public void save(FlowCountRecord bean) {
		
		flowCountRecordDao.save(bean);
		
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
		flowCountRecordDao.deleteById(id);
	}

	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<FlowCountRecord>  
	 * @author yifang.huang
	 * @date 2017年5月23日 下午3:47:05
	 */
	public List<FlowCountRecord> findList(FlowCountRecordCondition condition) {
		
		return flowCountRecordDao.findList(condition);
	}
	
	/**
	 * 
	 * @Description 根据查询参数取分页数据
	 * @param page
	 * @param condition
	 * @return Page<FlowCountRecord>  
	 * @author yifang.huang
	 * @date 2017年5月23日 下午3:47:14
	 */
	public Page<FlowCountRecord> findPage(Page<FlowCountRecord> page, FlowCountRecordCondition condition) {
		
		return flowCountRecordDao.findPage(page, condition);
	}

	/**
	 * 
	 * @Description 按区域和时间统计设备在各个国家的流量
	 * @param paramMap
	 * @return Map<String, String>
	 * @author yifang.huang
	 * @throws Exception 
	 * @date 2017年5月23日 下午4:42:44
	 */
	public void countFlow(String startDate, String endDate, FlowCountRecord record) throws Exception {

		try {
			Region region = record.getRegion();
			String regionMcc = region.getMcces();
			
			// 时间段内订单查询
			List<Map<String, String>> orderList = mifiOrderDao.getOrderByTripDate(startDate, endDate);
			if (orderList==null || orderList.size()==0) {
				return;
			}
			
			// 取出包含当前区域的订单
			List<Map<String, String>> orderTempList = new ArrayList<Map<String, String>>();
			String orderMcc = null;
			for (Map<String, String> order : orderList) {
				orderMcc = order.get("allowedMcc");
				if (StringUtils.isBlank(orderMcc))
					continue;
				if (com.uu.common.utils.StringUtils.mccInclude(orderMcc, regionMcc)) {
					orderTempList.add(order);
				}
				orderMcc = null;
			}
			if (orderTempList.size() == 0) {
				return;
			}
			
			// 区域国家编码数组
			String countryCodes = region.getCountryCodes();
			String[] countryCodeArr = countryCodes.split(",");
			
			String dsns = null;
			String[] dsnArr = null;
			String startDateStr = null;
			String endDateStr = null;
			String ueid = null;
			String mcces = null;
			String flow = null;
			Map<String, Object> flowMap = null;
			Map<String, String> countryInfo = null;
			
			FlowCount count = null;
			FlowCountItem countItem = null;
			List<FlowCount> countList = new ArrayList<FlowCount>();
			List<FlowCountItem> countItemList = new ArrayList<FlowCountItem>();
			Integer deviceTotal = 0;
			Double tempFlowTotal= 0.00;
			List<FlowCountItem> tempCountItemList = null;
			int countryTotal = 0;
			int oneCountryDeviceTotal = 0;
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (Map<String, String> order : orderTempList) {
				System.out.println("order start:" + df.format(new Date()));
				// 根据订单查询设备
				dsns = mifiOrderDetailDao.getDsnByOrderId(order.get("orderId"));
				if (StringUtils.isBlank(dsns))
					continue;
				dsnArr = dsns.split(",");
				// 取查询时间和订单时间的交集
				startDateStr = com.uu.common.utils.DateUtils.getAfterDate(startDate, ObjectUtils.toString(order.get("startDate"))) + " 00:00:00";
				endDateStr = com.uu.common.utils.DateUtils.getBeforeDate(endDate, ObjectUtils.toString(order.get("endDate"))) + " 23:59:59";
				if (StringUtils.isBlank(startDateStr) || StringUtils.isBlank(endDateStr))
					continue;
				for (String dsn : dsnArr) {
					ueid = mifiManageDao.getDeviceUeid(dsn);
					if (StringUtils.isBlank(ueid))
						continue;
					
					deviceTotal ++;
					
					count = new FlowCount();
					count.setFlowCountRecord(record);
					count.setImei(dsn);
					count.setOrderCode(order.get("outOrderId"));
					count.setStartDate(startDateStr);
					count.setEndDate(endDateStr);
					countList.add(count);
					
					tempCountItemList = new ArrayList<FlowCountItem>();
					for (String countryCode : countryCodeArr) {
						
						countryInfo = mifiOrderDao.findMccByCountryCode(countryCode);
						if (countryInfo == null) {
							continue;
						}
						
						mcces = countryInfo.get("mcces");
						mcces = "'" + mcces.replace(",", "','") + "'";
						flowMap = mifiTrafficDao.findDeviceFlow(startDateStr, endDateStr, ueid, mcces);
						flow = ObjectUtils.toString(flowMap.get("dataInfo"));
						flow = (StringUtils.isBlank(flow) || "null".equals(flow)) ? "0" : flow;

						countItem = new FlowCountItem();
						countItem.setFlowCount(count);
						countItem.setCountryCode(countryInfo.get("countryName"));
						countItem.setCountryName(countryInfo.get("countryName"));
						countItem.setFlow(Double.valueOf(flow.replace(",", "")));
						countItemList.add(countItem);
						tempCountItemList.add(countItem);
						
						if (countItem.getFlow() > 0)
							countryTotal ++;
						tempFlowTotal = tempFlowTotal + countItem.getFlow();
						
						flow = null;
						mcces = null;
						flowMap = null;
						countryInfo = null;
						countItem = null;
					}
					
					count.setFlowTotal(tempFlowTotal);
					if (countryTotal == 0) {
						deviceTotal --;
						countList.remove(count);
						countItemList.removeAll(tempCountItemList);
					}
					if (countryTotal == 1) {
						oneCountryDeviceTotal ++;
					}
					
					ueid = null;
					count = null;
					tempFlowTotal = 0.00;
					tempCountItemList = null;
					countryTotal = 0;
				}
				
				dsns = null;
				dsnArr = null;
				startDateStr = null;
				endDateStr = null;
				System.out.println("order end:" + df.format(new Date()));
			}
			
			// 保存数据
			System.out.println("count save start:" + df.format(new Date()));
			flowCountDao.save(countList);
			flowCountItemDao.save(countItemList);
			System.out.println("count save end:" + df.format(new Date()));
			
			// 流量统计汇总
			if (countList.size() > 0) {
				System.out.println("summary start:" + df.format(new Date()));
				FlowSummary summary = new FlowSummary();
				summary.setFlowCountRecord(record);
				summary.setDeviceTotal(deviceTotal);
				summary.setOneCountryDeviceTotal(oneCountryDeviceTotal);
				summary.setOrderTotal(orderTempList.size());
				flowSummaryDao.save(summary);
				
				if (countItemList.size() > 0) {
					Map<String, Double> countryFlowMap = new HashMap<String, Double>();
					for (FlowCountItem item : countItemList) {
						Double countryFlow = countryFlowMap.get(item.getCountryName());
						if (countryFlow == null)
							countryFlowMap.put(item.getCountryName(), item.getFlow());
						else {
							countryFlow = countryFlow + item.getFlow();
							countryFlowMap.put(item.getCountryName(), countryFlow);
						}
					}

					Double flowTotal = 0.00;
					FlowSummaryItem summaryItem = null;
					List<FlowSummaryItem> summaryItemList = new ArrayList<FlowSummaryItem>();
					for (Map.Entry<String, Double> entry : countryFlowMap.entrySet()) {  
						summaryItem = new FlowSummaryItem();
						summaryItem.setFlowSummary(summary);
						summaryItem.setCountryName(entry.getKey());
						summaryItem.setFlow(entry.getValue());
						summaryItemList.add(summaryItem);
						
						flowTotal = flowTotal + entry.getValue();
						summaryItem = null;
					} 
					
					flowSummaryItemDao.save(summaryItemList);
					
					// 设备平均流量
					BigDecimal bd = new BigDecimal(String.valueOf(flowTotal/deviceTotal));
		            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
					summary.setFlowTotal(flowTotal);
					summary.setFlowAverage(bd.doubleValue());
					flowSummaryDao.save(summary);
				}
				System.out.println("summary end:" + df.format(new Date()));
			}
		} catch (NumberFormatException e) {

			e.printStackTrace();
			// 修改统计记录状态
			record.setStatus(FlowCountRecord.status_fail);
			flowCountRecordDao.save(record);
		}
	}

}
