/** 
 * @Package com.uu.modules.mifi.web 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月15日 上午10:22:38 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uu.common.persistence.Page;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.condition.DeviceMonitorCondition;
import com.uu.modules.mifi.condition.DeviceMonitorDetailCondition;
import com.uu.modules.mifi.entity.DeviceMonitor;
import com.uu.modules.mifi.entity.DeviceMonitorDetail;
import com.uu.modules.mifi.service.DeviceMonitorDetailService;
import com.uu.modules.mifi.service.DeviceMonitorService;

/** 
 * @Description 设备监控主体信息 对外服务包实现
 * @author yifang.huang
 * @date 2016年4月15日 上午10:22:38 
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/deviceMonitor")
public class DeviceMonitorController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(DeviceMonitorController.class);
	
	@Autowired
	private DeviceMonitorService deviceMonitorService;
	
	@Autowired
	private DeviceMonitorDetailService dmdService;

	@ModelAttribute
	public DeviceMonitor get(@RequestParam(required=false) String id) {
		
		if (StringUtils.isNotBlank(id))
			return deviceMonitorService.get(id);
		
		return new DeviceMonitor();
		
	}
	
	/**
	 * 
	 * @Description 列表
	 * @param condtion
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午3:23:24
	 */
	@RequestMapping(value = {"list", ""})
	public String list(DeviceMonitorCondition condtion, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<DeviceMonitor> page = deviceMonitorService.find(new Page<DeviceMonitor>(request, response), condtion); 
		model.addAttribute("page", page);
		
		return "modules/mifi/deviceMonitorList";
		
	}
	
	/**
	 * 
	 * @Description 详细列表
	 * @param id
	 * @param request
	 * @param model
	 * @param redirectAttributes
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午3:17:21
	 */
	@RequestMapping(value = "detailList")
	public String detailList(DeviceMonitorDetailCondition condtion, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<DeviceMonitorDetail> page = dmdService.find(new Page<DeviceMonitorDetail>(request, response), condtion); 
		model.addAttribute("page", page);
		
		return "modules/mifi/deviceMonitorDetailList";
		
		
	}
	
}
