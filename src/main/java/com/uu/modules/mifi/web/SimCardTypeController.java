package com.uu.modules.mifi.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.entity.SimCardType;
import com.uu.modules.mifi.service.SimCardTypeService;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.om.utils.ChannelUtils;

/**
 * 
 * @author wangyong
 * @date 2016年3月30日
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/simCardType")
public class SimCardTypeController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(SimCardTypeController.class);

	@Autowired
	private SimCardTypeService simCardTypeService;

	@Autowired
	private ChannelService channelService;

	@ModelAttribute
	public SimCardType get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return simCardTypeService.get(id);
		} else {
			return new SimCardType();
		}
	}

	@RequestMapping(value = "init")
	public String init(SimCardType simCardType, HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/mifi/simCardTypeList";
	}

	@RequestMapping(value = { "list", "" })
	public String list(SimCardType simCardType, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SimCardType> page = simCardTypeService.find(new Page<SimCardType>(request, response), simCardType);
		model.addAttribute("page", page);
		return "modules/mifi/simCardTypeList";
	}

	@RequestMapping(value = "form")
	public String form(SimCardType simCardType, Model model) {
		model.addAttribute("simCardType", simCardType);
		List<String[]> countryList = channelService.findAllMCC();
		String mccs = simCardType.getMcc();
		String areaType = simCardType.getAreaType();

		List<Object[]> _countryList = new ArrayList<Object[]>();
		for (int i = 0; i < countryList.size(); i++) {
			Object[] obj = countryList.get(i);
			String countryMccs = (String) obj[0];
			Object[] _country = new Object[4];
			System.arraycopy(obj, 0, _country, 0, 3);
			_country[3] = "0";
			if (!SimCardType.areaType_0.equals(areaType)) {
				if (checkSplitStrInSplitStr(countryMccs, mccs)) {
					_country[3] = "1";
				}
			}
			_countryList.add(_country);
		}

		Object[] _country = new Object[4];
		_country[0] = "-1";
		_country[1] = "全球";
		_country[2] = "_earth";
		if (SimCardType.areaType_0.equals(areaType)) {
			_country[3] = "1";
		}
		_countryList.add(_country);
		model.addAttribute("countryList", _countryList);
		
		// 渠道列表
		List<Channel> channelList = ChannelUtils.getChannelList();
		// 允许使用该价格的渠道
		String containChannel = simCardType.getAllowedSource();
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
		
		return "modules/mifi/simCardTypeForm";
	}

	private boolean checkSplitStrInSplitStr(String src, String dest) {
		boolean result = false;
		if (StringUtils.isBlank(dest)) {
			result = false;
		} else {
			String[] countryMccs = src.split(",");
			String[] mccs = dest.split(",");
			for (int i = 0; i < countryMccs.length; i++) {
				for (int j = 0; j < mccs.length; j++) {
					if (countryMccs[i].equals(mccs[j])) {
						result = true;
						break;
					}
				}
			}
		}
		return result;
	}

	@RequestMapping(value = "save")
	public String save(SimCardType simCardType, String oldSourceType, String oldAllowedSource, HttpServletRequest request, Model model,
			RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, simCardType)) {
			return form(simCardType, model);
		}
		simCardTypeService.save(simCardType, oldSourceType, oldAllowedSource);
		addMessage(redirectAttributes, "保存卡类型'" + simCardType.getCardTypeName() + "'成功");
		return "redirect:" + Global.getAdminPath() + "/mifi/simCardType/init";
	}

	@ResponseBody
	@RequestMapping("checkCardType")
	public String checkCardType(String oldCardType, String cardType) {
		String result = "false";
		if (cardType != null && cardType.equals(oldCardType)) {
			result = "true";
		} else if (cardType != null && simCardTypeService.getCardInfoByCardType(cardType) == null) {
			result = "true";
		}
		return result;
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
