package com.uu.modules.user.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.modules.user.entity.UserMifiLink;

/**
 * 
 * @author jiangbo
 * @date 2016年3月22日
 */
@Repository
public class UserMifiLinkDao extends BaseDao<UserMifiLink> {

	public static Logger logger = LoggerFactory.getLogger(UserMifiLinkDao.class);	
	
	/**
	 * 根据userId和mifiId查询UserMifiLink
	 * @param userId
	 * @return
	 */
	public UserMifiLink queryUserMifiLinkById(String userId, String mifiId) {
		
		DetachedCriteria detachedCriteria = createDetachedCriteria();
		if (StringUtils.isNotBlank(userId))
			detachedCriteria.add(Restrictions.eq("userId", userId));
		if (StringUtils.isNotBlank(mifiId))
			detachedCriteria.add(Restrictions.eq("mifiId", mifiId));
		List<UserMifiLink> userMifiLinks = find(detachedCriteria);
		
		return userMifiLinks.size() > 0 ? userMifiLinks.get(0) : null;
	}
	
	/**
	 * 根据userId查询UserMifiLink
	 * @param userId
	 * @return
	 */
	public List<UserMifiLink> queryUserMifiLinkByUserId(String userId) {
		DetachedCriteria detachedCriteria = createDetachedCriteria();
		detachedCriteria.add(Restrictions.eq("userId", userId));
		List<UserMifiLink> userMifiLinks = find(detachedCriteria);
		return userMifiLinks.size() >0 ? userMifiLinks : null;
	}
	

	
}
