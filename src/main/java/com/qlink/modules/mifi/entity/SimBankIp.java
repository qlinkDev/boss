package main.java.com.qlink.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.uu.common.persistence.BaseEntity;
/**
 * SIMBankIp管理
 * @Description 
 * @author wangsai
 * @date 2016年11月3日 上午11:27:35
 */
@Entity
@Table( name = "simbank_ip" )
public class SimBankIp  extends  BaseEntity<SimBankIp>{

	private static final long serialVersionUID = 1L;
	private String id;
	private String simbankid;
	private String onlineip; //当前此sim bank正在使用的ip信息
	private String defaultip;//默认的ip信息，也是最高优先级的ip信息，负载均衡读到这个数据返回给设备
	private String backupip; //记录所有可以连接的ip信息
	private Date  stampCreated;//创建时间
	@Id
	@Column(name = "ID")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Column(name = "SIMBANKID")
	public String getSimbankid() {
		return simbankid;
	}
	public void setSimbankid(String simbankid) {
		this.simbankid = simbankid;
	}
	@Column(name = "ONLINEIP")
	public String getOnlineip() {
		return onlineip;
	}
	public void setOnlineip(String onlineip) {
		this.onlineip = onlineip;
	}
	@Column(name = "DEFAULTIP")
	public String getDefaultip() {
		return defaultip;
	}
	public void setDefaultip(String defaultip) {
		this.defaultip = defaultip;
	}
	@Column(name = "BACKUPIP")
	public String getBackupip() {
		return backupip;
	}
	public void setBackupip(String backupip) {
		this.backupip = backupip;
	}
	@Column(name = "STAMP_CREATED")
	public Date getStampCreated() {
		return stampCreated;
	}
	public void setStampCreated(Date stampCreated) {
		this.stampCreated = stampCreated;
	}
	
}
