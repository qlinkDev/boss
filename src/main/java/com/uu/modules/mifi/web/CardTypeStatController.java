package com.uu.modules.mifi.web;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uu.common.persistence.Page;
import com.uu.common.utils.DateUtils;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.entity.CardBasicInfo;
import com.uu.modules.mifi.service.CardManageService;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.utils.Constants;

/**
 * 
 * @author wangyong
 * @date 2016年4月8日
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/cardTypeStat")
public class CardTypeStatController extends BaseController {
	
	public static Logger logger = LoggerFactory.getLogger(CardTypeStatController.class);

	@Autowired
	private CardManageService cardManageService;
	@Autowired
	private ChannelService channelService;
	
	
	@RequestMapping(value = "init")
	public String init(CardBasicInfo cardBasicInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/mifi/cardTypeStatList";
	}
	
	@RequestMapping(value = { "list", "" })
	public String list(@RequestParam Map<String, Object> paramMap, @RequestParam(required = false) String[] typeArr, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<HashMap> page = cardManageService.findCardTypeStatList(new Page<HashMap>(request, response), paramMap, typeArr);
		Map<String, Object> map = cardManageService.statAllSimFreeAndUsed(paramMap, typeArr);
		model.addAttribute("page", page);
		model.addAttribute("map", map);
		model.addAllAttributes(paramMap);
		// 多选框回显到页面
		if (null != typeArr && 0 != typeArr.length) {
			JSONArray jsonArr = JSONArray.fromObject(typeArr);
			model.addAttribute("typeArr", jsonArr);
		}
		return "modules/mifi/cardTypeStatList";
	}
	
	/**
	 * 
	 * @Description 按国家统计卡用量页面跳转
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年10月19日 上午11:29:31
	 */
	@RequestMapping(value = "cardStatisticsByCountryPage")
	public String cardStatisticsByCountryPage(@RequestParam Map<String, Object> paramMap, 
			HttpServletRequest request, HttpServletResponse response, Model model) {
		List<String[]> mccList = channelService.findAllMCC();
		model.addAttribute("mccList", mccList);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		model.addAttribute("startDate", df.format(new Date()));
		model.addAttribute("endDate", df.format(new Date()));
		return "modules/mifi/cardCountryStatisticsList";
		
	}
	
	/**
	 * 
	 * @Description 按国家统计卡用量
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年10月19日 上午11:29:31
	 */
	@RequestMapping(value = "cardStatisticsByCountry")
	public String cardStatisticsByCountry(@RequestParam Map<String, Object> paramMap, @RequestParam(required = false) String[] mccs,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		// 生成时间列
		String startDateStr = ObjectUtils.toString(paramMap.get(Constants.STARTDATE));
		String endDateStr = ObjectUtils.toString(paramMap.get(Constants.ENDDATE));
		List<String> dateList = new ArrayList<String>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtils.parseDate(startDateStr));
		Long startTime = calendar.getTimeInMillis();
		calendar.setTime(DateUtils.parseDate(endDateStr));
		Long endTime = calendar.getTimeInMillis();
		Long oneDay = 1000 * 60 * 60 * 24l;
		Long time = startTime;
		String dateStr;
		for (int i = 0; time <= endTime && i < 7; i++) {
			dateStr = DateUtils.formatDate(new Date(time), (Object[]) null);
			dateList.add(dateStr);
			time += oneDay;
		}
		model.addAttribute(Constants.DATELIST, dateList);
		// 执行统计
		Map<String, Map<String, Object>> resultMap = cardManageService.cardStatisticsByCountry(dateList, mccs);
		model.addAttribute(Constants.ROWS, resultMap);
		// mcc选项列表
		List<String[]> mccList = channelService.findAllMCC();
		model.addAttribute(Constants.MCCLIST, mccList);
		// 国家多选框回显到页面
		if (null != mccs && 0 != mccs.length) {
			JSONArray jsonArr = JSONArray.fromObject(mccs);
			model.addAttribute(Constants.MCCS, jsonArr);
		}

		model.addAttribute(Constants.STARTDATE, startDateStr);
		model.addAttribute(Constants.ENDDATE, endDateStr);
		return "modules/mifi/cardCountryStatisticsList";
	}
}
