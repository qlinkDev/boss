/** 
 * @Package com.uu.modules.mifi.web 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月8日 下午2:01:16 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.uu.modules.mifi.condition.TestDeviceCondition;
import com.uu.modules.mifi.entity.TestDevice;
import com.uu.modules.mifi.entity.TestDevice.Status;
import com.uu.modules.mifi.service.MifiManageService;
import com.uu.modules.mifi.service.TestDeviceService;

/** 
 * @Description 测试设备 对外服务包实现
 * @author yifang.huang
 * @date 2016年4月8日 下午2:01:16 
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/testDevice")
public class TestDeviceController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(TestDeviceController.class);
	
	@Autowired
	private TestDeviceService testDeviceService;
	
	@Autowired
	private MifiManageService mifiManageService;
	
	@ModelAttribute
	public TestDevice get(@RequestParam(required=false) String id) {
		
		if (StringUtils.isNotBlank(id))
			return testDeviceService.get(id);
		
		return new TestDevice();
		
	}
	
	@RequiresPermissions("mifi:test:view")
	@RequestMapping(value = {"list", ""})
	public String list(TestDeviceCondition condtion, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<TestDevice> page = testDeviceService.find(new Page<TestDevice>(request, response), condtion); 
		model.addAttribute("page", page);
		
		return "modules/mifi/testDeviceList";
		
	}
	
	@RequiresPermissions("mifi:test:view")
	@RequestMapping(value = "form")
	public String form(TestDevice bean, Model model) {
		
		model.addAttribute("device", bean);
		
		return "modules/mifi/testDeviceForm";
		
	}

	@RequiresPermissions("mifi:test:edit")
	@RequestMapping(value = "save")
	public String save(TestDevice bean, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/mifi/TestDevice/?repage";
		}
		
		// 配置参数验证
		if (!beanValidator(model, bean)){
			return form(bean, model);
		}
		
		// 根据设备编号查询mifilist表判断设备是否存在
		HashMap<String, String> map = mifiManageService.getMifilistBySn(bean.getImei());
		if (map == null) {
			addMessage(model, "设备未入库");
			return form(bean, model);
		}
		
		// 借出状态的设备，不能再次借出
		TestDeviceCondition condition = new TestDeviceCondition();
		condition.setEqImei(bean.getImei());
		condition.setEqStatus(Status.LEND);
		List<TestDevice> list = testDeviceService.findListByCondition(condition);
		if (list!=null && list.size()>0) {
			addMessage(model, "设备'" + bean.getImei() + "'已借出，请先归还");
			return form(bean, model);
		}
		
		// 保存数据
		testDeviceService.save(bean);
		
		addMessage(redirectAttributes, "保存测试设备'" + bean.getImei() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/mifi/testDevice/?repage";
		
	}

	/**
	 * 
	 * @Description 归还测试设备
	 * @param bean
	 * @param request
	 * @param model
	 * @param redirectAttributes
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年4月8日 下午3:40:21
	 */
	@RequiresPermissions("mifi:test:edit")
	@RequestMapping(value = "sendBack")
	public String sendBack(String id, String returnUserName, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/mifi/testDevice/?repage";
		}
		
		if (StringUtils.isBlank(id)) {
			addMessage(redirectAttributes, "请选择要归还的设备");
			return "redirect:"+Global.getAdminPath()+"/mifi/testDevice/?repage";
		}
		TestDevice device = testDeviceService.get(id);
		if (device == null) {
			addMessage(redirectAttributes, "请选择要归还的设备");
			return "redirect:"+Global.getAdminPath()+"/mifi/testDevice/?repage";
		}
		
		if (StringUtils.isBlank(returnUserName)) {
			addMessage(redirectAttributes, "请输入归还人姓名");
			return "redirect:"+Global.getAdminPath()+"/mifi/testDevice/?repage";
		}
		
		device.setReturnUserName(returnUserName);
		device.setReturnDate(new Date());
		device.setStatus(Status.RETURN);
		
		// 保存数据
		testDeviceService.save(device);
		
		addMessage(redirectAttributes, "归还测试设备'" + device.getImei() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/mifi/testDevice/?repage";
		
	}
	
	@RequiresPermissions("mifi:test:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/mifi/testDevice/?repage";
		}
		
		testDeviceService.delete(id);
		
		addMessage(redirectAttributes, "删除测试设备成功");
		return "redirect:"+Global.getAdminPath()+"/mifi/testDevice/?repage";
	}

}
