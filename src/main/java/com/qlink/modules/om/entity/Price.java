/** 
 * @Package com.uu.modules.om.entity 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月18日 上午9:46:56 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.om.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.uu.common.persistence.IdEntity;

/** 
 * @Description 价格 实体类
 * @author yifang.huang
 * @date 2016年3月18日 上午9:46:56 
 */
@Entity
@Table(name = "om_price")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Price extends IdEntity<Price> {

	private static final long serialVersionUID = 7096688735204826731L;
	
	private Channel channel;							// 所属渠道
	
	private Region region;								// 所属区域
	
	private Double price;								// 价格（默认为区域默认价格）
	
	private Double newPrice;							// 新价格
	
	private String newPriceStartDate;					// 新价格开始时间（yyyy-MM-dd HH）
	
	private String containChannel;						// 允许使用该价格的渠道
	
	private String downShelf;							// 下架（0：上架；1：下架）
	
	public Price() {
		super();
	}
	
	public Price(String id) {
		this();
		this.id = id;
	}


	/** 
	 * @return channel
	 */
	@ManyToOne
	@JoinColumn(name="channel_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public Channel getChannel() {
		return channel;
	}

	/** 
	 * @param channel
	 */
	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	/** 
	 * @return region
	 */
	@ManyToOne
	@JoinColumn(name="region_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public Region getRegion() {
		return region;
	}

	/** 
	 * @param region
	 */
	public void setRegion(Region region) {
		this.region = region;
	}

	/** 
	 * @return price
	 */
	public Double getPrice() {
		return price;
	}

	/** 
	 * @param price
	 */
	public void setPrice(Double price) {
		this.price = price;
	}

	/** 
	 * @return newPrice
	 */
	public Double getNewPrice() {
		return newPrice;
	}

	/** 
	 * @param newPrice
	 */
	public void setNewPrice(Double newPrice) {
		this.newPrice = newPrice;
	}

	/** 
	 * @return newPriceStartDate
	 */
	public String getNewPriceStartDate() {
		return newPriceStartDate;
	}

	/** 
	 * @param newPriceStartDate
	 */
	public void setNewPriceStartDate(String newPriceStartDate) {
		this.newPriceStartDate = newPriceStartDate;
	}

	/** 
	 * @return containChannel
	 */
	public String getContainChannel() {
		return containChannel;
	}

	/** 
	 * @param containChannel
	 */
	public void setContainChannel(String containChannel) {
		this.containChannel = containChannel;
	}

	/** 
	 * @return downShelf
	 */
	public String getDownShelf() {
		return downShelf;
	}

	/** 
	 * @param downShelf
	 */
	public void setDownShelf(String downShelf) {
		this.downShelf = downShelf;
	}
	
	// 下架标记（0：上架；1：下架；）
	public static final String DOWN_SHELF_UP = "0";
	public static final String DOWN_SHELF_DOWN = "1";

}
