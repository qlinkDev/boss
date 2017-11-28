package com.qlink.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.qlink.common.persistence.BaseEntity;
import com.qlink.common.utils.IdGen;
import com.qlink.common.utils.excel.annotation.ExcelField;

/**
 * @author wangyong
 * @date 2016年1月25日
 */
@SuppressWarnings("rawtypes")
@Entity
@Table(name = "card_basic_info")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CardBasicInfo extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	public CardBasicInfo() {
		id = IdGen.uuid();// 生成新的唯一序列
		status = status_1;// 卡状态
		createTime = new Date();//入库时间
		//createUser = UserUtils.getUser().getLoginName();//入库操作员
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
	@NotNull(message = "卡号不能为空")
	@ExcelField(title = "卡号", align = 2, sort = 10)
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	private String imsi;

	@Column(name = "imsi")
	@ExcelField(title = "imsi", align = 2, sort = 20)
	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	private String iccid;

	@Column(name = "iccid")
	@ExcelField(title = "iccid", align = 2, sort = 25)
	public String getIccid() {
		return iccid;
	}

	public void setIccid(String iccid) {
		this.iccid = iccid;
	}

	private String pin;

	@Column(name = "pin")
	@ExcelField(title = "pin", align = 2, sort = 30)
	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	private String puk;

	@Column(name = "puk")
	@ExcelField(title = "puk", align = 2, sort = 40)
	public String getPuk() {
		return puk;
	}

	public void setPuk(String puk) {
		this.puk = puk;
	}

	private String simNo;

	@Column(name = "sim_no")
	public String getSimNo() {
		return simNo;
	}

	public void setSimNo(String simNo) {
		this.simNo = simNo;
	}

	private String place;

	@Column(name = "place")
	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}
	
	public final static String status_1 = "1";//空闲
	public final static String status_2 = "2";//预订中
	public final static String status_3 = "3";//使用中

	private String status;

	@Column(name = "status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private String type;

	@Column(name = "type")
	//@NotNull(message = "卡类型不能为空")
	@ExcelField(title = "卡类型", align = 2, sort = 5)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	private Date activeTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "active_time")
	@ExcelField(title = "激活时间(YYYY/MM/DD)", align = 2, sort = 8)
	public Date getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(Date activeTime) {
		this.activeTime = activeTime;
	}
	
	private Date clearTime;				// 上次流量清零时间

	/** 
	 * @return clearTime
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "clear_time")
	public Date getClearTime() {
		return clearTime;
	}

	/** 
	 * @param clearTime
	 */
	public void setClearTime(Date clearTime) {
		this.clearTime = clearTime;
	}

	private String delete;

	@Column(name = "`delete`")
	public String getDelete() {
		return delete;
	}

	public void setDelete(String delete) {
		this.delete = delete;
	}
	
	private String active;

	@Column(name = "active")
	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}
	
	private String bath;
	
	@Column(name="bath")
	@ExcelField(title = "批次号", align = 2, sort = 80)
	public String getBath(){
		return bath;
	}
	
	public void setBath(String bath){
		this.bath = bath;
	}
	
	private String supplier;
	
	@Column(name="supplier")
	@ExcelField(title = "供应商", align = 2, sort = 90)
	public String getSupplier(){
		return supplier;
	}
	
	public void setSupplier(String supplier){
		this.supplier = supplier;
	}

	private String createUser;

	@Column(name = "create_user")
	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	private Date createTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	@ExcelField(title = "入库时间", align = 2, sort = 100)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	private String updateUser;

	@Column(name = "update_user")
	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
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

	private String dataCap;					// 充值的流量（添加到simnode）

	/** 
	 * @return dataCap
	 */
	@Transient
	public String getDataCap() {
		return dataCap;
	}

	/** 
	 * @param dataCap
	 */
	public void setDataCap(String dataCap) {
		this.dataCap = dataCap;
	}

	private String inBank = "1";					// 是否在卡槽上(1_是)
	private String createDateStart;					// 入库时间开始
	private String createDateEnd;					// 入库时间结束
	
	/** 
	 * @return inBank
	 */
	@Transient
	public String getInBank() {
		return inBank;
	}

	/** 
	 * @param inBank
	 */
	public void setInBank(String inBank) {
		this.inBank = inBank;
	}

	/** 
	 * @return createDateStart
	 */
	@Transient
	public String getCreateDateStart() {
		return createDateStart;
	}

	/** 
	 * @param createDateStart
	 */
	public void setCreateDateStart(String createDateStart) {
		this.createDateStart = createDateStart;
	}

	/** 
	 * @return createDateEnd
	 */
	@Transient
	public String getCreateDateEnd() {
		return createDateEnd;
	}

	/** 
	 * @param createDateEnd
	 */
	public void setCreateDateEnd(String createDateEnd) {
		this.createDateEnd = createDateEnd;
	}

	private String sourceType;					// 卡所属渠道

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
