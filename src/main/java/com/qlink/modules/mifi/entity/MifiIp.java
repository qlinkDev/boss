package main.java.com.qlink.modules.mifi.entity;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.uu.common.persistence.BaseEntity;
/**
 * 多服务器配置管理
 * @Description 
 * @author wangsai
 * @date 2016年11月3日 下午2:56:36
 */
@Entity
@Table(name="mifi_ip")
public class MifiIp  extends BaseEntity<MifiIp>{
	private static final long serialVersionUID = 1L;
	private String id;
	private String mcc;
	private String defaultip;//默认的ip信息，也是最高优先级的ip信息，负载均衡读到这个数据返回给设备
	private String backupip;//记录所有可以连接的ip信息
	private Date stampCreated; //创建时间
	@Id
	@Column(name="Id")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Column(name = "Mcc")
	public String getMcc() {
		return mcc;
	}
	public void setMcc(String mcc) {
		this.mcc = mcc;
	}
	@Column(name = "Defaultip")
	public String getDefaultip() {
		return defaultip;
	}
	public void setDefaultip(String defaultip) {
		this.defaultip = defaultip;
	}
	@Column(name = "Backupip")
	public String getBackupip() {
		return backupip;
	}
	
	public void setBackupip(String backupip) {
		this.backupip = backupip;
	}
	@Column(name = "stamp_created")
	public Date getStampCreated() {
		return stampCreated;
	}
	public void setStampCreated(Date stampCreated) {
		this.stampCreated = stampCreated;
	}
	
}
