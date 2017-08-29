package main.java.com.qlink.modules.user.service;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.mifi.dao.MifiManageDao;
import com.uu.modules.om.dao.ConsumeRecordDao;
import com.uu.modules.om.entity.ConsumeRecord;
import com.uu.modules.om.entity.ConsumeRecord.RecordType;
import com.uu.modules.om.entity.ConsumeRecord.Status;
import com.uu.modules.user.condition.DayPassRecordCondition;
import com.uu.modules.user.dao.DayPassRecordDao;
import com.uu.modules.user.dao.UserBasicInfoDao;
import com.uu.modules.user.dao.UserMifiLinkDao;
import com.uu.modules.user.entity.DayPassRecord;
import com.uu.modules.user.entity.UserBasicInfo;
import com.uu.modules.user.entity.UserMifiLink;
import com.uu.modules.utils.Constants;
import com.uu.modules.utils.ReturnCode;

import net.sf.json.JSONObject;

/**
 * 
 * @author jiangbo
 * @date 2016年3月22日
 */
@Service
public class UserBasicInfoService extends BaseService {

	public static Logger logger = LoggerFactory.getLogger(UserBasicInfoService.class);

	@Autowired
	private UserMifiLinkDao userMifiLinkDao;
	
	@Autowired
	private UserBasicInfoDao userBasicInfoDao;
	
	@Autowired
	private ConsumeRecordDao consumeRecordDao;
	
	@Autowired
	private DayPassRecordDao dayPassRecordDao; 
	
	@Autowired
	private MifiManageDao mifiManageDao;

	public UserBasicInfo findByUserId(String userId){
		return userBasicInfoDao.findByUserId(userId);
	}

	//@Transactional(readOnly = false)
	public void save(UserBasicInfo bean) {
		userBasicInfoDao.save(bean);
	}
	
