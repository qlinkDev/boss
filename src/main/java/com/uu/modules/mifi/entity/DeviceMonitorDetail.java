/** 
 * @Package com.uu.modules.mifi.entity 
 * @Description 
 * @author yifang.huang
 * @date 2016年5月20日 上午11:02:43 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.uu.common.persistence.IdEntity;

/** 
 * @Description 设备监控详细信息 实体类
 * @author yifang.huang
 * @date 2016年5月20日 上午11:02:43 
 */
@Entity
@Table(name = "mifi_device_monitor_detail")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DeviceMonitorDetail extends IdEntity<DeviceMonitorDetail> {

	private static final long serialVersionUID = 3373210233658834675L;
	
	private String deviceMonitorId;					// 所属设备监控Id
	
	private String imei;							// 设备编号
	
	private String preId;							// 上一记录Id
	
	private String preStatus;						// 上一记录(状态[RSSI_9215,RSSI_6200])
	
	private Date preHappenDate;						// 上一记录发生时间
	
	private String nextId;							// 下一记录Id
	
	private String nextStatus;						// 下一记录(状态[RSSI_9215,RSSI_6200])
	
	private Date nextHappenDate;					// 下一记录发生时间
	
	private String countryName;						// 国家中文名
	
	private String countryNameEn;					// 国家英文名

	/** 
	 * @return deviceMonitorId
	 */
	@Column(name = "device_monitor_id")
	@NotBlank(message = "所属设备监控Id不能为空")
	@Length(min=32, max=32, message="所属设备监控Id长度为32")
	public String getDeviceMonitorId() {
		return deviceMonitorId;
	}

	/** 
	 * @param deviceMonitorId
	 */
	public void setDeviceMonitorId(String deviceMonitorId) {
		this.deviceMonitorId = deviceMonitorId;
	}

	/** 
	 * @return imei
	 */
	@Column(name = "imei")
	@NotBlank(message = "设备编号不能为空")
	@Length(min=1, max=32, message="设备编号长度在1~32之间")
	public String getImei() {
		return imei;
	}

	/** 
	 * @param imei
	 */
	public void setImei(String imei) {
		this.imei = imei;
	}

	/** 
	 * @return preId
	 */
	@Column(name = "pre_id")
	public String getPreId() {
		return preId;
	}

	/** 
	 * @param preId
	 */
	public void setPreId(String preId) {
		this.preId = preId;
	}

	/** 
	 * @return preStatus
	 */
	@Column(name = "pre_status")
	public String getPreStatus() {
		return preStatus;
	}

	/** 
	 * @param preStatus
	 */
	public void setPreStatus(String preStatus) {
		this.preStatus = preStatus;
	}

	/** 
	 * @return preHappenDate
	 */
	@Column(name = "pre_happen_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getPreHappenDate() {
		return preHappenDate;
	}

	/** 
	 * @param preHappenDate
	 */
	public void setPreHappenDate(Date preHappenDate) {
		this.preHappenDate = preHappenDate;
	}

	/** 
	 * @return nextId
	 */
	@Column(name = "next_id")
	public String getNextId() {
		return nextId;
	}

	/** 
	 * @param nextId
	 */
	public void setNextId(String nextId) {
		this.nextId = nextId;
	}

	/** 
	 * @return nextStatus
	 */
	@Column(name = "next_status")
	public String getNextStatus() {
		return nextStatus;
	}

	/** 
	 * @param nextStatus
	 */
	public void setNextStatus(String nextStatus) {
		this.nextStatus = nextStatus;
	}

	/** 
	 * @return nextHappenDate
	 */
	@Column(name = "next_happen_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getNextHappenDate() {
		return nextHappenDate;
	}

	/** 
	 * @param nextHappenDate
	 */
	public void setNextHappenDate(Date nextHappenDate) {
		this.nextHappenDate = nextHappenDate;
	}

	/** 
	 * @return countryName
	 */
	@Column(name = "country_name")
	public String getCountryName() {
		return countryName;
	}

	/** 
	 * @param countryName
	 */
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	/** 
	 * @return countryNameEn
	 */
	@Column(name = "country_name_en")
	public String getCountryNameEn() {
		return countryNameEn;
	}

	/** 
	 * @param countryNameEn
	 */
	public void setCountryNameEn(String countryNameEn) {
		this.countryNameEn = countryNameEn;
	}
	
}
