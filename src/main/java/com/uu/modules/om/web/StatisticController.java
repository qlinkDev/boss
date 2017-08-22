package com.uu.modules.om.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uu.common.config.Global;
import com.uu.common.persistence.Page;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.excel.ExportExcel;
import com.uu.common.web.BaseController;
import com.uu.modules.om.condition.ConsumeRecordCondition;
import com.uu.modules.om.entity.ConsumeRecord;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.om.service.ConsumeRecordService;
import com.uu.modules.om.service.StatisticService;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.utils.UserUtils;

/**
 * 订单统计
 * 
 * @Description
 * @author yuxiaoyu
 * @date 2016年3月31日 上午11:24:34
 */
@Controller
@RequestMapping(value = "${adminPath}/om/statistic")
public class StatisticController extends BaseController {

	@Autowired
	private ChannelService channelService;
	
	@Autowired
	private StatisticService statisticService;

	@Autowired
	private ConsumeRecordService consumeRecordService;

	@ModelAttribute
	public ConsumeRecord get(@RequestParam(required = false) String id) {

		if (StringUtils.isNotBlank(id))
			return consumeRecordService.get(id);

		return new ConsumeRecord();

	}
	
	/**
	 * 列表页
	 * @Description 
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * @return String  
	 * @author yuxiaoyu
	 * @date 2016年4月11日 下午1:49:08
	 */
	@RequiresPermissions("om:statistic:view")
	@RequestMapping(value ="")
	public String listPage(ConsumeRecordCondition condition, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<String[]> mccList = channelService.findAllMCC();
		model.addAttribute("mccList", mccList);
		model.addAttribute("sumMoney", String.valueOf(new Double(0)));
		model.addAttribute("page", new Page<ConsumeRecord>(request, response));
		
		User user = UserUtils.getUser();
		if (user.getChannelAdmin())
			return "channel/om/statisticList";
		
		return "modules/om/statisticList";
	}

	/**
	 * 查询
	 * 
	 * @Description
	 * @param condition
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @return String
	 * @author yuxiaoyu
	 * @date 2016年4月1日 上午11:47:59
	 */
	@RequiresPermissions("om:statistic:view")
	@RequestMapping(value ="list")
	public String list(ConsumeRecordCondition condition, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<ConsumeRecord> page = consumeRecordService.findPage(new Page<ConsumeRecord>(request, response), condition);
		String sumMoney = statisticService.sumMoney(condition);
		List<String[]> mccList = channelService.findAllMCC();
		
		model.addAttribute("mccList", mccList);
		model.addAttribute("sumMoney", sumMoney);
		model.addAttribute("page", page);
		
		User user = UserUtils.getUser();
		if (user.getChannelAdmin())
			return "channel/om/statisticList";

		return "modules/om/statisticList";
	}

	/**
	 * 导出
	 * 
	 * @param channel
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("om:statistic:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(ConsumeRecordCondition condition, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		try {
			String fileName = "订单统计数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			Page<ConsumeRecord> page = consumeRecordService.findPage(new Page<ConsumeRecord>(request, response, -1), condition);
			String sumMoney = statisticService.sumMoney(condition);
			List<ConsumeRecord> dataList = page.getList();
			for(ConsumeRecord record: dataList){
				record.setCreateDateForExcel(record.getCreateDate());
			}
			//此处导出为特例，再开发请参考用户导出UserController.java
			ExportExcel exportExcel = new ExportExcel("订单统计数据", ConsumeRecord.class, String.valueOf(dataList.size()), sumMoney);
			exportExcel.setDataList(dataList);
			exportExcel.write(response, fileName);
			exportExcel.dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出渠道失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/om/statistic?repage";
	}

	@RequiresPermissions("om:statistic:edit")
	@RequestMapping(value = "balance")
	public String balance(ConsumeRecordCondition condition, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		if(!"1".equals(condition.getEqBalanceStatus())){//仅当查询条件为已结算时不需要处理
			statisticService.updateBalanceByConditions(condition);
		}
		addMessage(redirectAttributes, "结算状态更新成功");
		return "redirect:" + Global.getAdminPath() + "/om/statistic?repage";

	}
}
