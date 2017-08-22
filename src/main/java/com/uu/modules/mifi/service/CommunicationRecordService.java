package com.uu.modules.mifi.service;

import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.mifi.condition.CommunicationRecordCondition;
import com.uu.modules.mifi.dao.CommunicationRecordDao;
import com.uu.modules.mifi.entity.CommunicationRecord;

/**
 * 通信业务层
 * @author shuxin
 * @date 2016年7月20日	
 */
@Service
public class CommunicationRecordService extends BaseService {
	
	@Autowired
	private CommunicationRecordDao cRecordDao;

	public static Logger logger = LoggerFactory.getLogger(CommunicationRecordService.class);

	/**
	 * 分页
	 * @athor shuxin
	 * @date 2016年7月20日下午3:57:16
	 * @param page
	 * @param condition
	 * @return
	 * Page<CommunicationRecord> 
	 */
	public Page<CommunicationRecord> findcRecordsByPage(Page<CommunicationRecord> page, CommunicationRecordCondition condition) {
		DetachedCriteria dc = cRecordDao.createDetachedCriteria();
		condition.build(dc);
		return cRecordDao.find(page, dc);
	}
	
	/**
	 * 保存
	 * @athor shuxin
	 * @date 2016年7月21日下午2:16:07
	 * @param cRecord
	 * void 
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveCRecord(CommunicationRecord cRecord){
		cRecordDao.save(cRecord);
	}
	
	/**
	 * 获取通信记录
	 * @athor shuxin
	 * @date 2016年7月21日下午2:16:01
	 * @param id
	 * @return
	 * CommunicationRecord 
	 */
	public CommunicationRecord getCRecordById(String id){
		return cRecordDao.getByHql(" from CommunicationRecord t where t.id = '"+id + "'");
	}

}
