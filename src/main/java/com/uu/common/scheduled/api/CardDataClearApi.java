package com.uu.common.scheduled.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.uu.common.scheduled.CardDataClearScheduled;
import com.uu.common.utils.Socekt;
import com.uu.modules.mifi.entity.CardBasicInfo;
import com.uu.modules.mifi.entity.SimCardType;
import com.uu.modules.mifi.service.CardManageService;
import com.uu.modules.mifi.service.SimCardTypeService;
import com.uu.modules.sys.service.LogService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 
 * @Description 卡流量清零定时任务 接口
 * @author yifang.huang
 * @date 2017年3月24日 上午10:59:26
 */
@Controller
@RequestMapping(value = "/api/cdc", produces = "application/json")
@Api(value = "/cdc", description = "卡流量清零定时任务 接口")
public class CardDataClearApi {

	public static Logger logger = LoggerFactory.getLogger(CardDataClearScheduled.class);

	@Autowired
	private SimCardTypeService simCardTypeService;
	
	@Autowired
	private CardManageService cardManageService;
	
	@Autowired
	private LogService logSerivce;
	
	@RequestMapping(value = "/execute.json", method = { RequestMethod.POST })
	@ApiOperation(position = 100, value = "卡流量清零定时任务", notes = "卡流量清零定时任务")
	public void execute(@ApiParam(value = "请求入参为URLEncoder处理的JSON格式", required = true) @RequestBody String reqobj,
			HttpServletRequest request, HttpServletResponse response) {
		
		Date nowDate = new Date();
		
		logger.info("流量清零定时任务开始！");
		
		// 状态为6的超流量的卡集合，需要通信
		Set<Map<String, Object>> cardInfoSet = new HashSet<Map<String, Object>>();
		// 记录所有流量清零的卡编号
		Set<String> cardIdSet = new HashSet<String>();
		
		try {
			// 卡流量清空类型为自定义、需要定时流量清零的卡类型
			List<SimCardType> ctList = simCardTypeService.findList();
			if (ctList!=null && ctList.size()>0) {
				List<CardBasicInfo> cbiList = null;
				List<String> iccIdList = null;
				for (SimCardType bean : ctList) {
					cbiList = cardManageService.findByCardType(bean.getCardType());
					
					if (cbiList!=null && cbiList.size()>0) {
						iccIdList = new ArrayList<String>();
						
						for (CardBasicInfo cbi : cbiList) {
							if (check(bean.getClearDay(), nowDate, cbi)) {
								iccIdList.add(cbi.getSn());
								// 修改流量清零时间
								cbi.setClearTime(nowDate);
								cardManageService.saveForApi(cbi);
							}
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
						
						iccIdList = null;
					}
					
					iccIdList = null;
				}
			}

			// 卡流量清空类型为月清空
			List<String> snList = new ArrayList<String>();
			List<SimCardType> cardTypeList = simCardTypeService.findCardTypeToClear();
			if (cardTypeList!=null && cardTypeList.size()>0) {
				List<CardBasicInfo> cardBasicInfoList = cardManageService.findByType(cardTypeList);
				if (cardBasicInfoList!=null && cardBasicInfoList.size()>0) {
					String sn = null;
					for(CardBasicInfo cardBasicInfo:cardBasicInfoList){
						sn = cardBasicInfo.getSn();
						logger.info("执行流量清零，卡号(sn)："+ sn);
						snList.add(sn);
						sn = null;
					}
					
				}
			}
			
			if (snList.size() > 0) {
				// 状态等于6并且超流量的卡单独处理，状态需要制成2
				for (String iccId: snList) {
					List<Map<String, Object>> listMap = cardManageService.findExceedFlowAndStatusIs6ByIccId(iccId);
					if (listMap!=null && listMap.size()>0) {
						cardInfoSet.add(listMap.get(0));
					}
				}
				
				cardManageService.updateClearDataBySn(snList);
				cardIdSet.addAll(snList);
			}
			
			// 状态等于6并且超流量的卡状态制成2
			if (cardInfoSet.size() > 0 ) {
				Socekt.simStatusBatchController(new ArrayList<Map<String, Object>>(cardInfoSet), 2, "clearCardData(CardDataClearScheduled)");
				cardInfoSet = null;
			}
			
			// 取流量清零的卡对应的SIMBANKID、SIMID,通信状态改为7
			if (cardIdSet.size() > 0) {
				List<Map<String, Object>> cardInfoList = cardManageService.findSimNode(new ArrayList<String>(cardIdSet));
				if (cardInfoList!=null && cardInfoList.size()>0)
					Socekt.simStatusBatchController(cardInfoList, 7, "clearCardData(CardDataClearScheduled)");
			}
			
		} catch (Exception e) {
			logSerivce.saveTimingLog("CardDataClearScheduled", "clearCardData", "卡流量清零定时任务执行失败,日期[{}],日期[{}]", new SimpleDateFormat("MM月dd号").format(new Date()), new SimpleDateFormat("HH时").format(new Date()));
			e.printStackTrace();
		}
		
		logger.info("流量清零定时任务结束！");
		
	}

	/**
	 * 
	 * @Description 上次流量清零时间与现在间隔天数是否大于等于，卡类型设备的流量清零天数
	 * @param clearDay
	 * @param bean
	 * @return boolean  
	 * @author yifang.huang
	 * @date 2016年5月18日 下午5:18:37
	 */
	private boolean check(int clearDay, Date nowDate, CardBasicInfo bean) {
		
		// 上次清零时间
		Date clearTime = bean.getClearTime();
		if (clearTime == null) { // 如果上次清零时间为空，则取simnode中状态!=0的激活时间
			List<Map<String, Object>> simnodeList = cardManageService.findSimnodesByStatusAndIccids(bean.getSn());
			if (simnodeList!=null && simnodeList.size()>0) {
				Map<String, Object> simnodeMap = simnodeList.get(0);
				String activeDate = ObjectUtils.toString(simnodeMap.get("activeDate"));
				if (StringUtils.isNotBlank(activeDate) && !"0000-00-00 00:00:00".equals(activeDate)) {
					try {
						clearTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(activeDate);
					} catch (ParseException e) {
						e.printStackTrace();
						logger.info("卡["+bean.getSn()+"]取激活时间出错!");
						return false;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		
		// 上次清零时间到现在间隔天数
		int days = Integer.valueOf(((nowDate.getTime() - clearTime.getTime()) / (86400000)) + "");
		if (days >= clearDay)
			return true;
		
		return false;
		
	}
	
}
