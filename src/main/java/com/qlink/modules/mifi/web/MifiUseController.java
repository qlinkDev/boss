/** 
 * @Package com.uu.modules.mifi.web 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月31日 上午10:06:21 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.web;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uu.common.web.BaseController;
import com.uu.modules.mifi.condition.TestDeviceCondition;
import com.uu.modules.mifi.entity.DeviceBoot;
import com.uu.modules.mifi.entity.TestDevice;
import com.uu.modules.mifi.service.DeviceBootService;
import com.uu.modules.mifi.service.MifiManageService;
import com.uu.modules.mifi.service.MifiOrderService;
import com.uu.modules.mifi.service.TestDeviceService;
import com.uu.modules.om.condition.AdvertisingCondition;
import com.uu.modules.om.condition.ConsumeRecordCondition;
import com.uu.modules.om.condition.PriceCondition;
import com.uu.modules.om.entity.Advertising;
import com.uu.modules.om.entity.AdvertisingItem;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.entity.ConsumeRecord;
import com.uu.modules.om.entity.ConsumeRecord.RecordType;
import com.uu.modules.om.entity.ConsumeRecord.Status;
import com.uu.modules.om.entity.Price;
import com.uu.modules.om.entity.Region;
import com.uu.modules.om.service.AdvertisingService;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.om.service.ConsumeRecordService;
import com.uu.modules.om.service.PriceService;
import com.uu.modules.om.service.RegionService;
import com.uu.modules.om.web.ConsumeRecordFrontController;
import com.uu.modules.user.entity.UserBasicInfo;
import com.uu.modules.user.service.UserBasicInfoService;
import com.uu.modules.utils.Constants;

/** 
 * @Description 设备使用 控制器
 * @author yifang.huang
 * @date 2016年3月31日 上午10:06:21 
 */
