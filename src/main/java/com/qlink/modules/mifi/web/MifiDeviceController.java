package main.java.com.qlink.modules.mifi.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.uu.common.config.Global;
import com.uu.common.persistence.Page;
import com.uu.common.persistence.Parameter;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.IdGen;
import com.uu.common.utils.Socekt;
import com.uu.common.utils.StringUtils;
import com.uu.common.utils.excel.ExportExcel;
import com.uu.common.utils.excel.ImportExcel;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.entity.MifiBasicInfo;
import com.uu.modules.mifi.service.CardManageService;
import com.uu.modules.mifi.service.MifiDeviceService;
import com.uu.modules.mifi.service.MifiManageService;
import com.uu.modules.mifi.service.MifiOrderService;
import com.uu.modules.mifi.service.MifiTrafficService;
import com.uu.modules.om.entity.Region;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.om.service.RegionService;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.service.LogService;
import com.uu.modules.sys.utils.UserUtils;
import com.uu.modules.utils.Constants;

import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "${adminPath}/mifi/mifiDevice")
public class MifiDeviceController  extends BaseController {
	
	private static final String ALL = "ALL";
	
	private static final String HOMEFORBIDDEN = "homeForbidden";
	
	@Autowired
	private MifiDeviceService mifiDeviceService;
	
	@Autowired
	private MifiManageService mifiManageService;
	
	@Autowired
	private RegionService regionService;
	
	@Autowired
	private LogService logSerivce;
	
	@Autowired
	private MifiOrderService mifiOrderSerivce;
	
	@Autowired
	private MifiTrafficService mifiTrafficService;
	
	@Autowired
	private CardManageService cardManageService;
	
	@Autowired
	private ChannelService channelService;

	@RequiresPermissions("mifi:mifiDevice:view")
	@RequestMapping(value = "init")
	public String init(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
		HttpServletResponse response, Model model) {
		model.addAllAttributes(paramMap);
		
		// 归属地
		List<String[]> mccList = channelService.findAllMCC();
		model.addAttribute("mccList", mccList);
		
		User user = UserUtils.getUser();
		if (user.getChannelAdmin())
			return "channel/mifi/mifiDeviceList";
		
		return "modules/mifi/mifiDeviceList";
	}

	@RequiresPermissions("mifi:mifiDevice:view")
	@RequestMapping(value = "list")
	public String list(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
		HttpServletResponse response, Model model) {
		
		// 是否运营商
		User user = UserUtils.getUser();
		boolean isChannelAdmin = user.getChannelAdmin();
		if (isChannelAdmin)
			paramMap.put("sourceType", user.getChannelNameEn());// 只能查询运营商自己的数据
		
		Page<HashMap> page = mifiDeviceService.findMifiDeviceList(new Page<HashMap>(request, response), paramMap);
		model.addAttribute("page", page);
		model.addAllAttributes(paramMap);
		
		// 归属地
		List<String[]> mccList = channelService.findAllMCC();
		model.addAttribute("mccList", mccList);
		
		if (isChannelAdmin)
			return "channel/mifi/mifiDeviceList";
		
		return "modules/mifi/mifiDeviceList";
	}
	
	/**
	 * 修改页
	 * @Description 
	 * @author yuxiaoyu
	 * @date 2016年5月5日 上午10:23:27
	 */
	@RequiresPermissions("mifi:mifiDevice:edit")
	@RequestMapping("form")
	public String form(String id, Model model) {
		User user = UserUtils.getUser();
		if (user.getChannelAdmin()){
			return "error/403";
		}
		MifiBasicInfo mifiBasicInfo = mifiDeviceService.getMifiDevice(id);
		
		// 查询mifilist：TEST_IP,TEST_UPDATE_IP,SOFTSIM_TYPE,web_portal_flag
		Map<String, Object> map = mifiManageService.getMifiInfoBySn(mifiBasicInfo.getSn());
		if (map!=null) {
			mifiBasicInfo.setTestIp(ObjectUtils.toString(map.get("testIp")));
			mifiBasicInfo.setTestUpdateIp(ObjectUtils.toString(map.get("testUpdateIp")));
			String softsimType = ObjectUtils.toString(map.get("softsimType"));
			mifiBasicInfo.setSoftsimType(StringUtils.isBlank(softsimType) ? null : Integer.valueOf(softsimType));
			String webPortalFlag = ObjectUtils.toString(map.get("webPortalFlag"));
			mifiBasicInfo.setWebPortalFlag(StringUtils.isBlank(webPortalFlag) ? null : Integer.valueOf(webPortalFlag));
		}
		
		model.addAttribute("mifiBasicInfo", mifiBasicInfo);
		return "modules/mifi/mifiDeviceForm";
	}
	
