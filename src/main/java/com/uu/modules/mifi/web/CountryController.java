/** 
 * @Package com.uu.modules.mifi.web 
 * @Description 
 * @author yifang.huang
 * @date 2016年5月19日 下午4:09:54 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uu.common.persistence.Page;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.excel.ExportExcel;
import com.uu.common.web.BaseController;
import com.uu.modules.om.service.RegionService;

/** 
 * @Description 国家 对外服务包实现
 * @author yifang.huang
 * @date 2016年5月19日 下午4:09:54 
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/country")
public class CountryController extends BaseController {

	@Autowired
	private RegionService regionService;
	
	/**
	 * 
	 * @Description 分页查询国家信息
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年5月19日 下午4:23:26
	 */
	@RequestMapping(value = {"list", ""})
	public String list(@RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		Page<Map<String, String>> page = regionService.findAllCountry(new Page<Map<String, String>>(request, response), paramMap);
		model.addAttribute("page", page);
		model.addAllAttributes(paramMap);
		
		return "modules/mifi/countryList";
	}
	
	
	/**
	 * 所有国家MCC信息导出
	 * @athor shuxin
	 * @date 2016年6月27日下午3:31:46
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 * String 
	 */
	@RequestMapping("export")
	public String export(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		String fileName = "国家MCC信息" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
		List<Map<String, Object>>  countryList = regionService.findForExport();
		ExportExcel ee = new ExportExcel("国家MCC信息", new String[] { "国家编号", "国家名称", "国家英文名称", "MCC" });
		Row row = null;
		Map<String, Object> map = null;
		try {
			for (int i = 0; i < countryList.size(); i++) {
				row = ee.addRow();
				map =  countryList.get(i);
				ee.addCell(row, 0, map.get("countryCode"));
				ee.addCell(row, 1, map.get("countryName"));
				ee.addCell(row, 2, map.get("countryNameEn"));
				ee.addCell(row, 3, map.get("mcces"));
				row = null;
				map = null;
			}
			ee.write(response, fileName).dispose();
			return null;
		} catch (IOException e) {
			addMessage(redirectAttributes, "导出国家MCC数据失败！失败信息：" + e.getMessage());
		}
		return "modules/mifi/countryList";
	}
}
