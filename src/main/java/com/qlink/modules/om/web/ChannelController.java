/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package main.java.com.qlink.modules.om.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.uu.common.config.Global;
import com.uu.common.persistence.Page;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.StringUtils;
import com.uu.common.utils.excel.ExportExcel;
import com.uu.common.web.BaseController;
import com.uu.modules.om.condition.RegionCondition;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.entity.Price;
import com.uu.modules.om.entity.Region;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.om.service.PriceService;
import com.uu.modules.om.service.RegionService;
import com.uu.modules.sys.service.LogService;

/**
 * 渠道Controller
 * 
 * @author yuxiaoyu
 * @version 2016-3-18
 */
@Controller
@RequestMapping(value = "${adminPath}/om/channel")
public class ChannelController extends BaseController {

	@Autowired
	private ChannelService channelService;
	@Autowired
	private PriceService priceService;
	@Autowired
	private RegionService regionService;
	@Autowired
	private LogService logService;

	@ModelAttribute
	public Channel get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return channelService.getChannel(id);
		} else {
			return new Channel();
		}
	}

	/**
	 * 列表页
	 * 
	 * @param channel
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("om:channel:view")
	@RequestMapping({ "list", "" })
	public String list(Channel channel, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Channel> page = channelService.findChannel(new Page<Channel>(request, response), channel);
		model.addAttribute("page", page);
		return "modules/om/channelList";
	}

	/**
	 * 修改页
	 * 
	 * @param channel
	 * @param model
	 * @return
	 */
	@RequiresPermissions("om:channel:view")
	@RequestMapping("form")
	public String form(Channel channel, Model model) {
		List<String[]> priceList = priceService.findListByChannelID(channel.getId(), channel.getChannelNameEn());// 获取区域价格
		if (null == priceList || 0 == priceList.size()) {// 找不到默认渠道的区域价格配置，则获取区域,初始化默认渠道
			RegionCondition regionCondition = new RegionCondition();
			List<Region> regionList = regionService.findListByCondition(regionCondition);
			model.addAttribute("regionList", regionList);
		}
		List<String[]> mccList = channelService.findAllMCC();
		
		model.addAttribute("mccList", mccList);
		model.addAttribute("priceList", priceList);
		model.addAttribute("channel", channel);
		return "modules/om/channelForm";
	}

	/**
	 * 保存
	 * 
	 * @param channel
	 * @param request
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("om:channel:edit")
	@RequestMapping("save")
	@Transactional(readOnly = false)
	public String save(Channel channel, String[] priceInfos, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		boolean flag = false;
		if(StringUtils.isNotBlank(channel.getId())){
			flag = true;
		}
		// 保存渠道信息并生成渠道ID
		channelService.saveChannel(channel);

		//如果用户编辑了价格，则保存价格
		if (null != priceInfos) {
			String[] priceArr;
			Price price;
			List<Price> priceList = Lists.newArrayList();
			for (String priceInfo : priceInfos) {
				priceArr = priceInfo.split("\\|");//价格id|区域id|价格|是否新增
				price = new Price();
				price.setChannel(channel);
				price.setRegion(new Region(priceArr[1]));
				price.setPrice(Double.valueOf(priceArr[2]));
				if(!"true".equals(priceArr[3])){
					price.setId(priceArr[0]);
				}
				priceList.add(price);
			}
			// 保存区域价格信息
			priceService.saveByRegionAndChannel(channel.getId(), priceList);
		}
		
		if(flag){
			logService.saveOperateLog(request, "[运营管理 》渠道管理]-渠道修改，修改的ID为：[{}]", channel.getId());
		} else {
			logService.saveOperateLog(request, "[运营管理 》渠道管理]-渠道添加，渠道ID为：[{}]", channel.getId());
		}
		addMessage(redirectAttributes, "保存渠道'" + channel.getChannelName() + "'成功");
		return "redirect:" + Global.getAdminPath() + "/om/channel?repage";
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("om:channel:edit")
	@RequestMapping("delete")
	@Transactional(readOnly = false)
	public String delete(String id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		channelService.deleteChannel(id);//删除渠道
		priceService.deleteByChannelID(id);//删除对应价格
		logService.saveOperateLog(request, "[运营管理 》渠道管理]-渠道删除，删除的ID为：[{}]", id);
		addMessage(redirectAttributes, "删除渠道成功");
		return "redirect:" + Global.getAdminPath() + "/om/channel?repage";
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
	@RequiresPermissions("om:channel:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(Channel channel, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "渠道数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			Page<Channel> page = channelService.findChannel(new Page<Channel>(request, response, -1), channel);
			new ExportExcel("渠道数据", Channel.class).setDataList(page.getList()).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出渠道失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/om/channel?repage";
	}

	@ResponseBody
	@RequiresPermissions("om:channel:view")
	@RequestMapping("isChannelNameUnique")
	public String isChannelNameUnique(String channelId, String channelName) {
		if (StringUtils.isNotEmpty(channelId)) {
			return "true";
		}
		if (StringUtils.isEmpty(channelName)) {
			return "true";
		}
		List<Channel> channelList = channelService.findChannelByName(channelName);
		if (null == channelList || 0 == channelList.size()) {
			return "true";
		}
		return "false";
	}

	@ResponseBody
	@RequiresPermissions("om:channel:view")
	@RequestMapping("isChannelNameEnUnique")
	public String isChannelNameEnUnique(String channelId, String channelNameEn) {
		if (StringUtils.isNotEmpty(channelId)) {
			return "true";
		}
		if (StringUtils.isEmpty(channelNameEn)) {
			return "true";
		}
		List<Channel> channelList = channelService.findChannelByNameEn(channelNameEn);
		if (null == channelList || 0 == channelList.size()) {
			return "true";
		}
		return "false";
	}
}
