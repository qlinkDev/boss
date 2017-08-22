/** 
 * @Package com.uu.modules.user.web 
 * @Description 
 * @author yifang.huang
 * @date 2016年12月7日 上午10:01:59 
 * @version V1.0 
 */ 
package com.uu.modules.user.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uu.common.persistence.Page;
import com.uu.common.web.BaseController;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.service.LogService;
import com.uu.modules.sys.utils.UserUtils;
import com.uu.modules.user.condition.DayPassRecordCondition;
import com.uu.modules.user.entity.DayPassRecord;
import com.uu.modules.user.service.DayPassRecordService;

/** 
 * @Description 
 * @author yifang.huang
 * @date 2016年12月7日 上午10:01:59 
 */
@Controller
@RequestMapping(value = "${adminPath}/user/dayPass")
public class DayPassRecordController extends BaseController {
	
	@Autowired
	private DayPassRecordService dayPassRecordService;
	
	@Autowired
	private LogService logService;
	
	/**
	 * 
	 * @Description 分布查询
	 * @param condition
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年12月8日 下午2:24:05
	 */
	@RequiresPermissions("user:dayPass:view")
	@RequestMapping(value = {"list", ""})
	public String list(DayPassRecordCondition condition, 
			HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<DayPassRecord> page = dayPassRecordService.findPage(new Page<DayPassRecord>(request, response), condition); 
		model.addAttribute("page", page);
		
		// 是否运营商
		User user = UserUtils.getUser();
		if (user.getChannelAdmin()) {
			return "channel/om/dayPassRecordList";
		}
		return "modules/om/dayPassRecordList";
		
	}
	
	/**
	 * 
	 * @Description 订单取消
	 * @param paramMap
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月12日 下午2:34:45
	 */
	@RequiresPermissions("user:dayPass:edit")
    @RequestMapping(value = "/callback.json",produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> callback(@RequestParam Map<String, Object> paramMap, HttpServletRequest request) {
		
		Map<String, String>  map = dayPassRecordService.saveAndCallback(paramMap);
		
		if(map.get("code") == "1"){
			logService.saveOperateLog(request, "[运营管理 》运营管理》dayPass记录查询]-消费记录回调,记录ID为：[{}]", ObjectUtils.toString(paramMap.get("recordId")));
		}
		
		return map;
		
	}

}
