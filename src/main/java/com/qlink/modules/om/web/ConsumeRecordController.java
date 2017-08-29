/** 
 * @Package com.uu.modules.om.web 
 * @Description 
 * @author yifang.huang
 * @date 2016-3-14 下午5:27:11 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.om.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uu.common.persistence.Page;
import com.uu.common.web.BaseController;
import com.uu.modules.mifi.service.MifiOrderService;
import com.uu.modules.om.condition.ConsumeRecordCondition;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.entity.ConsumeRecord;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.om.service.ConsumeRecordService;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.utils.UserUtils;

/** 
 * @Description 消费记录 对外服务包实现
 * @author yifang.huang
 * @date 2016-3-14 下午5:27:11 
 */
@Controller
@RequestMapping(value = "${adminPath}/om/consumeRecord")
public class ConsumeRecordController extends BaseController {
	
	@Autowired
	private ConsumeRecordService consumeRecordService;
	
	@Autowired
	private ChannelService channelService;
	
	@Autowired
	private MifiOrderService mifiOrderService;
	
	@ModelAttribute
	public ConsumeRecord get(@RequestParam(required=false) String id) {
		
		if (StringUtils.isNotBlank(id))
			return consumeRecordService.get(id);
		
		return new ConsumeRecord();
		
	}
	
	@RequiresPermissions("om:consumeRecord:view")
	@RequestMapping(value = {"list", ""})
	public String list(ConsumeRecordCondition condition, HttpServletRequest request, HttpServletResponse response, Model model) {

        Page<ConsumeRecord> page = consumeRecordService.findPage(new Page<ConsumeRecord>(request, response), condition); 
        model.addAttribute("page", page);
		
		return "modules/om/consumeRecordList";
		
	}
	
	@RequiresPermissions("om:consumeRecord:view")
	@RequestMapping(value = "csv")
	public String csv(HttpServletRequest request, HttpServletResponse response, Model model) {

		User user = UserUtils.getUser();
		if (user.getChannelAdmin())
			return "channel/om/createCsvFile";
		
		return "modules/om/createCsvFile";
		
	}
	
	/**
	 * 
	 * @Description 创建csv文件
	 * @param recordId
	 * @param request
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年4月20日 上午9:29:38
	 */
	@RequiresPermissions("om:consumeRecord:edit")
	@RequestMapping(value = "createCsv", produces="application/json;charset=UTF-8")
	@ResponseBody  
	public Map<String, String> createCsv(@RequestParam String startDateStr, @RequestParam String endDateStr, HttpServletRequest request) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		try {
			// 时间判断
			if (StringUtils.isBlank(startDateStr) || StringUtils.isBlank(endDateStr)) {
				map.put("code", "-1");
				map.put("msg", "开始时间和结束时间不能为空");
				return map;
			}
			if (startDateStr.equals(endDateStr)) {
				map.put("code", "-1");
				map.put("msg", "时间错误[最少1小时,最多100小时]");
				return map;
			}
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH");
			Date startDate = df.parse(startDateStr);
			Date endDate = df.parse(endDateStr);
			if (endDate.before(startDate)) {
				map.put("code", "-1");
				map.put("msg", "时间错误");
				return map;
			}
			long hours = (endDate.getTime() - startDate.getTime()) / (60*60*1000);
			if (hours > 100) {
				map.put("code", "-1");
				map.put("msg", "时间错误[最少1小时,最多100小时]");
				return map;
			}
			
			// 获取渠道信息
			User user = UserUtils.getUser();
			List<Channel> cList = channelService.findChannelByNameEn(user.getChannelNameEn());
			if (cList==null || cList.size()==0) {
				map.put("code", "-1");
				map.put("msg", "未受权渠道管理,不能执行此操作");
				return map;
			}
			Channel channel = cList.get(0);
			
			// 生成csv文件
			map = consumeRecordService.createCsvFiles(startDate, endDate, channel);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("生成csv文件失败", e);
			map.put("code", "-1");
			map.put("msg", "生成csv文件失败");
			return map;
		}
		
		return map;
		
	}
	
	/**
	 * 
	 * @Description 创建csv文件
	 * @param recordId
	 * @param request
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年4月20日 上午9:29:38
	 */
	@RequiresPermissions("om:consumeRecord:edit")
	@RequestMapping(value = "createMifiOrderCsv", produces="application/json;charset=UTF-8")
	@ResponseBody  
	public Map<String, String> createMifiOrderCsv(@RequestParam String startDateStr, @RequestParam String endDateStr, HttpServletRequest request) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		try {
			// 时间判断
			if (StringUtils.isBlank(startDateStr) || StringUtils.isBlank(endDateStr)) {
				map.put("code", "-1");
				map.put("msg", "开始时间和结束时间不能为空");
				return map;
			}
			if (startDateStr.equals(endDateStr)) {
				map.put("code", "-1");
				map.put("msg", "时间错误[最少1小时,最多100小时]");
				return map;
			}
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH");
			Date startDate = df.parse(startDateStr);
			Date endDate = df.parse(endDateStr);
			if (endDate.before(startDate)) {
				map.put("code", "-1");
				map.put("msg", "时间错误");
				return map;
			}
			long hours = (endDate.getTime() - startDate.getTime()) / (60*60*1000);
			if (hours > 100) {
				map.put("code", "-1");
				map.put("msg", "时间错误[最少1小时,最多100小时]");
				return map;
			}
			
			// 获取渠道信息
			User user = UserUtils.getUser();
			List<Channel> cList = channelService.findChannelByNameEn(user.getChannelNameEn());
			if (cList==null || cList.size()==0) {
				map.put("code", "-1");
				map.put("msg", "未受权渠道管理,不能执行此操作");
				return map;
			}
			Channel channel = cList.get(0);
			
			// 生成csv文件
			map = mifiOrderService.createCsvFiles(startDate, endDate, channel);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("生成csv文件失败", e);
			map.put("code", "-1");
			map.put("msg", "生成csv文件失败");
			return map;
		}
		
		return map;
		
	}

	/**
	 * 
	 * @Description 渠道商后台充值跳转页面
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年5月6日 下午2:07:45
	 */
	@RequestMapping(value = "recharge")
	public String recharge(HttpServletRequest request, HttpServletResponse response, Model model) {

		User user = UserUtils.getUser();
		List<Channel> list = channelService.findChannelByNameEn(user.getChannelNameEn());
		if (list!=null && list.size()>0)
			model.addAttribute("channel", list.get(0));
		
		return "channel/om/recharge";
		
	}

}
