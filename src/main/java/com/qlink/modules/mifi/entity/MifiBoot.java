/** 
 * @Package com.uu.modules.mifi.entity 
 * @Description 
 * @author yifang.huang
 * @date 2016年12月7日 上午10:42:51 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.uu.common.persistence.BaseEntity;
import com.uu.common.utils.IdGen;

/** 
 * @Description MIFI开机(一天一条MIFI状态为4记录) 实体类
 * @author yifang.huang
 * @date 2016年12月7日 上午10:42:51 
 */
@Entity
@Table(name = "mifi_boot")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MifiBoot extends BaseEntity<MifiBoot> {

	private static final long serialVersionUID = 6719632613231503602L;

	private String id; 								// 唯一标识
	
	private String imei;	 						// 设备编号
	
	private String mcc;								// 使用地mcc
	
	private String status;							// 状态（NEW_新建,HANDLED_已处理）
	
	private Date createDate; 						// 开机时间

	public MifiBoot() {
		super();
		id = IdGen.uuid();
		createDate = new Date();
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
	 * @return mcc
	 */
	public String getMcc() {
		return mcc;
	}

	/** 
	 * @param mcc
	 */
	public void setMcc(String mcc) {
		this.mcc = mcc;
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
	 * @return createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/** 
	 * @param createDate
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public static final String STATUS_NEW = "NEW";					// 新建
	public static final String STATUS_HANDLED = "HANDLED";			// 已处理

}
