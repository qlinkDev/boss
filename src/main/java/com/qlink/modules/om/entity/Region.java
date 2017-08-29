/** 
 * @Package com.uu.modules.om.entity 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月18日 上午9:39:53 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.om.entity;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.google.common.collect.ImmutableSet;
import com.uu.common.persistence.IdEntity;
import com.uu.common.utils.StringUtils;

/** 
 * @Description 区域 实体类
 * @author yifang.huang
 * @date 2016年3月18日 上午9:39:53 
 */
@Entity
@Table(name = "om_region")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Region extends IdEntity<Region> {

	private static final long serialVersionUID = -5408377286360143985L;
	
	private String code;								// 编码
	
	private String name;								// 名称
	
	private String mcces;								// 包含的MCC值，以“,”分隔
	
	private String countryCodes;						// 国家编号，以“,”分隔（用于数据回显）
	
	private String countryNames;						// 国家中文名称，以“,”分隔（冗余字段，有于页面显示）
	
	private Double defaultPrice;						// 默认价格
	
	public Region() {
		super();
	}
	
	public Region(String id) {
		this();
		this.id = id;
	}

	/** 
	 * @return code
	 */
	@NotBlank(message = "区域编码不能为空")
	@Length(min=1, max=20, message="区域编码长度在1~20之间")
	public String getCode() {
		return code;
	}

	/** 
	 * @param code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/** 
	 * @return name
	 */
	@NotBlank(message = "区域名称不能为空")
	@Length(min=1, max=50, message="区域名称长度在1~50之间")
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
	 * @return countryCodes
	 */
	@NotBlank(message = "国家编号不能为空")
	@Length(min=1, max=255, message="国家编号长度在1~255之间")
	public String getCountryCodes() {
		return countryCodes;
	}

	/** 
	 * @param countryCodes
	 */
	public void setCountryCodes(String countryCodes) {
		this.countryCodes = countryCodes;
	}

	/** 
	 * @return mcces
	 */
	@NotBlank(message = "区域所包含MCC不能为空")
	@Length(min=1, max=255, message="区域所包含MCC长度在1~255之间")
	public String getMcces() {
		return mcces;
	}

	/** 
	 * @param mcces
	 */
	public void setMcces(String mcces) {
		this.mcces = mcces;
	}

	/** 
	 * @return countryNames
	 */
	public String getCountryNames() {
		return countryNames;
	}

	/** 
	 * @param countryNames
	 */
	public void setCountryNames(String countryNames) {
		this.countryNames = countryNames;
	}

	/** 
	 * @return defaultPrice
	 */
	public Double getDefaultPrice() {
		return defaultPrice;
	}

	/** 
	 * @param defaultPrice
	 */
	public void setDefaultPrice(Double defaultPrice) {
		this.defaultPrice = defaultPrice;
	}

	@Transient
	public Set<String> getMccesSet() {
		return ImmutableSet.copyOf(StringUtils.split(mcces, ","));
	}

	@Transient
	public Set<String> getCountryCodeSet() {
		return ImmutableSet.copyOf(StringUtils.split(countryCodes, ","));
	}

}
