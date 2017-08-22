/** 
 * @Package com.uu.modules.mifi.web 
 * @Description 
 * @author yuxiaoyu
 * @date 2017年3月3日 下午2:59:57 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uu.common.config.Global;
import com.uu.common.persistence.Page;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.excel.ExportExcel;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.condition.MifiUsageRecordSegmentCondition;
import com.uu.modules.mifi.entity.MifiUsageRecordSegment;
import com.uu.modules.mifi.service.MifiTrafficService;
import com.uu.modules.mifi.service.MifiUsageRecordSegmentService;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.utils.DictUtils;
import com.uu.modules.sys.utils.UserUtils;

/** 
 * @Description 
 * @author yuxiaoyu
 * @date 2017年3月3日 下午2:59:57 
 */
@Controller
@RequestMapping(value = "${adminPath}/mifi/usageRecordSegment")
public class MifiUsageRecordSegmentController extends BaseController{
	public static Logger logger = LoggerFactory.getLogger(MifiUsageRecordSegmentController.class);

	@Autowired
	private MifiUsageRecordSegmentService recordService;

	@Autowired
	MifiTrafficService mifiTrafficService;
	
	@Autowired
	private ChannelService channelService;

	/**
	 * 列表页
	 * @Description 
	 * @param condition
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * @return String  
	 * @author yuxiaoyu
	 * @date 2017年3月6日 下午2:31:17
	 */
	@RequiresPermissions("mifi:usageRecord:view")
	@RequestMapping(value = "")
	public String init(MifiUsageRecordSegmentCondition condition, HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		// 是否运营商
		User user = UserUtils.getUser();
		boolean isChannelAdmin = user.getChannelAdmin();
		// 国家
		List<String[]> mccList = channelService.findAllMCC();
		model.addAttribute("mccList", mccList);
		if (isChannelAdmin) {
			return "channel/mifi/mifiUsageRecordSegmentList";
		}
		return "modules/mifi/mifiUsageRecordSegmentList";
	}
	@RequiresPermissions("mifi:usageRecord:view")
	@RequestMapping(value = "list")
	public String list(MifiUsageRecordSegmentCondition condition, HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		// 是否运营商
		User user = UserUtils.getUser();
		boolean isChannelAdmin = user.getChannelAdmin();
		if (isChannelAdmin) {
			condition.setSourceType(user.getChannelNameEn());
		}
		if (condition.getBeginDate() == null && condition.getBeginDate() == null) {
			condition.setBeginDate(DateUtils.getPreDate());
			condition.setEndDate(DateUtils.getPreDate());
		}
		Page<MifiUsageRecordSegment> page = recordService.findPage(new Page<MifiUsageRecordSegment>(request, response),
				condition);
		model.put("page", page);
		model.put("condition", condition);
		
		// 国家
		List<String[]> mccList = channelService.findAllMCC();
		model.addAttribute("mccList", mccList);
		
		if (isChannelAdmin) {
			return "channel/mifi/mifiUsageRecordSegmentList";
		}
		return "modules/mifi/mifiUsageRecordSegmentList";
	}

