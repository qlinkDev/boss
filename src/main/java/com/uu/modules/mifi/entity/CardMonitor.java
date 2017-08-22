/** 
 * @Package com.uu.modules.mifi.entity 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月15日 上午9:56:40 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.uu.common.persistence.IdEntity;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.StringUtils;
import com.uu.common.utils.excel.annotation.ExcelField;
import com.uu.modules.sys.entity.User;

/** 
 * @Description 卡监控信息 实体类
 * @author yifang.huang
 * @date 2016年4月15日 上午9:56:40 
 */
@Entity
@Table(name = "mifi_card_monitor")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CardMonitor extends IdEntity<CardMonitor> {

	private static final long serialVersionUID = -8033180166255788355L;
	
	private String imei;							// 设备编号
	
	private String sourceType;						// 所属渠道
	
	private String mcc;								// mcc
	
	private String countryCode; 					// 国家编号
	
	private String countryName;						// 国家名称
	
	private String status = "NEW";					// 状态（NEW-新建，SENT_MSG-已发信息，HANDLED-已处理）
	
	private String faultCode;						// 故障编码（F3_无卡，F8_未入库，F9_设备不可接入，SimbankClose_卡箱与服务器断开连接，DPOHC_设备在home国家开机）
	
	private User handleBy;							// 处理人
	
	private Date handleDate;						// 处理时间
	
	private String type;							// 类型（0_MIFI[设备]， 1_SIMBANK[卡箱]）

	/** 
	 * @return imei
	 */
	@Column(name = "imei")
	@NotBlank(message = "设备编号不能为空")
	@Length(min=1, max=32, message="设备编号长度在1~32之间")
	@ExcelField(title="设备编号", align=2, sort=20)
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
	 * @return mcc
	 */
	@Column(name = "mcc")
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
	@Column(name = "country_code")
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
	 * @return status
	 */
	@Column(name = "status")
	@ExcelField(title="状态", align=2, sort=40, dictType="card_monitor_status")
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
	 * @return faultCode
	 */
	@Column(name = "fault_code")
	@ExcelField(title="故障", align=2, sort=60, dictType="card_monitor_fault_code")
	public String getFaultCode() {
		return faultCode;
	}

	/** 
	 * @param faultCode
	 */
	public void setFaultCode(String faultCode) {
		this.faultCode = faultCode;
	}

	/** 
	 * @return handleBy
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="handle_by")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getHandleBy() {
		return handleBy;
	}

	/** 
	 * @param handleBy
	 */
	public void setHandleBy(User handleBy) {
		this.handleBy = handleBy;
	}

	/** 
	 * @return handleDate
	 */
	@Column(name = "handle_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getHandleDate() {
		return handleDate;
	}

	/** 
	 * @param handleDate
	 */
	public void setHandleDate(Date handleDate) {
		this.handleDate = handleDate;
	}

	/** 
	 * @return type
	 */
	@ExcelField(title="类型", align=2, sort=50, dictType="card_monitor_type")
	public String getType() {
		return type;
	}

	/** 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	@ExcelField(title="国家", align=2, sort=30)
	@Transient
	public String getMccFormatted() {
		if(StringUtils.isNotBlank(countryName)){
			return countryName;
		}
		return mcc;
	}

	@ExcelField(title="发生时间", align=2, sort=70)
	@Transient
	public String getCreateDateStr() {
		return DateUtils.formatDateTime(createDate);
	}
	
	@ExcelField(title="处理人", align=2, sort=80)
	@Transient
	public String getHandlerName() {
		if(null == handleBy){
			return StringUtils.EMPTY;
		}
		return StringUtils.emptyIfNull(handleBy.getName());
	}
	
	@ExcelField(title="处理时间", align=2, sort=90)
	@Transient
	public String getHandleDateStr() {
		if(null == handleDate){
			return StringUtils.EMPTY;
		}
		return DateUtils.formatDateTime(handleDate);
	}
	
	@ExcelField(title="说明", align=2, sort=100)
	@Transient
	public String getRemarksExport() {
		return StringUtils.emptyIfNull(remarks);
	}
}
