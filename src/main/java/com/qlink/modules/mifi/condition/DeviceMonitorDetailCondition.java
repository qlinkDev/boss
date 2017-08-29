/** 
 * @Package com.uu.modules.mifi.condition 
 * @Description 
 * @author yifang.huang
 * @date 2016年5月20日 下午2:53:11 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.condition;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.uu.modules.om.entity.ConsumeRecord;

/** 
 * @Description 设备监控详细信息 查询条件类
 * @author yifang.huang
 * @date 2016年5月20日 下午2:53:11 
 */
public class DeviceMonitorDetailCondition {
	
	private String eqDeviceMonitorId;						// 所属设备监控Id eq 查询值
	
	private String likeImei;								// 设备编号 like 查询值

	public void build(DetachedCriteria dc) {
		
		if (StringUtils.isNotBlank(eqDeviceMonitorId)){
			dc.add(Restrictions.eq("deviceMonitorId", eqDeviceMonitorId));
		}
		
		if (StringUtils.isNotBlank(likeImei)) {
			dc.add(Restrictions.like("imei", likeImei, MatchMode.ANYWHERE));
		}
		
		// 查询标记为0（正常）的数据
		dc.add(Restrictions.eq(ConsumeRecord.FIELD_DEL_FLAG, ConsumeRecord.DEL_FLAG_NORMAL));
		
		// 按创建时间降序排序
		dc.addOrder(Order.desc("createDate"));
		
	}

	/** 
	 * @return eqDeviceMonitorId
	 */
	public String getEqDeviceMonitorId() {
		return eqDeviceMonitorId;
	}

	/** 
	 * @param eqDeviceMonitorId
	 */
	public void setEqDeviceMonitorId(String eqDeviceMonitorId) {
		this.eqDeviceMonitorId = eqDeviceMonitorId;
	}

	/** 
	 * @return likeImei
	 */
	public String getLikeImei() {
		return likeImei;
	}

	/** 
	 * @param likeImei
	 */
	public void setLikeImei(String likeImei) {
		this.likeImei = likeImei;
	}

}
