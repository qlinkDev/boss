package com.uu.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.uu.common.persistence.IdEntity;

/**
 * 设备使用记录(定时调度异常日志)
 * 
 * @author shuxin
 * @date 2016年8月2日
 */
@Entity
@Table(name = "mifi_usage_record_log")
@DynamicInsert
@DynamicUpdate
public class MifiUsageRecordLog extends IdEntity<MifiUsageRecordLog> {

	private static final long serialVersionUID = 5787282344870576337L;

	/** 时间 */
	private Date stampCreated;

	private Integer result;
	
	/** 调度执行设备数量 */
	private Integer count;
	
	private Integer isCheck;

	public Date getStampCreated() {
		return stampCreated;
	}

	public void setStampCreated(Date stampCreated) {
		this.stampCreated = stampCreated;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getIsCheck() {
		return isCheck;
	}

	public void setIsCheck(Integer isCheck) {
		this.isCheck = isCheck;
	}

}
