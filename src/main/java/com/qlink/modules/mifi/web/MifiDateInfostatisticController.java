package main.java.com.qlink.modules.mifi.web;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
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
import com.uu.modules.mifi.service.MifiUsageRecordService;
import com.uu.modules.om.condition.RegionCondition;
import com.uu.modules.om.entity.Region;
import com.uu.modules.om.service.RegionService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 设备使用记录流量统计控制器
 * 
 * @author shuxin
 * @date 2016年8月18日
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/usedDateInfoStat")
public class MifiDateInfostatisticController extends BaseController {
	public static Logger logger = LoggerFactory.getLogger(MifiDateInfostatisticController.class);

	@Autowired
	private RegionService regionService;

	@Autowired
	private MifiUsageRecordService mifiUsageService;

	/**
	 * 进入流量统计页面
	 * 
	 * @athor shuxin
	 * @date 2016年8月26日下午2:48:13
	 * @return String
	 */
	@RequestMapping(value = { "mcc", "" })
	public String mcc(ModelMap model) {
		String month = DateUtils.formatDate(DateUtils.parseDate(DateUtils.getPreDate()), "MM");
		model.put("begin", DateUtils.getYear() + "-" + month + "-" + "01");
		model.put("end", DateUtils.getPreDate());
		return "modules/mifi/mifiUseDateInfoStatMcc";
	}

	/**
	 * 年统计流量页面
	 * 
	 * @athor shuxin
	 * @date 2016年8月29日下午5:41:14
	 * @param model
	 * @return String
	 */
	@RequestMapping(value = { "year" })
	public String year(ModelMap model) {
		Map<String, String> countryMap = getCountrys();
		model.put("countrys", countryMap);
		model.put("begin", DateUtils.formatDate(new Date(), "yyyy"));
		model.put("end", DateUtils.formatDate(new Date(), "yyyy"));
		return "modules/mifi/mifiUseDateInfoStatYear";
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
		List<Map<String, String>> mccList = mifiUsageService.findMccsByCountryCode(countryCode);
		for (int i = 0; i < mccList.size(); i++) {
			Map<String, String> tempMap = mccList.get(i);
			countryMap.put(tempMap.get("country_code"), tempMap.get("country_name_cn"));
		}
		return countryMap;
	}

	/**
	 * 月统计页面
	 * 
	 * @athor shuxin
	 * @date 2016年8月30日上午11:07:53
	 * @param model
	 * @return String
	 */
	@RequestMapping("month")
	public String month(ModelMap model) {
		Map<String, String> countryMap = getCountrys();
		model.put("countrys", countryMap);
		model.put("begin", DateUtils.formatDate(new Date(), "yyyy-MM"));
		model.put("end", DateUtils.formatDate(new Date(), "yyyy-MM"));
		return "modules/mifi/mifiUseDateInfoStatMonth";
	}

	/**
	 * 日统计页面
	 * 
	 * @athor shuxin
	 * @date 2016年9月5日下午1:52:02
	 * @param model
	 * @return String
	 */
	@RequestMapping("day")
	public String day(ModelMap model) {
		Map<String, String> countryMap = getCountrys();
		String month = DateUtils.formatDate(DateUtils.parseDate(DateUtils.getPreDate()), "MM");
		model.put("countrys", countryMap);
		model.put("begin", DateUtils.getYear() + "-" + month + "-" + "01");
		model.put("end", DateUtils.getPreDate());
		return "modules/mifi/mifiUseDateInfoStatDay";
	}