@Controller
@RequestMapping(value = "${frontPath}/mifi/use")
public class MifiUseController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(ConsumeRecordFrontController.class);
	
	@Autowired
	private MifiManageService mifiManageService;
	
	@Autowired
	private MifiOrderService mifiOrderService;
	
	@Autowired
	private ChannelService channelService;
	
	@Autowired
	private RegionService regionService;
	
	@Autowired
	private PriceService priceService;
	
	@Autowired
	private UserBasicInfoService userService;
	
	@Autowired
	private ConsumeRecordService consumeRecordService;
	
	@Autowired
	private TestDeviceService testDeviceService;
	
	@Autowired
	private DeviceBootService deviceBootService;
	
	@Autowired
	private AdvertisingService advertisingService;
	
	// 缓存设备开机记录
	private static Map<String, String> recordMap = new HashMap<String, String>();
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 
	 * @Description 设备开机使用（游友专车）
	 * @param mcc 使用国家MCC
	 * @param sn 设备编号
	 * @param mac 连网设备MAC地址
	 * @param flowUseRate 流量使用率
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年11月22日 下午1:36:33
	 */
	@RequestMapping(value = "boot")
	public String boot(@RequestParam Map<String, Object> paramMap, 
			HttpServletRequest request, HttpServletResponse response, Model model) {
		
		// 参数
		String mcc = ObjectUtils.toString(paramMap.get("mcc"));
		String sn = ObjectUtils.toString(paramMap.get("sn"));
		String mac = ObjectUtils.toString(paramMap.get("mac"));
		String flowUseRate = ObjectUtils.toString(paramMap.get("flowUseRate"));
		if (StringUtils.isBlank(mcc)) {
	        model.addAttribute("code", "error_mifi");					// 设备未入库标识
	        model.addAttribute("msg", "参数'mcc'不能为空");
			return "/WEB-INF/views/app/use/adv_default_fail.jsp";
		}
		if (StringUtils.isBlank(sn)) {
	        model.addAttribute("code", "error_mifi");					// 设备未入库标识
	        model.addAttribute("msg", "参数'sn'不能为空");
			return "/WEB-INF/views/app/use/adv_default_fail.jsp";
		}
		
		// 判断设备是否入库
		HashMap<String, String> mifi = mifiManageService.getMifilistBySn(sn);
		if (mifi == null) {
	        model.addAttribute("code", "error_mifi");					// 设备未入库标识
	        model.addAttribute("msg", "设备未入库 请与客服联系");
			return "/WEB-INF/views/app/use/adv_default_fail.jsp";
		}
		String sourceType = mifi.get("sourceType");

		// 根据mcc根据国家编号
		Map<String, String> countryMap = mifiOrderService.findCountryByMcc(mcc);
		if (countryMap==null || StringUtils.isBlank(countryMap.get("countryCode"))) {
	        model.addAttribute("code", "error_mcc");					// 地区未投入运营
	        model.addAttribute("msg", "抱歉 该地区暂未投入运营 不能使用WIFI网络");
			return "/WEB-INF/views/app/use/adv_default_fail.jsp";
		}
        model.addAttribute("countryName", countryMap.get("countryName"));
        
        // 流量使用率提醒
        if (StringUtils.isNotBlank(flowUseRate)) {

    		// 保存连接设备开机记录 TODO 测试代码
    		DeviceBoot bean = new DeviceBoot();
    		bean.setType("FLOW_USE_RATE");
    		bean.setImei(sn);
    		bean.setMac(mac);
    		bean.setMcc(mcc);
    		bean.setSourceType(sourceType);
    		deviceBootService.save(bean);
    		
        	if ("90".equals(flowUseRate)) {
                model.addAttribute("msg", "您的流量已超过" + flowUseRate + "%");
        		return "/WEB-INF/views/app/use/flow_use_rate.jsp";
        	}
        }

		// 广告信息
        Advertising advertising = getAdvertising(sourceType, countryMap.get("countryCode"));
        
        // 判断缓存中一分钟之内是不是有开机记录
        String preRecordTime = recordMap.get(sn);
        if (StringUtils.isNotBlank(preRecordTime)) {
        	try {
				Date preTime = df.parse(preRecordTime);
				if (new Date().getTime() - preTime.getTime() < 60000) {
					
					// 游友专车渠道专用
					if (Constants.CHANNEL_YOUYOUYATTO_VALUE.equalsIgnoreCase(sourceType)) {
				        model.addAttribute("mcc", mcc);
						return "/WEB-INF/views/app/use/suc_youyouauto.jsp";
					}

					// 广告跳转
					if (advertising!=null && Advertising.ADVERTISING_TYPE.equals(advertising.getType())) {	// 四广告跳转
						return fourStepOne(sn, mcc, advertising, model);
					}
					
			        model.addAttribute("code", "no_adv");					// 没有配置广告信息
			        model.addAttribute("msg", "无开机使用相关配置信息 请与客服联系");
					return "/WEB-INF/views/app/use/adv_default_suc.jsp";
				}
			} catch (ParseException e) {
				System.out.println("imei:" + sn);
				System.out.println("preRecordTime:" + preRecordTime);
				e.printStackTrace();
			}
        }
        
        // 获取客户端user-agent信息
		String userAgent = request.getHeader("user-agent");
		String clientType = "未知";
		if (StringUtils.isNotBlank(userAgent)) {
			if(userAgent.contains("Android")) {
				clientType = "Android移动客户端";
			} else if(userAgent.contains("iPhone")) {
				clientType = "iPhone移动客户端";
			} else if(userAgent.contains("iPad")) {
				clientType = "iPad客户端";
			} else {
				clientType = "其他客户端";
			}
		}
        
		// 保存连接设备开机记录
		DeviceBoot bean = new DeviceBoot();
		bean.setType(DeviceBoot.TYPE_BOOT);
		bean.setImei(sn);
		bean.setMac(mac);
		bean.setMcc(mcc);
		bean.setAdvertising(advertising);
		bean.setSourceType(sourceType);
		bean.setClientType(clientType);
		bean.setUserAgent(userAgent);
		deviceBootService.save(bean);
		
		// 缓存开机记录
		recordMap.put(sn, df.format(new Date()));
		
		// 游友专车渠道专用
		if (Constants.CHANNEL_YOUYOUYATTO_VALUE.equalsIgnoreCase(sourceType)) {
	        model.addAttribute("mcc", mcc);
			return "/WEB-INF/views/app/use/suc_youyouauto.jsp";
		}

		// 广告跳转
		if (advertising!=null && Advertising.ADVERTISING_TYPE.equals(advertising.getType())) {	// 四广告跳转
			return fourStepOne(sn, mcc, advertising, model);
		}
		
        model.addAttribute("code", "no_adv");					// 没有配置广告信息
        model.addAttribute("msg", "无开机使用相关配置信息 请与客服联系");
		return "/WEB-INF/views/app/use/adv_default_suc.jsp";
	}
	
	// ******************************* 四广告位 方法开始  ************************** TODO...
	/**
	 * 
	 * @Description 第一广告位返回
	 * @param imei
	 * @param mcc
	 * @param advertising
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年12月19日 上午11:06:29
	 */
	private String fourStepOne(String imei, String mcc, Advertising advertising, Model model) {
		
        model.addAttribute("advId", advertising.getId());
        model.addAttribute("imei", imei);
        model.addAttribute("mcc", mcc);
        
        // 第一广告位地址
        try {
			AdvertisingItem item = advertising.getItemList().get(0);
	        model.addAttribute("advUrl", item.getImgPath());
		} catch (Exception e) {
			e.printStackTrace();
	        model.addAttribute("code", "no_adv");					// 没有配置广告信息
	        model.addAttribute("msg", "无开机使用相关配置信息 请与客服联系");
			return "/WEB-INF/views/app/use/adv_default_suc.jsp";
		}
        
		return "/WEB-INF/views/app/use/adv_4position_one.jsp";
		
	}
	
	/**
	 * 
	 * @Description 第二、三广告位返回
	 * @param advId
	 * @param imei
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年12月19日 上午11:23:55
	 */
	@RequestMapping(value = "fourStepTow")
	public String fourStepTow(@RequestParam Map<String, Object> paramMap,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		
		// 参数
		String advId = ObjectUtils.toString(paramMap.get("advId"));
		String imei = ObjectUtils.toString(paramMap.get("imei"));
		String mcc = ObjectUtils.toString(paramMap.get("mcc"));
		if (StringUtils.isBlank(advId)) {
	        model.addAttribute("code", "error_mifi");					
	        model.addAttribute("msg", "参数'advId'不能为空");
			return "/WEB-INF/views/app/use/adv_default_suc.jsp";
		}
		if (StringUtils.isBlank(imei)) {
	        model.addAttribute("code", "error_mifi");					
	        model.addAttribute("msg", "参数'imei'不能为空");
			return "/WEB-INF/views/app/use/adv_default_suc.jsp";
		}
		if (StringUtils.isBlank(mcc)) {
	        model.addAttribute("code", "error_mifi");					
	        model.addAttribute("msg", "参数'mcc'不能为空");
			return "/WEB-INF/views/app/use/adv_default_suc.jsp";
		}

        model.addAttribute("advId", advId);
        model.addAttribute("imei", imei);
        model.addAttribute("mcc", mcc);
        
        // 第二、三广告位地址
		try {
			Advertising advertising = advertisingService.get(advId);
			List<AdvertisingItem> itemList = advertising.getItemList();
	        model.addAttribute("advUrl1", itemList.get(1).getImgPath());
	        model.addAttribute("advUrl2", itemList.get(2).getImgPath());
		} catch (Exception e) {
			e.printStackTrace();
	        model.addAttribute("code", "no_adv");					// 没有配置广告信息
	        model.addAttribute("msg", "无开机使用相关配置信息 请与客服联系");
			return "/WEB-INF/views/app/use/adv_default_suc.jsp";
		}

		return "/WEB-INF/views/app/use/adv_4position_two.jsp";
	}
	
	/**
	 * 
	 * @Description 第四广告位返回
	 * @param advId
	 * @param imei
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年12月19日 上午11:23:55
	 */
	@RequestMapping(value = "fourStepThree")
	public String fourStepThree(@RequestParam Map<String, Object> paramMap,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		
		// 参数
		String advId = ObjectUtils.toString(paramMap.get("advId"));
		String imei = ObjectUtils.toString(paramMap.get("imei"));
		String mcc = ObjectUtils.toString(paramMap.get("mcc"));
		if (StringUtils.isBlank(advId)) {
	        model.addAttribute("code", "error_mifi");					
	        model.addAttribute("msg", "参数'advId'不能为空");
			return "/WEB-INF/views/app/use/adv_default_suc.jsp";
		}
		if (StringUtils.isBlank(imei)) {
	        model.addAttribute("code", "error_mifi");					
	        model.addAttribute("msg", "参数'imei'不能为空");
			return "/WEB-INF/views/app/use/adv_default_suc.jsp";
		}
		if (StringUtils.isBlank(mcc)) {
	        model.addAttribute("code", "error_mifi");					
	        model.addAttribute("msg", "参数'mcc'不能为空");
			return "/WEB-INF/views/app/use/adv_default_suc.jsp";
		}

        model.addAttribute("advId", advId);
        model.addAttribute("imei", imei);
        model.addAttribute("mcc", mcc);
        
        // 第四广告位地址
		try {
			Advertising advertising = advertisingService.get(advId);
			List<AdvertisingItem> itemList = advertising.getItemList();
	        model.addAttribute("advUrl", itemList.get(3).getImgPath());
	        model.addAttribute("httpUrl", itemList.get(3).getUrl());
	        model.addAttribute("showBaiduAd", itemList.get(3).getShowBaiduAd());
		} catch (Exception e) {
			e.printStackTrace();
	        model.addAttribute("code", "no_adv");					// 没有配置广告信息
	        model.addAttribute("msg", "无开机使用相关配置信息 请与客服联系");
			return "/WEB-INF/views/app/use/adv_default_suc.jsp";
		}

		return "/WEB-INF/views/app/use/adv_4position_three.jsp";
	}

	// ******************************* 四广告位 方法结束  **************************

	// ******************************* 广告页面点击行为统计  开始  ************************** TODO
	/**
	 * 
	 * @Description 记录广告页面点击行为(1'我要上网',2 '立即预定')
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param model
	 * @return Map<String,Object>  
	 * @author yifang.huang
	 * @date 2017年1月22日 上午10:50:15
	 */
    @RequestMapping(value = "/page_click.json", produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> pageClick(@RequestParam Map<String, Object> paramMap, 
			HttpServletRequest request, HttpServletResponse response, Model model) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		try {
			
			// 保存
			return deviceBootService.saveAndPageClick(paramMap);
			
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", "error");
			map.put("message", "记录广告页面点击行为失败");
			return map;
		}
		
	}

	// ******************************* 广告页面点击行为  结束  **************************

	// ******************************* 私有方法  开始  **************************
    private Advertising getAdvertising(String sourceType, String countryCode) {
    	
    	Advertising advertising = null;
    	
    	// 首页根据渠道商和国家编号取，如果没有再按渠道商和'ALL'取
    	AdvertisingCondition aCondition = new AdvertisingCondition();
		aCondition.setLikeSourceType(sourceType);
		aCondition.setLikeCountryCodes(countryCode);
		aCondition.setEqShelfUpDown(Advertising.SHELF_UP);
		List<Advertising> aList = advertisingService.findList(aCondition);
		if (aList==null || aList.size()==0) {	
			aCondition.setLikeCountryCodes("ALL");// 广告投放国家为'ALL'
			aList = advertisingService.findList(aCondition);
		}
		if (aList!=null && aList.size()>0) {
			advertising = aList.get(0);
		}
		
		return advertising;
    }

	// ******************************* 私有方法  结束  **************************
    
	
    // -------------------------------- 新接口结束 ------------------------------ //
	
	/**
	 * 
	 * @Description 设备开机检验
	 * @param mcc 
	 * @param sn 设备编号
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年3月31日 上午10:44:16
	 */
	@RequestMapping(value = "check")
	public String check(@RequestParam(required=true) String mcc, @RequestParam(required=true) String sn, 
			HttpServletRequest request, HttpServletResponse response, Model model) {
		
		// 判断设备是否入库
		HashMap<String, String> mifi = mifiManageService.getMifilistBySn(sn);
		if (mifi == null) {
	        model.addAttribute("code", "0");					// 设备未入库标识
	        model.addAttribute("msg", "设备未入库,请与客服联系!");
			return "/WEB-INF/views/app/use/fail_youyou.jsp";
		}
		String sourceType = mifi.get("sourceType");
		String ownerType = mifi.get("ownerType");
        model.addAttribute("ownerType", ownerType);				// 如果是运营商,则需要显示不同语言	

		Channel channel = null;
		String pageName = "channel_default";	// 返回页面
		if ("1".equals(ownerType)) {
			// 取运营商
			String channelNameEn = sourceType;
			List<Channel> cList = channelService.findChannelByNameEn(channelNameEn);
			if (cList!=null && cList.size()>0)
				channel = cList.get(0);
			else {	// 未找到设备运营商
		        model.addAttribute("code", "11");			
		        model.addAttribute("msg", "未找到该设备的运营商,请与客服联系!");
				return "/WEB-INF/views/app/use/fail_channel_default.jsp";
			}
			if ("1".equals(channel.getCustomized()))
				pageName = channel.getChannelNameEn();
		}
        model.addAttribute("channel", channel);	
        
		// 取用户信息
		UserBasicInfo user = userService.findByUserId(sourceType);	
		model.addAttribute("user", user);	
		
		int timeDifference = consumeRecordService.findBjTimeDifferenceByMcc(mcc);// 与北京时间差
		Map<String, String> country = consumeRecordService.findCountryCodeByMcc(mcc);
		if (country == null) {		// mcc没有找到对应国家信息
	        model.addAttribute("code", "-1");					
	        model.addAttribute("msg", "抱歉 该地区暂未投入运营 不能使用WIFI网络!");
			if ("1".equals(ownerType)) 
				return "/WEB-INF/views/app/use/fail_" + pageName + ".jsp";
			else if ("2".equals(ownerType)) {
				model.addAttribute("countryName", null);
				return "/WEB-INF/views/app/use/fail_youyou.jsp";
			}
			
		}
		String countryCode = country.get("countryCode");// 国家编号
        model.addAttribute("countryName", country.get("countryName"));
		
		// 判断是否是测试设备
		TestDeviceCondition tdCondition = new TestDeviceCondition();
		tdCondition.setEqImei(sn);
		tdCondition.setEqStatus(TestDevice.Status.LEND);
		List<TestDevice> tdList = testDeviceService.findListByCondition(tdCondition);
		if (tdList!=null && tdList.size()>0) { // 是测试设备可直接使用
	        model.addAttribute("code", "s0");					
	        model.addAttribute("msg", "欢迎使用!");
			return "/WEB-INF/views/app/use/suc_youyou.jsp";
		}
		
		// 判断当天是否已扣费
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date localDate = getLocalDate(timeDifference);
		ConsumeRecordCondition crCondition = new ConsumeRecordCondition();
		crCondition.setEqSn(sn);
		crCondition.setEqCountryCode(countryCode);
		crCondition.setGtLocalDate(df.format(localDate));
		crCondition.setLtLocalDate(df.format(localDate));
		List<ConsumeRecord> crList = consumeRecordService.findList(crCondition);
		if (crList!=null && crList.size()>0) {
	        model.addAttribute("code", "s1");					// 当天已扣费
	        model.addAttribute("msg", "欢迎使用!");
			if ("1".equals(ownerType)) 
				return "/WEB-INF/views/app/use/suc_" + pageName + ".jsp";
			else if ("2".equals(ownerType)) {
				return "/WEB-INF/views/app/use/suc_youyou.jsp";
			} else {
				// 系统错误
		        model.addAttribute("code", "100");
		        model.addAttribute("msg", "系统错误,请与客服联系!");
				return "/WEB-INF/views/app/use/fail_youyou.jsp";
			}
		}
		
		// 当地日期
		model.addAttribute("localDate", new SimpleDateFormat("yyyy.MM.dd").format(localDate));
		
		// 用户下单公司设备（公司）
		if ("0".equals(ownerType)) {
			// 根据设备编号查询有效订单
			List<Map<String, String>> orderList = mifiOrderService.getValidOrder(sn, null);
			if (orderList==null || orderList.size()==0) { // 没有有效订单
		        model.addAttribute("code", "01");				
		        model.addAttribute("msg", "不在订单的有效时间内,请与客服联系!");
				return "/WEB-INF/views/app/use/fail_youyou.jsp";
			} else {	// 有效订单
				// 判断mcc是否允许使用设备
				if (includeInAllowedMcc(orderList, sourceType, mcc)) {
			        model.addAttribute("code", "s2");				
			        model.addAttribute("msg", "欢迎使用!");
					return "/WEB-INF/views/app/use/suc_youyou.jsp";
				} else {
			        model.addAttribute("code", "02");				
			        model.addAttribute("msg", "设备不允许在当前区域使用,请与客服联系!");
					return "/WEB-INF/views/app/use/fail_youyou.jsp";
				}
			}
		}
		
		// 用户购买渠道设备（运营商）
		if ("1".equals(ownerType)) {
			
			// 判断运营商模式,如果是订单模式直接查询订单
			if ("ORDER".equals(channel.getModel())) {
				// 根据设备编号查询订单
				List<Map<String, String>> orderList = mifiOrderService.getValidOrder(sn, null);
				if (orderList==null || orderList.size()==0) { // 没有有效订单
					String ownerMcc = mifi.get("ownerMcc");
					if (StringUtils.isNotBlank(ownerMcc) && include(ownerMcc, mcc)) {
				        model.addAttribute("code", "18");				
				        model.addAttribute("msg", "非订单时间内开机仅限测试,10分钟后设备将自动关机。点击确认开始测试!");
					} else {
				        model.addAttribute("code", "16");				
				        model.addAttribute("msg", "不在订单的有效时间内,请与客服联系!");
					}
					return "/WEB-INF/views/app/use/fail_" + pageName + ".jsp";
				} else {														// 预付费渠道商直接使用,后付费渠道商开机扣费
					// 判断mcc是否允许使用设备
					if (includeInAllowedMcc(orderList, sourceType, mcc)) {
						String payType = channel.getPayType();
						if ("1".equals(payType)) {
							Map<String, String> map = new HashMap<String, String>();
							Price price = getPrice(mcc, channel.getId(), map);
							if (!"1".equals(map.get("code"))) {
						        model.addAttribute("code", map.get("code"));			
						        model.addAttribute("msg", map.get("msg"));
								return "/WEB-INF/views/app/use/fail_" + pageName + ".jsp";
							}
							// 支付提示页面
					        model.addAttribute("code", "p1");	
					        model.addAttribute("msg", "需扣费'" + price.getPrice() + "',是否确认使用?");
					        model.addAttribute("mcc", mcc);		
					        model.addAttribute("sn", sn);		
							return "/WEB-INF/views/app/use/pay_" + pageName + ".jsp";
						} 
						if ("0".equals(payType)) {
					        model.addAttribute("code", "s3");				
					        model.addAttribute("msg", "欢迎使用!");
							return "/WEB-INF/views/app/use/suc_" + pageName + ".jsp";
						}
					} else {
				        model.addAttribute("code", "17");				
				        model.addAttribute("msg", "设备不允许在当前区域使用,请与客服联系!");
						return "/WEB-INF/views/app/use/fail_" + pageName + ".jsp";
					}
				}
			} else {
				// 取运营商对应区域价格
				Map<String, String> map = new HashMap<String, String>();
				Price price = getPrice(mcc, channel.getId(), map);
				if (!"1".equals(map.get("code"))) {
			        model.addAttribute("code", map.get("code"));			
			        model.addAttribute("msg", map.get("msg"));
					return "/WEB-INF/views/app/use/fail_" + pageName + ".jsp";
				}
				
				// 运营商类型（预付费运营商需要判断余额,后付费运营商直接扣费）
				String payType = channel.getPayType();				// 付费类型,0_预付费 1_后付费
				if ("0".equals(payType)) {
					Double balance = channel.getBalance();		// 预付费运营商余额
					Double money = price.getPrice();
					if (money > balance) { // 余额不足提示
				        model.addAttribute("code", "15");				// 余额不足,请联系运营商
				        model.addAttribute("msg", "余额不足,请联系运营商(需扣费'" + money + "',当前余额'" + balance + "')!");
						return "/WEB-INF/views/app/use/fail_" + pageName + ".jsp";
					}
				}
	
				// 支付提示页面
		        model.addAttribute("code", "p1");	
		        model.addAttribute("msg", "需扣费'" + price.getPrice() + "',是否确认使用?");
		        model.addAttribute("mcc", mcc);		
		        model.addAttribute("sn", sn);		
				return "/WEB-INF/views/app/use/pay_" + pageName + ".jsp";
			}
			
		}
		
		// 用户购买设备（个人）
		if ("2".equals(ownerType)) {
	        model.addAttribute("mcc", mcc);		
	        model.addAttribute("sn", sn);	
			// 判断设备是否激活
			if (StringUtils.isBlank(sourceType)) {
		        model.addAttribute("code", "21");				// 设备未激活
		        model.addAttribute("msg", "设备未激活,请与客服联系!");
				return "/WEB-INF/views/app/use/fail_youyou.jsp";
			}
			
			// 取用户信息
			if (user == null) {
		        model.addAttribute("code", "25");				// 设备未绑定到用户
		        model.addAttribute("msg", "设备未绑定到用户,请与客服联系!");
				return "/WEB-INF/views/app/use/fail_youyou.jsp";
			}
			
			// 取默认价格
			Price price = null;
			Region region = regionService.findByMcc(mcc);
			if (region == null) {
		        model.addAttribute("code", "22");				// mcc没有对应的区域
		        model.addAttribute("msg", "该线路暂未运营,请与客服联系!");
				return "/WEB-INF/views/app/use/fail_youyou.jsp";
			}
			List<Channel> channelList = channelService.findChannelByNameEn(Constants.CHANNEL_DEFAULT_VALUE); // 默认渠道
			Channel defaultChannel = null;
			if (channelList!=null && channelList.size()>0) {
				defaultChannel = channelList.get(0);
			} else {
		        model.addAttribute("code", "23");				// 未设置默认渠道
		        model.addAttribute("msg", "该线路暂未运营,请与客服联系!");
				return "/WEB-INF/views/app/use/fail_youyou.jsp";
			}
			PriceCondition condition = new PriceCondition();
			condition.setEqChannelId(defaultChannel.getId());
			condition.setEqRegionId(region.getId());
			condition.setEqDownShelf(Price.DOWN_SHELF_UP);
			List<Price> priceList = priceService.findListByCondition(condition);
			if (priceList!=null && priceList.size()>0) {
				price = priceList.get(0);
			} else {
		        model.addAttribute("code", "24");				// mcc没有对应的价格
		        model.addAttribute("msg", "该线路暂未运营,请与客服联系!");
				return "/WEB-INF/views/app/use/fail_youyou.jsp";
			}
			// 用户余额判断
			Double balance = Double.valueOf(user.getBalance());
			Double money = price.getPrice();
	        model.addAttribute("payMoney", money);
			if (money > balance) { // 余额不足提示
		        model.addAttribute("code", "26");				// 余额不足,请联系运营商
		        model.addAttribute("msg", "余额不足,请充值!");
				return "/WEB-INF/views/app/use/fail_youyou.jsp";
			}

			// 支付提示页面
	        model.addAttribute("code", "p2");				
	        model.addAttribute("msg", "需扣费'" + price.getPrice() + "',是否确认使用?");
			return "/WEB-INF/views/app/use/pay_youyou.jsp";
		}

		// 系统错误
        model.addAttribute("code", "100");
        model.addAttribute("msg", "系统错误,请与客服联系!");
		return "/WEB-INF/views/app/use/fail_youyou.jsp";
	}
	
	/**
	 * 
	 * @Description 确认支付
	 * @param mcc
	 * @param sn
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年4月1日 上午9:37:14
	 */
    @RequestMapping(value = "/pay.json",produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> pay(@RequestParam(required=true) String mcc, @RequestParam(required=true) String sn, 
			HttpServletRequest request, HttpServletResponse response, Model model) {
		
		Map<String, String> map = new HashMap<String, String>();

		// 判断设备是否入库
		HashMap<String, String> mifi = mifiManageService.getMifilistBySn(sn);
		if (mifi == null) {// 设备未入库标识
			map.put("code", "0");
			map.put("msg", "设备未入库,请与客服联系!");
			return map;
		}
		String sourceType = mifi.get("sourceType");
		String ownerType = mifi.get("ownerType");
		String ssid = mifi.get("ssid");
        model.addAttribute("ownerType", ownerType);				// 如果是运营商,则需要显示不同语言	
		// 取用户信息
		UserBasicInfo user = userService.findByUserId(sourceType);			
		
		int timeDifference = consumeRecordService.findBjTimeDifferenceByMcc(mcc);// 与北京时间差
		Map<String, String> country = consumeRecordService.findCountryCodeByMcc(mcc);// 国家
		String countryCode = country.get("countryCode");// 国家编号
		String countryName = country.get("countryName");// 国家名称
		
		// 用户购买渠道设备（运营商）[所有条件再重新判断一次]
		if ("1".equals(ownerType)) {
			// 取运营商
			String channelNameEn = sourceType;
			List<Channel> cList = channelService.findChannelByNameEn(channelNameEn);
			Channel channel = null;
			if (cList!=null && cList.size()>0)
				channel = cList.get(0);
			else {	// 未找到设备运营商
				map.put("code", "11");
				map.put("msg", "未找到该设备的运营商,请与客服联系!");
				return map;
			}
			
			// 取运营商对应区域价格
			Price price = null;
			Region region = regionService.findByMcc(mcc);
			if (region == null) {// mcc没有对应的区域
				map.put("code", "12");
				map.put("msg", "该线路暂未运营,请与客服联系!");
				return map;
			}
			PriceCondition condition = new PriceCondition();
			condition.setEqChannelId(channel.getId());
			condition.setEqRegionId(region.getId());
			condition.setEqDownShelf(Price.DOWN_SHELF_UP);
			List<Price> priceList = priceService.findListByCondition(condition);
			if (priceList!=null && priceList.size()>0) {
				price = priceList.get(0);
			} else {	// 运营商没有设定各区域价格,则取默认价格
				// 默认渠道
				List<Channel> channelList = channelService.findChannelByNameEn(Constants.CHANNEL_DEFAULT_VALUE);
				if (channelList!=null && channelList.size()>0) {
					Channel defaultChannel = channelList.get(0);
					condition.setEqChannelId(defaultChannel.getId());
					priceList = priceService.findListByCondition(condition);
					if (priceList!=null && priceList.size()>0) {
						price = priceList.get(0);
					} else {// mcc没有对应的价格
						map.put("code", "13");
						map.put("msg", "该线路暂未运营,请与客服联系!");
						return map;
					}
				} else {// 未设置默认渠道
					map.put("code", "14");
					map.put("msg", "该线路暂未运营,请与客服联系!");
					return map;
				}
			}
			
			// 运营商类型（预付费运营商需要判断余额,后付费运营商直接扣费）
			String payType = channel.getPayType();				// 付费类型,0_预付费 1_后付费
			if ("0".equals(payType)) {
				Double balance = channel.getBalance();		// 预付费运营商余额
				Double money = price.getPrice();
				if (money > balance) { // 余额不足,请联系运营商
					map.put("code", "15");
					map.put("msg", "余额不足,请联系运营商(需扣费'" + money + "',当前余额'" + balance + "')!");
					return map;
				}
				// 修改运营商余额,保存消费记录
				channelService.saveOrUpdateConsume(channel, money, sn, ssid, country, mcc, timeDifference);
			}
			if ("1".equals(payType)) {
				// 保存消费记录
				ConsumeRecord record = new ConsumeRecord();
				record.setChannel(channel);
				record.setRecordType(RecordType.BUY);
				record.setStatus(Status.COMPLETED);
				record.setMoney(price.getPrice());
				record.setTargetName("用户使用运营商产品消费");
				record.setCountryCode(countryCode);
				record.setCountryName(countryName);
				record.setMcc(mcc);
				record.setLocalDate(getLocalDate(timeDifference));
				record.setSn(sn);
				record.setSsid(ssid);
				record.setSourceType(Constants.SOURCE_TYPE_MIFI);
				consumeRecordService.save(record);
			}
			
			// 修改设备可用状态
			mifiManageService.updateMifilistUeAllowed(Constants.ueAllowed_y, sn);

			// 支付成功页面
			map.put("code", "ps1");
			map.put("msg", "扣费成功,欢迎使用!");
			return map;
			
		}
		// 用户购买设备（个人）[所有条件再重新判断一次]
		if ("2".equals(ownerType)) {
			// 判断设备是否激活
			if (StringUtils.isBlank(sourceType)) {// 设备未激活
				map.put("code", "21");
				map.put("msg", "设备未激活,请与客服联系!");
				return map;
			}
			
			// 取默认价格
			Price price = null;
			Region region = regionService.findByMcc(mcc);
			if (region == null) {// mcc没有对应的区域
				map.put("code", "22");
				map.put("msg", "该线路暂未运营,请与客服联系!");
				return map;
			}
			List<Channel> channelList = channelService.findChannelByNameEn(Constants.CHANNEL_DEFAULT_VALUE); // 默认渠道
			Channel defaultChannel = null;
			if (channelList!=null && channelList.size()>0) {
				defaultChannel = channelList.get(0);
			} else {// 未设置默认渠道
				map.put("code", "23");
				map.put("msg", "该线路暂未运营,请与客服联系!");
				return map;
			}
			PriceCondition condition = new PriceCondition();
			condition.setEqChannelId(defaultChannel.getId());
			condition.setEqRegionId(region.getId());
			condition.setEqDownShelf(Price.DOWN_SHELF_UP);
			List<Price> priceList = priceService.findListByCondition(condition);
			if (priceList!=null && priceList.size()>0) {
				price = priceList.get(0);
			} else {// mcc没有对应的价格
				map.put("code", "24");
				map.put("msg", "该线路暂未运营,请与客服联系!");
				return map;
			}
			
			// 取用户信息
			if (user == null) {// 设备未绑定到用户
				map.put("code", "25");
				map.put("msg", "设备未绑定到用户,请与客服联系!");
				return map;
			}
			Double balance = Double.valueOf(user.getBalance());
			Double money = price.getPrice();
			if (money > balance) { // 余额不足提示
				map.put("code", "26");
				map.put("msg", "余额不足,请充值(需扣费'" + money + "',当前余额'" + balance + "')!");
				return map;
			}
			// 修改用户余额,保存消费记录
			userService.saveOrUpdateConsume(user, money, sn, ssid, country, mcc, timeDifference);
			
			// 修改设备可用状态
			mifiManageService.updateMifilistUeAllowed(Constants.ueAllowed_y, sn);

			// 支付成功页面
			map.put("code", "ps2");
			map.put("msg", "扣费成功,欢迎使用!");
			return map;
		}

		// 系统错误
		map.put("code", "100");
		map.put("msg", "系统错误!");
		return map;
		
	}
	
	/**
	 * 
	 * @Description 取当地时间
	 * @param timeDifference
	 * @return Date  
	 * @author yifang.huang
	 * @date 2016年4月1日 下午2:26:41
	 */
	private Date getLocalDate(int timeDifference) {
		Date localDate = new Date(); // 当天时间
		if (timeDifference != 0) {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.add(Calendar.HOUR, timeDifference);
			localDate = c.getTime();
		}
		return localDate;
	}
	
	/**
	 * 
	 * @Description 根据mcc和渠道取价格
	 * @param mcc
	 * @param channelId
	 * @param price
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月9日 下午5:27:15
	 */
	private Price getPrice(String mcc, String channelId, Map<String, String> map) {
		
		// 取运营商对应区域价格
		Price price = null;
		Region region = regionService.findByMcc(mcc);
		if (region == null) {
			map.put("code", "12");				// mcc没有对应的区域
			map.put("msg", "该线路暂未运营,请与客服联系!");
			return price;
		}
		PriceCondition condition = new PriceCondition();
		condition.setEqChannelId(channelId);
		condition.setEqRegionId(region.getId());
		condition.setEqDownShelf(Price.DOWN_SHELF_UP);
		List<Price> priceList = priceService.findListByCondition(condition);
		if (priceList!=null && priceList.size()>0) {
			price = priceList.get(0);
		} else {	// 运营商没有设定各区域价格,则取默认价格
			// 默认渠道
			List<Channel> channelList = channelService.findChannelByNameEn(Constants.CHANNEL_DEFAULT_VALUE);
			if (channelList!=null && channelList.size()>0) {
				Channel defaultChannel = channelList.get(0);
				condition.setEqChannelId(defaultChannel.getId());
				priceList = priceService.findListByCondition(condition);
				if (priceList!=null && priceList.size()>0) {
					price = priceList.get(0);
				} else {
					map.put("code", "13");				// mcc没有对应的价格
					map.put("msg", "该线路暂未运营,请与客服联系!");
					return price;
				}
			} else {
				map.put("code", "14");				// 未设置默认渠道
				map.put("msg", "该线路暂未运营,请与客服联系!");
				return price;
			}
		}
		
		map.put("code", "1");
		return price;
	}
	
	/**
	 * 
	 * @Description 判断设备是否允许在当前mcc中使用
	 * @param orderList
	 * @param sourceType
	 * @param mcc
	 * @return boolean  
	 * @author yifang.huang
	 * @date 2016年5月10日 上午10:46:13
	 */
	private boolean includeInAllowedMcc(List<Map<String, String>> orderList, String sourceType, String mcc) {
		
		// 渠道商允许的mcc
		if (StringUtils.isNotBlank(sourceType)) {
			List<Channel> channelList = channelService.findChannelByNameEn(sourceType);
			if (channelList!=null && channelList.size()>0) {
				Channel channel = channelList.get(0); 
				String allowedMcc = channel.getMcces();
				if (StringUtils.isNotBlank(allowedMcc) && include(allowedMcc, mcc))
					return true;
			}
		}
		
		// 订单允许的mcc
		if (orderList!=null && orderList.size()>0) {
			for (Map<String, String> orderMap : orderList) {
				String allowedMcc = orderMap.get("allowedMcc");
				if (StringUtils.isNotBlank(allowedMcc) && include(allowedMcc, mcc))
					return true;
			}
		}
		
		return false;
	}
	
	// mcc包含判断
	private boolean include(String ownerMcces, String mcc) {
		
		if (StringUtils.isBlank(ownerMcces))
			return false;
		
		String[] mccArr = ownerMcces.split(",");
		for (String str : mccArr) {
			if (str.equals(mcc)) {
				return true;
			}
		}
		
		return false;
	}
}
