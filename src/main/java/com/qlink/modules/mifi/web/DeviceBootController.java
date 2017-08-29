/** 
 * @Package com.uu.modules.mifi.web 
 * @Description 
 * @author yifang.huang
 * @date 2016年11月22日 上午11:27:01 
 * @version V1.0 
 */
package main.java.com.qlink.modules.mifi.web;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uu.common.config.Global;
import com.uu.common.persistence.Page;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.excel.ExportExcel;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.condition.DeviceBootCondition;
import com.uu.modules.mifi.entity.DeviceBoot;
import com.uu.modules.mifi.service.DeviceBootService;
import com.uu.modules.om.condition.AdvertisingCondition;
import com.uu.modules.om.service.AdvertisingService;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.utils.UserUtils;

import net.sf.json.JSONObject;

/**
 * @Description 设备开机 后台控制类
 * @author yifang.huang
 * @date 2016年11月22日 上午11:27:01
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/deviceBoot")
public class DeviceBootController extends BaseController {

	@Autowired
	private DeviceBootService deviceBootService;

	@Autowired
	private AdvertisingService advertisingService;

	/**
	 * 
	 * @Description 分布查询
	 * @param condition
	 * @param request
	 * @param response
	 * @param model
	 * @return String
	 * @author yifang.huang
	 * @date 2016年11月22日 上午11:30:00
	 */
	@RequiresPermissions("mifi:boot:view")
	@RequestMapping(value = { "list", "" })
	public String list(DeviceBootCondition condition, HttpServletRequest request, HttpServletResponse response,
			Model model) {

		if (StringUtils.isNotBlank(request.getParameter("queryData"))) {// 是否查询数据
			// 列表数据
			Page<DeviceBoot> page = deviceBootService.findPage(new Page<DeviceBoot>(request, response), condition);
			model.addAttribute("page", page);

			// 按设备、时间(yyyy-MM-dd)分组统计数据
			String total = "0";
			List<Map<String, Object>> listMap = deviceBootService.getTotalGroupByImeiAndTime(condition);
			if (listMap != null && listMap.size() > 0) {
				Map<String, Object> map = listMap.get(0);
				total = ObjectUtils.toString(map.get("total"));
			}
			model.addAttribute("total", total);
		}

		User user = UserUtils.getUser();
		// 广告列表
		AdvertisingCondition advCondition = new AdvertisingCondition();
		if (user.getChannelAdmin())
			advCondition.setLikeSourceType(user.getChannelNameEn());
		model.addAttribute("advList", advertisingService.findList(advCondition));

		// 是否运营商
		if (user.getChannelAdmin())
			return "channel/mifi/deviceBootList";
		return "modules/mifi/deviceBootList";

	}
	/**
	 * 广告统计 类型
	 * @Description 
	 * @param condition
	 * @param model
	 * @return 
	 * @return String  
	 * @author wangsai
	 * @date 2017年2月23日 下午2:20:13
	 */
	@RequestMapping(value = "byType")
	public String byType(DeviceBootCondition condition, Model model) {
		// 按设备、时间(yyyy-MM-dd)分组统计数据
		User user = UserUtils.getUser();
		AdvertisingCondition advCondition = new AdvertisingCondition();
		if (user.getChannelAdmin())
			advCondition.setLikeSourceType(user.getChannelNameEn());
		model.addAttribute("advList", advertisingService.findList(advCondition));
		return "modules/mifi/deviceBootByType";
	}
	/**
	 * 按月统计
	 * @Description 
	 * @param model
	 * @param year
	 * @return 
	 * @return String  
	 * @author wangsai
	 * @date 2017年2月23日 下午2:20:34
	 */
	@RequestMapping(value = "month")
	public String month(ModelMap model, String year) {
		User user = UserUtils.getUser();
		AdvertisingCondition advCondition = new AdvertisingCondition();
		if (user.getChannelAdmin())
			advCondition.setLikeSourceType(user.getChannelNameEn());
		model.addAttribute("advList", advertisingService.findList(advCondition));
		if (StringUtils.isNotBlank(year)) {
			model.put("begin", year + "-" + "01");
			model.put("end", year + "-" + DateUtils.getMonth());
		} else {
			model.put("begin", DateUtils.formatDate(new Date(), "yyyy-MM"));
			model.put("end", DateUtils.formatDate(new Date(), "yyyy-MM"));
		}
		return "modules/mifi/deviceBootByTypeMonth";
	}
	/**
	 * 按日统计
	 * @Description 
	 * @param model
	 * @return 
	 * @return String  
	 * @author wangsai
	 * @date 2017年2月23日 下午2:20:42
	 */
	@RequestMapping("day")
	public String day(ModelMap model) {
		User user = UserUtils.getUser();
		AdvertisingCondition advCondition = new AdvertisingCondition();
		if (user.getChannelAdmin())
			advCondition.setLikeSourceType(user.getChannelNameEn());
		model.addAttribute("advList", advertisingService.findList(advCondition));
		String month = DateUtils.formatDate(DateUtils.parseDate(DateUtils.getPreDate()), "MM");
		model.put("begin", DateUtils.getYear() + "-" + month + "-" + "01");
		model.put("end", DateUtils.getPreDate());
		return "modules/mifi/deviceBootByTypeDay";
	}

	/**
	 * 广告统计图
	 * @date 2016年8月19日上午11:02:31
	 * @param paramMap
	 * @return JSONObject
	 */
	@RequestMapping("ByDeviceBoot")
	public @ResponseBody JSONObject statByDeviceBoot(@RequestParam Map<String, Object> paramMap) {
		JSONObject obj = new JSONObject();
		List<Map<String, Integer>> list = deviceBootService.statByDeviceBoot(paramMap);
		List<String> x = new ArrayList<String>();
		List<Integer> y = new ArrayList<Integer>();
		List<Integer> y1 = new ArrayList<Integer>();// 连接网络
		List<Integer> y2 = new ArrayList<Integer>();// 页面跳转
		List<Integer> y3 = new ArrayList<Integer>();// 未知
		boolean falg = true;
		boolean falg1 = true;
		boolean falg2 = true;
		boolean falg3 = true;
		for (int i = 0; i < list.size(); i++) {
			Map<String, Integer> temp = list.get(i);
			String countryName = ObjectUtils.toString(temp.get("type"));
			Integer total = Integer.valueOf(ObjectUtils.toString(temp.get("num")));
			if (countryName.equals("BOOT")) {
				y.add(total);
				falg = false;
			}
			if (countryName.equals("CON_NET")) {
				y1.add(total);
				falg1 = false;
			}
			if (countryName.equals("JUMP")) {
				y2.add(total);
				falg2 = false;
			}
			if (countryName.equals("HOME_SHOW")) {
				y3.add(total);
				falg3 = false;
			}
			x.add(countryName);
		}
		if (falg) {
			y.add(0);
		}
		if (falg1) {
			y1.add(0);
		}
		if (falg2) {
			y2.add(0);
		}
		if (falg3) {
			y3.add(0);
		}
		obj.put("countryName", x.toArray());
		obj.put("y", y.toArray());
		obj.put("y1", y1.toArray());
		obj.put("y2", y2.toArray());
		obj.put("y3", y3.toArray());

		return obj;
	}

	/**
	 * 按月统计
	 * 
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
		List<Map<String, Integer>> list = deviceBootService.statByDeviceBootMonth(paramMap);
		List<String> x = new ArrayList<String>();
		List<Integer> y = new ArrayList<Integer>();// 开机
		List<Integer> y1 = new ArrayList<Integer>();// 连接网络
		List<Integer> y2 = new ArrayList<Integer>();// 页面跳转
		List<Integer> y3 = new ArrayList<Integer>();// 未知
		List<String> months = (List<String>) object.get("months");
		String type = StringUtils.EMPTY;
		for (int i = 0; i < months.size(); i++) {
			x.add(months.get(i));
			for (int j = 0; j < list.size(); j++) {
				Map<String, Integer> temp = list.get(j);
				String countryName2 = months.get(i);
				Integer total = Integer.valueOf(ObjectUtils.toString(temp.get("num")));
				type = ObjectUtils.toString(temp.get("type"));
				String time = String.valueOf(temp.get("time"));
				if (time.equals(countryName2) && type.equals("BOOT")) {
					y.add(total);
				}
				if (time.equals(countryName2) && type.equals("CON_NET")) {
					y1.add(total);
				}
				if (time.equals(countryName2) && type.equals("JUMP")) {
					y2.add(total);
				}
				if (time.equals(countryName2) && type.equals("HOME_SHOW")) {
					y3.add(total);
				}
			}
			if (y.size() <= i) {
				y.add(0);
			}
			if (y1.size() <= i) {
				y1.add(0);
			}
			if (y2.size() <= i) {
				y2.add(0);
			}
			if (y3.size() <= i) {
				y3.add(0);
			}
		}
		object.put("x", x.toArray());
		object.put("y", y.toArray());
		object.put("y1", y1.toArray());
		object.put("y2", y2.toArray());
		object.put("y3", y3.toArray());
		return object;
	}

	/**
	 * 获取x轴上的数据，以及时间间隔验证
	 * 
	 * @athor wangsai
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
	 * 按日统计
	 * @Description 
	 * @param paramMap
	 * @return 
	 * @return JSONObject  
	 * @author wangsai
	 * @date 2017年2月23日 下午2:21:34
	 */
	@RequestMapping("statByday")
	public @ResponseBody JSONObject statByDay(@RequestParam Map<String, Object> paramMap) {
		JSONObject json = new JSONObject();
		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		Integer beginYear = Integer.valueOf(beginDate.split("-")[0]);
		Integer beginMonth = Integer.valueOf(beginDate.split("-")[1]);
		Integer beginDay = Integer.valueOf(beginDate.split("-")[2]);
		Integer endYear = Integer.valueOf(endDate.split("-")[0]);
		Integer endMonth = Integer.valueOf(endDate.split("-")[1]);
		Integer endDay = Integer.valueOf(endDate.split("-")[2]);
		if (!StringUtils.isNotBlank(beginDate) && !StringUtils.isNotBlank(endDate)) { // 参数校验
			json.put("code", "-1");
			json.put("msg", "请选择要统计的时间");
			return json;
		}
		int dayOff;
		try {
			dayOff = DateUtils.daysBetween(beginDate, endDate);
			if ((dayOff + 1) > 31) {
				json.put("code", "-1");
				json.put("msg", "时间间隔不能超过31天");
				return json;
			}
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

			List<Integer> y = new ArrayList<Integer>();
			List<Integer> y1 = new ArrayList<Integer>();
			List<Integer> y2 = new ArrayList<Integer>();
			List<Integer> y3 = new ArrayList<Integer>();
			List<Map<String, Integer>> orderList = deviceBootService.statByDeviceBootDay(paramMap);
			int xSize = x.size();
			int orderSize = orderList.size();
			for (int i = 0; i < xSize; i++) {
				String year = x.get(i);
				for (int j = 0; j < orderSize; j++) {
					Map<String, Integer> tempMap = orderList.get(j);
					String time = String.valueOf(tempMap.get("time"));
					Object type = tempMap.get("type");
					Integer orderNum = Integer.valueOf(ObjectUtils.toString(tempMap.get("num")));
					if (time.equals(year) && type.equals("BOOT")) {
						y.add(orderNum);
					}
					if (time.equals(year) && type.equals("CON_NET")) {
						y1.add(orderNum);
					}
					if (time.equals(year) && type.equals("JUMP")) {
						y2.add(orderNum);
					}
					if (time.equals(year) && type.equals("HOME_SHOW")) {
						y3.add(orderNum);
					}
				}
				// 没有匹配到，默认初始化为0
				if (y.size() <= i) {
					y.add(0);
				}
				if (y1.size() <= i) {
					y1.add(0);
				}
				if (y2.size() <= i) {
					y2.add(0);
				}
				if (y3.size() <= i) {
					y3.add(0);
				}
			}

			json.put("x", x.toArray());
			json.put("y", y.toArray());
			json.put("y1", y1.toArray());
			json.put("y2", y2.toArray());
			json.put("y3", y3.toArray());

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 导出类型数据
	 * 
	 * @Description
	 * @author wangsai
	 * @date 2016年12月19日 上午11:36:05
	 */
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(@RequestParam Map<String, Object> paramMap,
			 HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "广告统计数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<DeviceBoot> list = deviceBootService.findDeviceBootListForExport(paramMap);
			if (list != null && list.size() > 0) {
				new ExportExcel("广告统计数据", DeviceBoot.class).setDataList(list).write(response, fileName).dispose();
				return null;
			} else {
				addMessage(redirectAttributes, "导出广告统计数据失败！失败信息：没有数据");
			}
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出广告统计数据失败！失败信息：" + e.getMessage());
			e.printStackTrace();
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/deviceBoot/byType";
	}

	/**
	 * 导出按月统计数据
	 * 
	 * @Description
	 * @author wangsai
	 * @date 2016年12月19日 上午11:36:05
	 */
	@RequestMapping(value = "export1", method = RequestMethod.POST)
	public String exportFile1(@RequestParam Map<String, Object> paramMap,
			 HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "广告统计按月数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<DeviceBoot> list = deviceBootService.findDeviceBootMonthForExport(paramMap);
			if (list != null && list.size() > 0) {
				new ExportExcel("广告统计按月数据", DeviceBoot.class).setDataList(list).write(response, fileName).dispose();
				return null;
			} else {
				addMessage(redirectAttributes, "导出连接按月统计数据失败！失败信息：没有数据");
			}
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出连接按月统计数据失败！失败信息：" + e.getMessage());
			e.printStackTrace();
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/deviceBoot/month";
	}

	/**
	 * 导出按日统计数据
	 * 
	 * @Description
	 * @author wangsai
	 * @date 2016年12月19日 上午11:36:05
	 */
	@RequestMapping(value = "export2", method = RequestMethod.POST)
	public String exportFile2(@RequestParam Map<String, Object> paramMap,
			 HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "广告统计按日数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<DeviceBoot> list = deviceBootService.findDeviceBootDayForExport(paramMap);
			if (list != null && list.size() > 0) {
				new ExportExcel("广告统计按日数据", DeviceBoot.class).setDataList(list).write(response, fileName).dispose();
				return null;
			} else {
				addMessage(redirectAttributes, "导出广告按日统计数据失败！失败信息：没有数据");
			}
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出广告按日 统计数据失败！失败信息：" + e.getMessage());
			e.printStackTrace();
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/deviceBoot/day";
	}
}
