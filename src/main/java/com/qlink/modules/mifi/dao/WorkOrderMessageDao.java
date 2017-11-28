package com.qlink.modules.mifi.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.qlink.common.persistence.BaseDao;
import com.qlink.modules.mifi.entity.WorkOrderMessage;

/**
 * 工单会话消息DAO
 * 
 * @author shuxin
 * @date 2016年5月31日
 */
@Repository
public class WorkOrderMessageDao extends BaseDao<WorkOrderMessage> {

	public static Logger logger = LoggerFactory.getLogger(WorkOrderMessageDao.class);

}
