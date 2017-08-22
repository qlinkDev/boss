/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.uu.modules.om.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.uu.common.persistence.IdEntity;
import com.uu.common.utils.excel.annotation.ExcelField;

/**
 * 渠道Entity
 * 
 * @author yuxiaoyu
 * @version 2016-3-18
 */
@Entity
@Table(name = "om_channel")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Channel extends IdEntity<Channel> {

	private static final long serialVersionUID = 1L;
	private String channelName;// 渠道名称
	private String channelNameEn;// 渠道英文名称
	private String mcces; // 包含的MCC值，以“,”分隔
	private String payType;// 付费类型，0_预付费 1_后付费
	private Double balance = 0.00;// 余额
	private Double backPoint;// 返点
	private String model = "BOOT_BUTTON";	// 模式，ORDER_订单模式|BOOT_BUTTON_开机扣费模式
	private String createCsvFile = "0";	// 生成csv文件（0_不生成，1_生成）
	private String customized = "0";			// 开机确认页面是否定制（0_使用默认渠道商页面,1_使用定制页面）

	public Channel() {
		super();
	}

	public Channel(String id) {
		this();
		this.id = id;
	}

	@Length(min = 1, max = 50)
	@NotBlank(message = "渠道名称不能为空")
	@ExcelField(title = "渠道名称", align = 2, sort = 30)
	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	@NotBlank(message = "渠道名称（英文）不能为空")
	public String getChannelNameEn() {
		return channelNameEn;
	}

	public void setChannelNameEn(String channelNameEn) {
		this.channelNameEn = channelNameEn;
	}

	public String getMcces() {
		return mcces;
	}

	public void setMcces(String mcces) {
		this.mcces = mcces;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	@Digits(integer = 10, fraction = 2)
	@ExcelField(title = "返点", align = 2, sort = 30)
	public Double getBackPoint() {
		return backPoint;
	}

	public void setBackPoint(Double backPoint) {
		this.backPoint = backPoint;
	}

	/** 
	 * @return model
	 */
	public String getModel() {
		return model;
	}

	/** 
	 * @param model
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/** 
	 * @return createCsvFile
	 */
	public String getCreateCsvFile() {
		return createCsvFile;
	}

	/** 
	 * @param createCsvFile
	 */
	public void setCreateCsvFile(String createCsvFile) {
		this.createCsvFile = createCsvFile;
	}

	/** 
	 * @return customized
	 */
	public String getCustomized() {
		return customized;
	}

	/** 
	 * @param customized
	 */
	public void setCustomized(String customized) {
		this.customized = customized;
	}

}