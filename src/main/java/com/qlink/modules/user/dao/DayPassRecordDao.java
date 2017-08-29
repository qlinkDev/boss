/** 
 * @Package com.uu.modules.user.dao 
 * @Description 
 * @author yifang.huang
 * @date 2016年12月6日 上午11:46:07 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.user.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Page;
import com.uu.modules.user.condition.DayPassRecordCondition;
import com.uu.modules.user.entity.DayPassRecord;

/** 
 * @Description 用户开通天数记录(增加减少) DAO层接口类
 * @author yifang.huang
 * @date 2016年12月6日 上午11:46:07 
 */
@Repository
public class DayPassRecordDao extends BaseDao<DayPassRecord> {

	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<DayPassRecord>  
	 * @author yifang.huang
	 * @date 2016年12月6日 上午11:46:59
	 */
	public List<DayPassRecord> findList(DayPassRecordCondition condition) {
		
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
	 * @return Page<DayPassRecord>  
	 * @author yifang.huang
	 * @date 2016年12月6日 上午11:47:13
	 */
	public Page<DayPassRecord> findPage(Page<DayPassRecord> page, DayPassRecordCondition condition) {
		
		DetachedCriteria dc = createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return find(page, dc);
	}

}
