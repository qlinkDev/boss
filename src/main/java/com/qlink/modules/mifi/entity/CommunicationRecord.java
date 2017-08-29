/** 
 * @Package com.uu.modules.om.entity 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月18日 上午9:39:53 
 * @version V1.0 
 */
package main.java.com.qlink.modules.mifi.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.uu.common.persistence.IdEntity;

/**
 * 通信记录
 * 
 * @author shuxin
 * @date 2016年7月20日
 */
@Entity
@Table(name = "communication_record")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CommunicationRecord extends IdEntity<CommunicationRecord> {

	private static final long serialVersionUID = -730473528513215677L;

	private String deviceSn; // 设备编号,多个以“,”分隔

	private Integer type; // 通信类型

	private Integer result; // 通信结果

	public CommunicationRecord() {
		super();
	}

	public CommunicationRecord(String id) {
		this();
		this.id = id;
	}

	public String getDeviceSn() {
		return deviceSn;
	}

	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}
	
	public static final Integer CARD_TYPE = 1;
	public static final Integer RESULT_SUCESS = 1;
	public static final Integer RESULT_FAILED = 2;

}
