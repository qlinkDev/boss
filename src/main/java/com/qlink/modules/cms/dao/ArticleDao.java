/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.qlink.modules.cms.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.qlink.common.persistence.BaseDao;
import com.qlink.common.persistence.Parameter;
import com.qlink.modules.cms.entity.Article;

/**
 * 文章DAO接口
 * @author admin
 * @version 2013-8-23
 */
@Repository
public class ArticleDao extends BaseDao<Article> {
	
	public List<Article> findByIdIn(String[] ids){
		return find("from Article where id in (:p1)", new Parameter(new Object[]{ids}));
	}
	
	public int updateHitsAddOne(String id){
		return update("update Article set hits=hits+1 where id = :p1", new Parameter(id));
	}
	
	public int updateExpiredWeight(){
		return update("update Article set weight=0 where weight > 0 and weightDate < current_timestamp()");
	}
	
}
