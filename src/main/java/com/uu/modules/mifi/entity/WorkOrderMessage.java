package com.uu.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import com.uu.common.persistence.BaseEntity;
import com.uu.common.utils.IdGen;

/**
 * 工单会话信息实体
 * 
 * @author shuxin
 * @date 2016年5月31日
 */
@Entity
@Table(name = "work_order_message")
@DynamicInsert
@DynamicUpdate
public class WorkOrderMessage extends BaseEntity<WorkOrderMessage> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1819682774962720467L;

	private String id;

	/**
	 * 工单ID
	 */
	private Integer wid;

	/**
	 * 消息类型：1客户，2同事
	 */
	private Integer messageType;

	/** 消息内容 */
	private String content;

	/** 消息时间 */
	private Date createTime;

	/**
	 * 是否读过 0 否，1 是
	 */
	private Integer isRead;

	/** 创建者ID */
	private Integer uid;

	/** 创建者 */
	private String userName;

	/** 附件路径 */
	private String attachPath;
	
	public WorkOrderMessage() {
		super();
	}
	

	public WorkOrderMessage(Integer wid, Integer messageType, String content, Date createTime, Integer isRead,
			Integer uid, String userName, String attachPath) {
		super();
		this.wid = wid;
		this.messageType = messageType;
		this.content = content;
		this.createTime = createTime;
		this.isRead = isRead;
		this.uid = uid;
		this.userName = userName;
		this.attachPath = attachPath;
	}
	
	@PrePersist
	public void prePersist(){
		this.id = IdGen.uuid();
	}

	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Column(name = "wid")
	@NotNull(message = "工单编号不能为空")
	public Integer getWid() {
		return wid;
	}

	public void setWid(Integer wid) {
		this.wid = wid;
	}

	@Column(name = "message_type")
	@NotNull(message = "消息类型不能为空")
	public Integer getMessageType() {
		return messageType;
	}

	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}

	@Column(name = "content")
	@NotBlank(message = "消息内容不能为空")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content == null ? null : content.trim();
	}

	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getIsRead() {
		return isRead;
	}

	@Column(name = "is_read")
	public void setIsRead(Integer isRead) {
		this.isRead = isRead;
	}

	@Column(name = "uid")
	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	@Column(name = "user_name")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "attach_path")
	public String getAttachPath() {
		return attachPath;
	}

	public void setAttachPath(String attachPath) {
		this.attachPath = attachPath;
	}

}
