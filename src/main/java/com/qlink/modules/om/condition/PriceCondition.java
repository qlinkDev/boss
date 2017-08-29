/** 
 * @Package com.uu.modules.om.condition 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月22日 上午9:44:20 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.om.condition;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.uu.common.utils.SpringContextHolder;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.entity.Price;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.utils.UserUtils;
import com.uu.modules.utils.Constants;

/** 
 * @Description 价格 查询条件类
 * @author yifang.huang
 * @date 2016年3月22日 上午9:44:20 
 */
public class PriceCondition {
	
	private String neId; 							// id ne 查询值
	
	private String eqChannelId;						// 所属渠道ID eq 查询值
	
	private String eqChannelNameEn;				    // 所属渠道英文名称 eq 查询值
	
	private String eqRegionId;						// 所属区域ID eq 查询值
	
	private String eqNewPriceStartDate;				// 新价格开始时间 eq 查询值
	
	private String needChangePrice;					// 需要更改价格
	
	private String eqDownShelf;						// 下架 eq 查询值
	
	public PriceCondition() {
		super();
		// 初始化渠道查询值，渠道数据权限控制
		initEqChannelId();
	}
	
	public void build(DetachedCriteria dc) {
		
		if (StringUtils.isNotBlank(neId))
			dc.add(Restrictions.ne("id", neId));
		
		if (StringUtils.isNotBlank(eqChannelId))
			dc.add(Restrictions.eq("channel.id", eqChannelId));
		
		if (StringUtils.isNotBlank(eqChannelNameEn)){
			dc.createAlias("channel", "channel");
			dc.add(Restrictions.eq("channel.channelNameEn", eqChannelNameEn));
		}
		
		if (StringUtils.isNotBlank(eqRegionId))
			dc.add(Restrictions.eq("region.id", eqRegionId));
		
		if (StringUtils.isNotBlank(eqNewPriceStartDate))
			dc.add(Restrictions.eq("newPriceStartDate", eqNewPriceStartDate));
		
		if (StringUtils.isNotBlank(needChangePrice)) {
			dc.add(Restrictions.isNotNull("newPrice"));
			dc.add(Restrictions.isNotNull("newPriceStartDate"));
		}
		
		if (StringUtils.isNotBlank(eqDownShelf))
			dc.add(Restrictions.eq("downShelf", eqDownShelf));
		
		// 查询标记为0（正常）的数据
		dc.add(Restrictions.eq(Price.FIELD_DEL_FLAG, Price.DEL_FLAG_NORMAL));
		
		// 按创建时间降序排序
		dc.addOrder(Order.desc("createDate"));

	}

	/** 
	 * @return neId
	 */
	public String getNeId() {
		return neId;
	}

	/** 
	 * @param neId
	 */
	public void setNeId(String neId) {
		this.neId = neId;
	}

	/** 
	 * @return eqChannelId
	 */
	public String getEqChannelId() {
		return eqChannelId;
	}

	/** 
	 * @param eqChannelId
	 */
	public void setEqChannelId(String eqChannelId) {
		this.eqChannelId = eqChannelId;
	}

	public String getEqChannelNameEn() {
		return eqChannelNameEn;
	}

	public void setEqChannelNameEn(String eqChannelNameEn) {
		this.eqChannelNameEn = eqChannelNameEn;
	}

	/** 
	 * @return eqRegionId
	 */
	public String getEqRegionId() {
		return eqRegionId;
	}

	/** 
	 * @param eqRegionId
	 */
	public void setEqRegionId(String eqRegionId) {
		this.eqRegionId = eqRegionId;
	}

	/** 
	 * @return eqNewPriceStartDate
	 */
	public String getEqNewPriceStartDate() {
		return eqNewPriceStartDate;
	}

	/** 
	 * @param eqNewPriceStartDate
	 */
	public void setEqNewPriceStartDate(String eqNewPriceStartDate) {
		this.eqNewPriceStartDate = eqNewPriceStartDate;
	}

	/** 
	 * @return needChangePrice
	 */
	public String getNeedChangePrice() {
		return needChangePrice;
	}

	/** 
	 * @param needChangePrice
	 */
	public void setNeedChangePrice(String needChangePrice) {
		this.needChangePrice = needChangePrice;
	}

	/** 
	 * @return eqDownShelf
	 */
	public String getEqDownShelf() {
		return eqDownShelf;
	}

	/** 
	 * @param eqDownShelf
	 */
	public void setEqDownShelf(String eqDownShelf) {
		this.eqDownShelf = eqDownShelf;
	}

	/**
	 * 
	 * @Description  根据当前登录用户，初始化渠道查询值
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年4月12日 下午5:36:37
	 */
	private void initEqChannelId() {
		User user = UserUtils.getUser();
		String channelNameEn = user.getChannelNameEn();
		// 用户绑定了渠道商
		if (StringUtils.isNotBlank(channelNameEn) && !Constants.CHANNEL_DEFAULT_VALUE.equals(channelNameEn)) {
			ChannelService channelService = SpringContextHolder.getBean(ChannelService.class);
			List<Channel> list = channelService.findChannelByNameEn(channelNameEn);
			if (list!=null && list.size()>0) {
				Channel channel = list.get(0);
				eqChannelId = channel.getId();
			}
		}
	}

}
