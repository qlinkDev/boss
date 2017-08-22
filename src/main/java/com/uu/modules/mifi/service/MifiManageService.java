package com.uu.modules.mifi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.common.utils.StringUtils;
import com.uu.modules.mifi.dao.MifiManageDao;
import com.uu.modules.mifi.entity.MifiBasicInfo;

@Service
public class MifiManageService extends BaseService {

	public static Logger logger = LoggerFactory.getLogger(MifiManageService.class);

	@Autowired
	private MifiManageDao mifiManageDao;

	public Page<HashMap> findMifiManageList(Page<HashMap> page, Map<String, Object> paramMap) {
		String sqlString = "select a.sn,a.source_type,b.cnt,b.now_flag, b.outOrderId from mifi_basic_info a left join (";
		sqlString += "select t.out_order_id outOrderId, f.dsn,count(1) cnt, if(date(min(t.start_date))<=date(now()),1,0) now_flag from mifi_order t,mifi_order_detail f";
		sqlString += " where t.order_id = f.order_id and t.end_date >= date(now()) ";
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			sqlString += " and t.source_type = '" + sourceType + "' ";
		}
		String sn = ObjectUtils.toString(paramMap.get("sn"));
		if (StringUtils.isNotBlank(sn)) {
			sqlString += " and f.dsn = '" + sn + "' ";
		}
		sqlString += " group by dsn) as b on (a.sn = b.dsn) ";
		sqlString += " where 1 = 1 ";
		if (StringUtils.isNotBlank(sourceType)) {
			sqlString += " and a.source_type = '" + sourceType + "' ";
		}
		if (StringUtils.isNotBlank(sn)) {
			sqlString += " and a.sn = '" + sn + "' ";
		}
		Page<HashMap> _page = mifiManageDao.findBySql(page, sqlString, null, Map.class);
		List<HashMap> list = _page.getList();
		for(int i = 0; i < list.size(); i++){
			if(StringUtils.isNotBlank(ObjectUtils.toString(list.get(i).get("now_flag")))){
				list.get(i).putAll(getSnMccInfo((String)list.get(i).get("sn")));
			}
		}
		_page.setList(list);
		return _page;
	}
	
	public HashMap getSnMccInfo(String sn){
		String sqlString = "select t.UEID_MCC mcc,t.nwstatus,t.uestatus,t.stamp_updated,t.ueid ueId,f.country_code,f.country_name_cn,f.country_name_en from mifinode t,mcc_def f";
		sqlString += " where t.UEID_MCC = f.mcc ";
		sqlString += " and t.imei = '" + sn + "' ";
		sqlString += " order by t.stamp_updated desc limit 0,1";
		List<HashMap> list = mifiManageDao.findBySql(sqlString, null, Map.class);
		HashMap result = new HashMap();
		if(list.size() > 0){
			result = list.get(0);
		}
		return result;
	}
	
	/**
	 * 
	 * @Description 根据设备编号查询设备信息
	 * @param sn 设备编号
	 * @return MifiBasicInfo  
	 * @author yifang.huang
	 * @date 2016年3月31日 上午11:16:37
	 */
	public MifiBasicInfo getMifiBasicInfoBySn(String sn){
		return mifiManageDao.getMifiBasicInfoBySn(sn);
	}
	
	/**
	 * 查询设备信息
	 * @param sourceType
	 * @return
	 */
	public List<MifiBasicInfo> findMifiBasicInfoList(String sourceType){
		return mifiManageDao.findMifiBasicInfoList(sourceType);
	}
	
	/**
	 * 查询Mifilist
	 * @param sn
	 * @return
	 */
	public HashMap<String, String> getMifilistBySn(String sn){
		return mifiManageDao.getMifilistBySn(sn);
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
		return mifiManageDao.getMifiInfoBySn(sn);
	}
	
	/**
	 * 修改Mifilist的UeAllowed
	 * @param flag
	 * @param sn
	 * @return
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updateMifilistUeAllowed(String flag, String sn) {
		mifiManageDao.updateMifilistUeAllowed(flag, sn);
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
		return mifiManageDao.getCanBuyDevice(sourceType, startDate, endDate);
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
		return mifiManageDao.checkCanBuyDevice(imei, sourceType, startDate, endDate);
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
		return mifiManageDao.findServicerIpBy(dsn);
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
		return mifiManageDao.getDeviceUeid(dsn);
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
		return mifiManageDao.getDeviceFlow(ueid, cardSourceType, startDate, endDate);
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
		return mifiManageDao.getDeviceTotalGroupByChannel();
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
		return mifiManageDao.getDeviceUsedTotalGroupByChannel(flowData);
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
		return mifiManageDao.getInTimeList();
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
		return mifiManageDao.getDeviceTotalGroupByInTime();
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
		return mifiManageDao.getDeviceUsedTotalGroupByInTime(flowData);
	}
	
}
