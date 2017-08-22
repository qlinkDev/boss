/** 
 * @Package com.uu.modules.mifi.entity 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月8日 上午11:21:47 
 * @version V1.0 
 */
package com.uu.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
 * @Description 测试设备 实体类
 * @author yifang.huang
 * @date 2016年4月8日 上午11:21:47
 */
@Entity
@Table(name = "mifi_test_device")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TestDevice extends IdEntity<TestDevice> {

	private static final long serialVersionUID = 6966755443035750180L;

	private String imei; 							// 设备编号

	private String lendUserName;					// 借出人姓名

	private Date lendDate = new Date();				// 借出时间

	private String returnUserName;					// 归还人姓名

	private Date returnDate;						// 归还时间

	private Status status = Status.LEND;			// 状态

	public static enum Status {
		LEND, RETURN;
		public String getName() {
			if (this.toString().equals("LEND")) {
				return "借出";
			} else if (this.toString().equals("RETURN")) {
				return "归还";
			} else {
				return "未知";
			}
		}
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
	 * @return lendUserName
	 */
	@Column(name = "lend_user_name")
	@NotBlank(message = "借出人姓名不能为空")
	@Length(min=1, max=50, message="借出人姓名长度在1~50之间")
	public String getLendUserName() {
		return lendUserName;
	}

	/** 
	 * @param lendUserName
	 */
	public void setLendUserName(String lendUserName) {
		this.lendUserName = lendUserName;
	}

	/** 
	 * @return lendDate
	 */
	@Column(name = "lend_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLendDate() {
		return lendDate;
	}

	/** 
	 * @param lendDate
	 */
	public void setLendDate(Date lendDate) {
		this.lendDate = lendDate;
	}

	/** 
	 * @return returnUserName
	 */
	@Column(name = "return_user_name")
	@Length(min=1, max=50, message="归还人姓名长度在1~50之间")
	public String getReturnUserName() {
		return returnUserName;
	}

	/** 
	 * @param returnUserName
	 */
	public void setReturnUserName(String returnUserName) {
		this.returnUserName = returnUserName;
	}

	/** 
	 * @return returnDate
	 */
	@Column(name = "return_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getReturnDate() {
		return returnDate;
	}

	/** 
	 * @param returnDate
	 */
	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	/** 
	 * @return status
	 */
	@Enumerated(EnumType.STRING)
	public Status getStatus() {
		return status;
	}

	/** 
	 * @param status
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

}
