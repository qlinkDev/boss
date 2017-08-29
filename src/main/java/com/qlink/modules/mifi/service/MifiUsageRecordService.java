package main.java.com.qlink.modules.mifi.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.mifi.condition.MifiUsageRecordCondition;
import com.uu.modules.mifi.dao.MifiUsageRecordDao;
import com.uu.modules.mifi.entity.MifiUsageRecord;
import com.uu.modules.utils.Constants;

@Service
public class MifiUsageRecordService extends BaseService {
	public static Logger logger = LoggerFactory.getLogger(MifiUsageRecordService.class);

	@Autowired
	private MifiUsageRecordDao recordDao;

	/**
	 * 保存数据
	 * 
	 * @athor shuxin
	 * @date 2016年8月3日上午9:36:48
	 * @param mifiUsageRecords
	 *            void
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveMifiUsageRecords(List<MifiUsageRecord> mifiUsageRecords) {
		recordDao.save(mifiUsageRecords);
	}

	/**
	 * 分页查询
	 * 
	 * @athor shuxin
	 * @date 2016年8月3日下午2:41:38
	 * @param page
	 * @param condition
	 * @return Page<MifiUsageRecord>
	 */
	public Page<MifiUsageRecord> findMifiUsageRecordPage(Page<MifiUsageRecord> page,
			MifiUsageRecordCondition condition) {
		DetachedCriteria dc = recordDao.createDetachedCriteria();
		condition.build(dc);
		return recordDao.find(page, dc);
	}

