package com.uu.modules.mifi.service;

import java.util.HashMap;

import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.mifi.condition.WorkOrderCondition;
import com.uu.modules.mifi.dao.MifiManageDao;
import com.uu.modules.mifi.dao.WorkOrderDao;
import com.uu.modules.mifi.entity.WorkOrder;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.utils.UserUtils;

/**
 * 工单业务层
 * 
 * @author shuxin
 * @date 2016年5月31日
 */
@Service
public class WorkOrderService extends BaseService {
	@Autowired
	private WorkOrderDao workOrderDao;
	@Autowired
	private MifiManageDao mifiManageDao;

	public static Logger logger = LoggerFactory.getLogger(WorkOrderService.class);

	/**
	 * 工单分页
	 * 
	 * @athor shuxin
	 * @date 2016年6月6日上午9:51:34
	 * @param page
	 * @param condition
	 * @return Page<WorkOrder>
	 */
	public Page<WorkOrder> findWorkOrdersByPage(Page<WorkOrder> page, WorkOrderCondition condition) {
		DetachedCriteria dc = workOrderDao.createDetachedCriteria();
		condition.build(dc);
		return workOrderDao.find(page, dc);
	}
	
	/**
	 * 保存工单
	 * @athor shuxin
	 * @date 2016年6月6日上午11:26:30
	 * @param workOrder
	 * void 
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveWorkOrder(WorkOrder workOrder){
		workOrderDao.save(workOrder);
	}
	
	/**
	 * 获取工单对象
	 * @athor shuxin
	 * @date 2016年6月6日下午5:12:44
	 * @param wid
	 * @return
	 * WorkOrder 
	 */
	public WorkOrder getWorkOrderById(Integer wid){
		return workOrderDao.getByHql(" from WorkOrder w where w.wid = "+wid);
	}

	/**
	 *  查看设备编号是否属于渠道商
	 * @athor shuxin
	 * @date 2016年6月16日上午10:05:47
	 * @param deviceSn
	 * @return
	 * Boolean
	 */
	public Boolean isBelongToChannel(String deviceSn) {
		User user = UserUtils.getUser();
		HashMap<String, String> mifiMap = mifiManageDao.getMifilistBySn(deviceSn);
		if(mifiMap != null){
			if(user.getChannelNameEn().equals(mifiMap.get("sourceType"))){
				return true;
			}
		}
		return false;
	}
	
}
