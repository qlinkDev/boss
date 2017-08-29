/** 
 * @Package com.uu.modules.mifi.web 
 * @Description 
 * @author yifang.huang
 * @date 2016年11月9日 下午5:01:31 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uu.common.config.Global;
import com.uu.common.persistence.Page;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.condition.DeviceDetectionCondition;
import com.uu.modules.mifi.entity.DeviceDetection;
import com.uu.modules.mifi.service.DeviceDetectionService;
import com.uu.modules.mifi.service.MifiManageService;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.service.LogService;
import com.uu.modules.sys.utils.UserUtils;

/** 
 * @Description 设备检查   对外服务包实现
 * @author yifang.huang
 * @date 2016年11月9日 下午5:01:31 
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/deviceDetection")
public class DeviceDetectionController extends BaseController {

	@Autowired
	private DeviceDetectionService detectionService;
	
	@Autowired
	private MifiManageService mifiManageService;
	
	@Autowired
	private LogService logService;
	
	@ModelAttribute
	public DeviceDetection get(@RequestParam(required=false) String id) {
		
		if (StringUtils.isNotBlank(id))
			return detectionService.get(id);
		
		return new DeviceDetection();
		
	}
	
	@RequiresPermissions("mifi:detection:view")
	@RequestMapping(value = "form")
	public String form(DeviceDetection bean, Model model) {
		
		model.addAttribute("detection", bean);
		
		return "modules/mifi/deviceDetectionForm";
		
	}

	/**
	 * 
	 * @Description 保存
	 * @param paramMap
	 * @param request
	 * @param model
	 * @param redirectAttributes
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年11月10日 上午11:21:23
	 */
	@RequiresPermissions("mifi:detection:edit")
	@RequestMapping(value = "save")//@Valid 
	public String save(DeviceDetection detection, 
			HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/mifi/deviceDetection/?repage";
		}

		// 参数
		String imei = detection.getImei();
		String useFlag = detection.getUseFlag();
		String itemStrs = detection.getItemStrs();
		
		// 设备编号不能为空
		if (StringUtils.isBlank(imei)) {
			addMessage(model, "设备编号不能为空");
			return form(new DeviceDetection(), model);
		}
		// 检查项不能为空
		if (StringUtils.isBlank(itemStrs)) {
			addMessage(model, "检查项不能为空");
			return form(new DeviceDetection(), model);
		}
		// 检查项不能为空
		if (StringUtils.isBlank(useFlag)) {
			addMessage(model, "设备是否可用不能为空");
			return form(new DeviceDetection(), model);
		}
		// 设备入库判断
		Map<String, String> mifiMap = mifiManageService.getMifilistBySn(imei);
		if (mifiMap == null) {
			addMessage(model, "设备 [" + imei + "] 未找到");
			return form(new DeviceDetection(), model);
		}
		String sourceType = mifiMap.get("sourceType");
		// 如果是渠道管理员，判断设备是否属于当前渠道
		User user = UserUtils.getUser();
		if (user.getChannelAdmin()) {
			if (!sourceType.equals(user.getChannelNameEn())) {
				addMessage(model, "设备 [" + imei + "] 不属于当前渠道商");
				return form(new DeviceDetection(), model);
			}
		}
		
		try {
			// 保存数据
			Map<String, String> map = detectionService.save(detection, sourceType);
			String id = map.get("id");
			String isUpdate = map.get("isUpdate");
			if("1".equals(isUpdate)){
				logService.saveOperateLog(request, "[设备管理 》设备检测]-设备检测修改，修改的ID为：[{}]", id);
			} else {
				logService.saveOperateLog(request, "[运营管理 》设备检测]-设备检测添加，添加的ID为：[{}]", id);
			}
			addMessage(model, map.get("message"));

		} catch (NumberFormatException e) {
			e.printStackTrace();
			addMessage(model, "保存失败，请联系客户！");
			return form(new DeviceDetection(), model);
		}
		
		return "redirect:"+Global.getAdminPath()+"/mifi/deviceDetection/?repage";
		
	}
	
	/**
	 * 
	 * @Description 分页查询
	 * @param condition
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年11月10日 上午11:21:43
	 */
	@RequiresPermissions("mifi:detection:view")
	@RequestMapping(value = "")
	public String init(DeviceDetectionCondition condition, HttpServletRequest request, HttpServletResponse response, Model model) {
		// 是否运营商
		User user = UserUtils.getUser();
		if (user.getChannelAdmin()) {
			return "channel/mifi/deviceDetectionList";
		}
		return "modules/mifi/deviceDetectionList";
	}
	@RequiresPermissions("mifi:detection:view")
	@RequestMapping(value = "list")
	public String list(DeviceDetectionCondition condition, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<DeviceDetection> page = detectionService.findPage(new Page<DeviceDetection>(request, response), condition); 
		model.addAttribute("page", page);
		// 是否运营商
		User user = UserUtils.getUser();
		if (user.getChannelAdmin()) {
			return "channel/mifi/deviceDetectionList";
		}
		return "modules/mifi/deviceDetectionList";
	}
	
	/**
	 * 
	 * @Description 删除
	 * @param id
	 * @param request
	 * @param redirectAttributes
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年11月10日 上午11:23:27
	 */
	@RequiresPermissions("mifi:detection:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/mifi/deviceDetection/?repage";
		}
		detectionService.delete(id);
		logService.saveOperateLog(request, "[运营管理 》设备检测]-设备检测删除，删除的ID为：[{}]", id);
		addMessage(redirectAttributes, "删除成功");
		return "redirect:"+Global.getAdminPath()+"/mifi/deviceDetection/?repage";
	}
	
}
