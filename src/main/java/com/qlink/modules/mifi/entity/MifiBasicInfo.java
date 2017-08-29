package main.java.com.qlink.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.uu.common.persistence.BaseEntity;
import com.uu.common.utils.IdGen;
import com.uu.common.utils.excel.annotation.ExcelField;
import com.uu.modules.sys.utils.UserUtils;

/**
 * @author wangyong
 * @date 2016年1月21日
 */
@Entity
@Table(name = "mifi_basic_info")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MifiBasicInfo extends BaseEntity<MifiBasicInfo> {

	private static final long serialVersionUID = 1L;

	public MifiBasicInfo() {
		id = IdGen.uuid();// 生成新的唯一序列
		status = status_1;// 设备状态
		inTime = new Date();// 入库时间
		inUser = UserUtils.getUser().getLoginName();// 入库操作员
	}

	private String id;

	@Id
	@Column(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String sn;

	@Column(name = "sn")
	@NotNull(message = "设备序列号不能为空")
	@ExcelField(title = "设备序列号", align = 2, sort = 10)
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	private String imei;

	@Column(name = "imei")
	@ExcelField(title = "设备IMEI", align = 2, sort = 20)
	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	private String type;

	@Column(name = "type")
	@ExcelField(title = "设备类型", align = 2, sort = 30)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private String model;

	@Column(name = "model")
	@ExcelField(title = "设备型号", align = 2, sort = 35)
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public final static String ownerType_0 = "0";// 游友移动

	public final static String ownerType_1 = "1";// 渠道

	public final static String ownerType_2 = "2";// 个人

	private String ownerType;

	@Column(name = "owner_type")
	@ExcelField(title = "设备归属(0:游友移动,1:代理商,2:个人)", align = 2, sort = 40)
	public String getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(String ownerType) {
		if (StringUtils.isBlank(ownerType)) {
			this.ownerType = ownerType_0;
		} else {
			this.ownerType = ownerType;
		}
	}

	private String sourceType;

	@ExcelField(title = "设备归属渠道", align = 2, sort = 50)
	@Column(name = "source_type")
	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	private String place;

	@Column(name = "place")
	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public final static String status_1 = "1";// 空闲
	public final static String status_2 = "2";// 预订中
	public final static String status_3 = "3";// 使用中

	private String status;

	@Column(name = "status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private String ssid;

	@Column(name = "ssid")
	@ExcelField(title = "SSID", align = 2, sort = 60)
	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	private String pwd;

	@Column(name = "pwd")
	@ExcelField(title = "PASSWORD", align = 2, sort = 70)
	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	private String bath;

	@Column(name = "bath")
	@ExcelField(title = "批次号", align = 2, sort = 80)
	public String getBath() {
		return bath;
	}

	public void setBath(String bath) {
		this.bath = bath;
	}

	private String uniqueNo;

	@Column(name = "unique_no")
	@ExcelField(title = "序列号", align = 2, sort = 90)
	public String getUniqueNo() {
		return uniqueNo;
	}

	public void setUniqueNo(String uniqueNo) {
		this.uniqueNo = uniqueNo;
	}

	private String supplier;

	@Column(name = "supplier")
	@ExcelField(title = "设备供应商", align = 2, sort = 100)
	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	private Date inTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "in_time")
	public Date getInTime() {
		return inTime;
	}

	public void setInTime(Date inTime) {
		this.inTime = inTime;
	}

	private String inUser;

	@Column(name = "in_user")
	public String getInUser() {
		return inUser;
	}

	public void setInUser(String inUser) {
		this.inUser = inUser;
	}

	private Date updateTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	private String updateUser;

	@Column(name = "update_user")
	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private String ownerMcc;			// 设备归属地

	/** 
	 * @return ownerMcc
	 */
	@Transient
	@ExcelField(title = "归属地", align = 2, sort = 110)
	public String getOwnerMcc() {
		return ownerMcc;
	}

	/** 
	 * @param ownerMcc
	 */
	public void setOwnerMcc(String ownerMcc) {
		this.ownerMcc = ownerMcc;
	}
	
	private String iccId;
	
	/** 
	 * @return iccId
	 */
	@Transient
	@ExcelField(title = "ICCID", align = 2, sort = 115)
	public String getIccId() {
		return iccId;
	}

	/** 
	 * @param iccId
	 */
	public void setIccId(String iccId) {
		this.iccId = iccId;
	}

	// ------------------- 设备版本信息  ------------------- // 
	private String 	MAIN_VERSION ;
	
	private String YYM_VERSION;
	
	private String YY_DAEMON_VERSION ;
	
	private String APN_3G_VERSION;

	private String APN_4G_VERSION;
	
	private String  COPS_CONF_VERSION ;
	
	private String	YY_UPDATE_VERSION ;

	/** 
	 * @return mAIN_VERSION
	 */
	@Transient
	@ExcelField(title = "MAIN_VERSION", align = 2, sort = 120)
	public String getMAIN_VERSION() {
		return MAIN_VERSION;
	}

	/** 
	 * @param mAIN_VERSION
	 */
	public void setMAIN_VERSION(String mAIN_VERSION) {
		MAIN_VERSION = mAIN_VERSION;
	}

	/** 
	 * @return yYM_VERSION
	 */
	@Transient
	@ExcelField(title = "YYM_VERSION", align = 2, sort = 130)
	public String getYYM_VERSION() {
		return YYM_VERSION;
	}

	/** 
	 * @param yYM_VERSION
	 */
	public void setYYM_VERSION(String yYM_VERSION) {
		YYM_VERSION = yYM_VERSION;
	}

	/** 
	 * @return yY_DAEMON_VERSION
	 */
	@Transient
	@ExcelField(title = "YY_DAEMON_VERSION", align = 2, sort = 140)
	public String getYY_DAEMON_VERSION() {
		return YY_DAEMON_VERSION;
	}

	/** 
	 * @param yY_DAEMON_VERSION
	 */
	public void setYY_DAEMON_VERSION(String yY_DAEMON_VERSION) {
		YY_DAEMON_VERSION = yY_DAEMON_VERSION;
	}

	/** 
	 * @return aPN_3G_VERSION
	 */
	@Transient
	@ExcelField(title = "APN_3G_VERSION", align = 2, sort = 150)
	public String getAPN_3G_VERSION() {
		return APN_3G_VERSION;
	}

	/** 
	 * @param aPN_3G_VERSION
	 */
	public void setAPN_3G_VERSION(String aPN_3G_VERSION) {
		APN_3G_VERSION = aPN_3G_VERSION;
	}

	/** 
	 * @return aPN_4G_VERSION
	 */
	@Transient
	@ExcelField(title = "APN_4G_VERSION", align = 2, sort = 160)
	public String getAPN_4G_VERSION() {
		return APN_4G_VERSION;
	}

	/** 
	 * @param aPN_4G_VERSION
	 */
	public void setAPN_4G_VERSION(String aPN_4G_VERSION) {
		APN_4G_VERSION = aPN_4G_VERSION;
	}

	/** 
	 * @return cOPS_CONF_VERSION
	 */
	@Transient
	@ExcelField(title = "COPS_CONF_VERSION", align = 2, sort = 170)
	public String getCOPS_CONF_VERSION() {
		return COPS_CONF_VERSION;
	}

	/** 
	 * @param cOPS_CONF_VERSION
	 */
	public void setCOPS_CONF_VERSION(String cOPS_CONF_VERSION) {
		COPS_CONF_VERSION = cOPS_CONF_VERSION;
	}

	/** 
	 * @return yY_UPDATE_VERSION
	 */
	@Transient
	@ExcelField(title = "YY_UPDATE_VERSION", align = 2, sort = 180)
	public String getYY_UPDATE_VERSION() {
		return YY_UPDATE_VERSION;
	}

	/** 
	 * @param yY_UPDATE_VERSION
	 */
	public void setYY_UPDATE_VERSION(String yY_UPDATE_VERSION) {
		YY_UPDATE_VERSION = yY_UPDATE_VERSION;
	}

	// ------------------- mifilist 需要修改字段  ------------------- // 
	private String testIp;							// 业务ip及端口
	
	private String testUpdateIp;					// 升级ip及端口
	
	private Integer softsimType;					// 是否软卡
	
	private Integer webPortalFlag;					// 是否弹窗

	/** 
	 * @return testIp
	 */
	@Transient
	public String getTestIp() {
		return testIp;
	}

	/** 
	 * @param testIp
	 */
	public void setTestIp(String testIp) {
		this.testIp = testIp;
	}

	/** 
	 * @return testUpdateIp
	 */
	@Transient
	public String getTestUpdateIp() {
		return testUpdateIp;
	}

	/** 
	 * @param testUpdateIp
	 */
	public void setTestUpdateIp(String testUpdateIp) {
		this.testUpdateIp = testUpdateIp;
	}

	/** 
	 * @return softsimType
	 */
	@Transient
	public Integer getSoftsimType() {
		return softsimType;
	}

	/** 
	 * @param softsimType
	 */
	public void setSoftsimType(Integer softsimType) {
		this.softsimType = softsimType;
	}

	/** 
	 * @return webPortalFlag
	 */
	@Transient
	public Integer getWebPortalFlag() {
		return webPortalFlag;
	}

	/** 
	 * @param webPortalFlag
	 */
	public void setWebPortalFlag(Integer webPortalFlag) {
		this.webPortalFlag = webPortalFlag;
	}
	
}
