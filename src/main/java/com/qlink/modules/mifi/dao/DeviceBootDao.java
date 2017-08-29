/** 
 * @Package com.uu.modules.mifi.dao 
 * @Description 
 * @author yifang.huang
 * @date 2016年11月22日 上午10:15:51 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Page;
import com.uu.modules.mifi.condition.DeviceBootCondition;
import com.uu.modules.mifi.entity.DeviceBoot;

/** 
 * @Description 设备开机   DAO接口类
 * @author yifang.huang
 * @date 2016年11月22日 上午10:15:51 
 */
@Repository
public class DeviceBootDao extends BaseDao<DeviceBoot> {

	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<DeviceBoot>  
	 * @author yifang.huang
	 * @date 2016年11月22日 上午10:17:04
	 */
	public List<DeviceBoot> findList(DeviceBootCondition condition) {
		
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
	 * @return Page<DeviceBoot>  
	 * @author yifang.huang
	 * @date 2016年11月22日 上午10:17:13
	 */
	public Page<DeviceBoot> findPage(Page<DeviceBoot> page, DeviceBootCondition condition) {
		
		DetachedCriteria dc = createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return find(page, dc);
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
		

		String imei = condition.getLikeImei();											// 设备编号
		String type = condition.getEqType();											// 点击类型
		String sourceType = condition.getEqSourceType();								// 设备所属渠道	
		String advertisingId = condition.getEqAdvertisingId();							// 记录所属广告
		String startDate = condition.getGeCreateDate();									// 发生开始时间
		startDate = StringUtils.isBlank(startDate) ? null : startDate + " 00:00:00";
		String endDate = condition.getLeCreateDate();									// 发生结束时间
		endDate = StringUtils.isBlank(endDate) ? null : endDate + " 23:59:59";
		
		StringBuffer sb = new StringBuffer("select count(t.imei) total from (");
		sb.append("select a.imei, DATE_FORMAT(a.create_date, '%Y-%m-%d') createDate from mifi_device_boot a where (1=1)");
		if (StringUtils.isNotBlank(imei))
			sb.append(" and a.imei like '%" + imei + "%'");
		if (StringUtils.isNotBlank(type))
			sb.append(" and a.type='" + type + "'");
		if (StringUtils.isNotBlank(advertisingId))
			sb.append(" and a.advertising_id='" + advertisingId + "'");
		if (StringUtils.isNotBlank(sourceType))
			sb.append(" and a.source_type='" + sourceType + "'");
		if (StringUtils.isNotBlank(startDate))
			sb.append(" and a.create_date>='" + startDate + "'");
		if (StringUtils.isNotBlank(endDate))
			sb.append(" and a.create_date<='" + endDate + "'");
		sb.append(" group by a.imei, createDate");
		sb.append(") t");
		
		return findBySql(sb.toString(), null, Map.class);
	}
}
