package main.java.com.qlink.modules.mifi.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.uu.common.config.Global;
import com.uu.common.persistence.Page;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.Socekt;
import com.uu.common.utils.excel.ExportExcel;
import com.uu.common.utils.excel.ImportExcel;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.entity.CardBasicInfo;
import com.uu.modules.mifi.entity.CommunicationRecord;
import com.uu.modules.mifi.entity.SimCardType;
import com.uu.modules.mifi.service.CardManageService;
import com.uu.modules.mifi.service.CommunicationRecordService;
import com.uu.modules.mifi.service.MifiTrafficService;
import com.uu.modules.mifi.service.SimCardTypeService;
import com.uu.modules.sys.service.LogService;
import com.uu.modules.sys.utils.DictUtils;

import jodd.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author wangyong
 * @date 2016年3月30日
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/cardBasicInfo")
public class CardBasicInfoController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(CardBasicInfoController.class);

	@Autowired
	private CardManageService cardManageService;
	
	@Autowired
	private LogService logService;
	
	@Autowired
	private MifiTrafficService mifiTrafficService;
	
	@Autowired
	private CommunicationRecordService cRecordService;
	
	@Autowired
	private SimCardTypeService simCardTypeService;

	@ModelAttribute
	public CardBasicInfo get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return cardManageService.get(id);
		} else {
			return new CardBasicInfo();
		}
	}

	@RequiresPermissions("mifi:cardBasicInfo:view")
	@RequestMapping(value = "init")
	public String init(CardBasicInfo cardBasicInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		return "modules/mifi/cardBasicInfoList";
	}

	@RequiresPermissions("mifi:cardBasicInfo:view")
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = { "list", "" })
	public String list(CardBasicInfo cardBasicInfo, HttpServletRequest request, HttpServletResponse response, Model model, 
			@RequestParam(required=false) String[] typeArr, @RequestParam(required=false) String[] allowedSourceArr, 
			@RequestParam(required=false) String simbankid, @RequestParam(required=false) String simid, 
			@RequestParam(required=false) String simStatus) {
		Page<HashMap> page = cardManageService.find(new Page<HashMap>(request, response), cardBasicInfo, typeArr, allowedSourceArr, simbankid, simid, simStatus);
		model.addAttribute("simbankid", simbankid);
		model.addAttribute("simid", simid);
		model.addAttribute("simStatus", simStatus);
		model.addAttribute("page", page);
		// 多选框回显到页面
		if (null != typeArr && 0 != typeArr.length) {
			JSONArray jsonArr = JSONArray.fromObject(typeArr);
			model.addAttribute("typeArr", jsonArr);
		}
		if (allowedSourceArr!=null  && allowedSourceArr.length>0) {
			JSONArray jsonArr = JSONArray.fromObject(allowedSourceArr);
			model.addAttribute("allowedSourceArr", jsonArr);
		}
		return "modules/mifi/cardBasicInfoList";
	}

	@RequiresPermissions("mifi:cardBasicInfo:view")
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(CardBasicInfo cardBasicInfo, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes, 
			@RequestParam(required=false) String[] typeArr, @RequestParam(required=false) String[] allowedSourceArr,
			@RequestParam(required=false) String simbankid,@RequestParam(required=false) String simid) {
		try {
			String fileName = "卡数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<HashMap> cardMapList = cardManageService.findForExport(cardBasicInfo, typeArr, allowedSourceArr, simbankid, simid);
			ExportExcel ee = new ExportExcel("卡数据", new String[] { "卡号", "卡类型", "激活时间(YYYY/MM/DD)", "IMSI", "ICCID", "SIMBANKID", "SIMID", "卡当前状态", "总流量(M)", "已使用流量(M)", "地区", "地区中文名", "地区英文名", "APN", "ALLOWEDMCC", "pin", "puk", "批次号", "供应商", "入库时间" });
			HashMap map = null;
			Row row = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (int i = 0; i < cardMapList.size(); i++) {
				row = ee.addRow();
				map = cardMapList.get(i);
				ee.addCell(row, 0, map.get("sn"));
				ee.addCell(row, 1, map.get("type"));
				ee.addCell(row, 2, StringUtils.isBlank(ObjectUtils.toString(map.get("stamp_firstactive"))) ? "" : df.format(map.get("stamp_firstactive")));
				ee.addCell(row, 3, map.get("imsi"));
				ee.addCell(row, 4, map.get("iccid"));
				ee.addCell(row, 5, ObjectUtils.toString(map.get("simBankId")));
				ee.addCell(row, 6, ObjectUtils.toString(map.get("simId")));
				ee.addCell(row, 7, DictUtils.getDictLabel(ObjectUtils.toString(map.get("USIMSTATUS")), "usimstatus", "未知状态"));
				ee.addCell(row, 8, map.get("dataCap"));
				ee.addCell(row, 9, map.get("dataUsed"));
				ee.addCell(row, 10, map.get("mcc"));
				ee.addCell(row, 11, map.get("country_name_cn"));
				ee.addCell(row, 12, map.get("country_name_en"));
				ee.addCell(row, 13, map.get("apn_info"));
				ee.addCell(row, 14, map.get("allowedmcc"));
				ee.addCell(row, 15, map.get("pin"));
				ee.addCell(row, 16, map.get("puk"));
				ee.addCell(row, 17, map.get("bath"));
				ee.addCell(row, 18, map.get("supplier"));
				ee.addCell(row, 19, StringUtils.isBlank(ObjectUtils.toString(map.get("create_time"))) ? "" : df.format(map.get("create_time")));
				
				row = null;
				map = null;
				
			}
			ee.write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出卡数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/cardBasicInfo/init";
	}

	@RequiresPermissions("mifi:cardBasicInfo:import")
	@RequestMapping("import/template")
	public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "卡数据导入模板.xlsx";
			List<CardBasicInfo> list = Lists.newArrayList();
			new ExportExcel("卡数据", CardBasicInfo.class, 2).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/cardBasicInfo/init";
	}

	@RequiresPermissions("mifi:cardBasicInfo:import")
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "import", method = RequestMethod.POST)
	public String importFile(MultipartFile file, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		try {
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<CardBasicInfo> list = ei.getDataList(CardBasicInfo.class);
			JSONObject obj = cardManageService.importFile(list);
			List<Map<String, Integer>> listMap = obj.get("notice") == null ? new ArrayList<Map<String,Integer>>() : (List<Map<String, Integer>>) obj.get("notice");
			 //通知服务
			 Map<String, String> resultMap = Socekt.simUpdate(listMap);
			//通信记录保存、通信日志保存
			List<String> iccids =(List<String>) obj.get("iccids");
			String iccidsStr = com.uu.common.utils.StringUtils.convertListtoString(iccids);
			if(StringUtils.isNotBlank(iccidsStr)){
				CommunicationRecord cRecord =new CommunicationRecord();
				cRecord.setDeviceSn(iccidsStr);
				cRecord.setType(CommunicationRecord.CARD_TYPE);
				cRecord.setResult("-1".equals(resultMap.get("code")) ? CommunicationRecord.RESULT_FAILED : CommunicationRecord.RESULT_SUCESS);
				cRecord.setRemarks(resultMap.get("msg"));
				cRecordService.saveCRecord(cRecord); //保存通信记录
				logService.saveOperateLog(request, "[MIFI管理 》SIM卡管理]-导入卡数据，导入的SIM卡号：[{}]，卡信息修改通知服务执行结果：[{}]",  iccidsStr, resultMap.get("msg")); //保存操作日志
			}
			
			addMessage(redirectAttributes, "已成功导入[" + obj.getString("successNum") + "]条卡数据" + obj.getString("failureMsg"));
			
			// 修改card_basic_info的sn_hex值
			cardManageService.updateSnHex();
			
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "导入卡数据失败!失败信息:" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/cardBasicInfo/init";
	}

	@RequiresPermissions("mifi:cardBasicInfo:edit")
	@RequestMapping(value = "form")
	public String form(CardBasicInfo cardBasicInfo, Model model) {
		model.addAttribute("cardBasicInfo", cardBasicInfo);
		return "modules/mifi/cardBasicInfoForm";
	}

	@RequiresPermissions("mifi:cardBasicInfo:edit")
	@RequestMapping(value = "save")
	public String save(CardBasicInfo cardBasicInfo, String oldCardType, HttpServletRequest request, Model model,
			RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, cardBasicInfo)) {
			return form(cardBasicInfo, model);
		}
		CardBasicInfo _cardBasicInfo = get(cardBasicInfo.getId());
		_cardBasicInfo.setType(cardBasicInfo.getType());
		_cardBasicInfo.setActiveTime(cardBasicInfo.getActiveTime());
		if (cardBasicInfo.getClearTime() == null)
			cardBasicInfo.setClearTime(cardBasicInfo.getActiveTime());
		cardManageService.save(cardBasicInfo, oldCardType);
		//保存日志
		StringBuffer buffer =new StringBuffer("[MIFI管理 》SIM卡管理]-修改SIM卡，");
		buffer.append("SIM卡号：[{}]，");
		buffer.append("修改卡类型名称为：[{}]-[{}]，");
		buffer.append("修改卡激活时间：[{}]，");
		buffer.append("修改卡重置流量：[{}]");
		String activeTime = "";
		if(cardBasicInfo.getActiveTime() != null){
			activeTime = DateUtils.formatDate(cardBasicInfo.getActiveTime(), "yyyy-MM-dd HH:mm:ss") ;
		}
		logService.saveOperateLog(request, buffer.toString(), cardBasicInfo.getSn(),cardBasicInfo.getType(),DictUtils.getLabelByTable("sim_card_type", "card_type","card_type_name", cardBasicInfo.getType()), activeTime,_cardBasicInfo.getDataCap());
		//卡信息修改通知服务日志记录
		String iccId = cardBasicInfo.getSn();
		Map<String, Integer> simMap = mifiTrafficService.findSimBlankIdAndSimIDByIccidAndUsimtatus(iccId, 2); //查找有效simNode
		List<Map<String, Integer>> listMap =new ArrayList<Map<String,Integer>>();
		if(!simMap.isEmpty()){
			listMap.add(simMap);
		}
		Map<String, String> result = Socekt.simUpdate(listMap);
		//保存通信记录
		CommunicationRecord cRecord =new CommunicationRecord();
		cRecord.setDeviceSn(cardBasicInfo.getSn().trim());
		cRecord.setType(CommunicationRecord.CARD_TYPE);
		cRecord.setResult("-1".equals(result.get("code")) ? CommunicationRecord.RESULT_FAILED : CommunicationRecord.RESULT_SUCESS);
		cRecord.setRemarks(result.get("msg"));
		cRecordService.saveCRecord(cRecord);
		String message = "[MIFI管理 》SIM卡管理]-修改SIM卡，修改SIM卡[{}]，卡信息修改通知服务执行结果：[{}]";
		logService.saveOperateLog(request, message, cardBasicInfo.getSn(),result.get("msg"));
		addMessage(redirectAttributes, "保存卡[卡号:" + cardBasicInfo.getSn() + "]成功");
		return "redirect:" + Global.getAdminPath() + "/mifi/cardBasicInfo/init";
	}
	
	/**
	 * 
	 * @Description 主控版查看
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年5月27日 上午11:12:43
	 */
	@RequestMapping(value = "simNodeView")
	public String simNodeView(HttpServletRequest request, HttpServletResponse response, Model model) {
		
		model.addAttribute("simBankList", cardManageService.findSimBankIdList());

		return "modules/mifi/simNodeView";
		
	}
	
	/**
	 * 
	 * @Description 主控版详情
	 * @param dateStr
	 * @param sourceType
	 * @param request
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月27日 上午11:12:58
	 */
	@RequestMapping(value = "simNodeDetail", produces="application/json;charset=UTF-8")
	@ResponseBody  
	public Map<String, Object> simNodeDetail(@RequestParam String simBankId, HttpServletRequest request) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			
			// 卡槽ID列表
			List<Map<String, Object>> simIdList = cardManageService.findSimIdListBySimBankId(simBankId);
			map.put("simIdList", simIdList);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取主控版详情失败", e);
			map.put("code", "-1");
			map.put("msg", "获取主控版详情失败");
			return map;
		}
		
		map.put("code", "1");
		map.put("simBankId", simBankId);
		return map;
		
	}
	/**
	 * 激活时间重置
	 * @athor shuxin
	 * @date 2016年6月17日下午2:56:13
	 * @param redirectAttributes
	 * @return
	 * String 
	 */
	@RequiresPermissions("mifi:cardBasicInfo:time")
	@RequestMapping("resetTime")
	public  @ResponseBody JSONObject resetFirstTime(@RequestParam(required=false) String cardTypes, HttpServletRequest request) {
		
		if (StringUtils.isNotBlank(cardTypes))
			cardTypes = cardTypes.replace(",", "','");
		JSONObject json = cardManageService.updateResetSimActiveTime(cardTypes);
		
		logService.saveOperateLog(request, "[MIFI管理 》SIM卡管理]-激活时间重置，重置的结果为：[{}]", json.get("msg").toString());
		return json;
	}
	
	/**
	 * 下载修改APN的卡数据模板
	 * 
	 * @athor shuxin
	 * @date 2016年6月20日下午3:51:19
	 * @param response
	 * @param redirectAttributes
	 * @return
	 * String 
	 */
	@RequiresPermissions("mifi:cardBasicInfo:apnInfo")
	@RequestMapping("import/APN")
	public String importAPNTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes){
		try {
			String fileName = "卡数据导入模板-修改APN信息.xlsx";
			new ExportExcel("修改APN信息的卡数据", new String[]{"iccid"}).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/cardBasicInfo/init";
	}
	
	/**
	 * 处理apn文件
	 * @athor shuxin
	 * @date 2016年6月22日下午3:27:15
	 * @param file
	 * @param redirectAttributes
	 * @return
	 * String 
	 */
	@RequiresPermissions("mifi:cardBasicInfo:apnInfo")
	@RequestMapping("importApnFile")
	public String importAPNFile(MultipartFile file, RedirectAttributes redirectAttributes){
		try{
			ImportExcel ei = new ImportExcel(file, 1, 0);
			JSONObject obj  = cardManageService.updateAndGetIccidsString(ei);
			addMessage(redirectAttributes, "已成功导入[" + obj.getString("successNum") + "]条卡数据" + obj.getString("failureMsg"));
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入卡数据失败!失败信息:" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/cardBasicInfo/init";
	}
	
	/**
	 * 卡数据恢复
	 * @Description 
	 * @param file
	 * @param redirectAttributes
	 * @return 
	 * @return String  
	 * @author yuxiaoyu
	 * @date 2016年12月29日 下午4:24:34
	 */
	@RequiresPermissions("mifi:cardBasicInfo:import")
	@RequestMapping("importRecovery")
	public String importRecovery(MultipartFile file, RedirectAttributes redirectAttributes){
		try{
			ImportExcel ei = new ImportExcel(file, 1, 0);
			int totalNum = cardManageService.updateRecoverySimnode(ei);
			addMessage(redirectAttributes, totalNum + "条卡数据恢复成功");
		} catch (Exception e) {
			addMessage(redirectAttributes, "卡数据恢复失败!失败信息:" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/cardBasicInfo/init";
	}
	
	/**
	 * 
	 * @Description 批量修改卡状态
	 * @param response
	 * @param redirectAttributes
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年8月31日 下午4:09:59
	 */
	@RequiresPermissions("mifi:cardBasicInfo:status")
	@RequestMapping("download/template/status")
    public String downloadStatusTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "卡状态修改模板.xlsx";
			ExportExcel ee = new ExportExcel("卡及状态信息", new String[] {"SIMBANKID", "SIMID", "状态"});
			ee.write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "卡状态修改模板下载失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/mifiDevice/simStatusControllerPage";
    }
	
	/**
	 * 
	 * @Description 设备归属地修改
	 * @param file
	 * @param redirectAttributes
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年5月19日 下午2:55:00
	 */
	@RequiresPermissions("mifi:cardBasicInfo:status")
	@RequestMapping(value = "importStatusFile", method=RequestMethod.POST)
    public String importStatusFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			ImportExcel ei = new ImportExcel(file, 1, 0);
			JSONObject obj = cardManageService.importStatusFile(ei);
			addMessage(redirectAttributes, "已成功修改[" + obj.getString("successNum") + "]张卡状态" + obj.getString("failureMsg"));
		} catch (Exception e) {
			addMessage(redirectAttributes, "卡状态修改失败!失败信息:" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/mifiDevice/simStatusControllerPage";
    }
	
	/**
	 * 
	 * @Description 回收状态为3的卡
	 * @param request
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年11月22日 下午3:34:18
	 */
	@RequiresPermissions("mifi:cardBasicInfo:recovery")
    @RequestMapping(value = "/recovery3.json",produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> cardRecovery3(HttpServletRequest request) {
		
		Map<String, String> resultMap = new HashMap<String, String>();
		int count = 0;
		
		try {
			List<Map<String, Object>> listMap = cardManageService.findSimNode(null, 3, null);
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			if (listMap!=null && listMap.size()>0) {
				for (Map<String, Object> map : listMap) {
					try {
						String ueId = ObjectUtils.toString(map.get("UEID"));
						Integer simBankId = Integer.valueOf(ObjectUtils.toString(map.get("SIMBANKID")));
						Integer simId = Integer.valueOf(ObjectUtils.toString(map.get("SIMID")));
						
						// 判断mifiNode中是否有数据，有数据则不通信
						List<Map<String, Object>> tempMap = cardManageService.findMifiNode(ueId, simBankId, simId);
						if (tempMap!=null && tempMap.size()>0)
							continue;
						
						result.add(map);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				// 对结果通信
				count = result.size();
				if (count > 0) {
					Socekt.simStatusBatchController(result, 2, "cardRecovery3(CardBasicInfoController)");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("status", "error");
			resultMap.put("message", "状态3的卡回收失败");
			return resultMap;
		}

		resultMap.put("status", "success");
		resultMap.put("count", count + "");
		resultMap.put("message", "状态3的卡回收成功");
		return resultMap;
		
	}
	
	/**
	 * 
	 * @Description 回收状态为6的卡（状态为6未超流量未过期，必须指定卡类型）
	 * @param request
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年11月22日 下午3:34:18
	 */
	@RequiresPermissions("mifi:cardBasicInfo:recovery")
    @RequestMapping(value = "/recovery6.json",produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> cardRecovery6(@RequestParam Map<String, Object> paramMap, 
			HttpServletRequest request) {
		
		Map<String, String> resultMap = new HashMap<String, String>();
		int count = 0;
		
		try {
			
			// 卡类型
			String cardTypes = ObjectUtils.toString(paramMap.get("cardTypes"));
			if (StringUtils.isBlank(cardTypes)) {
				resultMap.put("status", "error");
				resultMap.put("message", "请选择卡类型");
				return resultMap;
			}
			String[] cardTypeArr = cardTypes.split(",");

			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> listMap = null;
			String days = "";
			String simCardValidDay = "";
			for (String cardType : cardTypeArr) {
				listMap = cardManageService.findSimNode(cardType, 6, false);
				if (listMap!=null && listMap.size()>0) {
					for (Map<String, Object> map : listMap) {
						// 不过期判断（1 SIMCARDVALIDDAY等于null或者DAYS等于null, 2 DAYS小于等于SIMCARDVALIDDAY）
						days = ObjectUtils.toString(map.get("DAYS"));
						simCardValidDay = ObjectUtils.toString(map.get("SIMCARDVALIDDAY"));
						
						if (StringUtils.isBlank(days) || StringUtils.isBlank(simCardValidDay))
							result.add(map);
						else if (Integer.valueOf(days) <= Integer.valueOf(simCardValidDay))
							result.add(map);
						
						days = "";
						simCardValidDay = "";
					}
					listMap = null;
				}
			}
				
			// 对结果通信
			count = result.size();
			if (count > 0) {
				Socekt.simStatusBatchController(result, 2, "cardRecovery6(CardBasicInfoController)");
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("status", "error");
			resultMap.put("message", "状态6的卡回收失败");
			return resultMap;
		}

		resultMap.put("status", "success");
		resultMap.put("count", count + "");
		resultMap.put("message", "状态6的卡回收成功");
		return resultMap;
		
	}
	
	/**
	 * 
	 * @Description 异步获取卡信息
	 * @param simBankId
	 * @param simId
	 * @param request
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年12月22日 上午10:21:29
	 */
    @RequestMapping(value = "/cardInfo.json",produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> cardInfo(@RequestParam String simBankId, @RequestParam String simId, 
			HttpServletRequest request) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		try {
			
			// 取卡信息
			Map<String, Object> cardMap = cardManageService.findSimNode(simBankId, simId);
			if (cardMap == null) {
				map.put("status", "error");
				map.put("message", "位置simBankId:"+simBankId+",simId:"+simId+"没有找到卡记录");
				return map;
			}

			// 卡信息
			map.put("iccId", ObjectUtils.toString(cardMap.get("iccId")));
			String simStatus = ObjectUtils.toString(cardMap.get("simStatus"));
			String type = ObjectUtils.toString(cardMap.get("type"));
			
			// 卡状态
			if (StringUtil.isNotBlank(simStatus)) {
				simStatus = simStatus + "|" + DictUtils.getDictLabel(simStatus, "usimstatus", "未配置");
				map.put("simStatus", simStatus);
			}
			
			// 卡类型
			if (StringUtils.isNotBlank(type)) {
				SimCardType simCardType = simCardTypeService.finByCardType(type);
				type = type + "|" + (simCardType==null ? "未找到" : simCardType.getCardTypeName());
				map.put("type", type);
			}

			map.put("status", "success");
			map.put("message", "取卡信息成功");
			return map;
			
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", "error");
			map.put("message", "取卡信息失败");
			return map;
		}
		
	}
    /**
	 * 
	 * @Description 
	 * @return String  
	 * @author wangsai
	 * @date 2017年1月10日 下午2:46:25
	 */
    @RequestMapping(value = "fi")
	public String lists(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		model.addAttribute("simBankList", cardManageService.findSimBankIdList());
		model.addAllAttributes(paramMap);
		return "modules/mifi/simNode";

	}
    /**
     * 异步获取list
     * @Description 
     * @return JSONObject  
     * @author wangsai
     * @date 2017年1月13日 下午4:08:09
     */
	@RequestMapping(value = "simBankList", method=RequestMethod.POST)
	@ResponseBody
	public JSONObject search(HttpServletRequest request){
		List<Map<String, Object>> map =	cardManageService.findSimBankIdList();
		JSONObject obj = new JSONObject();
		obj.put("results", map);
		return obj;
	}
	@RequestMapping(value = "find")
	public String list(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		@SuppressWarnings("rawtypes")
		Page<HashMap> page  = cardManageService.findSimIdListBySimBankId(new Page<HashMap>(request, response), paramMap);
		model.addAttribute("page", page);
		model.addAttribute("simBankList", cardManageService.findSimBankIdList());
		model.addAllAttributes(paramMap);
		return "modules/mifi/simNode";
	}
	
}
