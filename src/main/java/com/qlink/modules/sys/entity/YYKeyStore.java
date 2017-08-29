package main.java.com.qlink.modules.sys.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import com.uu.common.persistence.DataEntity;
import com.uu.common.utils.IdGen;


 
/**
 * 密钥库实体
 * @author liaowu
 */
@Entity
@Table(name = "tb_sys_keystore")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class YYKeyStore extends DataEntity<YYKeyStore> {
	private static final long serialVersionUID = 1L;

	public YYKeyStore() {
		super();
	}
	
	@PrePersist
	public void prePersist(){
		super.prePersist();
		this.keyId = IdGen.uuid();
	}

	private String keyId;
	private String keyType;//加密类型
	private String keyDesc;//密钥描述
	private String keyValue;//密钥
	private String sourceType;//来源

	@Id
	//无需写NotNull等注解，校验发生在prePersist之前，否则新增数据无法通过校验
	//@NotNull
	//@Length(min=1, max=32)
	public String getKeyId() {
		return this.keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	@NotNull
	@Length(min=1, max=10)
	public String getKeyType() {
		return this.keyType;
	}

	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	@NotNull
	@Length(min=1, max=2000)
	public String getKeyValue() {
		return this.keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	@NotNull
	@Length(min=1, max=20)
	public String getSourceType() {
		return this.sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	@Length(min=1, max=50)
	public String getKeyDesc() {
		return this.keyDesc;
	}

	public void setKeyDesc(String keyDesc) {
		this.keyDesc = keyDesc;
	}
}
