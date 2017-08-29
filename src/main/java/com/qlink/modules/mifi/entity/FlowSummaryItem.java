/** 
 * @Package com.uu.modules.mifi.entity 
 * @Description 
 * @author yifang.huang
 * @date 2017年5月23日 下午3:02:27 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.uu.common.persistence.BaseEntity;
import com.uu.common.utils.IdGen;

/** 
 * @Description 流量统计汇总项(国家总流量)
 * @author yifang.huang
 * @date 2017年5月23日 下午3:02:27 
 */
@Entity
@Table(name = "mifi_flow_summary_item")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FlowSummaryItem extends BaseEntity<FlowSummaryItem> {

	private static final long serialVersionUID = 5429579972915101382L;
	
	private String id;
	
	private FlowSummary flowSummary;		// 所属流量统计汇总(设备数量、订单数量)
	
	private String countryName;				// 国家名称
	
	private Double flow;					// 流量

	public FlowSummaryItem() {
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
	 * @return flowSummary
	 */
	@ManyToOne
	@JoinColumn(name="flow_summary_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public FlowSummary getFlowSummary() {
		return flowSummary;
	}

	/** 
	 * @param flowSummary
	 */
	public void setFlowSummary(FlowSummary flowSummary) {
		this.flowSummary = flowSummary;
	}

	/** 
	 * @return countryName
	 */
	public String getCountryName() {
		return countryName;
	}

	/** 
	 * @param countryName
	 */
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	/** 
	 * @return flow
	 */
	public Double getFlow() {
		return flow;
	}

	/** 
	 * @param flow
	 */
	public void setFlow(Double flow) {
		this.flow = flow;
	}
	
}
