package main.java.com.qlink.modules.user.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.uu.common.persistence.BaseEntity;
import com.uu.common.utils.IdGen;

/**
 * @author jiangbo
 * @date 2016年3月22日
 */
@Entity
@Table(name = "user_basic_info")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserBasicInfo extends BaseEntity<UserBasicInfo>{
	
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String sourceType;
	private String userId;
	private String userName;
	private Integer dayPass;
	private String phone;
	private String email;
	private String balance;
	private String totalDeposit;
	private Date createTime;
	private Date updateTime;
	
	public UserBasicInfo() {
		super();
		id = IdGen.uuid();
		createTime = new Date();
	}
	
	/** 
	 * @return id
	 */
	@Id
	@Column(name = "id")
	public String getId() {
		return id;
	}
	/** 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	@Column(name = "source_type")
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
	
	@Column(name = "user_id")
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@Column(name = "balance")
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	
	
	@Column(name = "user_name")
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Column(name = "email")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(name = "total_deposit")
	public String getTotalDeposit() {
		return totalDeposit;
	}
	public void setTotalDeposit(String totalDeposit) {
		this.totalDeposit = totalDeposit;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time")
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	/** 
	 * @return phone
	 */
	@Column(name = "phone")
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
	 * @return dayPass
	 */
	@Column(name = "day_pass")
	public Integer getDayPass() {
		return dayPass;
	}

	/** 
	 * @param dayPass
	 */
	public void setDayPass(Integer dayPass) {
		this.dayPass = dayPass;
	}
	
}
