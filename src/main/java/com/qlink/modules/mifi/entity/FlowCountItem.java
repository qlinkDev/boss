/** 
 * @Package com.uu.modules.mifi.entity 
 * @Description 
 * @author yifang.huang
 * @date 2017年5月23日 下午2:09:28 
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
 * @Description 流量统计项(国家流量)
 * @author yifang.huang
 * @date 2017年5月23日 下午2:09:28 
 */
@Entity
@Table(name = "mifi_flow_count_item")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FlowCountItem extends BaseEntity<FlowCountItem> {

	private static final long serialVersionUID = 9214033075818154580L;
	
	private String id;
	
	private FlowCount flowCount;			// 所属流量统计(设备、订单、时间段)
	
	private String countryCode;				// 国家编号
	
	private String countryName;				// 国家名称
	
	private Double flow;					// 流量
	
	public FlowCountItem() {
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
	 * @return flowCount
	 */
	@ManyToOne
	@JoinColumn(name="flow_count_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public FlowCount getFlowCount() {
		return flowCount;
	}

	/** 
	 * @param flowCount
	 */
	public void setFlowCount(FlowCount flowCount) {
		this.flowCount = flowCount;
	}

	/** 
	 * @return countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/** 
	 * @param countryCode
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
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
