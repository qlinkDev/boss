package com.uu.modules.mifi.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;

@Repository
public class MifiTrafficDao extends BaseDao{
	
	public static Logger logger = LoggerFactory.getLogger(MifiTrafficDao.class);

	
	/**
	 * 
	 * @Description 统计设备流量
	 * @param stratDate
	 * @param endDate
	 * @param ueid
	 * @param mcces
	 * @return Map<String,Object>  
	 * @author yifang.huang
	 * @date 2017年5月19日 下午4:57:50
	 */
	@SuppressWarnings("unchecked")
	public  Map<String, Object> findDeviceFlow(String stratDate, String endDate, String ueid, String mcces){
		
		StringBuffer sb = new StringBuffer("select format(sum(a.DATAADD)/1024/1024, 2) dataInfo "
				+ "from simcardstatus a where 1=1");
		
		if (StringUtils.isNotBlank(stratDate))
			sb.append(" and a.stamp_created>=str_to_date('" + stratDate + "','%Y-%m-%d %H:%i:%s')");
		if (StringUtils.isNotBlank(endDate))
			sb.append(" and a.stamp_created<=str_to_date('" + endDate + "','%Y-%m-%d %H:%i:%s')");
		if (StringUtils.isNotBlank(ueid))
			sb.append(" and a.UEID_CURRNUM='" + ueid + "'");
		if (StringUtils.isNotBlank(mcces))
			sb.append(" and a.UEID_MCC in(" + mcces + ")");
		
		List<Map<String, Object>> list = findBySql(sb.toString(), null, Map.class);
		if (list==null || list.size()==0)
			return null;
		return list.get(0);
	}
	
}
