/** 
 * @Package com.uu.modules.sys.entity 
 * @Description 
 * @author yifang.huang
 * @date 2016-3-7 下午4:43:17 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.sys.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.uu.common.persistence.IdEntity;

/** 
 * @Description 在线支付 配置信息 实体类
 * @author yifang.huang
 * @date 2016-3-7 下午4:43:17 
 */
@Entity
@Table(name = "sys_payConfig")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PayConfig extends IdEntity<PayConfig> {

	private static final long serialVersionUID = -420157508086337673L;

	private String partner;   											// 合作身份者ID

	private String privateKey;   										// 私钥

	private String verifyUrl;											// 消息验证地址

	private String gateway;												// 服务接入网关

	private String payType;												// 支付类型

	private String name; 												// 支付方式名称

	private String isEnabled = "1";										// 是否启用（0：禁用；1：启用）
	
	public PayConfig() {
		super();
	}
	
	public PayConfig(String id) {
		this();
		this.id = id;
	}

	/** 
	 * @return name
	 */
	@Length(min=1, max=50, message="支付接口名称长度在1~50之间")
	public String getName() {
		return name;
	}

	/** 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** 
	 * @return partner
	 */
	@NotBlank(message = "合作者身份ID不能为空")
	@Length(min=16, max=16, message="合作者身份ID长度为16")
	public String getPartner() {
		return partner;
	}

	/** 
	 * @param partner
	 */
	public void setPartner(String partner) {
		this.partner = partner;
	}

	/** 
	 * @return privateKey
	 */
	@NotBlank(message = "私钥不能为空")
	@Length(min=32, max=32, message="私钥长度为32")
	public String getPrivateKey() {
		return privateKey;
	}

	/** 
	 * @param privateKey
	 */
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	/** 
	 * @return verifyUrl
	 */
	@Length(min=0, max=255, message="消息验证地址长度在0~255之间")
	public String getVerifyUrl() {
		return verifyUrl;
	}

	/** 
	 * @param verifyUrl
	 */
	public void setVerifyUrl(String verifyUrl) {
		this.verifyUrl = verifyUrl;
	}

	/** 
	 * @return gateway
	 */
	@Length(min=0, max=255, message="服务接入网关长度在0~255之间")
	public String getGateway() {
		return gateway;
	}

	/** 
	 * @param gateway
	 */
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	/** 
	 * @return payType
	 */
	@NotBlank(message = "支付类型不能为空")
	@Length(min=1, max=50, message="支付类型长度在1~50之间")
	public String getPayType() {
		return payType;
	}

	/** 
	 * @param payType
	 */
	public void setPayType(String payType) {
		this.payType = payType;
	}

	/** 
	 * @return isEnabled
	 */
	public String getIsEnabled() {
		return isEnabled;
	}

	/** 
	 * @param isEnabled
	 */
	public void setIsEnabled(String isEnabled) {
		this.isEnabled = isEnabled;
	}

}
