/** 
 * @Package com.uu.modules.om.service 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月18日 上午11:26:08 
 * @version V1.0 
 */ 
package com.uu.modules.om.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.common.utils.CacheUtils;
import com.uu.modules.om.condition.PriceCondition;
import com.uu.modules.om.condition.RegionCondition;
import com.uu.modules.om.dao.RegionDao;
import com.uu.modules.om.entity.Price;
import com.uu.modules.om.entity.Region;
import com.uu.modules.om.utils.RegionUtils;

/** 
 * @Description 区域 业务处理
 * @author yifang.huang
 * @date 2016年3月18日 上午11:26:08 
 */
@Service
public class RegionService extends BaseService {
	
	@Autowired
	private RegionDao regionDao;
	
	@Autowired
	private PriceService priceService;
	
	public Region get(String id) {
		Region oldBean = regionDao.get(id);
		if (oldBean != null) {
			Region newBean = new Region();
			BeanUtils.copyProperties(oldBean, newBean);
			// 清除指定对象缓存
			regionDao.getSession().evict(oldBean);
			return newBean;
		}
		return null;
	}
	
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void save(Region bean) {
		
		boolean isCreate = StringUtils.isNotBlank(bean.getId()) ? false : true;
		
		regionDao.save(bean);
		CacheUtils.remove(RegionUtils.CACHE_REGION_LIST);
		
		// 保存一条区域默认价格记录（Price）
		if (isCreate)
			priceService.saveByRegion(bean);
	}
	
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void delete(String id) {
		regionDao.deleteById(id);
		CacheUtils.remove(RegionUtils.CACHE_REGION_LIST);
		
		// 删除区域对应价格
		PriceCondition condition = new PriceCondition();
		condition.setEqRegionId(id);
		List<Price> list = priceService.findListByCondition(condition);
		if (list!=null && list.size()>0) {
			for (Price price : list) 
				priceService.delete(price.getId());
		}
	}

	/**
	 * 
	 * @Description 根据mcc取记录
	 * @param mcc
	 * @return Region  
	 * @author yifang.huang
	 * @date 2016年3月18日 上午11:23:21
	 */
	public Region findByMcc(String mcc) {
		
		return regionDao.findByMcc(mcc);
	}

	/**
	 * 
	 * @Description 根据countryCode取记录
	 * @param countryCode
	 * @return Region  
	 * @author yifang.huang
	 * @date 2016年5月4日 上午10:14:20
	 */
	public Region findByCountryCode(String countryCode) {
		
		return regionDao.findByCountryCode(countryCode);
	}

	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<Region>  
	 * @author yifang.huang
	 * @date 2016年3月18日 下午5:05:26
	 */
	public List<Region> findListByCondition(RegionCondition condition) {
		
		DetachedCriteria dc = regionDao.createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return regionDao.find(dc);
	}
	
	/**
	 * 
	 * @Description 根据查询参数取分页数据
	 * @param page
	 * @param condition
	 * @return Page<Region>  
	 * @author yifang.huang
	 * @date 2016年3月18日 下午5:10:21
	 */
	public Page<Region> find(Page<Region> page, RegionCondition condition) {
		
		DetachedCriteria dc = regionDao.createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return regionDao.find(page, dc);
	}
	
	/**
	 * 
	 * @Description 所有被选择到区域的国家编号
	 * @param neId 不包括当前区域
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年3月28日 上午11:54:55
	 */
	public String findAllCountryCodes(String neId) {
		return regionDao.findAllCountryCodes(neId);
	}

	/**
	 * 
	 * @Description 分页查询国家信息
	 * @param page
	 * @param paramMap
	 * @return Page<Map<String,String>>  
	 * @author yifang.huang
	 * @date 2016年5月19日 下午4:21:20
	 */
	public Page<Map<String, String>> findAllCountry(Page<Map<String, String>> page, Map<String, Object> paramMap) {
		return regionDao.findAllCountry(page, paramMap);
	}
	
	/**
	 *  查找要导出的国家MCC数据信息
	 * @athor shuxin
	 * @date 2016年6月27日下午3:06:03
	 * @return List<Map<String,Object>> 
	 * 
	 */
	public List<Map<String, Object>> findForExport(){
		return regionDao.findAllCountrys();
	}
	
	/**
	 * 所有区域列表
	 * @athor shuxin
	 * @date 2016年7月7日上午9:26:23
	 * @return
	 * List<Region> 
	 */
	public List<Region> findAllList() {
		DetachedCriteria dc = regionDao.createDetachedCriteria();
		// 按创建时间降序排序
		dc.addOrder(Order.desc("createDate"));
		return regionDao.find(dc);
	}

	/**
	 * 根据mcc查询国家编码列表
	 * @athor shuxin
	 * @date 2016年7月7日下午2:14:03
	 * @param mcc
	 * @return
	 * List<String> 
	 */
	public List<String> findAllListByMcc(String mcc){
		if(!StringUtils.isNotBlank(mcc)){
			return new ArrayList<String>();
		}
		String sqlString = "SELECT country_code FROM mcc_def WHERE mcc in ("+mcc+") GROUP BY country_name_cn";
		return regionDao.findBySql(sqlString);
	}
	
	/**
	 * 根据code查找mcc
	 * @athor shuxin
	 * @date 2016年7月8日上午9:49:20
	 * @param countryCode
	 * @return
	 * List<String> 
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public List<String> findMccByCountryCode(String countryCode){
		return regionDao.findMccsByCountryCode(countryCode);
	}
}
