/** 
 * @Package com.uu.modules.om.dao 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月22日 上午9:42:15 
 * @version V1.0 
 */
package main.java.com.qlink.modules.om.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.BaseEntity;
import com.uu.common.persistence.Parameter;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.entity.Price;
import com.uu.modules.utils.Constants;

/**
 * @Description 价格 DAO
 * @author yifang.huang
 * @date 2016年3月22日 上午9:42:15
 */
@Repository
public class PriceDao extends BaseDao<Price> {
	
	@Autowired
	private ChannelDao channelDao;
	
	/**
	 * 根据渠道ID获取价格
	 * 
	 * @param channelID
	 * @return
	 */
	public List<String[]> findListByChannelID(String channelID, String channelNameEn) {
		String sqlStr = "select * from ( select ifnull(r.id, l.id)id, ifnull(r.channel_id, l.channel_id)channel_id, "
				+ "ifnull(r.region_id, l.region_id)region_id, ifnull(r.price, l.price)price, ifnull(r.create_by, l.create_by)create_by, "
				+ "ifnull(r.create_date,l.create_date)create_date, ifnull(r.update_by, l.update_by)update_by,  ifnull(r.update_date,l.update_date)update_date, "
				+ "ifnull(r.remarks, l.remarks)remarks, ifnull(r.del_flag, l.del_flag)del_flag, reg.`name` `name`, ifnull(r.id, 'true') need_create "
				+ "from om_region reg, (select prc.* from om_channel chl, om_price prc where chl.channel_name_en = :p1 and "
				+ "chl.del_flag = '0' and prc.del_flag = '0' and chl.id = prc.channel_id "
				+ "AND (prc.contain_channel='ALL' OR find_in_set(:p2, prc.contain_channel)>0)) l  "
				+ "left join (select * from om_price where channel_id = :p3 and del_flag = '0') r on l.region_id = r.region_id "
				+ "where reg.del_flag = '0' and reg.id = l.region_id ) t  order by t.region_id asc";
		return this.findBySql(sqlStr, new Parameter(Constants.CHANNEL_DEFAULT_VALUE, channelNameEn, channelID));
	}

	/**
	 * 根据渠道ID删除价格
	 * 
	 * @param channelID
	 * @return
	 */
	public int deleteByChannelID(String channelID) {
		return this.update("update Price set delFlag='" + BaseEntity.DEL_FLAG_DELETE + "' where channel_id = :p1", 
				new Parameter(channelID));
	}
	
	/**
	 * 
	 * @Description 取价格列表
	 * @param channelId
	 * @param regionId
	 * @return List<Price>  
	 * @author yifang.huang
	 * @date 2016年5月9日 下午2:30:37
	 */
	public List<Price> findList(String channelId, String regionId) {
		
		if (StringUtils.isBlank(channelId) && StringUtils.isBlank(regionId)) 
			return null;
		
		List<Price> pList = null;
		DetachedCriteria pDc = createDetachedCriteria();
		if (StringUtils.isNotBlank(regionId))
			pDc.add(Restrictions.eq("region.id", regionId));
		if (StringUtils.isNotBlank(channelId))
			pDc.add(Restrictions.eq("channel.id", channelId));
		pDc.add(Restrictions.eq("downShelf", Price.DOWN_SHELF_UP));
		pDc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
		pList = find(pDc);
		
		if (pList==null || pList.size()==0) {
			pDc = createDetachedCriteria();
			if (StringUtils.isNotBlank(regionId))
				pDc.add(Restrictions.eq("region.id", regionId));
			// 取默认渠道
			DetachedCriteria cDc = channelDao.createDetachedCriteria();
			cDc.add(Restrictions.eq("channelNameEn", Constants.CHANNEL_DEFAULT_VALUE));
			cDc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
			List<Channel> cList = channelDao.find(cDc);
			if (cList!=null && cList.size()>0) {
				pDc.add(Restrictions.eq("channel.id", cList.get(0).getId()));
			}
			pDc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
			pList = find(pDc);
		}
		return pList;
	}
	
