package main.java.com.qlink.modules.user.web;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uu.common.persistence.Page;
import com.uu.common.web.BaseController;
import com.uu.modules.user.entity.UserMifiLink;
import com.uu.modules.user.service.UserMifiLinkService;

@Controller
@RequestMapping(value = "${adminPath}/user/userMifiLink")
public class UserMifiLinkController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(UserMifiLinkController.class);

	@Autowired
	UserMifiLinkService userMifiLinkService;
	
	@RequestMapping(value = "")
	public String init(@RequestParam Map<String, Object> paramMap,UserMifiLink userMifiLink, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		return "modules/user/userMifiLinkList";

	}
	@RequestMapping(value = "list")
	public String list(@RequestParam Map<String, Object> paramMap,UserMifiLink userMifiLink, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<HashMap> page  = userMifiLinkService.findLinkAndUser(new Page<HashMap>(request, response), userMifiLink, paramMap);
		model.addAttribute("page", page);
		model.addAllAttributes(paramMap);
		return "modules/user/userMifiLinkList";

	}
}
