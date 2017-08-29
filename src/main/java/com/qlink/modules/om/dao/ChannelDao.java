/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package main.java.com.qlink.modules.om.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Parameter;
import com.uu.modules.om.entity.Channel;

/**
 * 渠道DAO接口
 * @author yuxiaoyu
 * @version 2016-3-18
 */
@Repository
public class ChannelDao extends BaseDao<Channel> {
	
	public List<Channel> findAllList() {
		return find("from Channel where delFlag=:p1 order by id", new Parameter(Channel.DEL_FLAG_NORMAL));
	}
	
	public List<String[]> findAllMCC() {
		return findBySql("select group_concat(mcc) mcces, country_name_cn, country_code from mcc_def group by country_code, country_name_cn order by convert(country_name_cn using gbk) asc");
	}
	
	/**
	 * 
	 * @Description 根据渠道编号取渠道
	 * @param channelNameEn
	 * @return List<Channel>  
	 * @author yifang.huang
	 * @date 2016年5月9日 下午4:16:17
	 */
	public List<Channel> findList(String channelNameEn) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("channelNameEn", channelNameEn));
		dc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
		return find(dc);
	}
}
