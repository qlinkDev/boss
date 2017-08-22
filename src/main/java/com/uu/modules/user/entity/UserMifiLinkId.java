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
public class UserMifiLinkId extends BaseEntity<UserMifiLinkId>{
	
	private static final long serialVersionUID = 1599709416874617200L;
	private String userId;
	private String mifiId;
	

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getMifiId() {
		return mifiId;
	}
	public void setMifiId(String mifiId) {
		this.mifiId = mifiId;
	}
	
	@Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
     
    @Override
    public int hashCode() {
        return super.hashCode();
    }
	
}
