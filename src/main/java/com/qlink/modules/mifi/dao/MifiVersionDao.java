/** 
 * @Package com.uu.modules.mifi.dao 
 * @Description 
 * @author yifang.huang
 * @date 2016年5月24日 下午4:28:41 
 * @version V1.0 
 */
package main.java.com.qlink.modules.mifi.dao;

import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Page;
import com.uu.common.persistence.Parameter;
import com.uu.modules.mifi.condition.MifiVersionCondition;
import com.uu.modules.mifi.entity.MifiVersion;

/**
 * 设备版本号DAO
 * 
 * @author shuxin
 * @date 2016年8月1日
 */
@Repository
public class MifiVersionDao extends BaseDao<MifiVersion> {

	public  int  updataspeedlimitflag(String num){
		return update("update MifiVersion set SPEEDLIMITFLAG = :p1 ", new Parameter(num));
	}
	
	public  int  updataupdateflag(String num){
		return update("update MifiVersion set UPDATEFLAG = :p1 ", new Parameter(num));
	}

	/**
	 * 根据查询参数取列表数据
	 * @Description 
	 * @param condition
	 * @return List<MifiVersion>  
	 * @author yifang.huang
	 * @date 2016年10月26日 上午11:26:52
	 */
	public List<MifiVersion> findListByCondition(MifiVersionCondition condition) {
		
		DetachedCriteria dc = createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return find(dc);
	}
	
	/**
	 * 根据查询参数取分页数据
	 * @Description 
	 * @param page
	 * @param condition
	 * @return Page<MifiVersion>  
	 * @author yifang.huang
	 * @date 2016年10月26日 上午11:28:01
	 */
	public Page<MifiVersion> findPageByCondition(Page<MifiVersion> page, MifiVersionCondition condition) {
		
		DetachedCriteria dc = createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return find(page, dc);
	}
	
	/**
	 * 取id
	 * @Description 
	 * @return int  
	 * @author yifang.huang
	 * @date 2016年10月26日 下午2:04:41
	 */
	public int getMaxId() {
		
		String sql = "SELECT MAX(ID) FROM MIFIVERSION";
		
		List<Object> list = findBySql(sql);
		if (list!=null && list.size()>0) {
			return Integer.valueOf(list.get(0).toString()) + 1;
		}
		
		return 1;
	}
	
	/**
	 * 
	 * @Description 取系统当前设备的最高版本号
	 * @return double  
	 * @author yifang.huang
	 * @date 2016年11月1日 上午10:05:35
	 */
	public double getHighestVersion() {
		
		String sql = "select MAIN_VERSION, hex(YYM_VERSION), hex(YY_DAEMON_VERSION), hex(APN_3G_VERSION), hex(APN_4G_VERSION), hex(COPS_CONF_VERSION), hex(YY_UPDATE_VERSION) "
				+ "from mifiversion a where a.ID=3";
		
		List<Object[]> list = findBySql(sql);
		if (list!=null && list.size()>0) {
			Object[] strArr = list.get(0);
			String mainVersion = ObjectUtils.toString(strArr[0]);
			String str = null;
			Integer highestVersion = 0;
			for (int i=1; i<strArr.length; i++) {
				str = ObjectUtils.toString(strArr[i]);
				str = String.valueOf(str.charAt(str.length() - 1));
				highestVersion += Integer.parseInt(str, 16);
				str = null;
			}
			return Double.valueOf(mainVersion + "." + highestVersion);
		}
		
		return 0;
	}
	
	/**
	 * 
	 * @Description 取设备当前版本号
	 * @param imei 设备编号
	 * @return double  
	 * @author yifang.huang
	 * @date 2016年11月1日 上午10:06:22
	 */
	public double getMifiVersion(String imei) {
		
		String sql = "select MAIN_VERSION, hex(YYM_VERSION), hex(YY_DAEMON_VERSION), hex(APN_3G_VERSION), hex(APN_4G_VERSION), hex(COPS_CONF_VERSION), hex(YY_UPDATE_VERSION), hex(COMPRESSLIB_VERSION) "
				+ "from mifiversion a where a.IMEI_6200='" + imei + "'";
		
		List<Object[]> list = findBySql(sql);
		if (list!=null && list.size()>0) {
			Object[] strArr = list.get(0);
			String mainVersion = ObjectUtils.toString(strArr[0]);
			String str = null;
			Integer version = 0;
			for (int i=1; i<strArr.length; i++) {
				str = ObjectUtils.toString(strArr[i]);
				if (StringUtils.isNotBlank(str) && str.length()==6) {
					str = str.substring(str.length() - 4);
					version += Integer.parseInt(str, 16);
				}
				str = null;
			}
			return Double.valueOf(mainVersion + "." + version);
		}
		
		return 0;
	}
	
}
