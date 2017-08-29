/** 
 * @Package com.uu.modules.om.service 
 * @Description 
 * @author yifang.huang
 * @date 2016-3-14 下午4:15:19 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.om.service;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.common.utils.CsvWriter;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.PropertiesLoader;
import com.uu.common.utils.StringUtils;
import com.uu.modules.om.condition.ConsumeRecordCondition;
import com.uu.modules.om.dao.ChannelDao;
import com.uu.modules.om.dao.ConsumeRecordDao;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.entity.ConsumeRecord;
import com.uu.modules.om.entity.ConsumeRecord.Status;
import com.uu.modules.user.dao.UserBasicInfoDao;
import com.uu.modules.user.entity.UserBasicInfo;
import com.uu.modules.utils.ApiUtils;
import com.uu.modules.utils.Constants;
import com.uu.modules.utils.ReturnCode;
import com.uu.modules.utils.ToolUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/** 
 * @Description 消费记录 业务处理
 * @author yifang.huang
 * @date 2016-3-14 下午4:15:19 
 */
@Service
public class ConsumeRecordService extends BaseService {
	
	@Autowired
	private ConsumeRecordDao consumeRecordDao;
	
	@Autowired
	private UserBasicInfoDao userBasicInfoDao;
	
	@Autowired
	private ChannelDao channelDao;
	
	/**
	 * 
	 * @Description 根据ID取数据
	 * @param id
	 * @return ConsumeRecord  
	 * @author yifang.huang
	 * @date 2016-3-14 下午4:17:51
	 */
	public ConsumeRecord get(String id) {
		ConsumeRecord oldBean = consumeRecordDao.get(id);
		if (oldBean != null) {
			ConsumeRecord newBean = new ConsumeRecord();
			BeanUtils.copyProperties(oldBean, newBean);
			// 清除指定对象缓存
			consumeRecordDao.getSession().evict(oldBean);
			return newBean;
		}
		return null;
	}

	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<ConsumeRecord>  
	 * @author yifang.huang
	 * @date 2016年3月23日 上午11:45:55
	 */
	public List<ConsumeRecord> findList(ConsumeRecordCondition condition) {
		
		DetachedCriteria dc = consumeRecordDao.createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return consumeRecordDao.find(dc);
	}
	
	/**
	 * 
	 * @Description 根据查询参数取分页数据
	 * @param page
	 * @param condition
	 * @return Page<ConsumeRecord>  
	 * @author yifang.huang
	 * @date 2016年3月23日 上午11:45:40
	 */
	public Page<ConsumeRecord> findPage(Page<ConsumeRecord> page, ConsumeRecordCondition condition) {
		
		DetachedCriteria dc = consumeRecordDao.createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return consumeRecordDao.find(page, dc);
	}
	
	/**
	 * 消费充值记录查询接口主查询方法
	 * @param requestObj
	 * @param responseObj
	 */
	public void findConsumeRecordByParams(JSONObject requestObj, JSONObject responseObj) {
		requestObj = ToolUtil.transObject(requestObj);// key忽略大小写
		Object params = requestObj.get("params");
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(df.format(new Date()) + "[入参]：" + params.toString());
		
		if(null == params || !(params instanceof JSONObject)){
			responseObj.put("code", "61451");
			responseObj.put("msg", ReturnCode.ERR_61451 + "|需要JSON格式的参数[params]!");
			logger.info(df.format(new Date()) + "[出参]：" + responseObj.toString());
			return;
		}
		Page<ConsumeRecord> page = new Page<ConsumeRecord>();
		List<ConsumeRecord> consumeRecordList;
		ConsumeRecordCondition condition = new ConsumeRecordCondition(true);
		String errorMessage = validateAndBuild((JSONObject)params, condition, page);
		if (null != errorMessage) {
			responseObj.put("code", "61451");
			responseObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + responseObj.toString());
			return;
		}
		if(-1 == page.getPageSize()){//-1 == page.getPageSize()为非分页
			consumeRecordList = this.findList(condition);
		}else{
			consumeRecordList = this.findPage(page, condition).getList();
		}
		
