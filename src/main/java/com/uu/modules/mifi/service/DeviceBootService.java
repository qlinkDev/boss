/** 
 * @Package com.uu.modules.mifi.service 
 * @Description 
 * @author yifang.huang
 * @date 2016年11月22日 上午10:30:05 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.mifi.condition.DeviceBootCondition;
import com.uu.modules.mifi.dao.DeviceBootDao;
import com.uu.modules.mifi.dao.MifiManageDao;
import com.uu.modules.mifi.entity.DeviceBoot;
import com.uu.modules.om.dao.AdvertisingDao;
import com.uu.modules.om.entity.Advertising;
import com.uu.modules.sys.utils.DictUtils;

import freemarker.core.ParseException;

/** 
 * @Description 设备开机   业务处理类
 * @author yifang.huang
 * @date 2016年11月22日 上午10:30:05 
 */
@Service
public class DeviceBootService extends BaseService {
	
	@Autowired
	private DeviceBootDao deviceBootDao;
	
	@Autowired
	private MifiManageDao mifiManageDao;
	
	@Autowired
	private AdvertisingDao advertisingDao;
	
	/**
	 * 
	 * @Description 根据ID取数据
	 * @param id
	 * @return DeviceBoot  
	 * @author yifang.huang
	 * @date 2016年11月22日 上午10:40:22
	 */
	public DeviceBoot get(String id) {
		DeviceBoot oldBean = deviceBootDao.get(id);
		if (oldBean != null) {
			DeviceBoot newBean = new DeviceBoot();
			BeanUtils.copyProperties(oldBean, newBean);
			
			// 清除指定对象缓存
			deviceBootDao.getSession().evict(oldBean);
			
			return newBean;
		}
		return null;
	}

	/**
	 * 
	 * @Description 设备开机 保存
	 * @param bean
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年11月22日 上午11:18:22
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized Map<String, String> save(DeviceBoot bean) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		try {
			
			// 需要判断重复的字段（imei,mac,createDate 一个手机一天内在一台设备上只记录一次开机记录）
			String imei = bean.getImei();
			String mac = bean.getMac();
			Date createDate = bean.getCreateDate();
			String createDateStr = new SimpleDateFormat("yyyy-MM-dd").format(createDate);
			
			// 如果mac为空，一分钟内记录一次开机记录
			if (StringUtils.isBlank(mac)) {

				Calendar c = Calendar.getInstance();
				c.add(Calendar.MINUTE, -1);
				DeviceBootCondition condition = new DeviceBootCondition();
				condition.setGeFullCreateDate(c.getTime());
				condition.setEqImei(imei);
				List<DeviceBoot> bootList = findList(condition);
				if (bootList==null || bootList.size()==0) {
					deviceBootDao.save(bean);
					map.put("code", "success");
					map.put("message", "保存成功！");
					return map;
				}
			}
				
			// 查询是否已经有开机记录
			DeviceBootCondition condition = new DeviceBootCondition();
			condition.setEqImei(imei);
			condition.setEqMac(mac);
			condition.setGeCreateDate(createDateStr);
			condition.setLeCreateDate(createDateStr);
			List<DeviceBoot> list = deviceBootDao.findList(condition);
			if (list!=null && list.size()>0) {
				map.put("code", "error");
				map.put("message", "设备[" + mac + "],在" + createDateStr + "已有开机记录！");
				return map;
			}
			
			// 如果没有记录则保存
			deviceBootDao.save(bean);
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
			map.put("code", "error");
			map.put("message", "保存失败，请联系客户！");
			return map;
		}

		map.put("code", "success");
		map.put("message", "保存成功！");
		return map;
	}

	/**
	 * 
	 * @Description 根据ID删除数据
	 * @param id 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年11月22日 上午10:41:38
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void delete(String id) {
		deviceBootDao.deleteById(id);
	}
	
	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<DeviceBoot>  
	 * @author yifang.huang
	 * @date 2016年11月22日 上午10:48:33
	 */
	public List<DeviceBoot> findList(DeviceBootCondition condition) {
		
		return deviceBootDao.findList(condition);
		
	}
	
	/**
	 * 
	 * @Description 根据查询参数取分页数据
	 * @param page
	 * @param condition
	 * @return Page<DeviceBoot>  
	 * @author yifang.huang
	 * @date 2016年11月22日 上午10:48:22
	 */
	public Page<DeviceBoot> findPage(Page<DeviceBoot> page, DeviceBootCondition condition) {
		
		return deviceBootDao.findPage(page, condition);
		
	}
	
