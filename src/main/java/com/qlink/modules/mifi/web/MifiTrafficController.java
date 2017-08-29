package main.java.com.qlink.modules.mifi.web;

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
import com.uu.common.utils.DateUtils;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.service.MifiTrafficService;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.utils.UserUtils;

@Controller
@RequestMapping(value = "${adminPath}/mifi/mifiTraffic")
public class MifiTrafficController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(MifiTrafficController.class);

	@Autowired
	MifiTrafficService mifiTrafficService;

	@RequestMapping(value = { "list", "" })
	public String list(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		
		// 是否运营商
		User user = UserUtils.getUser();
		boolean isChannelAdmin = user.getChannelAdmin();
		
		if (paramMap.containsKey("initTag")) {// 无奈啊,否则默认执行此方法
			
			if (isChannelAdmin)
				paramMap.put("sourceType", user.getChannelNameEn());	// 只能查询运营商自己的数据
			
			Page<Object[]> page = mifiTrafficService.findMifiTrafficList(new Page<Object[]>(request, response), paramMap);
			model.addAttribute("page", page);
			
		} else {
			paramMap.put("beginDate", DateUtils.getPreDate());
			paramMap.put("endDate", DateUtils.getPreDate());
		}
		model.addAllAttributes(paramMap);
		
		if (isChannelAdmin)
			return "channel/mifi/mifiTrafficList";
		return "modules/mifi/mifiTrafficList";

	}

	@RequestMapping(value = "listForToday")
	public String listForToday(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		
		// 是否运营商
		User user = UserUtils.getUser();
		boolean isChannelAdmin = user.getChannelAdmin();
		
		if (paramMap.containsKey("initTag")) {// 无奈啊,否则默认执行此方法
			
			if (isChannelAdmin)
				paramMap.put("sourceType", user.getChannelNameEn());	// 只能查询运营商自己的数据
			
			Page<Object[]> page = mifiTrafficService.findTodayFlow(new Page<Object[]>(request, response), paramMap);
			model.addAttribute("page", page);
			
		} else {
			paramMap.put("beginDate", DateUtils.getPreDate());
			paramMap.put("endDate", DateUtils.getPreDate());
		}
		model.addAllAttributes(paramMap);
		
		if (isChannelAdmin)
			return "channel/mifi/mifiTrafficList";
		return "modules/mifi/mifiTrafficList";

	}
}