		JSONArray jsonArr = new JSONArray();
		JSONObject jsonObj;
		for (ConsumeRecord record : consumeRecordList) {
			jsonObj = new JSONObject();
			jsonObj.put("id", record.getId());
			jsonObj.put("userId", record.getUserId());
			jsonObj.put("phone", record.getPhone());
			jsonObj.put("targetId", record.getTargetId());
			jsonObj.put("targetName", record.getTargetName());
			jsonObj.put("sourceType", record.getSourceType());
			jsonObj.put("recordType", record.getRecordType().name());
			jsonObj.put("status", record.getStatus().name());
			jsonObj.put("money", record.getMoney());
			jsonObj.put("paymentNo", record.getPaymentNo());
			if (null != record.getPaymentDate()) {
				jsonObj.put("paymentDate", DateUtils.formatDateTime(record.getPaymentDate()));
			}
			jsonObj.put("ip", record.getIp());
			ApiUtils.buildCommonAttributes(jsonObj, record);
			jsonArr.add(jsonObj);
		}
		responseObj.put("pageNo", page.getPageNo());
		responseObj.put("pageSize", page.getPageSize());
		if(-1 != page.getPageSize() && page.getPageNo() > page.getCount() / page.getPageSize() + 1){//分页查询，且查询页码超过最大页码
			responseObj.put("totalSize", 0);
			responseObj.put("data", new JSONArray());
		}else{
			responseObj.put("totalSize", -1 == page.getPageSize() ? jsonArr.size():page.getCount());
			responseObj.put("data", jsonArr);
		}
		
	}

	/**
	 * 校验并构建查询参数
	 * @param responseObj
	 * @param params
	 * @return
	 */
	private String validateAndBuild(JSONObject params, ConsumeRecordCondition condition, Page<ConsumeRecord> page) {
		Object paramObj = params.get("user_id");
		if (!(paramObj instanceof String)) {
			return "|[user_id]为空或格式错误!";
		}
		String userId = paramObj.toString();
		if (StringUtils.isBlank(userId)) {
			return "|[user_id]不能为空!";
		}
		if (50 < userId.length()) {
			return "|[user_id]长度超过50!";
		}
		condition.setEqUserId(userId);
		
		paramObj = params.get("record_type");
		if (null != paramObj) {
			if(!(paramObj instanceof String)){
				return "|[record_type]格式错误!";
			}
			try {
				condition.setEqRecordType(ConsumeRecord.RecordType.valueOf(paramObj.toString()));
			} catch (IllegalArgumentException e) {
				return "|[record_type]取值不符合要求!";
			}
		}
		
		paramObj = params.get("status");
		if (null != paramObj) {
			if(!(paramObj instanceof String)){
				return "|[status]格式错误!";
			}
			try {
				condition.setEqStatus(ConsumeRecord.Status.valueOf(paramObj.toString()));
			} catch (IllegalArgumentException e) {
				return "|[status]取值不符合要求!";
			}
		}
		
		paramObj = params.get("start_date");
		if (null != paramObj) {
			if(!(paramObj instanceof String)){
				return "|[status]格式错误!";
			}
			String startDate = paramObj.toString();
			if (!startDate.matches(Constants.REG_DATE_YYYY_MM_DD)) {
				return "|[start_date]格式错误!";
			} else {
				condition.setGeCreateDate(startDate);
			}
		}
		
		paramObj = params.get("end_date");
		if (null != paramObj) {
			if(!(paramObj instanceof String)){
				return "|[end_date]格式错误!";
			}
			String endDate = paramObj.toString();
			if (!endDate.matches(Constants.REG_DATE_YYYY_MM_DD)) {
				return "|[end_date]格式错误!";
			} else {
				condition.setLeCreateDate(endDate);
			}
		}
		
		paramObj = params.get("page_size");
		if(null != paramObj){
			if(!(paramObj instanceof Integer)){
				return "|[page_size]格式错误!";
			}
			int pageSize = (Integer) params.get("page_size");
			paramObj = params.get("page_no");
			if(null != paramObj){
				if(!(paramObj instanceof Integer)){
					return "|[page_no]格式错误!";
				}
				page.setPageNo((Integer)paramObj);
			}
			page.setPageSize(pageSize);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @Description 根据ID删除数据
	 * @param id 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016-3-14 下午4:45:16
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void delete(String id) {
		consumeRecordDao.deleteById(id);
	}

	/**
	 * 
	 * @Description 保存数据
	 * @param bean 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016-3-14 下午4:45:03
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void save(ConsumeRecord bean) {
		consumeRecordDao.save(bean);
	}
	
	/**
	 * 
	 * @Description 保存通过API接口传递的数据
	 * @param params
	 * @return JSONObject  
	 * @author yifang.huang
	 * @date 2016年3月28日 下午3:16:42
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public JSONObject saveForApi(JSONObject params) {
		
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		String errorMessage = "";
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(df.format(new Date()) + "[入参]：" + params.toString());
		
		// 记录ID是否存在
		if (!params.containsKey("id") || StringUtils.isBlank(params.getString("id"))) {
			errorMessage = "|[充值记录唯一标识]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		// 记录是否存在
		String id = params.getString("id");
		ConsumeRecord record = consumeRecordDao.get(id);
		if (record == null) {
			errorMessage = "|充值记录未找到!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		// 实际充值金额
		if (!params.containsKey("money") || StringUtils.isBlank(params.getString("money"))) {
			errorMessage = "|[实际充值金额]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		// 支付流水号
		if (!params.containsKey("paymentNo") || StringUtils.isBlank(params.getString("paymentNo"))) {
			errorMessage = "|[支付流水号]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		// 买家付款时间
		if (!params.containsKey("paymentDate") || StringUtils.isBlank(params.getString("paymentDate"))) {
			errorMessage = "|[买家付款时间]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		// 同步数据的服务器I
		if (!params.containsKey("ip") || StringUtils.isBlank(params.getString("ip"))) {
			errorMessage = "|[同步数据的服务器IP]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		

		try {
			
	    	record.setStatus(Status.COMPLETED);
	    	record.setMoney(Double.valueOf(params.getString("money")));
	    	record.setPaymentNo(params.getString("paymentNo"));
	    	record.setPaymentDate(df.parse(params.getString("paymentDate")));
	    	record.setIp(params.getString("ip"));
			
			consumeRecordDao.getSession().saveOrUpdate(record);
			
			// 修改用户余额、充值总额或者修改渠道商余额
			String userId = record.getUserId();
			if (StringUtils.isNotBlank(userId)) {
				UserBasicInfo info = userBasicInfoDao.findByUserId(record.getUserId());
				if (info != null) {
					DecimalFormat df1 = new DecimalFormat("0.00"); 
					String balance = df1.format(Double.valueOf(info.getBalance()) + record.getMoney());
					String totalDeposit = df1.format(Double.valueOf(info.getTotalDeposit()) + record.getMoney());
					info.setBalance(balance);
					info.setTotalDeposit(totalDeposit);
					userBasicInfoDao.save(info);
				}	
			} else {
				Channel channel = record.getChannel();
				channel.setBalance(channel.getBalance() + record.getMoney());
				channelDao.getSession().saveOrUpdate(channel);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			resObj.put("code", "61450");
			resObj.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
		return resObj;
	}

	/**
	 * 
	 * @Description 根据mcc取与北京的时间差
	 * @param mcc
	 * @return int  
	 * @author yifang.huang
	 * @date 2016年3月31日 下午5:48:41
	 */
	public int findBjTimeDifferenceByMcc(String mcc) {
		return consumeRecordDao.findBjTimeDifferenceByMcc(mcc);
	}
	
	/**
	 * 
	 * @Description 根据mcc取国家编号
	 * @param mcc
	 * @return HashMap<String,String>  
	 * @author yifang.huang
	 * @date 2016年4月1日 下午3:32:36
	 */
	public HashMap<String, String> findCountryCodeByMcc(String mcc) {
		return consumeRecordDao.findCountryCodeByMcc(mcc);
	}
	
	/****************************** 生成csv文件 TODO ****************************/
	/**
	 * 
	 * @Description 生成csv文件
	 * @param startDate
	 * @param endDate
	 * @param channel
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年4月20日 上午10:11:35
	 */
	public synchronized Map<String, String> createCsvFiles(Date startDate, Date endDate, Channel channel) {
		
		Map<String, String> map = new HashMap<String, String>();

		int sum = 0;
		try {
			// 文件存放路径
			PropertiesLoader propertiesLoader = new PropertiesLoader("app.properties");
			String csvDir = propertiesLoader.getProperty("consumeRecordFiles_dir");
			
			int flag = 0;
			Date endDateTemp = null;
			ConsumeRecordCondition condition = null;
			while(flag < 100) {
				
				endDateTemp = getEndTime(startDate);
				
				// 查询渠道对应的完成的消费记录
				condition = new ConsumeRecordCondition();
				condition.setEqChannelId(channel.getId());
				condition.setEqStatus(ConsumeRecord.Status.COMPLETED);
				condition.setGeCreateDateForDate(getFullDate(startDate));
				condition.setLeCreateDateForDate(getFullDate(endDateTemp));
				List<ConsumeRecord> crList = findList(condition);
				if (crList!=null && crList.size()>0) {
					createCsvFile(endDateTemp, csvDir, channel, crList);
					sum ++;
				}
				
				condition = null;
				
				if (endDateTemp.getTime() == endDate.getTime()) 
					break;
				flag ++;
				startDate = endDateTemp;
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("code", "-1");
			map.put("msg", "生成csv文件失败");
			return map;
		}

		map.put("code", "1");
		map.put("msg", "生成csv文件成功[" + sum + "个]");
		return map;
		
	}
	
	/**
	 * 
	 * @Description 生成csv文件
	 * @param fileDate
	 * @param csvDir
	 * @param channel
	 * @param crList 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年4月19日 上午10:19:59
	 */
	private void createCsvFile(Date fileDate, String csvDir, Channel channel, List<ConsumeRecord> crList) {
		
		// 行数据
		List<String> data = this.getDataStr(crList);

		// 目录路径
		String fPath = csvDir + File.separator + channel.getChannelNameEn();
		
		// 文件名
		String fName = "SIM" + new SimpleDateFormat("yyyyMMddHH").format(fileDate) + ".csv";
		
		// 生成csv文件
		CsvWriter.createCSVFile(data, fPath, fName);
	}
	
	/**
	 * 
	 * @Description 取后一小时时间
	 * @return Date  
	 * @author yifang.huang
	 * @date 2016年4月19日 上午10:30:57
	 */
	private Date getEndTime(Date startDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		c.add(Calendar.HOUR, 1);
  		return c.getTime();
	}
	
	// 转成string
	private List<String> getDataStr(List<ConsumeRecord> crList) {

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStartStr = df.format(DateUtils.getDateStart(new Date()));
		String dateEndStr = df.format(DateUtils.getDateEnd(new Date()));
		List<String> data = new ArrayList<String>();			
		
		for (ConsumeRecord cr : crList) {
			data.add("H," + cr.getSn() + "," + cr.getSsid() + ",0.00," + cr.getMcc() + "," + dateStartStr + "," + dateStartStr + "," + dateStartStr + ",9999999999");
			data.add("D," + cr.getSn() + "," + cr.getSsid() + "," + cr.getMoney() + "," + cr.getMcc() + "," + dateStartStr + "," + df.format(cr.getLocalDate()) + "," + dateEndStr + "," + cr.getId());
			data.add("T," + cr.getSn() + "," + cr.getSsid() + "," + cr.getMoney() + "," + cr.getMcc() + "," + dateStartStr + "," + dateStartStr + "," + dateStartStr + ",9999999999");
		}
		
		return data;
		
	}
	
	// 日期补上分秒
	private Date getFullDate(Date date) throws ParseException {

		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH");
		DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String dateStr = df1.format(date);
		dateStr = dateStr + ":00:00";
		
		return df2.parse(dateStr);
	}
	/****************************** 生成csv文件 结束 ****************************/
}
