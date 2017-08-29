package main.java.com.qlink.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.uu.common.persistence.BaseEntity;
@Entity
@Table(name = "mifiversion")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MifiVersion extends BaseEntity<MifiVersion> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id; 							// 唯一标识
	
	private String	IMEI_6200;
	
	private String 	MAIN_VERSION ;
	
	private String YYM_VERSION;
	
	private String YY_DAEMON_VERSION ;
	
	private String APN_3G_VERSION;

	private String APN_4G_VERSION;
	
	private String  COPS_CONF_VERSION ;
	
	private String	YY_UPDATE_VERSION ;
	
	private String  UPDATEFLAG;
	
	private Date stamp_created ;
	
	private Date stamp_update ;
	
	private String SPEEDLIMITFLAG;
	
	private String Lcd_version_Type;
	
	private String	Log_file_Type;
	
	private String speedlimitType;
	
	@Id
	@Column(name = "ID")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIMEI_6200() {
		return IMEI_6200;
	}

	public void setIMEI_6200(String iMEI_6200) {
		IMEI_6200 = iMEI_6200;
	}

	public String getMAIN_VERSION() {
		return MAIN_VERSION;
	}

	public void setMAIN_VERSION(String mAIN_VERSION) {
		MAIN_VERSION = mAIN_VERSION;
	}

	public String getYYM_VERSION() {
		return YYM_VERSION;
	}

	public void setYYM_VERSION(String yYM_VERSION) {
		YYM_VERSION = yYM_VERSION;
	}

	public String getYY_DAEMON_VERSION() {
		return YY_DAEMON_VERSION;
	}

	public void setYY_DAEMON_VERSION(String yY_DAEMON_VERSION) {
		YY_DAEMON_VERSION = yY_DAEMON_VERSION;
	}

	public String getAPN_3G_VERSION() {
		return APN_3G_VERSION;
	}

	public void setAPN_3G_VERSION(String aPN_3G_VERSION) {
		APN_3G_VERSION = aPN_3G_VERSION;
	}

	public String getAPN_4G_VERSION() {
		return APN_4G_VERSION;
	}

	public void setAPN_4G_VERSION(String aPN_4G_VERSION) {
		APN_4G_VERSION = aPN_4G_VERSION;
	}

	public String getCOPS_CONF_VERSION() {
		return COPS_CONF_VERSION;
	}

	public void setCOPS_CONF_VERSION(String cOPS_CONF_VERSION) {
		COPS_CONF_VERSION = cOPS_CONF_VERSION;
	}

	public String getYY_UPDATE_VERSION() {
		return YY_UPDATE_VERSION;
	}

	public void setYY_UPDATE_VERSION(String yY_UPDATE_VERSION) {
		YY_UPDATE_VERSION = yY_UPDATE_VERSION;
	}

	public String getUPDATEFLAG() {
		return UPDATEFLAG;
	}

	public void setUPDATEFLAG(String uPDATEFLAG) {
		UPDATEFLAG = uPDATEFLAG;
	}

	public Date getStamp_created() {
		return stamp_created;
	}

	public void setStamp_created(Date stamp_created) {
		this.stamp_created = stamp_created;
	}

	public Date getStamp_update() {
		return stamp_update;
	}

	public void setStamp_update(Date stamp_update) {
		this.stamp_update = stamp_update;
	}

	public String getSPEEDLIMITFLAG() {
		return SPEEDLIMITFLAG;
	}

	public void setSPEEDLIMITFLAG(String sPEEDLIMITFLAG) {
		SPEEDLIMITFLAG = sPEEDLIMITFLAG;
	}

	public String getLcd_version_Type() {
		return Lcd_version_Type;
	}

	public void setLcd_version_Type(String lcd_version_Type) {
		Lcd_version_Type = lcd_version_Type;
	}

	public String getLog_file_Type() {
		return Log_file_Type;
	}

	public void setLog_file_Type(String log_file_Type) {
		Log_file_Type = log_file_Type;
	}

	public String getSpeedlimitType() {
		return speedlimitType;
	}

	public void setSpeedlimitType(String speedlimitType) {
		this.speedlimitType = speedlimitType;
	}

}	
