/** 
 * @Package com.uu.modules.mifi.entity 
 * @Description 
 * @author yifang.huang
 * @date 2016年5月20日 上午10:38:11 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.uu.common.persistence.IdEntity;

/** 
 * @Description 设备监控主体信息 实体类
 * @author yifang.huang
 * @date 2016年5月20日 上午10:38:11 
 */
@Entity
@Table(name = "mifi_device_monitor")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DeviceMonitor extends IdEntity<DeviceMonitor> {

	private static final long serialVersionUID = -1176816452124167227L;
	
	private String code;							// 监控编号（yyyyMMddHH）
	
	private Integer deviceCount;					// 本次监控扫描的设备数量
		
	private Integer statusRecordCount;				// 本次监控扫描的状态记录数量
	
	private Integer resultCount;					// 本次监控异常数量
	
	private Date startDate;							// 本次监控执行开始时间
	
	private Date endDate;							// 本次监控执行结束时间

	/** 
	 * @return code
	 */
	@Column(name = "code")
	@NotBlank(message = "编号不能为空")
	@Length(min=10, max=10, message="编号长度为10")
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
	 * @return deviceCount
	 */
	@Column(name = "device_count")
	public Integer getDeviceCount() {
		return deviceCount;
	}

	/** 
	 * @param deviceCount
	 */
	public void setDeviceCount(Integer deviceCount) {
		this.deviceCount = deviceCount;
	}

	/** 
	 * @return statusRecordCount
	 */
	@Column(name = "status_record_count")
	public Integer getStatusRecordCount() {
		return statusRecordCount;
	}

	/** 
	 * @param statusRecordCount
	 */
	public void setStatusRecordCount(Integer statusRecordCount) {
		this.statusRecordCount = statusRecordCount;
	}

	/** 
	 * @return resultCount
	 */
	@Column(name = "result_count")
	public Integer getResultCount() {
		return resultCount;
	}

	/** 
	 * @param resultCount
	 */
	public void setResultCount(Integer resultCount) {
		this.resultCount = resultCount;
	}

	/** 
	 * @return startDate
	 */
	@Column(name = "start_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getStartDate() {
		return startDate;
	}

	/** 
	 * @param startDate
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/** 
	 * @return endDate
	 */
	@Column(name = "end_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getEndDate() {
		return endDate;
	}

	/** 
	 * @param endDate
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
