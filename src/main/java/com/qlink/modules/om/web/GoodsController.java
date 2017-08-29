/** 
 * @Package com.uu.modules.om.web 
 * @Description 
 * @author yifang.huang
 * @date 2016年12月16日 下午2:45:54 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.om.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uu.common.config.Global;
import com.uu.common.persistence.Page;
import com.uu.common.web.BaseController;
import com.uu.modules.om.condition.AdvertisingCondition;
import com.uu.modules.om.entity.Advertising;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.entity.Region;
import com.uu.modules.om.service.AdvertisingService;
import com.uu.modules.om.service.RegionService;
import com.uu.modules.om.utils.ChannelUtils;
import com.uu.modules.sys.service.LogService;

/** 
 * @Description 广告   对外服务包实现
 * @author yifang.huang
 * @date 2016年12月16日 下午2:45:54 
 */
@Controller
@RequestMapping(value = "${adminPath}/om/goods")
public class GoodsController extends BaseController {

	@Autowired
	private AdvertisingService advertisingService;
	
	@Autowired
	private RegionService regionService;
	
	@Autowired
	private LogService logService;
	
	@ModelAttribute
	public Advertising get(@RequestParam(required=false) String id) {
		
		if (StringUtils.isNotBlank(id))
			return advertisingService.get(id);
		
		return new Advertising();
		
	}
	
	@RequiresPermissions("om:goods:view")
	@RequestMapping(value = "form")
	public String form(Advertising bean, Model model) {
		
		model.addAttribute("goods", bean);
		
		// 渠道列表
		List<Channel> channelList = ChannelUtils.getChannelList();
		String sourceTypes = bean.getSourceTypes();			// 广告所属渠道
		List<Object[]> result = new ArrayList<Object[]>();
		Object[] tempArr = null;
		for (Channel channel : channelList) {
			
			tempArr = new Object[3];
			tempArr[0] = channel.getChannelNameEn();
			tempArr[1] = channel.getChannelName();
		
			// 回显已选择的渠道
			if (include(sourceTypes, channel.getChannelNameEn()))
				tempArr[2] = "1";
			else 
				tempArr[2] = "0";
			
			result.add(tempArr);
			tempArr = null;
		}
		model.addAttribute("channelList", result);
		
		// 运营国家列表
		model.addAttribute("countryList", getCountryList(bean.getCountryCodes()));
		
		
		return "modules/om/advertisingForm";
		
	}

	/**
	 * 
	 * @Description 保存
	 * @param bean
	 * @param request
	 * @param model
	 * @param redirectAttributes
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年12月16日 下午3:00:36
	 */
	@RequiresPermissions("om:goods:edit")
	@RequestMapping(value = "save")//@Valid 
	public String save(Advertising bean, 
			HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/om/goods/?repage";
		}

		try {
			// 保存数据
			Map<String, String> map = advertisingService.save(bean);
			if  ("success".equals(map.get("status"))) {
				String id = map.get("id");
				String isUpdate = map.get("isUpdate");
				if("1".equals(isUpdate)){
					logService.saveOperateLog(request, "[运营管理 》广告管理]-广告修改，修改的ID为：[{}]", id);
				} else {
					logService.saveOperateLog(request, "[运营管理 》广告管理]-广告添加，添加的ID为：[{}]", id);
				}
			} else {
				addMessage(model, map.get("message"));
				return form(new Advertising(), model);
			}
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(model, "保存失败，请联系客户！");
			return form(new Advertising(), model);
		}
		
		return "redirect:"+Global.getAdminPath()+"/om/goods/?repage";
		
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
	 * @date 2016年12月16日 下午3:00:48
	 */
	@RequiresPermissions("om:goods:view")
	@RequestMapping(value = {"list", ""})
	public String list(AdvertisingCondition condition, HttpServletRequest request, 
			HttpServletResponse response, Model model) {

		Page<Advertising> page = advertisingService.findPage(new Page<Advertising>(request, response), condition); 
		model.addAttribute("page", page);
		
		return "modules/om/advertisingList";
		
	}

	/**
	 * 
	 * @Description 广告上架
	 * @param paramMap
	 * @param request
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2017年2月13日 下午3:26:54
	 */
	@RequiresPermissions("om:goods:edit")
    @RequestMapping(value = "/shelfUp.json", produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> shelfUp(@RequestParam Map<String, Object> paramMap, HttpServletRequest request) {
		
		Map<String, String>  map = advertisingService.saveAndShelfUp(paramMap);
		
		if(map.get("code") == "1"){
			logService.saveOperateLog(request, "[运营管理 》运营管理 》广告管理]-广告上架，广告ID为：[{}]", ObjectUtils.toString(paramMap.get("id")));
		}
		
		return map;
		
	}

	/**
	 * 
	 * @Description 广告下架
	 * @param paramMap
	 * @param request
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2017年2月13日 下午3:26:54
	 */
	@RequiresPermissions("om:goods:edit")
    @RequestMapping(value = "/shelfDown.json", produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> shelfDown(@RequestParam Map<String, Object> paramMap, HttpServletRequest request) {
		
		Map<String, String>  map = advertisingService.saveAndShelfDown(paramMap);
		
		if(map.get("code") == "1"){
			logService.saveOperateLog(request, "[运营管理 》运营管理 》广告管理]-广告下架，广告ID为：[{}]", ObjectUtils.toString(paramMap.get("id")));
		}
		
		return map;
		
	}
	
	/**
	 * 
	 * @Description 删除
	 * @param id
	 * @param request
	 * @param redirectAttributes
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年12月16日 下午3:01:20
	 */
	@RequiresPermissions("om:goods:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/om/goods/?repage";
		}
		advertisingService.delete(id);
		logService.saveOperateLog(request, "[运营管理 》广告管理]-广告删除，删除的ID为：[{}]", id);
		addMessage(redirectAttributes, "删除成功");
		return "redirect:"+Global.getAdminPath()+"/om/goods/?repage";
	}
	
	// 取运营的国家，并且回显广告已投放的国家
	private List<Object[]> getCountryList(String countryCodes){
		List<Region> list = regionService.findAllList();
		List<Object[]> result = new ArrayList<Object[]>();
		Object[] tempArr = null;
		for (Region region : list) {
			if(region.getCountryCodes().indexOf(",") != -1){
				String[] tempCode = region.getCountryCodes().split(",");
				String[] tempName = region.getCountryNames().split(",");
				for (int i = 0; i < tempName.length; i++) {
					tempArr = new Object[3];
					tempArr[0] = tempCode[i];
					tempArr[1] = tempName[i];
					if (include(countryCodes, tempArr[0].toString()))
						tempArr[2] = "1";
					else 
						tempArr[2] = "0";
					result.add(tempArr);
				}
			} else {
				tempArr = new Object[3];
				tempArr[0] = region.getCountryCodes();
				tempArr[1] = region.getCountryNames();
				if (include(countryCodes, tempArr[0].toString()))
					tempArr[2] = "1";
				else 
					tempArr[2] = "0";
				result.add(tempArr);
			}
		}
		return result;
	}
	
	// 字符包含判断
	private boolean include(String strs, String str) {
		
		if (StringUtils.isBlank(strs))
			return false;
		
		if ("ALL".equals(strs))
			return false;
		
		String[] strArr = strs.split(",");
		for (String temp : strArr) {
			if (temp.equals(str)) {
				return true;
			}
		}
		
		return false;
	}

}
