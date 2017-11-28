package com.qlink.modules.mifi.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.qlink.common.persistence.BaseDao;
import com.qlink.common.persistence.Parameter;
import com.qlink.common.utils.StringUtils;
import com.qlink.modules.mifi.entity.Speedrestrit;
/**
 * 限速管理  
 * @Description 
 * @author wangsai
 * @date 2016年11月2日 上午9:44:41
 */
@Repository
public class SpeedrestritDao extends BaseDao<Speedrestrit>{
	
	public static Logger logger = LoggerFactory.getLogger(SpeedrestritDao.class);
	
	/**
	 * 取id
	 * @Description 
	 * @return int  
	 * @author yifang.huang
	 * @date 2016年10月26日 下午2:04:41
	 */
	public int getMaxId() {
		
		String sql = "SELECT MAX(ID) FROM speedrestrit";
		
		List<Object> list = findBySql(sql);
		if (list!=null && list.size()>0) {
			return Integer.valueOf(list.get(0).toString()) + 1;
		}
		
		return 1;
	}
	/**
	 * 判断MCC是否重复
	 * @Description 
	 * @param mcc
	 * @return 
	 * @return boolean  
	 * @author wangsai
	 * @date 2016年11月2日 下午1:44:07
	 */
	public boolean isRankExists(String mcc) {
		String sqlStr = "select 1 from dual where exists(select 1 from Speedrestrit where speedrestritmcc=:p1 )";
		Parameter param = new Parameter( mcc);
		List<Object> resultList = findBySql(sqlStr, param);
		if (StringUtils.isEmpty(resultList)) {
			return false;
		}
		return true;
		}
	public boolean isRankExistss(String mcc,String id) {
		String sqlStr = "select 1 from dual where exists(select 1 from Speedrestrit where speedrestritmcc=:p1 and id=:p2 )";
		Parameter param = new Parameter( mcc,id);
		List<Object> resultList = findBySql(sqlStr, param);
		if (StringUtils.isEmpty(resultList)) {
			return false;
		}
		return true;
		}
}
