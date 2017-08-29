package main.java.com.qlink.modules.user.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.StringUtils;
import com.uu.modules.mifi.dao.MifiManageDao;
import com.uu.modules.user.dao.UserBasicInfoDao;
import com.uu.modules.user.dao.UserMifiLinkDao;
import com.uu.modules.user.entity.UserBasicInfo;
import com.uu.modules.user.entity.UserMifiLink;
import com.uu.modules.utils.ReturnCode;

import net.sf.json.JSONObject;

/**
 * 
 * @author jiangbo
 * @date 2016年3月22日
 */
@Service
public class UserMifiLinkService extends BaseService {

	public static Logger logger = LoggerFactory.getLogger(UserMifiLinkService.class);

	@Autowired
	private UserMifiLinkDao userMifiLinkDao;
	@Autowired
	private UserBasicInfoDao userBasicInfoDao;

	@Autowired
	private MifiManageDao mifiManageDao;

	public Page<UserMifiLink> find(Page<UserMifiLink> page, UserMifiLink userMifiLink,Map<String, Object> paramMap) {
		DetachedCriteria dc = userMifiLinkDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(userMifiLink.getUserId())){
			dc.add(Restrictions.like("userId", "%"+userMifiLink.getUserId()+"%"));
		}
		if (StringUtils.isNotEmpty(userMifiLink.getMifiId())){
			dc.add(Restrictions.like("mifiId", "%"+userMifiLink.getMifiId()+"%"));
		}
		Date beginDate = DateUtils.parseDate(paramMap.get("beginDate"));
		if (beginDate == null){
			beginDate = DateUtils.setDays(new Date(), 1);
			paramMap.put("beginDate", DateUtils.formatDate(beginDate, "yyyy-MM-dd"));
		}
		Date endDate = DateUtils.parseDate(paramMap.get("endDate"));
		if (endDate == null){
			endDate = DateUtils.addDays(DateUtils.addMonths(beginDate, 1), -1);
			paramMap.put("endDate", DateUtils.formatDate(endDate, "yyyy-MM-dd"));
		} 
		endDate = DateUtils.addDays(endDate,1);
		dc.add(Restrictions.between("linkTime", beginDate, endDate));
		
