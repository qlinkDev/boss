package com.qlink.modules.mifi.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.qlink.common.persistence.BaseDao;
import com.qlink.modules.mifi.entity.MifiUsageRecord;

@Repository
public class MifiUsageRecordDao extends BaseDao<MifiUsageRecord> {

	public static Logger logger = LoggerFactory.getLogger(MifiUsageRecordDao.class);
	
}
