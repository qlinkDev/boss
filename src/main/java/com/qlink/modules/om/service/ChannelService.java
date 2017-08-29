/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package main.java.com.qlink.modules.om.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.common.utils.CacheUtils;
import com.uu.common.utils.StringUtils;
import com.uu.modules.om.dao.ChannelDao;
import com.uu.modules.om.dao.ConsumeRecordDao;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.entity.ConsumeRecord;
import com.uu.modules.om.entity.ConsumeRecord.RecordType;
import com.uu.modules.om.entity.ConsumeRecord.Status;
import com.uu.modules.om.utils.ChannelUtils;
import com.uu.modules.utils.Constants;

/**
 * 渠道Service
 * 
 * @author yuxiaoyu
 * @version 2016-3-18
 */
@Service
public class ChannelService extends BaseService {

	@Autowired
	private ChannelDao channelDao;
	
	@Autowired
	private ConsumeRecordDao consumeRecordDao;

	public Channel getChannel(String id) {
		Channel oldBean = channelDao.get(id);
		if (oldBean != null) {
			Channel newBean = new Channel();
			BeanUtils.copyProperties(oldBean, newBean);
			// 清除指定对象缓存
			channelDao.getSession().evict(oldBean);
			return newBean;
		}
		return null;
	}

	/**
	 * 查询、导出
	 * 
	 * @param page
	 * @param channel
	 * @return
	 */
	public Page<Channel> findChannel(Page<Channel> page, Channel channel) {
		DetachedCriteria dc = channelDao.createDetachedCriteria();

		if (StringUtils.isNotEmpty(channel.getChannelName())) {
			dc.add(Restrictions.like("channelName", "%" + channel.getChannelName() + "%"));
		}
		if (StringUtils.isNotEmpty(channel.getChannelNameEn())) {
			dc.add(Restrictions.like("channelNameEn", "%" + channel.getChannelNameEn() + "%"));
		}
		if (StringUtils.isNotEmpty(channel.getPayType())) {
			dc.add(Restrictions.eq("payType", channel.getPayType()));
		}

		dc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
		if (StringUtils.isEmpty(page.getOrderBy())){
			dc.addOrder(Order.desc("updateDate"));
		}

		return channelDao.find(page, dc);
	}

	/**
	 * 根据名称查询渠道
	 * @param channelName
	 * @return
	 */
	public List<Channel> findChannelByName(String channelName) {
		DetachedCriteria dc = channelDao.createDetachedCriteria();
		dc.add(Restrictions.eq("channelName", channelName));
		dc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
		return channelDao.find(dc);
	}
	
	/**
	 * 根据英文名称查询渠道
	 * @param channelNameEn
	 * @return
	 */
	public List<Channel> findChannelByNameEn(String channelNameEn) {
		DetachedCriteria dc = channelDao.createDetachedCriteria();
		dc.add(Restrictions.eq("channelNameEn", channelNameEn));
		dc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
		return channelDao.find(dc);
	}
	
	/**
	 * 根据英文名称查询渠道
	 * @param channelNameEn
	 * @return
	 */
	public List<Channel> findChannelList() {
		DetachedCriteria dc = channelDao.createDetachedCriteria();
		dc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
		return channelDao.find(dc);
	}
	
	/**
	 * 根据是否需要生成csv文件查询渠道
	 * @param createCsvFile
	 * @return
	 */
	public List<Channel> findChannelByCreateCsvFile(String createCsvFile) {
		DetachedCriteria dc = channelDao.createDetachedCriteria();
		dc.add(Restrictions.eq("createCsvFile", createCsvFile));
		dc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
		return channelDao.find(dc);
	}

	/**
	 * 查询MCC
	 * 
	 * @return
	 */
	public List<String[]> findAllMCC() {
		return channelDao.findAllMCC();
	}

	/**
	 * 保存
	 * 
	 * @param channel
	 */
	//@Transactional(readOnly = false)
	public void saveChannel(Channel channel) {
		channelDao.clear();
		channelDao.save(channel);
		CacheUtils.remove(ChannelUtils.CACHE_CHANNEL_LIST);
	}

	/**
	 * 删除
	 * 
	 * @param id
	 */
	//@Transactional(readOnly = false)
	public void deleteChannel(String id) {
		channelDao.deleteById(id);
		CacheUtils.remove(ChannelUtils.CACHE_CHANNEL_LIST);
	}
	
	/**
	 * 
	 * @Description 渠道消费，修改余额并保存消费记录
	 * @param channel
	 * @param money
	 * @param sn
	 * @param ssid
	 * @param country
	 * @param mcc
	 * @param timeDifference 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年3月31日 下午6:19:22
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveOrUpdateConsume(Channel channel, Double money, String sn, String ssid, Map<String, String> country, String mcc, int timeDifference) {
		
		// 修改余额
		Double balance = channel.getBalance();		
		channel.setBalance(balance - money);
		channelDao.save(channel);
		
		// 当地时间
		Date localDate = new Date();
		if (timeDifference != 0) {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.add(Calendar.HOUR, timeDifference);
			localDate = c.getTime();
		}
		// 保存消费记录
		ConsumeRecord record = new ConsumeRecord();
		record.setChannel(channel);
		record.setRecordType(RecordType.BUY);
		record.setStatus(Status.COMPLETED);
		record.setMoney(money);
		record.setTargetName("联网消费");
		record.setCountryCode(country.get("countryCode"));
		record.setCountryName(country.get("countryName"));
		record.setMcc(mcc);
		record.setLocalDate(localDate);
		record.setSn(sn);
		record.setSsid(ssid);
		record.setSourceType(Constants.SOURCE_TYPE_MIFI);
		
		consumeRecordDao.save(record);
	}
}
