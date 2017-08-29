package main.java.com.qlink.modules.mifi.web;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uu.common.config.Global;
import com.uu.common.persistence.Page;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.MailBody;
import com.uu.common.utils.MailUtils;
import com.uu.common.utils.excel.ExportExcel;
import com.uu.common.utils.mail.MailThread;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.entity.DeviceMonitorDetail;
import com.uu.modules.mifi.entity.MifiBasicInfo;
import com.uu.modules.mifi.service.CardMonitorService;
import com.uu.modules.mifi.service.MifiManageService;
import com.uu.modules.mifi.service.MifiTrafficService;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.om.service.PriceService;
import com.uu.modules.sys.condition.NoticeReceiveCondition;
import com.uu.modules.sys.entity.NoticeReceive;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.service.NoticeReceiveService;
import com.uu.modules.sys.service.SystemService;
import com.uu.modules.sys.utils.DictUtils;
import com.uu.modules.sys.utils.UserUtils;
import com.uu.modules.utils.Constants;

@Controller
@RequestMapping(value = "${adminPath}/mifi/mifiStatus")
public class MifiStatusController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(MifiStatusController.class);

	public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	MifiTrafficService mifiTrafficService;
	
	@Autowired
	private MifiManageService mifiManageService;
	
	@Autowired
	private NoticeReceiveService noticeReceiveService;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private ChannelService channelService;
	
	@Autowired
	private PriceService priceService;
	
	@Autowired
	private CardMonitorService cardMonitorService;

	@RequestMapping(value = { "list", "" })
	public String list(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 是否运营商
		User user = UserUtils.getUser();
		boolean isChannelAdmin = user.getChannelAdmin();
		if (isChannelAdmin) {
			paramMap.put("sourceType", user.getChannelNameEn());
			paramMap.put("isChannelAdmin", isChannelAdmin);
		}
		if (paramMap.containsKey("initTag")) {// 无奈啊,否则默认执行此方法
			Page<HashMap> page = mifiTrafficService.findMifiStatusList(new Page<HashMap>(request, response), paramMap);
			model.addAttribute("page", page);
		} else {
			paramMap.put("beginDate", DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
			paramMap.put("endDate", DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
		}
		model.addAllAttributes(paramMap);
		List<String[]> mccList = channelService.findAllMCC();
		model.addAttribute("mccList", mccList);
		return "modules/mifi/mifiStatusList";
	}

	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "设备状态数据" + paramMap.get("beginDate") + ".xlsx";
			Page<Object[]> _page = new Page<Object[]>();
			Page<Object[]> page = mifiTrafficService.exportMifiStatusList(_page, paramMap);
			ExportExcel ee = new ExportExcel("设备状态数据",
					new String[] { "设备序列号", "代理商", "归属地区MCC值", "设备状态", "时间", "所在地区中文名", "所在地区英文名", "卡槽编号", "卡槽位置",
							"设备服务器连接状态", "设备电量", "主卡被网络拒绝原因", "主卡注册国家区域码", "主卡注册运营商编码", "主卡位置跟踪区域码", "主卡所处基站编号",
							"主卡接收信号强度", "副卡注册国家区域码", "副卡注册运营商编码", "副卡位置跟踪区域码", "副卡所处基站编号", "副卡接收信号强度", "外设连接数量",
							"设备使用总流量(M)" });
			List<Object[]> list = page.getList();
			List<HashMap> sourceList = DictUtils.getListByTableAndWhere("om_channel", "channel_name_en", "channel_name",
					" and del_flag = 0 ");
			for (int i = 0; i < list.size(); i++) {
				Row row = ee.addRow();
				Object[] objs = list.get(i);
				for (int j = 0; j < objs.length; j++) {
					Object obj = null;
						if (objs[j] != null) {
						/*if (i == 0) {
							logger.debug("------j:" + j + ",className:" + objs[j].getClass().getName());
						}*/
						if (objs[j] instanceof byte[]) {
							obj = new String((byte[]) objs[0], "utf-8");
						} else if (objs[j] instanceof Byte) {
							obj = Byte.toString((Byte) objs[j]);
							if (j == 3) {
								obj = obj + "|"
										+ DictUtils.getDictLabel(Byte.toString((Byte) objs[j]), "mifi_uestatus", "未知状态");
							} else if (j == 9) {
								obj = obj + "|"
										+ DictUtils.getDictLabel(Byte.toString((Byte) objs[j]), "mifi_nwstatus", "未知状态");
							}
						} else if (objs[j] instanceof BigInteger) {
							obj = ((BigInteger) objs[j]).longValue();
						} else if (objs[j] instanceof BigDecimal) {
							obj = ((BigDecimal) objs[j]).toString();
						} else {
							obj = objs[j];
							if (j == 1) {
								for (int x = 0; x < sourceList.size(); x++) {
									HashMap map = sourceList.get(x);
									if (objs[j].equals((String) map.get("channel_name_en"))) {
										obj = obj + "|" + ((String) map.get("channel_name"));
										break;
									}
								}
							}
						}
					}
					ee.addCell(row, j, obj);
				}
			}
			ee.write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "导出设备状态数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/mifiStatus/?repage";
	}
	
	/**
	 * 
	 * @Description 使用记录导出
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年6月14日 上午10:27:45
	 */
	@RequiresPermissions("mifi:mifiStatus:export")
	@RequestMapping(value = "export2", method = RequestMethod.POST)
	public String exportFile2(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		
		try {

			// 使用记录文档标题
			String fileName = "设备使用记录" + paramMap.get("beginDate") + ".xlsx";
			
			// 使用记录列表
			Page<Object[]> page = mifiTrafficService.exportMifiStatus4List(new Page<Object[]>(), paramMap);
			List<Object[]> list = page.getList();
			
			// 渠道列表
			List<Channel> channelList = channelService.findChannelList();
			
			// 查询费用
			Map<String, String> mccPriceMap = getMccPriceMap(channelList);
			
			// 开始时间结束时间用于流量查询
			String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
			String endDate = ObjectUtils.toString(paramMap.get("endDate"));
			beginDate = StringUtils.isNotBlank(beginDate) ? (beginDate+" 00:00:00") : beginDate;
			endDate = StringUtils.isNotBlank(endDate) ? (endDate+" 23:59:59") : endDate;
			
			ExportExcel ee = new ExportExcel("设备使用记录", new String[] {"设备序列号", "代理商", "订单编号", "设备状态", "时间",
					"卡槽编号", "卡槽位置", "设备服务器连接状态", "设备电量", "主卡被网络拒绝原因", "主卡注册国家区域码", "主卡注册运营商编码", "主卡位置跟踪区域码", "主卡所处基站编号", 
					"主卡接收信号强度", "副卡注册国家区域码", "副卡注册运营商编码", "副卡位置跟踪区域码", "副卡所处基站编号", "副卡接收信号强度", "外设连接数量", "设备批次号",
					"费用", "MCC", "国家编号", "国家中文名", "国家英文名", "流量(M)"});
			
			// 循环使用的变量
			long flow = 0l;
			String sourceTypeTemp = "", mcces = "", mcc="", price = "";
			
			for (int i = 0; i < list.size(); i++) {
				Row row = ee.addRow();
				Object[] objs = list.get(i);
				int len = objs.length;
				for (int j = 0; j < objs.length; j++) {
					Object obj = null;
					if (objs[j] != null) {
						if (j == 1)
							sourceTypeTemp = ObjectUtils.toString(objs[j]);
						if (j == 22)
							mcces = ObjectUtils.toString(objs[j]);
						if (objs[j] instanceof byte[]) {
							obj = new String((byte[]) objs[0], "utf-8");
						} else if (objs[j] instanceof Byte) {
							obj = Byte.toString((Byte) objs[j]);
							if (j == 3) {
								obj = obj + "|" + DictUtils.getDictLabel(Byte.toString((Byte) objs[j]), "mifi_uestatus", "未知状态");
							} else if (j == 7) {
								obj = obj + "|" + DictUtils.getDictLabel(Byte.toString((Byte) objs[j]), "mifi_nwstatus", "未知状态");
							}
						} else if (objs[j] instanceof BigInteger) {
							obj = ((BigInteger) objs[j]).longValue();
						} else if (objs[j] instanceof BigDecimal) {
							obj = ((BigDecimal) objs[j]).toString();
						} else {
							obj = objs[j];
							if (j == 1) {
								for (Channel channel : channelList) {
									if (objs[j].equals(channel.getChannelNameEn())) {
										obj = obj + "|" + channel.getChannelName();
										break;
									}
								}
							}
						}
					}
					if (j < (len -1))
						ee.addCell(row, j, obj);
				}
				// 费用（渠道商没有取youyoumob的价格）、使用国家信息
				if (StringUtils.isNotBlank(mcces)) {
					// 取出价格最高的mcc
					String[] mccArr = mcces.split(",");
					if (mccArr.length == 1) {
						mcc = mccArr[0];
						price = mccPriceMap.get(sourceTypeTemp + "#" + mcc);
						price = StringUtils.isBlank(price) ? mccPriceMap.get(Constants.CHANNEL_DEFAULT_VALUE + "#" + mcc) : price;
					} else {
						String priceStr = "";
						Double priceDou = 0.0;
						// 过滤掉设备ownerMcc
						Map<String, String> mifiMap = mifiManageService.getMifilistBySn(ObjectUtils.toString(objs[0]));
						String ownerMcc = mifiMap.get("ownerMcc");
						for (String str : mccArr) {
							if (StringUtils.isNotBlank(ownerMcc) && ownerMcc.equals(str))
								continue;
							priceStr = mccPriceMap.get(sourceTypeTemp + "#" + str);
							priceStr = StringUtils.isBlank(priceStr) ? mccPriceMap.get(Constants.CHANNEL_DEFAULT_VALUE + "#" + str) : priceStr;
							if (StringUtils.isNotBlank(priceStr) && priceDou < Double.valueOf(priceStr)) {
								priceDou = Double.valueOf(priceStr);
								mcc = str;
							}
						}
						price = String.valueOf(priceDou);
					}
					ee.addCell(row, len-1, (Object) price);	// 费用
					
					// 使用国家信息
					ee.addCell(row, len, (Object) mcc);
					HashMap<String, String> countryMap = cardMonitorService.findCountryByMcc(mcc);
					if (countryMap != null) {
						ee.addCell(row, len+1, (Object) countryMap.get("countryCode"));
						ee.addCell(row, len+2, (Object) countryMap.get("countryName"));
						ee.addCell(row, len+3, (Object) countryMap.get("countryNameEn"));
					}
				}
				sourceTypeTemp = "";
				mcces = "";
				mcc = "";
				price = "";
				
				// 统计设备流量(从simcardstatus表统计设备使用流量)
				String ueid = mifiManageService.getDeviceUeid(ObjectUtils.toString(objs[0]));
				if (StringUtils.isNotBlank(ueid)) {
					flow = mifiManageService.getDeviceFlow(ueid, ObjectUtils.toString(objs[23]), beginDate, endDate);
				}
				ee.addCell(row, len+4, (Object) flow);
				flow = 0l;
				
			}
			ee.write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出设备使用记录失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/mifiStatus/?repage";
	}
	
	// 组装渠道的mcc对应价格map<sourceType#mcc, price>
	private Map<String, String> getMccPriceMap(List<Channel> channelList) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		for (Channel channel : channelList) {
			String sourceType = channel.getChannelNameEn();
			List<Map<String, Object>> mccPirceMapList = priceService.findMccPriceMapList(channel.getId());
			if (mccPirceMapList!=null && mccPirceMapList.size()>0) {
				for (Map<String, Object> mccPriceMap : mccPirceMapList) {
					String price = ObjectUtils.toString(mccPriceMap.get("price"));
					String mcces = ObjectUtils.toString(mccPriceMap.get("mcces"));
					String[] mccArr = mcces.split(",");
					for (String mcc : mccArr) {
						map.put(sourceType + "#" + mcc, price);
					}
				}
			}
		}
		
		return map;
	}
	
	/**
	 * 
	 * @Description 设备状态统计页面
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年5月26日 上午11:19:41
	 */
	@RequiresPermissions("mifi:mifiStatus:view")
	@RequestMapping(value = "statusCountPage")
	public String statusCountPage(HttpServletRequest request, HttpServletResponse response, Model model) {

		return "modules/mifi/mifiStatusCount";
		
	}
	
	/**
	 * 
	 * @Description 设备状态统计
	 * @param startDateStr
	 * @param endDateStr
	 * @param request
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月26日 上午11:19:59
	 */
	@RequiresPermissions("mifi:mifiStatus:edit")
	@RequestMapping(value = "statusCount", produces="application/json;charset=UTF-8")
	@ResponseBody  
	public Map<String, String> statusCount(@RequestParam String dateStr, String sourceType, HttpServletRequest request) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		try {
			
			// 时间判断
			if (StringUtils.isBlank(dateStr)) {
				map.put("code", "-1");
				map.put("msg", "统计日期不能为空");
				return map;
			}
			
			// 循环设备状态信息
			List<MifiBasicInfo> mifiList = mifiManageService.findMifiBasicInfoList(sourceType); // 设备列表
			map = countDeviceStatus(mifiList, dateStr);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设备状态统计失败", e);
			map.put("code", "-1");
			map.put("msg", "设备状态统计失败");
			return map;
		}
		
		return map;
		
	}
	
	/**
	 * 
	 * @Description 根据mcc获取国家名称
	 * @param mcc
	 * @param request
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年9月5日 上午11:35:06
	 */
	@RequestMapping(value = "getCountryNameByMcc", produces="application/json;charset=UTF-8")
	@ResponseBody  
	public Map<String, String> getCountryNameByMcc(@RequestParam String mcc, HttpServletRequest request) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		try {
			
			String countryName = mifiTrafficService.getCountryNameByMcc(mcc);
			if (StringUtils.isBlank(countryName)) {
				map.put("code", "-1");
				map.put("msg", mcc + "未找到对应国家");
			} else {
				map.put("code", "1");
				map.put("msg", "获取国家名称");
				map.put("countryName", countryName);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取国家名称失败", e);
			map.put("code", "-1");
			map.put("msg", "获取国家名称失败");
			return map;
		}
		
		return map;
		
	}
	
	/**
	 * 
	 * @Description 根据设备监控状态信息
	 * @param mifiList
	 * @param monitor 
	 * @return void  
	 * @author yifang.huang
	 * @throws ParseException 
	 * @date 2016年5月20日 下午5:10:44
	 */
	private synchronized Map<String, String> countDeviceStatus(List<MifiBasicInfo> mifiList, String dateStr) throws ParseException {

		Map<String, String> map = new HashMap<String, String>();
		
		// 其它状态到4的时间
		String statusEq0 = DictUtils.getDictValue(Constants.DICT_DEVICE_MONITOR_STATUS_EQ0_LABEL, Constants.DICT_DEVICE_MONITOR_STATUS_EQ0_TYPE, "10");
		// 4到其它状态的时间
		String statusGt0 = DictUtils.getDictValue(Constants.DICT_DEVICE_MONITOR_STATUS_GT0_LABEL, Constants.DICT_DEVICE_MONITOR_STATUS_GT0_TYPE, "65");
		int stampEq0 = Integer.valueOf(statusEq0);
		int stampGt0 = Integer.valueOf(statusGt0);

		// 邮件内容
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		String warningInfo = "";
		for (MifiBasicInfo mifi : mifiList) {
			
			String imei = mifi.getImei();	
			List<Map<String, String>> statusList = mifiTrafficService.findMifiStatusList(imei, dateStr, dateStr);
			if (statusList!=null && statusList.size()>0) {
				// 联网时长、所在国家、关机时间
				Map<String, Object> resultMap = mifiTrafficService.countDeviceStatus(statusList, stampGt0);
				// 设备编号
				resultMap.put("imei", imei);
				// 流量
				resultMap.put("dataInfo", mifiTrafficService.getMaxDataInfo(imei, dateStr, dateStr));
				// 异常
				List<DeviceMonitorDetail> dmdList = mifiTrafficService.checkDeviceStatus(statusList, stampEq0, stampGt0);
				if (dmdList!=null && dmdList.size()>0) {
					for (DeviceMonitorDetail detail : dmdList) {
						warningInfo += "#" + (detail.getPreStatus() + "(" + df.format(detail.getPreHappenDate()) +  ")~" 
								+ detail.getNextStatus() + "(" + df.format(detail.getNextHappenDate()) +  ")");
					}
					if (StringUtils.isNotBlank(warningInfo)) {
						warningInfo = warningInfo.substring(1);
						warningInfo = warningInfo.replace("#", "<br />");
					}
					resultMap.put("warningInfo", warningInfo);
					warningInfo = "";
				}
				list.add(resultMap);
			}
		}
		
		// 没有数据
		if (list.size()==0) {
			map.put("code", "-1");
			map.put("msg", "统计结果为空");
			return map;
		}
		
		// 按国家排序
		Collections.sort(list, new countResultComparator());
		
		// 发信息
		map = sendMessage(list, dateStr);
		
		// 给渠道商发邮件
		sendMailToChannel(list, dateStr);
		
		return map;
	}
	
	/**
	 * 
	 * @Description 发送信息
	 * @param monitor 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年5月23日 下午1:40:12
	 */
	private Map<String, String> sendMessage(List<Map<String, Object>> result, String date) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		// 设备统计信息接收邮箱
		NoticeReceiveCondition condition = new NoticeReceiveCondition();
		condition.setEqType("DEVICE_COUNT");
		List<NoticeReceive> list = noticeReceiveService.findListByCondition(condition);
		if (list.size()==0) {
			map.put("code", "-1");
			map.put("msg", "设备统计信息接收邮箱未设置");
			return map;
		}
		
		NoticeReceive nr = list.get(0);
		// 邮件发送
		String emails = nr.getEmails();
		if (StringUtils.isNotBlank(emails)) {
			// 邮件内容
			String msgText = MailBody.deviceCount(result);
			MailThread mThread = new MailThread("设备运行状态统计信息["+ date +"]", "[游友移动]", msgText, emails);
			mThread.start();
		}
		
		map.put("code", "1");
		map.put("msg", "统计成功，邮件已发送！");
		return map;
	}
	
	/**
	 * 
	 * @Description 设备运行状态统计信息给渠道商发邮件
	 * @param list 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年5月25日 上午11:32:54
	 */
	private void sendMailToChannel(List<Map<String, Object>> result, String date) {
		
		Map<String, List<Map<String, Object>>> channelStatusCountMap = new HashMap<String, List<Map<String, Object>>>();
		String sourceType = null;
		List<Map<String, Object>> tempList = null;
		for (Map<String, Object> statusCount : result) {
			Map<String, String> mifi = mifiManageService.getMifilistBySn(ObjectUtils.toString(statusCount.get("imei")));
			if (mifi!=null && "1".equals(mifi.get("ownerType"))) {
				
				sourceType = mifi.get("sourceType");
				tempList = channelStatusCountMap.get(sourceType);
				if (tempList==null || tempList.size()==0) {
					tempList = new ArrayList<Map<String, Object>>();
				}
				tempList.add(statusCount);
				channelStatusCountMap.put(sourceType, tempList);
				
				tempList = null;
			}
		}
		
		// 分别给渠道商发邮件
		for (Map.Entry<String, List<Map<String, Object>>> entry : channelStatusCountMap.entrySet()) {
			User user = systemService.getUserByChannelNameEn(entry.getKey());
			if (user!=null && StringUtils.isNotBlank(user.getEmail())) {
				
				// 邮件发送
				String emails = user.getEmail();// 系统配置邮箱
				// 邮件内容
				String msgText = MailBody.deviceCountForChannel(entry.getValue());
				MailThread mThread = new MailThread("设备运行状态统计信息["+ date +"]", "[游友移动]", msgText, emails);
				mThread.start();
				
			}
		}
		
	}
	
	// 按国家名排序
	class countResultComparator implements Comparator<Map<String, Object>> {

		@Override
		public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
			String country0 = ObjectUtils.toString(arg0.get("country"));
			String country1 = ObjectUtils.toString(arg1.get("country"));
			return compare(country0, country1);
		}

		public int compare(String o1, String o2) {

			String s1 = (String) o1;
			String s2 = (String) o2;
			int len1 = s1.length();
			int len2 = s2.length();
			int n = Math.min(len1, len2);
			char v1[] = s1.toCharArray();
			char v2[] = s2.toCharArray();
			int pos = 0;

			while (n-- != 0) {
				char c1 = v1[pos];
				char c2 = v2[pos];
				if (c1 != c2) {
					return c1 - c2;
				}
				pos++;
			}
			return len1 - len2;
		} 
		
	}
	
}
