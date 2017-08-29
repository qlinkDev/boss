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
import com.uu.modules.mifi.dao.MifiIpDao;
import com.uu.modules.mifi.entity.MifiIp;
/**
 * 多服务器配置管理
 * @Description 
 * @author wangsai
 * @date 2016年11月3日 下午2:56:36
 */
@Service
public class MifiIpService extends BaseService {
	@Autowired
	private   MifiIpDao  miDao;
	
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
	public  MifiIp get(String id){
		MifiIp entity = (MifiIp)getEntityById(id, miDao, new MifiIp(), logger);
		return entity;
	}
	//查询
	public  Page<MifiIp> find(Page<MifiIp>page,MifiIp mifiIp){
		DetachedCriteria dc=miDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(mifiIp.getMcc())){
			dc.add(Restrictions.like("mcc", mifiIp.getMcc(), MatchMode.ANYWHERE));
		}
		dc.addOrder(Order.desc("stampCreated"));
		return miDao.find(page,dc);
	}
	//保存
	//@Transactional(readOnly = false)
	public void save(MifiIp mifiIp) {
		if (StringUtils.isBlank(mifiIp.getId())) {
			mifiIp.setId(miDao.getMaxId() + "");
			mifiIp.setStampCreated(new Date());
		}else {
			mifiIp.setId(mifiIp.getId() + "");
		} 
		miDao.clear();
		miDao.save(mifiIp);
	}
	public boolean isRankExists(String mcc) {
		return miDao.isRankExists(mcc);
	}
	public boolean isRankExistss(String mcc , String id) {
		return miDao.isRankExistss(mcc,id);
	}
}
