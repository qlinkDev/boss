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
import com.uu.modules.mifi.entity.Speedrestrit;
import com.uu.modules.mifi.service.SpeedrestritService;

/**
 * 限速管理
 * 
 * @Description
 * @author wangsai
 * @date 2016年11月2日 上午9:44:41
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/speedrestrit")
public class SpeedrestritController extends BaseController {
	@Autowired
	private SpeedrestritService speedrestritService;

	@ModelAttribute
	public Speedrestrit get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return speedrestritService.get(id);
		} else {
			return new Speedrestrit();
		}
	}

	/**
	 * 列表页
	 * 
	 * @Description
	 * @param speedrestrit
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @return String
	 * @author wangsai
	 * @date 2016年11月2日 上午10:19:55
	 */
	@RequestMapping(value = "")
	public String init(HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/mifi/speedrestritList";
	}
	
	/**
	 * 列表页
	 * 
	 * @Description
	 * @param speedrestrit
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @return String
	 * @author wangsai
	 * @date 2016年11月2日 上午10:19:55
	 */
	@RequestMapping(value = "list")
	public String list(Speedrestrit speedrestrit, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Speedrestrit> page = speedrestritService.find(new Page<Speedrestrit>(request, response), speedrestrit);
		model.addAttribute("page", page);
		return "modules/mifi/speedrestritList";
	}

	/**
	 * 修改
	 * 
	 * @Description
	 * @param speedrestrit
	 * @param model
	 * @return
	 * @return String
	 * @author wangsai
	 * @date 2016年11月2日 上午10:20:12
	 */
	@RequestMapping(value = "form")
	public String form(Speedrestrit speedrestrit, Model model) {
		model.addAttribute("speedrestrit", speedrestrit);
		return "modules/mifi/speedrestritForm";
	}

	// 保存
	@RequestMapping(value = "save")
	public String save(Speedrestrit speedrestrit, HttpServletRequest request, Model model,
			RedirectAttributes redirectAttributes) {
		speedrestritService.save(speedrestrit);
		addMessage(redirectAttributes, "保存'" + speedrestrit.getSpeedrestritmcc() + "'成功");
		return "redirect:" + Global.getAdminPath() + "/mifi/speedrestrit";
	}

	@ResponseBody
	@RequestMapping("isRankExists")
	public String isRankExists(String speedrestritmcc, String id) {
		if (id == null) {//新添加判断
			boolean isRankExists = speedrestritService.isRankExists(speedrestritmcc);

			if (isRankExists) {
				return "speedrestritmcc已经存在不允许添加 请修改";
			}
			return "true";
		} else {
			//修改判断是否重复speedrestritmcc
			boolean isRankExists = speedrestritService.isRankExistss(speedrestritmcc, id);
			if (isRankExists) {
				return "true";
			} else {
				boolean isRankExistss = speedrestritService.isRankExists(speedrestritmcc);
				if (isRankExistss) {
					return "speedrestritmcc已经存在不允许添加 请修改";
				}
				return "true";
			}
		}
	}
}
