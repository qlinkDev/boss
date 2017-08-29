package main.java.com.qlink.modules.mifi.web;

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
import com.uu.modules.mifi.entity.SimBankIp;
import com.uu.modules.mifi.service.SimBankIpService;
/**
 * SIMBankIp管理
 * @Description 
 * @author wangsai
 * @date 2016年11月3日 上午11:27:35
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/simbankip")
public class SimBankIpController extends BaseController {
	@Autowired
	private SimBankIpService simBankIpService;

	@ModelAttribute
	public SimBankIp get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return simBankIpService.get(id);
		} else {
			return new SimBankIp();
		}
	}

	/**
	 * 列表页
	 * 
	 * @Description
	 * @param SimBankIp
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @return String
	 * @author wangsai
	 * @date 2016年11月2日 上午10:19:55
	 */
	@RequestMapping(value = { "list", "" })
	public String list(SimBankIp simBankIp, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<SimBankIp> page = simBankIpService.find(new Page<SimBankIp>(request, response), simBankIp);
		model.addAttribute("page", page);
		return "modules/mifi/simBankIpList";
	}

	/**
	 * 修改
	 * 
	 * @Description
	 * @param SimBankIp
	 * @param model
	 * @return
	 * @return String
	 * @author wangsai
	 * @date 2016年11月2日 上午10:20:12
	 */
	@RequestMapping(value = "form")
	public String form(SimBankIp simbankip, Model model) {
		model.addAttribute("simbankip", simbankip);
		return "modules/mifi/simBankIpForm";
	}

	// 保存
	@RequestMapping(value = "save")
	public String save(SimBankIp simBankIp, HttpServletRequest request, Model model,
			RedirectAttributes redirectAttributes) {
		simBankIpService.save(simBankIp);
		addMessage(redirectAttributes, "保存'" + simBankIp.getSimbankid() + "'成功");
		return "redirect:" + Global.getAdminPath() + "/mifi/simbankip";
	}
	//判斷simbankid是否重複 
	@ResponseBody
	@RequestMapping("isRankExists")
	public String isRankExists(String simbankid, String id) {
		if (id == null) {//新添加判断
			boolean isRankExists = simBankIpService.isRankExists(simbankid);

			if (isRankExists) {
				return "Simbankid已经存在不允许添加 请修改";
			}
			return "true";
		} else {
			//修改判断是否重复SimBankIpmcc
			boolean isRankExists = simBankIpService.isRankExistss(simbankid, id);
			if (isRankExists) {
				return "true";
			} else {
				boolean isRankExistss = simBankIpService.isRankExists(simbankid);
				if (isRankExistss) {
					return "Simbankid已经存在不允许添加 请修改";
				}
				return "true";
			}
		}
	}
}
