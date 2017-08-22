/** 
 * @Package com.uu.modules.om.dao 
 * @Description 
 * @author yifang.huang
 * @date 2016-3-14 下午4:07:06 
 * @version V1.0 
 */ 
package com.uu.modules.om.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.modules.om.entity.ConsumeRecord;

/** 
 * @Description 消费记录DAO接口
 * @author yifang.huang
 * @date 2016-3-14 下午4:07:06 
 */
@Repository
public class ConsumeRecordDao extends BaseDao<ConsumeRecord> {

	/**
	 * 
	 * @Description 根据mcc取与北京的时间差
	 * @param mcc
	 * @return int  
	 * @author yifang.huang
	 * @date 2016年3月31日 下午5:48:41
	 */
	public int findBjTimeDifferenceByMcc(String mcc) {
		
		List<String> list = findBySql("select time_difference_bj from mcc_timer where mcc='" + mcc + "'");
		if (list!=null && list.size()>0) {
			String time = list.get(0);
			if (time.indexOf(".") != -1) {
				double num = Double.valueOf(time);
				time = String.valueOf(Math.round(num));
			}
			return Integer.valueOf(time);
		}
		
		return 0;
	}
	
	/**
	 * 
	 * @Description 根据mcc取国家编号
	 * @param mcc
	 * @return HashMap<String,String>  
	 * @author yifang.huang
	 * @date 2016年4月1日 下午3:32:36
	 */
	public HashMap<String, String> findCountryCodeByMcc(String mcc) {
		
		List<HashMap<String, String>> list = findBySql("select country_code countryCode, country_name_cn countryName from mcc_def where mcc='" + mcc + "'", null, Map.class);
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		
		return null;
		
	}

}
