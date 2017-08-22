/** 
 * @Package com.uu.modules.mifi.entity 
 * @Description 
 * @author yifang.huang
 * @date 2016年11月22日 上午9:56:03 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.entity;

import java.util.Date;

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
import com.uu.common.utils.excel.annotation.ExcelField;
import com.uu.modules.om.entity.Advertising;

/** 
 * @Description 设备开机(接入设备:手机,平板...)   实体类
 * @author yifang.huang
 * @date 2016年11月22日 上午9:56:03 
 */
@Entity
@Table(name = "mifi_device_boot")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DeviceBoot extends BaseEntity<DeviceBoot> {

	private static final long serialVersionUID = -3085849091245475660L;

	private String id; 								// 唯一标识
	
	private String type;							// 类型（将点击'我要上网'行为也记录到此表中，BOOT[开机]，CON_NET[点击'我要上网']，JUMP[页面跳转]）
	
	private String imei;	 						// 设备编号
	
	private Advertising advertising;				// 广告（点击行为所在广告）
	
	private String mac; 							// MAC地址
	
	private String mcc; 							// 设备开机国家mcc
	
	private String sourceType;				 		// 设备所属渠道
	
	private String clientType;						// 客户端类型
	
	private String userAgent;						// 用户代理
	
	private Date createDate; 						// 开机时间

	public DeviceBoot() {
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
	 * @return type
	 */
	@ExcelField(title = "类型" ,align =2,sort=20)
	public String getType() {
		return type;
	}

	/** 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/** 
	 * @return imei
	 */
	@ExcelField(title = "数量" ,align =2,sort=30)
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
	 * @return advertising
	 */
	@ManyToOne
	@JoinColumn(name="advertising_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public Advertising getAdvertising() {
		return advertising;
	}

	/** 
	 * @param advertising
	 */
	public void setAdvertising(Advertising advertising) {
		this.advertising = advertising;
	}

	/** 
	 * @return mac
	 */
	public String getMac() {
		return mac;
	}

	/** 
	 * @param mac
	 */
	public void setMac(String mac) {
		this.mac = mac;
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
	 * @return sourceType
	 */
	public String getSourceType() {
		return sourceType;
	}

	/** 
	 * @param sourceType
	 */
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	/** 
	 * @return clientType
	 */
	public String getClientType() {
		return clientType;
	}

	/** 
	 * @param clientType
	 */
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	/** 
	 * @return userAgent
	 */
	@ExcelField(title = "时间" ,align =2,sort=10)
	public String getUserAgent() {
		return userAgent;
	}

	/** 
	 * @param userAgent
	 */
	
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
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

	public static final String TYPE_BOOT = "BOOT";				// 开机
	public static final String TYPE_HOME_SHOW = "HOME_SHOW";	// 首页显示
	public static final String TYPE_CON_NET = "CON_NET";		// 连接网络
	public static final String TYPE_JUMP = "JUMP";				// 页面跳转
}
