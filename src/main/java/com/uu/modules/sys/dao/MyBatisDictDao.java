/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.uu.modules.sys.dao;

import java.util.List;

import com.uu.common.persistence.annotation.MyBatisDao;
import com.uu.modules.sys.entity.Dict;

/**
 * MyBatis字典DAO接口
 * @author admin
 * @version 2013-8-23
 */
@MyBatisDao
public interface MyBatisDictDao {
	
    Dict get(String id);
    
    List<Dict> find(Dict dict);
    
}
