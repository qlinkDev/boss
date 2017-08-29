package main.java.com.qlink.modules.mifi.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.modules.mifi.entity.MifiBasicInfo;

/**
 * 
 * @author wangyong
 * @date 2016年2月17日
 */
@Repository
public class MifiManageDao extends BaseDao<MifiBasicInfo> {

	public static Logger logger = LoggerFactory.getLogger(MifiManageDao.class);
	
	/**
	 * 查询设备量
	 * @param sn
	 * @return
	 */
	public long getMifiDeviceCntBySn(String sn){
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("sn", sn));
		return count(dc);
	}
	
	/**
	 * 查询设备信息
	 * @param sn
	 * @return
	 */
	public MifiBasicInfo getMifiBasicInfoBySn(String sn){
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("sn", sn));
		List<MifiBasicInfo> lists = find(dc);
		return lists.size() > 0 ? lists.get(0) : null;
	}
	
	/**
	 * 查询设备信息
	 * @param sourceType
	 * @return
	 */
	public List<MifiBasicInfo> findMifiBasicInfoList(String sourceType){
		DetachedCriteria dc = createDetachedCriteria();
		if (StringUtils.isNotBlank(sourceType))
			dc.add(Restrictions.eq("sourceType", sourceType));
		return find(dc);
	}
	
	/**
	 * 查询渠道信息
	 * @param sourceType
	 * @return
	 */
	public HashMap getChannelInfo(String sourceType){
		StringBuffer sb = new StringBuffer();
		sb.append("select mcces from om_channel where channel_name_en = '" + sourceType + "' and del_flag = '0' ");
		List<HashMap> lists = findBySql(sb.toString(), null, Map.class);
		return lists.size() > 0 ? lists.get(0) : null;
	}
	
	/**
	 * 查询Mifilist
	 * @param sn
	 * @return
	 */
	public HashMap<String, String> getMifilistBySn(String sn){
		String sql = "select source_type sourceType, owner_type ownerType, ssid, owner_mcc ownerMcc, use_flag useFlag, cast(VFICCID as char) vfIccId from mifilist where IMEI_6200='" + sn + "'";
		List<HashMap<String, String>> lists = findBySql(sql, null, Map.class);
		return lists.size() > 0 ? lists.get(0) : null;
	}
	
	/**
	 * 
	 * @Description 查询mifilist：TEST_IP,TEST_UPDATE_IP,SOFTSIM_TYPE,web_portal_flag
	 * @param sn
	 * @return Map<String,Object>  
	 * @author yifang.huang
	 * @date 2017年3月13日 上午10:27:59
	 */
	public Map<String, Object> getMifiInfoBySn(String sn) {
		String sql = "select TEST_IP as testIp, TEST_UPDATE_IP as testUpdateIp, SOFTSIM_TYPE as softsimType, web_portal_flag as webPortalFlag from mifilist where imei_6200 = "+sn;
		List<Map<String, Object>> lists = findBySql(sql, null, Map.class);
		return lists.size() > 0 ? lists.get(0) : null;
	}
	
	/**
	 * 修改Mifilist的UeAllowed
	 * @param flag
	 * @param sn
	 * @return
	 */
	public void updateMifilistUeAllowed(String flag, String sn) {
		String sql = "update mifilist t set UEALLOWED=" + flag + " where IMEI_6200='" + sn + "'";
		updateBySql(sql, null);
	}
	
	/**
	 * 修改Mifilist的UEALLOWEDMCC
	 * @param mccs
	 * @param sn
	 * @return
	 */
	public void updateMifilistUeAllowedMcc(String mccs, String sn) {
		String sql = "update mifilist set UEALLOWEDMCC='" + mccs + "' where IMEI_6200='" + sn + "'";
		updateBySql(sql, null);
	}
	
	/**
	 * 修改Mifilist的OWNER_MCC
	 * @param mccs
	 * @param sn
	 * @return
	 */
	public void updateMifilistOwnerMcc(String mccs, String sn) {
		String sql = "update mifilist set OWNER_MCC='" + mccs + "' where IMEI_6200='" + sn + "'";
		updateBySql(sql, null);
	}
	
	/**
	 * 修改Mifilist的use_flag
	 * @param mccs
	 * @param sn
	 * @return
	 */
	public void updateMifilistUseFlag(String useFlag, String sn) {
		String sql = "update mifilist set use_flag='" + useFlag + "' where IMEI_6200='" + sn + "'";
		updateBySql(sql, null);
	}
	
	/**
	 * 修改mifiversion的speedlimit_type
	 * @param speedlimit_type
	 * @param sn
	 * @return
	 */
	public void updateMifiVersionLimitSpeedFlag(String limitSpeedFlag, String sn) {
		String sql = "update mifiversion set speedlimit_type='" + limitSpeedFlag + "' where IMEI_6200='" + sn + "'";
		updateBySql(sql, null);
	}
	
	/**
	 * 修改mifiversion的speedlimit_type
	 * @param speedlimit_type
	 * @param sns
	 * @return
	 */
	public void updateMifiVersionsLimitSpeedFlag(String limitSpeedFlag, String sns) {
		String sql = "update mifiversion set speedlimit_type='" + limitSpeedFlag + "' where IMEI_6200 in(" + sns + ")";
		updateBySql(sql, null);
	}
	
	/**
	 * 
	 * @Description 修改Mifilist的UeAllowed,UEALLOWEDMCC
	 * @param ueAllowed
	 * @param ueAllowedMcc
	 * @param sn 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年8月26日 下午2:21:17
	 */
	public void updateMifilist(String ueAllowed, String ueAllowedMcc, String sn) {
		String sql = "update mifilist set UEALLOWED=" + ueAllowed + ", UEALLOWEDMCC='" + ueAllowedMcc + "' where IMEI_6200='" + sn + "'";
		updateBySql(sql, null);
	}
	
	/**
	 * 
	 * @Description 修改Mifilist的UeAllowed,UEALLOWEDMCC,OWNER_MCC
	 * @param ueAllowed
	 * @param ueAllowedMcc
	 * @param ownerMcc
	 * @param sn 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年8月26日 下午2:21:17
	 */
	public void updateMifilist(String ueAllowed, String ueAllowedMcc, String ownerMcc, String sn) {
		String sql = "update mifilist set UEALLOWED=" + ueAllowed + ", UEALLOWEDMCC='" + ueAllowedMcc + "', OWNER_MCC='" + ownerMcc + "' where IMEI_6200='" + sn + "'";
		updateBySql(sql, null);
	}
	
	/**
	 * 
	 * @Description 根据渠道商和订单开始结束时间取可以购买的设备
	 * @param sourceType
	 * @param startDate
	 * @param endDate
	 * @return List<Map<String,String>>  
	 * @author yifang.huang
	 * @date 2016年4月29日 下午5:40:22
	 */
	public List<Map<String, String>> getCanBuyDevice(String sourceType, String startDate, String endDate) {
		
		if (StringUtils.isBlank(sourceType))
			return null;
		
		StringBuffer sql = new StringBuffer("SELECT cast(IMEI_6200 as char) as imei FROM mifilist WHERE source_type='"+sourceType+"' AND IMEI_6200 NOT IN"
				+ "(SELECT od.dsn FROM mifi_order o, mifi_order_detail od WHERE o.order_id=od.order_id"
				+ " AND o.source_type='"+sourceType+"' AND o.order_status='1'");
		if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate))
			sql.append(" AND (start_date BETWEEN STR_TO_DATE('"+startDate+"','%Y-%m-%d %H:%i:%s') AND STR_TO_DATE('"+endDate+"','%Y-%m-%d %H:%i:%s') "
				+ "OR end_date BETWEEN STR_TO_DATE('"+startDate+"','%Y-%m-%d %H:%i:%s') AND STR_TO_DATE('"+endDate+"','%Y-%m-%d %H:%i:%s') "
				+ "OR (start_date<=STR_TO_DATE('"+startDate+"','%Y-%m-%d %H:%i:%s') AND end_date>=STR_TO_DATE('"+endDate+"','%Y-%m-%d %H:%i:%s')))");
		sql.append(")");
		
		return findBySql(sql.toString(), null, Map.class);
	}
	
	/**
	 * 
	 * @Description 判断设备是否可以下单
	 * @param imei
	 * @param sourceType
	 * @param startDate
	 * @param endDate
	 * @return boolean  
	 * @author yifang.huang
	 * @date 2016年12月10日 下午2:33:35
	 */
	public boolean checkCanBuyDevice(String imei, String sourceType, String startDate, String endDate) {
		
		if (StringUtils.isBlank(sourceType) || StringUtils.isBlank(imei))
			return false;
		
		StringBuffer sql = new StringBuffer("SELECT cast(IMEI_6200 as char) as imei FROM mifilist WHERE source_type='"+sourceType+"'"
				+ " AND IMEI_6200='" + imei + "'"
				+ " AND IMEI_6200 NOT IN"
				+ "(SELECT od.dsn FROM mifi_order o, mifi_order_detail od WHERE o.order_id=od.order_id"
				+ " AND o.source_type='"+sourceType+"' AND o.order_status='1'");
		if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate))
			sql.append(" AND (start_date BETWEEN STR_TO_DATE('"+startDate+"','%Y-%m-%d %H:%i:%s') AND STR_TO_DATE('"+endDate+"','%Y-%m-%d %H:%i:%s') "
				+ "OR end_date BETWEEN STR_TO_DATE('"+startDate+"','%Y-%m-%d %H:%i:%s') AND STR_TO_DATE('"+endDate+"','%Y-%m-%d %H:%i:%s') "
				+ "OR (start_date<=STR_TO_DATE('"+startDate+"','%Y-%m-%d %H:%i:%s') AND end_date>=STR_TO_DATE('"+endDate+"','%Y-%m-%d %H:%i:%s')))");
		sql.append(")");
		
		List<Map<String, String>> result = findBySql(sql.toString(), null, Map.class);
		if (result!=null && result.size()>0)
			return true;
		
		return false;
	}
	
	/**
	 * 
	 * @Description 统计设备流量(从simcardstatus表统计设备使用流量)
	 * @param ueid 设备对应UEID后5位
	 * @param cardSourceType 卡所属渠道
	 * @param startDate 开始时间
	 * @param endDate 结束时间
	 * @return Long
	 * @author yifang.huang
	 * @date 2016年10月12日 上午11:17:43
	 */
	public Long getDeviceFlow(String ueid, String cardSourceType, String startDate, String endDate) {
		
		if (StringUtils.isBlank(ueid))
			return 0l;
		
		StringBuffer sql = new StringBuffer("SELECT convert(sum(a.DATAADD)/1024/1024, decimal(10, 0)) as dataInfo FROM simcardstatus a WHERE a.UEID_CURRNUM='" + ueid + "'");
		if (StringUtils.isNotBlank(cardSourceType)) {
			sql.append(" AND a.SOURCE_TYPE='" + cardSourceType + "'");
		}
		if (StringUtils.isNotBlank(startDate))
			sql.append(" AND a.stamp_created>=STR_TO_DATE('" + startDate + "', '%Y-%m-%d %H:%i:%s')");
		if (StringUtils.isNotBlank(endDate))
			sql.append(" AND a.stamp_created <= STR_TO_DATE('" + endDate + "', '%Y-%m-%d %H:%i:%s')");
		sql.append(" AND a.DATAADD>0 AND a.UEID!=0 AND a.MCC!=0");
		
		List<Map<String, Object>> list = findBySql(sql.toString(), null, Map.class);
		if (list!=null && list.size()>0) {
			Map<String, Object> map = list.get(0);
			String dataInfo = ObjectUtils.toString(map.get("dataInfo"));
			if (StringUtils.isBlank(dataInfo))
				return 0l;
			return Long.valueOf(dataInfo);
		}
		 
		return 0l;
	}
	
	/**
	 * 
	 * @Description 按国家分组统计设备流量(从simcardstatus表统计设备使用流量)
	 * @param ueid 设备对应UEID后5位
	 * @param startDate 开始时间
	 * @param endDate 结束时间
	 * @return List<Map<String,String>>  
	 * @author yifang.huang
	 * @date 2016年10月12日 上午11:17:43
	 */
	public List<Map<String, String>> getDeviceFlowGroupByCountry(String ueid, String startDate, String endDate) {
		
		if (StringUtils.isBlank(ueid) || StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate))
			return null;
		
		String sql = "select c.countryCode, c.countryNameCn, c.countryNameEn, format(sum(c.dataInfo)/1024/1024, 2) dataInfo "
				+ "from(select b.country_code countryCode, b.country_name_cn countryNameCn, b.country_name_en countryNameEn, a.datainfo dataInfo "
				+ "from (select hex(a.MCC) mcc, sum(a.DATAADD) datainfo "
				+ "from simcardstatus a where a.UEID_CURRNUM='" + ueid + "' "
				+ "AND stamp_created>=STR_TO_DATE('" + startDate + "', '%Y-%m-%d %H:%i:%s') AND stamp_created<=STR_TO_DATE('" + endDate + "', '%Y-%m-%d %H:%i:%s') "
				+ "and a.DATAADD>0 and a.UEID!=0 and a.MCC!=0 group by hex(a.MCC)) as a, mcc_def b where a.mcc=b.mcc) "
				+ "c group by c.countryCode, c.countryNameCn, c.countryNameEn";
		

		return findBySql(sql.toString(), null, Map.class);		
	}
	
	/**
	 * 
	 * @Description 按国家分组统计设备流量(从设备使用记录中统计流量)
	 * @param sourceType 渠道编号
	 * @param imei 设备编号
	 * @param startDate 开始时间
	 * @param endDate 结束时间
	 * @return List<Map<String,String>>  
	 * @author yifang.huang
	 * @date 2016年10月12日 上午11:17:43
	 */
	public List<Map<String, String>> getDeviceFlowGroupByCountry(String sourceType, String imei, String startDate, String endDate) {
		
		StringBuffer sql = new StringBuffer("select a.country_code countryCode, a.country_name_cn countryNameCn, a.country_name_en countryNameEn, sum(a.datainfo) dataInfo from mifi_usage_record a where 1=1");
		
		if (StringUtils.isNotBlank(sourceType)) {
			sql.append(" and a.source_type='" + sourceType + "'");
		}
		
		if (StringUtils.isNotBlank(imei)) {
			sql.append(" and a.imei='" + imei + "'");
		}
		
		if (StringUtils.isNotBlank(startDate)) {
			sql.append("AND a.stamp_created >= STR_TO_DATE('" + startDate + "', '%Y-%m-%d %H:%i:%s')");
		}
		
		if (StringUtils.isNotBlank(endDate)) {
			sql.append("AND a.stamp_created <= STR_TO_DATE('" + endDate + "', '%Y-%m-%d %H:%i:%s')");
		}

		sql.append("GROUP BY a.country_code, a.country_name_cn, a.country_name_en ORDER BY dataInfo desc");

		return findBySql(sql.toString(), null, Map.class);		
	}
	
	/**
	 * 
	 * @Description 取设备UEID
	 * @param dsn
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年10月12日 上午11:28:40
	 */
	public String getDeviceUeid(String dsn) {
		
		if (StringUtils.isBlank(dsn))
			return null;
		
		String sql = "select a.UEID_CURRNUM ueid from mifinode a where a.IMEI='" + dsn + "' limit 1";
		
		List<Map<String, String>> listMap = findBySql(sql.toString(), null, Map.class);
		if (listMap!=null && listMap.size()>0) {
			
			Map<String, String> map = listMap.get(0);
			String ueid = map.get("ueid");
			
			if (StringUtils.isBlank(ueid))
				return null;
			
			if (ueid.length() == 5)
				return ueid;
			
			ueid = "0000" + ueid;
			return ueid.substring(ueid.length() - 5);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @Description 根据dsn查询设备连接的服务IP及端口
	 * @param dsn
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年10月13日 下午2:34:38
	 */
	public String findServicerIpBy(String dsn) {
		
		if (StringUtils.isBlank(dsn))
			return null;
		
		String sql = "select a.ONLINEIP onlineIp from mifinode a where a.IMEI='" + dsn + "' limit 1";
		
		List<Map<String, String>> listMap = findBySql(sql.toString(), null, Map.class);
		if (listMap!=null && listMap.size()>0) {
			Map<String, String> map = listMap.get(0);
			return map.get("onlineIp");
		}
		
		return null;
		
	}
	
	/**
	 * 
	 * @Description 按渠道分组统计设备数量
	 * @return 
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2017年1月16日 下午5:12:57
	 */
	public List<Map<String, Object>> getDeviceTotalGroupByChannel() {
		
		String sql = "select count(a.ID) total, a.source_type keyValue from mifi_basic_info a group by a.source_type";
		
		return findBySql(sql, null, Map.class);
	}
	
	/**
	 * 
	 * @Description 按渠道分组统计设备使用数量
	 * @param flowData 使用流量 
	 * @return 
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2017年1月17日 下午5:30:12
	 */
	public List<Map<String, Object>> getDeviceUsedTotalGroupByChannel(Integer flowData) {
		
		String sql = "select count(tt.imei) total, tt.sourceType keyValue from "
				+ "(select t2.imei, t1.source_type sourceType, t2.dataInfo from mifi_basic_info t1, "
				+ "(select t.imei, sum(t.datainfo) dataInfo from mifi_usage_record t group by t.imei) t2 "
				+ "where t1.imei=t2.imei) tt where tt.dataInfo>"+flowData+" GROUP BY tt.sourceType;";
		
		return findBySql(sql, null, Map.class);
	}
	
	/**
	 * 
	 * @Description 设备入库时间列表
	 * @return 
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2017年1月17日 下午6:11:21
	 */
	public List<Map<String, Object>> getInTimeList() {
		
		String sql = "SELECT DATE_FORMAT(a.in_time, '%Y-%m-%d') inTime FROM mifi_basic_info a group by inTime";
		
		return findBySql(sql, null, Map.class);
	}
	
	/**
	 * 
	 * @Description 按入库时间分组统计设备数量
	 * @return 
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2017年1月16日 下午5:12:57
	 */
	public List<Map<String, Object>> getDeviceTotalGroupByInTime() {
		
		String sql = "select count(a.ID) total, DATE_FORMAT(a.in_time, '%Y-%m-%d') keyValue from mifi_basic_info a group by keyValue";
		
		return findBySql(sql, null, Map.class);
	}
	
	/**
	 * 
	 * @Description 按入库时间分组统计设备使用数量
	 * @param flowData 使用流量 
	 * @return 
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2017年1月17日 下午5:30:12
	 */
	public List<Map<String, Object>> getDeviceUsedTotalGroupByInTime(Integer flowData) {
		
		String sql = "SELECT count(tt.imei)total, tt.inTime keyValue FROM"
				+ "(SELECT t2.imei, t2.dataInfo, DATE_FORMAT(t1.in_time, '%Y-%m-%d') inTime FROM mifi_basic_info t1, "
				+ "(SELECT t.imei, sum(t.datainfo)dataInfo FROM mifi_usage_record t GROUP BY t.imei) t2 "
				+ "WHERE t1.imei = t2.imei)tt WHERE tt.dataInfo>"+flowData+" GROUP BY tt.inTime;";
		
		return findBySql(sql, null, Map.class);
	}
	
}
