/** 
 * @Package com.uu.modules.mifi.service 
 * @Description 
 * @author yifang.huang
 * @date 2016年12月7日 上午11:38:43 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.mifi.condition.MifiBootCondition;
import com.uu.modules.mifi.dao.MifiBootDao;
import com.uu.modules.mifi.dao.MifiManageDao;
import com.uu.modules.mifi.entity.MifiBoot;
import com.uu.modules.user.dao.DayPassRecordDao;
import com.uu.modules.user.dao.UserBasicInfoDao;
import com.uu.modules.user.dao.UserMifiLinkDao;
import com.uu.modules.user.entity.DayPassRecord;
import com.uu.modules.user.entity.UserBasicInfo;
import com.uu.modules.user.entity.UserMifiLink;
import com.uu.modules.utils.Constants;

/** 
 * @Description MIFI开机(一天一条MIFI状态为4记录) 业务处理类
 * @author yifang.huang
 * @date 2016年12月7日 上午11:38:43 
 */
@Service
public class MifiBootService extends BaseService {
	
	@Autowired
	private MifiBootDao mifiBootDao;
	
	@Autowired
	private UserMifiLinkDao userMifiLinkDao;
	
	@Autowired
	private MifiManageDao mifiManageDao;

	@Autowired
	private UserBasicInfoDao userBasicInfoDao;
	
	@Autowired
	private DayPassRecordDao dayPassRecordDao;

	/**
	 * 
	 * @Description 开机记录处理
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年12月7日 下午4:41:18
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized void saveAndHandleMifiBoot() {
		// 查询状态为'NEW'的设备开机记录列表
		MifiBootCondition condition = new MifiBootCondition();
		condition.setEqStatus(MifiBoot.STATUS_NEW);
		List<MifiBoot> bootList = mifiBootDao.findList(condition);
		
		if (bootList!=null && bootList.size()>0) {
			String imei = "";
			UserMifiLink link = null;
			UserBasicInfo user = null;
			DayPassRecord record = null;
			Map<String, String> countryMap = null;
			for (MifiBoot boot : bootList) {

				// 开机记录改成 已处理
				boot.setStatus(MifiBoot.STATUS_HANDLED);
				mifiBootDao.save(boot);
				
				imei = boot.getImei();
				if (StringUtils.isBlank(imei))
					continue;
				
				// 设备与用户绑定对象
				link = userMifiLinkDao.queryUserMifiLinkById(null, imei);
				if (link == null) {	// 设备未绑定用户,将设备的ueallowed=0,ueallowedmcc='homeForbidden';
					mifiManageDao.updateMifilist(Constants.ueAllowed_n, "homeForbidden", imei);
					continue;
				} 
				
				// 用户信息
				user = userBasicInfoDao.findById(link.getUserId());
				if (user == null)
					continue;
				int dayPass = user.getDayPass();
				if (dayPass < 1) { // 如果用户dayPass<1,将设备的ueallowed=0,ueallowedmcc='homeForbidden';
					mifiManageDao.updateMifilist(Constants.ueAllowed_n, "homeForbidden", imei);
					// 删除当前记录，用户充值后继续开机重新记录
					mifiBootDao.delById(boot.getId());
					continue;
				}
				
				// 修改用户的dayPass
				user.setDayPass(dayPass - 1);
				userBasicInfoDao.save(user);
				
				// 记录dayPass消费记录
				record = new DayPassRecord();
				record.setUserId(user.getId());
				record.setLoginName(user.getUserId());
				record.setOrderId(boot.getId());
				record.setImei(boot.getImei());
				record.setMcc(boot.getMcc());
				record.setSourceType(user.getSourceType());
				record.setType(DayPassRecord.DAY_PASS_RECORD_CONSUME);
				record.setDays(-1);
				record.setRemarks("用户消费,消费前数量[" + dayPass + "],消费数量[" + -1 + "],消费结果[" + (dayPass - 1) + "]");
				record.setStatus(Constants.CONSTANTS_STATUS_FAIL);
				// 取国家信息
				countryMap = mifiBootDao.findCountryByMcc(boot.getMcc());
				if (countryMap != null) {
					record.setCountryCode(countryMap.get("countryCode"));
					record.setCountryNameCn(countryMap.get("countryName"));
					record.setCountryNameEn(countryMap.get("countryNameEn"));
				}
				dayPassRecordDao.save(record);
				
				imei = null;
				link = null;
				user = null;
				record = null;
				countryMap = null;
			}
		}
	}
	

	/**
	 * 
	 * @Description 根据ID取数据
	 * @param id
	 * @return MifiBoot  
	 * @author yifang.huang
	 * @date 2016年12月7日 上午11:42:20
	 */
	public MifiBoot get(String id) {
		MifiBoot oldBean = mifiBootDao.get(id);
		if (oldBean != null) {
			MifiBoot newBean = new MifiBoot();
			BeanUtils.copyProperties(oldBean, newBean);
			
			// 清除指定对象缓存
			mifiBootDao.getSession().evict(oldBean);
			
			return newBean;
		}
		return null;
	}

	/**
	 * 
	 * @Description 保存
	 * @param bean 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年12月7日 上午11:42:30
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void save(MifiBoot bean) {
		mifiBootDao.save(bean);
	}

	/**
	 * 
	 * @Description 根据ID删除数据
	 * @param id 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年12月7日 上午11:42:46
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void delete(String id) {
		mifiBootDao.deleteById(id);
	}
	
	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<MifiBoot>  
	 * @author yifang.huang
	 * @date 2016年12月7日 上午11:42:53
	 */
	public List<MifiBoot> findList(MifiBootCondition condition) {
		
		return mifiBootDao.findList(condition);
		
	}
	
	/**
	 * 
	 * @Description 根据查询参数取分页数据
	 * @param page
	 * @param condition
	 * @return Page<MifiBoot>  
	 * @author yifang.huang
	 * @date 2016年12月7日 上午11:43:02
	 */
	public Page<MifiBoot> findPage(Page<MifiBoot> page, MifiBootCondition condition) {
		
		return mifiBootDao.findPage(page, condition);
		
	}
	
}
