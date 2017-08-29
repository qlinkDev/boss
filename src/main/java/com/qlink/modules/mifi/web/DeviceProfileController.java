/** 
 * @Package com.uu.modules.mifi.web 
 * @Description 
 * @author yuxiaoyu
 * @date 2017年2月23日 上午11:34:15 
 * @version V1.0 
 */
package main.java.com.qlink.modules.mifi.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uu.common.utils.Encodes;
import com.uu.common.utils.IdGen;
import com.uu.common.utils.VodafoneUtils;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.service.DeviceProfileService;
import com.uu.modules.mifi.service.MifiManageService;
import com.uu.modules.sys.utils.DictUtils;
import com.uu.modules.utils.Constants;

/** 
 * @Description 设备消费级别设置
 * @author yuxiaoyu
 * @date 2017年2月23日 上午11:34:15 
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/deviceProfile")
public class DeviceProfileController extends BaseController {
	@Autowired
	private MifiManageService mifiManageService;
	@Autowired
	private DeviceProfileService deviceProfileService;

	/**
	 * 跳转至页面
	 * @Description 
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * @return String  
	 * @author yuxiaoyu
	 * @date 2017年2月23日 下午4:31:51
	 */
	@RequiresPermissions("mifi:mifiDevice:view")
	@RequestMapping(value = { "", "form" })
	public String form(HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/mifi/deviceProfileForm";
	}

	/**
	 * 消费级别查询
	 * @Description 
	 * @param deviceId
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * @return String  
	 * @author yuxiaoyu
	 * @date 2017年2月23日 下午4:32:16
	 */
	@RequiresPermissions("mifi:mifiDevice:view")
	@RequestMapping(value = "query")
	public @ResponseBody
	String query(@RequestParam String deviceId, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, String> deviceInfoMap = mifiManageService.getMifilistBySn(deviceId);
		if (null == deviceInfoMap || 0 == deviceInfoMap.size()) {
			return StringUtils.EMPTY;
		}
		String iccId = deviceInfoMap.get("vfIccId");
		String result = VodafoneUtils.getDeviceDetails(iccId);
		return result;
	}

	/**
	 * 设置消费级别
	 * @Description 
	 * @param deviceId
	 * @param customerServiceProfile
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * @return String  
	 * @author yuxiaoyu
	 * @date 2017年2月23日 下午4:42:34
	 */
	@RequiresPermissions("mifi:mifiDevice:edit")
	@RequestMapping(value = "save")
	public String save(@RequestParam String deviceId, @RequestParam String customerServiceProfile, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, String> deviceInfoMap = mifiManageService.getMifilistBySn(deviceId);
		if (null == deviceInfoMap || 0 == deviceInfoMap.size()) {
			addMessage(model, "设置失败，未找到对应设备");
			return "modules/mifi/deviceProfileForm";
		}
		String iccId = deviceInfoMap.get("vfIccId");
		String minorReturnCode = VodafoneUtils.setDeviceDetails(iccId, customerServiceProfile);
		if (StringUtils.isBlank(minorReturnCode)) {
			addMessage(model, "设置失败");
		} else if ("0000".equals(minorReturnCode)) {
			addMessage(model, "设置成功");
		} else {
			addMessage(model, "设置失败，返回码：" + minorReturnCode);
		}
		return "modules/mifi/deviceProfileForm";
	}

	/**
	 * 批量更新设备消费级别
	 * @Description 
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * @return String  
	 * @author yuxiaoyu
	 * @date 2017年2月28日 下午2:15:38
	 */
	@RequiresPermissions("mifi:mifiDevice:edit")
	@RequestMapping(value = "batch")
	public @ResponseBody
	String batch(HttpServletRequest request, HttpServletResponse response, Model model) {
		String logId = IdGen.uuid();
		if (VodafoneUtils.isBatchProcessing()) {
			return "系统正在执行批量操作，请稍后再试";
		}
		List<String> iccIdList = VodafoneUtils.getFilteredDeviceList(Constants.VODAFONE_CARD_LEVEL_5);
		if (null == iccIdList || 0 == iccIdList.size()) {
			return "没有可批量操作的数据";
		}
		logger.info(logId + " 查询出消费级别为5的设备" + iccIdList.size() + "台");
		// 塞班关岛，马代，马来西亚，澳门，澳洲，印度的订单对应设备副卡保持消费级别5级，其余降到3级
		String mccForViceCard5 = DictUtils
				.getDictValue(Constants.DICT_MCC_FOR_VODAFONE_CARD_5_LABEL, Constants.DICT_MCC_FOR_VODAFONE_CARD_5_TYPE, "");
		List<String> finalIccIdList = new ArrayList<String>();// 最终需要执行降级的设备
		List<Map<String, Object>> deviceMccList = deviceProfileService.getOrderAllowedMcc(iccIdList);
		if (null == deviceMccList || 0 == deviceMccList.size()) {// 所有设备当前没有已发货的订单，全部降级
			finalIccIdList = iccIdList;
		} else {
			String allowedMcc;
			boolean orderExists = false;
			for (String iccId : iccIdList) {// 判断设备是否需要降级
				orderExists = false;
				for (Map<String, Object> map : deviceMccList) {// 有订单的设备按照某一条件过滤
					if (!iccId.equals(new String((byte[]) map.get("vficcid")))) {
						continue;
					}
					// 找到了设备对应订单
					orderExists = true;
					allowedMcc = (String) map.get("allowed_mcc");
					if (!com.uu.common.utils.StringUtils.mccInclude(allowedMcc, mccForViceCard5)) {// 不在指定国家中，降级
						finalIccIdList.add(iccId);
					}
					break;
				}
				if (!orderExists) {// 设备当前没有已发货的订单，降级
					finalIccIdList.add(iccId);
				}
			}
		}
		// 调用沃达丰接口，把设备消费等级改为3
		logger.info(logId + " 开始批量更新设备消费级别");
		String minorReturnCode;
		VodafoneUtils.setBatchProcessing(true);
		int successCount = 0;
		try {
			for (String iccId : finalIccIdList) {
				minorReturnCode = VodafoneUtils.setDeviceDetails(iccId, Constants.VODAFONE_CARD_LEVEL_3);
				if ("0000".equals(minorReturnCode)) {
					successCount++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			VodafoneUtils.setBatchProcessing(false);
		}
		logger.info(logId + " " + finalIccIdList.size() + "台设备消费级别更新执行完毕，成功" + successCount + "条");
		return "批量操作执行完毕";
	}

	/**
	 * 导出
	 * @Description 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception 
	 * @return void  
	 * @author yuxiaoyu
	 * @date 2017年3月1日 下午3:09:00
	 */
	@RequiresPermissions("mifi:mifiDevice:view")
	@RequestMapping(value = "export")
	public void export(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception{
		String logId = IdGen.uuid();
		SXSSFWorkbook wb = new SXSSFWorkbook(500);
		Sheet sheet = wb.createSheet("Export");
		response.reset();
		response.setContentType("application/octet-stream; charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode("消费等级为5的设备列表.xlsx"));
		
		List<String> iccIdList = VodafoneUtils.getFilteredDeviceList(Constants.VODAFONE_CARD_LEVEL_5);
		if (null == iccIdList || 0 == iccIdList.size()) {
			wb.write(response.getOutputStream());
			return;
		}
		logger.info(logId + " 查询出消费级别为5的设备" + iccIdList.size() + "台");
		List<Map<String, Object>> deviceSnList = deviceProfileService.getDeviceSn(iccIdList);
		logger.info(logId + " 查询出对应的设备号" + deviceSnList.size() + "条");
		if (null == deviceSnList || 0 == deviceSnList.size()) {// 所有设备当前没有已发货的订单，全部降级
			wb.write(response.getOutputStream());
			return;
		} 
		
		int rownum = 0;
		Row row = sheet.createRow(rownum++);
		Cell cell = row.createCell(0);
		cell.setCellValue("iccid");
		cell = row.createCell(1);
		cell.setCellValue("设备编号");
		
		for(Map<String, Object> map : deviceSnList){
			row = sheet.createRow(rownum++);
			cell = row.createCell(0);
			cell.setCellValue(new String((byte[]) map.get("vficcid")));
			cell = row.createCell(1);
			cell.setCellValue(new String((byte[]) map.get("imei_6200")));
		}
		
		wb.write(response.getOutputStream());
	}
}
