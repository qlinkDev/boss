/** 
 * @Package com.uu.modules.om.dao 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月18日 上午11:13:16 
 * @version V1.0 
 */ 
package com.qlink.modules.mifi.dao;

import org.springframework.stereotype.Repository;

import com.qlink.common.persistence.BaseDao;
import com.qlink.modules.mifi.entity.CommunicationRecord;

/**
 * 通信记录DAO
 * @author shuxin
 * @date 2016年7月20日	
 */
@Repository
public class CommunicationRecordDao extends BaseDao<CommunicationRecord> {
	
	public CommunicationRecord getCRecordBySn(String sn){
		String hql = "from CommunicationRecord where deviceSn = '%"+sn+"%'";
		return this.getByHql(hql);
	}
}
