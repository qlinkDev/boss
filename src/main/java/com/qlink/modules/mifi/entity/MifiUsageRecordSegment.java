/** 
 * @Package com.uu.modules.mifi.entity 
 * @Description 
 * @author yuxiaoyu
 * @date 2017年3月2日 下午6:05:27 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.uu.common.persistence.BaseEntity;
import com.uu.common.utils.IdGen;
import com.uu.common.utils.excel.annotation.ExcelField;

/** 
 * @Description 
 * @author yuxiaoyu
 * @date 2017年3月2日 下午6:05:27 
 */
@Entity
@Table(name = "mifi_usage_record_segment")
@DynamicInsert
@DynamicUpdate
public class MifiUsageRecordSegment extends BaseEntity<MifiUsageRecordSegment>{
	/** 
	 * serialVersionUID long
	 */ 
	private static final long serialVersionUID = 1L;

	private String id;

	/** 设备编号 */
	private String imei;

	/** 代理商 */
	private String sourceType;
	
	/** 卡所属代理商 */
	private String cardSourceType;

	/** 订单编号 */
	private String outOrderId;

	private String mcc;

	/** 设备状态 */
	private Integer uestatus;

	private Date stampCreated;

	/** 所在地区中文名 */
	private String countryNameCn;

	/** 所在地区英文名 */
	private String countryNameEn;

	/** 国家编码 */
	private String countryCode;

	/** 卡槽编号 */
	private Integer simBankId;

	/** 卡槽位置 */
	private Integer simId;

	/** 设备服务器连接状态 */
	private Integer nwstatus;

	/** 设备电量 */
	private Integer powerInfo;

	/** 主卡被网络拒绝原因 */
	private String mainRejCause;

	/** 主卡注册国家区域码 */
	private String mainMcc;

	/** 主卡注册运营商编码 */
	private String mainMnc;

	/** 主卡位置跟踪区域码 */
	private Integer mainTac;

	/** 主卡所处基站编号 */
	private Long mainCallid;

	/** 主卡接收信号强度 */
	private Integer mainRssi;

	/** 副卡注册国家区域码 */
	private String additionalMcc;

	/** 副卡注册运营商编码 */
	private String additionalMnc;

	/** 副卡位置跟踪区域码 */
	private Integer additionalTac;

	/** 副卡所处基站编号 */
	private Long additionalCellid;

	/** 副卡接收信号强度 */
	private Integer additionalRssi;

	/** 外设连接数量 */
	private Integer devices;

	/** 设备使用总流量(M) */
	private Long datainfo;

	/** 费用 */
	private Double cost;

	private String deviceStatus;

	/** 设备批次号 */
	private String bath;
	
	/** 21点以后的流量(M) */
	private Long dataAfter21;

	@PrePersist
	public void prePersist() {
		this.id = IdGen.uuid();
	}

	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	/** 
	 * @return cardSourceType
	 */
	public String getCardSourceType() {
		return cardSourceType;
	}

	/** 
	 * @param cardSourceType
	 */
	public void setCardSourceType(String cardSourceType) {
		this.cardSourceType = cardSourceType;
	}

	public String getOutOrderId() {
		return outOrderId;
	}

	public void setOutOrderId(String outOrderId) {
		this.outOrderId = outOrderId;
	}

	public String getMcc() {
		return mcc;
	}

	public void setMcc(String mcc) {
		this.mcc = mcc;
	}

	public Integer getUestatus() {
		return uestatus;
	}

	@Transient
	@ExcelField(title = "时间" ,align =2,sort=10)
	public String getDeviceStatus() {
		return deviceStatus;
	}

