package com.uu.modules.mifi.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.mifi.dao.MifiManageDao;
import com.uu.modules.mifi.dao.MifiOrderDao;
import com.uu.modules.mifi.dao.MifiOrderDetailDao;
import com.uu.modules.mifi.dao.MifiTrafficDao;
import com.uu.modules.mifi.entity.DeviceMonitorDetail;
import com.uu.modules.mifi.entity.SimCardType;
import com.uu.modules.om.dao.RegionDao;
import com.uu.modules.sys.utils.DictUtils;

@Service
public class MifiTrafficService extends BaseService {

	public static Logger logger = LoggerFactory.getLogger(MifiTrafficService.class);

	public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private MifiTrafficDao mifiTrafficDao;
	
	@Autowired
	private MifiManageDao mifiManageDao;
	
	@Autowired
	private MifiOrderDao mifiOrderDao;
	
	@Autowired
	private MifiOrderDetailDao mifiOrderDetailDao;
	
	@Autowired
	private RegionDao regionDao;

	/**
	 * 
	 * @Description 根据设备UEID从simcardstatus表统计设备流量
	 * @param page
	 * @param paramMap
	 * @return Page<Object[]>  
	 * @author yifang.huang
	 * @date 2017年3月28日 下午3:02:23
	 */
	public Page<Object[]> findMifiTrafficList_back(Page<Object[]> page, Map<String, Object> paramMap) {
		
		String imei = ObjectUtils.toString(paramMap.get("imei"));
		if (StringUtils.isBlank(imei))
			return null;
		
		// 取设备ueid
		String ueid = mifiManageDao.getDeviceUeid(imei);
		if (StringUtils.isBlank(ueid))
			return null;
		
		StringBuffer sql = new StringBuffer("SELECT c.countryCode, c.countryNameCn, c.countryNameEn, c.createDate,"
				+ " format(sum(c.dataInfo)/1024/1024, 2) dataInfo"
				+ " FROM (SELECT b.country_code countryCode, b.country_name_cn countryNameCn, b.country_name_en countryNameEn, , DATE_FORMAT(a.createDate, '%Y-%m-%d') createDate, a.dataInfo"
				+ " FROM (SELECT UEID_MCC mcc, date(stamp_created) createDate, sum(a.DATAADD) dataInfo FROM simcardstatus a"
				+ " WHERE (1=1) and UEID_CURRNUM='"+ueid+"'");
		
		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		if (StringUtils.isNotBlank(beginDate)) {
			sql.append(" and stamp_created>=str_to_date('" + beginDate + "', '%Y-%m-%d')");
		}
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(endDate)) {
			sql.append(" and stamp_created<date_add(str_to_date('" + endDate + "', '%Y-%m-%d'),interval 1 day)");
		}
		
		sql.append(" AND a.DATAADD>0 AND a.MCC!=0 GROUP BY UEID_MCC, date(stamp_created))AS a, mcc_def b WHERE a.mcc = b.mcc )c "
				+ "GROUP BY c.countryCode, c.countryNameCn, c.countryNameEn, DATE_FORMAT(c.createDate, '%Y-%m-%d')");
		
