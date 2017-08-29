/** 
 * @Package com.uu.modules.om.utils 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月22日 下午3:23:12 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.om.utils;

import java.util.List;

import com.uu.common.utils.CacheUtils;
import com.uu.common.utils.SpringContextHolder;
import com.uu.modules.om.dao.RegionDao;
import com.uu.modules.om.entity.Region;

/** 
 * @Description 区域 工具类
 * @author yifang.huang
 * @date 2016年3月22日 下午3:23:12 
 */
public class RegionUtils {
	
	private static RegionDao regionDao = SpringContextHolder.getBean(RegionDao.class);

	public static final String CACHE_REGION_LIST = "regionList";
	
	public static List<Region> getRegionList() {
		
		@SuppressWarnings("unchecked")
		List<Region> regionList = (List<Region>)CacheUtils.get(CACHE_REGION_LIST);
		if (regionList == null) {
			regionList = regionDao.findAllList();
			CacheUtils.put(CACHE_REGION_LIST, regionList);
		}
		
		return regionList;
	}

}
