/** 
 * @Package com.uu.modules.mifi.web 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月15日 上午10:22:38 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.web;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uu.common.config.Global;
import com.uu.common.persistence.Page;
import com.uu.common.utils.excel.ExportExcel;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.condition.CardMonitorCondition;
import com.uu.modules.mifi.entity.CardMonitor;
import com.uu.modules.mifi.service.CardMonitorService;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.sys.utils.UserUtils;

/** 
 * @Description 卡监控信息 对外服务包实现
 * @author yifang.huang
 * @date 2016年4月15日 上午10:22:38 
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/cardMonitor")
public class CardMonitorController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(CardMonitorController.class);
	
	@Autowired
	private CardMonitorService cardMonitorService;
	
	@Autowired
	private ChannelService channelService;
	
	@ModelAttribute
	public CardMonitor get(@RequestParam(required=false) String id) {
		
		if (StringUtils.isNotBlank(id))
			return cardMonitorService.get(id);
		
		return new CardMonitor();
		
	}
	
	@RequiresPermissions("mifi:cardMonitor:view")
	@RequestMapping(value = {"list", ""})
	public String list(CardMonitorCondition condtion, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CardMonitor> page = cardMonitorService.find(new Page<CardMonitor>(request, response), condtion); 
		model.addAttribute("page", page);
		List<String[]> mccList = channelService.findAllMCC();
		model.addAttribute("mccList", mccList);
		model.addAttribute("condtion", condtion);
		return "modules/mifi/cardMonitorList";
		
	}
	
	/**
	 * 监控信息导出
	 * @Description 
	 * @param condition
	 * @param request
	 * @param response 
	 * @return void  
	 * @author yuxiaoyu
	 * @date 2016年12月13日 下午2:55:41
	 */
	@RequiresPermissions("mifi:cardMonitor:view")
	@RequestMapping(value = "export")
	public void export(CardMonitorCondition condition, HttpServletRequest request, HttpServletResponse response) {
		try {
			ExportExcel exportExcel = cardMonitorService.excelExport(condition);
			exportExcel.write(response, "监控信息列表.xlsx").dispose();
		} catch (Exception e) {
			logger.error(StringUtils.EMPTY, e);
		}
	}
	
	/**
	 * 更新监控信息状态
	 * @Description 
	 * @param remarks
	 * @param id
	 * @return 
	 * @return boolean  
	 * @author yuxiaoyu
	 * @date 2016年12月13日 下午5:30:28
	 */
	@RequestMapping(value = "handled")
	public @ResponseBody
	boolean handle(String remarks, String id) {
		//查询目标实体信息
		CardMonitor cardMonitor = cardMonitorService.get(id);
		String remarksInDB = cardMonitor.getRemarks();
		if (StringUtils.isNotBlank(remarksInDB)) {
			cardMonitor.setRemarks(remarksInDB + "," + remarks);
		} else {
			cardMonitor.setRemarks(remarks);
		}
		cardMonitor.setHandleBy(UserUtils.getUser());
		cardMonitor.setHandleDate(new Date());
		cardMonitor.setStatus("HANDLED");
		cardMonitorService.save(cardMonitor);
		return true;
	}
	
	/**
	 * 已处理
	 * @Description 
	 * @return String  
	 * @author wangsai
	 * @date 2016年12月9日 下午3:28:54
	 */
	@RequiresPermissions("mifi:cardMonitor:edit")
	@RequestMapping(value = "form")
	public String handled(String id, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/mifi/cardMonitor/?repage";
		}
		
		if (StringUtils.isBlank(id)) {
			addMessage(redirectAttributes, "请选已处理信息");
			return "redirect:"+Global.getAdminPath()+"/mifi/cardMonitor/?repage";
		}
		CardMonitor bean = cardMonitorService.get(id);
		if (bean == null) {
			addMessage(redirectAttributes, "请选已处理信息");
			return "redirect:"+Global.getAdminPath()+"/mifi/cardMonitor/?repage";
		}
//		if ("HANDLED".equals(bean.getStatus())) {
//			addMessage(redirectAttributes, "不能重复处理");
//			return "redirect:"+Global.getAdminPath()+"/mifi/cardMonitor/?repage";
//		}
		return "modules/mifi/cardMonitorForm";
	}
	/**
	 * 
	 * @Description 获取国家信息
	 * @param id
	 * @param request
	 * @param model
	 * @param redirectAttributes
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年4月15日 上午10:33:35
	 */
	@RequiresPermissions("mifi:cardMonitor:edit")
	@RequestMapping(value = "getCountry")
	public String getCountry(String id, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/mifi/cardMonitor/?repage";
		}
		
		if (StringUtils.isBlank(id)) {
			addMessage(redirectAttributes, "请选已处理信息");
			return "redirect:"+Global.getAdminPath()+"/mifi/cardMonitor/?repage";
		}
		CardMonitor bean = cardMonitorService.get(id);
		if (bean == null) {
			addMessage(redirectAttributes, "请选已处理信息");
			return "redirect:"+Global.getAdminPath()+"/mifi/cardMonitor/?repage";
		}
		
		// 获取国家信息
		HashMap<String, String> map = cardMonitorService.findCountryByMcc(bean.getMcc());
		if (map == null) {
			addMessage(redirectAttributes, "获取国家信息失败");
			return "redirect:"+Global.getAdminPath()+"/mifi/cardMonitor/?repage";
		}
		// 保存数据
		bean.setCountryCode(map.get("countryCode"));
		bean.setCountryName(map.get("countryName"));
		cardMonitorService.save(bean);
		
		addMessage(redirectAttributes, "修改成功");
		return "redirect:"+Global.getAdminPath()+"/mifi/cardMonitor/?repage";
		
	}
	
	@RequiresPermissions("mifi:cardMonitor:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/mifi/cardMonitor/?repage";
		}
		
		cardMonitorService.delete(id);
		
		addMessage(redirectAttributes, "删除卡监控信息成功");
		return "redirect:"+Global.getAdminPath()+"/mifi/cardMonitor/?repage";
	}
	
}
