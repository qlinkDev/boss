/** 
 * @Package com.uu.modules.om.service 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月22日 上午9:49:41 
 * @version V1.0 
 */ 
package com.uu.modules.om.service;

import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.om.condition.PriceCondition;
import com.uu.modules.om.dao.PriceDao;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.entity.Price;
import com.uu.modules.om.entity.Region;
import com.uu.modules.utils.Constants;

/** 
 * @Description 价格 业务处理
 * @author yifang.huang
 * @date 2016年3月22日 上午9:49:41 
 */
@Service
public class PriceService extends BaseService {

	@Autowired
	private PriceDao priceDao;
	
	@Autowired
	private ChannelService channelService;
	
	public Price get(String id) {
		Price oldBean = priceDao.get(id);
		if (oldBean != null) {
			Price newBean = new Price();
			BeanUtils.copyProperties(oldBean, newBean);
			// 清除指定对象缓存
			priceDao.getSession().evict(oldBean);
			return newBean;
		}
		return null;
	}
	
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void save(Price bean) {
		priceDao.save(bean);
	}
	
	/**
	 * 
	 * @Description 保存区域默认价格信息
	 * @param region 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年3月22日 上午10:39:34
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveByRegion(Region region) {
		
		// 默认渠道
		List<Channel> channelList = channelService.findChannelByNameEn(Constants.CHANNEL_DEFAULT_VALUE);
		
		if (channelList!=null && channelList.size()>0) {
			
			Channel channel = channelList.get(0);
			
			Price price = new Price();
			price.setRegion(region);
			price.setChannel(channel);
			price.setPrice(region.getDefaultPrice());
			priceDao.save(price);
		}
	}
	
	/**
	 * 
	 * @Description 保存区域和渠道对应价信息
	 * @param regionId 渠道ID（如果为空表示新增）
	 * @param priceList 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年3月22日 上午10:50:13
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveByRegionAndChannel(String channelId, List<Price> priceList) {
		// 如果是修改渠道信息，则先删除已保存的渠道价格信息
		/*if (StringUtils.isNotBlank(channelId)) {	
			
			// 根据渠道查询价格列表
			PriceCondition condition = new PriceCondition();
			condition.setEqChannelId(channelId);
			
			List<Price> list = findListByCondition(condition);
			if (list!=null && list.size()>0) {
				for (Price price : list) {
					priceDao.deleteById(price.getId());
				}
			}
		}*/
		
		// 保存价格信息
		priceDao.save(priceList);
		
	}
	
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void delete(String id) {
		priceDao.deleteById(id);
	}
	
	/**
	 * 根据渠道ID删除价格
	 * @param id
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void deleteByChannelID(String channelID) {
		priceDao.deleteByChannelID(channelID);
	}
	
	/**
	 * 根据渠道ID获取价格
	 * 
	 * @param channelID
	 * @return
	 */
	public List<String[]> findListByChannelID(String channelID, String channelNameEn) {
		return priceDao.findListByChannelID(channelID, channelNameEn);
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
		return priceDao.findList(channelId, regionId);
	}

	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<Price>  
	 * @author yifang.huang
	 * @date 2016年3月22日 上午9:56:39
	 */
	public List<Price> findListByCondition(PriceCondition condition) {
		
		DetachedCriteria dc = priceDao.createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return priceDao.find(dc);
	}
	
	/**
	 * 
	 * @Description 根据查询参数取分页数据
	 * @param page
	 * @param condition
	 * @return Page<Price>  
	 * @author yifang.huang
	 * @date 2016年3月22日 上午9:56:29
	 */
	public Page<Price> find(Page<Price> page, PriceCondition condition) {
		
		DetachedCriteria dc = priceDao.createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return priceDao.find(page, dc);
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
		return priceDao.findMccPriceMapList(channelId);
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
		return priceDao.findChannelPriceList(channelNameEn);
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
		return priceDao.findMccByCountryCode(countryCode);
	}
	
}
