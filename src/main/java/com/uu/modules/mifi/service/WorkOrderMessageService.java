package com.uu.modules.mifi.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.service.BaseService;
import com.uu.modules.mifi.dao.WorkOrderMessageDao;
import com.uu.modules.mifi.entity.WorkOrderMessage;

/**
 * 工单会话消息业务层
 * 
 * @author shuxin
 * @date 2016年5月31日
 */
@Service
public class WorkOrderMessageService extends BaseService {
	@Autowired
	private WorkOrderMessageDao messageDao;

	public static Logger logger = LoggerFactory.getLogger(WorkOrderMessageService.class);

	/**
	 * 根据工单wid查找工单信息列表
	 * 
	 * @athor shuxin
	 * @date 2016年6月7日下午2:22:36
	 * @param wid
	 * @return List<WorkOrderMessage>
	 */
	public List<WorkOrderMessage> findWorkOrderMessagesByWid(Integer wid) {
		List<WorkOrderMessage> messages = new ArrayList<WorkOrderMessage>();
		if (wid != null) {
			messages = messageDao.find(" FROM WorkOrderMessage t  WHERE  t.wid =" + wid);
		}
		return messages;
	}

	/**
	 * 保存工单消息
	 * 
	 * @athor shuxin
	 * @date 2016年6月7日下午2:27:36
	 * @param message
	 *            void
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveWorkOrderMessage(WorkOrderMessage message) {
		messageDao.save(message);
	}

	/**
	 * 批量保存工单会话信息
	 * 
	 * @athor shuxin
	 * @date 2016年6月7日下午4:59:06
	 * @param entityList
	 *            void
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveWorkOrderMessageByList(List<WorkOrderMessage> entityList) {
		messageDao.save(entityList);
	}

}
