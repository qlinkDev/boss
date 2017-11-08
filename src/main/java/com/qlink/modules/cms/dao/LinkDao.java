/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.qlink.modules.cms.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.qlink.common.persistence.BaseDao;
import com.qlink.common.persistence.Parameter;
import com.qlink.modules.cms.entity.Link;

/**
 * 链接DAO接口
 * @author admin
 * @version 2013-8-23
 */
@Repository
public class LinkDao extends BaseDao<Link> {
	
	public List<Link> findByIdIn(Long[] ids){
		return find("front Like where id in (:p1)", new Parameter(new Object[]{ids}));
	}
	
	public int updateExpiredWeight(){
		return update("update Link set weight=0 where weight > 0 and weightDate < current_timestamp()");
	}
}