	/**
	 * 根据ids（'xx','ww'）删除记录
	 * 
	 * @athor shuxin
	 * @date 2016年8月4日下午2:50:41
	 * @param string
	 *            void
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void deleteMifiUsageRecordByIds(String ids) {
		String sql = "DELETE FROM MifiUsageRecord t WHERE t.id in (" + ids + ")";
		recordDao.update(sql);
	}

	public List<Map<String, Integer>> statUsageRecordByMcc(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer(
				"SELECT m.country_name_cn,count(m.country_code) num,SUM(m.cost) cost FROM mifi_usage_record m where 1=1");
		String bath = ObjectUtils.toString(paramMap.get("bath"));
		if (StringUtils.isNotBlank(bath)) {
			buffer.append(" and m.bath = '").append(bath).append("'");
		}

		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and m.source_type = '").append(sourceType).append("'");
		}

		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(beginDate)) {
			buffer.append(" and m.stamp_created >= str_to_date('" + beginDate + "','%Y-%m-%d')");
		}
		if (StringUtils.isNotBlank(endDate)) {
			buffer.append(" and m.stamp_created < date_add(str_to_date('" + endDate + "','%Y-%m-%d'),interval 1 day)");
		}

		buffer.append(" GROUP BY m.country_code");
		return recordDao.findBySql(buffer.toString(), null, Map.class);
	}
	/**
	 * MCC导出
	 * @Description 
	 * @param paramMap
	 * @return 
	 * @return List<MifiUsageRecord>  
	 * @author wangsai
	 * @date 2017年2月28日 上午10:14:42
	 */
	public List<MifiUsageRecord> findstatUsageRecordByMccExport(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer(
				"SELECT m.country_name_cn,count(m.country_code) num,SUM(m.cost) cost FROM mifi_usage_record m where 1=1");
		String bath = ObjectUtils.toString(paramMap.get("bath"));
		if (StringUtils.isNotBlank(bath)) {
			buffer.append(" and m.bath = '").append(bath).append("'");
		}

		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and m.source_type = '").append(sourceType).append("'");
		}

		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(beginDate)) {
			buffer.append(" and m.stamp_created >= str_to_date('" + beginDate + "','%Y-%m-%d')");
		}
		if (StringUtils.isNotBlank(endDate)) {
			buffer.append(" and m.stamp_created < date_add(str_to_date('" + endDate + "','%Y-%m-%d'),interval 1 day)");
		}
		buffer.append(" GROUP BY m.country_code");
		List<String[]> listObjArr = recordDao.findBySql(buffer.toString());
		ArrayList <MifiUsageRecord> list = null;
		if (listObjArr!=null && listObjArr.size()>0) {
			list = new ArrayList<MifiUsageRecord>();
			MifiUsageRecord UsageRecord = null;
			for (Object[] objs : listObjArr) {
				UsageRecord	 = new MifiUsageRecord();
				if(StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)){
					UsageRecord.setDeviceStatus(ObjectUtils.toString(beginDate+"到"+endDate));
				}else{
					UsageRecord.setDeviceStatus(ObjectUtils.toString("所有时间"));
				}
				UsageRecord.setCountryNameEn(ObjectUtils.toString(objs[1]));
				UsageRecord.setDeviceLinkStatus(ObjectUtils.toString(objs[2]));
				UsageRecord.setCountryNameCn(ObjectUtils.toString(objs[0]));
				list.add(UsageRecord);
				UsageRecord = null;
			}
		}
		return list;

	}
	public List<Map<String, Integer>> statUsageRecordByDate(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer("SELECT");
		String orderType = ObjectUtils.toString(paramMap.get("orderType"));
		String year = ObjectUtils.toString(paramMap.get("year"));
		String month = ObjectUtils.toString(paramMap.get("month"));
		if ("year".equals(orderType)) {
			buffer.append(" m.").append(orderType).append(" time");
		} else if ("month".equals(orderType)) {
			buffer.append(" CONCAT(m.year,").append("'-'").append(",m.month)").append(" time");
			;
		} else {
			buffer.append(" CONCAT(m.year,").append("'-'").append(",m.month,").append("'-'").append(",m.day)")
					.append(" time");
		}
		buffer.append(",count(m.").append(orderType).append(") num FROM mifi_usage_record m where 1=1");
		String bath = ObjectUtils.toString(paramMap.get("bath"));
		if (StringUtils.isNotBlank(bath)) {
			buffer.append(" and m.bath = '").append(bath).append("'");
		}

		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and m.source_type = '").append(sourceType).append("'");
		}

		if (StringUtils.isNotBlank(year)) {
			buffer.append(" and m.year = ").append(year);
		}

		if (StringUtils.isNotBlank(month)) {
			buffer.append(" and m.month = ").append(month);
		}

		buffer.append(" GROUP BY m.").append(orderType).append(" asc");
		return recordDao.findBySql(buffer.toString(), null, Map.class);
	}

	public List<Map<String, Integer>> statUsageRecordByYear(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer(
				"SELECT DATE_FORMAT(m.stamp_created, '%Y') as time, count(DATE_FORMAT(m.stamp_created, '%Y')) as num, sum(m.cost) as cost FROM mifi_usage_record m where 1=1");
		String begin = ObjectUtils.toString(paramMap.get("beginDate"));
		String end = ObjectUtils.toString(paramMap.get("endDate"));
		String bath = ObjectUtils.toString(paramMap.get("bath"));
		if (StringUtils.isNotBlank(bath)) {
			buffer.append(" and m.bath = '").append(bath).append("'");
		}
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and m.source_type = '").append(sourceType).append("'");
		}
		String allowedMcc = ObjectUtils.toString(paramMap.get("allowedMcc"));
		if (StringUtils.isNotBlank(allowedMcc)) {
			buffer.append(" and m.country_code = '").append(allowedMcc).append("'");
		}
		if (StringUtils.isNotBlank(begin)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y') >= '").append(begin).append("'");
		}
		if (StringUtils.isNotBlank(end)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y') <= '").append(end).append("'");
		}

		buffer.append(" GROUP BY DATE_FORMAT(m.stamp_created, '%Y')").append(" asc");
		return recordDao.findBySql(buffer.toString(), null, Map.class);
	}
	/**
	 * 按年导出
	 * @Description 
	 * @param paramMap
	 * @return 
	 * @return List<MifiUsageRecord>  
	 * @author wangsai
	 * @date 2017年2月28日 上午10:38:51
	 */
	public  List<MifiUsageRecord> statUsageRecordByYearExport(Map<String , Object> paramMap){
		 StringBuffer buffer= new StringBuffer(
					"SELECT DATE_FORMAT(m.stamp_created, '%Y') as time, count(DATE_FORMAT(m.stamp_created, '%Y')) as num, sum(m.cost) as cost , m.country_name_cn FROM mifi_usage_record m where 1=1");
		 	String begin = ObjectUtils.toString(paramMap.get("beginDate"));
			String end = ObjectUtils.toString(paramMap.get("endDate"));
			String bath = ObjectUtils.toString(paramMap.get("bath"));
			if (StringUtils.isNotBlank(bath)) {
				buffer.append(" and m.bath = '").append(bath).append("'");
			}
			String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
			if (StringUtils.isNotBlank(sourceType)) {
				buffer.append(" and m.source_type = '").append(sourceType).append("'");
			}
			String allowedMcc = ObjectUtils.toString(paramMap.get("allowedMcc"));
			if (StringUtils.isNotBlank(allowedMcc)) {
				buffer.append(" and m.country_code = '").append(allowedMcc).append("'");
			}
			if (StringUtils.isNotBlank(begin)) {
				buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y') >= '").append(begin).append("'");
			}
			if (StringUtils.isNotBlank(end)) {
				buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y') <= '").append(end).append("'");
			}

			buffer.append(" GROUP BY DATE_FORMAT(m.stamp_created, '%Y')").append(" asc");
			List<String[]> listObjArr = recordDao.findBySql(buffer.toString());
			ArrayList <MifiUsageRecord> list = null;
			if (listObjArr!=null && listObjArr.size()>0) {
				list = new ArrayList<MifiUsageRecord>();
				MifiUsageRecord UsageRecord = null;
				for (Object[] objs : listObjArr) {
					UsageRecord	 = new MifiUsageRecord();
					UsageRecord.setDeviceStatus(ObjectUtils.toString(objs[0]));
					UsageRecord.setCountryNameEn(ObjectUtils.toString(objs[1]));
					UsageRecord.setDeviceLinkStatus(ObjectUtils.toString(objs[2]));
					if(StringUtils.isNotBlank(allowedMcc)){
						UsageRecord.setCountryNameCn(ObjectUtils.toString(objs[3]));
					}else{
						UsageRecord.setCountryNameCn("全部国家");
					}
					list.add(UsageRecord);
					UsageRecord = null;
				}
			}
		return list;
	}
	public List<Map<String, Integer>> statUsageRecordByMonth(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer(
				"SELECT DATE_FORMAT(m.stamp_created, '%Y-%c') as time, count(DATE_FORMAT(m.stamp_created, '%Y-%c')) as num, sum(m.cost) as cost FROM mifi_usage_record m where 1=1");
		String begin = ObjectUtils.toString(paramMap.get("beginDate"));
		String end = ObjectUtils.toString(paramMap.get("endDate"));
		String bath = ObjectUtils.toString(paramMap.get("bath"));
		if (StringUtils.isNotBlank(bath)) {
			buffer.append(" and m.bath = '").append(bath).append("'");
		}
		
		String allowedMcc = ObjectUtils.toString(paramMap.get("allowedMcc"));
		if (StringUtils.isNotBlank(allowedMcc)) {
			buffer.append(" and m.country_code = '").append(allowedMcc).append("'");
		}
		
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and m.source_type = '").append(sourceType).append("'");
		}

		if (StringUtils.isNotBlank(begin)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%m') >= '").append(begin).append("'");
		}

		if (StringUtils.isNotBlank(end)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%m') <= '").append(end).append("'");
		}

		buffer.append(" GROUP BY ").append("DATE_FORMAT(m.stamp_created, '%Y-%c')").append(" asc");
		return recordDao.findBySql(buffer.toString(), null, Map.class);
	}
	/**
	 * 按月导出
	 * @Description 
	 * @param paramMap
	 * @return 
	 * @return List<MifiUsageRecord>  
	 * @author wangsai
	 * @date 2017年2月28日 上午10:38:51
	 */
	public  List<MifiUsageRecord> statUsageRecordByMonthExport(Map<String , Object> paramMap){
		StringBuffer buffer = new StringBuffer(
				"SELECT DATE_FORMAT(m.stamp_created, '%Y-%c') as time, count(DATE_FORMAT(m.stamp_created, '%Y-%c')) as num, sum(m.cost) as cost , m.country_name_cn  FROM mifi_usage_record m where 1=1");
		String begin = ObjectUtils.toString(paramMap.get("beginDate"));
		String end = ObjectUtils.toString(paramMap.get("endDate"));
		String bath = ObjectUtils.toString(paramMap.get("bath"));
		if (StringUtils.isNotBlank(bath)) {
			buffer.append(" and m.bath = '").append(bath).append("'");
		}
		
		String allowedMcc = ObjectUtils.toString(paramMap.get("allowedMcc"));
		if (StringUtils.isNotBlank(allowedMcc)) {
			buffer.append(" and m.country_code = '").append(allowedMcc).append("'");
		}
		
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and m.source_type = '").append(sourceType).append("'");
		}

		if (StringUtils.isNotBlank(begin)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%m') >= '").append(begin).append("'");
		}

		if (StringUtils.isNotBlank(end)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%m') <= '").append(end).append("'");
		}

		buffer.append(" GROUP BY ").append("DATE_FORMAT(m.stamp_created, '%Y-%c')").append(" asc");
			List<String[]> listObjArr = recordDao.findBySql(buffer.toString());
			ArrayList <MifiUsageRecord> list = null;
			if (listObjArr!=null && listObjArr.size()>0) {
				list = new ArrayList<MifiUsageRecord>();
				MifiUsageRecord UsageRecord = null;
				for (Object[] objs : listObjArr) {
					UsageRecord	 = new MifiUsageRecord();
					UsageRecord.setDeviceStatus(ObjectUtils.toString(objs[0]));
					UsageRecord.setCountryNameEn(ObjectUtils.toString(objs[1]));
					UsageRecord.setDeviceLinkStatus(ObjectUtils.toString(objs[2]));
					if(StringUtils.isNotBlank(allowedMcc)){
						UsageRecord.setCountryNameCn(ObjectUtils.toString(objs[3]));
					}else{
						UsageRecord.setCountryNameCn("全部国家");
					}
					list.add(UsageRecord);
					UsageRecord = null;
				}
			}
		return list;
	}
	public List<Map<String, Object>> statUsageRecordByDay(Map<String, Object> paramMap,String[] allowedMcc) {
		StringBuffer buffer = new StringBuffer(
				"SELECT DATE_FORMAT(m.stamp_created, '%Y-%c-%e') as time, count(DATE_FORMAT(m.stamp_created, '%Y-%c-%e')) as num, sum(m.cost) as cost ");
		if (allowedMcc != null && 0 < allowedMcc.length) {
			buffer.append(", country_code ");
		}
		buffer.append(" FROM mifi_usage_record m where 1=1 ");
		String begin = ObjectUtils.toString(paramMap.get("beginDate"));
		String end = ObjectUtils.toString(paramMap.get("endDate"));
		String bath = ObjectUtils.toString(paramMap.get("bath"));
		if (StringUtils.isNotBlank(bath)) {
			buffer.append(" and m.bath = '").append(bath).append("'");
		}
		StringBuilder stringBuilder = new StringBuilder();
		String sqlSuffix = StringUtils.EMPTY;
		if (allowedMcc != null && 0 < allowedMcc.length) {
			for (int i = 0; i < allowedMcc.length; i++) {
				stringBuilder.append(Constants.SINGLE_QUOTES).append(allowedMcc[i]).append(Constants.SINGLE_QUOTES).append(Constants.COMMA);
			}
			buffer.append(" and m.country_code in (" ).append(stringBuilder).deleteCharAt(buffer.length()-1).append(")");
			sqlSuffix = " GROUP BY time, country_code order by country_code, stamp_created asc";
		}else{
			sqlSuffix = " GROUP BY time order by stamp_created asc";
		}
		
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and m.source_type = '").append(sourceType).append("'");
		}

		if (StringUtils.isNotBlank(begin)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%m-%d') >= '").append(begin).append("'");
		}

		if (StringUtils.isNotBlank(end)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%m-%d') <= '").append(end).append("'");
		}

		buffer.append(sqlSuffix);
		return recordDao.findBySql(buffer.toString(),null, Map.class);
	}

	/**
	 * 根据国家编码条件获取相应的国家中文名称和对应的国家编码
	 * 
	 * @athor shuxin
	 * @date 2016年8月29日上午10:15:57
	 * @param countryCode
	 * @return List<Map<String,String>>
	 */
	public List<Map<String, String>> findMccsByCountryCode(String countryCode) {
		String sql = "select  country_name_cn, country_code from mcc_def WHERE country_code in (" + countryCode
				+ ") group by country_name_cn order by convert(country_name_cn using gbk) asc";
		return recordDao.findBySql(sql, null, Map.class);
	}

	/**
	 * 获取国家编码和流量总和集合列表
	 * 
	 * @athor shuxin
	 * @date 2016年8月29日上午10:51:37
	 * @param paramMap
	 * @return List<Map<String,Long>>
	 */
	public List<Map<String, Object>> findMifiDateInfo(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer(
				"SELECT m.country_code countryCode,SUM(m.datainfo) dateInfo FROM `mifi_usage_record` m where 1=1");
		String begin = ObjectUtils.toString(paramMap.get("beginDate"));
		String end = ObjectUtils.toString(paramMap.get("endDate"));
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and m.source_type = '").append(sourceType).append("'");
		}

		if (StringUtils.isNotBlank(begin)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%m-%d') >= '").append(begin).append("'");
		}

		if (StringUtils.isNotBlank(end)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%m-%d') <= '").append(end).append("'");
		}

		buffer.append(" GROUP BY m.country_code");

		return recordDao.findBySql(buffer.toString(), null, Map.class);
	}

	/**
	 * 查询某个国家在时间段内设备有效使用的时间列表
	 * 
	 * @athor shuxin
	 * @date 2016年8月29日上午11:04:14
	 * @param paramMap
	 * @return List<String>
	 */
	public List<String> findValidDays(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer("SELECT m.stamp_created day FROM `mifi_usage_record` m where 1=1");
		String begin = ObjectUtils.toString(paramMap.get("beginDate"));
		String end = ObjectUtils.toString(paramMap.get("endDate"));
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		String countryCode = ObjectUtils.toString(paramMap.get("countryCode"));
		if (StringUtils.isNotBlank(countryCode)) {
			buffer.append(" and m.country_code = '").append(countryCode).append("'");
		}

		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and m.source_type = '").append(sourceType).append("'");
		}

		String dayOff = ObjectUtils.toString(paramMap.get("dayOff"));
		if ("1".equals(dayOff)) {
			if (StringUtils.isNotBlank(begin)) {
				buffer.append(" and m.stamp_created >= '").append(begin).append(" 00:00:00'");
			}

			if (StringUtils.isNotBlank(end)) {
				buffer.append(" and m.stamp_created <= '").append(end).append(" 23:59:59'");
			}
		} else {
			if (StringUtils.isNotBlank(begin)) {
				buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%m-%d') >= '").append(begin).append("'");
			}

			if (StringUtils.isNotBlank(end)) {
				buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%m-%d') <= '").append(end).append("'");
			}
			buffer.append(" GROUP BY DATE_FORMAT(m.stamp_created, '%Y-%m-%d')");
		}
		return recordDao.findBySql(buffer.toString());
	}
	
	/**
	 * 查询某个国家在时间段内设备列表
	 * @athor shuxin
	 * @date 2016年8月31日上午10:46:11
	 * @param paramMap
	 * @return
	 * List<String> 
	 */
	public List<String> findValidDevicesDays(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer("SELECT m.imei  FROM `mifi_usage_record` m where 1=1");
		String begin = ObjectUtils.toString(paramMap.get("beginDate"));
		String end = ObjectUtils.toString(paramMap.get("endDate"));
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		String countryCode = ObjectUtils.toString(paramMap.get("countryCode"));
		if (StringUtils.isNotBlank(countryCode)) {
			buffer.append(" and m.country_code = '").append(countryCode).append("'");
		}

		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and m.source_type = '").append(sourceType).append("'");
		}

		String dayOff = ObjectUtils.toString(paramMap.get("dayOff"));
		if ("1".equals(dayOff)) {
			if (StringUtils.isNotBlank(begin)) {
				buffer.append(" and m.stamp_created >= '").append(begin).append(" 00:00:00'");
			}

			if (StringUtils.isNotBlank(end)) {
				buffer.append(" and m.stamp_created <= '").append(end).append(" 23:59:59'");
			}
		} else {
			if (StringUtils.isNotBlank(begin)) {
				buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%m-%d') >= '").append(begin).append("'");
			}

			if (StringUtils.isNotBlank(end)) {
				buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%m-%d') <= '").append(end).append("'");
			}
		}
		return recordDao.findBySql(buffer.toString());
	}

	/**
	 * 按年份统计某个国家流量总和
	 * 
	 * @athor shuxin
	 * @date 2016年8月29日下午6:02:39
	 * @param paramMap
	 * @return List<Map<String,Integer>>
	 */
	public List<Map<String, Object>> statUseDateInfoByYear(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer(
				"SELECT (DATE_FORMAT(m.stamp_created, '%Y')) `year`,SUM(m.datainfo) dateInfo FROM `mifi_usage_record` m where 1=1");
		String begin = ObjectUtils.toString(paramMap.get("beginDate"));
		String end = ObjectUtils.toString(paramMap.get("endDate"));
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		String countryCode = ObjectUtils.toString(paramMap.get("countryCode"));
		if (StringUtils.isNotBlank(countryCode)) {
			buffer.append(" and m.country_code = '").append(countryCode).append("'");
		}

		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and m.source_type = '").append(sourceType).append("'");
		}

		if (StringUtils.isNotBlank(begin)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y') >= '").append(begin).append("'");
		}

		if (StringUtils.isNotBlank(end)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y') <= '").append(end).append("'");
		}

		buffer.append(" GROUP BY DATE_FORMAT(m.stamp_created, '%Y')");
		return recordDao.findBySql(buffer.toString(), null, Map.class);
	}

	/**
	 * 某个国家在年份之内有效的设备使用列表
	 * 
	 * @athor shuxin
	 * @date 2016年8月30日上午10:05:19
	 * @param paramMap
	 * @return List<String>
	 */
	public List<String> findValidDaysByYear(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer("SELECT m.stamp_created FROM `mifi_usage_record` m where 1=1");
		String begin = ObjectUtils.toString(paramMap.get("beginDate"));
		String end = ObjectUtils.toString(paramMap.get("endDate"));
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		String countryCode = ObjectUtils.toString(paramMap.get("countryCode"));
		if (StringUtils.isNotBlank(countryCode)) {
			buffer.append(" and m.country_code = '").append(countryCode).append("'");
		}

		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and m.source_type = '").append(sourceType).append("'");
		}

		if (StringUtils.isNotBlank(begin)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y') >= '").append(begin).append("'");
		}

		if (StringUtils.isNotBlank(end)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y') <= '").append(end).append("'");
		}
		buffer.append(" GROUP BY DATE_FORMAT(m.stamp_created, '%Y-%m-%d ')");
		return recordDao.findBySql(buffer.toString());
	}
	
	public List<String> findValidDevicesByYear(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer("SELECT m.imei FROM `mifi_usage_record` m where 1=1");
		String begin = ObjectUtils.toString(paramMap.get("beginDate"));
		String end = ObjectUtils.toString(paramMap.get("endDate"));
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		String countryCode = ObjectUtils.toString(paramMap.get("countryCode"));
		if (StringUtils.isNotBlank(countryCode)) {
			buffer.append(" and m.country_code = '").append(countryCode).append("'");
		}

		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and m.source_type = '").append(sourceType).append("'");
		}

		if (StringUtils.isNotBlank(begin)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y') >= '").append(begin).append("'");
		}

		if (StringUtils.isNotBlank(end)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y') <= '").append(end).append("'");
		}
		return recordDao.findBySql(buffer.toString());
	}

	/**
	 * 按月份统计某个国家流量总和
	 * 
	 * @athor shuxin
	 * @date 2016年8月30日上午11:31:03
	 * @param paramMap
	 * @return List<Map<String,Object>>
	 */
	public List<Map<String, Object>> statUseDateInfoByMonth(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer(
				"SELECT (DATE_FORMAT(m.stamp_created, '%Y-%c')) `year`,SUM(m.datainfo) dateInfo FROM `mifi_usage_record` m where 1=1");
		String begin = ObjectUtils.toString(paramMap.get("beginDate"));
		String end = ObjectUtils.toString(paramMap.get("endDate"));
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		String countryCode = ObjectUtils.toString(paramMap.get("countryCode"));
		if (StringUtils.isNotBlank(countryCode)) {
			buffer.append(" and m.country_code = '").append(countryCode).append("'");
		}

		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and m.source_type = '").append(sourceType).append("'");
		}

		if (StringUtils.isNotBlank(begin)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%m') >= '").append(begin).append("'");
		}

		if (StringUtils.isNotBlank(end)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%m') <= '").append(end).append("'");
		}

		buffer.append(" GROUP BY DATE_FORMAT(m.stamp_created, '%Y-%m')");
		return recordDao.findBySql(buffer.toString(), null, Map.class);
	}

	/**
	 * 某个国家在月份之内有效的设备使用列表
	 * 
	 * @athor shuxin
	 * @date 2016年8月30日上午11:34:21
	 * @param paramMap
	 * @return List<String>
	 */
	public List<String> findValidDaysByMonth(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer("SELECT m.stamp_created FROM `mifi_usage_record` m where 1=1");
		String ym = ObjectUtils.toString(paramMap.get("ym"));
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		String countryCode = ObjectUtils.toString(paramMap.get("countryCode"));
		if (StringUtils.isNotBlank(countryCode)) {
			buffer.append(" and m.country_code = '").append(countryCode).append("'");
		}

		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and m.source_type = '").append(sourceType).append("'");
		}

		if (StringUtils.isNotBlank(ym)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%c') = '").append(ym).append("'");
		}

		buffer.append(" GROUP BY DATE_FORMAT(m.stamp_created, '%Y-%m-%d ')");
		return recordDao.findBySql(buffer.toString());
	}

	/**
	 * 某个月内使用的设备列表
	 * 
	 * @athor shuxin
	 * @date 2016年8月31日上午9:34:07
	 * @param paramMap
	 * @return List<String>
	 */
	public List<String> findValidDeviceByMonth(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer("SELECT m.imei FROM `mifi_usage_record` m where 1=1");
		String ym = ObjectUtils.toString(paramMap.get("ym"));
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		String countryCode = ObjectUtils.toString(paramMap.get("countryCode"));
		if (StringUtils.isNotBlank(countryCode)) {
			buffer.append(" and m.country_code = '").append(countryCode).append("'");
		}

		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and m.source_type = '").append(sourceType).append("'");
		}

		if (StringUtils.isNotBlank(ym)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%c') = '").append(ym).append("'");
		}

		return recordDao.findBySql(buffer.toString());
	}

	/**
	 * 按日统计某个国家一天流量总和
	 * 
	 * @athor shuxin
	 * @date 2016年8月30日下午4:01:40
	 * @param paramMap
	 * @return List<Map<String,Object>>
	 */
	public List<Map<String, Object>> statUseDateInfoByDay(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer(
				"SELECT (DATE_FORMAT(m.stamp_created, '%Y-%c-%e')) `year`,SUM(m.datainfo) dateInfo FROM `mifi_usage_record` m where 1=1");
		String begin = ObjectUtils.toString(paramMap.get("beginDate"));
		String end = ObjectUtils.toString(paramMap.get("endDate"));
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		String countryCode = ObjectUtils.toString(paramMap.get("countryCode"));
		if (StringUtils.isNotBlank(countryCode)) {
			buffer.append(" and m.country_code = '").append(countryCode).append("'");
		}

		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and m.source_type = '").append(sourceType).append("'");
		}

		if (StringUtils.isNotBlank(begin)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%m-%d') >= '").append(begin).append("'");
		}

		if (StringUtils.isNotBlank(end)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%m-%d') <= '").append(end).append("'");
		}

		buffer.append(" GROUP BY DATE_FORMAT(m.stamp_created, '%Y-%m-%d')");
		return recordDao.findBySql(buffer.toString(), null, Map.class);
	}
	
	/**
	 * 某个国家在一天之内有效的设备记录使用列表
	 * 
	 * @athor shuxin
	 * @date 2016年8月30日下午4:01:13
	 * @param paramMap
	 * @return List<String>
	 */
	public List<String> findValidDaysByDay(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer("SELECT m.imei FROM `mifi_usage_record` m where 1=1");
		String ym = ObjectUtils.toString(paramMap.get("day"));
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		String countryCode = ObjectUtils.toString(paramMap.get("countryCode"));
		if (StringUtils.isNotBlank(countryCode)) {
			buffer.append(" and m.country_code = '").append(countryCode).append("'");
		}

		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and m.source_type = '").append(sourceType).append("'");
		}

		if (StringUtils.isNotBlank(ym)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%c-%e') = '").append(ym).append("'");
		}

		return recordDao.findBySql(buffer.toString());
	}
	/**
	 * 导出使用记录按日统计数据  
	 * @Description 
	 * @author wangsai
	 * @date 2016年12月19日 上午11:36:05
	 */
	public List<MifiUsageRecord> findMifiUsageRecordListForExport(Map<String, Object> paramMap ,@RequestParam(required=false) String[] allowedMcc ) throws ParseException {
		List<MifiUsageRecord> list = null;
		StringBuffer buffer = new StringBuffer(
				"SELECT DATE_FORMAT(m.stamp_created, '%Y-%c-%e') as time, count(DATE_FORMAT(m.stamp_created, '%Y-%c-%e')) as num, sum(m.cost) as cost ,m.country_name_cn FROM mifi_usage_record m where 1=1");
		String begin = ObjectUtils.toString(paramMap.get("beginDate"));
		String end = ObjectUtils.toString(paramMap.get("endDate"));
		String bath = ObjectUtils.toString(paramMap.get("bath"));
		if (StringUtils.isNotBlank(bath)) {
			buffer.append(" and m.bath = '").append(bath).append("'");
		}
		String bb="";
		if(allowedMcc!=null){
		for (int i = 0; i < allowedMcc.length; i++) {
		String qw=allowedMcc[i];
		bb+="'"+qw+"'"+"," ;
		}}
		if (StringUtils.isNotBlank(bb)) {
				buffer.append(" and m.country_code in (" ).append(bb).deleteCharAt(buffer.length()-1).append(")") ;
			}

		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and m.source_type = '").append(sourceType).append("'");
		}

		if (StringUtils.isNotBlank(begin)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%m-%d') >= '").append(begin).append("'");
		}

		if (StringUtils.isNotBlank(end)) {
			buffer.append(" and DATE_FORMAT(m.stamp_created, '%Y-%m-%d') <= '").append(end).append("'");
		}

		buffer.append(" GROUP BY ").append("m.country_code ,time").append(" asc");
		List<String[]> listObjArr = recordDao.findBySql(buffer.toString());
		if (listObjArr!=null && listObjArr.size()>0) {
			list = new ArrayList<MifiUsageRecord>();
			MifiUsageRecord UsageRecord = null;
			for (Object[] objs : listObjArr) {
				UsageRecord = new MifiUsageRecord();
				UsageRecord.setDeviceStatus(ObjectUtils.toString(objs[0]));
				UsageRecord.setCountryNameEn(ObjectUtils.toString(objs[1]));
				UsageRecord.setDeviceLinkStatus(ObjectUtils.toString(objs[2]));
				UsageRecord.setCountryNameCn(ObjectUtils.toString(objs[3]));
				list.add(UsageRecord);
				UsageRecord = null;
			}
		}
		return list;
	}
	
}
