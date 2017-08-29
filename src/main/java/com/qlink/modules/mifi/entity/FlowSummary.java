/** 
 * @Package com.uu.modules.mifi.entity 
 * @Description 
 * @author yifang.huang
 * @date 2017年5月23日 下午2:39:30 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
 * @Description 流量统计汇总(设备数量、订单数量)
 * @author yifang.huang
 * @date 2017年5月23日 下午2:39:30 
 */
@Entity
@Table(name = "mifi_flow_summary")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FlowSummary extends BaseEntity<FlowSummary> {

	private static final long serialVersionUID = -5279898920858494081L;
	
	private String id;
	
	private FlowCountRecord flowCountRecord;	// 所属统计记录
	
	private Integer deviceTotal;				// 设备总数
	
	private Integer oneCountryDeviceTotal;		// 只去一个国家的设备总数
	
	private Integer orderTotal;					// 订单总数
	
	private Double flowTotal;					// 流量总数
	
	private Double flowAverage;					// 平均流量（flowTotal/deviceTotal）
	
	
	private List<FlowSummaryItem> itemList;		// 统计统计项列表（后台传前台）

	public FlowSummary() {
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
	@OneToOne
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
	 * @return deviceTotal
	 */
	public Integer getDeviceTotal() {
		return deviceTotal;
	}

	/** 
	 * @param deviceTotal
	 */
	public void setDeviceTotal(Integer deviceTotal) {
		this.deviceTotal = deviceTotal;
	}

	/** 
	 * @return oneCountryDeviceTotal
	 */
	public Integer getOneCountryDeviceTotal() {
		return oneCountryDeviceTotal;
	}

	/** 
	 * @param oneCountryDeviceTotal
	 */
	public void setOneCountryDeviceTotal(Integer oneCountryDeviceTotal) {
		this.oneCountryDeviceTotal = oneCountryDeviceTotal;
	}

	/** 
	 * @return orderTotal
	 */
	public Integer getOrderTotal() {
		return orderTotal;
	}

	/** 
	 * @param orderTotal
	 */
	public void setOrderTotal(Integer orderTotal) {
		this.orderTotal = orderTotal;
	}

	/** 
	 * @return itemList
	 */
	@Transient
	public List<FlowSummaryItem> getItemList() {
		return itemList;
	}

	/** 
	 * @param itemList
	 */
	public void setItemList(List<FlowSummaryItem> itemList) {
		this.itemList = itemList;
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
	 * @return flowAverage
	 */
	public Double getFlowAverage() {
		return flowAverage;
	}

	/** 
	 * @param flowAverage
	 */
	public void setFlowAverage(Double flowAverage) {
		this.flowAverage = flowAverage;
	}

}
