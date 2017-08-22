/** 
 * @Package com.uu.modules.mifi.dao 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月15日 上午10:16:58 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.modules.mifi.entity.CardMonitor;

/** 
 * @Description 卡监控信息 DAO
 * @author yifang.huang
 * @date 2016年4月15日 上午10:16:58 
 */
@Repository
public class CardMonitorDao extends BaseDao<CardMonitor> {
	
	/**
	 * 
	 * @Description 根据mcc取国家编号
	 * @param mcc
	 * @return HashMap<String,String>  
	 * @author yifang.huang
	 * @date 2016年4月15日 上午10:50:41
	 */
	public HashMap<String, String> findCountryByMcc(String mcc) {
		
		List<HashMap<String, String>> list = findBySql("select country_code countryCode, country_name_cn countryName, country_name_en countryNameEn from mcc_def where mcc='" + mcc + "'", null, Map.class);
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		
		return null;
		
	}
	
	/**
	 * 
	 * @Description 按imei分组取最新记录列表
	 * @param imei	编号
	 * @param faultCode	卡监控错误编号
	 * @param startDate	开始时间
	 * @param endDate	结束时间
	 * @return List<Map<String,String>>  
	 * @author yifang.huang
	 * @date 2017年1月12日 下午1:59:58
	 */
	public List<Map<String, String>> findList(String imei, String faultCode, String startDate, String endDate) {
		
		StringBuffer sb = new StringBuffer("select tt.imei, tt.createDate from (select a.imei, DATE_FORMAT(a.create_date, '%Y-%m-%d %H:%i:%s') createDate from mifi_card_monitor a where 1=1");
		
		if (StringUtils.isNotBlank(imei))
			sb.append(" and a.imei='" + imei + "'");
		if (StringUtils.isNotBlank(faultCode))
			sb.append(" and a.fault_code='" + faultCode + "'");
		if (StringUtils.isNotBlank(startDate))
			sb.append(" and a.create_date>='" + startDate + "'");
		if (StringUtils.isNotBlank(endDate))
			sb.append(" and a.create_date<'" + endDate + "'");
		
		sb.append(" order by a.create_date desc) tt group by tt.imei");
		
		return findBySql(sb.toString(), null, Map.class);
	}
	
}
