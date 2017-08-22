package com.uu.modules.mifi.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uu.common.persistence.Page;
import com.uu.common.utils.Socekt;
import com.uu.common.utils.StringUtils;
import com.uu.modules.mifi.condition.CommunicationRecordCondition;
import com.uu.modules.mifi.entity.CommunicationRecord;
import com.uu.modules.mifi.service.CommunicationRecordService;
import com.uu.modules.mifi.service.MifiTrafficService;

import net.sf.json.JSONObject;

/**
 * 通信记录控制器
 * 
 * @author shuxin
 * @date 2016年7月20日
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/cRecord")
public class CommunicationRecordController {
	public static Logger logger = LoggerFactory.getLogger(CommunicationRecordController.class);

	@Autowired
	private CommunicationRecordService cRecordService;

	@Autowired
	private MifiTrafficService mifiTrafficService;
	
	/**
	 * 列表页面
	 * 
	 * @athor shuxin
	 * @date 2016年7月20日下午4:20:09
	 * @param condition
	 * @param request
	 * @param response
	 * @param model
	 * @return String
	 */
	@RequiresPermissions("mifi:cRecord:view")
	@RequestMapping(value = "")
	public String init(CommunicationRecordCondition cRecordCondition, HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		return "modules/mifi/communicationRecordList";
	}
	
	@RequiresPermissions("mifi:cRecord:view")
	@RequestMapping(value = "list")
	public String list(CommunicationRecordCondition cRecordCondition, HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		Page<CommunicationRecord> page = cRecordService
				.findcRecordsByPage(new Page<CommunicationRecord>(request, response), cRecordCondition);
		model.put("page", page);
		model.put("condition", cRecordCondition);
		return "modules/mifi/communicationRecordList";
	}
	
	/**
	 * 通信
	 * @athor shuxin
	 * @date 2016年7月21日上午11:10:19
	 * @param sn
	 * @param type
	 * @return
	 * JSONObject 
	 */
	@RequiresPermissions("mifi:cRecord:edit")
	@RequestMapping("message")
	public @ResponseBody JSONObject message(String id, String sn, Integer type){
		JSONObject json = new JSONObject();
		if(!StringUtils.isNotBlank(sn) || null == type){
			Map<String, String> map = new HashMap<String, String>();
			map.put("code", "-1");
			map.put("msg", "无卡");
			json.put("data", map);
		}
		List<Map<String, Integer>> listMap =new ArrayList<Map<String,Integer>>();
		if(CommunicationRecord.CARD_TYPE == type){ //卡通信
			if(sn.indexOf(",")  != -1){
				String[] snArr = sn.split(",");
				for (int i = 0; i < snArr.length; i++) {
					Map<String, Integer> simMap = mifiTrafficService.findSimBlankIdAndSimIDByIccidAndUsimtatus(snArr[i], 2); //查找有效simNode
					if(!simMap.isEmpty()){
						listMap.add(simMap);
					}
				}
			} else {
				Map<String, Integer> simMap = mifiTrafficService.findSimBlankIdAndSimIDByIccidAndUsimtatus(sn, 2); //查找有效simNode
				if(!simMap.isEmpty()){
					listMap.add(simMap);
				}
			}
			Map<String, String> result = Socekt.simUpdate(listMap);
			if(result.get("code").equals("0")){ //通信成功后修改数据
				CommunicationRecord cRecord = cRecordService.getCRecordById(id);
				cRecord.setResult(CommunicationRecord.RESULT_SUCESS);
				cRecordService.saveCRecord(cRecord);
			}
			json.put("data", result);
		}
		return json;
	}

}