		return mifiTrafficDao.findBySql(page, sql.toString());
	}

	/**
	 * 
	 * @Description 根据设备UEID从simcardstatus表查询设备今天流量
	 * @param page
	 * @param paramMap
	 * @return Page<Object[]>  
	 * @author yifang.huang
	 * @date 2017年4月5日 上午11:28:05
	 */
	public Page<Object[]> findTodayFlow(Page<Object[]> page, Map<String, Object> paramMap) {
		
		String imei = ObjectUtils.toString(paramMap.get("imei"));
		if (StringUtils.isBlank(imei))
			return null;
		
		// 取设备ueid
		String ueid = mifiManageDao.getDeviceUeid(imei);
		if (StringUtils.isBlank(ueid))
			return null;
		
		StringBuffer sql = new StringBuffer("SELECT c.countryCode, c.countryNameCn, c.countryNameEn, c.createDate,"
				+ " format(sum(c.dataInfo)/1024/1024, 2) dataInfo"
				+ " FROM (SELECT b.country_code countryCode, b.country_name_cn countryNameCn, b.country_name_en countryNameEn, a.createDate, a.dataInfo"
				+ " FROM (SELECT UEID_MCC mcc, date(stamp_created) createDate, sum(a.DATAADD) dataInfo FROM simcardstatus a"
				+ " WHERE (1=1) and UEID_CURRNUM='"+ueid+"'");
		
		// 今天日期
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		sql.append(" and stamp_created>=str_to_date('" + date + "', '%Y-%m-%d')");
		sql.append(" and stamp_created<date_add(str_to_date('" + date + "', '%Y-%m-%d'),interval 1 day)");
		
		sql.append(" AND a.DATAADD>0 AND a.MCC!=0 GROUP BY UEID_MCC, date(stamp_created))AS a, mcc_def b WHERE a.mcc = b.mcc )c "
				+ "GROUP BY c.countryCode, c.countryNameCn, c.countryNameEn");
		
		return mifiTrafficDao.findBySql(page, sql.toString());
	}

	/**
	 * 
	 * @Description 根据设备编号从mifi_usage_record表统计设备流量
	 * @param page
	 * @param paramMap
	 * @return Page<Object[]>  
	 * @author yifang.huang
	 * @date 2017年3月28日 下午3:02:23
	 */
	public Page<Object[]> findMifiTrafficList(Page<Object[]> page, Map<String, Object> paramMap) {
		
		String imei = ObjectUtils.toString(paramMap.get("imei"));
		if (StringUtils.isBlank(imei))
			return null;
		
		StringBuffer sql = new StringBuffer("SELECT a.country_code countryCode, a.country_name_cn countryNameCn, a.country_name_en countryNameEn,"
				+ " DATE_FORMAT(a.stamp_created, '%Y-%m-%d') createDate, sum(a.datainfo)dataInfo FROM mifi_usage_record a WHERE a.imei='" + imei + "'");
		
		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		if (StringUtils.isNotBlank(beginDate)) {
			sql.append(" and a.stamp_created>=str_to_date('" + beginDate + "', '%Y-%m-%d')");
		}
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(endDate)) {
			sql.append(" and a.stamp_created<date_add(str_to_date('" + endDate + "', '%Y-%m-%d'),interval 1 day)");
		}
		
		sql.append("GROUP BY a.country_code, a.country_name_cn, a.country_name_en, a.stamp_created ORDER BY a.stamp_created asc");
		
		return mifiTrafficDao.findBySql(page, sql.toString());
	}

	public Page<HashMap> findMccTrafficList(Page<HashMap> page, Map<String, Object> paramMap) {
		String sqlString = "";
		return mifiTrafficDao.findBySql(page, sqlString, Map.class);
	}

	// 20160420新增字段dataadd 记录每次上传的流量值
	public Page<HashMap> findSimCardTrafficList(Page<HashMap> page, Map<String, Object> paramMap) {
		String sqlString = "select * from (";
		sqlString += "select hex(t.imsi) as imsi,hex(t.iccid) as iccid,format(sum(t.dataadd)/1024/1024,2) as dataused from simcardstatus t ";
		sqlString += " where t.dataadd != 0 ";
		String iccid = ObjectUtils.toString(paramMap.get("iccid"));
		if (StringUtils.isNotBlank(iccid)) {
			sqlString += " and hex(t.iccid) = '" + iccid + "' ";
		}
		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		if (StringUtils.isNotBlank(beginDate)) {
			sqlString += " and t.stamp_created >= str_to_date('" + beginDate + "','%Y-%m-%d %H:%i:%s')";
		}
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(endDate)) {
			sqlString += " and t.stamp_created < str_to_date('" + endDate + "','%Y-%m-%d %H:%i:%s')";
		}
		sqlString += " group by hex(t.imsi),hex(t.iccid)) as simcard_ ";
		sqlString += " order by iccid desc ";
		return mifiTrafficDao.findBySql(page, sqlString, Map.class);
	}

	public Page<HashMap<String, Object>> findSimNodeStatList(Page<HashMap<String, Object>> page, Map<String, Object> paramMap, String[] typeArr) {
		String sqlString = "select _x.* from (";
		sqlString += "select hex(t.iccid) iccid,t.SIMCARDVALIDDAY,t.stamp_firstactive,convert(t.DATACAP/1024/1024,decimal) as dataCap,t.simbankid,t.simid ";
		// 剩余有效期 9999表示非一次性卡 永久有效 stamp_firstactive 0000-00-00 00:00:00未激活
		sqlString += ",if(isnull(t.SIMCARDVALIDDAY),9999,if(DATE_FORMAT(t.stamp_firstactive,'%Y') = '0000',T.SIMCARDVALIDDAY,TIMESTAMPDIFF(DAY,DATE_ADD(date(sysdate()),INTERVAL 1 DAY),DATE_ADD(DATE(T.stamp_firstactive),INTERVAL t.SIMCARDVALIDDAY DAY)))) as remainValidDay ";
		// 剩余高速流量
		/*
		 * sqlString += ",convert((DATACAP-ifnull(("; sqlString +=
		 * "select sum(f.DATAADD) from simcardstatus f where f.usimstatus = 3 and hex(f.ICCID) = hex(t.iccid)"
		 * ; sqlString +=
		 * " and (isnull(t.SIMCARDVALIDDAY) or date_format(stamp_created,'%Y-%m') = date_format(now(),'%Y-%m'))"
		 * ; sqlString += "),0))/1024/1024,decimal) as remainCap ";
		 */
		sqlString += ",convert(t.dataused/1024/1024,decimal) as usedCap,(convert(t.DATACAP/1024/1024,decimal)-convert(t.dataused/1024/1024,decimal)) as remainCap,c.type ";
		sqlString += " from simnode t  LEFT JOIN card_basic_info c ON hex(t.iccid) = c.sn where hex(t.iccid) != '00000000000000000000' ";
		sqlString += " and t.USIMSTATUS != 0 ";
		String iccid = ObjectUtils.toString(paramMap.get("iccid"));
		if (StringUtils.isNotBlank(iccid)) {
			sqlString += " and hex(t.iccid) = '" + iccid + "' ";
		}
		if(null != typeArr && 0 < typeArr.length){
			StringBuilder typeCriteria = new StringBuilder();
			Boolean allowNull = false;
			for(String type : typeArr){
				if("null".equals(type)){
					allowNull = true;
					continue;
				}
				typeCriteria.append("'").append(type).append("',");
			}
			String typeCriteriaStr = StringUtils.EMPTY;
			if(0 < typeCriteria.length()){
				typeCriteriaStr = typeCriteria.substring(0, typeCriteria.length()-1);
				if(allowNull){
					sqlString += " and (c.type in (" + typeCriteriaStr + ") or c.type is null) ";
				}else{
					sqlString += " and c.type in (" + typeCriteriaStr + ") ";
				}
			}else if(allowNull){
				sqlString += " and c.type is null ";
			}
		}
		sqlString += " ) _x ";
		sqlString += " where 1 = 1 ";
		String minRemainValidDay = ObjectUtils.toString(paramMap.get("minRemainValidDay"));
		if (StringUtils.isNotBlank(minRemainValidDay)) {
			sqlString += " and _x.remainValidDay >= " + minRemainValidDay;
		}
		String maxRemainValidDay = ObjectUtils.toString(paramMap.get("maxRemainValidDay"));
		if (StringUtils.isNotBlank(maxRemainValidDay)) {
			sqlString += " and _x.remainValidDay <= " + maxRemainValidDay;
		}
		String minRemainCap = ObjectUtils.toString(paramMap.get("minRemainCap"));
		if (StringUtils.isNotBlank(minRemainCap)) {
			sqlString += " and _x.remainCap >= " + minRemainCap;
		}
		String maxRemainCap = ObjectUtils.toString(paramMap.get("maxRemainCap"));
		if (StringUtils.isNotBlank(maxRemainCap)) {
			sqlString += " and _x.remainCap <= " + maxRemainCap;
		}System.out.println(sqlString);
		return mifiTrafficDao.findBySql(page, sqlString, Map.class);
	}

	public Page<HashMap> findSimStatusList(Page<HashMap> page, Map<String, Object> paramMap) {
		String sqlString = "select id,hex(t.iccid) as iccid,t.UEID_MCC mcc,t.usimstatus,t.stamp_created,f.country_name_cn,f.country_name_en";
		sqlString += ",t.mcc as reg_mcc,t.nwstatus,convert(t.datacap/1024/1024,decimal) as data_cap,convert(t.dataused/1024/1024,decimal) as data_used,convert(t.dataadd/1024/1024,decimal) as data_add ";
		sqlString += " from simcardstatus t left join mcc_def f on (f.mcc = t.UEID_MCC) ";
		sqlString += " where 1 = 1 ";
		sqlString += " and hex(t.iccid) != '00000000000000000000' ";
		String iccid = ObjectUtils.toString(paramMap.get("iccid"));
		if (StringUtils.isNotBlank(iccid)) {
			sqlString += " and hex(t.iccid) = '" + iccid.trim() + "' ";
		}
		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		if (StringUtils.isNotBlank(beginDate)) {
			sqlString += " and t.stamp_created >= str_to_date('" + beginDate + "','%Y-%m-%d')";
		}
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(endDate)) {
			sqlString += " and t.stamp_created < date_add(str_to_date('" + endDate + "','%Y-%m-%d'),interval 1 day)";
		}
		sqlString += " order by hex(t.iccid),t.stamp_created desc ";
		return mifiTrafficDao.findBySql(page, sqlString, Map.class);
	}

	public Page<Object[]> exportSimStatusList(Page<Object[]> page, Map<String, Object> paramMap) {
		String sqlString = "select id,hex(t.iccid),t.usimstatus,CAST(t.stamp_created AS CHAR),t.UEID_MCC mcc,f.country_name_cn,f.country_name_en ";
		sqlString += ",t.mcc as reg_mcc, t.nwstatus, convert(t.datacap/1024/1024,decimal) as data_cap,convert(t.dataused/1024/1024,decimal) as data_used,convert(t.dataadd/1024/1024,decimal) as data_add ";
		sqlString += " from simcardstatus t left join mcc_def f on (f.mcc = t.UEID_MCC) ";
		sqlString += " where 1 = 1 ";
		sqlString += " and hex(t.iccid) != '00000000000000000000' ";
		String iccid = ObjectUtils.toString(paramMap.get("iccid"));
		if (StringUtils.isNotBlank(iccid)) {
			sqlString += " and hex(t.iccid) = '" + iccid + "' ";
		}
		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		if (StringUtils.isNotBlank(beginDate)) {
			sqlString += " and t.stamp_created >= str_to_date('" + beginDate + "','%Y-%m-%d')";
		}
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(endDate)) {
			sqlString += " and t.stamp_created < date_add(str_to_date('" + endDate + "','%Y-%m-%d'),interval 1 day)";
		}
		sqlString += " order by t.stamp_created desc ";
		return mifiTrafficDao.findBySql(page, sqlString);
	}

	public void statSimCardTrafficInfos() {

	}

	public Page<HashMap> findMifiStatusList(Page<HashMap> page, Map<String, Object> paramMap) {
		String sqlString = "select cast(t.imei as char) imei,t.source_type,t.UEID_MCC mcc,t.uestatus,t.stamp_created,f.country_name_cn,f.country_name_en ";
		sqlString += ",t.SIMBANKID sim_bank_id,t.SIMID sim_id,t.NWSTATUS nwstatus,CASE LEFT(RIGHT(hex(REGINFO),6),2) WHEN '0' THEN '无网络' WHEN '08' THEN '4G' ELSE '3G' END reg_info,"
				+ "t.POWERINFO power_info,t.REJCAUSE_9215 rej_cause_9215,hex(t.MCC_9215) mcc_9215,HEX(t.MNC_9215) mnc_9215,t.TAC_9215 tac_9215,t.CELLID_9215 callid_9215,t.RSSI_9215 rssi_9215";
		sqlString += ",hex(t.MCC_6200) mcc_6200,HEX(t.MNC_6200) mnc_6200,t.TAC_6200 tac_6200,t.CELLID_6200 cellid_6200,t.RSSI_6200 rssi_6200,t.DEVICES devices,convert(t.DATAINFO/1024/1024,decimal) datainfo";

		// 批次号
		String bath = ObjectUtils.toString(paramMap.get("bath"));
		if (StringUtils.isNotBlank(bath)) {
			sqlString += " from mifistatus t, mcc_def f, mifi_basic_info mbi where mbi.sn=t.imei and hex(t.MCC_9215) = f.mcc";
			sqlString += " and mbi.bath='" + bath + "'";
		} else
			sqlString += " FROM mifistatus t LEFT JOIN mcc_def f ON hex(t.MCC_9215) = f.mcc WHERE 1=1";
		
		//sqlString += " and hex(t.MCC_9215) != 0";
		
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			sqlString += " and t.source_type='" + sourceType + "'";
		}

		String imei = ObjectUtils.toString(paramMap.get("imei"));
		if (StringUtils.isNotBlank(imei)) {
			sqlString += " and t.imei = '" + imei.trim() + "' ";
		}

		String ueStatus = ObjectUtils.toString(paramMap.get("ueStatus"));
		if (StringUtils.isNotBlank(ueStatus)) {
			sqlString += " and t.uestatus = '" + ueStatus + "' ";
		}
		
		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		if (StringUtils.isNotBlank(beginDate)) {
			sqlString += " and t.stamp_created >= str_to_date('" + beginDate + "','%Y-%m-%d')";
		}
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(endDate)) {
			sqlString += " and t.stamp_created < date_add(str_to_date('" + endDate + "','%Y-%m-%d'),interval 1 day)";
		}
		
		// 国家
		String mcc = ObjectUtils.toString(paramMap.get("mcc"));
		if (StringUtils.isNotBlank(mcc)) {
			sqlString += " and f.mcc = '" + mcc + "' ";
		}
		
		sqlString += " order by t.imei,t.stamp_created desc, t.uestatus desc ";
		return mifiTrafficDao.findBySql(page, sqlString, Map.class);
	}

	/**
	 * 
	 * @Description 设备状态监控查询
	 * @param imei
	 * @param beginDate
	 * @param endDate
	 * @return List<Map<String,String>>  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午5:22:13
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> findMifiStatusList(String imei, String beginDate, String endDate) {
		String sqlString = "select id, cast(t.imei as char) imei,t.UEID_MCC mcc,t.uestatus ueStatus,DATE_FORMAT(t.stamp_created, '%Y-%m-%d %H:%i:%s') createDate, t.datainfo, t.devices, t.RSSI_9215, t.RSSI_6200, f.country_name_cn countryName,f.country_name_en countryNameEn";
		sqlString += " from mifistatus t, mcc_def f where t.UEID_MCC = f.mcc ";
		if (StringUtils.isNotBlank(imei)) {
			sqlString += " and t.imei = '" + imei + "' ";
		}

		if (StringUtils.isNotBlank(beginDate)) {
			sqlString += " and t.stamp_created >= str_to_date('" + beginDate + "','%Y-%m-%d')";
		}
		if (StringUtils.isNotBlank(endDate)) {
			sqlString += " and t.stamp_created < date_add(str_to_date('" + endDate + "','%Y-%m-%d'),interval 1 day)";
		}
		sqlString += " order by t.stamp_created asc ";
		return mifiTrafficDao.findBySql(sqlString, null, Map.class);
	}

	public Page<Object[]> exportMifiStatusList(Page<Object[]> page, Map<String, Object> paramMap) {
		String sqlString = "select t.imei,t.source_type,hex(t.MCC_9215) mcc,t.uestatus,t.stamp_created,f.country_name_cn,f.country_name_en ";
		sqlString += ",t.SIMBANKID sim_bank_id,t.SIMID sim_id,t.NWSTATUS nwstatus,t.POWERINFO power_info,t.REJCAUSE_9215 rej_cause_9215,hex(t.MCC_9215) mcc_9215,t.MNC_9215 mnc_9215,t.TAC_9215 tac_9215,t.CELLID_9215 callid_9215,t.RSSI_9215 rssi_9215";
		sqlString += ",hex(t.MCC_6200) mcc_6200,t.MNC_6200 mnc_6200,t.TAC_6200 tac_6200,t.CELLID_6200 cellid_6200,t.RSSI_6200 rssi_6200,t.DEVICES devices,convert(t.DATAINFO/1024/1024,decimal) datainfo";
		
		// 批次号
		String bath = ObjectUtils.toString(paramMap.get("bath"));
		if (StringUtils.isNotBlank(bath)) {
			sqlString += " from mifistatus t, mcc_def f, mifi_basic_info mbi where mbi.sn=t.imei and hex(t.MCC_9215) = f.mcc";
			sqlString += " and mbi.bath='" + bath + "'";
		} else
			sqlString += " FROM mifistatus t LEFT JOIN mcc_def f ON hex(t.MCC_9215) = f.mcc WHERE 1=1";
		
		//sqlString += " and hex(t.MCC_9215) != 0";
		
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			sqlString += " and t.source_type='" + sourceType + "'";
		}

		String imei = ObjectUtils.toString(paramMap.get("imei"));
		if (StringUtils.isNotBlank(imei)) {
			sqlString += " and t.imei = '" + imei + "' ";
		}

		String ueStatus = ObjectUtils.toString(paramMap.get("ueStatus"));
		if (StringUtils.isNotBlank(ueStatus)) {
			sqlString += " and t.uestatus = '" + ueStatus + "' ";
		}
		
		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		if (StringUtils.isNotBlank(beginDate)) {
			sqlString += " and t.stamp_created >= str_to_date('" + beginDate + "','%Y-%m-%d')";
		}
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(endDate)) {
			sqlString += " and t.stamp_created < date_add(str_to_date('" + endDate + "','%Y-%m-%d'),interval 1 day)";
		}
		
		// 国家
		String mcc = ObjectUtils.toString(paramMap.get("mcc"));
		if (StringUtils.isNotBlank(mcc)) {
			sqlString += " and f.mcc = '" + mcc + "' ";
		}
		
		sqlString += " order by t.imei,t.stamp_created desc ";
		return mifiTrafficDao.findBySql(page, sqlString);
	}

	public Page<Object[]> exportMifiStatus4List(Page<Object[]> page, Map<String, Object> paramMap) {
		StringBuffer sql = new StringBuffer("SELECT * FROM(SELECT t1.imei,t1.source_type,t2.out_order_id,t1.uestatus,t1.stamp_created,t1.sim_bank_id,t1.sim_id,t1.nwstatus,t1.power_info,t1.rej_cause_9215,t1.mcc_9215,t1.mnc_9215,t1.tac_9215,t1.callid_9215,t1.rssi_9215,t1.mcc_6200,t1.mnc_6200,t1.tac_6200,t1.cellid_6200,t1.rssi_6200,t1.devices,t1.bath,t1.mcces,t1.card_source_type FROM(");
		sql.append("select cast(t.imei AS CHAR) imei,t.source_type,t.card_source_type,t.uestatus,t.stamp_created ");
		sql.append(",t.SIMBANKID sim_bank_id,t.SIMID sim_id,t.NWSTATUS nwstatus,t.POWERINFO power_info,t.REJCAUSE_9215 rej_cause_9215,hex(t.MCC_9215) mcc_9215,t.MNC_9215 mnc_9215,t.TAC_9215 tac_9215,t.CELLID_9215 callid_9215,t.RSSI_9215 rssi_9215");
		sql.append(",hex(t.MCC_6200) mcc_6200,t.MNC_6200 mnc_6200,t.TAC_6200 tac_6200,t.CELLID_6200 cellid_6200,t.RSSI_6200 rssi_6200,max(t.DEVICES) devices,mbi.bath,group_concat(distinct hex(t.MCC_9215)) mcces");
		sql.append(" from mifistatus t ");
		sql.append(",mifi_basic_info mbi where mbi.sn=t.imei ");
		
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			sql.append("and t.source_type='" + sourceType + "'");
		}

		// 批次号
		String bath = ObjectUtils.toString(paramMap.get("bath"));
		if (StringUtils.isNotBlank(bath)) {
			sql.append("and mbi.bath='" + bath + "'");
		}

		String imei = ObjectUtils.toString(paramMap.get("imei"));
		if (StringUtils.isNotBlank(imei)) {
			sql.append(" and t.imei = '" + imei + "' ");
		}

		// 只导出状态为4的记录
		sql.append(" and t.uestatus = '4' AND t.SIMBANKID!=0 AND t.SIMID!=0 ");
		
		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(beginDate)) {
			sql.append(" and t.stamp_created >= str_to_date('" + beginDate + "','%Y-%m-%d')");
		}
		if (StringUtils.isNotBlank(endDate)) {
			sql.append(" and t.stamp_created < date_add(str_to_date('" + endDate + "','%Y-%m-%d'),interval 1 day)");
		}
		
		sql.append("and hex(t.MCC_9215) != 0");
		
		sql.append(" GROUP BY t.imei, t.card_source_type, DATE_FORMAT(t.stamp_created, '%Y-%m-%d')) t1");
		
		sql.append(" LEFT JOIN (SELECT o.out_order_id, o.start_date, o.end_date, od.dsn");
		sql.append(" FROM mifi_order o, mifi_order_detail od WHERE o.order_id = od.order_id AND o.order_status<9");
		if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate))
			sql.append(" AND (");
			sql.append(" o.start_date BETWEEN STR_TO_DATE('" + beginDate + "', '%Y-%m-%d') AND date_add(STR_TO_DATE('" + endDate + "', '%Y-%m-%d'),INTERVAL 1 DAY)");
			sql.append(" OR o.end_date BETWEEN STR_TO_DATE('" + beginDate + "', '%Y-%m-%d') AND date_add(STR_TO_DATE('" + endDate + "', '%Y-%m-%d'),INTERVAL 1 DAY)");
			sql.append(" OR (o.start_date <= STR_TO_DATE('" + beginDate + "', '%Y-%m-%d') AND o.end_date >= date_add(STR_TO_DATE('" + endDate + "', '%Y-%m-%d'),INTERVAL 1 DAY))");
			sql.append(" )");
		if (StringUtils.isNotBlank(beginDate) && StringUtils.isBlank(endDate))
			sql.append(" AND STR_TO_DATE('" + beginDate + "', '%Y-%m-%d') BETWEEN o.start_date AND o.end_date");
		if (StringUtils.isBlank(beginDate) && StringUtils.isNotBlank(endDate))
			sql.append(" AND date_add(str_to_date('" + endDate + "', '%Y-%m-%d'), INTERVAL 1 DAY) BETWEEN o.start_date AND o.end_date");
		sql.append(")t2 ON t1.imei = t2.dsn and t1.stamp_created BETWEEN t2.start_date and t2.end_date) tt GROUP BY tt.imei, tt.card_source_type, DATE_FORMAT(tt.stamp_created, '%Y-%m-%d')");
		return mifiTrafficDao.findBySql(page, sql.toString());
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> segmentedMifiStatusList(Map<String, Object> paramMap) {
		StringBuffer sql = new StringBuffer("SELECT * FROM(SELECT t1.imei,t1.source_type,t2.out_order_id,t1.uestatus,t1.stamp_created,t1.sim_bank_id,t1.sim_id,t1.nwstatus,t1.power_info,t1.rej_cause_9215,t1.mcc_9215,t1.mnc_9215,t1.tac_9215,t1.callid_9215,t1.rssi_9215,t1.mcc_6200,t1.mnc_6200,t1.tac_6200,t1.cellid_6200,t1.rssi_6200,t1.devices,t1.bath,t1.mcces,t1.card_source_type FROM(");
		sql.append("select cast(t.imei AS CHAR) imei,t.source_type,t.card_source_type,t.uestatus,t.stamp_created ");
		sql.append(",t.SIMBANKID sim_bank_id,t.SIMID sim_id,t.NWSTATUS nwstatus,t.POWERINFO power_info,t.REJCAUSE_9215 rej_cause_9215,hex(t.MCC_9215) mcc_9215,t.MNC_9215 mnc_9215,t.TAC_9215 tac_9215,t.CELLID_9215 callid_9215,t.RSSI_9215 rssi_9215");
		sql.append(",hex(t.MCC_6200) mcc_6200,t.MNC_6200 mnc_6200,t.TAC_6200 tac_6200,t.CELLID_6200 cellid_6200,t.RSSI_6200 rssi_6200,max(t.DEVICES) devices,mbi.bath,group_concat(distinct hex(t.MCC_9215)) mcces");
		sql.append(" from mifistatus t ");
		sql.append(",mifi_basic_info mbi where mbi.sn=t.imei ");
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			sql.append("and t.source_type='" + sourceType + "'");
		}
		String sourceTypeIn = ObjectUtils.toString(paramMap.get("sourceTypeIn"));
		if (StringUtils.isNotBlank(sourceTypeIn)) {
			sql.append("and t.source_type in (" + sourceTypeIn + ")");
		}
		// 批次号
		String bath = ObjectUtils.toString(paramMap.get("bath"));
		if (StringUtils.isNotBlank(bath)) {
			sql.append("and mbi.bath='" + bath + "'");
		}
		String imei = ObjectUtils.toString(paramMap.get("imei"));
		if (StringUtils.isNotBlank(imei)) {
			sql.append(" and t.imei = '" + imei + "' ");
		}
		// 只导出状态为4的记录
		sql.append(" and t.uestatus = '4' AND t.SIMBANKID!=0 AND t.SIMID!=0 ");
		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(beginDate)) {
			sql.append(" and t.stamp_created >= str_to_date('" + beginDate + "','%Y-%m-%d')");
		}
		if (StringUtils.isNotBlank(endDate)) {
			sql.append(" and t.stamp_created < date_add(str_to_date('" + endDate + "','%Y-%m-%d'),interval 1 day)");
		}
		sql.append("and hex(t.MCC_9215) != 0");
		sql.append(" GROUP BY t.imei, t.card_source_type, DATE_FORMAT(t.stamp_created, '%Y-%m-%d')) t1");
		sql.append(" LEFT JOIN (SELECT o.out_order_id, o.start_date, o.end_date, od.dsn");
		sql.append(" FROM mifi_order o, mifi_order_detail od WHERE o.order_id = od.order_id AND o.order_status<9");
		if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate))
			sql.append(" AND (");
			sql.append(" o.start_date BETWEEN STR_TO_DATE('" + beginDate + "', '%Y-%m-%d') AND date_add(STR_TO_DATE('" + endDate + "', '%Y-%m-%d'),INTERVAL 1 DAY)");
			sql.append(" OR o.end_date BETWEEN STR_TO_DATE('" + beginDate + "', '%Y-%m-%d') AND date_add(STR_TO_DATE('" + endDate + "', '%Y-%m-%d'),INTERVAL 1 DAY)");
			sql.append(" OR (o.start_date <= STR_TO_DATE('" + beginDate + "', '%Y-%m-%d') AND o.end_date >= date_add(STR_TO_DATE('" + endDate + "', '%Y-%m-%d'),INTERVAL 1 DAY))");
			sql.append(" )");
		if (StringUtils.isNotBlank(beginDate) && StringUtils.isBlank(endDate))
			sql.append(" AND STR_TO_DATE('" + beginDate + "', '%Y-%m-%d') BETWEEN o.start_date AND o.end_date");
		if (StringUtils.isBlank(beginDate) && StringUtils.isNotBlank(endDate))
			sql.append(" AND date_add(str_to_date('" + endDate + "', '%Y-%m-%d'), INTERVAL 1 DAY) BETWEEN o.start_date AND o.end_date");
		sql.append(")t2 ON t1.imei = t2.dsn and t1.stamp_created BETWEEN t2.start_date and t2.end_date) tt GROUP BY tt.imei, tt.card_source_type, DATE_FORMAT(tt.stamp_created, '%Y-%m-%d')");
		return mifiTrafficDao.findBySql(sql.toString());
	}
	
	/**
	 * 
	 * @Description 取设备使用的流量
	 * @param imei
	 * @param beginDate
	 * @param endDate
	 * @return Long  
	 * @author yifang.huang
	 * @date 2016年5月23日 下午4:56:12
	 */
	public String getMaxDataInfo(String imei, String beginDate, String endDate) {

		if (StringUtils.isBlank(imei)) 
			return "0";
		
		StringBuffer sb = new StringBuffer("select format(max(datainfo)/1024/1024, 2) datainfo from mifistatus where uestatus=4");
		sb.append(" and imei='" + imei + "'");
		
		if (StringUtils.isNotBlank(beginDate)) {
			sb.append(" and stamp_created>=str_to_date('" + beginDate + "','%Y-%m-%d')");
		}
		if (StringUtils.isNotBlank(endDate)) {
			sb.append(" and stamp_created<date_add(str_to_date('" + endDate + "','%Y-%m-%d'),interval 1 day)");
		}

		List<Map<String, String>> list = mifiTrafficDao.findBySql(sb.toString(), null, Map.class);
		if (list!=null && list.size()>0) {
			Map<String, String> map = list.get(0);
			String datainfo = ObjectUtils.toString(map.get("datainfo"));
			return String.valueOf(datainfo);
		}
		return "0";
	}
	
	/**
	 * 
	 * @Description 设备状态检测
	 * @param statusList
	 * @param stampEq0
	 * @param stampGt0
	 * @throws ParseException 
	 * @return Map<String,Object>  
	 * @author yifang.huang
	 * @date 2016年5月23日 下午4:01:12
	 */
	public List<DeviceMonitorDetail> checkDeviceStatus(List<Map<String, String>> statusList, int stampEq0, int stampGt0) throws ParseException {

		List<DeviceMonitorDetail> dmdList = new ArrayList<DeviceMonitorDetail>();
		
		Map<String, String> tempMap = null;
		DeviceMonitorDetail detail = null;
		for (Map<String, String> statusMap : statusList) {
			if (tempMap != null) {
				String preStatus = String.valueOf(tempMap.get("ueStatus"));
				String nextStatus = String.valueOf(statusMap.get("ueStatus"));
				// 如果当前记录状态是6
				if ("6".equals(nextStatus)) {			// 关机
					tempMap = null;
					continue;
				}
				// 如果上一条记录状态是6
				if ("6".equals(preStatus)) {			// 关机
					tempMap = statusMap;
					continue;
				}
				if (!"4".equals(preStatus) && !"6".equals(preStatus)) {
					if ("4".equals(nextStatus)) {
						if (timeOut(tempMap.get("createDate"), statusMap.get("createDate"), stampEq0)) {
							detail = new DeviceMonitorDetail();
							detail.setPreId(String.valueOf(tempMap.get("id")));
							detail.setPreStatus(preStatus+"|"+DictUtils.getDictLabel(preStatus, "mifi_uestatus", null) + "[" + ObjectUtils.toString(tempMap.get("RSSI_9215")) + "," + ObjectUtils.toString(tempMap.get("RSSI_6200")) + "]");
							detail.setPreHappenDate(df.parse(tempMap.get("createDate")));
							detail.setNextId(String.valueOf(statusMap.get("id")));
							detail.setNextStatus(nextStatus+"|"+DictUtils.getDictLabel(nextStatus, "mifi_uestatus", null) + "[" + ObjectUtils.toString(statusMap.get("RSSI_9215")) + "," + ObjectUtils.toString(statusMap.get("RSSI_6200")) + "]");
							detail.setNextHappenDate(df.parse(statusMap.get("createDate")));
							detail.setCountryName(statusMap.get("countryName"));
							detail.setCountryNameEn(statusMap.get("countryNameEn"));
							dmdList.add(detail);
							detail = null;
						}
						tempMap = statusMap;
						continue;
					}
					continue;
				}
				if ("4".equals(preStatus)) {
					if (timeOut(tempMap.get("createDate"), statusMap.get("createDate"), stampGt0)) {
						detail = new DeviceMonitorDetail();
						detail.setPreId(String.valueOf(tempMap.get("id")));
						detail.setPreStatus(preStatus+"|"+DictUtils.getDictLabel(preStatus, "mifi_uestatus", null) + "[" + ObjectUtils.toString(tempMap.get("RSSI_9215")) + "," + ObjectUtils.toString(tempMap.get("RSSI_6200")) + "]");
						detail.setPreHappenDate(df.parse(tempMap.get("createDate")));
						detail.setNextId(String.valueOf(statusMap.get("id")));
						detail.setNextStatus(nextStatus+"|"+DictUtils.getDictLabel(nextStatus, "mifi_uestatus", null) + "[" + ObjectUtils.toString(statusMap.get("RSSI_9215")) + "," + ObjectUtils.toString(statusMap.get("RSSI_6200")) + "]");
						detail.setNextHappenDate(df.parse(statusMap.get("createDate")));
						detail.setCountryName(statusMap.get("countryName"));
						detail.setCountryNameEn(statusMap.get("countryNameEn"));
						dmdList.add(detail);
						detail = null;
					}
					if ("6".equals(nextStatus))
						tempMap = null;
					else 
						tempMap = statusMap;
					continue;
				}
			} else {
				tempMap = statusMap;
			}
		}

		return dmdList;
	}
	
	/**
	 * 
	 * @Description 设备状态统计
	 * @param statusList
	 * @param stampGt0
	 * @throws ParseException 
	 * @return Map<String,Object>  
	 * @author yifang.huang
	 * @date 2016年5月23日 下午4:07:13
	 */
	public Map<String, Object> countDeviceStatus(List<Map<String, String>> statusList, int stampGt0) throws ParseException {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		Map<String, String> tempMap = null;
		Set<String> country = new HashSet<String>();
		List<String> shutDownTime = new ArrayList<String>();
		long onlineTime = 0;
		long dataTotal = 0;
		int devices = 0;
		for (Map<String, String> statusMap : statusList) {
			// 取最大连接设备数
			int tempDevices = Integer.valueOf(ObjectUtils.toString(statusMap.get("devices")));
			if (devices < tempDevices)
				devices = tempDevices;
			// 保存国家信息
			country.add(statusMap.get("countryNameEn"));
			if (tempMap != null) {
				String preStatus = String.valueOf(tempMap.get("ueStatus"));
				String nextStatus = String.valueOf(statusMap.get("ueStatus"));
				// 保存关机时间
				if ("6".equals(preStatus)) {
					shutDownTime.add(tempMap.get("createDate"));
				}
				if ("4".equals(preStatus) && "4".equals(nextStatus)) {
					if (!timeOut(tempMap.get("createDate"), statusMap.get("createDate"), stampGt0)) {
						// 两个状态为4的记录间隔时间不大于stampGt0，则记录时间差
						onlineTime += (df.parse(statusMap.get("createDate")).getTime() - df.parse(tempMap.get("createDate")).getTime());
					}
					long preDate = Long.valueOf(ObjectUtils.toString(tempMap.get("datainfo")));
					long nextDate = Long.valueOf(ObjectUtils.toString(statusMap.get("datainfo")));
					if (nextDate > preDate) 
						dataTotal += (nextDate - preDate);
				}
			}
			tempMap = statusMap;
		}
		// 国家信息处理
		String countryStr = country.size()==0 ? null : country.toString();
		if (StringUtils.isNotBlank(countryStr))
			countryStr = countryStr.replace("[", "").replace("]", "").replace(",", "<br />");
		resultMap.put("country", countryStr);
		// 关机时间处理
		String shutDownTimeStr = shutDownTime.size()==0 ? null : shutDownTime.toString();
		if (StringUtils.isNotBlank(shutDownTimeStr))
			shutDownTimeStr = shutDownTimeStr.replace("[", "").replace("]", "").replace(",", "<br />");
		resultMap.put("shutDownTime", shutDownTimeStr);
		// 在线时长（小时、分钟）
		long min = onlineTime/60000;
		String onlineTimeStr = min/60 + "小时" + min%60 + "分钟";
		resultMap.put("onlineTime", onlineTimeStr);
		// 流量（M）
		resultMap.put("dataTotal", dataTotal/1024/1024);
		// 最大连接设备数
		resultMap.put("devices", devices);
		

		return resultMap;
	}
		
	/**
	 * 
	 * @Description 判断时间是否超时
	 * @param startDate
	 * @param endDate
	 * @param stamp
	 * @return boolean  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午6:09:57
	 */
	private boolean timeOut(String startDate, String endDate, int stamp) {
		try {
			long timeCount = df.parse(endDate).getTime() - df.parse(startDate).getTime();
			return (timeCount / 60000) > stamp ? true : false;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 *  根据iccid和status查找
	 * @athor shuxin
	 * @date 2016年6月23日下午3:58:14
	 * @param iccid 卡号
	 * @param status 
	 * @return
	 * Map<String,Object> 
	 */
	@SuppressWarnings("unchecked")
	public  List<Map<String, Object>> findSimcardStatusByIccidAndUsimtatus(String iccid, Integer status){
		StringBuffer buffer = new StringBuffer("SELECT t.iccid as iccid,t.stamp_created AS firstUseTime FROM");
		buffer.append(" (SELECT HEX(s.ICCID) AS iccid,s.stamp_created FROM simcardstatus s WHERE HEX(s.ICCID) IN (");
		buffer.append(iccid);
		buffer.append(")");
		buffer.append(" AND s.USIMSTATUS =");
		buffer.append(status);
		buffer.append(" ORDER BY s.stamp_created ASC) t GROUP BY t.ICCID");
		return mifiTrafficDao.findBySql(buffer.toString(), null, Map.class);
	}
	
	/**
	 * 根据imei查询
	 * @athor shuxin
	 * @date 2016年7月15日上午11:49:38
	 * @param imei
	 * @return
	 * List<Map<String,Object>> 
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findSimCardStatusByImei(String imei){
		StringBuffer buffer =new StringBuffer("SELECT SIMBANKID,SIMID from mifistatus t");
		buffer.append(" where 1=1 ");
		buffer.append(" and t.IMEI = '").append(imei).append("'");
		buffer.append(" and t.SIMBANKID != 0 and t.SIMID != 0  ");
		buffer.append(" ORDER BY t.stamp_created DESC limit 0,1");
		return mifiTrafficDao.findBySql(buffer.toString(), null, Map.class);
	}
	
	/**
	 * 根据设备编号查找设备基站信息
	 * @athor shuxin
	 * @date 2016年7月18日上午11:46:31
	 * @param imei
	 * @return
	 * Map<String,Object> 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> findBaseStationInfoByImei(String imei){
		StringBuffer buffer =new StringBuffer("SELECT MCC_6200,MNC_6200,CELLID_6200,TAC_6200 from mifistatus t");
		buffer.append(" where 1=1");
		buffer.append(" and t.IMEI = '").append(imei).append("'");
		buffer.append(" and t.UESTATUS = 4");
		buffer.append(" ORDER BY t.stamp_created DESC limit 0,1");
		List<Map<String, Object>> list = mifiTrafficDao.findBySql(buffer.toString(), null, Map.class);
		if(list.isEmpty()){
			return new HashMap<String, Object>();
		}
		return list.get(0);
	}
	
	/**
	 * 根据设备iccid和status查找simblankID和simid
	 * @athor shuxin
	 * @date 2016年7月18日下午5:18:11
	 * @param iccid
	 * @param status
	 * @return
	 * Map<String,Object> 
	 */
	@SuppressWarnings("unchecked")
	public 	Map<String, Integer> findSimBlankIdAndSimIDByIccidAndUsimtatus(String iccid, Integer status){
		if(!StringUtils.isNotBlank(iccid) && status == null){
			return new HashMap<String, Integer>();
		}
		StringBuffer buffer = new StringBuffer("SELECT SIMBANKID as simBankId,SIMID as simId FROM simnode");
		buffer.append(" where  HEX(ICCID) ='");
		buffer.append(iccid);
		buffer.append("'");
		buffer.append(" AND USIMSTATUS =");
		buffer.append(status);
		buffer.append(" ORDER BY stamp_created desc");
		buffer.append(" limit 0,1");
		List<Map<String, Integer>> map = mifiTrafficDao.findBySql(buffer.toString(), null, Map.class);
		if(map.isEmpty()){
			return new HashMap<String, Integer>();
		}
		return map.get(0);
	}
	
	/**
	 * 
	 * @Description 根据mcc获取国家名称
	 * @param mcc
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年9月5日 上午11:58:42
	 */
	@SuppressWarnings("unchecked")
	public String getCountryNameByMcc(String mcc) {
		if (StringUtils.isBlank(mcc))
			return null;
		
		List<HashMap<String, String>> list = mifiTrafficDao.findBySql("select country_name_cn countryName from mcc_def where mcc='" + mcc + "'", null, Map.class);
		if (list!=null && list.size()>0) {
			return list.get(0).get("countryName");
		}
		
		return null;
	}
	/**
	 * 卡类型流量统计查询
	 * @Description 
	 * @return Page<HashMap>  
	 * @author wangsai
	 * @date 2016年12月6日 下午2:41:51
	 */
		public Page<HashMap> findSimCardTrafficType(Page<HashMap> page, Map<String, Object> paramMap) {
			String sqlString = "SELECT 	simcard_.imsi IMSI, 	simcard_.iccid ICCID, 	simcard_.dataused FROM(";
			sqlString += "SELECT hex(a.imsi)AS imsi, hex(a.iccid)AS iccid, format(sum(a.dataadd) / 1024 / 1024, 2)AS dataused FROM 	simcardstatus a, card_basic_info b 	WHERE 	hex(a.ICCID)= b.sn 	";
			String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
			if (StringUtils.isNotBlank(beginDate)) {
				sqlString += " AND a.stamp_created >= str_to_date('" + beginDate + "','%Y-%m-%d %H:%i:%s')";
			}
			String endDate = ObjectUtils.toString(paramMap.get("endDate"));
			if (StringUtils.isNotBlank(endDate)) {
				sqlString += " and a.stamp_created < str_to_date('" + endDate + "','%Y-%m-%d %H:%i:%s')";
			}
			String type = ObjectUtils.toString(paramMap.get("type"));
			if (StringUtils.isNotBlank(type)) {
				sqlString += " and  b.type = '" + type + "' ";
			}
			sqlString += " GROUP BY hex(a.imsi), hex(a.iccid))AS simcard_ ";
			sqlString += " ORDER BY iccid DESC ";
			return mifiTrafficDao.findBySql(page, sqlString, Map.class);
		}
		/**
		 *导出卡类型流量统计查询
		 * @Description 
		 * @author wangsai
		 * @throws ParseException 
		 * @date 2016年12月7日 上午10:20:12
		 */
		public List<SimCardType> findMifiDeviceListForExport(Map<String, Object> paramMap) throws ParseException {
			List<SimCardType> list = null;
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT simcard_.imsi IMSI, simcard_.iccid ICCID, simcard_.dataused FROM("
					+ "SELECT hex(a.imsi)AS imsi, hex(a.iccid)AS iccid, format(sum(a.dataadd) / 1024 / 1024, 2)AS dataused FROM 	simcardstatus a, card_basic_info b 	WHERE 	hex(a.ICCID)= b.sn 	");
			String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
			if (StringUtils.isNotBlank(beginDate)) {
				sb.append( " AND a.stamp_created >= str_to_date('" + beginDate + "','%Y-%m-%d %H:%i:%s')");
			}
			String endDate = ObjectUtils.toString(paramMap.get("endDate"));
			if (StringUtils.isNotBlank(endDate)) {
				sb.append(" and a.stamp_created < str_to_date('" + endDate + "','%Y-%m-%d %H:%i:%s')");
			}
			String type = ObjectUtils.toString(paramMap.get("type"));
			if (StringUtils.isNotBlank(type)) {
				sb.append( " and  b.type = '" + type + "' ");
			}
			sb.append( " GROUP BY hex(a.imsi), hex(a.iccid))AS simcard_ ");
			sb.append(" ORDER BY iccid DESC ");
			List<String[]> listObjArr = mifiTrafficDao.findBySql(sb.toString());
			if (listObjArr!=null && listObjArr.size()>0) {
				list = new ArrayList<SimCardType>();
				SimCardType info = null;
				for (Object[] objs : listObjArr) {
					info = new SimCardType();
					info.setCardTypeName(ObjectUtils.toString(objs[1]));
					String  str=ObjectUtils.toString(objs[2]);
					info.setApnInfo(str.toString());
					list.add(info);
					info = null;
				}
			}
			return list;
		}
		
}
