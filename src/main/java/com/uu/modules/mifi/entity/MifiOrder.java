package com.uu.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.uu.common.persistence.BaseEntity;
import com.uu.common.utils.excel.annotation.ExcelField;

/**
 * 订单信息
 * 
 * @author wangyong
 * @date 2016年1月21日
 */
@SuppressWarnings("rawtypes")
@Entity
@Table(name = "mifi_order")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MifiOrder extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	public MifiOrder(){
		this.stockStatus = stock_status_0;
	}

	private String orderId;// 内部订单编号
	
	@Id
	@Column(name = "order_id")
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public final static String order_status_0 = "0";// 已下单

	public final static String order_status_1 = "1";// 已发货
	
	public final static String order_status_3 = "3";// 已终止
	
	public final static String order_status_8 = "8";// 已完成

	public final static String order_status_9 = "9";// 已取消

	public final static String order_status_11 = "11";// 已删除

	public String orderStatus;// 订单状态

	@Column(name = "order_status")
	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String sourceType;// 渠道商

	@Column(name = "source_type")
	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	private String outOrderId;// 外部订单ID

	@Column(name = "out_order_id")
	@ExcelField(title = "订单总额" ,align =2,sort=30)
	public String getOutOrderId() {
		return outOrderId;
	}

	public void setOutOrderId(String outOrderId) {
		this.outOrderId = outOrderId;
	}

	private Date outOrderTime;// 订单时间

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "out_order_time")
	public Date getOutOrderTime() {
		return outOrderTime;
	}

	public void setOutOrderTime(Date outOrderTime) {
		this.outOrderTime = outOrderTime;
	}

	private String allowedMcc;// 限制使用国家范围

	@Column(name = "allowed_mcc")
	@ExcelField(title = "国家名称" ,align =2,sort=10)
	public String getAllowedMcc() {
		return allowedMcc;
	}

	public void setAllowedMcc(String allowedMcc) {
		this.allowedMcc = allowedMcc;
	}

	private String equipmentCnt;// 设备台数

	@Column(name = "equipment_cnt")
	@ExcelField(title = "订单总数" ,align =2,sort=40)
	public String getEquipmentCnt() {
		return equipmentCnt;
	}

	public void setEquipmentCnt(String equipmentCnt) {
		this.equipmentCnt = equipmentCnt;
	}

	private Date startDate;// 订单生效时间

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date")
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	private Date endDate;// 订单失效时间

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public static final String stock_status_0 = "0";//待备SIM卡
	
	public static final String stock_status_1 = "1";//已备SIM卡
	
	private String stockStatus;//备卡状态
	
	@Column(name = "stock_status")
	@ExcelField(title = "时间" ,align =2,sort=20)
	public String getStockStatus() {
		return stockStatus;
	}
	
	public void setStockStatus(String stockStatus) {
		this.stockStatus = stockStatus;
	}
	
	private String stockUser;//备卡用户
	
	@Column(name = "stock_user")
	public String getStockUser() {
		return stockUser;
	}
	
	public void setStockUser(String stockUser) {
		this.stockUser = stockUser;
	}
	
	private Date stockTime;//备卡时间

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "stock_time")
	public Date getStockTime() {
		return stockTime;
	}

	public void setStockTime(Date stockTime) {
		this.stockTime = stockTime;
	}
	
	private Date deliveryTime;// 发货时间

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "delivery_time")
	public Date getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(Date deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	private String delayRemark;// 续租备注

	@Column(name = "delay_remark")
	public String getDelayRemark() {
		return delayRemark;
	}

	public void setDelayRemark(String delayRemark) {
		this.delayRemark = delayRemark;
	}

	private Date delayTime;// 订单续租时间

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "delay_time")
	public Date getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(Date delayTime) {
		this.delayTime = delayTime;
	}

	private Date cancelTime;// 订单取消时间

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "cancel_time")
	public Date getCancelTime() {
		return cancelTime;
	}

	public void setCancelTime(Date cancelTime) {
		this.cancelTime = cancelTime;
	}
	
	private Date finishTime;// 订单完成时间

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "finish_time")
	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}
	
	private Date deleteTime;// 订单删除时间

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "delete_time")
	public Date getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(Date deleteTime) {
		this.deleteTime = deleteTime;
	}
	
	private int days;								// 行程天数
	
	private String referenceUnitPrice;				// 参考单价
	
	private String referenceTotalPrice;				// 参考总价（天数 * 参考单价 * 设备数）

	/** 
	 * @return days
	 */
	@Column(name = "days")
	public int getDays() {
		return days;
	}

	/** 
	 * @param days
	 */
	public void setDays(int days) {
		this.days = days;
	}

	/** 
	 * @return referenceUnitPrice
	 */
	@Column(name = "reference_unit_price")
	public String getReferenceUnitPrice() {
		return referenceUnitPrice;
	}

	/** 
	 * @param referenceUnitPrice
	 */
	public void setReferenceUnitPrice(String referenceUnitPrice) {
		this.referenceUnitPrice = referenceUnitPrice;
	}

	/** 
	 * @return referenceTotalPrice
	 */
	@Column(name = "reference_total_price")
	public String getReferenceTotalPrice() {
		return referenceTotalPrice;
	}

	/** 
	 * @param referenceTotalPrice
	 */
	public void setReferenceTotalPrice(String referenceTotalPrice) {
		this.referenceTotalPrice = referenceTotalPrice;
	}
	
	private String dsns;
	
	private String ssids;
	
	private String allowedMccCn;
	
	private String allowedMccEn;
	
	private String customerId;						// 订单客户ID
	
	private String limitSpeedFlag = "0";			// 订单速度标识(0_非低速,1_低速)
	
	private String orderType;						// 订单类型(0_默认订单,1_流量订单)
	
	private Integer flow;							// 订单流量

	/** 
	 * @return dsns
	 */
	@Transient
	public String getDsns() {
		return dsns;
	}

	/** 
	 * @param dsns
	 */
	public void setDsns(String dsns) {
		this.dsns = dsns;
	}

	/** 
	 * @return ssids
	 */
	@Transient
	public String getSsids() {
		return ssids;
	}

	/** 
	 * @param ssids
	 */
	public void setSsids(String ssids) {
		this.ssids = ssids;
	}

	/** 
	 * @return allowedMccCn
	 */
	@Transient
	public String getAllowedMccCn() {
		return allowedMccCn;
	}

	/** 
	 * @param allowedMccCn
	 */
	public void setAllowedMccCn(String allowedMccCn) {
		this.allowedMccCn = allowedMccCn;
	}

	/** 
	 * @return allowedMccEn
	 */
	@Transient
	public String getAllowedMccEn() {
		return allowedMccEn;
	}

	/** 
	 * @param allowedMccEn
	 */
	public void setAllowedMccEn(String allowedMccEn) {
		this.allowedMccEn = allowedMccEn;
	}

	/** 
	 * @return customerId
	 */
	public String getCustomerId() {
		return customerId;
	}

	/** 
	 * @param customerId
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	/** 
	 * @return limitSpeedFlag
	 */
	public String getLimitSpeedFlag() {
		return limitSpeedFlag;
	}

	/** 
	 * @param limitSpeedFlag
	 */
	public void setLimitSpeedFlag(String limitSpeedFlag) {
		this.limitSpeedFlag = limitSpeedFlag;
	}

	/** 
	 * @return orderType
	 */
	public String getOrderType() {
		return orderType;
	}

	/** 
	 * @param orderType
	 */
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	/** 
	 * @return flow
	 */
	public Integer getFlow() {
		return flow;
	}

	/** 
	 * @param flow
	 */
	public void setFlow(Integer flow) {
		this.flow = flow;
	}

	public static final String ORDER_TYPE_DEFAULT = "0";
	public static final String ORDER_TYPE_FLOW = "1";
	
	
}
