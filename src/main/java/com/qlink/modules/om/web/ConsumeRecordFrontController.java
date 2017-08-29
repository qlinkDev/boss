/** 
 * @Package com.uu.modules.om.web.front 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月24日 上午9:57:15 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.om.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.WebUtils;

import com.uu.common.utils.PropertiesLoader;
import com.uu.common.web.BaseController;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.entity.ConsumeRecord;
import com.uu.modules.om.entity.ConsumeRecord.RecordType;
import com.uu.modules.om.entity.ConsumeRecord.Status;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.om.service.ConsumeRecordService;
import com.uu.modules.user.entity.UserBasicInfo;
import com.uu.modules.user.service.UserBasicInfoService;

import net.sf.json.JSONObject;

/** 
 * @Description 消费记录前端 控制器
 * @author yifang.huang
 * @date 2016年3月24日 上午9:57:15 
 */
@Controller
@RequestMapping(value = "${frontPath}/om")
public class ConsumeRecordFrontController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(ConsumeRecordFrontController.class);
	
	@Autowired
	private ConsumeRecordService consumeRecordService;
	
	@Autowired
	private UserBasicInfoService userBasicInfoService;
	
	@Autowired
	private ChannelService channelService;
	
	/**
	 * 
	 * @Description 用户充值首页
	 * @param reqobj
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年3月24日 上午10:45:09
	 */
	@RequestMapping(value = "recharge")
	public String recharge(@RequestParam Map<String, Object> reqobj, HttpServletRequest request, HttpServletResponse response, Model model) {

        JSONObject req = JSONObject.fromObject(reqobj);
        
        // 用户ID与渠道商ID二先一必填
    	String userId = (String) req.get("userId");					// 用户ID
    	String channelId = (String) req.get("channelId");			// 渠道商ID
    	String phone = (String) req.get("phone");					// 联系手机号码
    	String moneyStr = (String) req.get("money");				// 充值金额
    	String returnUri = (String) req.get("returnUri");			// 充值成功回调地址

    	// 新建消费记录
    	ConsumeRecord record = new ConsumeRecord();
    	
    	// 用户ID、渠道商ID不能同时为空
    	Exception exception = null;
    	if (StringUtils.isBlank(userId) && StringUtils.isBlank(channelId)) {
    		exception = new Exception("参数错误[充值账户不能为空]");
    		try {
				WebUtils.exposeErrorRequestAttributes(request, exception, "FrontConsumeRecordController");
				request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
				return null;
    		} catch (Exception e) {
    			logger.error("跳转500网页出错", e);
    		}
    	}
    	if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(channelId)) {
    		exception = new Exception("参数错误[充值账户只能是用户或者运营商]");
    		try {
				WebUtils.exposeErrorRequestAttributes(request, exception, "FrontConsumeRecordController");
				request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
				return null;
    		} catch (Exception e) {
    			logger.error("跳转500网页出错", e);
    		}
    	}
    	if (StringUtils.isNotBlank(userId)) {
			UserBasicInfo info = userBasicInfoService.findByUserId(userId);
			if (info == null) {
	    		exception = new Exception("参数错误[MIFI平台未找到用户信息]");
	    		try {
					WebUtils.exposeErrorRequestAttributes(request, exception, "FrontConsumeRecordController");
					request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
					return null;
	    		} catch (Exception e) {
	    			logger.error("跳转500网页出错", e);
	    		}
			}
	    	record.setUserId(userId);
    	}
    	if (StringUtils.isNotBlank(channelId)) {
    		Channel channel = channelService.getChannel(channelId);
    		if (channel == null) {
	    		exception = new Exception("参数错误[MIFI平台未找到运营商信息]");
	    		try {
					WebUtils.exposeErrorRequestAttributes(request, exception, "FrontConsumeRecordController");
					request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
					return null;
	    		} catch (Exception e) {
	    			logger.error("跳转500网页出错", e);
	    		}
    		}
        	record.setChannel(channel);
    	}
    	
    	// 参数处理
    	Double money = StringUtils.isNotBlank(moneyStr) ? Double.valueOf(moneyStr) : null;
    	
    	record.setPhone(phone);
    	record.setMoney(money);
    	record.setReturnUri(returnUri);
    	record.setStatus(Status.NEW);
    	record.setSourceType("MIFI");
    	record.setRecordType(RecordType.RECHARGE);
    	record.setTargetName("充值");
    	consumeRecordService.save(record);

    	PropertiesLoader propertiesLoader = new PropertiesLoader("app.properties");
    	// 充值接口
    	String rechargeUri = propertiesLoader.getProperty("recharge_uri");
    	
    	// 充值成功充值状态回写接口
    	String callbackUri = propertiesLoader.getProperty("callback_uri");

        model.addAttribute("id", record.getId());
        model.addAttribute("userId", StringUtils.isNotBlank(userId) ? userId : channelId);
        model.addAttribute("phone", phone);
        model.addAttribute("money", money);
        model.addAttribute("sourceType", "MIFI");
        model.addAttribute("callbackUri", callbackUri);
        model.addAttribute("rechargeUri", rechargeUri);
        model.addAttribute("returnUri", returnUri);
		
        return "/WEB-INF/views/app/recharge/recharge.jsp";
        
	}

}
