package main.java.com.qlink.modules.mifi.web;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uu.common.persistence.Page;
import com.uu.common.utils.DateUtils;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.condition.MifiUsageRecordCondition;
import com.uu.modules.mifi.condition.MifiUsageRecordLogCondition;
import com.uu.modules.mifi.entity.MifiUsageRecord;
import com.uu.modules.mifi.entity.MifiUsageRecordLog;
import com.uu.modules.mifi.service.CardMonitorService;
import com.uu.modules.mifi.service.MifiManageService;
import com.uu.modules.mifi.service.MifiTrafficService;
import com.uu.modules.mifi.service.MifiUsageRecordLogService;
import com.uu.modules.mifi.service.MifiUsageRecordService;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.om.service.PriceService;
import com.uu.modules.sys.service.LogService;
import com.uu.modules.utils.Constants;

import net.sf.json.JSONObject;

/**
 * 设备定时调度日志
 * @author shuxin
 * @date 2016年8月4日	
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/usageRecordLog")
public class MifiUsageRecordLogController extends BaseController {
	public static Logger logger = LoggerFactory.getLogger(MifiUsageRecordLogController.class);

	@Autowired
	private MifiUsageRecordLogService errorLogService;
	
	@Autowired
	private LogService logService;
	
	@Autowired
	private ChannelService channelService;

	@Autowired
	private PriceService priceService;
	
	@Autowired
	private MifiUsageRecordService recordService;
	
	@Autowired
	MifiTrafficService mifiTrafficService;
	
	@Autowired
	private MifiManageService mifiManageService;
	
	@Autowired
	private CardMonitorService cardMonitorService;

	/**
	 * 列表页面
	 * 
	 * @athor shuxin
	 * @date 2016年8月3日下午2:49:33
	 * @param condition
	 * @param request
	 * @param response
	 * @param model
	 * @return String
	 */
	@RequiresPermissions("mifi:usageRecordLog:view")
	@RequestMapping(value = "")
	public String init(MifiUsageRecordLogCondition condition, HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		return "modules/mifi/mifiUsageRecordLogList";
	}
	@RequiresPermissions("mifi:usageRecordLog:view")
	@RequestMapping(value = "list")
	public String list(MifiUsageRecordLogCondition condition, HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		if(condition.getResult() != null && condition.getResult() == 1){ //搜索执行失败
			List<Map<String, String>> recordList = errorLogService.findRecordLogsForMonth(condition);
			String str =  handleNoSelectTime(recordList);
			model.put("lose", str);
		}
		Page<MifiUsageRecordLog> page = errorLogService.findMifiUsageRecordLogsPage(new Page<MifiUsageRecordLog>(request, response),
				condition);
	
		model.put("page", page);
		model.put("condition", condition);
		return "modules/mifi/mifiUsageRecordLogList";
	}
	
	/**
	 * @athor shuxin
	 * @date 2016年9月14日下午2:50:23
	 * @param recordList
	 * @return
	 * String 
	 */
	private String handleNoSelectTime(List<Map<String, String>> recordList) {
		StringBuffer lostDay = new StringBuffer();
		boolean flag = true;
		if(!recordList.isEmpty()){
			int size = recordList.size();
			for (int i = 0; i < size; i++) {
				String temp = recordList.get(i).get("month");
				String year = temp.split("-")[0];
				String month = temp.split("-")[1];
				int tempDay = DateUtils.getDaysByYearMonth(Integer.valueOf(year), Integer.valueOf(month)); //获取当前的年月的天数
				List<Map<String, Object>> actualList = errorLogService.findRecordLogsByMonth(temp);   //获取temp月内实际的天数
				int  actualSize = actualList.size();
				if(tempDay == actualSize){ //修改isCheck属性值
					errorLogService.updateRecordLogOfIsCheckByMonth(temp);
				}
				if(DateUtils.getDate("yyyy-MM").equals(temp)){  //等于当前的月份，当前月份的当前的天数
					tempDay = Integer.parseInt(DateUtils.getDay()) - 1;
				}
				for (int j = 1; j <= tempDay; j++) {
					for (int z = 0; z < actualSize; z++) {
						int day = Integer.parseInt(ObjectUtils.toString(actualList.get(z).get("day")));
						if(j == day){
							flag = false;
							break;
						} else {
							flag = true;
						}
					}
					if(flag){
						lostDay.append(year).append("-").append(month).append("-").append(j);
						lostDay.append("<br/>");
					}
				}
			}
		}
		return lostDay.toString();
	}
	
	
	/**
	 * 统计指定日期日志
	 * @athor shuxin
	 * @date 2016年8月5日下午2:22:15
	 * @param model
	 * @return
	 * String 
	 */
	@RequiresPermissions("mifi:usageRecordLog:stat")
	@RequestMapping("form")
	public String form(ModelMap model){
		model.put("sched",DateUtils.getPreDate());
		return "modules/mifi/mifiUsageRecordLogForm";
	}
	
	/**
	 * 手动统计
	 * 
	 * @athor shuxin
	 * @date 2016年8月5日下午1:31:18
	 * @param id
	 * @param schedTime
	 * @return JSONObject
	 */
	@RequiresPermissions("mifi:usageRecordLog:stat")
	@RequestMapping("/getResult")
	public @ResponseBody JSONObject getResult(HttpServletRequest request, String id, String schedTime) {
		JSONObject json = new JSONObject();
		if (!StringUtils.isNotBlank(schedTime) && !StringUtils.isNotBlank(id)) {
			json.put("code", -1);
			json.put("msg", "参数错误");
			return json;
		}
		try {
			delExistMifiUsageRecords(schedTime); // 先删除统计日期内存在的数据
			json = createJob(id, schedTime); // 调度当前时间数据进行保存
			logService.saveOperateLog(request, "[MIIF管理-设备管理]-统计日志，手动统计指定日期：[{}]，统计使用数：[{}]，执行结果：[{}]", schedTime,ObjectUtils.toString(json.get("size")),
					ObjectUtils.toString(json.get("msg")));
		} catch (Exception e) {
			json.put("code", -1);
			json.put("msg", "统计失败");
			MifiUsageRecordLog errorLog = new MifiUsageRecordLog();
			errorLog.setId(id);
			errorLog.setStampCreated(DateUtils.parseDate(schedTime));
			saveOrupdateFailLog(errorLog);
		}
		return json;
	}

	/**
	 * 删除统计日期内存在的数据
	 * 
	 * @athor shuxin
	 * @date 2016年8月5日上午10:47:20
	 * @param schedTime
	 *            void
	 */
	private void delExistMifiUsageRecords(String schedTime) {
		MifiUsageRecordCondition condition = new MifiUsageRecordCondition();
		condition.setBeginDate(schedTime);
		condition.setEndDate(schedTime);
		Page<MifiUsageRecord> page = recordService.findMifiUsageRecordPage(new Page<MifiUsageRecord>(), condition);
		List<MifiUsageRecord> mRecords = page.getList();

		if (!mRecords.isEmpty()) { // 删除当前调度日期的所有数据
			StringBuffer delIds = new StringBuffer();
			for (MifiUsageRecord mifiUsageRecord : mRecords) {
				delIds.append("'");
				delIds.append(mifiUsageRecord.getId());
				delIds.append("'");
				delIds.append(",");
			}
			String ids = delIds.substring(0, delIds.lastIndexOf(",")).toString();
			System.out.println(delIds.substring(0, delIds.lastIndexOf(",")).toString());
			recordService.deleteMifiUsageRecordByIds(ids);
		}
	}

	private JSONObject createJob(String id, String schedTime) {
		JSONObject json = new JSONObject();
		Integer size = 0;
		json.put("code", 1);
		json.put("msg", "统计日志成功");
		json.put("stampCreated", schedTime);
		MifiUsageRecordLog errorLog = new MifiUsageRecordLog();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("beginDate", schedTime);
			paramMap.put("endDate", schedTime);
			Page<Object[]> p = new Page<Object[]>();
			p.setCount(-1);
			Page<Object[]> page = mifiTrafficService.exportMifiStatus4List(p, paramMap);
			List<Object[]> list = page.getList();

			// 渠道列表
			List<Channel> channelList = channelService.findChannelList();

			// 查询费用
			Map<String, String> mccPriceMap = getMccPriceMap(channelList);
			size = list.size();
			
			// 循环使用的变量
			String sourceTypeTemp = "", mcces = "", mcc="", price = "";
			
			json.put("size", size);
			List<MifiUsageRecord> records = new ArrayList<MifiUsageRecord>();
			for (int i = 0; i < size; i++) {
				Object[] objs = list.get(i);
				MifiUsageRecord record = new MifiUsageRecord();
				record.setImei(ObjectUtils.toString(objs[0]));
				record.setSourceType(ObjectUtils.toString(objs[1]));	
				record.setCardSourceType(ObjectUtils.toString(objs[23]));
				record.setOutOrderId(ObjectUtils.toString(objs[2]));	
				record.setUestatus(Integer.valueOf(ObjectUtils.toString(objs[3])));	
				record.setStampCreated(new Date( ((Timestamp)objs[4]).getTime()));	
				record.setSimBankId(Integer.valueOf(ObjectUtils.toString(objs[5])));
				record.setSimId(Integer.valueOf(ObjectUtils.toString(objs[6])));
				record.setNwstatus(Integer.valueOf(ObjectUtils.toString(objs[7])));
				record.setPowerInfo(Integer.valueOf(ObjectUtils.toString(objs[8])));
				record.setMainRejCause(ObjectUtils.toString(objs[9]));
				record.setMainMcc(ObjectUtils.toString(objs[10]));
				record.setMainMnc(ObjectUtils.toString(objs[11]));
				record.setMainTac(Integer.valueOf(ObjectUtils.toString(objs[12])));
				record.setMainCallid(Long.valueOf(ObjectUtils.toString(objs[13])));
				record.setMainRssi(Integer.valueOf(ObjectUtils.toString(objs[14])));
				record.setAdditionalMcc(ObjectUtils.toString(objs[15]));
				record.setAdditionalMnc(ObjectUtils.toString(objs[16]));
				record.setAdditionalTac(Integer.valueOf(ObjectUtils.toString(objs[17])));
				record.setAdditionalCellid(Long.valueOf(ObjectUtils.toString(objs[18])));
				record.setAdditionalRssi(Integer.valueOf(ObjectUtils.toString(objs[19])));
				record.setDevices(Integer.valueOf(ObjectUtils.toString(objs[20])));
				record.setBath(ObjectUtils.toString(objs[21]));

				// 费用（渠道商没有取youyoumob的价格）、使用国家信息
				sourceTypeTemp = ObjectUtils.toString(objs[1]);
				mcces = ObjectUtils.toString(objs[22]);
				if (StringUtils.isNotBlank(mcces)) {
					// 取出价格最高的mcc
					String[] mccArr = mcces.split(",");
					if (mccArr.length == 1) {
						mcc = mccArr[0];
						price = mccPriceMap.get(sourceTypeTemp + "#" + mcc);
						price = StringUtils.isBlank(price) ? mccPriceMap.get(Constants.CHANNEL_DEFAULT_VALUE + "#" + mcc) : price;
					} else {
						String priceStr = "";
						Double priceDou = 0.0;
						// 过滤掉设备ownerMcc
						Map<String, String> mifiMap = mifiManageService.getMifilistBySn(ObjectUtils.toString(objs[0]));
						String ownerMcc = mifiMap.get("ownerMcc");
						for (String str : mccArr) {
							if (StringUtils.isNotBlank(ownerMcc) && ownerMcc.equals(str))
								continue;
							priceStr = mccPriceMap.get(sourceTypeTemp + "#" + str);
							priceStr = StringUtils.isBlank(priceStr) ? mccPriceMap.get(Constants.CHANNEL_DEFAULT_VALUE + "#" + str) : priceStr;
							if (StringUtils.isNotBlank(priceStr) && priceDou < Double.valueOf(priceStr)) {
								priceDou = Double.valueOf(priceStr);
								mcc = str;
							}
						}
						price = String.valueOf(priceDou);
					}
					record.setCost(StringUtils.isBlank(price) ? null : Double.valueOf(price) );
					
					// 使用国家信息
					record.setMcc(mcc);
					HashMap<String, String> countryMap = cardMonitorService.findCountryByMcc(mcc);
					if (countryMap != null) {
						record.setCountryCode(countryMap.get("countryCode"));
						record.setCountryNameCn(countryMap.get("countryName"));
						record.setCountryNameEn(countryMap.get("countryNameEn"));
					}
				}
				sourceTypeTemp = "";
				mcces = "";
				mcc = "";
				price = "";
				
				// 统计设备流量(从simcardstatus表统计设备使用流量)
				String ueid = mifiManageService.getDeviceUeid(ObjectUtils.toString(objs[0]));
				if (StringUtils.isNotBlank(ueid)) {
					record.setDatainfo(mifiManageService.getDeviceFlow(ueid, ObjectUtils.toString(objs[23]), schedTime + " 00:00:00", schedTime + " 23:59:59"));
				} else {
					record.setDatainfo(0l);
				}

				records.add(record);
			}

			// 保存数据
			recordService.saveMifiUsageRecords(records);

			// 更新设备使用统计日志
			errorLog.setId(id);
			errorLog.setStampCreated(DateUtils.parseDate(schedTime));
			errorLog.setCount(size);
			saveOrupdateSucessLog(errorLog);
			return json;
		} catch (Exception e) {
			// 更新设备统计日志
			errorLog.setId(id);
			errorLog.setStampCreated(DateUtils.parseDate(schedTime));
			errorLog.setCount(size);
			json.put("code", -1);
			json.put("msg", "统计失败");
			json.put("size", size);
			saveOrupdateFailLog(errorLog);
			e.printStackTrace();
			return json;
		}
	}

	/**
	 * 成功统计日志处理
	 * 
	 * @athor shuxin
	 * @date 2016年8月5日上午9:59:21
	 * @param errorLog
	 *            void
	 */
	private void saveOrupdateSucessLog(MifiUsageRecordLog errorLog) {
		String id = errorLog.getId();
		String createTime = DateUtils.formatDate(errorLog.getStampCreated(), "yyyy-MM-dd");
		Integer count = errorLog.getCount();
		if (errorLog.getStampCreated() != null && StringUtils.isNotBlank(id)) { // 有id和统计日期
			errorLog = errorLogService.getMifiUsageRecordLogById(id);
			if (StringUtils.isNotBlank(errorLog.getId())) {
				errorLog.setUpdateDate(new Date());
				errorLog.setResult(0);
				errorLog.setCount(count);
				errorLog.setRemarks("执行成功");
				errorLogService.saveMifiUsageRecordErrorLog(errorLog);
			}
		}
		if (!StringUtils.isNotBlank(id)) { // 只有统计日期
			errorLog = errorLogService.getMifiUsageRecordLogByStampCreated(createTime);
			if (StringUtils.isNotBlank(errorLog.getId())) { // 统计日志记录存在
				errorLog.setUpdateDate(new Date());
				errorLog.setResult(0);
				errorLog.setRemarks("执行成功");
				errorLog.setCount(count);
				errorLogService.saveMifiUsageRecordErrorLog(errorLog);
			} else { // 统计日志记录不存在，进行添加
				MifiUsageRecordLog newLog = new MifiUsageRecordLog();
				newLog.setStampCreated(DateUtils.parseDate(createTime));
				newLog.setResult(0);
				newLog.setRemarks("执行成功");
				newLog.setCount(count);
				errorLogService.saveMifiUsageRecordErrorLog(newLog);
			}
		}
	}

	/**
	 * 异常统计日志处理
	 * 
	 * @athor shuxin
	 * @date 2016年8月5日上午9:59:49
	 * @param errorLog
	 *            void
	 */
	private void saveOrupdateFailLog(MifiUsageRecordLog errorLog) {
		String id = errorLog.getId();
		String createTime = DateUtils.formatDate(errorLog.getStampCreated(), "yyyy-MM-dd");
		Integer count = errorLog.getCount();
		if (StringUtils.isNotBlank(createTime) && StringUtils.isNotBlank(id)) {
			errorLog = errorLogService.getMifiUsageRecordLogById(id);
			errorLog.setUpdateDate(new Date());
			errorLog.setCount(count);
			errorLogService.saveMifiUsageRecordErrorLog(errorLog);
		}
		if (!StringUtils.isNotBlank(id)) {
			errorLog = errorLogService.getMifiUsageRecordLogByStampCreated(createTime);
			if (StringUtils.isNotBlank(errorLog.getId())) {
				errorLog.setUpdateDate(new Date());
				errorLog.setCount(count);
				errorLog.setResult(1);
				errorLog.setRemarks("执行失败");
				errorLogService.saveMifiUsageRecordErrorLog(errorLog);
			} else {
				MifiUsageRecordLog newLog = new MifiUsageRecordLog();
				newLog.setCount(count);
				newLog.setStampCreated(DateUtils.parseDate(createTime));
				newLog.setResult(1); // 执行失败
				newLog.setRemarks("执行失败");
				errorLogService.saveMifiUsageRecordErrorLog(newLog);
			}
		}

	}

	// 组装渠道的mcc对应价格map<sourceType#mcc, price>
	private Map<String, String> getMccPriceMap(List<Channel> channelList) {

		Map<String, String> map = new HashMap<String, String>();

		for (Channel channel : channelList) {
			String sourceType = channel.getChannelNameEn();
			List<Map<String, Object>> mccPirceMapList = priceService.findMccPriceMapList(channel.getId());
			if (mccPirceMapList != null && mccPirceMapList.size() > 0) {
				for (Map<String, Object> mccPriceMap : mccPirceMapList) {
					String price = ObjectUtils.toString(mccPriceMap.get("price"));
					String mcces = ObjectUtils.toString(mccPriceMap.get("mcces"));
					String[] mccArr = mcces.split(",");
					for (String mcc : mccArr) {
						map.put(sourceType + "#" + mcc, price);
					}
				}
			}
		}
		return map;
	}

	/**
	 * 按时间统计
	 * 
	 * @athor shuxin
	 * @date 2016年8月4日下午5:47:31
	 * @param schedTime
	 * @return JSONObject
	 */
	@RequiresPermissions("mifi:usageRecordLog:stat")
	@RequestMapping("stat")
	public @ResponseBody JSONObject stat(HttpServletRequest request, String schedTime) {
		JSONObject json = new JSONObject();
		if (!StringUtils.isNotBlank(schedTime)) {
			json.put("code", -1);
			json.put("msg", "参数错误");
			return json;
		}
		delExistMifiUsageRecords(schedTime);
		json = createJob(null, schedTime);
		logService.saveOperateLog(request, "[MIIF管理-设备管理]-统计日志，手动统计指定日期：[{}]，统计使用数：[{}]，执行结果：[{}]", schedTime,ObjectUtils.toString(json.get("size")),
				ObjectUtils.toString(json.get("msg")));
		return json;
	}
}
