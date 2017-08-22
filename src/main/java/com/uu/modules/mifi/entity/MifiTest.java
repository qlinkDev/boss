/** 
 * @Package com.uu.modules.mifi.entity 
 * @Description 
 * @author yifang.huang
 * @date 2016年5月24日 下午4:17:17 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.uu.common.persistence.BaseEntity;

/** 
 * @Description 测试设备与卡绑定
 * @author yifang.huang
 * @date 2016年5月24日 下午4:17:17 
 */
@Entity
@Table(name = "mifitest")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MifiTest extends BaseEntity<MifiTest> {

	private static final long serialVersionUID = 6966755443035750180L;
	
	private Integer id; 							// 唯一标识
	
	private String imei;							// 设备编号
	
	private String simBankId;						// 卡槽编号
	
	private String simId;							// 卡槽位置
	
	private Date startDate;							// 生效时间
	
	private Date endDate;							// 失效时间
	
	private Date createDate;						// 创建时间
	
	private String createBy;						// 创建者
		
	private String remarks;							// 备注信息
	
	/** 
	 * @return id
	 */
	@Id
	@Column(name = "ID")
	public Integer getId() {
		return id;
	}

	/** 
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/** 
	 * @return imei
	 */
	@Column(name = "IMEI")
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
	 * @return simBankId
	 */
	@Column(name = "SIMBANKID")
	public String getSimBankId() {
		return simBankId;
	}

	/** 
	 * @param simBankId
	 */
	public void setSimBankId(String simBankId) {
		this.simBankId = simBankId;
	}

	/** 
	 * @return simId
	 */
	@Column(name = "SIMID")
	public String getSimId() {
		return simId;
	}

	/** 
	 * @param simId
	 */
	public void setSimId(String simId) {
		this.simId = simId;
	}

	/** 
	 * @return startDate
	 */
	@Column(name = "start_date")
	public Date getStartDate() {
		return startDate;
	}

	/** 
	 * @param startDate
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/** 
	 * @return endDate
	 */
	@Column(name = "end_date")
	public Date getEndDate() {
		return endDate;
	}

	/** 
	 * @param endDate
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/** 
	 * @return createDate
	 */
	@Column(name = "stamp_created")
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
	 * @return createBy
	 */
	@Column(name = "create_by")
	public String getCreateBy() {
		return createBy;
	}

	/** 
	 * @param createBy
	 */
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	/** 
	 * @return remarks
	 */
	@Column(name = "remarks")
	public String getRemarks() {
		return remarks;
	}

	/** 
	 * @param remarks
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
