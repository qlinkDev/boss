/** 
 * @Package com.uu.modules.mifi.entity 
 * @Description 
 * @author yifang.huang
 * @date 2017年5月23日 下午2:04:52 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.uu.common.persistence.BaseEntity;
import com.uu.common.utils.IdGen;

/** 
 * @Description 流量统计(设备、订单、时间段)
 * @author yifang.huang
 * @date 2017年5月23日 下午2:04:52 
 */
@Entity
@Table(name = "mifi_flow_count")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FlowCount extends BaseEntity<FlowCount> {

	private static final long serialVersionUID = 137731555882424769L;

	private String id;							// 唯一值
	
	private FlowCountRecord flowCountRecord;	// 所属统计记录
	
	private String imei;						// 设备编号
	
	private String orderCode;					// 订单编号
	
	private String startDate;					// 开始时间
	
	private String endDate;						// 结束时间
	
	private Double flowTotal;					// 流量总数
	
	private List<FlowCountItem> itemList;		// 统计项列表（后台传前台）
	
	public FlowCount() {
		super();
		id = IdGen.uuid();
	}

	/** 
	 * @return id
	 */
	@Id
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
	 * @return flowCountRecord
	 */
	@ManyToOne
	@JoinColumn(name="flow_count_record_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public FlowCountRecord getFlowCountRecord() {
		return flowCountRecord;
	}

	/** 
	 * @param flowCountRecord
	 */
	public void setFlowCountRecord(FlowCountRecord flowCountRecord) {
		this.flowCountRecord = flowCountRecord;
	}

	/** 
	 * @return imei
	 */
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
	 * @return orderCode
	 */
	public String getOrderCode() {
		return orderCode;
	}

	/** 
	 * @param orderCode
	 */
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	/** 
	 * @return startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/** 
	 * @param startDate
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/** 
	 * @return endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/** 
	 * @param endDate
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/** 
	 * @return flowTotal
	 */
	public Double getFlowTotal() {
		return flowTotal;
	}

	/** 
	 * @param flowTotal
	 */
	public void setFlowTotal(Double flowTotal) {
		this.flowTotal = flowTotal;
	}

	/** 
	 * @return itemList
	 */
	@Transient
	public List<FlowCountItem> getItemList() {
		return itemList;
	}

	/** 
	 * @param itemList
	 */
	public void setItemList(List<FlowCountItem> itemList) {
		this.itemList = itemList;
	}

}
