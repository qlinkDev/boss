/** 
 * @Package com.uu.modules.om.entity 
 * @Description 
 * @author yifang.huang
 * @date 2016年12月16日 上午11:18:56 
 * @version V1.0 
 */ 
package com.uu.modules.om.entity;

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
 * @Description 广告位   实体类
 * @author yifang.huang
 * @date 2016年12月16日 上午11:18:56 
 */
@Entity
@Table(name = "om_advertising_item")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AdvertisingItem extends BaseEntity<AdvertisingItem> {

	private static final long serialVersionUID = -4074066387327843369L;
	
	private String id;								// 唯一标识
	
	private String advertisingId;					// 广告ID
	
	private String imgPath;							// 图片地址
	
	private String url;								// 跳转地址
	
	private String showBaiduAd;						// 显示百度广告
	
	private Integer sequence;						// 排序

	public AdvertisingItem() {
		super();
		id = IdGen.uuid();
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
	 * @return advertisingId
	 */
	public String getAdvertisingId() {
		return advertisingId;
	}

	/** 
	 * @param advertisingId
	 */
	public void setAdvertisingId(String advertisingId) {
		this.advertisingId = advertisingId;
	}

	/** 
	 * @return imgPath
	 */
	public String getImgPath() {
		return imgPath;
	}

	/** 
	 * @param imgPath
	 */
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	/** 
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	/** 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/** 
	 * @return showBaiduAd
	 */
	public String getShowBaiduAd() {
		return showBaiduAd;
	}

	/** 
	 * @param showBaiduAd
	 */
	public void setShowBaiduAd(String showBaiduAd) {
		this.showBaiduAd = showBaiduAd;
	}
	
	/** 
	 * @return sequence
	 */
	public Integer getSequence() {
		return sequence;
	}

	/** 
	 * @param sequence
	 */
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

}
