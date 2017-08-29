package main.java.com.qlink.modules.mifi.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uu.common.persistence.Page;
import com.uu.common.utils.Socekt;
import com.uu.common.utils.StringUtils;
import com.uu.common.utils.excel.ExportExcel;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.entity.CardBasicInfo;
import com.uu.modules.mifi.entity.SimCardType;
import com.uu.modules.mifi.service.CardManageService;
import com.uu.modules.mifi.service.MifiTrafficService;
import com.uu.modules.mifi.service.SimCardTypeService;
import com.uu.modules.sys.utils.DictUtils;

import net.sf.json.JSONArray;

@Controller
@RequestMapping(value = "${adminPath}/mifi/simNode")
public class SimNodeController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(SimNodeController.class);

	@Autowired
	MifiTrafficService mifiTrafficService;
	
	@Autowired
	private SimCardTypeService simCardTypeService;
	
	@Autowired
	private CardManageService cardManageService;

	@RequestMapping(value = "init")
	public String init(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		model.addAllAttributes(paramMap);
		return "modules/mifi/simNodeList";
	}

	@RequestMapping(value = "list")
	public String list(@RequestParam Map<String, Object> paramMap, @RequestParam(required=false) String[] typeArr, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<HashMap<String, Object>> page = mifiTrafficService.findSimNodeStatList(new Page<HashMap<String, Object>>(request, response), paramMap, typeArr);
		List<HashMap<String, Object>> resultList =  page.getList();
		if(!resultList.isEmpty()){
			getFirstUseTime(resultList);
		}
		model.addAttribute("page", page);
		model.addAllAttributes(paramMap);
		// 多选框回显到页面
		if (null != typeArr && 0 != typeArr.length) {
			JSONArray jsonArr = JSONArray.fromObject(typeArr);
			model.addAttribute("typeArr", jsonArr);
		}
		return "modules/mifi/simNodeList";
	}
	
	/**
	 * SIM卡有效数据导出
	 * @Description 
	 * @param paramMap
	 * @param typeArr
	 * @param request
	 * @param response
	 * @param model 
	 * @return void  
	 * @author yuxiaoyu
	 * @date 2016年12月16日 上午10:33:25
	 */
	@RequestMapping(value = "export")
	public void export(@RequestParam Map<String, Object> paramMap, @RequestParam(required = false) String[] typeArr, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		try {
			Page<HashMap<String, Object>> page = mifiTrafficService.findSimNodeStatList(new Page<HashMap<String, Object>>(),
					paramMap, typeArr);
			List<HashMap<String, Object>> resultList = page.getList();
			if (!resultList.isEmpty()) {
				getFirstUseTime(resultList);
			}
			ExportExcel exportExcel = new ExportExcel("SIM卡有效数据", new String[] { "卡号", "卡类型", "卡槽编号", "卡槽位置", "剩余有效天数(9999:永久有效)", "总有效天数", "首次使用时间",
					"激活时间", "总高速流量(M)", "已使用高速流量(M)", "剩余高速流量(M)" });
			Row row;
			for (HashMap<String, Object> resultMap : resultList) {
				row = exportExcel.addRow();
				exportExcel.addCell(row, 0, resultMap.get("iccid"));
				String type = StringUtils.emptyIfNull((String)resultMap.get("type"));
				type = DictUtils.getLabelByTable("sim_card_type", "card_type","card_type_name", type);
				exportExcel.addCell(row, 1, type);
				exportExcel.addCell(row, 2, resultMap.get("simbankid"));
				exportExcel.addCell(row, 3, resultMap.get("simid"));
				exportExcel.addCell(row, 4, resultMap.get("remainValidDay"));
				exportExcel.addCell(row, 5, resultMap.get("SIMCARDVALIDDAY"));
				exportExcel.addCell(row, 6, resultMap.get("firstUseTime"));
				exportExcel.addCell(row, 7, resultMap.get("stamp_firstactive"));
				exportExcel.addCell(row, 8, resultMap.get("dataCap"));
				exportExcel.addCell(row, 9, resultMap.get("usedCap"));
				exportExcel.addCell(row, 10, resultMap.get("remainCap"));
			}
			exportExcel.write(response, "SIM卡有效数据.xlsx").dispose();
		} catch (Exception e) {
			logger.error(StringUtils.EMPTY, e);
		}
	}
	
	/** 
	 * @Description 查出sim卡首次使用时间
	 * @param list 
	 * @return void  
	 * @author yuxiaoyu
	 * @date 2016年12月16日 上午10:06:53 
	 */ 
	private void getFirstUseTime(List<HashMap<String, Object>> list) {
		StringBuffer buffer = new StringBuffer();
		for (HashMap<String, Object> hashMap : list) {
			buffer.append("'");
			buffer.append(hashMap.get("iccid"));
			buffer.append("'");
			buffer.append(",");
		}
		String iccids = buffer.substring(0, buffer.lastIndexOf(",")).toString();
		//查出sim卡首次使用时间列表
		List<Map<String, Object>> fisstTimeList = mifiTrafficService.findSimcardStatusByIccidAndUsimtatus(iccids, 3);
		for (HashMap<String, Object> hashMap : list) {
			for (Map<String, Object> map : fisstTimeList) {
				if(map.get("iccid").equals(hashMap.get("iccid"))){
					hashMap.put("firstUseTime", map.get("firstUseTime"));
					break;
				}
			}
		}
	}

	/**
	 * 
	 * @Description 卡流量清零
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2017年3月6日 下午5:09:51
	 */
	@RequestMapping(value = "cardFlowClearPage")
	public String cardFlowClearPage(HttpServletRequest request, HttpServletResponse response, Model model) {

		return "modules/mifi/cardFlowClear";
		
	}
	
	/**
	 * 
	 * @Description 卡流量清零
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param model
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2017年3月6日 下午5:32:51
	 */
	@RequestMapping(value = "/cardFlowClear.json", produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> cardFlowClear(@RequestParam Map<String, Object> paramMap, 
			HttpServletRequest request, HttpServletResponse response, Model model) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		// 参数
		String cardType = ObjectUtils.toString(paramMap.get("cardType"));
		
		// 参数判断
		if (StringUtils.isBlank(cardType)) {
			map.put("status", "error");
			map.put("message", "请输入卡类型编号");
			return map;
		}
		SimCardType type = simCardTypeService.finByCardType(cardType);
		if (type == null) {
			map.put("status", "error");
			map.put("message", "卡类型未找到");
			return map;
		}
		
		try {
			// 取卡列表
			List<CardBasicInfo> cbiList = cardManageService.findByCardType(cardType);
			if (cbiList!=null && cbiList.size()>0) {
				
				// 状态为6的超流量的卡集合，需要通信
				Set<Map<String, Object>> cardInfoSet = new HashSet<Map<String, Object>>();
				// 记录所有流量清零的卡编号
				Set<String> cardIdSet = new HashSet<String>();
				
				List<String> iccIdList = new ArrayList<String>();
				for (CardBasicInfo cbi : cbiList) {
					iccIdList.add(cbi.getSn());
					// 不修改流量清零时间，下次自己清零时会被继续执行
					//cbi.setClearTime(new Date());
					//cardManageService.save(cbi);
				}
				
				// 对满足条件的卡集合进行处理
				if (iccIdList.size() > 0) {
					// 状态等于6并且超流量的卡单独处理，状态需要制成2
					for (String iccId: iccIdList) {
						List<Map<String, Object>> listMap = cardManageService.findExceedFlowAndStatusIs6ByIccId(iccId);
						if (listMap!=null && listMap.size()>0) {
							cardInfoSet.add(listMap.get(0));
						}
					}
					
					// simnode表已使用流量清零
					cardManageService.updateClearDataBySn(iccIdList);
					cardIdSet.addAll(iccIdList);
				}
				
				// 状态等于6并且超流量的卡状态制成2
				if (cardInfoSet.size() > 0 ) {
					Socekt.simStatusBatchController(new ArrayList<Map<String, Object>>(cardInfoSet), 2, "clearCardData(CardDataClearScheduled)");
				}
				
				// 取流量清零的卡对应的SIMBANKID、SIMID,通信状态改为7
				if (cardIdSet.size() > 0) {
					List<Map<String, Object>> cardInfoList = cardManageService.findSimNode(new ArrayList<String>(cardIdSet));
					if (cardInfoList!=null && cardInfoList.size()>0)
						Socekt.simStatusBatchController(cardInfoList, 7, "clearCardData(CardDataClearScheduled)");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", "error");
			map.put("message", "卡流量清零失败");
			return map;
		}

		map.put("status", "success");
		map.put("message", "卡流量清零成功");
		return map;
		
	}
}
