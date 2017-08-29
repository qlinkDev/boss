package main.java.com.qlink.modules.user.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uu.common.persistence.Page;
import com.uu.common.utils.StringUtils;
import com.uu.common.web.BaseController;
import com.uu.modules.user.entity.UserBasicInfo;
import com.uu.modules.user.service.UserBasicInfoService;
@Controller
@RequestMapping(value = "${adminPath}/user/userBasic")
public class UserBasicInfoContronller extends BaseController {
	
	@Autowired
	 private	UserBasicInfoService userBasicInfoService;

	@ModelAttribute("UserBasicInfo")
	public UserBasicInfo get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return userBasicInfoService.getuserBasicInfo(id);
		} else {
			return new UserBasicInfo();
		}
	}
	@RequestMapping(value = "")
	public String init(UserBasicInfo userBasicInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/user/userBasicInfoList";
	}
	/**
	 * 列表页
	 * 
	 * @Description
	 * @author wangsai
	 */
	@RequestMapping(value = "list")
	public String list(UserBasicInfo userBasicInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<UserBasicInfo> page = userBasicInfoService.find(new Page<UserBasicInfo>(request, response), userBasicInfo);
		model.addAttribute("page", page);
		return "modules/user/userBasicInfoList";
	}
	
	
}
