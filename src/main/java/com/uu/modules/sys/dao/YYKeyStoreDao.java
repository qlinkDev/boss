package com.uu.modules.sys.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Parameter;
import com.uu.modules.sys.entity.Dict;
import com.uu.modules.sys.entity.YYKeyStore;


/**
 * @author liaowu
 *
 */
@Repository
public class YYKeyStoreDao extends BaseDao<YYKeyStore> {

	public List<YYKeyStore> findAllList(){
		return find("from YYKeyStore where delFlag=:p1 order by createDate desc", new Parameter(Dict.DEL_FLAG_NORMAL));
	}

}
