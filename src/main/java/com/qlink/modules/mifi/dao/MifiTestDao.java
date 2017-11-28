/** 
 * @Package com.uu.modules.mifi.dao 
 * @Description 
 * @author yifang.huang
 * @date 2016年5月24日 下午4:28:41 
 * @version V1.0 
 */ 
package com.qlink.modules.mifi.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.stereotype.Repository;

import com.qlink.common.persistence.BaseDao;
import com.qlink.modules.mifi.condition.MifiTestCondition;
import com.qlink.modules.mifi.entity.MifiTest;

/** 
 * @Description 测试设备与卡绑定 DAO
 * @author yifang.huang
 * @date 2016年5月24日 下午4:28:41 
 */
@Repository
public class MifiTestDao extends BaseDao<MifiTest> {

	
	/**
	 * 
	 * @Description 根据simbankid、simid、status取simnode列表
	 * @param simBankId
	 * @param simId
	 * @param status
	 * @return List<Map<String, Object>>  
	 * @author yifang.huang
	 * @date 2016年5月24日 下午4:56:30
	 */
	public List<Map<String, Object>> findSimNodeList(String simBankId, String simId, String status) {
		
		StringBuffer sb = new StringBuffer("select simbankid, simid, hex(imsi) imsi, hex(iccid) iccid, usimstatus, "
				+ "if(DATACAP < DATAUSED, 0, format((DATACAP - DATAUSED)/ 1024 / 1024 / 1024, 0))AS dataSur, DATAUSED dataUsed from simnode where 1=1");
		
		if (StringUtils.isNotBlank(simBankId)) {
			sb.append(" and simbankid=" + simBankId);
		}
		if (StringUtils.isNotBlank(simId)) {
			sb.append(" and simid=" + simId);
		}
		if (StringUtils.isNotBlank(status)) {
			sb.append(" and USIMSTATUS=" + status);
		} else {
			sb.append(" and USIMSTATUS!=0");
		}
		
		return findBySql(sb.toString(), null, Map.class);
	}
	
	public Integer getMaxId() {
		String sql = "select if(isnull(max(id)),0,max(id)) maxId from mifitest";
		List<Map<String, String>> list = findBySql(sql, null, Map.class);
		if (list!=null && list.size()>0) {
			Map<String, String> map = list.get(0);
			String maxId = ObjectUtils.toString(map.get("maxId"));
			return Integer.valueOf(maxId);
		}
		return 0;
	}


	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<MifiTest>  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午3:10:14
	 */
	public List<MifiTest> findListByCondition(MifiTestCondition condition) {
		
		DetachedCriteria dc = createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return find(dc);
	}
}
