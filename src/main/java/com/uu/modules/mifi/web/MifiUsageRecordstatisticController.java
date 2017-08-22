package com.uu.modules.mifi.web;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uu.common.config.Global;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.excel.ExportExcel;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.entity.MifiUsageRecord;
import com.uu.modules.mifi.service.MifiUsageRecordService;
import com.uu.modules.om.condition.RegionCondition;
import com.uu.modules.om.entity.Region;
import com.uu.modules.om.service.RegionService;
import com.uu.modules.utils.Constants;

/**
 * 设备使用记录统计控制器
 * 
 * @author shuxin
 * @date 2016年8月18日
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/useRecordStat")
public class MifiUsageRecordstatisticController extends BaseController {
	public static Logger logger = LoggerFactory.getLogger(MifiUsageRecordstatisticController.class);

	@Autowired
	private MifiUsageRecordService recordService;
	
	@Autowired
	private RegionService regionService;
	
	/**
	 * 国家统计页面
	 * 
	 * @athor shuxin
	 * @date 2016年8月18日下午3:37:44
	 * @return String
	 */
	@RequestMapping(value = { "mcc", "" })
	public String mcc() {

		return "modules/mifi/mifiUsageRecordStatMcc";
	}

	/**
	 * 日期统计页面
	 * 
	 * @athor shuxin
	 * @date 2016年8月18日下午3:37:57
	 * @return String
	 */
	@RequestMapping(value = { "year" })
	public String year(ModelMap model) {
		Map<String, String> countryMap = getCountrys();
		model.put("countrys", countryMap);
		model.put("year", DateUtils.formatDate(new Date(), "yyyy"));
		return "modules/mifi/mifiUsageRecordStatYear";
	}

	/**
	 * 月统计页面
	 * 
	 * @athor shuxin
	 * @date 2016年8月25日下午1:43:19
	 * @param model
	 * @return String
	 */
	@RequestMapping(value = { "month" })
	public String month(ModelMap model, String year, String countryCode) {
		Map<String, String> countryMap = getCountrys();
		model.put("countrys", countryMap);
		model.put("countryCode", countryCode);
		if(StringUtils.isNotBlank(year)){
				model.put("begin", year+"-"+"01");
				model.put("end", year+"-"+DateUtils.getMonth());
		} else {
			model.put("begin", DateUtils.formatDate(new Date(), "yyyy-MM"));
			model.put("end", DateUtils.formatDate(new Date(), "yyyy-MM"));
		}
		return "modules/mifi/mifiUsageRecordStatMonth";
	}

	/**
	 * 日统计页面
	 * 
	 * @athor shuxin
	 * @date 2016年8月25日下午1:43:31
	 * @param model
	 * @return String
	 */
	@RequestMapping(value = { "day" })
	public String day(ModelMap model, String ym, String countryCode) {
		if(StringUtils.isNotBlank(ym)){
			try {
				Date date = DateUtils.parseDate(ym, "yyyy-MM");
				String year = DateUtils.formatDate(date, "yyyy");
				String month =  DateUtils.formatDate(date, "MM");
				if(DateUtils.getMonth().equals(month)){
					model.put("begin",year + "-" + month + "-" +"01");
					model.put("end", DateUtils.getPreDate());
				} else {
					int days = DateUtils.getDaysByYearMonth(Integer.valueOf(year), Integer.valueOf(month));
					model.put("begin",year + "-" + month + "-" +"01");
					model.put("end", year + "-" + month + "-" +days);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			String month = DateUtils.formatDate(DateUtils.parseDate(DateUtils.getPreDate()), "MM");
			model.put("begin", DateUtils.getYear() + "-" + month + "-" +"01");
			model.put("end",  DateUtils.getPreDate());
		}
		Map<String, String> countryMap = getCountrys();
		model.put("countrys", countryMap);
		model.put("countryCode", countryCode);
		return "modules/mifi/mifiUsageRecordStatDay";
	}

	/**
	 * 按mcc统计
	 * 
	 * @athor shuxin
	 * @date 2016年8月19日上午11:02:31
	 * @param paramMap
	 * @return JSONObject
	 */
	@RequestMapping("statByMcc")
	public @ResponseBody JSONObject statByMcc(@RequestParam Map<String, Object> paramMap) {
		JSONObject obj = new JSONObject();
		List<Map<String, Integer>> list = recordService.statUsageRecordByMcc(paramMap);
		List<String> x = new ArrayList<String>();
		List<Integer> y = new ArrayList<Integer>();
		List<Double> z = new ArrayList<Double>();

		for (int i = 0; i < list.size(); i++) {
			Map<String, Integer> temp = list.get(i);
			String countryName = ObjectUtils.toString(temp.get("country_name_cn"));
			Integer total = Integer.valueOf(ObjectUtils.toString(temp.get("num")));
			String cost = ObjectUtils.toString(temp.get("cost") == null ? "0.00" : temp.get("cost") );
			x.add(countryName);
			y.add(total);
			z.add(Double.parseDouble(cost));
		}
		obj.put("countryName", x.toArray());
		obj.put("num", y.toArray());
		obj.put("cost", z.toArray());
		return obj;
	}

	/**
	 * 按年份统计
	 * 
	 * @athor shuxin
	 * @date 2016年8月24日下午5:52:25
	 * @param paramMap
	 * @return JSONObject
	 */
	@RequestMapping("statByYear")
	public @ResponseBody JSONObject statByYear(@RequestParam Map<String, Object> paramMap) {
		JSONObject obj = new JSONObject();
		Integer begin = Integer.valueOf(ObjectUtils.toString(paramMap.get("beginDate")).equals("") ? "0"
				: ObjectUtils.toString(paramMap.get("beginDate")));
		Integer end = Integer.valueOf(ObjectUtils.toString(paramMap.get("endDate")).equals("") ? "0"
				: ObjectUtils.toString(paramMap.get("endDate")));
		Integer cal = end - begin;
		if (cal > 9) {
			obj.put("code", -1);
			obj.put("code", "时间间隔不能超过10年");
			return obj;
		}
		if (begin != 0 && end != 0) { // 有时间戳
			if (cal == 0) { // 时间戳为0表示为相同的年份
				return this.getYearJson(paramMap, obj);
			}
			String[] tempArr = new String[cal + 1];
			for (int i = 0; i < cal + 1; i++) {
				tempArr[i] = begin.toString();
				begin++;
			}
			// 统计数据
			List<Map<String, Integer>> list = recordService.statUsageRecordByYear(paramMap);
			// x坐标显示数据
			List<String> x = new ArrayList<String>();
			List<Integer> y = new ArrayList<Integer>();
			List<Double> z = new ArrayList<Double>();
			for (int i = 0; i < tempArr.length; i++) {
				x.add(tempArr[i].toString());
				for (int j = 0; j < list.size(); j++) {
					Map<String, Integer> temp = list.get(j);
					String countryName = ObjectUtils.toString(temp.get("time"));
					Integer total = Integer.valueOf(ObjectUtils.toString(temp.get("num")));
					String cost = ObjectUtils.toString(temp.get("cost") == null ? "0.00" : temp.get("cost") );
					if (tempArr[i].equals(countryName)) {
						y.add(total);
						z.add(Double.parseDouble(cost));
					} else {
						y.add(0);
						z.add(0.00);
					}
				}
			}
			obj.put("x", x.toArray());
			obj.put("y", y.toArray());
			obj.put("cost", z.toArray());
		} else {
			return this.getYearJson(paramMap, obj);
		}
		return obj;
	}

	/**
	 * 单个年份处理方法
	 * 
	 * @athor shuxin
	 * @date 2016年8月24日下午5:52:49
	 * @param paramMap
	 * @param obj
	 * @return JSONObject
	 */
	private JSONObject getYearJson(Map<String, Object> paramMap, JSONObject obj) {
		List<Map<String, Integer>> list = recordService.statUsageRecordByYear(paramMap);
		List<String> x = new ArrayList<String>();
		List<Integer> y = new ArrayList<Integer>();
		List<Double> z = new ArrayList<Double>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Integer> temp = list.get(i);
			String countryName = ObjectUtils.toString(temp.get("time"));
			Integer total = Integer.valueOf(ObjectUtils.toString(temp.get("num")));
			String cost = ObjectUtils.toString(temp.get("cost") == null ? "0.00" : temp.get("cost") );
			x.add(countryName);
			y.add(total);
			z.add(Double.parseDouble(cost));
		}
		obj.put("x", x.toArray());
		obj.put("y", y.toArray());
		obj.put("cost", z.toArray());
		return obj;
	}

	/**
	 * 按月统计
	 * 
	 * @athor shuxin
	 * @date 2016年8月25日上午9:57:16
	 * @param paramMap
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("statByMonth")
	public @ResponseBody JSONObject statByMonth(@RequestParam Map<String, Object> paramMap) {
		JSONObject object = new JSONObject();
		object = getXDate(paramMap, object);
		if (object.has("code")) {
			return object;
		}
		List<Map<String, Integer>> list = recordService.statUsageRecordByMonth(paramMap);
		List<String> x = new ArrayList<String>();
		List<Integer> y = new ArrayList<Integer>();
		List<Double> z = new ArrayList<Double>();
		List<String> months = (List<String>) object.get("months");
		for (int i = 0; i < months.size(); i++) {
			x.add(months.get(i));
			for (int j = 0; j < list.size(); j++) {
				Map<String, Integer> temp = list.get(j);
				String countryName = ObjectUtils.toString(temp.get("time"));
				Integer total = Integer.valueOf(ObjectUtils.toString(temp.get("num")));
				String cost = ObjectUtils.toString(temp.get("cost") == null ? "0.00" : temp.get("cost") );
				if (months.get(i).equals(countryName)) {
					y.add(total);
					z.add(Double.parseDouble(cost));
					break;
				}
				if (j == (list.size() - 1)) {
					y.add(0);
					z.add(0.00);
				}
			}
		}
		object.put("x", x.toArray());
		object.put("y", y.toArray());
		object.put("cost", z.toArray());
		return object;
	}

	/**
	 * 获取x轴上的数据，以及时间间隔验证
	 * 
	 * @athor shuxin
	 * @date 2016年8月24日下午10:58:44
	 * @param paramMap
	 * @param object
	 *            void
	 */
	private JSONObject getXDate(Map<String, Object> paramMap, JSONObject object) {
		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		Integer beginYear = Integer.valueOf(beginDate.split("-")[0]);
		Integer beginMonth = Integer.valueOf(beginDate.split("-")[1]);
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		Integer endYear = Integer.valueOf(endDate.split("-")[0]);
		Integer endMonth = Integer.valueOf(endDate.split("-")[1]);
		List<String> tempList = new ArrayList<String>();
		if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)) { // 都不为空的情况
			Integer calYear = endYear - beginYear;
			if (calYear == 0) { // 表示相同年份
				Integer calMonth = endMonth - beginMonth;
				if (calMonth != 0) { // 月份也相同，进行数据处理
					// TODO
					if (calMonth > 12) {
						object.put("code", -1);
						object.put("msg", "时间间隔不能超过12个月");
						return object;
					}
					Integer tempBeginMonth = beginMonth;
					for (int i = 0; i < calMonth + 1; i++) {
						tempList.add(ObjectUtils.toString(beginYear) + "-" + tempBeginMonth);
						tempBeginMonth++;
					}
				} else {
					tempList.add(ObjectUtils.toString(beginYear) + "-" + beginMonth);
				}
				object.put("months", tempList);
			} else { // 表示不同的年份
				if (calYear > 1) {
					object.put("code", -1);
					object.put("msg", "时间间隔不能超过12个月");
					return object;
				}
				if ((((12 - beginMonth) + 1) + endMonth) > 12) {
					object.put("code", -1);
					object.put("msg", "时间间隔不能超过12个月");
					return object;
				}
				Integer[] year = new Integer[calYear + 1];
				Integer tempBeginYear = beginYear;
				for (int i = 0; i < year.length; i++) {
					year[i] = tempBeginYear;
					tempBeginYear++;
				}
				for (int i = 0; i < year.length; i++) {
					if (year[i].equals(beginYear)) {
						int monthCal = (12 - beginMonth) + 1;
						Integer tempBeginMonth = beginMonth;
						for (int j = 0; j < monthCal; j++) {
							tempList.add(ObjectUtils.toString(beginYear) + "-" + tempBeginMonth);
							tempBeginMonth++;
						}
						continue;
					}
					if (year[i].equals(endYear)) {
						for (int j = 1; j <= endMonth; j++) {
							tempList.add(ObjectUtils.toString(endYear) + "-" + j);
						}
						continue;
					}
					for (int j = 1; j <= 12; j++) {
						tempList.add(ObjectUtils.toString(year[i]) + "-" + j);
					}
				}
				object.put("months", tempList);
			}
		}
		return object;
	}

	/**
	 * 统计日报表
	 * @athor shuxin
	 * @date 2016年8月25日下午5:11:31
	 * @param paramMap
	 * @return
	 * JSONObject 
	 */
	@RequestMapping("statByDay")
	public @ResponseBody
	JSONObject statByDay(@RequestParam Map<String, Object> paramMap, @RequestParam(required = false) String[] allowedMcc) {
		JSONObject resultObject = new JSONObject();
		List<String> xAxisDataList = getXDaysList(paramMap);// x轴：日期
		if (null == xAxisDataList || 0 == xAxisDataList.size()) {
			resultObject.put("code", -1);
			resultObject.put("msg", "时间间隔不能超过31天");
			return resultObject;
		}
		List<Map<String, Object>> dbResultList = recordService.statUsageRecordByDay(paramMap, allowedMcc);
		if (null == dbResultList || 0 == dbResultList.size()) {
			return resultObject;
		}
		List<Integer> yAxisDataList = null;// y轴：记录数
		List<Double> zAxisDataList = null;// z轴：消费总额
		String time = StringUtils.EMPTY, currentCountryCode = StringUtils.EMPTY, countryCode = StringUtils.EMPTY, total = StringUtils.EMPTY, cost = StringUtils.EMPTY, xData = StringUtils.EMPTY;
		JSONObject countryObject = null;
		int xIndex = 0;
		for (Map<String, Object> dbRecord : dbResultList) {
			countryCode = ObjectUtils.toString(dbRecord.get("country_code"));
			time = ObjectUtils.toString(dbRecord.get("time"));
			total = ObjectUtils.toString(dbRecord.get("num"));
			cost = ObjectUtils.toString(dbRecord.get("cost") == null ? "0.00" : dbRecord.get("cost"));
			if (null != countryObject && currentCountryCode.equals(countryCode)) {// 跟上次循环的国家相同
				for (; xIndex < xAxisDataList.size(); xIndex++) {// 查询结果按日期排序，所以xIndex不重置，这样对于同一国家，x轴只遍历一次，减少无用扫描
					xData = xAxisDataList.get(xIndex);
					if (xData.equals(time)) {
						yAxisDataList.add(Integer.valueOf(total));
						zAxisDataList.add(Double.valueOf(cost));
						xIndex++;
						break;// 扫描到当前数据库行的时间即可
					} else {
						yAxisDataList.add(0);
						zAxisDataList.add(0D);
					}
				}
				continue;
			}
			// 即将创建新的国家，在这之前先写入上一个国家的数据
			appendResult(resultObject, xAxisDataList, yAxisDataList, zAxisDataList, currentCountryCode, countryObject, xIndex);
			// 开始创建新的国家（一个国家对应一张图表）
			currentCountryCode = countryCode;
			countryObject = new JSONObject();
			yAxisDataList = new ArrayList<Integer>();
			zAxisDataList = new ArrayList<Double>();
			for (xIndex = 0; xIndex < xAxisDataList.size(); xIndex++) {
				xData = xAxisDataList.get(xIndex);
				if (xData.equals(time)) {
					yAxisDataList.add(Integer.valueOf(total));
					zAxisDataList.add(Double.valueOf(cost));
					xIndex++;
					break;// 扫描到当前数据库行的时间即可
				} else {
					yAxisDataList.add(0);
					zAxisDataList.add(0D);
				}
			}
		}
		// 循环结束后写入最后一个国家的三维数据
		appendResult(resultObject, xAxisDataList, yAxisDataList, zAxisDataList, currentCountryCode, countryObject, xIndex);
		return resultObject;
	}

	private void appendResult(JSONObject resultObject, List<String> xAxisDataList, List<Integer> yAxisDataList, List<Double> zAxisDataList,
			String currentCountryCode, JSONObject countryObject, int xIndex) {
		// 补全无数据的日期
		if (null != countryObject && xIndex < xAxisDataList.size()) {
			for (; xIndex < xAxisDataList.size(); xIndex++) {
				yAxisDataList.add(0);
				zAxisDataList.add(0D);
			}
		}
		// 写入上一个国家的三维数据
		if (null != countryObject) {
			countryObject.put("xAxisDataList", JSONArray.fromObject(xAxisDataList));
			countryObject.put("yAxisDataList", JSONArray.fromObject(yAxisDataList));
			countryObject.put("zAxisDataList", JSONArray.fromObject(zAxisDataList));
			resultObject.put(currentCountryCode, countryObject);
		}
	}

	private List<String> getXDaysList(Map<String, Object> paramMap) {
		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		String[] strArr = beginDate.split(Constants.MINUS);
		Integer beginYear = Integer.valueOf(strArr[0]);
		Integer beginMonth = Integer.valueOf(strArr[1]);
		Integer beginDay = Integer.valueOf(strArr[2]);
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		strArr = endDate.split(Constants.MINUS);
		Integer endYear = Integer.valueOf(strArr[0]);
		Integer endMonth = Integer.valueOf(strArr[1]);
		Integer endDay = Integer.valueOf(strArr[2]);
		List<String> daysList = new ArrayList<String>();
		try {
			int timeInterval = DateUtils.daysBetween(beginDate, endDate);
			if (30 < timeInterval) {
				return null;
			}
			if ((endMonth - beginMonth) == 0) { // 同一月的数据处理
				for (int i = beginDay; i <= endDay; i++) {
					daysList.add(ObjectUtils.toString(beginYear) + Constants.MINUS + ObjectUtils.toString(beginMonth) + Constants.MINUS
							+ ObjectUtils.toString(i));
				}
			} else {
				// 处理开始月的数据
				Integer beginMonthOfDay = DateUtils.getDaysByYearMonth(beginYear, beginMonth);
				for (int i = beginDay; i <= beginMonthOfDay; i++) {
					daysList.add(ObjectUtils.toString(beginYear) + Constants.MINUS + ObjectUtils.toString(beginMonth) + Constants.MINUS
							+ ObjectUtils.toString(i));
				}
				// 处理结束月的数据
				for (int i = 1; i <= endDay; i++) {
					daysList.add(ObjectUtils.toString(endYear) + Constants.MINUS + ObjectUtils.toString(endMonth) + Constants.MINUS
							+ ObjectUtils.toString(i));
				}
			}
			return daysList;
		} catch (ParseException e) {
			logger.error(StringUtils.EMPTY, e);
			return null;
		}
	}

	/**
	 * 统计当前年内（月和日）
	 * 
	 * @athor shuxin
	 * @date 2016年8月25日下午2:44:22
	 * @param paramMap
	 * @return JSONObject
	 */
	@RequestMapping("statByDate")
	public @ResponseBody JSONObject statByDate(@RequestParam Map<String, Object> paramMap) {
		JSONObject object = new JSONObject();
		List<Map<String, Integer>> list = recordService.statUsageRecordByDate(paramMap);
		List<String> x = new ArrayList<String>();
		List<Integer> y = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Integer> temp = list.get(i);
			String countryName = ObjectUtils.toString(temp.get("time"));
			Integer total = Integer.valueOf(ObjectUtils.toString(temp.get("num")));
			x.add(countryName);
			y.add(total);
		}
		object.put("x", x.toArray());
		object.put("y", y.toArray());
		return object;
	}
	
	/**
	 * 获取区域下面有效的国家
	 * 
	 * @athor shuxin
	 * @date 2016年8月30日上午11:10:27
	 * @return Map<String,String>
	 */
	private Map<String, String> getCountrys() {
		Map<String, String> countryMap = new HashMap<String, String>();
		List<Region> regionList = regionService.findListByCondition(new RegionCondition());
		StringBuffer buffer = new StringBuffer();
		for (Region region : regionList) {
			if (region.getCountryCodes().indexOf(",") != -1) {
				String[] tempStr = region.getCountryCodes().split(",");
				for (int i = 0; i < tempStr.length; i++) {
					buffer.append("'").append(tempStr[i]).append("'").append(",");
				}
			}
			buffer.append("'").append(region.getCountryCodes()).append("'").append(",");
		}
		String countryCode = buffer.substring(0, buffer.lastIndexOf(",")).toString();
		List<Map<String, String>> mccList = recordService.findMccsByCountryCode(countryCode);
		for (int i = 0; i < mccList.size(); i++) {
			Map<String, String> tempMap = mccList.get(i);
			countryMap.put(tempMap.get("country_code"), tempMap.get("country_name_cn"));
		}
		return countryMap;
	}
	/**
	 * 导出使用记录按日统计数据  
	 * @Description 
	 * @author wangsai
	 * @date 2016年12月19日 上午11:36:05
	 */
	@RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(@RequestParam Map<String, Object> paramMap,@RequestParam(required=false) String[] allowedMcc, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "使用记录按日统计数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx"; 
			List<MifiUsageRecord> list = recordService.findMifiUsageRecordListForExport(paramMap ,allowedMcc);
			if(list!=null && list.size()>0){
				new ExportExcel("使用记录按日统计数据", MifiUsageRecord.class).setDataList(list).write(response, fileName).dispose();
				return null;
			}else{
				addMessage(redirectAttributes, "导出使用记录按日统计数据失败！失败信息：没有数据");
			}
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出使用记录按日统计数据失败！失败信息："+e.getMessage());
			e.printStackTrace();
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/useRecordStat/day";
    }
	/**
	 * 导出使用记录按MCC统计数据  
	 * @Description 
	 * @author wangsai
	 * @date 2016年12月19日 上午11:36:05
	 */
	@RequestMapping(value = "export1", method=RequestMethod.POST)
    public String exportFile1(@RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "使用记录按MCC统计数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx"; 
			List<MifiUsageRecord> list = recordService.findstatUsageRecordByMccExport(paramMap);
			if(list!=null && list.size()>0){
				new ExportExcel("使用记录按MCC统计数据", MifiUsageRecord.class).setDataList(list).write(response, fileName).dispose();
				return null;
			}else{
				addMessage(redirectAttributes, "导出使用记录按MCC统计数据失败！失败信息：没有数据");
			}
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出使用记录按MCC统计数据失败！失败信息："+e.getMessage());
			e.printStackTrace();
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/useRecordStat/mcc";
    }/**
	 * 导出使用记录按年统计数据  
	 * @Description 
	 * @author wangsai
	 * @date 2016年12月19日 上午11:36:05
	 */
	@RequestMapping(value = "export2", method=RequestMethod.POST)
    public String exportFile2(@RequestParam Map<String, Object> paramMap,@RequestParam(required=false) String[] allowedMcc, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "使用记录按年统计数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx"; 
			List<MifiUsageRecord> list = recordService.statUsageRecordByYearExport(paramMap);
			if(list!=null && list.size()>0){
				new ExportExcel("使用记录按年统计数据", MifiUsageRecord.class).setDataList(list).write(response, fileName).dispose();
				return null;
			}else{
				addMessage(redirectAttributes, "导出使用记录按年统计数据失败！失败信息：没有数据");
			}
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出使用记录按年统计数据失败！失败信息："+e.getMessage());
			e.printStackTrace();
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/useRecordStat/year";
    }/**
	 * 导出使用记录按月统计数据  
	 * @Description 
	 * @author wangsai
	 * @date 2016年12月19日 上午11:36:05
	 */
	@RequestMapping(value = "export3", method=RequestMethod.POST)
    public String exportFile3(@RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "使用记录按月统计数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx"; 
			List<MifiUsageRecord> list = recordService.statUsageRecordByMonthExport(paramMap);
			if(list!=null && list.size()>0){
				new ExportExcel("使用记录按月统计数据", MifiUsageRecord.class).setDataList(list).write(response, fileName).dispose();
				return null;
			}else{
				addMessage(redirectAttributes, "导出使用记录按月统计数据失败！失败信息：没有数据");
			}
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出使用记录按月统计数据失败！失败信息："+e.getMessage());
			e.printStackTrace();
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/useRecordStat/month";
    }
}
