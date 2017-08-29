package main.java.com.qlink.modules.mifi.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uu.common.config.Global;
import com.uu.common.persistence.Page;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.excel.ExportExcel;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.entity.MifiOrder;
import com.uu.modules.mifi.service.MifiOrderService;
import com.uu.modules.om.condition.CustomerCondition;
import com.uu.modules.om.entity.Customer;
import com.uu.modules.om.entity.Price;
import com.uu.modules.om.entity.Region;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.om.service.CustomerService;
import com.uu.modules.om.service.PriceService;
import com.uu.modules.om.service.RegionService;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.service.LogService;
import com.uu.modules.sys.utils.DictUtils;
import com.uu.modules.sys.utils.UserUtils;

@Controller
@RequestMapping(value = "${adminPath}/mifi/mifiOrderList")
public class MifiOrderListController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(MifiOrderListController.class);

	@Autowired
	MifiOrderService mifiOrderService;
	
	@Autowired
	private PriceService priceService;
	
	@Autowired
	private ChannelService channelService;
	
	@Autowired
	private RegionService regionService;
	
	@Autowired
	private LogService logService;
	
	@Autowired
	private CustomerService customerService;

	@RequiresPermissions("mifi:mifiOrder:view")
	@RequestMapping(value = { "list", "" })
	public String list(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		
		// 是否运营商
		User user = UserUtils.getUser();
		boolean isChannelAdmin = user.getChannelAdmin();
		List<Region> regionList = regionService.findAllList();
		if (paramMap.containsKey("initTag")) {
			
			if (isChannelAdmin)
				paramMap.put("sourceType", user.getChannelNameEn());	// 只能查询运营商自己的数据
			
			Page<HashMap> page = mifiOrderService.mifiOrderList(new Page<HashMap>(request, response), paramMap, regionList);
			model.addAttribute("page", page);
		} else {
			paramMap.put("startDate", DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
		}
		model.addAllAttributes(paramMap);
		List<String[]> mccList = channelService.findAllMCC();
		model.addAttribute("mccList", mccList);
		model.addAttribute("regionList", regionList);
		if (isChannelAdmin)
			return "channel/mifi/mifiOrderList";
		
		return "modules/mifi/mifiOrderList";
	}

	@RequiresPermissions("mifi:mifiOrder:stock")
	@RequestMapping(value = "stockSimCard")
	public String stockSimCard(@RequestParam Map<String, Object> paramMap, RedirectAttributes redirectAttributes) {
		mifiOrderService.updateStockSimCardByOrderId((String) paramMap.get("order_id"));
		addMessage(redirectAttributes, "确认[已备SIM卡]成功!");
		return "redirect:" + Global.getAdminPath() + "/mifi/mifiOrderList/?repage&sourceType="
				+ paramMap.get("sourceType") + "&stockStatus=" + paramMap.get("stockStatus") + "&startDate="
				+ paramMap.get("startDate") + "&endDate=" + paramMap.get("endDate") + "&outOrderId="
				+ paramMap.get("outOrderId") + "&orderStatus=" + paramMap.get("orderStatus") + "&initTag";

	}

	@RequiresPermissions("mifi:mifiOrder:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "MIFI订单数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<Region> regionList = regionService.findAllList();
			Page<HashMap> page = mifiOrderService.mifiOrderList(new Page<HashMap>(), paramMap, regionList);
			ExportExcel ee = new ExportExcel("MIFI订单数据",
					new String[] { "订单编号", "订单状态", "备货状态", "订单时间", "行程开始时间", "行程结束时间", "行程天数", "参考单价", "参考总价", "SIM卡数量", "设备序列号", "SSID", "代理商", "地区", "地区中文名", "地区英文名" });
			List<HashMap> list = page.getList();
			HashMap map = null;
			Row row = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (int i = 0; i < list.size(); i++) {
				row = ee.addRow();
				map = list.get(i);
				ee.addCell(row, 0, map.get("out_order_id"));
				ee.addCell(row, 1, DictUtils.getDictLabel((String) map.get("order_status"), "mifi_order_status", "未知状态"));
				ee.addCell(row, 2, DictUtils.getDictLabel((String) map.get("stock_status"), "order_stock_status", "未知状态"));
				ee.addCell(row, 3, df.format(map.get("out_order_time")));
				ee.addCell(row, 4, df.format(map.get("start_date")));
				ee.addCell(row, 5, df.format(map.get("end_date")));
				ee.addCell(row, 6, map.get("days"));
				ee.addCell(row, 7, map.get("reference_unit_price"));
				ee.addCell(row, 8, map.get("reference_total_price"));
				ee.addCell(row, 9, map.get("equipment_cnt"));
				ee.addCell(row, 10, map.get("dsn"));
				ee.addCell(row, 11, map.get("ssid"));
				ee.addCell(row, 12, map.get("source_type"));
				ee.addCell(row, 13, map.get("allowed_mcc"));
				ee.addCell(row, 14, map.get("allowed_mcc_cn"));
				ee.addCell(row, 15, map.get("allowed_mcc_en"));
				
				row = null;
				map = null;
			}
			ee.write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出MIFI订单数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/mifi/mifiOrderList/?repage";
	}

	/****************************** 渠道商PC端订单管理 TODO ****************************/
	@ModelAttribute
	public MifiOrder get(@RequestParam(required=false) String id) {
		
		if (StringUtils.isNotBlank(id))
			return mifiOrderService.get(id);
		
		return new MifiOrder();
		
	}
	
	@RequestMapping(value = "form")
	public String form(MifiOrder bean, Model model) {
		
		User user = UserUtils.getUser();
		String channelNameEn = user.getChannelNameEn();		// 取渠道编号
		
		// 订单对象
		model.addAttribute("mifiOrder", bean);
		
		// 渠道对应价格列表[根据价格取区域拼装成国家列表]
		List<Map<String, String>> countryList = null;
		try {
			List<Price> priceList = priceService.findChannelPriceList(channelNameEn);
			if (priceList!=null && priceList.size()>0) {
				countryList = new ArrayList<Map<String, String>>();
				Map<String, String> map = null;
				for (Price price : priceList) {
					Region region = price.getRegion();
					String[] countryCodeArr = region.getCountryCodes().split(",");
					String[] countryNameArr = region.getCountryNames().split(",");
					for (int i=0; i<countryCodeArr.length; i++) {
						map = new HashMap<String, String>();

						map.put("regionId", region.getId());
						map.put("regionName", region.getName());
						map.put("countryCode", countryCodeArr[i]);
						map.put("countryName", countryNameArr[i]);
						map.put("price", ObjectUtils.toString(price.getPrice()));
						countryList.add(map);
						
						map = null;
					}
				}
			}
		} catch (Exception e) {
			logger.info("初始化国家列表失败");
			e.printStackTrace();
		}
		
		model.addAttribute("countryList", countryList);
		
		return "modules/mifi/mifiOrderForm";
		
	}

	/**
	 * 
	 * @Description 价格预览
	 * @param paramMap
	 * @param request
	 * @param model
	 * @param redirectAttributes
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月4日 上午11:36:58
	 */
    @RequestMapping(value = "/preview.json",produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, Object> preview(@RequestParam Map<String, Object> paramMap, HttpServletRequest request, Model model, 
			RedirectAttributes redirectAttributes) {
		
		return mifiOrderService.preview(paramMap);
		
	}

	/**
	 * 
	 * @Description 渠道商下单
	 * @param paramMap
	 * @param request
	 * @param model
	 * @param redirectAttributes
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月4日 上午11:36:58
	 */
    @RequestMapping(value = "/createOrder.json",produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> createOrder(@RequestParam Map<String, Object> paramMap, HttpServletRequest request, Model model, 
			RedirectAttributes redirectAttributes) {
		
		return mifiOrderService.saveAndCreateOrder(paramMap);
		
	}

	/**
	 * 
	 * @Description 订单延期
	 * @param paramMap
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月12日 下午2:33:22
	 */
	@RequiresPermissions("mifi:mifiOrder:delay")
    @RequestMapping(value = "/delayOrder.json",produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> delayOrder(@RequestParam Map<String, Object> paramMap, HttpServletRequest request) {
		
		Map<String, String>  map = mifiOrderService.saveAndDelayOrder(paramMap);
		
		if(map.get("code") == "1"){
			logService.saveOperateLog(request, "[MIFI管理 》订单管理 》MIFI订单管理]-延期订单，订单编号为：[{}]", ObjectUtils.toString(paramMap.get("orderId")));
		}
		
		return map;
		
	}

	/**
	 * 
	 * @Description 订单取消
	 * @param paramMap
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月12日 下午2:34:45
	 */
	@RequiresPermissions("mifi:mifiOrder:cancel")
    @RequestMapping(value = "/cancelOrder.json",produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> cancelOrder(@RequestParam Map<String, Object> paramMap, HttpServletRequest request) {
		
		Map<String, String>  map = mifiOrderService.saveAndCancelOrder(paramMap);
		
		if(map.get("code") == "1"){
			logService.saveOperateLog(request, "[MIFI管理 》订单管理 》MIFI订单管理]-取消订单，订单编号为：[{}]", ObjectUtils.toString(paramMap.get("orderId")));
		}
		
		return map;
		
	}

	/**
	 * 
	 * @Description 订单完成
	 * @param paramMap
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月12日 下午2:35:02
	 */
	@RequiresPermissions("mifi:mifiOrder:finish")
    @RequestMapping(value = "/finishOrder.json",produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> finishOrder(@RequestParam Map<String, Object> paramMap, HttpServletRequest request) {
		
		Map<String, String>  map = mifiOrderService.saveAndFinishOrder(paramMap);
		
		if(map.get("code") == "1"){
			logService.saveOperateLog(request, "[MIFI管理 》订单管理 》MIFI订单管理]-结束订单，订单编号为：[{}]", ObjectUtils.toString(paramMap.get("orderId")));
		}
		
		return map;
		
	}

	/**
	 * 
	 * @Description 订单删除
	 * @param paramMap
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月12日 下午2:35:02
	 */
	@RequiresPermissions("mifi:mifiOrder:del")
    @RequestMapping(value = "/deleteOrder.json",produces="application/json;charset=UTF-8") 
	@ResponseBody  
	public Map<String, String> deleteOrder(@RequestParam Map<String, Object> paramMap, HttpServletRequest request) {
		Map<String, String>  map = mifiOrderService.deleteOrder(paramMap);
		if(map.get("code") == "1"){
			logService.saveOperateLog(request, "[MIFI管理 》订单管理 》MIFI订单管理]-删除订单，删除订单编号为：[{}]", ObjectUtils.toString(map.get("outOrderId")));
		}
		return map;
		
	}
	
	/**
	 * 
	 * @Description 订单详情
	 * @param orderNo 订单编号
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年9月26日 上午11:44:26
	 */
	@SuppressWarnings("rawtypes")
	@RequiresPermissions("mifi:mifiOrder:view")
	@RequestMapping("detail")
	public String orderDetail(String orderNo, HttpServletRequest request,
			HttpServletResponse response,  ModelMap model){
		
		String result = "success";
		
		if(StringUtils.isBlank(orderNo)){
			result = "fail";
		} else {
			// 订单基本信息
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("outOrderId", orderNo);
			Page<HashMap> page = mifiOrderService.mifiOrderList(new Page<HashMap>(request, response), paramMap, null);
			List<HashMap> listMap = page.getList();
			if (listMap==null || listMap.size()==0) {
				result = "fail";
			} else {
				Map map = listMap.get(0);
				model.put("order", map);
				String customerId = ObjectUtils.toString(map.get("customer_id"));
				if (StringUtils.isNotBlank(customerId)) {
					// 订单客户信息
					CustomerCondition condition = new CustomerCondition();
					condition.setEqId(customerId);
					List<Customer> customerList = customerService.findListByCondition(condition);
					if (customerList!=null && customerList.size()>0) {
						model.put("customer", customerList.get(0));
					}
				}
			}
		}
		model.put("result", result);
		return "modules/mifi/mifiOrderDetail";
	}
	/****************************** 渠道商PC端订单管理 结束 ****************************/
	
}