	public void setDeviceStatus(String deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

	public void setUestatus(Integer uestatus) {
		this.uestatus = uestatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getStampCreated() {
		return stampCreated;
	}

	public void setStampCreated(Date stampCreated) {
		this.stampCreated = stampCreated;
	}
	@ExcelField(title = "国家名称" ,align =2,sort=40)
	public String getCountryNameCn() {
		return countryNameCn;
	}

	public void setCountryNameCn(String countryNameCn) {
		this.countryNameCn = countryNameCn;
	}
	@ExcelField(title = "使用记录数" ,align =2,sort=20)
	public String getCountryNameEn() {
		return countryNameEn;
	}

	public void setCountryNameEn(String countryNameEn) {
		this.countryNameEn = countryNameEn;
	}
	public Integer getSimBankId() {
		return simBankId;
	}

	public void setSimBankId(Integer simBankId) {
		this.simBankId = simBankId;
	}

	public Integer getSimId() {
		return simId;
	}

	public void setSimId(Integer simId) {
		this.simId = simId;
	}

	public Integer getNwstatus() {
		return nwstatus;
	}

	public void setNwstatus(Integer nwstatus) {
		this.nwstatus = nwstatus;
	}

	private String deviceLinkStatus;

	@Transient
	@ExcelField(title = "消费总额" ,align =2,sort=30)
	public String getDeviceLinkStatus() {
		return deviceLinkStatus;
	}

	public void setDeviceLinkStatus(String deviceLinkStatus) {
		this.deviceLinkStatus = deviceLinkStatus;
	}

	public Integer getPowerInfo() {
		return powerInfo;
	}

	public void setPowerInfo(Integer powerInfo) {
		this.powerInfo = powerInfo;
	}

	@Column(name = "rej_cause_9215")
	public String getMainRejCause() {
		return mainRejCause;
	}

	public void setMainRejCause(String mainRejCause) {
		this.mainRejCause = mainRejCause;
	}

	@Column(name = "mcc_9215")
	public String getMainMcc() {
		return mainMcc;
	}

	public void setMainMcc(String mainMcc) {
		this.mainMcc = mainMcc;
	}

	@Column(name = "mnc_9215")
	public String getMainMnc() {
		return mainMnc;
	}

	public void setMainMnc(String mainMnc) {
		this.mainMnc = mainMnc;
	}

	@Column(name = "tac_9215")
	public Integer getMainTac() {
		return mainTac;
	}

	public void setMainTac(Integer mainTac) {
		this.mainTac = mainTac;
	}

	@Column(name = "callid_9215")
	public Long getMainCallid() {
		return mainCallid;
	}

	public void setMainCallid(Long mainCallid) {
		this.mainCallid = mainCallid;
	}

	@Column(name = "rssi_9215")
	public Integer getMainRssi() {
		return mainRssi;
	}

	public void setMainRssi(Integer mainRssi) {
		this.mainRssi = mainRssi;
	}

	@Column(name = "mcc_6200")
	public String getAdditionalMcc() {
		return additionalMcc;
	}

	public void setAdditionalMcc(String additionalMcc) {
		this.additionalMcc = additionalMcc;
	}

	@Column(name = "mnc_6200")
	public String getAdditionalMnc() {
		return additionalMnc;
	}

	public void setAdditionalMnc(String additionalMnc) {
		this.additionalMnc = additionalMnc;
	}

	@Column(name = "tac_6200")
	public Integer getAdditionalTac() {
		return additionalTac;
	}

	public void setAdditionalTac(Integer additionalTac) {
		this.additionalTac = additionalTac;
	}

	@Column(name = "cellid_6200")
	public Long getAdditionalCellid() {
		return additionalCellid;
	}

	public void setAdditionalCellid(Long additionalCellid) {
		this.additionalCellid = additionalCellid;
	}

	@Column(name = "rssi_6200")
	public Integer getAdditionalRssi() {
		return additionalRssi;
	}

	public void setAdditionalRssi(Integer additionalRssi) {
		this.additionalRssi = additionalRssi;
	}

	public Integer getDevices() {
		return devices;
	}

	public void setDevices(Integer devices) {
		this.devices = devices;
	}

	public Long getDatainfo() {
		return datainfo;
	}

	public void setDatainfo(Long datainfo) {
		this.datainfo = datainfo;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public String getBath() {
		return bath;
	}

	public void setBath(String bath) {
		this.bath = bath;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Long getDataAfter21() {
		return dataAfter21;
	}

	public void setDataAfter21(Long dataAfter21) {
		this.dataAfter21 = dataAfter21;
	}
}
