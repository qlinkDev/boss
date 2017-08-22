/** 
 * @Package com.uu.modules.mifi.service 
 * @Description 
 * @author yifang.huang
 * @date 2016年5月24日 下午4:30:11 
 * @version V1.0 
 */
package com.uu.modules.mifi.service;


import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.common.utils.StringUtils;
import com.uu.modules.mifi.condition.MifiVersionCondition;
import com.uu.modules.mifi.dao.MifiVersionDao;
import com.uu.modules.mifi.entity.MifiVersion;

/**
 * 设备版本号 业务处理类
 * 
 * @author shuxin
 * @date 2016年8月1日
 */
@Service
public class MifiVersionService extends BaseService {

	@Autowired
	private MifiVersionDao  mifiVersionDao;
	
	
	@SuppressWarnings({ "rawtypes" })
	public static Object getEntityById(String id, BaseDao dao, Object duplicateObj, Logger logger) {
		if (StringUtils.isEmpty(id)) {
			return duplicateObj;
		}
		Object sourceObj = dao.get(id);
		if (null == sourceObj) {
			return duplicateObj;
		}
		try {
			BeanUtils.copyProperties(duplicateObj, sourceObj);
		} catch (Exception e) {
			dao.clear();
			return sourceObj;
		}
		return duplicateObj;
	}
	public  MifiVersion get(String id){
		MifiVersion entity = (MifiVersion)getEntityById(id, mifiVersionDao, new MifiVersion(), logger);
		return entity;
	}
	public Page<MifiVersion> find(Page<MifiVersion> page, MifiVersion mifiversion) {
		DetachedCriteria dc = mifiVersionDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(mifiversion.getIMEI_6200())) {
			dc.add(Restrictions.eq("IMEI_6200", mifiversion.getIMEI_6200()));
		}
		return mifiVersionDao.find(page, dc);
	}
	//保存
	//@Transactional(readOnly = false)
	public void save(MifiVersion mifiversion) {
		mifiVersionDao.clear();
		mifiVersionDao.save(mifiversion);
	}
	//@Transactional(readOnly = false)
	public  int  updatespeedlimitflag(String num){
		return mifiVersionDao.updataspeedlimitflag(num);
	}
	//@Transactional(readOnly = false)
	public  int  updateupdateflag(String num){
		return mifiVersionDao.updataupdateflag(num);
	}

	/**
	 * 根据查询参数取列表数据
	 * @Description 
	 * @param condition
	 * @return List<MifiVersion>  
	 * @author yifang.huang
	 * @date 2016年10月26日 上午11:26:52
	 */
	public List<MifiVersion> findListByCondition(MifiVersionCondition condition) {
		
		return mifiVersionDao.findListByCondition(condition);
		
	}
	
	/**
	 * 根据查询参数取分页数据
	 * @Description 
	 * @param page
	 * @param condition
	 * @return Page<MifiVersion>  
	 * @author yifang.huang
	 * @date 2016年10月26日 上午11:28:01
	 */
	public Page<MifiVersion> findPageByCondition(Page<MifiVersion> page, MifiVersionCondition condition) {
		
		return mifiVersionDao.findPageByCondition(page, condition);

	}
	
	/**
	 * 
	 * @Description 取设备当前版本号
	 * @param imei 设备编号
	 * @return double  
	 * @author yifang.huang
	 * @date 2016年11月1日 上午10:06:22
	 */
	public double getMifiVersion(String imei) {
		
		return mifiVersionDao.getMifiVersion(imei);
		
	}
}
