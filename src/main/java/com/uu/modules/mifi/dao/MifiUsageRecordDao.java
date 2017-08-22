package com.uu.modules.mifi.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.modules.mifi.entity.MifiUsageRecord;

@Repository
public class MifiUsageRecordDao extends BaseDao<MifiUsageRecord> {

	public static Logger logger = LoggerFactory.getLogger(MifiUsageRecordDao.class);
	
}