	/**
	 * 按国家统计
	 * 
	 * @athor shuxin
	 * @date 2016年8月29日下午4:42:19
	 * @param paramMap
	 * @return JSONObject
	 */
	@RequestMapping("statDateInfoByMcc")
	public @ResponseBody JSONObject statDateInfoByMcc(@RequestParam Map<String, Object> paramMap) {
		JSONObject json = new JSONObject();
		List<Region> regionList = regionService.findListByCondition(new RegionCondition());
		if (regionList.isEmpty()) { // 没有统计的数据
			json.put("code", -1);
			return json;
		}
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
		// x轴上显示的数据
		List<Map<String, String>> mccList = mifiUsageService.findMccsByCountryCode(countryCode);
		if (mccList.isEmpty()) {// 没有统计的数据
			json.put("code", -1);
		}
		// 数据流量统计数据
		List<Map<String, Object>> dateInfoList = mifiUsageService.findMifiDateInfo(paramMap);
		if (dateInfoList.isEmpty()) {// 没有统计的数据
			json.put("code", -1);
		}
		try {
			json = handleDate(mccList, dateInfoList, paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 导出MCC
	 * @Description 
	 * @author wangsai
	 * @throws IOException 
	 * @date 2017年2月27日 上午11:22:36
	 */
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,HttpServletResponse response, RedirectAttributes redirectAttributes) throws IOException {
		try {
			// 使用记录文档标题
		   String fileName = "订单使用流量统计数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
		   JSONObject json=statDateInfoByMcc(paramMap);
		   String begin = ObjectUtils.toString(paramMap.get("beginDate"));
			String end = ObjectUtils.toString(paramMap.get("endDate"));
			ExportExcel ee = new ExportExcel("设备使用记录",
					new String[] { "时间", "国家", "实际平均流量","有效平均流量" });
			Row row = null;
			JSONArray array = json.getJSONArray("x");
			JSONArray vaildY = json.getJSONArray("vaildY");
			JSONArray actualY = json.getJSONArray("actualY");
			for(int i=0;i<array.size();i++){
				String jsonobject=(String) vaildY.get(i);
				if(jsonobject.equals("0.00")==false){
				row = ee.addRow();
				if(StringUtils.isNotBlank(end) && StringUtils.isNotBlank(begin)){
				ee.addCell(row, 0, begin+"到"+end);
				}else{
				ee.addCell(row, 0, "所有时间");
				}
				ee.addCell(row, 1, (String) array.get(i));
				ee.addCell(row, 2, (String) actualY.get(i));
				ee.addCell(row, 3, (String) vaildY.get(i));
				row = null;
			}
			
			}
			ee.write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出设备使用记录失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/usedDateInfoStat";
	}
	
	/**
	 * 数据封装处理
	 * 
	 * @athor shuxin
	 * @date 2016年8月29日下午4:42:30
	 * @param mccList
	 * @param dateInfoList
	 * @param paramMap
	 * @return
	 * @throws Exception
	 *             JSONObject
	 */
	private JSONObject handleDate(List<Map<String, String>> mccList, List<Map<String, Object>> dateInfoList,
			Map<String, Object> paramMap) throws Exception {
		JSONObject json = new JSONObject();
		boolean flag = true;
		String begin = ObjectUtils.toString(paramMap.get("beginDate"));
		String end = ObjectUtils.toString(paramMap.get("endDate"));
		if (!StringUtils.isNotBlank(begin) && !StringUtils.isNotBlank(end)) {
			json.put("code", "-1");
			json.put("msg", "请选择开始和结束时间");
			return json;
		}
		int validDays = DateUtils.daysBetween(begin, end) + 1;
		List<String> x = new ArrayList<String>(); // x轴上数据
		List<String> actualY = new ArrayList<String>(); // 实际平均值
		List<String> vaildY = new ArrayList<String>(); // 有效平均值
		int mccSize = mccList.size();
		int dateInfoSize = dateInfoList.size();
		DecimalFormat df = new DecimalFormat("#0.00");
		for (int i = 0; i < mccSize; i++) {
			Map<String, String> tempMcc = mccList.get(i);
			x.add(tempMcc.get("country_name_cn"));
			for (int j = 0; j < dateInfoSize; j++) {
				Map<String, Object> tempDateInfo = dateInfoList.get(j);
				if (tempMcc.get("country_code").equals(tempDateInfo.get("countryCode"))) { // 匹配找国家编号相等的情况
					paramMap.put("countryCode", tempDateInfo.get("countryCode"));
					paramMap.put("dayOff", validDays);
					int devices = getValidDevicesByContryCode(paramMap);
					actualY.add(df.format(
							(Double.parseDouble(tempDateInfo.get("dateInfo").toString())) / validDays / devices));
					// 根据国家编码查询时间段内有效的使用天数
					if (validDays == 1) { // 一天内计算一天有效的平均值
						vaildY.add(df.format(
								Double.parseDouble(tempDateInfo.get("dateInfo").toString()) / validDays / devices));
					} else {
						int days = getValidDaysByContryCode(paramMap);
						vaildY.add(df
								.format(Double.parseDouble(tempDateInfo.get("dateInfo").toString()) / days / devices));
					}
					flag = false;
					break;
				} else {
					flag = true;
				}
			}
			// 没有匹配到，默认初始化为0
			if (flag) {
				actualY.add("0.00");
				vaildY.add("0.00");
			}
		}
		json.put("x", x.toArray());
		json.put("actualY", actualY.toArray());
		json.put("vaildY", vaildY.toArray());
		return json;
	}

	/**
	 * 获取有效天数
	 * 
	 * @athor shuxin
	 * @date 2016年8月29日下午12:00:28
	 * @param contryCode
	 * @return int
	 */
	private int getValidDaysByContryCode(Map<String, Object> paramMap) {
		List<String> dayList = mifiUsageService.findValidDays(paramMap);
		return dayList.size();
	}

	private int getValidDevicesByContryCode(Map<String, Object> paramMap) {
		List<String> dayList = mifiUsageService.findValidDevicesDays(paramMap);
		return dayList.size();
	}
	/**
	 * 导出年
	 * @Description 
	 * @author wangsai
	 * @date 2017年2月27日 上午11:22:36
	 */
	@RequestMapping(value = "export1", method = RequestMethod.POST)
	public String exportFile1(@RequestParam Map<String, Object> paramMap,
			 HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			// 使用记录文档标题
		   String fileName = "订单使用流量按年统计数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
		   JSONObject json=statByYear(paramMap);
			String countryCode = ObjectUtils.toString(paramMap.get("countryName"));
			ExportExcel ee = new ExportExcel("设备使用记录按年",
					new String[] { "时间", "国家", "实际平均流量","有效平均流量" });
			Row row = null;
			JSONArray array = json.getJSONArray("x");
			JSONArray vaildY = json.getJSONArray("vaildY");
			JSONArray actualY = json.getJSONArray("actualY");
			for(int i=0;i<array.size();i++){
				String jsonobject=(String) vaildY.get(i);
				if(jsonobject.equals("0.00")==false){
				row = ee.addRow();
				ee.addCell(row, 0, (String) array.get(i));
				ee.addCell(row, 1, (String) countryCode);
				ee.addCell(row, 2, (String) actualY.get(i));
				ee.addCell(row, 3, (String) vaildY.get(i));
				row = null;
			}
			
			}
			ee.write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出设备使用记录失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/usedDateInfoStat/year";
	}
	/**
	 * 按年统计
	 * 
	 * @athor shuxin
	 * @date 2016年8月31日上午9:49:39
	 * @param paramMap
	 * @return JSONObject
	 */
	@RequestMapping("statByYear")
	public @ResponseBody JSONObject statByYear(@RequestParam Map<String, Object> paramMap) {
		JSONObject json = new JSONObject();
		boolean flag = true;
		String begin = ObjectUtils.toString(paramMap.get("beginDate"));
		String end = ObjectUtils.toString(paramMap.get("endDate"));
		if (ObjectUtils.toString(paramMap.get("countryCode")).equals("")) {
			json.put("code", "-1");
			json.put("msg", "请选择要统计的国家");
			return json;
		}
		if (!StringUtils.isNotBlank(begin) && !StringUtils.isNotBlank(end)) { // 参数校验
			json.put("code", "-1");
			json.put("msg", "请选择要统计的时间");
			return json;
		}
		int bYear = Integer.valueOf(begin);
		int eYear = Integer.valueOf(end);
		if ((eYear - bYear) > 10) {
			json.put("code", "-1");
			json.put("msg", "时间间隔不能超过10年");
			return json;
		}
		List<String> x = new ArrayList<String>();
		int dayOff = eYear - bYear;
		for (int i = 0; i < dayOff + 1; i++) {
			x.add(ObjectUtils.toString(bYear));
			bYear++;
		}
		List<String> actualY = new ArrayList<String>(); // 实际平均值
		List<String> vaildY = new ArrayList<String>(); // 有效平均值
		List<Map<String, Object>> yearDateInfo = mifiUsageService.statUseDateInfoByYear(paramMap);
		DecimalFormat df = new DecimalFormat("#0.00");
		for (int i = 0; i < x.size(); i++) {
			for (int j = 0; j < yearDateInfo.size(); j++) {
				Map<String, Object> tempMap = yearDateInfo.get(j);
				String year = ObjectUtils.toString(tempMap.get("year"));
				String dateInfo = ObjectUtils.toString(tempMap.get("dateInfo"));
				if (x.get(i).equals(year)) {
					List<String> imeis = mifiUsageService.findValidDevicesByYear(paramMap);
					int devices = imeis.size();
					int totalDays = DateUtils.getDaysByYear(Integer.valueOf(year));
					actualY.add(df.format(Double.parseDouble(dateInfo) / totalDays / devices));
					List<String> list = mifiUsageService.findValidDaysByYear(paramMap);
					vaildY.add(df.format(Double.parseDouble(dateInfo) / list.size() / devices));
					flag = false;
					break;
				} else {
					flag = true;
				}
			}
			// 没有匹配到，默认初始化为0
			if (flag) {
				actualY.add("0.00");
				vaildY.add("0.00");
			}
		}
		json.put("x", x.toArray());
		json.put("actualY", actualY.toArray());
		json.put("vaildY", vaildY.toArray());
		return json;
	}
	/**
	 * 按月统计设备使用流量
	 * 
	 * @athor shuxin
	 * @date 2016年8月30日上午11:41:19
	 * @param paramMap
	 * @return JSONObject
	 */
	@RequestMapping("statByMonth")
	public @ResponseBody JSONObject statByMonth(@RequestParam Map<String, Object> paramMap) {
		boolean flag = true;
		JSONObject json = new JSONObject();
		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		String countryCode = ObjectUtils.toString(paramMap.get("countryCode"));
		if (!StringUtils.isNotBlank(countryCode)) { // 参数校验
			json.put("code", "-1");
			json.put("msg", "请选择要统计的国家");
			return json;
		}
		if (!StringUtils.isNotBlank(beginDate) && !StringUtils.isNotBlank(endDate)) { // 参数校验
			json.put("code", "-1");
			json.put("msg", "请选择要统计的时间");
			return json;
		}
		Integer beginYear = Integer.valueOf(beginDate.split("-")[0]);
		Integer beginMonth = Integer.valueOf(beginDate.split("-")[1]);
		Integer endYear = Integer.valueOf(endDate.split("-")[0]);
		Integer endMonth = Integer.valueOf(endDate.split("-")[1]);
		// 计算x轴上面数据
		List<String> x = new ArrayList<String>();
		if (beginYear.equals(endYear)) { // 年份相同
			if (endMonth.equals(beginMonth)) {
				x.add(beginYear + "-" + beginMonth);
			} else {
				int monthOff = endMonth - beginMonth;
				for (int i = 0; i <= monthOff; i++) {
					x.add(beginDate.split("-")[0] + "-" + beginMonth);
					beginMonth++;
				}
			}
		} else {
			String countryName = ObjectUtils.toString(paramMap.get("countryName"));
			if(StringUtils.isBlank(countryName)){
			if (endYear - beginYear > 1) { // 超过一年
				json.put("code", -1);
				json.put("msg", "时间间隔不能超过12个月");
				return json;
			}
			if ((((12 - beginMonth) + 1) + endMonth) > 12) { // 只跨一年，计算月份是否超过12个月
				json.put("code", -1);
				json.put("msg", "时间间隔不能超过12个月");
				return json;
			}}
			int beginMonthOff = 12 - beginMonth;
			for (int i = 0; i <= beginMonthOff; i++) {
				x.add(beginDate.split("-")[0] + "-" + beginMonth);
				beginMonth++;
			}
			int endMonthOff = endMonth;
			for (int i = 1; i <= endMonthOff; i++) {
				x.add(endDate.split("-")[0] + "-" + i);
			}
		}
		List<String> actualY = new ArrayList<String>(); // 实际平均值
		List<String> vaildY = new ArrayList<String>(); // 有效平均值
		DecimalFormat df = new DecimalFormat("#0.00");
		List<Map<String, Object>> monthDateInfo = mifiUsageService.statUseDateInfoByMonth(paramMap);
		for (int i = 0; i < x.size(); i++) {
			String[] date = x.get(i).split("-");
			int year = Integer.valueOf(date[0]);
			int month = Integer.valueOf(date[1]);
			int actual = DateUtils.getDaysByYearMonth(year, month);
			for (int j = 0; j < monthDateInfo.size(); j++) {
				Map<String, Object> map = monthDateInfo.get(j);
				String ym = ObjectUtils.toString(map.get("year"));
				String dateInfo = ObjectUtils.toString(map.get("dateInfo"));
				if (x.get(i).equals(ym)) {
					paramMap.put("ym", x.get(i));
					List<String> imeis = mifiUsageService.findValidDeviceByMonth(paramMap);
					actualY.add(df.format(Double.parseDouble(dateInfo) / actual / imeis.size()));
					List<String> list = mifiUsageService.findValidDaysByMonth(paramMap);
					vaildY.add(df.format(Double.parseDouble(dateInfo) / list.size() / imeis.size()));
					flag = false;
					break;
				} else {
					flag = true;
				}
			}
			// 没有匹配到，默认初始化为0
			if (flag) {
				actualY.add("0.00");
				vaildY.add("0.00");
			}
		}
		json.put("x", x.toArray());
		json.put("actualY", actualY.toArray());
		json.put("vaildY", vaildY.toArray());
		return json;
	}
	/**
	 * 导出月
	 * @Description 
	 * @author wangsai
	 * @date 2017年2月27日 上午11:22:36
	 */
	@RequestMapping(value = "export2", method = RequestMethod.POST)
	public String exportFile2(@RequestParam Map<String, Object> paramMap,
			 HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			// 使用记录文档标题
		   String fileName = "订单使用流量按月统计数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
		   JSONObject json=statByMonth(paramMap);
			ExportExcel ee = new ExportExcel("设备使用记录按月",
					new String[] { "时间", "国家", "实际平均流量","有效平均流量" });
			Row row = null;
			JSONArray array = json.getJSONArray("x");
			JSONArray vaildY = json.getJSONArray("vaildY");
			JSONArray actualY = json.getJSONArray("actualY");
			String countryCode = ObjectUtils.toString(paramMap.get("countryName"));

			for(int i=0;i<array.size();i++){
				String jsonobject=(String) vaildY.get(i);
				if(jsonobject.equals("0.00")==false){
				row = ee.addRow();
				ee.addCell(row, 0, (String) array.get(i));
				ee.addCell(row, 1, countryCode);
				ee.addCell(row, 2, (String)actualY.get(i));
				ee.addCell(row, 3, (String) vaildY.get(i));
				row = null;
			}
			}
			ee.write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出设备使用记录失败！失败信息：" + e.getMessage());
			}
		return "redirect:" + Global.getAdminPath() + "/mifi/usedDateInfoStat/month";

	}
	/**
	 * 统计某个国家每天设备使用流量统计
	 * 
	 * @athor shuxin
	 * @date 2016年8月30日下午4:10:13
	 * @param paramMap
	 * @return JSONObject
	 */
	@RequestMapping("statByDay")
	public @ResponseBody JSONObject statByDay(@RequestParam Map<String, Object> paramMap) {
		JSONObject json = new JSONObject();
		boolean flag = true;
		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		String countryCode = ObjectUtils.toString(paramMap.get("countryCode"));
		Integer beginYear = Integer.valueOf(beginDate.split("-")[0]);
		Integer beginMonth = Integer.valueOf(beginDate.split("-")[1]);
		Integer beginDay = Integer.valueOf(beginDate.split("-")[2]);
		Integer endYear = Integer.valueOf(endDate.split("-")[0]);
		Integer endMonth = Integer.valueOf(endDate.split("-")[1]);
		Integer endDay = Integer.valueOf(endDate.split("-")[2]);
		if (!StringUtils.isNotBlank(countryCode)) { // 参数校验
			json.put("code", "-1");
			json.put("msg", "请选择要统计的国家");
			return json;
		}
		if (!StringUtils.isNotBlank(beginDate) && !StringUtils.isNotBlank(endDate)) { // 参数校验
			json.put("code", "-1");
			json.put("msg", "请选择要统计的时间");
			return json;
		}
		try {
			int dayOff = DateUtils.daysBetween(beginDate, endDate);
			String countryName = ObjectUtils.toString(paramMap.get("countryName"));
			if(StringUtils.isBlank(countryName)){
			if ((dayOff + 1) > 31) {
				json.put("code", "-1");
				json.put("msg", "时间间隔不能超过31天");
				return json;
			}}
			// 计算x轴上数据
			List<String> x = new ArrayList<String>();
			if ((endMonth - beginMonth) == 0) { // 同一月的数据处理
				for (int i = beginDay; i <= endDay; i++) {
					x.add(ObjectUtils.toString(beginYear) + "-" + ObjectUtils.toString(beginMonth) + "-"
							+ ObjectUtils.toString(i));
				}
			} else {
				// 处理开始月的数据
				Integer beginMonthOfDay = DateUtils.getDaysByYearMonth(beginYear, beginMonth);
				for (int i = beginDay; i <= beginMonthOfDay; i++) {
					x.add(ObjectUtils.toString(beginYear) + "-" + ObjectUtils.toString(beginMonth) + "-"
							+ ObjectUtils.toString(i));
				}
				// 处理结束月的数据
				for (int i = 1; i <= endDay; i++) {
					x.add(ObjectUtils.toString(endYear) + "-" + ObjectUtils.toString(endMonth) + "-"
							+ ObjectUtils.toString(i));
				}
			}
			// 根据时间段查询数据
			List<Map<String, Object>> dayDateInfo = mifiUsageService.statUseDateInfoByDay(paramMap);
			List<String> actualY = new ArrayList<String>(); // 实际平均值
			List<String> vaildY = new ArrayList<String>(); // 有效平均值
			DecimalFormat df = new DecimalFormat("#0.00");
			for (int i = 0; i < x.size(); i++) {
				for (int j = 0; j < dayDateInfo.size(); j++) {
					Map<String, Object> map = dayDateInfo.get(j);
					String ym = ObjectUtils.toString(map.get("year"));
					String dateInfo = ObjectUtils.toString(map.get("dateInfo"));
					if (x.get(i).equals(ym)) {
						paramMap.put("day", x.get(i));
						List<String> tempList = mifiUsageService.findValidDaysByDay(paramMap);
						actualY.add(df.format(Double.parseDouble(dateInfo) / tempList.size()));
						vaildY.add(df.format(Double.parseDouble(dateInfo) / tempList.size()));
						flag = false;
						break;
					} else {
						flag = true;
					}
				}
				// 没有匹配到，默认初始化为0
				if (flag) {
					actualY.add("0.00");
					vaildY.add("0.00");
				}
			}
			json.put("x", x.toArray());
			json.put("actualY", actualY.toArray());
			json.put("vaildY", vaildY.toArray());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 导出日
	 * @Description 
	 * @author wangsai
	 * @date 2017年2月27日 上午11:22:36
	 */
	@RequestMapping(value = "export3", method = RequestMethod.POST)
	public String exportFile3(@RequestParam Map<String, Object> paramMap,
			 HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			// 使用记录文档标题
		   String fileName = "订单使用流量按日统计数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
		   JSONObject json=statByDay(paramMap);
			ExportExcel ee = new ExportExcel("设备使用记录按日",
					new String[] { "时间", "国家", "实际平均流量","有效平均流量" });
			Row row = null;
			JSONArray array = json.getJSONArray("x");
			JSONArray vaildY = json.getJSONArray("vaildY");
			JSONArray actualY = json.getJSONArray("actualY");
			String countryCode = ObjectUtils.toString(paramMap.get("countryName"));
			for(int i=0;i<array.size();i++){
				String jsonobject=(String) vaildY.get(i);
				if(jsonobject.equals("0.00")==false){
				row = ee.addRow();
				ee.addCell(row, 0, (String) array.get(i));
				ee.addCell(row, 1, countryCode);
				ee.addCell(row, 2, (String) actualY.get(i));
				ee.addCell(row, 3, (String) vaildY.get(i));
				row = null;
			}
			}
			ee.write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出设备使用记录失败！失败信息：" + e.getMessage());
			}
		return "redirect:" + Global.getAdminPath() + "/mifi/usedDateInfoStat/day";

	}
}
