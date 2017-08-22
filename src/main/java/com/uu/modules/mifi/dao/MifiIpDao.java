package com.uu.modules.mifi.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Parameter;
import com.uu.common.utils.StringUtils;
import com.uu.modules.mifi.entity.MifiIp;
/**
 * 多服务器配置管理
 * @Description 
 * @author wangsai
 * @date 2016年11月3日 下午2:56:36
 */
@Repository
public class MifiIpDao  extends BaseDao<MifiIp>{
	
	public static Logger logger = LoggerFactory.getLogger(MifiIpDao.class);

	/**
	 * 取id
	 * @Description 
	 * @return int  
	 * @author yifang.huang
	 * @date 2016年10月26日 下午2:04:41
	 */
	public int getMaxId() {
		
		String sql = "SELECT MAX(ID) FROM mifi_ip";
		
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
		String sqlStr = "select 1 from dual where exists(select 1 from mifi_ip where mcc=:p1 )";
		Parameter param = new Parameter( mcc);
		List<Object> resultList = findBySql(sqlStr, param);
		if (StringUtils.isEmpty(resultList)) {
			return false;
		}
		return true;
		}
	public boolean isRankExistss(String mcc,String id) {
		String sqlStr = "select 1 from dual where exists(select 1 from  mifi_ip where mcc=:p1 and id=:p2 )";
		Parameter param = new Parameter( mcc,id);
		List<Object> resultList = findBySql(sqlStr, param);
		if (StringUtils.isEmpty(resultList)) {
			return false;
		}
		return true;
		}
}