	/**
	 * 保存
	 * @Description 
	 * @author yuxiaoyu
	 * @date 2016年5月5日 上午10:54:39
	 */
	@RequiresPermissions("mifi:mifiDevice:edit")
	@RequestMapping("save")
	@Transactional(readOnly = false)
	public String save(MifiBasicInfo mifiBasicInfo, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		User user = UserUtils.getUser();
		if (user.getChannelAdmin()){
			return "error/403";
		}
		mifiDeviceService.save(mifiBasicInfo);
		logSerivce.saveOperateLog(request, "[[MIFI管理 》设备管理 》MIFI设备管理]]-修改设备归属代理商，设备sn：[{}], 修改后设备归属代理商[SourceType]为：[{}]", mifiBasicInfo.getSn(), mifiBasicInfo.getSourceType());
		addMessage(redirectAttributes, "保存MIFI设备'" + mifiBasicInfo.getSn() + "'成功");
		return "redirect:" + Global.getAdminPath() + "/mifi/mifiDevice/init?repage";
	}
	
	/**
	 * 同步
	 * @Description 
	 * @author yuxiaoyu
	 * @date 2016年5月5日 下午2:09:03
	 */
	@RequiresPermissions("mifi:mifiDevice:sync")
	@RequestMapping(value = "sync")
	public String sync(MifiBasicInfo mifiBasicInfo, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		User user = UserUtils.getUser();
		if (user.getChannelAdmin()){
			return "error/403";
		}
		List<Object[]> mifiList = mifiDeviceService.getMifiList();
		if(!StringUtils.isEmpty(mifiList)){
			Date now = new Date();
			String userId = user.getId();
			boolean isFirstSelect = true;
			StringBuilder sb = new StringBuilder();
			sb.append("insert into mifi_basic_info(id, sn, imei, owner_type, source_type, status, in_time, in_user) ");
			String sn;
			for(Object[] mifi:mifiList){
				if(!isFirstSelect){
					sb.append(" union all ");
				}
				sn = new String((byte[])mifi[0]);
				sb.append("select ");
				sb.append("'").append(IdGen.uuid()).append("', ");
				sb.append("'").append(sn).append("', ");
				sb.append("'").append(sn).append("', ");
				sb.append("'").append(mifi[1]).append("', ");
				sb.append("'").append(mifi[2]).append("', ");
				sb.append("'1', '").append(DateUtils.formatDateTime(now)).append("', '").append(userId).append("' ");
				if(isFirstSelect){
					isFirstSelect = false;
				}
			}
			mifiDeviceService.updateAndSync(sb.toString());
		}
		addMessage(redirectAttributes, mifiList.size() + "条设备数据同步成功");
		return "redirect:" + Global.getAdminPath() + "/mifi/mifiDevice/init?repage";

	}

	@RequiresPermissions("mifi:mifiDevice:view")
	@RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(@RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "设备数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx"; 
			List<MifiBasicInfo> list = mifiDeviceService.findMifiDeviceListForExport(paramMap); 
    		new ExportExcel("设备数据", MifiBasicInfo.class).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出设备数据失败！失败信息："+e.getMessage());
			e.printStackTrace();
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/mifiDevice/init";
    }

	@RequiresPermissions("mifi:mifiDevice:import")
	@RequestMapping("import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "设备数据导入模板.xlsx";
			List<MifiBasicInfo> list = Lists.newArrayList();
			new ExportExcel("设备数据(录入数据前设置单元格格式为文本)", MifiBasicInfo.class, 2).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/mifiDevice/init";
    }
	
