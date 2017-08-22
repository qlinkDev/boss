/** 
 * @Package com.uu.modules.mifi.web 
 * @Description 
 * @author yuxiaoyu
 * @date 2017年3月3日 下午4:11:09 
 * @version V1.0 
 */
package com.uu.modules.mifi.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

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
import com.uu.common.scheduled.DeviceUsageRecordSegmentedScheduled;
import com.uu.common.utils.DateUtils;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.condition.MifiUsageRecordSegmentCondition;
import com.uu.modules.mifi.condition.MifiUsageRecordSegmentLogCondition;
import com.uu.modules.mifi.entity.MifiUsageRecordSegment;
import com.uu.modules.mifi.entity.MifiUsageRecordSegmentLog;
import com.uu.modules.mifi.service.CardMonitorService;
import com.uu.modules.mifi.service.MifiManageService;
import com.uu.modules.mifi.service.MifiTrafficService;
import com.uu.modules.mifi.service.MifiUsageRecordSegmentLogService;
import com.uu.modules.mifi.service.MifiUsageRecordSegmentService;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.om.service.PriceService;
import com.uu.modules.sys.service.LogService;

/** 
 * @Description 设备定时调度日志
 * @author yuxiaoyu
 * @date 2017年3月3日 下午4:11:09 
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/usageRecordSegmentLog")
public class MifiUsageRecordSegmentLogController extends BaseController {
	public static Logger logger = LoggerFactory.getLogger(MifiUsageRecordSegmentLogController.class);

	@Autowired
	private MifiUsageRecordSegmentLogService errorLogService;

	@Autowired
	private LogService logService;

	@Autowired
	private ChannelService channelService;

	@Autowired
	private PriceService priceService;

	@Autowired
	private MifiUsageRecordSegmentService recordService;

	@Autowired
	MifiTrafficService mifiTrafficService;

	@Autowired
	private MifiManageService mifiManageService;

	@Autowired
	private CardMonitorService cardMonitorService;

	/**
	 * 列表页面
	 * @Description 
	 * @param condition
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * @return String  
	 * @author yuxiaoyu
	 * @date 2017年3月6日 下午2:25:59
	 */
	@RequiresPermissions("mifi:usageRecordLog:view")
	@RequestMapping(value = "")
	public String init(MifiUsageRecordSegmentLogCondition condition, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "modules/mifi/mifiUsageRecordSegmentLogList";
	}

	@RequiresPermissions("mifi:usageRecordLog:view")
	@RequestMapping(value = "list")
	public String list(MifiUsageRecordSegmentLogCondition condition, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if (condition.getResult() != null && condition.getResult() == 1) { // 搜索执行失败
			List<Map<String, String>> recordList = errorLogService.findRecordLogsForMonth(condition);
			String str = handleNoSelectTime(recordList);
			model.put("lose", str);
		}
		Page<MifiUsageRecordSegmentLog> page = errorLogService.findPage(new Page<MifiUsageRecordSegmentLog>(request, response), condition);

		model.put("page", page);
		model.put("condition", condition);
		return "modules/mifi/mifiUsageRecordSegmentLogList";
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
		if (!recordList.isEmpty()) {
			int size = recordList.size();
			for (int i = 0; i < size; i++) {
				String temp = recordList.get(i).get("month");
				String year = temp.split("-")[0];
				String month = temp.split("-")[1];
				int tempDay = DateUtils.getDaysByYearMonth(Integer.valueOf(year), Integer.valueOf(month)); // 获取当前的年月的天数
				List<Map<String, Object>> actualList = errorLogService.findByMonth(temp); // 获取temp月内实际的天数
				int actualSize = actualList.size();
				if (tempDay == actualSize) { // 修改isCheck属性值
					errorLogService.updateOfIsCheckByMonth(temp);
				}
				if (DateUtils.getDate("yyyy-MM").equals(temp)) { // 等于当前的月份，当前月份的当前的天数
					tempDay = Integer.parseInt(DateUtils.getDay()) - 1;
				}
				for (int j = 1; j <= tempDay; j++) {
					for (int z = 0; z < actualSize; z++) {
						int day = Integer.parseInt(ObjectUtils.toString(actualList.get(z).get("day")));
						if (j == day) {
							flag = false;
							break;
						} else {
							flag = true;
						}
					}
					if (flag) {
						lostDay.append(year).append("-").append(month).append("-").append(j);
						lostDay.append("<br/>");
					}
				}
			}
		}
		return lostDay.toString();
	}

	/**
	 * form页（指定日期统计）
	 * @Description 
	 * @param model
	 * @return 
	 * @return String  
	 * @author yuxiaoyu
	 * @date 2017年3月6日 下午2:22:52
	 */
	@RequiresPermissions("mifi:usageRecordLog:stat")
	@RequestMapping("form")
	public String form(ModelMap model) {
		model.put("sched", DateUtils.getPreDate());
		return "modules/mifi/mifiUsageRecordSegmentLogForm";
	}

	/**
	 * 手动统计
	 * @Description 
	 * @param request
	 * @param id
	 * @param schedTime
	 * @return 
	 * @return JSONObject  
	 * @author yuxiaoyu
	 * @date 2017年3月6日 下午2:39:42
	 */
	@RequiresPermissions("mifi:usageRecordLog:stat")
	@RequestMapping("/getResult")
	public @ResponseBody
	JSONObject getResult(HttpServletRequest request, String id, String schedTime) {
		JSONObject json = new JSONObject();
		if (!StringUtils.isNotBlank(schedTime) && !StringUtils.isNotBlank(id)) {
			json.put("code", -1);
			json.put("msg", "参数错误");
			return json;
		}
		try {
			delExistUsageRecords(schedTime); // 先删除统计日期内存在的数据
			json = createJob(id, schedTime); // 调度当前时间数据进行保存
			logService.saveOperateLog(request, "[MIIF管理-设备管理]-统计日志，手动统计指定日期：[{}]，统计使用数：[{}]，执行结果：[{}]", schedTime,
					ObjectUtils.toString(json.get("size")), ObjectUtils.toString(json.get("msg")));
		} catch (Exception e) {
			json.put("code", -1);
			json.put("msg", "统计失败");
			MifiUsageRecordSegmentLog errorLog = new MifiUsageRecordSegmentLog();
			errorLog.setId(id);
			errorLog.setStampCreated(DateUtils.parseDate(schedTime));
			errorLogService.save(errorLog);
		}
		return json;
	}

	/**
	 * 指定日期统计
	 * @Description 
	 * @param request
	 * @param schedTime
	 * @return 
	 * @return JSONObject  
	 * @author yuxiaoyu
	 * @date 2017年3月6日 下午2:54:44
	 */
	@RequiresPermissions("mifi:usageRecordLog:stat")
	@RequestMapping("stat")
	public @ResponseBody
	JSONObject stat(HttpServletRequest request, String schedTime) {
		JSONObject json = new JSONObject();
		if (!StringUtils.isNotBlank(schedTime)) {
			json.put("code", -1);
			json.put("msg", "参数错误");
			return json;
		}
		delExistUsageRecords(schedTime);
		json = createJob(null, schedTime);
		logService.saveOperateLog(request, "[MIIF管理-设备管理]-统计日志，手动统计指定日期：[{}]，统计使用数：[{}]，执行结果：[{}]", schedTime,
				ObjectUtils.toString(json.get("size")), ObjectUtils.toString(json.get("msg")));
		return json;
	}
	
	/**
	 * 删除日期内存在的统计数据
	 * @Description 
	 * @param schedTime 
	 * @return void  
	 * @author yuxiaoyu
	 * @date 2017年3月6日 下午2:41:01
	 */
	private void delExistUsageRecords(String schedTime) {
		MifiUsageRecordSegmentCondition condition = new MifiUsageRecordSegmentCondition();
		condition.setBeginDate(schedTime);
		condition.setEndDate(schedTime);
		Page<MifiUsageRecordSegment> page = recordService.findPage(new Page<MifiUsageRecordSegment>(), condition);
		List<MifiUsageRecordSegment> mRecords = page.getList();

		if (!mRecords.isEmpty()) { // 删除当前调度日期的所有数据
			StringBuffer delIds = new StringBuffer();
			for (MifiUsageRecordSegment mifiUsageRecord : mRecords) {
				delIds.append("'");
				delIds.append(mifiUsageRecord.getId());
				delIds.append("'");
				delIds.append(",");
			}
			String ids = delIds.substring(0, delIds.lastIndexOf(",")).toString();
			System.out.println(delIds.substring(0, delIds.lastIndexOf(",")).toString());
			recordService.deleteByIds(ids);
		}
	}
	
	/**
	 * 统计主流程
	 * @Description 
	 * @param id
	 * @param schedTime
	 * @return 
	 * @return JSONObject  
	 * @author yuxiaoyu
	 * @date 2017年3月6日 下午2:41:34
	 */
	private JSONObject createJob(String id, String schedTime) {
		JSONObject json = new JSONObject();
		json.put("code", 1);
		json.put("msg", "统计日志成功");
		json.put("stampCreated", schedTime);
		MifiUsageRecordSegmentLog errorLog = DeviceUsageRecordSegmentedScheduled.doJob(schedTime, mifiTrafficService, channelService,
				mifiManageService, cardMonitorService, priceService, recordService);
		if (1 == errorLog.getResult()) {
			json.put("code", -1);
			json.put("msg", "统计失败");
		}
		errorLog.setId(id);
		errorLogService.save(errorLog);
		return json;
	}
}
