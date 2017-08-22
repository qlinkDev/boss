package com.uu.modules.mifi.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.modules.mifi.entity.MifiOrderDetail;

/**
 * 
 * @author wangyong
 * @date 2016年2月17日
 */
@Repository
public class MifiOrderDetailDao extends BaseDao<MifiOrderDetail> {

	public static Logger logger = LoggerFactory.getLogger(MifiOrderDetailDao.class);

	public String getDsnByOrderId(String orderId) {
		String dsn = "";
		DetachedCriteria detachedCriteria = createDetachedCriteria();
		detachedCriteria.add(Restrictions.eq("orderId", orderId));
		List<MifiOrderDetail> mifiOrderDetails = find(detachedCriteria);
		for (int i = 0; i < mifiOrderDetails.size(); i++) {
			dsn += (i == 0 ? "" : ",") + mifiOrderDetails.get(i).getDsn();
		}
		return dsn;
	}

	public String getSsidByOrderId(String orderId) {
		String ssid = "";
		DetachedCriteria detachedCriteria = createDetachedCriteria();
		detachedCriteria.add(Restrictions.eq("orderId", orderId));
		List<MifiOrderDetail> mifiOrderDetails = find(detachedCriteria);
		String str = null;
		for (int i = 0; i < mifiOrderDetails.size(); i++) {
			str = mifiOrderDetails.get(i).getSsid();
			if (StringUtils.isNotBlank(str)) {
				ssid += "," + str;
			}
		}
		if (StringUtils.isNotBlank(ssid))
			return ssid.substring(1);
		
		return null;
	}

	public List<MifiOrderDetail> getByOrderId(String orderId) {
		DetachedCriteria detachedCriteria = createDetachedCriteria();
		detachedCriteria.add(Restrictions.eq("orderId", orderId));
		List<MifiOrderDetail> mifiOrderDetails = find(detachedCriteria);
		return mifiOrderDetails;
	}
	
	public int deleteByOrderId(String orderId) {
		DetachedCriteria detachedCriteria = createDetachedCriteria();
		detachedCriteria.add(Restrictions.eq("orderId", orderId));
		List<MifiOrderDetail> mifiOrderDetails = find(detachedCriteria);
		for (MifiOrderDetail bean : mifiOrderDetails)
			getSession().delete(bean);
		return mifiOrderDetails.size();
	}

	public List<MifiOrderDetail> getByDsn(String dsn) {
		DetachedCriteria detachedCriteria = createDetachedCriteria();
		detachedCriteria.add(Restrictions.eq("dsn", dsn));
		List<MifiOrderDetail> mifiOrderDetails = find(detachedCriteria);
		return mifiOrderDetails;
	}

}
