/** 
 * @Package com.uu.modules.om.web 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月22日 上午10:21:22 
 * @version V1.0 
 */ 
package com.uu.modules.om.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
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
import com.uu.modules.om.condition.PriceCondition;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.entity.Price;
import com.uu.modules.om.entity.Region;
import com.uu.modules.om.service.PriceService;
import com.uu.modules.om.utils.ChannelUtils;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.service.LogService;
import com.uu.modules.sys.utils.UserUtils;

/** 
 * @Description 价格 对外服务包实现
 * @author yifang.huang
 * @date 2016年3月22日 上午10:21:22 
 */
@Controller
@RequestMapping(value = "${adminPath}/om/price")
public class PriceController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(PriceController.class);
	
	@Autowired
	private PriceService priceService;
	
	@Autowired
	private LogService logService;
	
	@ModelAttribute
	public Price get(@RequestParam(required=false) String id) {
		
		if (StringUtils.isNotBlank(id))
			return priceService.get(id);
		
		return new Price();
		
	}
	
	@RequiresPermissions("om:price:view")
	@RequestMapping(value = "form")
	public String form(Price bean, Model model) {
		
		model.addAttribute("region", bean);
		
		// 渠道列表
		List<Channel> channelList = ChannelUtils.getChannelList();
		
		// 允许使用该价格的渠道
		String containChannel = bean.getContainChannel();
		
		List<Object[]> result = new ArrayList<Object[]>();
		Object[] tempArr = null;
		for (Channel channel : channelList) {
			
			tempArr = new Object[3];
			tempArr[0] = channel.getChannelNameEn();
			tempArr[1] = channel.getChannelName();
		
			// 回显已选择的渠道
			if (include(containChannel, channel.getChannelNameEn()))
				tempArr[2] = "1";
			else 
				tempArr[2] = "0";
			
			result.add(tempArr);
			tempArr = null;
		}
		
		model.addAttribute("channelList", result);
		
		return "modules/om/priceForm";
		
	}

	@RequiresPermissions("om:price:edit")
	@RequestMapping(value = "save")//@Valid 
	public String save(Price bean, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/om/region/?repage";
		}
		boolean flag = false;
		if(StringUtils.isNotBlank(bean.getId())){
			flag = true;
		}
		// 渠道不能为空
		if (bean.getChannel() == null) {
			addMessage(model, "请选择渠道");
			return form(bean, model);
		}
		
		// 同一渠道同一区域只能有一条记录
		PriceCondition condition = new PriceCondition();
		condition.setNeId(bean.getId());
		condition.setEqChannelId(bean.getChannel().getId());
		condition.setEqRegionId(bean.getRegion().getId());
		List<Price> list = priceService.findListByCondition(condition);
		if (list!=null && list.size()>0) {
			addMessage(model, "所选渠道和区域的价格已存在");
			return form(bean, model);
		}
		
		// 配置参数验证
		if (!beanValidator(model, bean)){
			return form(bean, model);
		}
		
		// 保存数据
		priceService.save(bean);
		if(flag){
			logService.saveOperateLog(request, "[运营管理 》价格管理]-价格修改，修改的ID为：[{}]", bean.getId());
		} else {
			logService.saveOperateLog(request, "[运营管理 》价格管理]-价格添加，添加的ID为：[{}]", bean.getId());
		}
		addMessage(redirectAttributes, "价格保存成功");
		return "redirect:"+Global.getAdminPath()+"/om/price/?repage";
		
	}
	
	/**
	 * 
	 * @Description 查看渠道产品列表
	 * @param condition
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2017年4月12日 上午11:51:16
	 */
	@RequiresPermissions("om:price:view")
	@RequestMapping(value = "goods")
	public String goods(@RequestParam Map<String, Object> paramMap, 
			HttpServletRequest request, HttpServletResponse response, Model model) {
		
		// 渠道编号
		String channelNameEn = ObjectUtils.toString(paramMap.get("channelNameEn"));
		
		if (StringUtils.isNotBlank(channelNameEn)) {
			// 渠道对应价格列表[根据价格取区域拼装成国家列表]
			List<Price> priceList = priceService.findChannelPriceList(channelNameEn);
			if (priceList!=null && priceList.size()>0) {
				List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
				Map<String, String> map = null;
				Map<String, String> mccesMap = null;
				for (Price price : priceList) {
					Region region = price.getRegion();
					String[] countryCodeArr = region.getCountryCodes().split(",");
					String[] countryNameArr = region.getCountryNames().split(",");
					for (int i=0; i<countryCodeArr.length; i++) {
						map = new HashMap<String, String>();
						map.put("countryCode", countryCodeArr[i]);
						map.put("countryName", countryNameArr[i]);
						mccesMap = priceService.findMccByCountryCode(countryCodeArr[i]);
						if (mccesMap != null)
							map.put("mcces", mccesMap.get("mcces"));
						map.put("price", price.getPrice() + "");
						listMap.add(map);
						mccesMap = null;
						map = null;
					}
				}
				model.addAttribute("listMap", listMap);
			}

			model.addAttribute("channelNameEn", channelNameEn);
		}
		
		return "modules/om/goodsList";
		
	}
	
	/**
	 * 
	 * @Description 删除
	 * @param id
	 * @param redirectAttributes
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年3月22日 上午10:31:40
	 */
	@RequiresPermissions("om:price:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/om/price/?repage";
		}
		priceService.delete(id);
		logService.saveOperateLog(request, "[运营管理 》价格管理]-价格删除，删除的ID为：[{}]", id);
		addMessage(redirectAttributes, "删除价格成功");
		return "redirect:"+Global.getAdminPath()+"/om/price/?repage";
	}
	
	/**
	 * 
	 * @Description 渠道对应区域价格列表
	 * @param condtion
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年3月22日 下午1:58:58
	 */
	@RequiresPermissions("om:price:view")
	@RequestMapping(value = {"list", ""})
	public String list(PriceCondition condition, HttpServletRequest request, HttpServletResponse response, Model model) {

		// 是否运营商
		User user = UserUtils.getUser();
		if (user.getChannelAdmin()) {
			model.addAttribute("priceList", priceService.findChannelPriceList(user.getChannelNameEn()));
			return "channel/om/priceList";
		}
		
		Page<Price> page = priceService.find(new Page<Price>(request, response), condition); 
		model.addAttribute("page", page);
		return "modules/om/priceList";
		
	}
	
	// 判断渠道是否被选择
	private boolean include(String containChannel, String channelNameEn) {
		
		if (StringUtils.isBlank(containChannel))
			return false;
		
		if ("ALL".equals(containChannel))
			return false;
		
		String[] channelCodeArr = containChannel.split(",");
		for (String code : channelCodeArr) {
			if (code.equals(channelNameEn)) {
				return true;
			}
		}
		
		return false;
	}

}