		dc.addOrder(org.hibernate.criterion.Order.desc("linkTime"));
		return userMifiLinkDao.find(page, dc);
	}
	
	public Page<HashMap> findLinkAndUser(Page<HashMap> page, UserMifiLink userMifiLink,Map<String, Object> paramMap) {
		String sqlString = "select uml.*, ubi.user_name,ubi.user_id id ";
		sqlString += " from user_mifi_link uml,user_basic_info ubi ";
		sqlString += " where uml.user_id = ubi.id";
		
		if (paramMap.containsKey("userId") && StringUtils.isNotBlank(paramMap.get("userId").toString())) {
			sqlString += " and ubi.user_id like '%" + paramMap.get("userId").toString() + "%'";
		}
		if (paramMap.containsKey("userName") && StringUtils.isNotBlank(paramMap.get("userName").toString())) {
			sqlString += " and ubi.user_name like '%" + paramMap.get("userName").toString() + "%'";
		}
		if (paramMap.containsKey("mifiId") && StringUtils.isNotBlank(paramMap.get("mifiId").toString())) {
			sqlString += " and  uml.mifi_id like '%" + paramMap.get("mifiId").toString() + "%'";
		}
		Date beginDate = DateUtils.parseDate(paramMap.get("beginDate"));
		Date endDate = DateUtils.parseDate(paramMap.get("endDate"));
		if(beginDate!=null){
		sqlString += " and date(uml.link_time) >= str_to_date('" + DateUtils.formatDate(beginDate, "yyyy-MM-dd") + "','%Y-%m-%d')";
		}
		if(endDate!=null){
		sqlString += " and date(uml.link_time) <= str_to_date('" + DateUtils.formatDate(endDate, "yyyy-MM-dd") + "','%Y-%m-%d')";
		}
		/*if (paramMap.containsKey("beginDate") && StringUtils.isNotBlank(paramMap.get("beginDate").toString())) {
			sqlString += " and date(uml.link_time) >= str_to_date('" + paramMap.get("beginDate").toString() + "','%Y-%m-%d')";
		} else {
			beginDate = DateUtils.setDays(new Date(), 1).toString();
			paramMap.put("beginDate", DateUtils.formatDate(beginDate, "yyyy-MM-dd"));
		}
		if (paramMap.containsKey("endDate") &&  StringUtils.isNotBlank(paramMap.get("endDate").toString())) {
			sqlString += " and date(uml.link_time) <= str_to_date('" + paramMap.get("endDate").toString() + "','%Y-%m-%d')";
		}*/
		return userMifiLinkDao.findBySql(page,sqlString,Map.class);
	}
	
	
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public JSONObject saveUserMifiLink(JSONObject param) {
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		String errorMessage = "";
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(df.format(new Date()) + "[入参]：" + param.toString());

		if (!param.containsKey("userid") || StringUtils.isBlank(param.getString("userid"))) {
			errorMessage = "|[用户标识]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		if (!param.containsKey("mifiid") || StringUtils.isBlank(param.getString("mifiid"))) {
			errorMessage = "|[mifiId]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		//保存用户和设置关联表逻辑
		String userId = param.getString("userid");
		String mifiId = param.getString("mifiid");
		if (0 == userBasicInfoDao.getMifiListCountByDsn(mifiId)) {
			errorMessage = "[设备序列号:" + mifiId + "]无对应设备数据!";
			resObj.put("code", "-1");
			resObj.put("msg", errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		UserMifiLink _userMifiLink = userMifiLinkDao.queryUserMifiLinkById(userId, mifiId);
		if (null != _userMifiLink) {
			errorMessage = "用户和设备已经绑定过!";
			resObj.put("code", "-1");
			resObj.put("msg", errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		} else {
			//保存userMifiLink
			UserMifiLink userMifiLink = new UserMifiLink();
			userMifiLink.setUserId(userId);
			userMifiLink.setMifiId(mifiId);
			userMifiLink.setLinkTime(new Date());
			userMifiLinkDao.save(userMifiLink);
		}
		
		//保存用户基本信息表逻辑
		UserBasicInfo _userBasicInfo = userBasicInfoDao.queryUserBasicInfoByUserId(userId);
		UserBasicInfo userBasicInfo = new UserBasicInfo();
		Date now = new Date();
		//如果此用户不存在，则插入创建时间
		if(_userBasicInfo == null) {
			userBasicInfo.setCreateTime(now);
		} else {
			userBasicInfo.setUserName(_userBasicInfo.getUserName());
			userBasicInfo.setEmail(_userBasicInfo.getEmail());
			userBasicInfo.setBalance(_userBasicInfo.getBalance());
			userBasicInfo.setTotalDeposit(_userBasicInfo.getTotalDeposit());
			userBasicInfo.setCreateTime(_userBasicInfo.getCreateTime());
			userBasicInfo.setUpdateTime(now);
		}
		
		userBasicInfo.setUserId(userId);
		if(param.containsKey("username") && StringUtils.isNotBlank(param.getString("username"))) {
			userBasicInfo.setUserName(param.getString("username"));
		}
		if(param.containsKey("email") && StringUtils.isNotBlank(param.getString("email"))) {
			userBasicInfo.setEmail(param.getString("email"));
		}
		if(param.containsKey("phone") && StringUtils.isNotBlank(param.getString("phone"))) {
			userBasicInfo.setPhone(param.getString("phone"));
		}
		userBasicInfoDao.getSession().merge(userBasicInfo);
		
		//更新mifilist和mifi_basic_info表
		userBasicInfoDao.mifiTypeUpdate(userId, mifiId);

		logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
		return resObj;
	}
	
	public JSONObject getUserLinkInfo(String userId) {
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		UserBasicInfo userBasicInfo = userBasicInfoDao.queryUserBasicInfoByUserId(userId);
		if (null == userBasicInfo) {
			resObj.put("code", "-1");
			resObj.put("msg", ReturnCode.ERR_46004);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		} 
		resObj.put("userId", userBasicInfo.getUserId());
		resObj.put("balance", userBasicInfo.getBalance());
		
		List<UserMifiLink> userMifiLinkList = userMifiLinkDao.queryUserMifiLinkByUserId(userId);
		if(null != userMifiLinkList) {
			String linkStr = "";
			for(int i=0; i<userMifiLinkList.size(); i++) {
				UserMifiLink userMifiLink = userMifiLinkList.get(i);
				if(i == (userMifiLinkList.size() -1)) {
					linkStr += userMifiLink.getMifiId();
				} else {
					linkStr += userMifiLink.getMifiId()+",";
				}
			}
			resObj.put("miFiLinkList", linkStr);
		}
		
		logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
		return resObj;
	}
	
	/**
	 * 
	 * @Description 渠道商设备用户激活接口
	 * @param param 请求参数
	 * @param channelCode 渠道编号
	 * @return JSONObject  
	 * @author yifang.huang
	 * @date 2016年9月26日 下午4:46:25
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized JSONObject saveUserMifiActivate(JSONObject param, String channelCode) {
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		String errorMessage = "";
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(df.format(new Date()) + "[入参]：" + param.toString());

		if (!param.containsKey("loginname") || StringUtils.isBlank(param.getString("loginname"))) {
			errorMessage = "|[loginName]Can not be empty!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		if (!param.containsKey("mifiid") || StringUtils.isBlank(param.getString("mifiid"))) {
			errorMessage = "|[mifiId]Can not be empty!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		String loginName = param.getString("loginname");
		String mifiId = param.getString("mifiid");
		
		// 查询设备是否存在、判断所选设备是否属于当前渠道商
		HashMap<String, String> mifiMap = mifiManageDao.getMifilistBySn(mifiId);
		if (mifiMap == null) {
			errorMessage = "| [device:" + mifiId + "]not found!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		if (!channelCode.equals(mifiMap.get("sourceType"))) {
			errorMessage = "| [device:" + mifiId + "]Does not belong to the current channel!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_40035 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		
		// 删除设备已有绑定记录
		String sql = "delete from user_mifi_link where mifi_id='" + mifiId + "'";
		userMifiLinkDao.updateBySql(sql, null);
		
		// 保存用户基本信息逻辑
		UserBasicInfo _userBasicInfo = userBasicInfoDao.findUserByUserIdAndSourceType(loginName, channelCode);
		UserBasicInfo userBasicInfo = new UserBasicInfo();
		Date now = new Date();
		// 如果此用户不存在，则插入创建时间
		if(_userBasicInfo == null) {
			userBasicInfo.setCreateTime(now);
			userBasicInfo.setDayPass(0);
		} else {
			userBasicInfo.setId(_userBasicInfo.getId());
			userBasicInfo.setUserName(_userBasicInfo.getUserName());
			userBasicInfo.setEmail(_userBasicInfo.getEmail());
			userBasicInfo.setBalance(_userBasicInfo.getBalance());
			userBasicInfo.setTotalDeposit(_userBasicInfo.getTotalDeposit());
			userBasicInfo.setCreateTime(_userBasicInfo.getCreateTime());
			userBasicInfo.setDayPass(_userBasicInfo.getDayPass());
			userBasicInfo.setUpdateTime(now);
		}
		
		userBasicInfo.setUserId(loginName);
		userBasicInfo.setSourceType(channelCode);
		if(param.containsKey("username") && StringUtils.isNotBlank(param.getString("username"))) {
			userBasicInfo.setUserName(param.getString("username"));
		}
		if(param.containsKey("email") && StringUtils.isNotBlank(param.getString("email"))) {
			userBasicInfo.setEmail(param.getString("email"));
		}
		if(param.containsKey("phone") && StringUtils.isNotBlank(param.getString("phone"))) {
			userBasicInfo.setPhone(param.getString("phone"));
		}
		userBasicInfoDao.getSession().merge(userBasicInfo);

		//更新mifilist和mifi_basic_info表
		userBasicInfoDao.mifiUpdate(mifiId, userBasicInfo.getDayPass()>0 ? "1" : "0");
		
		// 保存userMifiLink
		UserMifiLink userMifiLink = new UserMifiLink();
		userMifiLink.setUserId(userBasicInfo.getId());
		userMifiLink.setMifiId(mifiId);
		userMifiLink.setLinkTime(new Date());
		userMifiLinkDao.save(userMifiLink);

		logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
		return resObj;
	}
	
	/**
	 * 
	 * @Description 获取用户信息和绑定设备列表
	 * @param param
	 * @param channelCode
	 * @return JSONObject  
	 * @author yifang.huang
	 * @date 2016年9月26日 下午6:04:35
	 */
	public JSONObject getUserActivateInfo(JSONObject param, String channelCode) {
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		String errorMessage = "";
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(df.format(new Date()) + "[入参]：" + param.toString());

		if (!param.containsKey("loginname") || StringUtils.isBlank(param.getString("loginname"))) {
			errorMessage = "|[loginName]Can not be empty!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		String loginName = param.getString("loginname");
		
		UserBasicInfo userBasicInfo = userBasicInfoDao.findUserByUserIdAndSourceType(loginName, channelCode);
		if (null == userBasicInfo) {
			errorMessage = "|[loginName]Does not exist in the boss system!";
			resObj.put("code", "-1");
			resObj.put("msg", ReturnCode.ERR_46004 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		} 
		
		resObj.put("loginName", userBasicInfo.getUserId());
		resObj.put("balance", userBasicInfo.getBalance());
		
		List<UserMifiLink> userMifiLinkList = userMifiLinkDao.queryUserMifiLinkByUserId(userBasicInfo.getId());
		if(null != userMifiLinkList) {
			String linkStr = "";
			for(int i=0; i<userMifiLinkList.size(); i++) {
				UserMifiLink userMifiLink = userMifiLinkList.get(i);
				if(i == (userMifiLinkList.size() -1)) {
					linkStr += userMifiLink.getMifiId();
				} else {
					linkStr += userMifiLink.getMifiId()+",";
				}
			}
			resObj.put("miFiActivateList", linkStr);
		}
		
		logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
		return resObj;
	}
	
	
}
