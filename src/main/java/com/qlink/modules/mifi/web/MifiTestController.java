/** 
 * @Package com.uu.modules.mifi.web 
 * @Description 
 * @author yifang.huang
 * @date 2016年5月24日 下午4:34:11 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.web;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uu.common.config.Global;
import com.uu.common.persistence.Page;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.condition.MifiTestCondition;
import com.uu.modules.mifi.entity.CardBasicInfo;
import com.uu.modules.mifi.entity.MifiTest;
import com.uu.modules.mifi.entity.SimCardType;
import com.uu.modules.mifi.service.CardManageService;
import com.uu.modules.mifi.service.MifiManageService;
import com.uu.modules.mifi.service.MifiOrderService;
import com.uu.modules.mifi.service.MifiTestService;
import com.uu.modules.mifi.service.SimCardTypeService;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.service.LogService;
import com.uu.modules.sys.utils.UserUtils;

/** 
 * @Description 测试设备与卡绑定 对外服务包实现
 * @author yifang.huang
 * @date 2016年5月24日 下午4:34:11 
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/mifitest")
public class MifiTestController extends BaseController {


	public static Logger logger = LoggerFactory.getLogger(MifiTestController.class);
	
	@Autowired
	private MifiTestService mifiTestService;
	
	@Autowired
	private MifiManageService mifiManageService;
	
	@Autowired
	private MifiOrderService mifiOrderService;
	
	@Autowired
	private CardManageService cardManageService;
	
	@Autowired
	private SimCardTypeService simCardTypeService;
	
	@Autowired
	private LogService logService;
	
	@ModelAttribute
	public MifiTest get(@RequestParam(required=false) Integer id) {
		
		if (id != null)
			return mifiTestService.get(id);
		
		return new MifiTest();
		
	}
	
	@RequiresPermissions("mifi:mifiTest:view")
	@RequestMapping(value = "")
	public String init(MifiTestCondition condtion, HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/mifi/mifiTestList";
	}
	
	@RequiresPermissions("mifi:mifiTest:view")
	@RequestMapping(value = "list")
	public String list(MifiTestCondition condtion, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<MifiTest> page = mifiTestService.find(new Page<MifiTest>(request, response), condtion); 
		model.addAttribute("page", page);
		return "modules/mifi/mifiTestList";
	}
	
	@RequiresPermissions("mifi:mifiTest:view")
	@RequestMapping(value = "form")
	public String form(MifiTest bean, Model model) {
		
		model.addAttribute("mifiTest", bean);
		
		return "modules/mifi/mifiTestForm";
		
	}
	
	/**
	 * 
	 * @Description 异步判断是否可以绑定
	 * @param request
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年12月12日 下午5:49:32
	 */
    @RequestMapping(value = "/ajaxCheck.json",produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> ajaxCheck(@RequestParam Map<String, Object> paramMap, 
			HttpServletRequest request, HttpServletResponse response, Model model) {
		
		Map<String, String> map = new HashMap<String, String>();
    	
		try {
			// 参数
			String imei = ObjectUtils.toString(paramMap.get("imei"));
			String simBankId = ObjectUtils.toString(paramMap.get("simBankId"));
			String simId = ObjectUtils.toString(paramMap.get("simId"));
			String startDateStr = ObjectUtils.toString(paramMap.get("startDate"));
			String endDateStr = ObjectUtils.toString(paramMap.get("endDate"));
			
			// 判断设备是否存在
			HashMap<String, String> mifi = mifiManageService.getMifilistBySn(imei);
			if (mifi == null) {
				map.put("status", "error");
				map.put("message", "设备[" + imei +"]未找到!");
				return map;
			}
			
			// 判断卡位置上是否有卡
			List<Map<String, Object>> simNodeList = mifiTestService.findSimNodeList(simBankId, simId, null);
			if (simNodeList==null || simNodeList.size()==0) {
				map.put("status", "error");
				map.put("message", "位置[simbankid:'" + simBankId +", simid:" + simId + "]没插卡!");
				return map;
			}
			Map<String, Object> simNodeMap = simNodeList.get(0);
			
			// 开始、结束时间判断
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date startDate = df.parse(startDateStr);
			Date endDate = df.parse(endDateStr);
			if (startDate!=null && endDate!=null) {
				if (endDate.before(startDate)) {
					map.put("status", "error");
					map.put("message", "失效时间不能早于生效时间!");
					return map;
				}
			}
			
			String errorMsg = "";
			int index = 1;
			// 根据设备编号和生效时间取订单
			List<Map<String, Object>> orderList = mifiOrderService.findOrderList(imei, startDateStr);
			if (orderList!=null && orderList.size()>0) {
				Map<String, Object> temp = orderList.get(0);
				String outOrderId = ObjectUtils.toString(temp.get("outOrderId"));
				String orderEndDateStr = ObjectUtils.toString(temp.get("endDate"));
				String allowedMcc = ObjectUtils.toString(temp.get("allowedMcc"));	// 订单允许的MCC
				if (StringUtils.isNotBlank(orderEndDateStr)) {
					// 如果有订单，失效时间不能比订单结束时间晚
					Date orderEndDate = df.parse(orderEndDateStr);
					if (endDate.after(orderEndDate)) {
						errorMsg += (index++) + ".绑定有效期内订单的结束时间是[" + df.format(orderEndDate)+ "],失效时间比订单结束时间晚<br />";
						map.put("endDateStr", df.format(orderEndDate));
					}
					// 如果有订单，订单的国家mcc至少有一个在绑定卡的类型的国家mcc内
					String iccid = ObjectUtils.toString(simNodeMap.get("iccid"));
					CardBasicInfo cardInfo = cardManageService.findByIccid(iccid);
					if (cardInfo != null) {
						String cardType = cardInfo.getType();
						SimCardType typeInfo = simCardTypeService.finByCardType(cardType);
						if (typeInfo != null) {
							String mcces = typeInfo.getMcc();			// 卡类型允许的MCC
							if (StringUtils.isNotBlank(mcces) && StringUtils.isNotBlank(allowedMcc)) {
								String[] mccArr = mcces.split(",");
								String[] allowedMccArr = allowedMcc.split(",");
								boolean include = false;
								for (String mcc1 : allowedMccArr) {
									for (String mcc2 : mccArr) {
										if(mcc1.equals(mcc2)) {
											include = true;
											break;
										}
									}
									if (include) 
										break;
								}
								if (!include) {
									errorMsg += (index++) + ".卡类型包含国家与订单[" + outOrderId + "]中的国家不匹配<br />";
								}
							}
						}
					}
				}
			}
			
			// 根据绑卡天数和卡的剩余流量判断一天是否有1g流量提示
			String dataSur = ObjectUtils.toString(simNodeMap.get("dataSur"));			// 剩余流量（G）
			dataSur.replace(",", "");				// 去掉科学计数法的','
			Integer dataSurInt = Integer.valueOf(dataSur);
			int days = this.getDays(startDate, endDate);
			if (days > dataSurInt) {
				errorMsg += (index++) + ".卡剩余流量["+dataSurInt+"G]平均一天不足1G<br />";
			}
			
			// 是否产生过流量提示
			String dataUsed = ObjectUtils.toString(simNodeMap.get("dataUsed"));
			Long dataUsedL = Long.valueOf(dataUsed);
			if (dataUsedL == 0) {
				errorMsg += (index++) + ".卡未产生流量<br />";
			}
			
			if (StringUtils.isNotBlank(errorMsg)) {
				map.put("status", "error");
				map.put("message", errorMsg);
				return map;
			}
			
			map.put("status", "success");
			map.put("message", "可以绑定!");
			return map;
		} catch (ParseException e) {
			e.printStackTrace();
			map.put("status", "error");
			map.put("message", "是否可绑定检测失败!");
			return map;
		}
		
	}

	@RequiresPermissions("mifi:mifiTest:edit")
	@RequestMapping(value = "save")//@Valid 
	public String save(MifiTest bean, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		boolean flag = true;
		Map<String, Object> oldTest = null;
		if(bean.getId() != null){
			flag = false;
			oldTest = mifiTestService.findById(bean.getId());
		}
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作!");
			return "redirect:"+Global.getAdminPath()+"/mifi/mifitest/?repage";
		}
		
		// 配置参数验证
		if (!beanValidator(model, bean)){
			return form(bean, model);
		}
		
		// 判断设备是否存在
		HashMap<String, String> mifi = mifiManageService.getMifilistBySn(bean.getImei());
		if (mifi == null) {
			addMessage(model, "设备[" + bean.getImei() +"]未找到!");
			return form(bean, model);
		}
		
		// 设备编号是否已绑定
		MifiTestCondition condition = new MifiTestCondition();
		condition.setNeId(bean.getId());
		condition.setEqImei(bean.getImei());
		List<MifiTest> list = mifiTestService.findListByCondition(condition);
		if (list!=null && list.size()>0) {
			addMessage(model, "设备[" + bean.getImei() +"]已绑定，请先删除绑定或直接修改原来绑定关系!");
			return form(bean, model);
		}
		
		// 卡位置是否已绑定
		condition = null;
		condition = new MifiTestCondition();
		condition.setNeId(bean.getId());
		condition.setEqSimBankId(bean.getSimBankId());
		condition.setEqSimId(bean.getSimId());
		list = mifiTestService.findListByCondition(condition);
		if (list!=null && list.size()>0) {
			addMessage(model, "卡位置[simbankid:'" + bean.getSimBankId() +", simid:" + bean.getSimId() + "]'已绑定，请先删除绑定或直接修改原来绑定关系!");
			return form(bean, model);
		}
		
		// 判断卡位置上是否有卡
		List<Map<String, Object>> simNodeList = mifiTestService.findSimNodeList(bean.getSimBankId(), bean.getSimId(), null);
		if (simNodeList==null || simNodeList.size()==0) {
			addMessage(model, "卡位置[simbankid:'" + bean.getSimBankId() +", simid:" + bean.getSimId() + "]'没插卡!");
			return form(bean, model);
		}
		
		// 开始、结束时间判断
		Date startDate = bean.getStartDate();
		Date endDate = bean.getEndDate();
		if (startDate!=null && endDate!=null) {
			if (endDate.before(startDate)) {
				addMessage(model, "失效时间不能早于生效时间!");
				return form(bean, model);
			}
		}
		
		// 添加创建人或者修改人
		User user = UserUtils.getUser();
		if (user != null)
			bean.setCreateBy(user.getLoginName());
		
		// 保存数据
		mifiTestService.saveOrUpdate(bean);
		if(flag){
			logService.saveOperateLog(request, "[MIFI管理 》设备管理 》设备绑定卡]-设备与卡绑定添加，设备编号：[{}]，simbankid：[{}]，simid：[{}]", bean.getImei(),bean.getSimBankId(),bean.getSimId());
		} else {
			StringBuffer buffer =new StringBuffer("[MIFI管理 》设备管理 》设备绑定卡]-设备与卡绑定修改，");
			buffer.append("原设备号：[{}]，修改后的设备号：[{}]，");
			buffer.append("原simbankid：[{}]，修改后的simbankid：[{}]，");
			buffer.append("原simid：[{}]，修改后的simid：[{}]");
			logService.saveOperateLog(request, buffer.toString(), oldTest.get("IMEI").toString(),bean.getImei(),oldTest.get("SIMBANKID").toString(),bean.getSimBankId(),oldTest.get("SIMID").toString(),bean.getSimId());
		}
		addMessage(redirectAttributes, "设备卡绑定信息保存成功");
		return "redirect:"+Global.getAdminPath()+"/mifi/mifitest/?repage";
		
	}
	
	@RequiresPermissions("mifi:mifiTest:edit")
	@RequestMapping(value = "delete")
	public String delete(Integer id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作!");
			return "redirect:"+Global.getAdminPath()+"/mifi/mifitest/?repage";
		}
		MifiTest mifiTest = mifiTestService.get(id);
		logService.saveOperateLog(request, "[MIFI管理 》设备管理 》设备绑定卡]-设备与卡绑定删除，删除的设备号为：[{}]", mifiTest.getImei());
		mifiTestService.delete(id);
		addMessage(redirectAttributes, "删除设备卡绑定信息成功");
		return "redirect:"+Global.getAdminPath()+"/mifi/mifitest/?repage";
	}
	
	/**
	 * 
	 * @Description 计算两日期之间的天数（超过一个小时算一天）
	 * @param startDate
	 * @param endDate
	 * @return int  
	 * @author yifang.huang
	 * @date 2016年5月13日 下午5:39:49
	 */
	private int getDays(Date startDate, Date endDate) {

		int hours = Integer.valueOf(((endDate.getTime() - startDate.getTime()) / 3600000) + "");
		
		if (hours <= 0)
			return 0;
		
		int days = hours / 24;
		if ((hours%24) > 0)
			days += 1;
		
		return days;
	}
	
}
