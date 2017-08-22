package com.uu.modules.mifi.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uu.common.persistence.Page;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.service.MifiManageService;
import com.uu.modules.mifi.service.MifiVersionService;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.utils.DictUtils;
import com.uu.modules.sys.utils.UserUtils;
import com.uu.modules.utils.Constants;

@Controller
@RequestMapping(value = "${adminPath}/mifi/mifiManage")
public class MifiManageController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(MifiManageController.class);

	@Autowired
	MifiManageService mifiManageService;
	
	@Autowired
	private MifiVersionService mifiVersionService;
	
	@Autowired
	private ChannelService channelService;

	@RequestMapping(value = { "list", "" })
	public String list(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		
		// 是否运营商
		User user = UserUtils.getUser();
		boolean isChannelAdmin = user.getChannelAdmin();
		
		if (paramMap.containsKey("initTag")) {// 无奈啊,否则默认执行此方法
			
			if (isChannelAdmin)
				paramMap.put("sourceType", user.getChannelNameEn());	// 只能查询运营商自己的数据
			
			Page<HashMap> page = mifiManageService.findMifiManageList(new Page<HashMap>(request, response),
					paramMap);
			
			model.addAttribute("page", page);
		}
		model.addAllAttributes(paramMap);
		
		if (isChannelAdmin)
			return "channel/mifi/mifiManageList";
		
		return "modules/mifi/mifiManageList";

	}
	
	/**
	 * 
	 * @Description 取JSON格式的设备列表
	 * @param recordId
	 * @param request
	 * @return Map<String,Object>  
	 * @author yifang.huang
	 * @date 2016年3月24日 下午2:48:54
	 */
    @RequestMapping(value = "/device.json",produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public List<Map<String, String>> getDeviceList(@RequestParam String startDate, @RequestParam String endDate, HttpServletRequest request) {
		
		List<Map<String, String>> map = null;
		
		try {
			
			User user = UserUtils.getUser();
			map = mifiManageService.getCanBuyDevice(user.getChannelNameEn(), startDate, endDate);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("取设备列表失败", e);
		}
		
		return map;
		
	}
	
	/**
	 * 
	 * @Description 取JSON格式的设备列表
	 * @param recordId
	 * @param request
	 * @return Map<String,Object>  
	 * @author yifang.huang
	 * @date 2016年3月24日 下午2:48:54
	 */
    @RequestMapping(value = "/checkDevice.json",produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> checkDevice(@RequestParam String imei, @RequestParam String startDate, 
			@RequestParam String endDate, HttpServletRequest request) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		try {
			
			User user = UserUtils.getUser();
			String sourceType = user.getChannelNameEn();
			
			// 判断设备是否属于当前渠道
			Map<String, String> mifiMap = mifiManageService.getMifilistBySn(imei);
			if (mifiMap == null) {
				map.put("code", "error");
				map.put("message", "设备 [" + imei + "] 未入库");
				return map;
			}
			if (!sourceType.equals(mifiMap.get("sourceType"))) {
				map.put("code", "error");
				map.put("message", "设备 [" + imei + "] 不属于当前渠道商");
				return map;
			}
			
			// 判断设备是否空闲
			boolean flag = mifiManageService.checkCanBuyDevice(imei, user.getChannelNameEn(), startDate, endDate);
			if (flag) {
				map.put("code", "success");
				map.put("message", "设备[" + imei + "] 可以下单!");
				return map;
			} else {
				map.put("code", "error");
				map.put("message", "设备[" + imei + "] 在所选行程内已存在有效订单,不可以下单!");
				return map;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("取设备列表失败", e);
			map.put("code", "error");
			map.put("message", "检测设备是否可以下单失败!");
			return map;
		}

	}
	
	/**
	 * 
	 * @Description 取设备版本
	 * @param imei
	 * @param request
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年11月10日 下午5:49:30
	 */
    @RequestMapping(value = "/version.json",produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> getVersion(@RequestParam String imei, HttpServletRequest request) {
		
		Map<String, String> map = new HashMap<String, String>();
		String version = "";
		
		try {
			// 设备判断
			Map<String, String> mifiMap = mifiManageService.getMifilistBySn(imei);
			if (mifiMap == null) {
				map.put("status", "error");
				map.put("message", "设备 [" + imei + "] 未入库");
				return map;
			}
			User user = UserUtils.getUser();
			String sourceType = user.getChannelNameEn();
			if (user.getChannelAdmin() && !sourceType.equals(mifiMap.get("sourceType"))) {
				map.put("status", "error");
				map.put("message", "设备 [" + imei + "] 不属于当前渠道商");
				return map;
			}
			
			// 取设备版本
			Double imeiVersion = mifiVersionService.getMifiVersion(imei);
			
			if (imeiVersion == 0) {
				map.put("status", "error");
				map.put("message", "设备版本信息未配置");
				return map;
			}
			
			version = imeiVersion + "";
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", "error");
			map.put("message", "取设备版本失败");
			return map;
		}

		map.put("status", "success");
		map.put("version", version);
		map.put("message", "取设备版本成功");
		return map;
		
	}

    /**
     * 
     * @Description 设备库存率
     * @param paramMap
     * @param request
     * @param response
     * @param model
     * @return String  
     * @author yifang.huang
     * @date 2017年1月17日 下午2:00:02
     */
	@RequestMapping("deviceStock")
	public String deviceStock(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		
		if (paramMap.containsKey("queryData")) {// 是否查询数据
			
			String groupType = ObjectUtils.toString(paramMap.get("groupType"));
			
			List<Map<String, String>> resultList = null;
			if ("channel".equals(groupType))
				resultList = groupByChannel();
			if ("inTime".equals(groupType))
				resultList = groupByInTime();
			
			// 返回结果
			model.addAttribute("resultList", resultList);
			
		}
		
		model.addAllAttributes(paramMap);
		
		return "modules/mifi/deviceStock";

	}
	
	// 按渠道分组查询
	private List<Map<String, String>> groupByChannel() {
		
		// 返回结果
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		
		// 各渠道设备总数
		List<Map<String, Object>> totalList = mifiManageService.getDeviceTotalGroupByChannel();
		if (totalList!=null && totalList.size()>0) {
			// 各渠道设备总数Map<sourceType, total>
			Map<String, String> totalMap = mapListToMap(totalList);
			// 各渠道设备使用数
			String flowDateStr = DictUtils.getDictValue(Constants.DICT_DEVICE_USED_FLOW_DATA_LABEL, Constants.DICT_DEVICE_USED_FLOW_DATA_TYPE, "10");
			int flowDate = Integer.valueOf(flowDateStr);
			List<Map<String, Object>> usedList = mifiManageService.getDeviceUsedTotalGroupByChannel(flowDate);
			Map<String, String> usedMap = mapListToMap(usedList);
			// 渠道列表
			List<Channel> channelList = channelService.findChannelList();
			if (channelList!=null && channelList.size()>0) {
				Map<String, String> map = null;
				String total = null;
				String used = null;
				for (Channel channel : channelList) {
					
					map = new HashMap<String, String>();
					total = totalMap.get(channel.getChannelNameEn());
					used = usedMap.get(channel.getChannelNameEn());
					total = StringUtils.isBlank(total) ? "0" : total;
					used = StringUtils.isBlank(used) ? "0" : used;
					
					map.put("name", channel.getChannelNameEn() + "|" + channel.getChannelName());
					map.put("total", total);
					map.put("used", used);
					map.put("stock", (Integer.valueOf(total) - Integer.valueOf(used)) + "");
					resultList.add(map);
					
					map = null;
					total = null;
					used = null;
				}
			}
		}
		
		return resultList;
	}
	
	// 按渠道分组查询
	private List<Map<String, String>> groupByInTime() {
		
		// 返回结果
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		
		// 各渠道设备总数
		List<Map<String, Object>> totalList = mifiManageService.getDeviceTotalGroupByInTime();
		if (totalList!=null && totalList.size()>0) {
			// 各渠道设备总数Map<sourceType, total>
			Map<String, String> totalMap = mapListToMap(totalList);
			// 各渠道设备使用数
			String flowDateStr = DictUtils.getDictValue(Constants.DICT_DEVICE_USED_FLOW_DATA_LABEL, Constants.DICT_DEVICE_USED_FLOW_DATA_TYPE, "10");
			int flowDate = Integer.valueOf(flowDateStr);
			List<Map<String, Object>> usedList = mifiManageService.getDeviceUsedTotalGroupByInTime(flowDate);
			Map<String, String> usedMap = mapListToMap(usedList);
			// 入库时间列表
			List<Map<String, Object>> inTimeList = mifiManageService.getInTimeList();
			if (inTimeList!=null && inTimeList.size()>0) {
				Map<String, String> map = null;
				String total = null;
				String used = null;
				String inTime = null;
				for (Map<String, Object> bean : inTimeList) {
					inTime = ObjectUtils.toString(bean.get("inTime"));
					
					map = new HashMap<String, String>();
					total = totalMap.get(inTime);
					used = usedMap.get(inTime);
					total = StringUtils.isBlank(total) ? "0" : total;
					used = StringUtils.isBlank(used) ? "0" : used;
					
					map.put("name", inTime);
					map.put("total", total);
					map.put("used", used);
					map.put("stock", (Integer.valueOf(total) - Integer.valueOf(used)) + "");
					resultList.add(map);
					
					map = null;
					total = null;
					used = null;
				}
			}
		}
		
		return resultList;
	}
	
	private Map<String, String> mapListToMap(List<Map<String, Object>> mapList) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		if (mapList==null || mapList.size()==0)
			return map;
		
		for (Map<String, Object> tempMap : mapList) {
			map.put(ObjectUtils.toString(tempMap.get("keyValue")), ObjectUtils.toString(tempMap.get("total")));
		}
		
		return map;
	}
}
