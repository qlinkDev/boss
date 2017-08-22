package com.uu.modules.mifi.web;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uu.common.config.Global;
import com.uu.common.persistence.Page;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.excel.ExportExcel;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.service.MifiTrafficService;
import com.uu.modules.sys.utils.DictUtils;

@Controller
@RequestMapping(value = "${adminPath}/mifi/simStatus")
public class SimStatusController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(SimStatusController.class);

	@Autowired
	MifiTrafficService mifiTrafficService;

	@RequestMapping(value = "init")
	public String init(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		paramMap.put("beginDate", DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
		paramMap.put("endDate", DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
		model.addAllAttributes(paramMap);
		return "modules/mifi/simStatusList";
	}

	@RequestMapping(value = "list")
	public String list(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<HashMap> page = mifiTrafficService.findSimStatusList(new Page<HashMap>(request, response), paramMap);
		model.addAttribute("page", page);
		model.addAllAttributes(paramMap);
		return "modules/mifi/simStatusList";
	}

	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "SIM卡状态数据" + paramMap.get("beginDate") + ".xlsx";
			Page<Object[]> _page = new Page<Object[]>();
			Page<Object[]> page = mifiTrafficService.exportSimStatusList(_page, paramMap);
			ExportExcel ee = new ExportExcel("SIM卡状态数据", new String[] { "ID", "卡号", "卡状态", "时间", "所在地区MCC值", "所在地区中文名",
					"所在地区英文名", "注册国家的区域码", "卡箱服务器连接状态", "总流量(M)", "已用流量(M)", "流量增值(M)" });
			List<Object[]> list = page.getList();
			for (int i = 0; i < list.size(); i++) {
				Row row = ee.addRow();
				Object[] objs = list.get(i);
				for (int j = 0; j < objs.length; j++) {
					Object obj;
					if (objs[j] instanceof byte[]) {
						obj = new String((byte[]) objs[0], "utf-8");
					} else if (objs[j] instanceof Byte) {
						obj = Byte.toString((Byte) objs[j]);
						if (j == 2) {
							obj = objs[j] + "|" + DictUtils.getDictLabel(Byte.toString((Byte) objs[j]), "usimstatus", "未知状态");
						}
					} else if (objs[j] instanceof BigInteger) {
						obj = ((BigInteger) objs[j]).longValue();
					} else if (objs[j] instanceof BigDecimal) {
						obj = ((BigDecimal) objs[j]).toString();
					} else {
						obj = objs[j];
					}
					ee.addCell(row, j, obj);
				}
			}
			ee.write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "导出SIM卡状态数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/simStatus/init";
	}
}
