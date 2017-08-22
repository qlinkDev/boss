/** 
 * @Package com.uu.modules.om.web 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月18日 上午11:40:30 
 * @version V1.0 
 */ 
package com.uu.modules.om.web;

import java.util.ArrayList;
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
import com.uu.modules.om.condition.RegionCondition;
import com.uu.modules.om.entity.Region;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.om.service.RegionService;
import com.uu.modules.sys.service.LogService;

/** 
 * @Description 区域 对外服务包实现
 * @author yifang.huang
 * @date 2016年3月18日 上午11:40:30 
 */
@Controller
@RequestMapping(value = "${adminPath}/om/region")
public class RegionController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(RegionController.class);
	
	@Autowired
	private RegionService regionService;
	
	@Autowired
	private ChannelService channelService;
	
	@Autowired
	private LogService logService;
	
	@ModelAttribute
	public Region get(@RequestParam(required=false) String id) {
		
		if (StringUtils.isNotBlank(id))
			return regionService.get(id);
		
		return new Region();
		
	}
	
	@RequiresPermissions("om:region:view")
	@RequestMapping(value = {"list", ""})
	public String list(RegionCondition condtion, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<Region> page = regionService.find(new Page<Region>(request, response), condtion); 
		model.addAttribute("page", page);
		
		return "modules/om/regionList";
		
	}
	
	@RequiresPermissions("om:region:view")
	@RequestMapping(value = "form")
	public String form(Region bean, Model model) {
		
		model.addAttribute("region", bean);
		
		// 国家列表
		List<String[]> countryList = channelService.findAllMCC();
		
		// 所有被添加到区域的国家编号
		String seletedCountryCodes = regionService.findAllCountryCodes(bean.getId());
		
		// 当前区域本身选择的国家
		String countryCodes = bean.getCountryCodes();
		
		List<Object[]> result = new ArrayList<Object[]>();
		Object[] tempArr = null;
		for (int i=0; i<countryList.size(); i++) {
			
			Object[] country = (Object[]) countryList.get(i);
			tempArr = new Object[5];
			
			// 数组复制
			System.arraycopy(country, 0, tempArr, 0, 3);
			
			// 回显本地区域已选择国家，如果当前国家编号在区域的countryCodes字段里面，则表示当前国家已被选择
			if (include(countryCodes, (String) country[2]))
				tempArr[3] = "1";
			else 
				tempArr[3] = "0";
			
			// 被其它区域选中的国家不能选择
			if (include(seletedCountryCodes, (String) country[2]))
				tempArr[4] = "1";
			else 
				tempArr[4] = "0";
			
			
			result.add(tempArr);
			tempArr = null;
		}
		
		model.addAttribute("countryList", result);
		
		return "modules/om/regionForm";
		
	}

	@RequiresPermissions("om:region:edit")
	@RequestMapping(value = "save")//@Valid 
	public String save(Region bean, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/om/region/?repage";
		}
		boolean flag = false;
		if(StringUtils.isNotBlank(bean.getId())){
			flag = true;
		}
		
		// 名称不能重复
		RegionCondition condition = new RegionCondition();
		condition.setNeId(bean.getId());
		condition.setEqName(bean.getName());
		List<Region> list = regionService.findListByCondition(condition);
		if (list!=null && list.size()>0) {
			addMessage(model, "区域名称 '" + bean.getName() +"' 已存在");
			return form(bean, model);
		}
		
		// 编号不能重复
		condition.setEqName(null);
		condition.setEqCode(bean.getCode());
		list = regionService.findListByCondition(condition);
		if (list!=null && list.size()>0) {
			addMessage(model, "区域编号 '" + bean.getCode() +"' 已存在");
			return form(bean, model);
		}
		
		// 区域所包含的国家不能为空
		String countryCodes = bean.getCountryCodes();
		if (StringUtils.isBlank(countryCodes)) {
			addMessage(model, "请选择国家");
			return form(bean, model);
		}
		
		// 一个国家只能属于一个区域
		String seletedCountryCodes = regionService.findAllCountryCodes(bean.getId());   // 所有被添加到区域的国家编号
		if (StringUtils.isNotBlank(seletedCountryCodes)) {
			String[] codeArr = countryCodes.split(",");
			for (String code : codeArr) {
				if (seletedCountryCodes.indexOf(code) != -1) {
					addMessage(model, "国家 '" + code + "' 已被其它区域选择");
					return form(bean, model);
				}
			}
		}
		
		// 配置参数验证
		if (!beanValidator(model, bean)){
			return form(bean, model);
		}
		
		// 保存数据
		regionService.save(bean);
		if(flag){
			logService.saveOperateLog(request, "[运营管理 》区域管理]-修改区域，修改的ID为：[{}]", bean.getId());
		} else {
			logService.saveOperateLog(request, "[运营管理 》区域管理]-添加区域，区域的ID为：[{}]", bean.getId());
		}
		
		addMessage(redirectAttributes, "保存区域'" + bean.getName() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/om/region/?repage";
		
	}
	
	@RequiresPermissions("om:region:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/om/region/?repage";
		}
		regionService.delete(id);
		logService.saveOperateLog(request, "[区域管理 》区域管理]-删除区域，删除的ID为：[{}]", id);
		addMessage(redirectAttributes, "删除区域成功");
		return "redirect:"+Global.getAdminPath()+"/om/region/?repage";
	}
	
	private boolean include(String countryCodes, String countryCode) {
		
		if (StringUtils.isBlank(countryCodes))
			return false;
		
		String[] countryCodeArr = countryCodes.split(",");
		for (String code : countryCodeArr) {
			if (code.equals(countryCode)) {
				return true;
			}
		}
		
		return false;
	}
}
