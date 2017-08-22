/** 
 * @Package com.uu.modules.sys.entity 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月15日 下午6:01:50 
 * @version V1.0 
 */ 
package com.uu.modules.sys.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.uu.common.persistence.IdEntity;

/** 
 * @Description 通知接收 实体类
 * @author yifang.huang
 * @date 2016年4月15日 下午6:01:50 
 */
@Entity
@Table(name = "sys_notice_receive")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class NoticeReceive extends IdEntity<NoticeReceive> {
	
	private static final long serialVersionUID = 4249984616530825494L;

	private String name;						// 名称
	
	private String sourceType;					// 渠道编号
	
	private String faultCodes;					// 需要处理的错误信息编码
	
	private String phones;						// 手机号码
	
	private String emails;						// 邮箱
	
	private String type;						// 类别（CARD_MONITOR-卡监控）

	/** 
	 * @return name
	 */
	@Column(name = "name")
	@NotBlank(message = "通知接收名称不能为空")
	@Length(min=1, max=32, message="通知接收名称长度在1~50之间")
	public String getName() {
		return name;
	}

	/** 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** 
	 * @return sourceType
	 */
	@Column(name = "source_type")
	@NotBlank(message = "渠道编号不能为空")
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
	 * @return faultCodes
	 */
	@Column(name = "fault_codes")
	@Length(min=0, max=100, message="需要处理的错误信息编码长度在0~100之间")
	public String getFaultCodes() {
		return faultCodes;
	}

	/** 
	 * @param faultCodes
	 */
	public void setFaultCodes(String faultCodes) {
		this.faultCodes = faultCodes;
	}

	/** 
	 * @return phones
	 */
	@Column(name = "phones")
	@Length(min=0, max=255, message="通知接收手机号码长度在0~255之间")
	public String getPhones() {
		return phones;
	}

	/** 
	 * @param phones
	 */
	public void setPhones(String phones) {
		this.phones = phones;
	}

	/** 
	 * @return emails
	 */
	@Column(name = "emails")
	@Length(min=0, max=512, message="通知接收邮箱长度在0~512之间")
	public String getEmails() {
		return emails;
	}

	/** 
	 * @param emails
	 */
	public void setEmails(String emails) {
		this.emails = emails;
	}

	/** 
	 * @return type
	 */
	@Column(name = "type")
	@NotBlank(message = "通知接收类型不能为空")
	@Length(min=1, max=32, message="通知接收类型长度在1~50之间")
	public String getType() {
		return type;
	}

	/** 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
}
