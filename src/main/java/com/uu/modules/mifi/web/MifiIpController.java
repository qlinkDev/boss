package com.uu.modules.mifi.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import com.uu.modules.mifi.entity.MifiIp;
import com.uu.modules.mifi.service.MifiIpService;
/**
 * 多服务器配置管理
 * @Description 
 * @author wangsai
 * @date 2016年11月3日 下午2:56:36
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/mifiip")
public class MifiIpController  extends BaseController{
	@Autowired
	private  MifiIpService mifiIpService;


	@ModelAttribute
	public MifiIp get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return mifiIpService.get(id);
		} else {
			return new MifiIp();
		}
	}

	/**
	 * 列表页
	 * 
	 * @Description
	 * @param mifiIp
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @return String
	 * @author wangsai
	 * @date 2016年11月2日 上午10:19:55
	 */
	@RequestMapping(value = { "list", "" })
	public String list(MifiIp mifiIp, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<MifiIp> page = mifiIpService.find(new Page<MifiIp>(request, response), mifiIp);
		model.addAttribute("page", page);
		return "modules/mifi/mifiIpList";
	}

	/**
	 * 修改
	 * 
	 * @Description
	 * @param mifiIp
	 * @param model
	 * @return
	 * @return String
	 * @author wangsai
	 * @date 2016年11月2日 上午10:20:12
	 */
	@RequestMapping(value = "form")
	public String form(MifiIp mifiIp, Model model) {
		model.addAttribute("mifiip", mifiIp);
		return "modules/mifi/mifiIpForm";
	}

	// 保存
	@RequestMapping(value = "save")
	public String save(MifiIp mifiIp, HttpServletRequest request, Model model,
			RedirectAttributes redirectAttributes) {
		mifiIpService.save(mifiIp);
		addMessage(redirectAttributes, "保存'" + mifiIp.getMcc() + "'成功");
		return "redirect:" + Global.getAdminPath() + "/mifi/mifiip";
	}

	@ResponseBody
	@RequestMapping("isRankExists")
	public String isRankExists(String mcc, String id) {
		if (id == null) {//新添加判断
			boolean isRankExists = mifiIpService.isRankExists(mcc);

			if (isRankExists) {
				return "mcc已经存在不允许添加 请修改";
			}
			return "true";
		} else {
			//修改判断是否重复mifiIpmcc
			boolean isRankExists = mifiIpService.isRankExistss(mcc, id);
			if (isRankExists) {
				return "true";
			} else {
				boolean isRankExistss = mifiIpService.isRankExists(mcc);
				if (isRankExistss) {
					return "mcc已经存在不允许添加 请修改";
				}
				return "true";
			}
		}
	}
}
