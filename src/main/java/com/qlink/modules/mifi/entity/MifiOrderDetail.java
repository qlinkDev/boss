package main.java.com.qlink.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.uu.common.persistence.BaseEntity;

/**
 * mifi订单分配详情
 * @author wangyong
 * @date 2016年1月29日
 */
@SuppressWarnings("rawtypes")
@Entity
@Table(name = "mifi_order_detail")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MifiOrderDetail extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private String orderDetailId;//订单明细编号

	@Id
	@Column(name = "order_detail_id")
	public String getOrderDetailId() {
		return orderDetailId;
	}

	public void setOrderDetailId(String orderDetailId) {
		this.orderDetailId = orderDetailId;
	}
	
	private String orderId;//订单编号

	@Column(name = "order_id")
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	private String outOrderId;//外部订单编号

	@Column(name = "out_order_id")
	public String getOutOrderId() {
		return outOrderId;
	}

	public void setOutOrderId(String outOrderId) {
		this.outOrderId = outOrderId;
	}
	
	private String dsn;//发货设备序列号

	@Column(name = "dsn")
	public String getDsn() {
		return dsn;
	}

	public void setDsn(String dsn) {
		this.dsn = dsn;
	}
	
	private Date deliveryTime;//发货时间
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "delivery_time")
	public Date getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(Date deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	
	private String ssid;

	/** 
	 * @return ssid
	 */
	@Column(name = "ssid")
	public String getSsid() {
		return ssid;
	}

	/** 
	 * @param ssid
	 */
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	
}
