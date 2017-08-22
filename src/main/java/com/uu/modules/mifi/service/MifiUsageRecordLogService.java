package com.uu.modules.mifi.service;

import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.mifi.condition.MifiUsageRecordLogCondition;
import com.uu.modules.mifi.dao.MifiUsageRecordLogDao;
import com.uu.modules.mifi.entity.MifiUsageRecordLog;

/**
 * 设备使用调度日志记录
 * 
 * @author shuxin
 * @date 2016年8月3日
 */
@Service
public class MifiUsageRecordLogService extends BaseService {

	@Autowired
	private MifiUsageRecordLogDao errorLogDao;

	/**
	 * 保存数据
	 * 
	 * @athor shuxin
	 * @date 2016年8月3日上午9:36:48
	 * @param mifiUsageRecords
	 *            void
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveMifiUsageRecordErrorLog(MifiUsageRecordLog errorLog) {
		errorLogDao.save(errorLog);
	}

	/**
	 * 保存数据
	 * 
	 * @athor shuxin
	 * @date 2016年8月3日上午9:36:48
	 * @param mifiUsageRecords
	 *            void
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveForApi(MifiUsageRecordLog errorLog) {
		errorLogDao.getSession().merge(errorLog);
	}

	/**
	 * 分页查询
	 * 
	 * @athor shuxin
	 * @date 2016年8月4日上午11:55:39
	 * @param page
	 * @param condition
	 * @return Page<MifiUsageRecordLog>
	 */
	public Page<MifiUsageRecordLog> findMifiUsageRecordLogsPage(Page<MifiUsageRecordLog> page,
			MifiUsageRecordLogCondition condition) {
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
	 * @return MifiUsageRecordLog
	 */
	public MifiUsageRecordLog getMifiUsageRecordLogById(String id) {
		return errorLogDao.get(id);
	}

	/**
	 * 根据时间获取对象
	 * 
	 * @athor shuxin
	 * @date 2016年8月5日上午9:26:39
	 * @param stampCreated
	 * @return MifiUsageRecordLog
	 */
	public MifiUsageRecordLog getMifiUsageRecordLogByStampCreated(String stampCreated) {
		String qlString = " from MifiUsageRecordLog t where t.stampCreated = '" + stampCreated + "'";
		List<MifiUsageRecordLog> logs = errorLogDao.find(qlString);
		if (logs.isEmpty()) {
			return new MifiUsageRecordLog();
		}
		return logs.get(0);
	}

	/**
	 * 查找未验证的记录信息按月统计列表
	 * 
	 * @athor shuxin
	 * @date 2016年9月9日下午4:56:17
	 * @return List<Map<String,String>>
	 */
	public List<Map<String, String>> findRecordLogsForMonth(MifiUsageRecordLogCondition condition) {
		return errorLogDao.findRecordLogForMonth(condition);
	}

	/**
	 * 通过月份查询当月的使用记录列表
	 * 
	 * @athor shuxin
	 * @date 2016年9月9日下午5:00:19
	 * @param month
	 * @return List<Map<String,String>>
	 */
	public List<Map<String, Object>> findRecordLogsByMonth(String month) {
		return errorLogDao.findRecordLogByMonth(month);
	}
	
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updateRecordLogOfIsCheckByMonth(String month){
		String sql = "UPDATE mifi_usage_record_log m SET m.is_check = 1 WHERE DATE_FORMAT(m.stamp_created, '%Y-%c') = '"+month+"'";
		errorLogDao.updateBySql(sql, null);
	}
	
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updateRecordLogOfIsCheckByDay(String day){
		String sql = "UPDATE mifi_usage_record_log m SET m.is_check = 1 WHERE DATE_FORMAT(m.stamp_created, '%Y-%m-%e') = '"+day+"'";
		errorLogDao.updateBySql(sql, null);
	}
}
