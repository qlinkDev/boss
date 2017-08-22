/** 
 * @Package com.uu.modules.om.dao 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月18日 上午11:13:16 
 * @version V1.0 
 */ 
package com.uu.modules.om.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Page;
import com.uu.common.persistence.Parameter;
import com.uu.modules.om.entity.Region;

/** 
 * @Description 区域 DAO
 * @author yifang.huang
 * @date 2016年3月18日 上午11:13:16 
 */
@Repository
public class RegionDao extends BaseDao<Region> {

	/**
	 * 
	 * @Description 取删除标记为正常的区域列表
	 * @return List<Region>  
	 * @author yifang.huang
	 * @date 2016年3月18日 上午11:22:15
	 */
	public List<Region> findAllList(){
		return find("from Region where delFlag=:p1 order by createDate desc", new Parameter(Region.DEL_FLAG_NORMAL));
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
		
		StringBuffer sql = new StringBuffer("select group_concat(country_codes) from om_region where 1=1");
		if (StringUtils.isNotBlank(neId))
			sql.append(" and id!='" + neId + "'");
		sql.append(" and del_flag='" + Region.DEL_FLAG_NORMAL + "'");
		
		List<String> codesList = findBySql(sql.toString());
		if (codesList!=null && codesList.size()>0) {
			return codesList.get(0);
		} else 
			return null;
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
		
		List<Region> list = findAllList();
		
		if (list!=null && list.size()>0) {
			for (Region bean : list) {
				Set<String> mccSet = bean.getMccesSet();
				if (mccSet.contains(mcc))
					return bean;
			}
		}
		
		return null;
	}

	/**
	 * 
	 * @Description 根据countryCode取记录
	 * @param countryCode
	 * @return Region  
	 * @author yifang.huang
	 * @date 2016年5月4日 上午10:13:00
	 */
	public Region findByCountryCode(String countryCode) {
		
		List<Region> list = findAllList();
		
		if (list!=null && list.size()>0) {
			for (Region bean : list) {
				Set<String> countryCodeSet = bean.getCountryCodeSet();
				if (countryCodeSet.contains(countryCode))
					return bean;
			}
		}
		
		return null;
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
		
		StringBuffer sb = new StringBuffer("select countryCode, countryName, countryNameEn, mcces from (");
		
		sb.append("select country_code countryCode, country_name_cn countryName, country_name_en countryNameEn, group_concat(mcc) mcces from mcc_def where 1=1");

		String countryCode = ObjectUtils.toString(paramMap.get("countryCode"));
		if (StringUtils.isNotBlank(countryCode)) {
			sb.append(" and country_code like '%" + countryCode + "%'");
		}
		String countryName = ObjectUtils.toString(paramMap.get("countryName"));
		if (StringUtils.isNotBlank(countryName)) {
			sb.append(" and country_name_cn like '%" + countryName + "%'");
		}
		String countryNameEn = ObjectUtils.toString(paramMap.get("countryNameEn"));
		if (StringUtils.isNotBlank(countryNameEn)) {
			sb.append(" and country_name_en like '%" + countryNameEn + "%'");
		}
		sb.append(" group by country_name_cn) as t");
		
		String mcc = ObjectUtils.toString(paramMap.get("mcc")).trim();
		if (StringUtils.isNotBlank(mcc)) {
			sb.append(" where t.mcces like '%" + mcc + "%'");
		}

		return findBySql(page, sb.toString(), null, Map.class);
	}
	
	/**
	 * 查询所有国家MCC信息
	 * @athor shuxin
	 * @date 2016年6月27日下午3:10:21
	 * @param paramMap
	 * @return
	 * List<Map<String,String>> 
	 */
	public List<Map<String, Object>> findAllCountrys() {
		StringBuffer sb = new StringBuffer("select countryCode, countryName, countryNameEn, mcces from (");
		sb.append("select country_code countryCode, country_name_cn countryName, country_name_en countryNameEn, group_concat(mcc) mcces from mcc_def where 1=1");
		sb.append(" group by country_name_cn) as t");
		return findBySql(sb.toString(), null, Map.class);
	}
	
	/**
	 * 根据国家编码查找mcc
	 * @athor shuxin
	 * @date 2016年7月8日上午9:47:41
	 * @param countryCode
	 * @return
	 * List<String> 
	 */
	public List<String> findMccsByCountryCode(String countryCode){
		if(!StringUtils.isNotBlank(countryCode)){
			return new ArrayList<String>();
		}
		String sqlString = "select group_concat(mcc) mcces from mcc_def WHERE country_code in ("+countryCode+") group by country_name_cn order by convert(country_name_cn using gbk) asc";
		return findBySql(sqlString);
	}
}
