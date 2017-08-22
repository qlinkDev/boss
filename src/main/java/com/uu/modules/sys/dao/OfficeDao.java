/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.uu.modules.sys.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Parameter;
import com.uu.modules.sys.entity.Office;

/**
 * 机构DAO接口
 * @author admin
 * @version 2013-8-23
 */
@Repository
public class OfficeDao extends BaseDao<Office> {
	
	public List<Office> findByParentIdsLike(String parentIds){
		return find("from Office where parentIds like :p1", new Parameter(parentIds));
	}
	
//	@Query("from Office where (id=?1 or parent.id=?1 or parentIds like ?2) and delFlag='" + Office.DEL_FLAG_NORMAL + "' order by code")
//	public List<Office> findAllChild(Long parentId, String likeParentIds);
	
}
