package main.java.com.qlink.modules.mifi.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.uu.common.utils.StringUtils;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.entity.MifiVersion;
import com.uu.modules.mifi.service.MifiVersionService;
import com.uu.modules.sys.service.LogService;

/**
 * mifi版本控制器
 * 
 * @author shuxin
 * @date 2016年8月1日
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/version")
public class MifiVersionController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(CardBasicInfoController.class);

	@Autowired
	private MifiVersionService versionService;
	
	@Autowired
	private LogService logService;

	@ModelAttribute
	public MifiVersion get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return versionService.get(id);
		} else {
			return new MifiVersion();
		}
	}

	/**
	 * 分页列表
	 * 
	 * @athor shuxin
	 * @date 2016年8月1日下午2:37:20
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param model
	 * @return String
	 */
	@RequestMapping(value = "")
	public String init(MifiVersion mifiversion, HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/mifi/mifiVersionList";
	}
	
	@RequestMapping(value = "list")
	public String list(MifiVersion mifiversion, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<MifiVersion> page = versionService.find(new Page<MifiVersion>(request, response), mifiversion);
		model.addAttribute("page", page);
		return "modules/mifi/mifiVersionList";
	}

	/**
	 * 修改页
	 * 
	 * @return
	 */
	@RequestMapping(value = "form")
	public String form(MifiVersion mifiversion, Model model) {
		model.addAttribute("mifiversion", mifiversion);
		return "modules/mifi/mifiVersionForm";
	}

	/**
	 * 保存
	 * 
	 * @return
	 */
	@RequestMapping(value = "save")
	public String save(MifiVersion mifiversion, HttpServletRequest request, 
			Model model, RedirectAttributes redirectAttributes) {
		String q = mifiversion.getUPDATEFLAG();
		String s = mifiversion.getSPEEDLIMITFLAG();
		String b = mifiversion.getLog_file_Type();
		String e = mifiversion.getLcd_version_Type();
		boolean isNum = q.matches("[0-9]+");
		boolean isNums = s.matches("[0-9]+");
		boolean is = b.matches("[0-9]+");
		boolean isN = e.matches("[0-9]+");
		if (isNum && isNums && is && isN) {
			versionService.save(mifiversion);
			logService.saveOperateLog(request, "[设备管理 》MIFI版本管理]-版本修改，修改对象的ID为：[{}]", mifiversion.getId());
			addMessage(redirectAttributes, "[" + mifiversion.getIMEI_6200() + "版本信息修成功");
			return "redirect:"+Global.getAdminPath()+"/mifi/version/?repage";
		} else {
			addMessage(model, "数据输入错误");
			return form(mifiversion, model);
		}
	}

	@RequestMapping(value = "updataspeedlimitflag")
	public @ResponseBody boolean updataspeedlimitflag(String speedlimitflag) {
		boolean isNum = speedlimitflag.matches("[0-9]+");

		if (isNum) {
			versionService.updatespeedlimitflag(speedlimitflag);
			return true;
		} else {
			return false;

		}

	}

	@RequestMapping(value = "updataupdateflag")
	public @ResponseBody boolean updatauploadlogflag(String updateflag) {
		boolean isNum = updateflag.matches("[0-9]+");
		if (isNum) {
			versionService.updateupdateflag(updateflag);
			return true;
		} else {
			return false;

		}
	}

	@RequestMapping(value = "forms")
	public String forms() {
		return "modules/mifi/mifiVersionUpdata";
	}

	@RequestMapping(value = "formss")
	public String formss() {
		return "modules/mifi/mifiVersionUpdatas";
	}
}
