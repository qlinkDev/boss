/** 
 * @Package com.uu.modules.user.service 
 * @Description 
 * @author yifang.huang
 * @date 2016年12月6日 上午11:50:15 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.user.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.common.utils.HttpRequest;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.utils.DictUtils;
import com.uu.modules.sys.utils.UserUtils;
import com.uu.modules.user.condition.DayPassRecordCondition;
import com.uu.modules.user.dao.DayPassRecordDao;
import com.uu.modules.user.entity.DayPassRecord;
import com.uu.modules.utils.Constants;

/** 
 * @Description 用户开通天数记录(增加减少) 业务处理类
 * @author yifang.huang
 * @date 2016年12月6日 上午11:50:15 
 */
@Service
public class DayPassRecordService extends BaseService {

	@Autowired
	private DayPassRecordDao dayPassRecordDao;
	
	/**
	 * 
	 * @Description 根据ID取数据
	 * @param id
	 * @return DayPassRecord  
	 * @author yifang.huang
	 * @date 2016年12月6日 上午11:53:52
	 */
	public DayPassRecord get(String id) {
		DayPassRecord oldBean = dayPassRecordDao.get(id);
		if (oldBean != null) {
			DayPassRecord newBean = new DayPassRecord();
			BeanUtils.copyProperties(oldBean, newBean);
			
			// 清除指定对象缓存
			dayPassRecordDao.getSession().evict(oldBean);
			
			return newBean;
		}
		return null;
	}

	/**
	 * 
	 * @Description 保存
	 * @param id 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年12月6日 上午11:54:02
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void save(DayPassRecord bean) {
		dayPassRecordDao.save(bean);
	}

	/**
	 * 
	 * @Description 根据ID删除数据
	 * @param id 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年12月6日 上午11:54:02
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void delete(String id) {
		dayPassRecordDao.deleteById(id);
	}
	
	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<DayPassRecord>  
	 * @author yifang.huang
	 * @date 2016年12月6日 上午11:54:10
	 */
	public List<DayPassRecord> findList(DayPassRecordCondition condition) {
		
		return dayPassRecordDao.findList(condition);
		
	}
	
	/**
	 * 
	 * @Description 根据查询参数取分页数据
	 * @param page
	 * @param condition
	 * @return Page<DayPassRecord>  
	 * @author yifang.huang
	 * @date 2016年12月6日 上午11:54:17
	 */
	public Page<DayPassRecord> findPage(Page<DayPassRecord> page, DayPassRecordCondition condition) {
		
		return dayPassRecordDao.findPage(page, condition);
		
	}
	
	/**
	 * 
	 * @Description 消费记录回调
	 * @param paramMap
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年12月8日 下午5:15:19
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized Map<String, String> saveAndCallback(Map<String, Object> paramMap) {
		
		Map<String, String> map = new HashMap<String, String>();
		User user = UserUtils.getUser();
		String sourceType = user.getChannelNameEn();
		
		// 参数
		String recordId = ObjectUtils.toString(paramMap.get("recordId"));
		
		// 参数判断
		if (StringUtils.isBlank(recordId)) {
			map.put("code", "-1");
			map.put("msg", "请选择要手动回调的消费记录！");
			return map;
		}
		// 记录对象
		DayPassRecord record = dayPassRecordDao.get(recordId);	
		if (record == null) {
			map.put("code", "-1");
			map.put("msg", "消费记录未找到");
			return map;
		}
		// 消费记录所属判断
		if (user.getChannelAdmin() && !sourceType.equals(record.getSourceType())) {
			map.put("code", "-1");
			map.put("msg", "无操作权限，请与客服联系！");
			return map;
		}
		
		// 消费记录类型判断
		if (!record.getType().equals(DayPassRecord.DAY_PASS_RECORD_CONSUME)) {
			map.put("code", "-1");
			map.put("msg", "当前记录不是消费记录,无法执行回调操作!");
			return map;
		} 
		if (record.getStatus().equals(Constants.CONSTANTS_STATUS_SUCCESS)) {
			map.put("code", "-1");
			map.put("msg", "当前消费记录已回调成功,不能重复操作!");
			return map;
		}
		
		try {
			// 渠道商回调接口地址
			String callbackUrl = DictUtils.getDictValue(sourceType + "_" + Constants.DICT_CALLBACK_URL_LABEL, sourceType + "_" + Constants.DICT_CALLBACK_URL_TYPE, "https://www.geefi.co/api/notifyDaypassUsage.php");
			logger.info("DayPassRecord回调地址:" + callbackUrl);

			StringBuffer param = new StringBuffer("mifiId=" + record.getImei());
			param.append("&mcc=" + record.getMcc());
			param.append("&uuid=" + record.getId());
			param.append("&loginName=" + record.getLoginName());
			param.append("&countryName=" + record.getCountryNameEn());
			
			String result = HttpRequest.sendURLPost(callbackUrl, param.toString());
			logger.info("DayPassRecord[" + record.getId() + "],回调结果:" + result);
			
			JSONObject json = new JSONObject(result); 
			if ("200".equals(json.get("code"))) { // 如果回调成功，将状态修改成'已处理'
				record.setStatus(Constants.CONSTANTS_STATUS_SUCCESS);
				map.put("code", "1");
				map.put("msg", "消费记录回调成功！");
			} else {
				map.put("code", "-1");
				map.put("msg", "消费记录回调失败！");
			}
			record.setCallbackDate(new Date());
			dayPassRecordDao.save(record);
		} catch (JSONException e) {
			e.printStackTrace();
			map.put("code", "1");
			map.put("msg", "消费记录回调失败！");
		}
		
		return map;
	}
}
