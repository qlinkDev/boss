/** 
 * @Package com.uu.modules.mifi.entity 
 * @Description 
 * @author yifang.huang
 * @date 2016年11月9日 下午2:16:03 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.uu.common.persistence.IdEntity;

/** 
 * @Description 设备检查 实体类
 * @author yifang.huang
 * @date 2016年11月9日 下午2:16:03 
 */
@Entity
@Table(name = "mifi_device_detection")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DeviceDetection extends IdEntity<DeviceDetection> {

	private static final long serialVersionUID = 858921452981016248L;
	
	private String imei;						// 设备编号		
	
	private String useFlag;						// 使用标记（1_可使用，0_不可使用）
	
	private String sourceType;					// 渠道编号
	
	private List<DeviceDetectionItem> itemList;	// 检查项（后台传前台）
	
	private String itemStrs;					// 检查项字符串（前台传后台）

	/** 
	 * @return imei
	 */
	@Column(name = "imei")
	@NotNull(message = "设备编号不能为空")
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
	 * @return useFlag
	 */
	@Column(name = "use_flag")
	@NotNull(message = "设备能否使用不能为空")
	public String getUseFlag() {
		return useFlag;
	}

	/** 
	 * @param useFlag
	 */
	public void setUseFlag(String useFlag) {
		this.useFlag = useFlag;
	}

	/** 
	 * @return sourceType
	 */
	@Column(name = "source_type")
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
	 * @return itemList
	 */
	@Transient
	public List<DeviceDetectionItem> getItemList() {
		return itemList;
	}

	/** 
	 * @param itemList
	 */
	public void setItemList(List<DeviceDetectionItem> itemList) {
		this.itemList = itemList;
	}

	/** 
	 * @return itemStrs
	 */
	@Transient
	public String getItemStrs() {
		return itemStrs;
	}

	/** 
	 * @param itemStrs
	 */
	public void setItemStrs(String itemStrs) {
		this.itemStrs = itemStrs;
	}

}
