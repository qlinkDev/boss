package com.uu.modules.mifi.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
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
import com.uu.modules.mifi.service.CardManageService;

/**
 * 
 * @author wangyong
 * @date 2016年3月30日
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/syncCardInfo")
public class SyncCardInfoController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(SyncCardInfoController.class);

	@Autowired
	private CardManageService cardManageService;

	@RequestMapping(value = "init")
	public String init(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		paramMap.put("endDate", DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
		model.addAllAttributes(paramMap);
		return "modules/mifi/syncCardInfoList";
	}

	@RequestMapping(value = { "list", "" })
	public String list(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<HashMap> page = cardManageService.findSyncCardInfoList(new Page<HashMap>(request, response), paramMap);
		model.addAttribute("page", page);
		model.addAllAttributes(paramMap);
		return "modules/mifi/syncCardInfoList";
	}

	@RequestMapping(value = "syncCardInfo")
	public String syncCardInfo(@RequestParam Map<String, Object> paramMap, RedirectAttributes redirectAttributes) {
		String sn = ObjectUtils.toString(paramMap.get("sn"));
		int i = cardManageService.saveSyncCardInfo(paramMap);
		if (i == 0) {
			addMessage(redirectAttributes, "SIM卡[" + sn + "]已存在,无需重复同步!");
		} else if (i == 1) {
			addMessage(redirectAttributes, "同步SIM卡[" + sn + "]成功!");
		}
		
		// 如果是第一次导入，修改card_basic_info的sn_hex值
		cardManageService.updateSnHex(sn);
		
		return "redirect:" + Global.getAdminPath() + "/mifi/syncCardInfo/init";
	}

	@RequestMapping(value = "batchSyncCardInfo")
	public String batchSyncCardInfo(@RequestParam Map<String, Object> paramMap, RedirectAttributes redirectAttributes) {
		List<String> iccIds = cardManageService.saveBatchSyncCardInfo(paramMap);
		addMessage(redirectAttributes, "批量同步SIM卡[" + iccIds.size() + "条]成功!");

		// 修改card_basic_info的sn_hex值
		cardManageService.updateSnHex();
		
		return "redirect:" + Global.getAdminPath() + "/mifi/syncCardInfo/init";
	}
}
