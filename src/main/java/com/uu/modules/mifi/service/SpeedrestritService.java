package com.uu.modules.mifi.service;


import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.mifi.dao.SpeedrestritDao;
import com.uu.modules.mifi.entity.Speedrestrit;
/**
 * 限速管理  
 * @Description 
 * @author wangsai
 * @date 2016年11月2日 上午9:44:41
 */
@Service
public class SpeedrestritService extends BaseService {
	@Autowired
	private   SpeedrestritDao  sdDao;
	
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
	public  Speedrestrit get(String id){
		Speedrestrit entity = (Speedrestrit)getEntityById(id, sdDao, new Speedrestrit(), logger);
		return entity;
	}
	//查询
	public  Page<Speedrestrit> find(Page<Speedrestrit>page,Speedrestrit speedrestrit){
		DetachedCriteria dc=sdDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(speedrestrit.getSpeedrestritmcc())){
			dc.add(Restrictions.like("speedrestritmcc", speedrestrit.getSpeedrestritmcc(), MatchMode.ANYWHERE));
		}
		dc.addOrder(Order.asc("speedrestritmcc"));
		return sdDao.find(page,dc);
	}
	//保存
	//@Transactional(readOnly = false)
	public void save(Speedrestrit speedrestrit) {
		if (StringUtils.isBlank(speedrestrit.getId())) {
			speedrestrit.setId(sdDao.getMaxId() + "");
			speedrestrit.setStampCreated(new Date());
		} else {
			speedrestrit.setId(speedrestrit.getId() + "");
			speedrestrit.setStampUpdate(new Date());
		}
		sdDao.clear();
		sdDao.save(speedrestrit);
	}
	public boolean isRankExists(String mcc) {
		return sdDao.isRankExists(mcc);
	}
	public boolean isRankExistss(String mcc ,String id) {
		return sdDao.isRankExistss(mcc,id);
	}
}
