/** 
 * @Package com.uu.modules.om.entity 
 * @Description 
 * @author yifang.huang
 * @date 2016-3-11 下午5:03:42 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.om.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.validator.constraints.Length;

import com.uu.common.persistence.IdEntity;
import com.uu.common.utils.excel.annotation.ExcelField;

/** 
 * @Description 消费记录
 * @author yifang.huang
 * @date 2016-3-11 下午5:03:42 
 */
@Entity
@Table(name = "om_consume_record")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ConsumeRecord extends IdEntity<ConsumeRecord> {
	
	private static final long serialVersionUID = 6415032041317531168L;

	private String userId;										// 用户ID
	
	private String phone;										// 手机号码
	
	private String targetId;									// 目标ID（充值订单ID，购物订单ID等）
	
	private String targetName;									// 目标名称
	
	private String sourceType;									// 来源（所属平台）
	
	private RecordType recordType;								// 消费记录类型
	
	private Status status = Status.NEW;							// 消费记录状态	
	
	private Double money = 0.00;								// 实际交易金额
	
	private String paymentNo="";								// 支付流水号
	
	private Date paymentDate; 									// 买家付款时间
	
	private String ip;											// 写入数据的服务器IP
	
	private Channel channel;								    // 渠道ID

	private String balanceStatus;								// 结算状态
	
	private Date createDateForExcel;                            // 创建日期(excel导出使用)
	
	private String countryCode;									// 国家编号
	
	private String countryName;									// 国家名称
	
	private String mcc;											// mcc（冗余字段，目前同一国家一天只扣费一次）
	
	private Date localDate;										// 用户消费当地时间
	
	private String sn;											// 设备编号
	
	private String ssid;
	
	private String returnUri;									// 支付成功跳转地址
	
	private Integer days;										// 开机确认天数					
	
	public static enum Status { 
		NEW, COMPLETED, OVERDUE; 
        public String getName(){
        	if(this.toString().equals("NEW")) {
				return "新建";
			}else if(this.toString().equals("COMPLETED")){
				return "已完成";
			}else if(this.toString().equals("OVERDUE")){
				return "已过期";
			}else{
				return "未知";
			}
        }
	}
	
	public static enum RecordType { 
		RECHARGE, BUY, OTHER; 
        public String getName(){
        	if(this.toString().equals("RECHARGE")) {
				return "充值";
			}else if(this.toString().equals("BUY")){
				return "购物";
			}else if(this.toString().equals("OTHER")){
				return "其它";
			}else{
				return "未知";
			}
        }
	}

	/**
	 * 
	 * <p>Title: </p> 
	 * <p>Description: </p>
	 */
	public ConsumeRecord() {
		super();
	}
	
	/**
	 * 
	 * <p>Title: </p> 
	 * <p>Description: </p> 
	 * @param id
	 */
	public ConsumeRecord(String id) {
		this();
		this.id = id;
	}

	/** 
	 * @return userId
	 */
	@Length(min=1, max=32, message="用户ID长度为1~32")
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
	 * @return phone
	 */
	public String getPhone() {
		return phone;
	}

	/** 
	 * @param phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/** 
	 * @return targetId
	 */
	@Length(min=32, max=32, message="操作目标ID长度为32")
	public String getTargetId() {
		return targetId;
	}

	/** 
	 * @param targetId
	 */
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	/** 
	 * @return targetName
	 */
	@Length(min=0, max=500, message="操作目标名称长度在0~500之间")
	@ExcelField(title="商品名称", align=2, sort=10)
	public String getTargetName() {
		return targetName;
	}

	/** 
	 * @param targetName
	 */
	public void setTargetName(String targetName) {
		this.targetName = targetName;
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
	 * @return recordType
	 */
	@Enumerated(EnumType.STRING)
	@ExcelField(title="类型", align=2, sort=30, dictType="record_type")
	public RecordType getRecordType() {
		return recordType;
	}

	/** 
	 * @param recordType
	 */
	public void setRecordType(RecordType recordType) {
		this.recordType = recordType;
	}

	/** 
	 * @return status
	 */
	@Enumerated(EnumType.STRING)
	@ExcelField(title="订单状态", align=2, sort=40, dictType="consume_order_status")
	public Status getStatus() {
		return status;
	}

	/** 
	 * @param status
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/** 
	 * @return money
	 */
	@ExcelField(title="金额", align=2, sort=50)
	public Double getMoney() {
		return money;
	}

	/** 
	 * @param money
	 */
	public void setMoney(Double money) {
		this.money = money;
	}

	/** 
	 * @return paymentNo
	 */
	public String getPaymentNo() {
		return paymentNo;
	}

	/** 
	 * @param paymentNo
	 */
	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}

	/** 
	 * @return paymentDate
	 */
	public Date getPaymentDate() {
		return paymentDate;
	}

	/** 
	 * @param paymentDate
	 */
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	/** 
	 * @return ip
	 */
	public String getIp() {
		return ip;
	}

	/** 
	 * @param ip
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/** 
	 * @return channel
	 */
	@ManyToOne
	@JoinColumn(name="channel_id")
	@NotFound(action = NotFoundAction.IGNORE)
	@ExcelField(title="渠道名称", align=2, sort=20, value="channel.channelName")
	public Channel getChannel() {
		return channel;
	}

	/** 
	 * @param channel
	 */
	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	@ExcelField(title="结算状态", align=2, sort=70, dictType="balance_status")
	public String getBalanceStatus() {
		return balanceStatus;
	}

	public void setBalanceStatus(String balanceStatus) {
		this.balanceStatus = balanceStatus;
	}

	@Transient
	@ExcelField(title="日期", align=2, sort=60)
	public Date getCreateDateForExcel() {
		return createDateForExcel;
	}

	public void setCreateDateForExcel(Date createDateForExcel) {
		this.createDateForExcel = createDateForExcel;
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
	 * @return countryName
	 */
	@ExcelField(title="国家", align=2, sort=25)
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
	 * @return localDate
	 */
	public Date getLocalDate() {
		return localDate;
	}

	/** 
	 * @param localDate
	 */
	public void setLocalDate(Date localDate) {
		this.localDate = localDate;
	}

	/** 
	 * @return sn
	 */
	@ExcelField(title="设备编号", align=2, sort=15)
	public String getSn() {
		return sn;
	}

	/** 
	 * @param sn
	 */
	public void setSn(String sn) {
		this.sn = sn;
	}

	/** 
	 * @return ssid
	 */
	@Column(name = "ssid")
	@ExcelField(title = "SSID", align = 2, sort = 17)
	public String getSsid() {
		return ssid;
	}

	/** 
	 * @param ssid
	 */
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	/** 
	 * @return returnUri
	 */
	public String getReturnUri() {
		return returnUri;
	}

	/** 
	 * @param returnUri
	 */
	public void setReturnUri(String returnUri) {
		this.returnUri = returnUri;
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

}
