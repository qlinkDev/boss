/** 
 * @Package com.uu.modules.om.utils 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月22日 下午3:32:02 
 * @version V1.0 
 */ 
package com.uu.modules.om.utils;

import java.util.List;

import com.uu.common.utils.CacheUtils;
import com.uu.common.utils.SpringContextHolder;
import com.uu.modules.om.dao.ChannelDao;
import com.uu.modules.om.entity.Channel;

/** 
 * @Description 渠道 工具类
 * @author yifang.huang
 * @date 2016年3月22日 下午3:32:02 
 */
public class ChannelUtils {
	
	private static ChannelDao channelDao = SpringContextHolder.getBean(ChannelDao.class);

	public static final String CACHE_CHANNEL_LIST = "channelList";
	
	public static List<Channel> getChannelList() {
		
		@SuppressWarnings("unchecked")
		List<Channel> channelList = (List<Channel>)CacheUtils.get(CACHE_CHANNEL_LIST);
		if (channelList == null) {
			channelList = channelDao.findAllList();
			CacheUtils.put(CACHE_CHANNEL_LIST, channelList);
		}
		
		return channelList;
	}

}
