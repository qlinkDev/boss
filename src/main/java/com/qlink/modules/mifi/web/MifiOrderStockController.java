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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uu.common.config.Global;
import com.uu.common.persistence.Page;
import com.uu.common.utils.DateUtils;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.service.MifiOrderService;

import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "${adminPath}/mifi/mifiOrderStock")
public class MifiOrderStockController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(MifiOrderStockController.class);

	@Autowired
	MifiOrderService mifiOrderService;

	@RequestMapping(value = { "list", "" })
	public String list(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		if (paramMap.containsKey("initTag")) {// 无奈啊,否则默认执行此方法
			Page<HashMap> page = mifiOrderService.mifiOrderStockList(new Page<HashMap>(request, response), paramMap);
			model.addAttribute("page", page);
		} else {
			paramMap.put("startDate",
					DateUtils.formatDate(new Date(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000), "yyyy-MM-dd"));
		}
		model.addAllAttributes(paramMap);
		return "modules/mifi/mifiOrderStockList";
	}

	@RequestMapping(value = "stockSimCard")
	public String stockSimCard(@RequestParam Map<String, Object> paramMap, RedirectAttributes redirectAttributes) {
		mifiOrderService.updateStockSimCard(paramMap);
		addMessage(redirectAttributes, "批量确认[已备SIM卡]成功!");
		return "redirect:" + Global.getAdminPath() + "/mifi/mifiOrderStock/?repage&sourceType="
				+ paramMap.get("sourceType") + "&stockStatus=" + paramMap.get("stockStatus") + "&startDate="
				+ paramMap.get("startDate") + "&endDate=" + paramMap.get("endDate") + "&initTag";
	}
}