	/**
	 * 
	 * @Description 取渠道的mcc对应的价格列表
	 * @param channelId
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2016年6月16日 下午4:33:20
	 */
	public List<Map<String, Object>> findMccPriceMapList(String channelId) {
		
		if (StringUtils.isBlank(channelId))
			return null;
		
		String sql = "select r.mcces, p.price from om_price p, om_region r where p.region_id=r.id and p.channel_id='" + channelId + "' and p.del_flag=0 and r.del_flag=0";

		return findBySql(sql, null, Map.class);
		
	}

	/**
	 * 
	 * @Description 根据国家编号取MCC
	 * @param countryCode
	 * @return HashMap<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月3日 下午3:13:19
	 */
	public Map<String, String> findMccByCountryCode(String countryCode) {
		
		List<Map<String, String>> list = findBySql("select group_concat(mcc) mcces, country_code countryCode, country_name_cn countryName from mcc_def where country_code=:p1", new Parameter(countryCode), Map.class);
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @Description 根据渠道编号取渠道价格列表（如果是运营商，没有配置价格的取取默认渠道的价格）
	 * @param channelNameEn
	 * @return List<Price>  
	 * @author yifang.huang
	 * @date 2016年6月17日 下午4:01:48
	 */
	public List<Price> findChannelPriceList(String channelNameEn) {
		
		if (StringUtils.isBlank(channelNameEn))
			return null;
		
		// 默认渠道对象
		List<Channel> channelList = channelDao.findList(Constants.CHANNEL_DEFAULT_VALUE);
		Channel defaultChannel = channelList.get(0);
		
		// 如果是默认渠道
		if (Constants.CHANNEL_DEFAULT_VALUE.equals(channelNameEn)) {
			DetachedCriteria pDc = createDetachedCriteria();
			pDc.add(Restrictions.eq("channel.id", defaultChannel.getId()));
			pDc.add(Restrictions.eq("downShelf", Price.DOWN_SHELF_UP));
			pDc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
			List<Price> priceList = find(pDc);
			removePrice(priceList, channelNameEn);
			return priceList;
		} 
		
		// 如果是运营商
		channelList = channelDao.findList(channelNameEn);
		Channel channel = channelList.get(0);
		// 默认渠道价格列表
		DetachedCriteria pDc = createDetachedCriteria();
		pDc.add(Restrictions.eq("channel.id", defaultChannel.getId()));
		pDc.add(Restrictions.eq("downShelf", Price.DOWN_SHELF_UP));
		pDc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
		List<Price> defaultList = find(pDc);
		// 运营商已配置的价格列表
		pDc = createDetachedCriteria();
		pDc.add(Restrictions.eq("channel.id", channel.getId()));
		pDc.add(Restrictions.eq("downShelf", Price.DOWN_SHELF_UP));
		pDc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
		List<Price> list = find(pDc);
		// 组装返回值
		List<Price> priceList = new ArrayList<Price>();
		priceList.addAll(list);
		boolean include = false;
		for (Price p1 : defaultList) {
			for (Price p2 : list) {
				if (p1.getRegion().getId().equals(p2.getRegion().getId())) {
					include = true;
					break;
				}
			}
			if (!include)
				priceList.add(p1);
			include = false;
		}

		return removePrice(priceList, channelNameEn);
		
	}
	
	// 删除不允许渠道使用的价格
	private List<Price> removePrice(List<Price> priceList, String channelNameEn) {
		
		if (priceList==null || priceList.size()==0)
			return null;
		
		List<Price> tempList = new ArrayList<Price>();
		for (Price price : priceList) {
			String contailChannel = price.getContainChannel();
			if (StringUtils.isBlank(contailChannel) || "ALL".equals(contailChannel)) {
				tempList.add(price);
				continue;
			} else {
				String[] contailChannelArr = contailChannel.split(",");
				for (String str : contailChannelArr) {
					if (str.equals(channelNameEn)) {
						tempList.add(price);
						break;
					}
				}
			}
		}
		
		return tempList;
	}
}
