package com.uu.modules.mifi.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Parameter;
import com.uu.common.utils.StringUtils;
import com.uu.modules.mifi.entity.SimBankIp;

/**
 * SIMBankIp管理
 * 
 * @Description
 * @author wangsai
 * @date 2016年11月3日 上午11:27:35
 */
@Repository
public class SimBankIpDao extends BaseDao<SimBankIp> {

	public static Logger logger = LoggerFactory.getLogger(SimBankIpDao.class);

	public int getMaxId() {

		String sql = "SELECT MAX(ID) FROM simbank_ip";

		List<Object> list = findBySql(sql);
		if (list != null && list.size() > 0) {
			return Integer.valueOf(list.get(0).toString()) + 1;
		}

		return 1;
	}

	public boolean isRankExists(String simbankid) {
		String sqlStr = "select 1 from dual where exists(select 1 from simbank_ip where SIMBANKID=:p1 )";
		Parameter param = new Parameter(simbankid);
		List<Object> resultList = findBySql(sqlStr, param);
		if (StringUtils.isEmpty(resultList)) {
			return false;
		}
		return true;
	}

	public boolean isRankExistss(String simbankid, String id) {
		String sqlStr = "select 1 from dual where exists(select 1 from   simbank_ip where SIMBANKID=:p1 and id=:p2 )";
		Parameter param = new Parameter(simbankid, id);
		List<Object> resultList = findBySql(sqlStr, param);
		if (StringUtils.isEmpty(resultList)) {
			return false;
		}
		return true;
	}

}
