/** 
 * @Package com.uu.modules.mifi.service 
 * @Description 
 * @author yuxiaoyu
 * @date 2017年3月3日 上午10:37:29 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.service;

import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.mifi.condition.MifiUsageRecordSegmentLogCondition;
import com.uu.modules.mifi.dao.MifiUsageRecordSegmentLogDao;
import com.uu.modules.mifi.entity.MifiUsageRecordSegmentLog;

/** 
 * @Description 
 * @author yuxiaoyu
 * @date 2017年3月3日 上午10:37:29 
 */
@Service
public class MifiUsageRecordSegmentLogService extends BaseService{
	@Autowired
	private MifiUsageRecordSegmentLogDao errorLogDao;

	/**
	 * 保存
	 * @Description 
	 * @param errorLog 
	 * @return void  
	 * @author yuxiaoyu
	 * @date 2017年3月3日 上午10:38:59
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void save(MifiUsageRecordSegmentLog errorLog) {
		errorLogDao.save(errorLog);
	}
	
	/**
	 * 查找未验证的记录信息按月统计列表
	 * 
	 * @athor shuxin
	 * @date 2016年9月9日下午4:56:17
	 * @return List<Map<String,String>>
	 */
	public List<Map<String, String>> findRecordLogsForMonth(MifiUsageRecordSegmentLogCondition condition) {
		return errorLogDao.findRecordLogForMonth(condition);
	}
	
	/**
	 * 分页查询
	 * 
	 * @athor shuxin
	 * @date 2016年8月4日上午11:55:39
	 * @param page
	 * @param condition
	 * @return Page<MifiUsageRecordSegmentLog>
	 */
	public Page<MifiUsageRecordSegmentLog> findPage(Page<MifiUsageRecordSegmentLog> page,
			MifiUsageRecordSegmentLogCondition condition) {
		DetachedCriteria dc = errorLogDao.createDetachedCriteria();
		condition.build(dc);
		return errorLogDao.find(page, dc);
	}

	/**
	 * 获取对象
	 * 
	 * @athor shuxin
	 * @date 2016年8月5日上午9:22:30
	 * @param id
	 * @return MifiUsageRecordSegmentLog
	 */
	public MifiUsageRecordSegmentLog getById(String id) {
		return errorLogDao.get(id);
	}

	/**
	 * 根据时间获取对象
	 * 
	 * @athor shuxin
	 * @date 2016年8月5日上午9:26:39
	 * @param stampCreated
	 * @return MifiUsageRecordSegmentLog
	 */
	public MifiUsageRecordSegmentLog getByStampCreated(String stampCreated) {
		String qlString = " from MifiUsageRecordSegmentLog t where t.stampCreated = '" + stampCreated + "'";
		List<MifiUsageRecordSegmentLog> logs = errorLogDao.find(qlString);
		if (logs.isEmpty()) {
			return new MifiUsageRecordSegmentLog();
		}
		return logs.get(0);
	}

	/**
	 * 通过月份查询当月的使用记录列表
	 * 
	 * @athor shuxin
	 * @date 2016年9月9日下午5:00:19
	 * @param month
	 * @return List<Map<String,String>>
	 */
	public List<Map<String, Object>> findByMonth(String month) {
		return errorLogDao.findRecordLogByMonth(month);
	}
	
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updateOfIsCheckByMonth(String month){
		String sql = "UPDATE mifi_usage_record_segment_log m SET m.is_check = 1 WHERE DATE_FORMAT(m.stamp_created, '%Y-%c') = '"+month+"'";
		errorLogDao.updateBySql(sql, null);
	}
	
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updateOfIsCheckByDay(String day){
		String sql = "UPDATE mifi_usage_record_segment_log m SET m.is_check = 1 WHERE DATE_FORMAT(m.stamp_created, '%Y-%m-%e') = '"+day+"'";
		errorLogDao.updateBySql(sql, null);
	}
}
