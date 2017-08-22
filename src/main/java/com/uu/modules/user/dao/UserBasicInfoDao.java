package com.uu.modules.user.dao;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Parameter;
import com.uu.modules.user.entity.UserBasicInfo;

/**
 * 
 * @author jiangbo
 * @date 2016年3月22日
 */
@Repository
public class UserBasicInfoDao extends BaseDao<UserBasicInfo> {

	public static Logger logger = LoggerFactory.getLogger(UserBasicInfoDao.class);
	
	
	@Transactional(readOnly = false)
	public void saveOrUpdate(UserBasicInfo usserBasicInfo) {
		this.getSession().saveOrUpdate(usserBasicInfo);
	}
	public long getMifiListCountByDsn(String mifiId) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("select count(*) from mifilist where IMEI_6200 = " + mifiId);
		return Long.parseLong(findBySql(buffer.toString()).get(0).toString());
	}
	
	@Transactional(readOnly = false)
	public void mifiTypeUpdate(String userId, String mifiId) {
		StringBuffer sb = new StringBuffer();
		sb.append("update mifilist a ");
		sb.append("set a.owner_type='2',a.UEALLOWED='1',a.UEALLOWEDMCC='ALL',a.source_type='"+userId+"' ");
		sb.append("where a.IMEI_6200="+mifiId);
		SQLQuery  sqlQuery = getSession().createSQLQuery(sb.toString());
		sqlQuery.executeUpdate();
		
		StringBuffer sb2 = new StringBuffer();
		sb2.append("update mifi_basic_info a ");
		sb2.append("set a.owner_type ='2',a.source_type='"+userId+"' ");
		sb2.append("where a.sn="+mifiId);
		SQLQuery  sqlQuery2 = getSession().createSQLQuery(sb2.toString());
		sqlQuery2.executeUpdate();
	}
	
	@Transactional(readOnly = false)
	public void mifiUpdate(String mifiId, String ueallowed) {
		StringBuffer sb = new StringBuffer();
		sb.append("update mifilist a ");
		sb.append("set a.owner_type='2',a.UEALLOWED='" + ueallowed + "',a.UEALLOWEDMCC='homeForbidden' ");
		sb.append("where a.IMEI_6200="+mifiId);
		SQLQuery  sqlQuery = getSession().createSQLQuery(sb.toString());
		sqlQuery.executeUpdate();
		
		StringBuffer sb2 = new StringBuffer();
		sb2.append("update mifi_basic_info a ");
		sb2.append("set a.owner_type ='2' ");
		sb2.append("where a.sn="+mifiId);
		SQLQuery  sqlQuery2 = getSession().createSQLQuery(sb2.toString());
		sqlQuery2.executeUpdate();
	}

	public UserBasicInfo findByUserId(String userId){
		List<UserBasicInfo> list = find("from UserBasicInfo where userId=:p1", new Parameter(userId));
		if (list!=null && list.size()>0)
			return list.get(0);
		return null;
	}

	public UserBasicInfo findById(String id){
		List<UserBasicInfo> list = find("from UserBasicInfo where id=:p1", new Parameter(id));
		if (list!=null && list.size()>0)
			return list.get(0);
		return null;
	}
	
	/**
	 * 
	 * @Description 根据用户登录名和渠道编号查询用户信息
	 * @param userId
	 * @param sourceType
	 * @return UserBasicInfo  
	 * @author yifang.huang
	 * @date 2016年9月26日 下午5:33:42
	 */
	public UserBasicInfo findUserByUserIdAndSourceType(String userId, String sourceType) {
		DetachedCriteria detachedCriteria = createDetachedCriteria();
		detachedCriteria.add(Restrictions.eq("userId", userId));
		detachedCriteria.add(Restrictions.eq("sourceType", sourceType));
		List<UserBasicInfo> userBasicInfos = find(detachedCriteria);
		return userBasicInfos.size() > 0 ? userBasicInfos.get(0) : null;
	}
	
	/**
	 * 根据userId查询UserBasicInfo
	 * @param userId
	 * @return
	 */
	public UserBasicInfo queryUserBasicInfoByUserId(String userId) {
		DetachedCriteria detachedCriteria = createDetachedCriteria();
		detachedCriteria.add(Restrictions.eq("userId", userId));
		List<UserBasicInfo> userBasicInfos = find(detachedCriteria);
		return userBasicInfos.size() > 0 ? userBasicInfos.get(0) : null;
	}

}