	/**
	 * 文件导出
	 * 
	 * @athor shuxin
	 * @date 2016年8月5日下午1:31:02
	 * @param condition
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return String
	 */
	@RequiresPermissions("mifi:usageRecord:export")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(MifiUsageRecordSegmentCondition condition, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			// 使用记录文档标题
		   String fileName = "设备使用记录【" + condition.getBeginDate() + "到" + (StringUtils.isNotBlank(condition.getEndDate()) ? condition.getEndDate() : DateUtils.getDate()) + "】.xlsx";

			// 是否运营商
			User user = UserUtils.getUser();
			boolean isChannelAdmin = user.getChannelAdmin();
			if (isChannelAdmin) {
				condition.setSourceType(user.getChannelNameEn());
			}

			// 使用记录列表
			Page<MifiUsageRecordSegment> page = recordService.findPage(new Page<MifiUsageRecordSegment>(), condition);
			List<MifiUsageRecordSegment> list = page.getList();

			ExportExcel ee = new ExportExcel("设备使用记录",
					new String[] { "设备序列号", "设备批次号", "代理商", "订单编号", "归属地区MCC值", "设备状态", "时间", "所在地区中文名", "所在地区英文名", "卡槽编号",
							"卡槽位置", "设备服务器连接状态", "设备电量", "主卡被网络拒绝原因", "主卡注册国家区域码", "主卡注册运营商编码", "主卡位置跟踪区域码", "主卡所处基站编号",
							"主卡接收信号强度", "副卡注册国家区域码", "副卡注册运营商编码", "副卡位置跟踪区域码", "副卡所处基站编号", "副卡接收信号强度", "外设连接数量",
							"设备使用总流量(M)", "21点以后总流量(M)", "费用" });

			Row row = null;
			for (MifiUsageRecordSegment mifiUsageRecord : list) {
				row = ee.addRow();
				ee.addCell(row, 0, mifiUsageRecord.getImei());
				ee.addCell(row, 1, mifiUsageRecord.getBath());
				ee.addCell(row, 2,
						mifiUsageRecord.getSourceType() + "|"
								+ DictUtils.getLabelByTableAndWhere("om_channel", "channel_name_en", "channel_name",
										" and del_flag = 0 ", mifiUsageRecord.getSourceType()));
				ee.addCell(row, 3, mifiUsageRecord.getOutOrderId());
				ee.addCell(row, 4, mifiUsageRecord.getMcc());
				mifiUsageRecord.setDeviceStatus(mifiUsageRecord.getUestatus() + "|"
						+ DictUtils.getDictLabel(mifiUsageRecord.getUestatus().toString(), "mifi_uestatus", "未知状态"));
				ee.addCell(row, 5, mifiUsageRecord.getDeviceStatus());
				ee.addCell(row, 6, mifiUsageRecord.getStampCreated());
				ee.addCell(row, 7, mifiUsageRecord.getCountryNameCn());
				ee.addCell(row, 8, mifiUsageRecord.getCountryNameEn());
				ee.addCell(row, 9, mifiUsageRecord.getSimBankId());
				ee.addCell(row, 10, mifiUsageRecord.getSimId());
				mifiUsageRecord.setDeviceLinkStatus(mifiUsageRecord.getNwstatus() + "|"
						+ DictUtils.getDictLabel(mifiUsageRecord.getNwstatus().toString(), "mifi_nwstatus", "未知状态"));
				ee.addCell(row, 11, mifiUsageRecord.getDeviceLinkStatus());
				ee.addCell(row, 12, mifiUsageRecord.getPowerInfo());
				ee.addCell(row, 13, mifiUsageRecord.getMainRejCause());
				ee.addCell(row, 14, mifiUsageRecord.getMainMcc());
				ee.addCell(row, 15, mifiUsageRecord.getMainMnc());
				ee.addCell(row, 16, mifiUsageRecord.getMainTac());
				ee.addCell(row, 17, mifiUsageRecord.getMainCallid());
				ee.addCell(row, 18, mifiUsageRecord.getMainRssi());
				ee.addCell(row, 19, mifiUsageRecord.getAdditionalMcc());
				ee.addCell(row, 20, mifiUsageRecord.getAdditionalMnc());
				ee.addCell(row, 21, mifiUsageRecord.getAdditionalTac());
				ee.addCell(row, 22, mifiUsageRecord.getAdditionalCellid());
				ee.addCell(row, 23, mifiUsageRecord.getAdditionalRssi());
				ee.addCell(row, 24, mifiUsageRecord.getDevices());
				ee.addCell(row, 25, mifiUsageRecord.getDatainfo());
				ee.addCell(row, 26, mifiUsageRecord.getDataAfter21());
				ee.addCell(row, 27, mifiUsageRecord.getCost() == null ? "" : mifiUsageRecord.getCost().toString());
				row = null;
			}
			ee.write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出设备使用记录失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/usageRecordSegment/?repage";
	}
}
