/** 
 * @Package com.uu.modules.mifi.service 
 * @Description 
 * @author yuxiaoyu
 * @date 2017年2月23日 上午11:36:41 
 * @version V1.0 
 */
package com.uu.modules.mifi.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Parameter;
import com.uu.common.service.BaseService;
import com.uu.common.utils.DateUtils;
import com.uu.modules.mifi.dao.MifiOrderDao;

/** 
 * @Description 
 * @author yuxiaoyu
 * @date 2017年2月23日 上午11:36:41 
 */
@Service
public class DeviceProfileService extends BaseService {
	@Autowired
	private MifiOrderDao mifiOrderDao;

	public List<Map<String, Object>> getOrderAllowedMcc(List<String> iccIdList) {
		String sqlString = "SELECT l.vficcid, o.allowed_mcc FROM mifilist l, mifi_order_detail od, mifi_order o WHERE l.VFICCID IN :p1 AND l.IMEI_6200 = od.dsn AND od.order_id = o.order_id AND o.order_status = '1' and :p2 between o.start_date and o.end_date";
		String now = DateUtils.getDate(DateUtils.YYYY_MM_DD_HH_MM_SS);
		Parameter param = new Parameter(iccIdList, now);
		List<Map<String, Object>> resultList = mifiOrderDao.findBySql(sqlString, param, Map.class);
		return resultList;
	}
	
	public List<Map<String, Object>> getDeviceSn(List<String> iccIdList) {
		String sqlString = "select vficcid, imei_6200 from mifilist where vficcid in :p1";
		Parameter param = new Parameter(iccIdList);
		List<Map<String, Object>> resultList = mifiOrderDao.findBySql(sqlString, param, Map.class);
		return resultList;
	}
}
