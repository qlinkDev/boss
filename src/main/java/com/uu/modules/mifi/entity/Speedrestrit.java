package com.uu.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.uu.common.persistence.BaseEntity;

/**
 * 限速管理  
 * @Description 
 * @author wangsai
 * @date 2016年11月2日 上午9:44:41
 */
@Entity
@Table(name = "speedrestrit")
public class Speedrestrit extends BaseEntity<Speedrestrit> {
	
	private static final long serialVersionUID = 1L;

	private String  id;
	private String	speedrestritmcc;
	private String 	countryName;
	private String	firstleveldata;
	private String	firstlevelspeed;
	private String	secondleveldata;
	private String	secondlevelspeed;
	private Date 	stampCreated;    //创建时间
	private Date	stampUpdate;     //修改时间
	private String ownerMcc;
	private String sourceType;	
	
	@Id
	@Column(name = "ID")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Column(name = "SPEEDRESTRITMCC")
	public String getSpeedrestritmcc() {
		return speedrestritmcc;
	}
	public void setSpeedrestritmcc(String speedrestritmcc) {
		this.speedrestritmcc = speedrestritmcc;
	}
	@Column(name = "FIRSTLEVELDATA")
	public String getFirstleveldata() {
		return firstleveldata;
	}
	public void setFirstleveldata(String firstleveldata) {
		this.firstleveldata = firstleveldata;
	}
	@Column(name = "FIRSTLEVELSPEED")
	public String getFirstlevelspeed() {
		return firstlevelspeed;
	}
	public void setFirstlevelspeed(String firstlevelspeed) {
		this.firstlevelspeed = firstlevelspeed;
	}
	@Column(name = "SECONDLEVELDATA")
	public String getSecondleveldata() {
		return secondleveldata;
	}
	public void setSecondleveldata(String secondleveldata) {
		this.secondleveldata = secondleveldata;
	}
	@Column(name = "SECONDLEVELSPEED")
	public String getSecondlevelspeed() {
		return secondlevelspeed;
	}
	public void setSecondlevelspeed(String secondlevelspeed) {
		this.secondlevelspeed = secondlevelspeed;
	}
	@Column(name = "STAMP_CREATED")
	public Date getStampCreated() {
		return stampCreated;
	}
	public void setStampCreated(Date stampCreated) {
		this.stampCreated = stampCreated;
	}
	@Column(name = "STAMP_UPDATE")
	public Date getStampUpdate() {
		return stampUpdate;
	}
	public void setStampUpdate(Date stampUpdate) {
		this.stampUpdate = stampUpdate;
	}
	/** 
	 * @return countryName
	 */
	@Column(name = "COUNTRYNAME")
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
	 * @return ownerMcc
	 */
	@Column(name = "OWNER_MCC")
	public String getOwnerMcc() {
		return ownerMcc;
	}
	/** 
	 * @param ownerMcc
	 */
	public void setOwnerMcc(String ownerMcc) {
		this.ownerMcc = ownerMcc;
	}
	/** 
	 * @return sourceType
	 */
	@Column(name = "source_type")
	public String getSourceType() {
		return sourceType;
	}
	/** 
	 * @param sourceType
	 */
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	
}
