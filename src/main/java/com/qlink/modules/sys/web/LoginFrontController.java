/** 
 * @Package com.uu.modules.om.web.front 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月24日 上午9:57:15 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.sys.web;

import java.util.Date;
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

import com.uu.common.utils.DigestUtils;
import com.uu.common.web.BaseController;
import com.uu.modules.sys.entity.YYKeyStore;
import com.uu.modules.sys.service.YYKeyStoreService;
import com.uu.modules.utils.ToolUtil;

import net.sf.json.JSONObject;

/**
 * 
 * @Description 登录API接口
 * @author yifang.huang
 * @date 2016年5月16日 上午10:08:42
 */
@Controller
@RequestMapping(value = "${frontPath}/api/login")
public class LoginFrontController extends BaseController {

	public static Logger logger = LoggerFactory.getLogger(LoginFrontController.class);
	
	@Autowired
	private YYKeyStoreService keyStoreService;
	
	/**
	 * 
	 * @Description API登录
	 * @param reqobj
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年5月16日 上午10:41:45
	 */
	@RequestMapping(value = "login")
	public String login(@RequestParam Map<String, Object> reqobj, HttpServletRequest request, HttpServletResponse response, Model model) {

        JSONObject req = JSONObject.fromObject(reqobj);
        
        // 参数
    	String authParams = (String) req.get("authParams");	
    	Exception exception = null;
    	if (StringUtils.isBlank(authParams)) {
    		exception = new Exception("参数错误");
    		try {
				WebUtils.exposeErrorRequestAttributes(request, exception, "FrontConsumeRecordController");
				request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
				return null;
    		} catch (Exception e) {
    			logger.error("跳转500网页出错", e);
    		}
    	}
    	// 认证参数
    	JSONObject json = JSONObject.fromObject(authParams);
    	json = ToolUtil.transObject(json);		//key忽略大小写
    	String appid = json.getString("appid");
    	String timestamp = json.getString("timestamp");
    	String params = json.getString("params");
    	String sign = json.getString("sign");
    	
    	if (StringUtils.isBlank(appid)) {
    		exception = new Exception("参数错误[appid不能为空]");
    		try {
				WebUtils.exposeErrorRequestAttributes(request, exception, "FrontConsumeRecordController");
				request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
				return null;
    		} catch (Exception e) {
    			logger.error("跳转500网页出错", e);
    		}
    	}
    	if (StringUtils.isBlank(timestamp)) {
    		exception = new Exception("参数错误[timestamp不能为空]");
    		try {
				WebUtils.exposeErrorRequestAttributes(request, exception, "FrontConsumeRecordController");
				request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
				return null;
    		} catch (Exception e) {
    			logger.error("跳转500网页出错", e);
    		}
    	}
    	if (StringUtils.isBlank(params)) {
    		exception = new Exception("参数错误[params不能为空]");
    		try {
				WebUtils.exposeErrorRequestAttributes(request, exception, "FrontConsumeRecordController");
				request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
				return null;
    		} catch (Exception e) {
    			logger.error("跳转500网页出错", e);
    		}
    	}
    	if (StringUtils.isBlank(sign)) {
    		exception = new Exception("参数错误[sign不能为空]");
    		try {
				WebUtils.exposeErrorRequestAttributes(request, exception, "FrontConsumeRecordController");
				request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
				return null;
    		} catch (Exception e) {
    			logger.error("跳转500网页出错", e);
    		}
    	}
    	// 用户名密码
    	JSONObject upJson = JSONObject.fromObject(params);
    	String username = upJson.getString("username");
    	String password = upJson.getString("password");
    	if (StringUtils.isBlank(username)) {
    		exception = new Exception("参数错误[username不能为空]");
    		try {
				WebUtils.exposeErrorRequestAttributes(request, exception, "FrontConsumeRecordController");
				request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
				return null;
    		} catch (Exception e) {
    			logger.error("跳转500网页出错", e);
    		}
    	}
    	if (StringUtils.isBlank(password)) {
    		exception = new Exception("参数错误[password不能为空]");
    		try {
				WebUtils.exposeErrorRequestAttributes(request, exception, "FrontConsumeRecordController");
				request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
				return null;
    		} catch (Exception e) {
    			logger.error("跳转500网页出错", e);
    		}
    	}
    	
    	// 超时判断（timestamp小于当前时间10分钟）
    	if ((new Date().getTime() - Long.valueOf(timestamp)) > 600000) {
    		exception = new Exception("认证失败[登录连接已超时]");
    		try {
				WebUtils.exposeErrorRequestAttributes(request, exception, "FrontConsumeRecordController");
				request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
				return null;
    		} catch (Exception e) {
    			logger.error("跳转500网页出错", e);
    		}
    	}
    	
    	// 取keyStore
    	YYKeyStore key = keyStoreService.get(appid);
    	if (key == null)
    		key = keyStoreService.findKeyBySourceType(appid);
    	if (key == null) {
    		exception = new Exception("参数错误["+appid+"对应密钥库未找到]");
    		try {
				WebUtils.exposeErrorRequestAttributes(request, exception, "FrontConsumeRecordController");
				request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
				return null;
    		} catch (Exception e) {
    			logger.error("跳转500网页出错", e);
    		}
    	}
    	
    	// sign认证
    	JSONObject obj = new JSONObject();
		obj.put("appid", appid);
		obj.put("timestamp", timestamp);
		obj.put("params", upJson);
		String tempSign = DigestUtils.getSignature(obj, key.getKeyValue(), "UTF-8");
		if (!tempSign.equals(sign)) {
    		exception = new Exception("认证失败");
    		try {
				WebUtils.exposeErrorRequestAttributes(request, exception, "FrontConsumeRecordController");
				request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
				return null;
    		} catch (Exception e) {
    			logger.error("跳转500网页出错", e);
    		}
		}

		model.addAttribute("username", username);
		model.addAttribute("password", password);
		return "modules/sys/apiLogin";
        
	}
	
}
