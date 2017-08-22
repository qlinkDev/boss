package com.uu.modules.user.entity;

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

/**
 * @author jiangbo
 * @date 2016年3月22日
 */
@Entity
@Table(name = "user_mifi_link")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserMifiLink extends BaseEntity<UserMifiLink>{
	
	private static final long serialVersionUID = 1L;
	
	private String userId;
	private String mifiId;
	private Date linkTime;

	@Id
	@Column(name = "user_id")
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@Id
	@Column(name = "mifi_id")
	public String getMifiId() {
		return mifiId;
	}
	public void setMifiId(String mifiId) {
		this.mifiId = mifiId;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "link_time")
	public Date getLinkTime() {
		return linkTime;
	}
	public void setLinkTime(Date linkTime) {
		this.linkTime = linkTime;
	}
	
}
