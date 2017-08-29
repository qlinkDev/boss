package main.java.com.qlink.modules.mifi.service;

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
import com.uu.modules.mifi.dao.SimBankIpDao;
import com.uu.modules.mifi.entity.SimBankIp;
/**
 * SIMBankIp管理
 * @Description 
 * @author wangsai
 * @date 2016年11月3日 上午11:27:35
 */
@Service
public class SimBankIpService extends BaseService {
   @Autowired
   private		SimBankIpDao  sbiDao;
   
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
	public  SimBankIp get(String id){
		SimBankIp entity = (SimBankIp)getEntityById(id, sbiDao, new SimBankIp(), logger);
		return entity;
	}
	//查询
	public  Page<SimBankIp> find(Page<SimBankIp>page,SimBankIp simBankIp){
		DetachedCriteria dc=sbiDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(simBankIp.getSimbankid())){
			dc.add(Restrictions.like("simbankid", simBankIp.getSimbankid(), MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotEmpty(simBankIp.getOnlineip())){
			dc.add(Restrictions.like("onlineip", simBankIp.getOnlineip(), MatchMode.ANYWHERE));
		}
		dc.addOrder(Order.desc("onlineip"));
		dc.addOrder(Order.desc("simbankid"));
		return sbiDao.find(page,dc);
	}
	//保存
	//@Transactional(readOnly = false)
	public void save(SimBankIp SimBankIp) {
		if (StringUtils.isBlank(SimBankIp.getId())) {
			SimBankIp.setId(sbiDao.getMaxId() + "");
			SimBankIp.setStampCreated(new Date());
		}else {
			SimBankIp.setId(SimBankIp.getId() + "");
		} 
		sbiDao.clear();
		sbiDao.save(SimBankIp);
	}
	public boolean isRankExists(String simbankid) {
		return sbiDao.isRankExists(simbankid);
	}
	public boolean isRankExistss(String simbankid , String id) {
		return sbiDao.isRankExistss(simbankid,id);
	}
}
