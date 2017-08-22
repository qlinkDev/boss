/** 
 * @Package com.uu.modules.mifi.entity 
 * @Description 
 * @author yifang.huang
 * @date 2017年5月23日 下午1:53:07 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.uu.common.persistence.IdEntity;
import com.uu.modules.om.entity.Region;

/** 
 * @Description 流量统计记录
 * @author yifang.huang
 * @date 2017年5月23日 下午1:53:07 
 */
@Entity
@Table(name = "mifi_flow_count_record")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FlowCountRecord extends IdEntity<FlowCountRecord> {

	private static final long serialVersionUID = 7654735230398855496L;
	
	private Date startDate;					// 统计开始时间
	
	private Date endDate;					// 统计结束时间
	
	private Region region;					// 统计区域
	
	private String status = "NEW";			// 状态（NEW_新建,COUNTING_统计中,ENDED_统计完成,FAIL_统计失败）
	
	private Date executeDate;				// 执行时间
	
	private Date finishDate;				// 完成时间

	public final static String status_new = "NEW";				// 新建
	public final static String status_counting = "COUNTING";	// 统计中
	public final static String status_ended = "ENDED";			// 统计完成
	public final static String status_fail = "FAIL";			// 统计失败
	
	/** 
	 * @return startDate
	 */
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
	public Date getEndDate() {
		return endDate;
	}
	
	/** 
	 * @param endDate
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	/** 
	 * @return region
	 */
	@ManyToOne
	@JoinColumn(name="region_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public Region getRegion() {
		return region;
	}
	
	/** 
	 * @param region
	 */
	public void setRegion(Region region) {
		this.region = region;
	}
	
	/** 
	 * @return status
	 */
	public String getStatus() {
		return status;
	}
	
	/** 
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/** 
	 * @return executeDate
	 */
	public Date getExecuteDate() {
		return executeDate;
	}

	/** 
	 * @param executeDate
	 */
	public void setExecuteDate(Date executeDate) {
		this.executeDate = executeDate;
	}

	/** 
	 * @return finishDate
	 */
	public Date getFinishDate() {
		return finishDate;
	}

	/** 
	 * @param finishDate
	 */
	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}
	
}