	/**
	 * 
	 * @Description 用户消费，修改余额并保存消费记录
	 * @param bean
	 * @param money
	 * @param sn
	 * @param ssid
	 * @param country
	 * @param mcc
	 * @param timeDifference 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年3月31日 下午6:26:19
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveOrUpdateConsume(UserBasicInfo bean, Double money, String sn, String ssid, Map<String, String> country, String mcc, int timeDifference) {
		
		// 修改余额
		Double balance = Double.valueOf(bean.getBalance());
		DecimalFormat df1 = new DecimalFormat("0.00"); 
		String balanceStr = df1.format(balance - money);
		bean.setBalance(balanceStr);
		userBasicInfoDao.save(bean);
		
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
		record.setUserId(bean.getUserId());
		record.setRecordType(RecordType.BUY);
		record.setStatus(Status.COMPLETED);
		record.setMoney(money);
		record.setTargetName("用户使用产品消费");
		record.setCountryCode(country.get("countryCode"));
		record.setCountryName(country.get("countryName"));
		record.setMcc(mcc);
		record.setLocalDate(localDate);
		record.setSn(sn);
		record.setSsid(ssid);
		record.setSourceType(Constants.SOURCE_TYPE_MIFI);
		
		consumeRecordDao.save(record);
	}

	
	/**
	 * 
	 * @Description 修改用户开通天数
	 * @param param 请求参数
	 * @param channelCode 渠道编号
	 * @return JSONObject  
	 * @author yifang.huang
	 * @date 2016年10月9日 下午2:24:59
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized JSONObject updateAndModifyUserDayPass(JSONObject param, String channelCode) {
		
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		String errorMessage = "";
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(df.format(new Date()) + "[入参]：" + param.toString());
		
		// 订单编号检测
		if (!param.containsKey("orderId") || StringUtils.isBlank(param.getString("orderId"))) {// 订单ID不能为空
			errorMessage = "|[orderId]Can not be empty!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		String orderId = param.getString("orderId");
		
		// 用户登录名检测
		if (!param.containsKey("loginName") || StringUtils.isBlank(param.getString("loginName"))) {// 用户登录名不能为空
			errorMessage = "|[loginName]Can not be empty!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		String loginName = param.getString("loginName");
		UserBasicInfo user = userBasicInfoDao.findUserByUserIdAndSourceType(loginName, channelCode);// 查询用户是否存在
		if (user == null) {
			errorMessage = "| [user:" + loginName + "]not found!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		
		// 如果是相同的订单号,则直接返回成功
		DayPassRecordCondition condition = new DayPassRecordCondition(true);
		condition.setEqOrderId(orderId);
		condition.setEqSourceType(channelCode);
		List<DayPassRecord> dprList = dayPassRecordDao.findList(condition);
		if (dprList!=null && dprList.size()>0) {	
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			
			resObj.put("dayPass", user.getDayPass());
			return resObj;
		}

		// 设备使用天数检测
		if (!param.containsKey("days") || StringUtils.isBlank(param.getString("days"))) {// 设备开机天数不能为空
			errorMessage = "|[days]Can not be empty!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		String daysStr = param.getString("days");
		int days = 0;
		try {
			days = Integer.valueOf(daysStr);// 开机天数格式判断
			if (days == 0) {
				errorMessage = "|[days=0]There is no sense in doing so!";
				resObj.put("code", "61451");
				resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			errorMessage = "|[days]Only allowed for integer!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_40035 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		int dayPass = user.getDayPass();			// 用户当前的设备使用天数
		
		// 修改用户dayPass
		int resultDays = days + dayPass;
		List<UserMifiLink> userMifiLinkList = userMifiLinkDao.queryUserMifiLinkByUserId(user.getId());
		if (resultDays <= 0) {	// 如果计算结果小于等于0，resultDays=0；修改用户绑定的设备ueallowed=0
			resultDays = 0;
			
			if(null != userMifiLinkList) {
				for(UserMifiLink link : userMifiLinkList) {
					mifiManageDao.updateMifilistUeAllowed(Constants.ueAllowed_n, link.getMifiId());
				}
			}
		} else { // 如果用户dayPass>0,修改用户绑定的设备ueallowed=1
			
			if(null != userMifiLinkList) {
				for(UserMifiLink link : userMifiLinkList) {
					mifiManageDao.updateMifilistUeAllowed(Constants.ueAllowed_y, link.getMifiId());
				}
			}
		}
		
		// 修改用户dayPass值
		user.setDayPass(resultDays);
		userBasicInfoDao.save(user);
		
		// 记录开通天数记录日志
		DayPassRecord record = new DayPassRecord();
		record.setUserId(user.getId());
		record.setLoginName(user.getUserId());
		record.setOrderId(orderId);
		record.setSourceType(channelCode);
		record.setType(DayPassRecord.DAY_PASS_RECORD_RECHARGE);
		record.setDays(days);
		record.setRemarks("开通天数充值,充值前数量[" + dayPass + "],充值数量[" + days + "],充值结果[" + resultDays + "]");
		dayPassRecordDao.save(record);
		
		logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());

		resObj.put("dayPass", resultDays);
		return resObj;
	}
	@SuppressWarnings({ "rawtypes" })
	public static Object getEntityById(String id, BaseDao dao, Object duplicateObj, Logger logger) {
		if (StringUtils.isEmpty(id)) {
			return duplicateObj;
		}
		Object sourceObj = dao.get(id);
		if (null == sourceObj) {
			return duplicateObj;
		}
		try {
			BeanUtils.copyProperties(duplicateObj, sourceObj);
		} catch (Exception e) {
			logger.info("复制实体对象失败，清空hibernate缓存！");
			dao.clear();
			return sourceObj;
		}
		return duplicateObj;
	}
	public UserBasicInfo getuserBasicInfo(String id) {
		UserBasicInfo entity = (UserBasicInfo) getEntityById(id, userBasicInfoDao, new UserBasicInfo(), logger);
		return entity;
	}
	public Page<UserBasicInfo> find(Page<UserBasicInfo> page, UserBasicInfo userBasicInfo) {
		DetachedCriteria dc = userBasicInfoDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(userBasicInfo.getUserId())) {
			dc.add(Restrictions.like("userId", "%" + userBasicInfo.getUserId() + "%"));
		}
		if (StringUtils.isNotEmpty(userBasicInfo.getSourceType())) {
			dc.add(Restrictions.like("sourceType", "%" + userBasicInfo.getSourceType() + "%"));

		}
		if (StringUtils.isNotEmpty(userBasicInfo.getEmail())) {
			dc.add(Restrictions.like("email", "%" + userBasicInfo.getEmail() + "%"));

		}
		if (StringUtils.isNotEmpty(userBasicInfo.getPhone())) {
			dc.add(Restrictions.like("phone", "%" + userBasicInfo.getPhone() + "%"));

		}
		dc.addOrder(Order.desc("updateTime"));
		return userBasicInfoDao.find(page, dc);
	}
	
	
}