	/**
	 * 
	 * @Description 记录  '我要上网' 点数行为
	 * @param paramMap
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2017年1月22日 上午10:53:40
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> saveAndPageClick(Map<String, Object> paramMap) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		// 参数
		String advId = ObjectUtils.toString(paramMap.get("advId"));
		String imei = ObjectUtils.toString(paramMap.get("imei"));
		String mcc = ObjectUtils.toString(paramMap.get("mcc"));
		String type = ObjectUtils.toString(paramMap.get("type"));
		
		// 参数判断
		if (StringUtils.isBlank(mcc)) {
			map.put("status", "error");
			map.put("message", "MCC不能为空");
			return map;
		}
		if (StringUtils.isBlank(type)) {
			map.put("status", "error");
			map.put("message", "页面点击行为类型不能为空");
			return map;
		}
		if (StringUtils.isBlank(advId)) {
			map.put("status", "error");
			map.put("message", "广告信息不能为空");
			return map;
		}
		Advertising adv = advertisingDao.get(advId);
		if (adv == null) {
			map.put("status", "error");
			map.put("message", "广告未找到");
			return map;
		}
		if (StringUtils.isBlank(imei)) {
			map.put("status", "error");
			map.put("message", "设备编辑不能为空");
			return map;
		}
		// 判断设备是否入库
		HashMap<String, String> mifi = mifiManageDao.getMifilistBySn(imei);
		if (mifi == null) {
			map.put("status", "error");
			map.put("message", "设备未入库 请与客服联系");
			return map;
		}
		String sourceType = mifi.get("sourceType");
		
		// 保存数据
		DeviceBoot bean = new DeviceBoot();
		bean.setType(type);
		bean.setImei(imei);
		bean.setMcc(mcc);
		bean.setAdvertising(adv);
		bean.setSourceType(sourceType);
		deviceBootDao.save(bean);

		map.put("status", "success");
		map.put("message", "点击行为记录成功");
		return map;
	}
	
	/**
	 * 
	 * @Description 按设备编辑和发生时间（年月日）分组统计记录总数
	 * @param condition
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2017年1月22日 下午3:13:15
	 */
	public List<Map<String, Object>> getTotalGroupByImeiAndTime(DeviceBootCondition condition) {
		return deviceBootDao.getTotalGroupByImeiAndTime(condition);
	}

	/**
	 * 统计页面
	 * 
	 * @Description
	 * @param paramMap
	 * @return
	 * @return List<Map<String,Integer>>
	 * @author wangsai
	 * @date 2017年2月13日 下午4:49:53
	 */
	public List<Map<String, Integer>> statByDeviceBoot(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer(
				"SELECT 	m.type, 	count(m.type)num FROM 	mifi_device_boot m WHERE 	1 = 1");
		String eqSourceType = ObjectUtils.toString(paramMap.get("eqSourceType"));
		if (StringUtils.isNotBlank(eqSourceType)) {
			buffer.append(" and m.source_type = '").append(eqSourceType).append("'");
		}

		String eqAdvertisingId = ObjectUtils.toString(paramMap.get("eqAdvertisingId"));
		if (StringUtils.isNotBlank(eqAdvertisingId)) {
			buffer.append(" and m.advertising_id = '").append(eqAdvertisingId).append("'");
		}

		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(beginDate)) {
			buffer.append(" and m.create_date >= str_to_date('" + beginDate + "','%Y-%m-%d')");
		}
		if (StringUtils.isNotBlank(endDate)) {
			buffer.append(" and m.create_date < date_add(str_to_date('" + endDate + "','%Y-%m-%d'),interval 1 day)");
		}

