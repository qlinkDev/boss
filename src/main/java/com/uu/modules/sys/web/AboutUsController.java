/** 
 * @Package com.uu.modules.sys.web 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月28日 下午1:46:50 
 * @version V1.0 
 */ 
package com.uu.modules.sys.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uu.common.web.BaseController;

/** 
 * @Description 联系客服|关于我们.... 对外服务包实现
 * @author yifang.huang
 * @date 2016年4月28日 下午1:46:50 
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/about")
public class AboutUsController extends BaseController {
	
	/**
	 * 
	 * @Description 联系客服
	 * @param request
	 * @param response
	 * @param model
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年4月28日 下午1:49:45
	 */
	@RequestMapping(value = "customService")
	public String customService(HttpServletRequest request, HttpServletResponse response, Model model) {

		return "modules/about/customService";
		
	}

}
