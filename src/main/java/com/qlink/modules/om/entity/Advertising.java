/** 
 * @Package com.uu.modules.om.entity 
 * @Description 
 * @author yifang.huang
 * @date 2016年12月16日 上午11:18:56 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.om.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.uu.common.persistence.IdEntity;

/** 
 * @Description 广告   实体类
 * @author yifang.huang
 * @date 2016年12月16日 上午11:18:56 
 */
@Entity
@Table(name = "om_advertising")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Advertising extends IdEntity<Advertising> {

	private static final long serialVersionUID = 1882731382035577665L;
	
	private String name;							// 广告名称(游友移动渠道广告)

	private String type;							// 广告类型
	
	private String sourceTypes;				 		// 所属渠道编号
	
	private String channelNames;				 	// 所属渠道名称
	
	private String countryCodes;					// 投入国家编号
	
	private String countryNames;					// 投入国家名称
	
	private String shelfUpDown;						// 上下架（UP：上架；DOWN：下架）
	
	private List<AdvertisingItem> itemList;			// 广告位（后台传前台）
	
	private String itemStrs;						// 广告位字符串（前台传后台）

	/** 
	 * @return name
	 */
	@Column(name = "name")
	@NotNull(message = "广告名称不能为空")
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
	 * @return sourceTypes
	 */
	@Column(name = "source_types")
	@NotNull(message = "所属渠道商不能为空")
	public String getSourceTypes() {
		return sourceTypes;
	}

	/** 
	 * @param sourceTypes
	 */
	public void setSourceTypes(String sourceTypes) {
		this.sourceTypes = sourceTypes;
	}

	/** 
	 * @return channelNames
	 */
	@Column(name = "channel_names")
	public String getChannelNames() {
		return channelNames;
	}

	/** 
	 * @param channelNames
	 */
	public void setChannelNames(String channelNames) {
		this.channelNames = channelNames;
	}

	/** 
	 * @return countryCodes
	 */
	@Column(name = "country_codes")
	@NotNull(message = "投放国家编号不能为空")
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
	 * @return countryNames
	 */
	@Column(name = "country_names")
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
	 * @return type
	 */
	@Column(name = "type")
	@NotNull(message = "广告类型不能为空")
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
	 * @return shelfUpDown
	 */
	public String getShelfUpDown() {
		return shelfUpDown;
	}

	/** 
	 * @param shelfUpDown
	 */
	public void setShelfUpDown(String shelfUpDown) {
		this.shelfUpDown = shelfUpDown;
	}

	/** 
	 * @return itemList
	 */
	@Transient
	public List<AdvertisingItem> getItemList() {
		return itemList;
	}

	/** 
	 * @param itemList
	 */
	public void setItemList(List<AdvertisingItem> itemList) {
		this.itemList = itemList;
	}

	/** 
	 * @return itemStrs
	 */
	@Transient
	public String getItemStrs() {
		return itemStrs;
	}

	/** 
	 * @param itemStrs
	 */
	public void setItemStrs(String itemStrs) {
		this.itemStrs = itemStrs;
	}
	
	// 类型常量
	public static String ADVERTISING_TYPE = "FOUR_POSITION";
	
	// 下架标记（0：上架；1：下架；）
	public static final String SHELF_UP = "UP";
	public static final String SHELF_DOWN = "DOWN";
}
