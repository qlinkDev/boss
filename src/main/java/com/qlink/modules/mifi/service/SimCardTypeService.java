package main.java.com.qlink.modules.mifi.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.common.utils.IdGen;
import com.uu.modules.mifi.dao.CardManageDao;
import com.uu.modules.mifi.dao.MifiOrderDao;
import com.uu.modules.mifi.dao.SimCardTypeDao;
import com.uu.modules.mifi.entity.SimCardType;
import com.uu.modules.sys.utils.UserUtils;

/**
 * 
 * @author wangyong
 * @date 2016年3月30日
 */
@Service
public class SimCardTypeService extends BaseService {

	public static Logger logger = LoggerFactory.getLogger(SimCardTypeService.class);

	@Autowired
	private SimCardTypeDao simCardTypeDao;

	@Autowired
	private MifiOrderDao mifiOrderDao;
	
	@Autowired
	private CardManageDao cardManageDao;

	public SimCardType get(String id) {
		return simCardTypeDao.get(id);
	}

	public Page<SimCardType> find(Page<SimCardType> page, SimCardType simCardType) {
		DetachedCriteria dc = simCardTypeDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(simCardType.getCardType())) {
			dc.add(Restrictions.like("cardType", simCardType.getCardType(), MatchMode.ANYWHERE));
		}
		if (StringUtils.isNotEmpty(simCardType.getCardTypeName())) {
			dc.add(Restrictions.like("cardTypeName", "%" + simCardType.getCardTypeName() + "%"));
		}
		String sourceType = simCardType.getSourceType();
		if (StringUtils.isNotBlank(sourceType)) {
			dc.add(Restrictions.eq("sourceType", simCardType.getSourceType()));
		}
		Page<SimCardType> _page = simCardTypeDao.find(page, dc);
		List<SimCardType> list = _page.getList();
		for (int i = 0; i < list.size(); i++) {
			SimCardType _simCardType = list.get(i);
			String allowedMccCn = "未配置";
			if (StringUtils.isNotBlank(_simCardType.getAreaType())) {
				if (_simCardType.getAreaType().equals(SimCardType.areaType_0)) {
					allowedMccCn = "全球";
				} else if (_simCardType.getAreaType().equals(SimCardType.areaType_1)
						|| _simCardType.getAreaType().equals(SimCardType.areaType_2)) {
					allowedMccCn = (String) mifiOrderDao.getMccs(_simCardType.getMcc()).get("allowed_mcc_cn");
				}
			}
			_simCardType.setAllowedMccCn(allowedMccCn);
		}
		_page.setList(list);
		return _page;
	}
	
	/**
	 * 根据当前日期查找需要清空流量的卡类型
	 * @Description 
	 * @author yuxiaoyu
	 * @date 2016年5月4日 上午9:26:31
	 */
	public List<SimCardType> findCardTypeToClear() {
		Calendar c = Calendar.getInstance();
		DetachedCriteria dc = simCardTypeDao.createDetachedCriteria();
		//dc.add(Restrictions.eq("validDays", "-1"));
		dc.add(Restrictions.eq("clearType", 1));
		c.add(Calendar.DAY_OF_MONTH, 1);// 日期+1判断是否月末
		int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		c.add(Calendar.DAY_OF_MONTH, -1);// 还原日历
		if (1 == dayOfMonth) {// 月末，clearDay为0即满足条件
			dc.add(Restrictions.or(Restrictions.eq("clearDay", 0), Restrictions.eq("clearDay", c.get(Calendar.DAY_OF_MONTH))));
		} else {
			dc.add(Restrictions.eq("clearDay", c.get(Calendar.DAY_OF_MONTH)));
		}
		dc.add(Restrictions.eq("clearHour", c.get(Calendar.HOUR_OF_DAY)));
		List<SimCardType> list = simCardTypeDao.find(dc);
		return list;
	}

	//@Transactional(readOnly = false)
	public void save(SimCardType simCardType, String oldSourceType, String oldAllowedSource) {
		if (StringUtils.isBlank(simCardType.getId())) {
			simCardType.setId(IdGen.uuid());
			simCardType.setCreateTime(new Date());
			simCardType.setCreateUser(UserUtils.getUser().getLoginName());
		} else {
			simCardType.setUpdateTime(new Date());
			simCardType.setUpdateUser(UserUtils.getUser().getLoginName());
			// 如果所属渠道发生变化，修改类型对应card_basic_info和simnode表的source_type字段
			if (!simCardType.getSourceType().equals(oldSourceType))
				cardManageDao.updateSourceTypeByCardType(simCardType.getCardType(), simCardType.getSourceType());
			// 如果允许该类型卡使用的渠道发生变化，修改simnode表的allowed_source字段
			if (!simCardType.getAllowedSource().equals(oldAllowedSource))
				cardManageDao.updateAllowedSourceByCardType(simCardType.getCardType(), simCardType.getAllowedSource());
		}
		simCardTypeDao.save(simCardType);
	}

	public SimCardType getCardInfoByCardType(String cardType) {
		return simCardTypeDao.finByCardType(cardType);
	}
	
	/**
	 * 
	 * @Description 取有效期不为-1、需要定时流量清零的卡类型
	 * @return List<SimCardType>  
	 * @author yifang.huang
	 * @date 2016年5月18日 下午4:46:03
	 */
	public List<SimCardType> findList() {
		// 当前小时数
		Calendar c = Calendar.getInstance();
		int hours = c.get(Calendar.HOUR_OF_DAY);
		
		DetachedCriteria dc = simCardTypeDao.createDetachedCriteria();
		//dc.add(Restrictions.ne("validDays", "-1"));
		dc.add(Restrictions.eq("clearType", 0));
		dc.add(Restrictions.isNotNull("clearDay"));
		dc.add(Restrictions.eq("clearHour", hours));
		
		return simCardTypeDao.find(dc);
	}
	/**
	 * 查询卡类型信息
	 * @param sn
	 * @return
	 */
	public SimCardType finByCardType(String cardType){
		return simCardTypeDao.finByCardType(cardType);
	}
	
}
