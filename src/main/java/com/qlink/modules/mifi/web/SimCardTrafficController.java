package main.java.com.qlink.modules.mifi.web;

import java.util.Date;
import java.util.HashMap;
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

@Controller
@RequestMapping(value = "${adminPath}/mifi/simCardTraffic")
public class SimCardTrafficController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(SimCardTrafficController.class);

	@Autowired
	MifiTrafficService mifiTrafficService;

	@RequestMapping(value = { "list", "" })
	public String list(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		if (paramMap.containsKey("initTag")) {// 无奈啊,否则默认执行此方法
			Page<HashMap> reqPage = new Page<HashMap>(request, response);
			Page<HashMap> page = mifiTrafficService.findSimCardTrafficList(reqPage, paramMap);
			model.addAttribute("page", page);
		} else {
			paramMap.put("beginDate", DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			paramMap.put("endDate", DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
		}
		model.addAllAttributes(paramMap);
		return "modules/mifi/simCardTrafficList";
	}
}