		buffer.append(" GROUP BY m.type");
		return deviceBootDao.findBySql(buffer.toString(), null, Map.class);
	}

	/**
	 * 按月
	 * 
	 * @Description
	 * @param paramMap
	 * @return
	 * @return List<Map<String,Integer>>
	 * @author wangsai
	 * @date 2017年2月20日 下午3:47:34
	 */
	public List<Map<String, Integer>> statByDeviceBootMonth(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer(
				"SELECT 	DATE_FORMAT(m.create_date, '%Y-%c')AS time,count(1)AS num ,m.type type FROM mifi_device_boot m where 1=1");
		String eqSourceType = ObjectUtils.toString(paramMap.get("eqSourceType"));
		if (StringUtils.isNotBlank(eqSourceType)) {
			buffer.append(" and m.source_type = '").append(eqSourceType).append("'");
		}

		String eqAdvertisingId = ObjectUtils.toString(paramMap.get("eqAdvertisingId"));
		if (StringUtils.isNotBlank(eqAdvertisingId)) {
			buffer.append(" and m.advertising_id = '").append(eqAdvertisingId).append("'");
		}

		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(beginDate)) {
			buffer.append(" and DATE_FORMAT(m.create_date, '%Y-%m') >= '").append(beginDate).append("'");
		}
		if (StringUtils.isNotBlank(endDate)) {
			buffer.append(" and DATE_FORMAT(m.create_date, '%Y-%m') <= '").append(endDate).append("'");
		}

		buffer.append(" GROUP BY DATE_FORMAT(m.create_date, '%Y-%m')  , type ");
		return deviceBootDao.findBySql(buffer.toString(), null, Map.class);
	}

	/**
	 * 按日
	 * 
	 * @Description
	 * @param paramMap
	 * @return
	 * @return List<Map<String,Integer>>
	 * @author wangsai
	 * @date 2017年2月20日 下午3:47:44
	 */
	public List<Map<String, Integer>> statByDeviceBootDay(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer(
				"SELECT 	DATE_FORMAT(m.create_date, '%Y-%c-%e')AS time,count(1)AS num ,m.type type FROM mifi_device_boot m where 1=1");
		String eqSourceType = ObjectUtils.toString(paramMap.get("eqSourceType"));
		if (StringUtils.isNotBlank(eqSourceType)) {
			buffer.append(" and m.source_type = '").append(eqSourceType).append("'");
		}

		String eqAdvertisingId = ObjectUtils.toString(paramMap.get("eqAdvertisingId"));
		if (StringUtils.isNotBlank(eqAdvertisingId)) {
			buffer.append(" and m.advertising_id = '").append(eqAdvertisingId).append("'");
		}

		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(beginDate)) {
			buffer.append(" and DATE_FORMAT(m.create_date, '%Y-%m-%d') >= '").append(beginDate).append("'");
		}
		if (StringUtils.isNotBlank(endDate)) {
			buffer.append(" and DATE_FORMAT(m.create_date, '%Y-%m-%d') <= '").append(endDate).append("'");
		}

		buffer.append(" GROUP BY DATE_FORMAT(m.create_date, '%Y-%m-%d')  , type ");
		return deviceBootDao.findBySql(buffer.toString(), null, Map.class);
	}

	/**
	 * 按类型导出
	 * 
	 * @Description
	 * @return List<MifiUsageRecord>
	 * @author wangsai
	 * @date 2017年2月21日 上午11:13:39
	 */
	public List<DeviceBoot> findDeviceBootListForExport(Map<String, Object> paramMap) throws ParseException {
		List<DeviceBoot> list = null;
		StringBuffer buffer = new StringBuffer(
				"SELECT 	m.type, 	count(m.type)num FROM 	mifi_device_boot m WHERE 	1 = 1");
		String eqSourceType = ObjectUtils.toString(paramMap.get("eqSourceType"));
		if (StringUtils.isNotBlank(eqSourceType)) {
			buffer.append(" and m.source_type = '").append(eqSourceType).append("'");
		}

		String eqAdvertisingId = ObjectUtils.toString(paramMap.get("eqAdvertisingId"));
		if (StringUtils.isNotBlank(eqAdvertisingId)) {
			buffer.append(" and m.advertising_id = '").append(eqAdvertisingId).append("'");
		}

		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(beginDate)) {
			buffer.append(" and m.create_date >= str_to_date('" + beginDate + "','%Y-%m-%d')");
		}
		if (StringUtils.isNotBlank(endDate)) {
			buffer.append(" and m.create_date < date_add(str_to_date('" + endDate + "','%Y-%m-%d'),interval 1 day)");
		}

		buffer.append(" GROUP BY m.type ORDER BY  FIELD(`type`, 'BOOT', 'HOME_SHOW', 'CON_NET', 'JUMP')");
		List<String[]> listObjArr = deviceBootDao.findBySql(buffer.toString());
		if (listObjArr != null && listObjArr.size() > 0) {
			list = new ArrayList<DeviceBoot>();
			DeviceBoot DeviceBoot = null;
			for (Object[] objs : listObjArr) {
				DeviceBoot = new DeviceBoot();
				if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)) {
					DeviceBoot.setUserAgent(beginDate + "到" + endDate);
				} else {
					DeviceBoot.setUserAgent("所有时间");
				}
				DeviceBoot.setImei(ObjectUtils.toString(objs[1]));
				String qq = DictUtils.getDictLabel(ObjectUtils.toString(objs[0]), "device_boot_type", "无");
				DeviceBoot.setType(qq);
				list.add(DeviceBoot);
				DeviceBoot = null;
			}
		}
		return list;
	}
	/**
	 * 按月导出
	 * @Description 
	 * @param paramMap
	 * @return
	 * @throws ParseException 
	 * @return List<DeviceBoot>  
	 * @author wangsai
	 * @date 2017年2月24日 下午4:39:11
	 */
	public List<DeviceBoot> findDeviceBootMonthForExport(Map<String, Object> paramMap) throws ParseException {
		List<DeviceBoot> list = null;
		StringBuffer buffer = new StringBuffer(
				"SELECT 	DATE_FORMAT(m.create_date, '%Y-%c')AS time,count(1)AS num ,m.type type FROM mifi_device_boot m where 1=1");
		String eqSourceType = ObjectUtils.toString(paramMap.get("eqSourceType"));
		if (StringUtils.isNotBlank(eqSourceType)) {
			buffer.append(" and m.source_type = '").append(eqSourceType).append("'");
		}

		String eqAdvertisingId = ObjectUtils.toString(paramMap.get("eqAdvertisingId"));
		if (StringUtils.isNotBlank(eqAdvertisingId)) {
			buffer.append(" and m.advertising_id = '").append(eqAdvertisingId).append("'");
		}

		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(beginDate)) {
			buffer.append(" and DATE_FORMAT(m.create_date, '%Y-%m') >= '").append(beginDate).append("'");
		}
		if (StringUtils.isNotBlank(endDate)) {
			buffer.append(" and DATE_FORMAT(m.create_date, '%Y-%m') <= '").append(endDate).append("'");
		}

		buffer.append(" GROUP BY DATE_FORMAT(m.create_date, '%Y-%m')  , type ORDER BY time ASC, FIELD(`type`, 'BOOT', 'HOME_SHOW', 'CON_NET', 'JUMP')");
		List<String[]> listObjArr = deviceBootDao.findBySql(buffer.toString());
		if (listObjArr != null && listObjArr.size() > 0) {
			list = new ArrayList<DeviceBoot>();
			DeviceBoot DeviceBoot = null;
			for (Object[] objs : listObjArr) {
				DeviceBoot = new DeviceBoot();
				DeviceBoot.setUserAgent(ObjectUtils.toString(objs[0]));
				String qq = DictUtils.getDictLabel(ObjectUtils.toString(objs[2]), "device_boot_type", "无");
				DeviceBoot.setType(qq);
				DeviceBoot.setImei(ObjectUtils.toString(objs[1]));
				list.add(DeviceBoot);
				DeviceBoot = null;
			}
		}
		return list;
	}
	/**
	 * 按日导出
	 * @Description 
	 * @param paramMap
	 * @return
	 * @throws ParseException 
	 * @return List<DeviceBoot>  
	 * @author wangsai
	 * @date 2017年2月24日 下午4:39:00
	 */
	public List<DeviceBoot> findDeviceBootDayForExport(Map<String, Object> paramMap) throws ParseException {
		List<DeviceBoot> list = null;
		StringBuffer buffer = new StringBuffer(
				"SELECT 	DATE_FORMAT(m.create_date, '%Y-%m-%d')AS time,count(1)AS num ,m.type type FROM mifi_device_boot m where 1=1");
		String eqSourceType = ObjectUtils.toString(paramMap.get("eqSourceType"));
		if (StringUtils.isNotBlank(eqSourceType)) {
			buffer.append(" and m.source_type = '").append(eqSourceType).append("'");
		}

		String eqAdvertisingId = ObjectUtils.toString(paramMap.get("eqAdvertisingId"));
		if (StringUtils.isNotBlank(eqAdvertisingId)) {
			buffer.append(" and m.advertising_id = '").append(eqAdvertisingId).append("'");
		}

		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(beginDate)) {
			buffer.append(" and DATE_FORMAT(m.create_date, '%Y-%m-%d') >= '").append(beginDate).append("'");
		}
		if (StringUtils.isNotBlank(endDate)) {
			buffer.append(" and DATE_FORMAT(m.create_date, '%Y-%m-%d') <= '").append(endDate).append("'");
		}

		buffer.append(" GROUP BY DATE_FORMAT(m.create_date, '%Y-%m-%d')  , type ORDER BY time ASC, FIELD(`type`, 'BOOT', 'HOME_SHOW', 'CON_NET', 'JUMP') ");
		List<String[]> listObjArr = deviceBootDao.findBySql(buffer.toString());
		if (listObjArr != null && listObjArr.size() > 0) {
			list = new ArrayList<DeviceBoot>();
			DeviceBoot DeviceBoot = null;
			for (Object[] objs : listObjArr) {
				DeviceBoot = new DeviceBoot();
				DeviceBoot.setUserAgent(ObjectUtils.toString(objs[0]));
				String qq = DictUtils.getDictLabel(ObjectUtils.toString(objs[2]), "device_boot_type", "无");
				DeviceBoot.setType(qq);
				DeviceBoot.setImei(ObjectUtils.toString(objs[1]));
				list.add(DeviceBoot);
				DeviceBoot = null;
			}
		}
		return list;
	}

}
