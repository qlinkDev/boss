package main.java.com.qlink.modules.mifi.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.modules.mifi.entity.SimCardType;

/**
 * 
 * @author wangyong
 * @date 2016年3月30日
 */
@Repository
public class SimCardTypeDao extends BaseDao<SimCardType> {

	public static Logger logger = LoggerFactory.getLogger(SimCardTypeDao.class);
	
	/**
	 * 查询卡类型信息
	 * @param sn
	 * @return
	 */
	public SimCardType finByCardType(String cardType){
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("cardType", cardType));
		List<SimCardType> lists = find(dc);
		return lists.size() > 0 ? lists.get(0) : null;
	}
	
}