	@RequiresPermissions("mifi:mifiDevice:import")
	@RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<MifiBasicInfo> list = ei.getDataList(MifiBasicInfo.class);
			JSONObject obj = mifiDeviceService.importFile(list);
			addMessage(redirectAttributes, "已成功导入[" + obj.getString("successNum") + "]条设备数据" + obj.getString("failureMsg"));
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入设备数据失败!失败信息:" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/mifiDevice/init";
    }
	
	/**
	 * 
	 * @Description 设备归属地修改模板下载
	 * @param response
	 * @param redirectAttributes
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年5月19日 上午11:20:23
	 */
	@RequiresPermissions("mifi:mifiDevice:ownerMcc")
	@RequestMapping("download/template/ownerMcc")
    public String downloadOwnerMccTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "设备归属地修改模板.xlsx";
			ExportExcel ee = new ExportExcel("设备归属地数据", new String[] {"设备IMEI", "国家编号"});
			ee.write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/mifiDevice/init";
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
	@RequiresPermissions("mifi:mifiDevice:ownerMcc")
	@RequestMapping(value = "importOwnerMcc", method=RequestMethod.POST)
    public String importOwnerMccFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			ImportExcel ei = new ImportExcel(file, 1, 0);
			JSONObject obj = mifiDeviceService.importOwnerMccFile(ei);
			addMessage(redirectAttributes, "已成功修改[" + obj.getString("successNum") + "]条设备归属地" + obj.getString("failureMsg"));
		} catch (Exception e) {
			addMessage(redirectAttributes, "设备归属地修改失败!失败信息:" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/mifiDevice/init";
    }
	
	/**
	 * 
	 * @Description sim卡状态控制
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年6月6日 下午2:12:47
	 */
	@RequestMapping(value = "simStatusControllerPage")
	public String simStatusControllerPage(HttpServletRequest request, HttpServletResponse response, Model model) {

		return "modules/mifi/simStatusController";
		
	}
	
	/**
	 * 
	 * @Description mifi设备状态控制
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年6月6日 下午2:12:47
	 */
	@RequestMapping(value = "mifiStatusControllerPage")
	public String mifiStatusControllerPage(HttpServletRequest request, HttpServletResponse response, Model model) {

		return "modules/mifi/mifiStatusController";
		
	}
	
	/**
	 * 
	 * @Description sim卡状态控制
	 * @param simBankId
	 * @param simId
	 * @param status
	 * @param request
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年6月6日 下午2:22:26
	 */
	@RequestMapping(value = "simStatusController", produces="application/json;charset=UTF-8")
	@ResponseBody  
	public Map<String, String> simStatusController(@RequestParam String simBankId, @RequestParam String simId, 
			@RequestParam String status, @RequestParam String statusText, HttpServletRequest request) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		// 参数判断
		if (StringUtils.isBlank(simBankId)) {
			map.put("code", "-1");
			map.put("msg", "主控板编号不能为空");
			return map;
		}
		if (StringUtils.isBlank(simId)) {
			map.put("code", "-1");
			map.put("msg", "卡槽编号不能为空");
			return map;
		}
		if (StringUtils.isBlank(status)) {
			map.put("code", "-1");
			map.put("msg", "卡状态不能为空");
			return map;
		}
		
		Integer simBankIdInt = null;
		Integer simIdInt = null;
		Integer statusInt = null;
		try {
			simBankIdInt = Integer.valueOf(simBankId);
		} catch (NumberFormatException e) {
			map.put("code", "-1");
			map.put("msg", "输入主控板数据格式错误");
			return map;
		}
		try {
			simIdInt = Integer.valueOf(simId);
		} catch (NumberFormatException e) {
			map.put("code", "-1");
			map.put("msg", "输入卡槽编号数据格式错误");
			return map;
		}
		try {
			statusInt = Integer.valueOf(status);
		} catch (NumberFormatException e) {
			map.put("code", "-1");
			map.put("msg", "输入状态数据格式错误");
			return map;
		}
		// 如果是7，则将simnode表中的已使用流量清零
		if (statusInt == 7) {
			Map<String, Object> cardMap = cardManageService.findSimNode(simBankId, simId);
			if (cardMap != null) {
				String iccId = ObjectUtils.toString(cardMap.get("iccId"));
				if (StringUtils.isNotBlank(iccId)) {
					List<String> iccIdList= new ArrayList<String>();
					iccIdList.add(iccId);
					cardManageService.updateClearDataBySn(iccIdList);
				}
			}
		}
		// 通信
		Map<String, String> result = Socekt.simStatusController(simBankIdInt, simIdInt, statusInt);
		logSerivce.saveOperateLog(request, "[MIFI管理 》设备管理 》设备通信]-SIM卡状态控制，主控板编号：[{}]，卡槽编号：[{}]， 修改的状态：[{}]-[{}]，执行结果为：[{}]", simBankId, simId, status, statusText, result.get("msg"));
		return result;
		
	}
	
	/**
	 * 
	 * @Description 设备状态控制
	 * @param imei
	 * @param status
	 * @param request
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年6月6日 下午4:21:46
	 */
	@RequestMapping(value = "mifiStatusController", produces="application/json;charset=UTF-8")
	@ResponseBody  
	public Map<String, String> mifiStatusController(@RequestParam String imei, @RequestParam String status, @RequestParam String statusText, HttpServletRequest request) {
		
		Map<String, String> map = new HashMap<String, String>();

		// 参数判断
		if (StringUtils.isBlank(imei)) {
			map.put("code", "-1");
			map.put("msg", "设备不能为空");
			return map;
		}
		if (StringUtils.isBlank(status)) {
			map.put("code", "-1");
			map.put("msg", "设备状态不能为空");
			return map;
		}
		Integer statusInt = null;
		try {
			statusInt = Integer.valueOf(status);
		} catch (NumberFormatException e) {
			map.put("code", "-1");
			map.put("msg", "输入状态数据格式错误");
			return map;
		}
		
		@SuppressWarnings("unchecked")
		Map<String, Object> mifiNodeMap = mifiManageService.getSnMccInfo(imei);
		if (mifiNodeMap != null) {
			String ueId = ObjectUtils.toString(mifiNodeMap.get("ueId"));
			if (StringUtils.isNotBlank(ueId)) {
				Map<String, String> result = Socekt.mifiStatusController(Integer.valueOf(ueId), statusInt, imei);
				logSerivce.saveOperateLog(request, "[MIFI管理 》设备管理 》设备通信]-SIM卡状态控制，设备编号：[{}]，设备修改的状态：[{}]-[{}]，执行结果为：[{}]", imei, status, statusText, result.get("msg"));
				return result;
			}
		}

		map.put("code", "-1");
		map.put("msg", "设备未入库");
		logSerivce.saveOperateLog(request, "[MIFI管理 》设备管理 》设备通信]-SIM卡状态控制，设备编号：[{}]，设备修改的状态：[{}]-[{}]，执行结果为：[{}]", imei, status, statusText, map.get("msg"));
		return map;
		
	}
	
	/**
	 * 弹出框的区域列表页面
	 * @athor shuxin
	 * @date 2016年7月7日上午9:28:39
	 * @param sn
	 * @param map
	 * @return
	 * String 
	 */
	@RequiresPermissions("mifi:mifiDevice:usePlace")
	@RequestMapping("getCountry")
	public String getCountry(String sn, ModelMap model ){
		Map<String, Object> map = mifiDeviceService.getMifiListBySn(sn);
		if(map != null && map.size() > 0){
			String countryCodes = "";
			String mcc = map.get("mcc") == null ? "" : map.get("mcc").toString();
			List<Object[]> result = new ArrayList<Object[]>();
			if(mcc.equals(ALL)){ //页面上直接选择ALL
				result = getResult(countryCodes);
				model.put("ALL", ALL);
			} else if(mcc.equals(HOMEFORBIDDEN)){ //页面禁用的话，国家先不需要显示
				result = getResult(countryCodes);
				model.put("homeForbidden", HOMEFORBIDDEN);
			} else {
				List<String> coutryCodeList = regionService.findAllListByMcc(mcc);
				countryCodes = toString(coutryCodeList);
				result = getResult(countryCodes);
			}
			model.put("country", result);
		} else {
			model.put("error", "error");//没有要修改的数据
		}
		model.put("allow", map.get("allowed"));
		model.put("sn", sn);
		return "modules/mifi/countryForm";
	}
	
	/**
	 * 获取国家列表
	 * @athor shuxin
	 * @date 2016年7月8日下午3:01:41
	 * @param countryCodes
	 * @return
	 * List<Object[]> 
	 */
	private List<Object[]> getResult(String countryCodes){
		List<Region> list = regionService.findAllList();
		List<Object[]> result = new ArrayList<Object[]>();
		Object[] tempArr = null;
		for (Region region : list) {
			if(region.getCountryCodes().indexOf(",") != -1){
				String[] tempCode = region.getCountryCodes().split(",");
				String[] tempName = region.getCountryNames().split(",");
				for (int i = 0; i < tempName.length; i++) {
					tempArr = new Object[3];
					tempArr[0] = tempCode[i];
					tempArr[1] = tempName[i];
					if (include(countryCodes, tempArr[0].toString()))
						tempArr[2] = "1";
					else 
						tempArr[2] = "0";
					result.add(tempArr);
				}
			} else {
				tempArr = new Object[3];
				tempArr[0] = region.getCountryCodes();
				tempArr[1] = region.getCountryNames();
				if (include(countryCodes, tempArr[0].toString()))
					tempArr[2] = "1";
				else 
					tempArr[2] = "0";
				result.add(tempArr);
			}
		}
		return result;
	}
	
	/**
	 * 集合转字符串(xx,xx,xx.....)
	 * @athor shuxin
	 * @date 2016年7月8日下午3:01:59
	 * @param coutryCodeList
	 * @return
	 * String 
	 */
	private String toString(List<String> coutryCodeList) {
		StringBuffer codes = new StringBuffer();
		for (String code : coutryCodeList) {
			codes.append(code).append(",");
		}
		return codes.length() == 0 ? null : codes.substring(0, codes.lastIndexOf(",")).toString();
	}
	
	/**
	 * 数组转字符串('AE','AL')
	 * @athor shuxin
	 * @date 2016年7月8日下午3:02:23
	 * @param countryCodes
	 * @return
	 * String 
	 */
	private String toString(String[] countryCodes) {
		StringBuffer codes = new StringBuffer();
		if(countryCodes == null){
			return codes.toString();
		}
		for (int i = 0; i < countryCodes.length; i++) {
			codes.append("'").append(countryCodes[i]).append("'").append(",");
		}
		return codes.length() == 0 ? null : codes.substring(0, codes.lastIndexOf(",")).toString();
	}

	private boolean include(String countryCodes, String countryCode) {
		if (StringUtils.isBlank(countryCodes))
			return false;
		String[] countryCodeArr = countryCodes.split(",");
		for (String code : countryCodeArr) {
			if (code.equals(countryCode)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 修改设备使用地
	 * @athor shuxin
	 * @date 2016年7月8日下午1:26:06
	 * @param sn
	 * @param allow
	 * @param countryCode
	 * @return
	 * Boolean 
	 */
	@RequiresPermissions("mifi:mifiDevice:usePlace")
	@RequestMapping("updateMifilist")
	public @ResponseBody Boolean mifiListForm(HttpServletRequest request, String sn, String allow, String[] countryCode){
		Map<String, Object> map = mifiDeviceService.getMifiListBySn(sn);
		if(map.isEmpty()){
			return false;
		}
		String mcc = map.get("mcc") == null ? "" : map.get("mcc").toString();
		String allowed =  map.get("allowed") == null ? "" : map.get("allowed") .toString();
		if("0".equals(allow)){ //如果是禁用状态，直接更新数据库
			if(allow.equals(allowed)){ //参数没更新，不需要更新数据库
				return true;
			}
			mifiDeviceService.updateMifiListForMccAndAllowBySn(new Parameter(sn, allow, mcc));
			logSerivce.saveOperateLog(request, "[MIFI管理 》设备管理 》MIFI设备管理]-修改设备使用地操作信息：设备sn:[{}]，设备[UEALLOWED]原始值为：[{}]，修改后的[UEALLOWED]值为：[{}]", sn,allowed,allow);
			return true;
		}
		List<String> mccsList = regionService.findMccByCountryCode(toString(countryCode));
		if(mccsList.isEmpty()){
			if(allowed.equals(allow) && mcc.equals(toString(countryCode))){//表单提交过来的参数没有需要修改的,直接返回页面提示
				return true;
			} 
		} else {
			String mccsString = toString(mccsList);
			if(allowed.equals(allow) && mcc.equals(mccsString)){ //表单提交过来的参数没有需要修改的,直接返回页面提示
				return true;
			}
		}
		mifiDeviceService.updateMifiListForMccAndAllowBySn(new Parameter(sn, allow, toString(mccsList) == null ? countryCode : toString(mccsList)));
		logSerivce.saveOperateLog(request, "[MIFI管理 》设备管理 》MIFI设备管理]-修改设备使用地操作信息：设备sn:[{}]，[UEALLOWED]原始值为：[{}]，[UEALLOWEDMCC]原始值为：[{}]，修改后的[UEALLOWED]值为：[{}]，修改后的[UEALLOWEDMCC]值为：[{}]", sn,allowed,mcc,allow,toString(mccsList) == null ? countryCode[0] : toString(mccsList));
		return true;
	}
	
	/**
	 * 设备详细信息
	 * @athor shuxin
	 * @date 2016年7月15日下午4:34:54
	 * @param sn
	 * @param model
	 * @return
	 * String 
	 */
	@RequiresPermissions("mifi:mifiDevice:view")
	@RequestMapping("detail")
	public String mifiDetail(String sn, ModelMap model){
		if(!StringUtils.isNotBlank(sn)){
			model.put("tips", false); //前端提示没有设备信息
		}
		// 是否运营商
		User user = UserUtils.getUser();
		boolean isChannelAdmin = user.getChannelAdmin();
		List<Map<String, Object>> deviceList = mifiDeviceService.findMifiDeviceListBySn(sn); //获取设备信息
		model.put("mifiDevice", deviceList.get(0));
		List<Map<String, Object>> orderList = mifiOrderSerivce.findMifiOrderListByDsn(sn); //获取设备订单信息
		model.put("mifiOrder", orderList);
		if(isChannelAdmin){ 
			return "channel/mifi/mifiDetail";
		}
		//查找当前设备卡基本信息
		List<Map<String, Object>> mifiStatus = mifiTrafficService.findSimCardStatusByImei(sn);
		if(!mifiStatus.isEmpty()){
			Map<String, Object> temp = mifiStatus.get(0);
			Integer simBlankId = Integer.valueOf(temp.get("SIMBANKID").toString()) ;
			Integer simId =  Integer.valueOf(temp.get("SIMID").toString()) ;
			model.put("sim", cardManageService.findSIMBySimblankIdAndsimid(simBlankId, simId));
		} else { //没有sim卡
			model.put("sim", new HashMap<String, Object>());
		}
		return "modules/mifi/mifiDetail";
	}

	/**
	 * 
	 * @Description 获取设备位置分布图
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年8月29日 上午10:39:03
	 */
	@RequiresPermissions("mifi:mifiDevice:position")
	@RequestMapping(value = "devicePositionMap")
	public String devicePositionMap(HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/mifi/devicePositionMap";
	}
	
	/**
	 * 获取设备位置分布图(微软Bing Maps)
	 * @Description 
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yuxiaoyu
	 * @date 2016年12月20日 下午2:35:59
	 */
	@RequiresPermissions("mifi:mifiDevice:position")
	@RequestMapping(value = "devicePositionMapMicro")
	public String devicePositionMapMicro(HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/mifi/devicePositionMapMicro";
	}
	
	/**
	 * 
	 * @Description 获取设备位置JSON数据
	 * @param dateStr 时间戳（1，4，8，12）小时
	 * @param request
	 * @return Map<String,Object>  
	 * @author yifang.huang
	 * @date 2016年8月29日 上午10:47:14
	 */
	@RequestMapping(value = "positionJsonData", produces="application/json;charset=UTF-8")
	@ResponseBody  
	public Map<String, Object> positionJsonData(@RequestParam String dateStr, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 渠道编号(YOUYOUMOB渠道用户可以查看所有设备信息)
		User user = UserUtils.getUser();
		String channelCode = user.getChannelNameEn();
		channelCode = Constants.CHANNEL_DEFAULT_VALUE.equals(channelCode) ? null : channelCode;
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (StringUtils.isNotBlank(dateStr)) {
				// 起始时间
				Date endDate = new Date();
				// 结束时间
				int timeStamp = Integer.valueOf(dateStr);
				Calendar c = Calendar.getInstance();
				c.setTime(endDate);
				c.add(Calendar.HOUR, -timeStamp);
				Date startDate = c.getTime();
				// 位置信息
				List<Map<String, Object>> listMap = mifiDeviceService.fidndPositionByDate(channelCode, df.format(startDate), df.format(endDate));
				map.put("positions", listMap);
			} else {
				map.put("code", "-1");
				map.put("msg", "请选择时间段！");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取设备位置信息失败！", e);
			map.put("code", "-1");
			map.put("msg", "获取设备位置信息失败！");
			return map;
		}
		map.put("code", "1");
		map.put("dateStr", dateStr);
		return map;
	}

	/**
	 * 
	 * @Description 获取设备线路图
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年8月29日 上午10:39:03
	 */
	@RequiresPermissions("mifi:mifiDevice:line")
	@RequestMapping(value = "deviceLineMap")
	public String deviceLineMap(HttpServletRequest request, HttpServletResponse response, Model model) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.HOUR_OF_DAY, -1);
		model.addAttribute("startDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime()));
		model.addAttribute("endDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		return "modules/mifi/deviceLineMap";
	}
	
	/**
	 * 获取设备线路图(微软Bing Maps)
	 * @Description 
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yuxiaoyu
	 * @date 2016年12月20日 下午4:53:08
	 */
	@RequiresPermissions("mifi:mifiDevice:line")
	@RequestMapping(value = "deviceLineMapMicro")
	public String deviceLineMapMicro(HttpServletRequest request, HttpServletResponse response, Model model) {
		Calendar calendar = Calendar.getInstance();
		model.addAttribute("endDate", DateUtils.formatDateTime(calendar.getTime()));
		calendar.add(Calendar.HOUR_OF_DAY, -1);
		model.addAttribute("startDate", DateUtils.formatDateTime(calendar.getTime()));
		return "modules/mifi/deviceLineMapMicro";
	}
	
	/**
	 * 
	 * @Description 获取设备线路JSON数据
	 * @param imei 设备编号
	 * @param startDateStr 开始时间
	 * @param endDateStr 结束时间
	 * @param request
	 * @return Map<String,Object>  
	 * @author yifang.huang
	 * @date 2016年8月29日 上午10:58:37
	 */
	@RequestMapping(value = "lineJsonData", produces="application/json;charset=UTF-8")
	@ResponseBody  
	public Map<String, Object> lineJsonData(@RequestParam String imei, @RequestParam String startDateStr, 
			@RequestParam String endDateStr, HttpServletRequest request) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		// 渠道编号(YOUYOUMOB渠道用户可以查看所有设备信息)
		User user = UserUtils.getUser();
		String channelCode = user.getChannelNameEn();
		channelCode = Constants.CHANNEL_DEFAULT_VALUE.equals(channelCode) ? null : channelCode;
		
		try {
			
			if (StringUtils.isNotBlank(imei)) {
				// 时间格式转换
				List<Map<String, Object>> listMap = mifiDeviceService.fidndPositionByDate(channelCode, imei, startDateStr, endDateStr);
				map.put("lines", listMap);
			} else {
				map.put("code", "-1");
				map.put("msg", "请输入要查看的设备编号！");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取设备线路信息失败！", e);
			map.put("code", "-1");
			map.put("msg", "获取设备线路信息失败！");
			return map;
		}
		
		map.put("code", "1");
		return map;
		
	}

	/**
	 * 
	 * @Description 根据经纬度获取单个设备位置信息
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年8月29日 上午10:39:03
	 */
	@RequiresPermissions("mifi:mifiDevice:position")
	@RequestMapping(value = "devicePosition")
	public String devicePosition(HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/mifi/devicePosition";
	}
	
	/**
	 * 根据经纬度获取单个设备位置信息(微软Bing Maps)
	 * @Description 
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * @return String  
	 * @author yuxiaoyu
	 * @date 2016年12月20日 下午6:09:54
	 */
	@RequiresPermissions("mifi:mifiDevice:position")
	@RequestMapping(value = "devicePositionMicro")
	public String devicePositionMicro(HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/mifi/devicePositionMicro";
	}
}
