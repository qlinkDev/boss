/** 
 * @Package com.uu.modules.mifi.dao 
 * @Description 
 * @author yifang.huang
 * @date 2016年12月7日 上午11:01:11 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Page;
import com.uu.modules.mifi.condition.MifiBootCondition;
import com.uu.modules.mifi.entity.MifiBoot;

/** 
 * @Description MIFI开机(一天一条MIFI状态为4记录) DAO接口类
 * @author yifang.huang
 * @date 2016年12月7日 上午11:01:11 
 */
@Repository
public class MifiBootDao extends BaseDao<MifiBoot> {

	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<MifiBoot>  
	 * @author yifang.huang
	 * @date 2016年12月7日 上午11:34:59
	 */
	public List<MifiBoot> findList(MifiBootCondition condition) {
		
		DetachedCriteria dc = createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return find(dc);
	}
	
	/**
	 * 
	 * @Description 根据查询参数取分页数据
	 * @param page
	 * @param condition
	 * @return Page<MifiBoot>  
	 * @author yifang.huang
	 * @date 2016年12月7日 上午11:35:12
	 */
	public Page<MifiBoot> findPage(Page<MifiBoot> page, MifiBootCondition condition) {
		
		DetachedCriteria dc = createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return find(page, dc);
	}
	
	/**
	 * 
	 * @Description 根据mcc取国家编号
	 * @param mcc
	 * @return HashMap<String,String>  
	 * @author yifang.huang
	 * @date 2016年4月15日 上午10:50:41
	 */
	public Map<String, String> findCountryByMcc(String mcc) {
		
		List<HashMap<String, String>> list = findBySql("select country_code countryCode, country_name_cn countryName, country_name_en countryNameEn from mcc_def where mcc='" + mcc + "'", null, Map.class);
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		
		return null;
		
	}

}
