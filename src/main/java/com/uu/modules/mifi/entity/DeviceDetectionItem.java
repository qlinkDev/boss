/** 
 * @Package com.uu.modules.mifi.entity 
 * @Description 
 * @author yifang.huang
 * @date 2016年11月9日 下午2:36:36 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.uu.common.persistence.BaseEntity;

/** 
 * @Description 设备检查项结果 实体类
 * @author yifang.huang
 * @date 2016年11月9日 下午2:36:36 
 */
@Entity
@Table(name = "mifi_device_detection_item")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DeviceDetectionItem extends BaseEntity<DeviceDetectionItem> {

	private static final long serialVersionUID = -2962390607334585772L;
	
	private String id;
	
	private String deviceDetectionId;			// 检查主体ID
	
	private String code;						// 检查项编码
	
	private String name;						// 检查顶名称
	
	private String result;						// 检查结果
	
	private Integer sequence;					// 排序
	
	private Date createDate;					// 添加时间

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

	/** 
	 * @return deviceDetectionId
	 */
	@Column(name = "device_detection_id")
	@NotNull(message = "检查主体不能为空")
	public String getDeviceDetectionId() {
		return deviceDetectionId;
	}

	/** 
	 * @param deviceDetectionId
	 */
	public void setDeviceDetectionId(String deviceDetectionId) {
		this.deviceDetectionId = deviceDetectionId;
	}

	/** 
	 * @return code
	 */
	@Column(name = "code")
	@NotNull(message = "检查项编码不能为空")
	public String getCode() {
		return code;
	}

	/** 
	 * @param code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/** 
	 * @return name
	 */
	@Column(name = "name")
	@NotNull(message = "检查项名称不能为空")
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
	 * @return result
	 */
	@Column(name = "result")
	@NotNull(message = "检查项结果不能为空")
	public String getResult() {
		return result;
	}

	/** 
	 * @param result
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/** 
	 * @return sequence
	 */
	@Column(name = "sequence")
	public Integer getSequence() {
		return sequence;
	}

	/** 
	 * @param sequence
	 */
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	/** 
	 * @return createDate
	 */
	@Column(name = "create_date")
	public Date getCreateDate() {
		return createDate;
	}

	/** 
	 * @param createDate
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
