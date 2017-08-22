/** 
 * @Package com.uu.modules.mifi.web 
 * @Description 
 * @author yifang.huang
 * @date 2017年5月23日 下午4:17:09 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.web;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uu.common.config.Global;
import com.uu.common.persistence.Page;
import com.uu.common.utils.excel.ExportExcel;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.condition.FlowCountCondition;
import com.uu.modules.mifi.condition.FlowCountRecordCondition;
import com.uu.modules.mifi.condition.FlowSummaryCondition;
import com.uu.modules.mifi.entity.FlowCount;
import com.uu.modules.mifi.entity.FlowCountItem;
import com.uu.modules.mifi.entity.FlowCountRecord;
import com.uu.modules.mifi.entity.FlowSummary;
import com.uu.modules.mifi.entity.FlowSummaryItem;
import com.uu.modules.mifi.service.FlowCountRecordService;
import com.uu.modules.mifi.service.FlowCountService;
import com.uu.modules.mifi.service.FlowSummaryService;
import com.uu.modules.om.entity.Region;
import com.uu.modules.om.service.RegionService;
import com.uu.modules.sys.service.LogService;

/** 
 * @Description 
 * @author yifang.huang
 * @date 2017年5月23日 下午4:17:09 
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/flowCount")
public class FlowCountRecordController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(FlowCountRecordController.class);
	
	@Autowired
	private FlowCountRecordService flowCountRecordService;
	
	@Autowired
	private FlowSummaryService flowSummaryService;
	
	@Autowired
	private FlowCountService flowCountService;
	
	@Autowired
	private RegionService regionService;
	
	@Autowired
	private LogService logService;
	
	@ModelAttribute
	public FlowCountRecord get(@RequestParam(required=false) String id) {
		
		if (StringUtils.isNotBlank(id))
			return flowCountRecordService.get(id);
		
		return new FlowCountRecord();
		
	}

	@RequiresPermissions("mifi:flowCount:view")
	@RequestMapping(value = "form")
	public String form(FlowCountRecord bean, Model model) {
		
		model.addAttribute("record", bean);
		
		return "modules/mifi/flowCountRecordForm";
		
	}

	/**
	 * 
	 * @Description 流量统计记录列表
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2017年5月23日 下午4:18:47
	 */
	@RequiresPermissions("mifi:flowCount:view")
	@RequestMapping(value = { "list", "" })
	public String list(FlowCountRecordCondition condition, HttpServletRequest request,
			HttpServletResponse response, Model model) {

		if (StringUtils.isNotBlank(request.getParameter("queryData"))) {// 是否查询数据
			
			Page<FlowCountRecord> page = flowCountRecordService.findPage(new Page<FlowCountRecord>(request, response), condition);
			model.addAttribute("page", page);
			
		}
		
		return "modules/mifi/flowCountRecord";

	}

	/**
	 * 
	 * @Description 流量统计记录列表
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2017年5月23日 下午4:18:47
	 */
	@RequiresPermissions("mifi:flowCount:view")
	@RequestMapping(value = "dataList")
	public String dataList(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {

		String id = ObjectUtils.toString(paramMap.get("id"));
		model.addAttribute("id", id);
		
		// 统计的国家列表
		FlowCountRecord record = flowCountRecordService.get(id);
		Region region = record.getRegion();
		model.addAttribute("countryList", Arrays.asList(region.getCountryNames().split(",")));
		
		// 统计汇总
		FlowSummaryCondition fsCondition = new FlowSummaryCondition();
		fsCondition.setEqFlowCountRecordId(id);
		List<FlowSummary> summaryList = flowSummaryService.findList(fsCondition);
		model.addAttribute("summaryList", summaryList);
		
		// 统计数据
		FlowCountCondition fcCondition = new FlowCountCondition();
		fcCondition.setEqFlowCountRecordId(id);
		Page<FlowCount> page = flowCountService.findPage(new Page<FlowCount>(request, response), fcCondition);
		model.addAttribute("page", page);
		
		return "modules/mifi/flowCountData";

	}
	
	/**
	 * 
	 * @Description 生成统计记录
	 * @param request
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2017年5月23日 下午4:32:53
	 */
	@RequiresPermissions("mifi:flowCount:count")
    @RequestMapping(value = "/count.json",produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> count(@RequestParam Map<String, Object> paramMap, 
			HttpServletRequest request) {
		
		Map<String, String> resultMap = new HashMap<String, String>();

		FlowCountRecord record = null;
		try {

			String startDate = ObjectUtils.toString(paramMap.get("startDate"));			// 开始时间
			String endDate = ObjectUtils.toString(paramMap.get("endDate"));				// 结束时间
			String regionId = ObjectUtils.toString(paramMap.get("regionId"));			// 区域ID
			
			// 保存统计记录
			Region region = regionService.get(regionId);
			if (region == null) {
				resultMap.put("status", "error");
				resultMap.put("message", "区域未找到!");
				return resultMap;
			}
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			record = new FlowCountRecord();
			record.setStartDate(df.parse(startDate + " 00:00:00"));
			record.setEndDate(df.parse(endDate + " 23:59:59"));
			record.setRegion(region);
			flowCountRecordService.save(record);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			resultMap.put("status", "error");
			resultMap.put("message", "生成统计任务失败!");
			return resultMap;
		}

		resultMap.put("recordId", record.getId());
		resultMap.put("status", "success");
		resultMap.put("message", "统计任务已生成!");
		return resultMap;
	}
	
	/**
	 * 
	 * @Description 统计
	 * @param request
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2017年5月23日 下午4:32:53
	 */
	@RequiresPermissions("mifi:flowCount:count")
    @RequestMapping(value = "/changeStatus.json",produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> changeStatus(@RequestParam Map<String, Object> paramMap, 
			HttpServletRequest request) {
		
		Map<String, String> resultMap = new HashMap<String, String>();

		// 判断有没有统计统计线程存在
		FlowCountRecordCondition fcrCondition = new FlowCountRecordCondition();
		fcrCondition.setEqStatus(FlowCountRecord.status_counting);
		List<FlowCountRecord> fcrList = flowCountRecordService.findList(fcrCondition);
		if (fcrList.size() > 0) {
			resultMap.put("status", "error");
			resultMap.put("message", "已存在统计线程,请稍后!");
			return resultMap;
		}

		String recordId = ObjectUtils.toString(paramMap.get("recordId"));
		FlowCountRecord record = flowCountRecordService.get(recordId);
		if (record == null) {
			resultMap.put("status", "error");
			resultMap.put("message", "统计任务未找到!");
			return resultMap;
		}
		record.setStatus(FlowCountRecord.status_counting);
		record.setExecuteDate(new Date());
		flowCountRecordService.save(record);

		resultMap.put("recordId", record.getId());
		resultMap.put("status", "success");
		resultMap.put("message", "可以执行统计!");
		return resultMap;
	}
	
	/**
	 * 
	 * @Description 统计
	 * @param request
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2017年5月23日 下午4:32:53
	 */
	@RequiresPermissions("mifi:flowCount:count")
    @RequestMapping(value = "/save.json",produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> save(@RequestParam Map<String, Object> paramMap, 
			HttpServletRequest request) {
		
		Map<String, String> resultMap = new HashMap<String, String>();

		FlowCountRecord record = null;
		try {
			
			String recordId = ObjectUtils.toString(paramMap.get("recordId"));
			record = flowCountRecordService.get(recordId);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String startDate = df.format(record.getStartDate());
			String endDate = df.format(record.getEndDate());
			
			flowCountRecordService.countFlow(startDate, endDate, record);
			
		} catch (Exception e) {
			
			if (record != null) {
				record.setStatus(FlowCountRecord.status_fail);
				flowCountRecordService.save(record);
			}
			
			e.printStackTrace();
			resultMap.put("status", "error");
			resultMap.put("message", "统计任务执行失败");
			return resultMap;
		}
		
		// 修改统计记录状态
		record.setStatus(FlowCountRecord.status_ended);
		record.setFinishDate(new Date());
		flowCountRecordService.save(record);

		resultMap.put("status", "success");
		resultMap.put("message", "统计任务执行成功");
		return resultMap;
	}
	
	/**
	 * 
	 * @Description 删除
	 * @param id
	 * @param request
	 * @param redirectAttributes
	 * @return String  
	 * @author yifang.huang
	 * @date 2017年5月25日 下午10:12:37
	 */
	@RequiresPermissions("mifi:flowCount:del")
	@RequestMapping(value = "delete")
	public String delete(String id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/mifi/flowCount/?repage";
		}
		flowCountRecordService.delete(id);
		logService.saveOperateLog(request, "[MIFI管理 》流量管理 》区域流量查询]-记录删除，删除的ID为：[{}]", id);
		addMessage(redirectAttributes, "删除流量统计记录成功");
		return "redirect:"+Global.getAdminPath()+"/mifi/flowCount/?repage";
	}

	/**
	 * 
	 * @Description 流量统计数据导出
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @param id
	 * @return String  
	 * @author yifang.huang
	 * @date 2017年5月27日 下午4:16:01
	 */
	@RequiresPermissions("mifi:flowCount:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(FlowCountRecordCondition condition, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		try {
			String id = condition.getEqId();
			FlowCountRecord record = flowCountRecordService.get(id);
			Region region = record.getRegion();
			
			// sheet1标题数组
			String[] countryNameArr = region.getCountryNames().split(",");
			String[] sheet1TitleArr =  new String[countryNameArr.length + 5];
			sheet1TitleArr[0] = "设备编号";
			sheet1TitleArr[1] = "订单编号";
			sheet1TitleArr[2] = "开始时间";
			sheet1TitleArr[3] = "结束时间";
			sheet1TitleArr[4] = "总流量";
			for (int i=5; i<countryNameArr.length+5; i++) {
				sheet1TitleArr[i] = countryNameArr[i-5];
			}
			
			// 统计详情
			FlowCountCondition fcCondition = new FlowCountCondition();
			fcCondition.setEqFlowCountRecordId(id);
			List<FlowCount> fcList = flowCountService.findList(fcCondition);
			
			String fileName = region.getName() + " 流量统计数据.xlsx";
			ExportExcel ee = new ExportExcel("统计详情", sheet1TitleArr);
			FlowCount count = null;
			Row row = null;
			for (int i=0; i<fcList.size(); i++) {
				row = ee.addRow();
				count = fcList.get(i);
				ee.addCell(row, 0, count.getImei());
				ee.addCell(row, 1, count.getOrderCode());
				ee.addCell(row, 2, count.getStartDate());
				ee.addCell(row, 3, count.getEndDate());
				ee.addCell(row, 4, count.getFlowTotal());
				for (int j=0; j<countryNameArr.length; j++) {
					for (FlowCountItem item : count.getItemList()) {
						if (countryNameArr[j].equals(item.getCountryName())) {
							ee.addCell(row, j+5, item.getFlow());
							break;
						}
					}
				}
				
				row = null;
				count = null;
			}

			// sheet2标题数组
			String[] sheet2TitleArr = new String[countryNameArr.length + 5];
			sheet2TitleArr[0] = "订单总数";
			sheet2TitleArr[1] = "设备总数";
			sheet2TitleArr[2] = "总流量";
			sheet2TitleArr[3] = "平均流量";
			sheet2TitleArr[4] = "非漫游率";
			for (int i=5; i<countryNameArr.length+5; i++) {
				sheet2TitleArr[i] = countryNameArr[i-5];
			}
			ee.addSheet("统计汇总", sheet2TitleArr);

			// 统计汇总
			FlowSummaryCondition fsCondition = new FlowSummaryCondition();
			fsCondition.setEqFlowCountRecordId(id);
			List<FlowSummary> fsList = flowSummaryService.findList(fsCondition);
			FlowSummary summary = null;
			for (int i=0; i<fsList.size(); i++) {
				row = ee.addRowToSheet2();
				summary = fsList.get(i);
				ee.addCell(row, 0, summary.getOrderTotal());
				ee.addCell(row, 1, summary.getDeviceTotal());
				ee.addCell(row, 2, summary.getFlowTotal());
				ee.addCell(row, 3, summary.getFlowAverage());
				BigDecimal bd1 = new BigDecimal(String.valueOf(summary.getOneCountryDeviceTotal()));
				BigDecimal bd2 = new BigDecimal(String.valueOf(summary.getDeviceTotal()));
				BigDecimal bd = bd1.divide(bd2, 4, BigDecimal.ROUND_HALF_UP);
	            bd = bd.multiply(new BigDecimal(100));
				ee.addCell(row, 4, bd + "%");
				for (int j=0; j<countryNameArr.length; j++) {
					for (FlowSummaryItem item : summary.getItemList()) {
						if (countryNameArr[j].equals(item.getCountryName())) {
							ee.addCell(row, j+5, item.getFlow());
							break;
						}
					}
				}
				
				row = null;
				summary = null;
			}
			
			ee.write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出统计数据失败！失败信息：" + e.getMessage());
			e.printStackTrace();
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/flowCount?queryData=yes";
	}
	
}
