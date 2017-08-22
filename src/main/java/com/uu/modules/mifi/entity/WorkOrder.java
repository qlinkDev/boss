package com.uu.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import com.uu.common.persistence.BaseEntity;
import com.uu.common.utils.IdGen;
import com.uu.modules.sys.entity.User;

/**
 * @author shuxin
 * @date 2016年5月31日
 */
@Entity
@Table(name = "work_order")
@DynamicInsert
@DynamicUpdate
public class WorkOrder extends BaseEntity<WorkOrder> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2340471711709048964L;

	private String id;

	/**
	 * 工单ID
	 */
	private Integer wid;

	/**
	 * 设备编号
	 */
	private String deviceSn;

	/**
	 * 工单问题详情
	 */
	private String problemDesc;

	/**
	 * 工单问题类型：1:故障,2:退款,3:设备激活'
	 */
	private Integer problemType;

	/**
	 * 工单处理优先级： 1普通，2紧急
	 */
	private Integer level;

	/**
	 * 工单处理状态:1:等待处理;2:正在处理;3:已关闭;
	 */
	private Integer status;

	/**
	 * 工单创建时间
	 */
	private Date createTime;

	/**
	 * 工单创建者
	 */
	private User createBy;

	/**
	 * 工单问题诊断结果
	 */
	private Integer pDiagnosisType;

	/**
	 * 工单附件访问路径
	 */
	private String attachPath;

	/**
	 * 渠道商编号
	 */
	private String channelSn;
	
	/** 
	 * 工单关闭说明 
	 */
	private String closeDesc;
	
	/**
	 * 问题是否解决 1是 0否
	 */
	private Integer isResove;
	
	/**
	 * 是否退款
	 */
	private Integer isRefund;
	
	/**
	 * 退款描述
	 */
	private String refundDesc;
	
	/**
	 * 是否产生损失
	 */
	private Integer isLoss;
	
	/**
	 * 插头是否损坏，1是，0未
	 */
	private Integer lossPlug;
	
	/**
	 * 数据线是否损坏，1是，0未
	 */
	private Integer lossDataLine;
	
	/**
	 * 贴纸密码错误，1是，0否
	 */
	private Integer lossPassword;
	
	
	
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
	public Integer getWid() {
		return wid;
	}

	public void setWid(Integer wid) {
		this.wid = wid;
	}

	@Column(name = "device_sn")
	@NotBlank(message = "设备编号不能为空")
	public String getDeviceSn() {
		return deviceSn;
	}

	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}

	@Column(name = "problem_desc")
	@NotBlank(message = "问题详情不能为空")
	public String getProblemDesc() {
		return problemDesc;
	}

	public void setProblemDesc(String problemDesc) {
		this.problemDesc = problemDesc == null ? null : problemDesc.trim() ;
	}

	@Column(name = "problem_type")
	@NotNull(message = "问题类型不能为空，请选择")
	public Integer getProblemType() {
		return problemType;
	}

	public void setProblemType(Integer problemType) {
		this.problemType = problemType;
	}

	@Column(name = "level")
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "create_by")
	public User getCreateBy() {
		return createBy;
	}

	public void setCreateBy(User createBy) {
		this.createBy = createBy;
	}

	@Column(name = "problem_diagnosis_type")
	public Integer getpDiagnosisType() {
		return pDiagnosisType;
	}

	public void setpDiagnosisType(Integer pDiagnosisType) {
		this.pDiagnosisType = pDiagnosisType;
	}

	@Column(name = "attach_path")
	public String getAttachPath() {
		return attachPath;
	}

	public void setAttachPath(String attachPath) {
		this.attachPath = attachPath;
	}

	@Column(name = "channel_sn")
	public String getChannelSn() {
		return channelSn;
	}

	public void setChannelSn(String channelSn) {
		this.channelSn = channelSn;
	}

	@Column(name = "close_desc")
	public String getCloseDesc() {
		return closeDesc;
	}

	public void setCloseDesc(String closeDesc) {
		this.closeDesc = closeDesc == null ? null : closeDesc;
	}

	@Transient
	public Integer getIsResove() {
		return isResove;
	}

	public void setIsResove(Integer isResove) {
		this.isResove = isResove;
	}

	@Transient
	public Integer getIsRefund() {
		return isRefund;
	}

	public void setIsRefund(Integer isRefund) {
		this.isRefund = isRefund;
	}

	@Transient
	public String getRefundDesc() {
		return refundDesc;
	}

	public void setRefundDesc(String refundDesc) {
		this.refundDesc = refundDesc == null ? null : refundDesc.trim();
	}

	@Transient
	public Integer getIsLoss() {
		return isLoss;
	}

	public void setIsLoss(Integer isLoss) {
		this.isLoss = isLoss;
	}

	@Transient
	public Integer getLossPlug() {
		return lossPlug;
	}

	public void setLossPlug(Integer lossPlug) {
		this.lossPlug = lossPlug;
	}

	@Transient
	public Integer getLossDataLine() {
		return lossDataLine;
	}

	public void setLossDataLine(Integer lossDataLine) {
		this.lossDataLine = lossDataLine;
	}

	@Transient
	public Integer getLossPassword() {
		return lossPassword;
	}

	public void setLossPassword(Integer lossPassword) {
		this.lossPassword = lossPassword;
	}

	
}
