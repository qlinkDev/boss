/** 
 * @Package com.uu.modules.user.entity 
 * @Description 
 * @author yifang.huang
 * @date 2016年12月6日 上午11:11:20 
 * @version V1.0 
 */ 
package com.uu.modules.user.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.uu.common.persistence.BaseEntity;
import com.uu.common.utils.IdGen;

/** 
 * @Description 用户开通天数记录(增加减少) 实体类
 * @author yifang.huang
 * @date 2016年12月6日 上午11:11:20 
 */
@Entity
@Table(name = "day_pass_record")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DayPassRecord extends BaseEntity<DayPassRecord> {

	private static final long serialVersionUID = -5079693422358759445L;

	private String id; 								// 唯一标识
	
	private String userId;							// 记录对应用户ID
	
	private String loginName;						// 记录对应用户登录名
	
	private String orderId;							// 记录对应的订单ID
	
	private String imei;	 						// 设备编号
	
	private String mcc;								// 使用地mcc
	
	private String countryCode;						// 使用国家编号
	
	private String countryNameCn;					// 使用国家中文名
	
	private String countryNameEn;					// 使用国家英文名		
	
	private String sourceType;				 		// 设备所属渠道
	
	private String type;							// 记录类型（充值_RECHARGE,消费_CONSUME）
	
	private Integer days;							// 天数
	
	private String status;							// 状态[如果是用户消费需要通知渠道商，些字段用于记录通知是否成功]（成功_SUCCESS,失败_FAIL）
	
	private Date createDate; 						// 开机时间
	
	private Date callbackDate;						// 最后一次回调时间
	
	private String remarks;							// 备注

	public DayPassRecord() {
		super();
		id = IdGen.uuid();
		createDate = new Date();
	}

	/** 
	 * @return id
	 */
	@Id
	public String getId() {
		return id;
	}

	/** 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/** 
	 * @return userId
	 */
	public String getUserId() {
		return userId;
	}

	/** 
	 * @param userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/** 
	 * @return loginName
	 */
	public String getLoginName() {
		return loginName;
	}

	/** 
	 * @param loginName
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/** 
	 * @return orderId
	 */
	public String getOrderId() {
		return orderId;
	}

	/** 
	 * @param orderId
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	/** 
	 * @return imei
	 */
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
	 * @return mcc
	 */
	public String getMcc() {
		return mcc;
	}

	/** 
	 * @param mcc
	 */
	public void setMcc(String mcc) {
		this.mcc = mcc;
	}

	/** 
	 * @return countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/** 
	 * @param countryCode
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/** 
	 * @return countryNameCn
	 */
	public String getCountryNameCn() {
		return countryNameCn;
	}

	/** 
	 * @param countryNameCn
	 */
	public void setCountryNameCn(String countryNameCn) {
		this.countryNameCn = countryNameCn;
	}

	/** 
	 * @return countryNameEn
	 */
	public String getCountryNameEn() {
		return countryNameEn;
	}

	/** 
	 * @param countryNameEn
	 */
	public void setCountryNameEn(String countryNameEn) {
		this.countryNameEn = countryNameEn;
	}

	/** 
	 * @return sourceType
	 */
	public String getSourceType() {
		return sourceType;
	}

	/** 
	 * @param sourceType
	 */
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	/** 
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/** 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/** 
	 * @return days
	 */
	public Integer getDays() {
		return days;
	}

	/** 
	 * @param days
	 */
	public void setDays(Integer days) {
		this.days = days;
	}

	/** 
	 * @return status
	 */
	public String getStatus() {
		return status;
	}

	/** 
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/** 
	 * @return createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/** 
	 * @param createDate
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/** 
	 * @return callbackDate
	 */
	public Date getCallbackDate() {
		return callbackDate;
	}

	/** 
	 * @param callbackDate
	 */
	public void setCallbackDate(Date callbackDate) {
		this.callbackDate = callbackDate;
	}

	/** 
	 * @return remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/** 
	 * @param remarks
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	/**
	 * 用户开通天数记录(增加减少)类型
	 */
	public static final String DAY_PASS_RECORD_RECHARGE = "RECHARGE";	// 充值
	public static final String DAY_PASS_RECORD_CONSUME = "CONSUME";		// 消费
}
