package com.uu.modules.mifi.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.uu.modules.mifi.entity.MifiOrder;
import com.uu.modules.mifi.service.MifiOrderService;
import com.uu.modules.om.condition.RegionCondition;
import com.uu.modules.om.entity.Region;
import com.uu.modules.om.service.RegionService;

import net.sf.json.JSONObject;

/**
 * 订单统计（国家订单数量以及订单营业额）
 * 
 * @author shuxin
 * @date 2016年9月1日
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/mifiOrderStat")
public class MifiOrderStatController extends BaseController {

	@Autowired
	private MifiOrderService mifiOrderService;
	
	@Autowired
	private RegionService regionService;
	

	/**
	 * 按国家统计订单页面
	 * 
	 * @athor shuxin
	 * @date 2016年9月2日上午10:01:05
	 * @param model
	 * @return String
	 */
	@RequestMapping(value = { "", "mcc" })
	public String mcc(ModelMap model) {
		model.put("begin", DateUtils.getYear() + "-" + DateUtils.getMonth() + "-" + "01");
		model.put("end", DateUtils.getDate());
		return "modules/mifi/mifiOrderStatMcc";
	}

	/**
	 * 按年统计订单页面
	 * 
	 * @athor shuxin
	 * @date 2016年9月2日下午4:34:59
	 * @param model
	 * @return String
	 */
	@RequestMapping(value = { "year" })
	public String year(ModelMap model) {
		Map<String, String> countryMap = getCountrys();
		model.put("countrys", countryMap);
		model.put("begin", DateUtils.formatDate(new Date(), "yyyy"));
		model.put("end", DateUtils.formatDate(new Date(), "yyyy"));
		return "modules/mifi/mifiOrderStatYear";
	}

	/**
	 * 按月统计订单页面
	 * 
	 * @athor shuxin
	 * @date 2016年9月5日上午9:53:01
	 * @param model
	 * @return String
	 */
	@RequestMapping(value = { "month" })
	public String month(ModelMap model) {
		Map<String, String> countryMap = getCountrys();
		model.put("countrys", countryMap);
		model.put("begin", DateUtils.formatDate(new Date(), "yyyy-MM"));
		model.put("end", DateUtils.formatDate(new Date(), "yyyy-MM"));
		return "modules/mifi/mifiOrderStatMonth";
	}

	/**
	 * 按天统计订单页面
	 * 
	 * @athor shuxin
	 * @date 2016年9月5日上午10:11:40
	 * @param model
	 * @return String
	 */
	@RequestMapping(value = { "day" })
	public String day(ModelMap model) {
		Map<String, String> countryMap = getCountrys();
		model.put("countrys", countryMap);
		model.put("begin", DateUtils.getYear() + "-" + DateUtils.getMonth() + "-" + "01");
		model.put("end", DateUtils.getDate());
		return "modules/mifi/mifiOrderStatDay";
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
		List<Map<String, String>> mccList = mifiOrderService.findMccsByCountryCode(countryCode);
		for (int i = 0; i < mccList.size(); i++) {
			Map<String, String> tempMap = mccList.get(i);
			countryMap.put(tempMap.get("country_mcc"), tempMap.get("country_name_cn"));
		}
		return countryMap;
	}


	/**
	 * 根据国家统计订单
	 * 
	 * @athor shuxin
	 * @date 2016年9月2日下午4:27:24
	 * @param paramMap
	 * @return JSONObject
	 */
	@RequestMapping("statByMcc")
	public @ResponseBody JSONObject statByMcc(@RequestParam Map<String, Object> paramMap) {
		return mifiOrderService.statMifiOrderBYMcc(paramMap);
	}

	/**
	 * 按年统计订单
	 * 
	 * @athor shuxin
	 * @date 2016年9月5日上午11:58:06
	 * @param paramMap
	 * @return JSONObject
	 */
	@RequestMapping("statByYear")
	public @ResponseBody JSONObject statByYear(@RequestParam Map<String, Object> paramMap) {
		return mifiOrderService.statMifiOrderByYear(paramMap);
	}

	/**
	 * 按月统计
	 * 
	 * @athor shuxin
	 * @date 2016年9月5日上午9:31:28
	 * @param paramMap
	 * @return JSONObject
	 */
	@RequestMapping("statByMonth")
	public @ResponseBody JSONObject statByMonth(@RequestParam Map<String, Object> paramMap) {
		return mifiOrderService.statMifiOrderByMonth(paramMap);
	}

	/**
	 * 按日统计
	 * @athor shuxin
	 * @date 2016年9月5日下午1:38:12
	 * @param paramMap
	 * @return
	 * JSONObject 
	 */
	@RequestMapping("statByDay")
	public @ResponseBody JSONObject statByDay(@RequestParam Map<String, Object> paramMap) {
		return mifiOrderService.statMifiOrderByDay(paramMap);
	}
	/**
	 * 导出MCC
	 * @Description 
	 * @author wangsai
	 * @date 2017年2月27日 上午11:22:36
	 */
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(@RequestParam Map<String, Object> paramMap,
			 HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "订单MCC统计数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<MifiOrder> list = mifiOrderService.statMifiOrderBYMccExport(paramMap);
			if (list != null && list.size() > 0) {
				new ExportExcel("订单MCC统计数据", MifiOrder.class).setDataList(list).write(response, fileName).dispose();
				return null;
			} else {
				addMessage(redirectAttributes, "导出订单MCC统计数据失败！失败信息：没有数据");
			}
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出订单MCC数据失败！失败信息：" + e.getMessage());
			e.printStackTrace();
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/mifiOrderStat";
	}
	@RequestMapping(value = "export1", method = RequestMethod.POST)
	public String exportFile1(@RequestParam Map<String, Object> paramMap,
			 HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "订单按年统计数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<MifiOrder> list = mifiOrderService.statMifiOrderBYYearExport(paramMap);
			if (list != null && list.size() > 0) {
				new ExportExcel("订单按年统计数据", MifiOrder.class).setDataList(list).write(response, fileName).dispose();
				return null;
			} else {
				addMessage(redirectAttributes, "导出订单按年统计数据失败！失败信息：没有数据");
			}
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出订单按年数据失败！失败信息：" + e.getMessage());
			e.printStackTrace();
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/mifiOrderStat/year";
	}
	@RequestMapping(value = "export2", method = RequestMethod.POST)
	public String exportFile2(@RequestParam Map<String, Object> paramMap,
			 HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "订单按月统计数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<MifiOrder> list = mifiOrderService.statMifiOrderBYMonthExport(paramMap);
			if (list != null && list.size() > 0) {
				new ExportExcel("订单按月统计数据", MifiOrder.class).setDataList(list).write(response, fileName).dispose();
				return null;
			} else {
				addMessage(redirectAttributes, "导出订单按月统计数据失败！失败信息：没有数据");
			}
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出订单按月数据失败！失败信息：" + e.getMessage());
			e.printStackTrace();
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/mifiOrderStat/month";
	}
	@RequestMapping(value = "export3", method = RequestMethod.POST)
	public String exportFile3(@RequestParam Map<String, Object> paramMap,
			 HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "订单按日统计数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<MifiOrder> list = mifiOrderService.statMifiOrderBYDayExport(paramMap);
			if (list != null && list.size() > 0) {
				new ExportExcel("订单按日统计数据", MifiOrder.class).setDataList(list).write(response, fileName).dispose();
				return null;
			} else {
				addMessage(redirectAttributes, "导出订单MCC统计数据失败！失败信息：没有数据");
			}
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出订单MCC数据失败！失败信息：" + e.getMessage());
			e.printStackTrace();
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/mifiOrderStat/day";
	}
	
}
