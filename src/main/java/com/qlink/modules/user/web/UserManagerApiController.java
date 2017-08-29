package main.java.com.qlink.modules.user.web;

import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uu.common.utils.StringUtils;
import com.uu.modules.user.service.UserBasicInfoService;
import com.uu.modules.user.service.UserMifiLinkService;
import com.uu.modules.utils.ReturnCode;
import com.uu.modules.utils.ToolUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author jiangbo
 * @date 2016年3月22日
 */

@Controller
@RequestMapping(value = "/api/user", produces = "application/json")
@Api(value = "/user", description = "用户模块相关接口")
public class UserManagerApiController {

	public static Logger logger = LoggerFactory.getLogger(UserManagerApiController.class);

	@Autowired
	UserMifiLinkService userMifiLinkService;
	
	@Autowired
	private UserBasicInfoService userService;
	
	@RequestMapping(value = "/userMifiLink.json", method = { RequestMethod.POST })
	@ApiOperation(position = 1, value = "用户设备绑定", notes = "用户设备绑定")
	public @ResponseBody ResponseEntity<JSONObject> userMifiLink(
			@ApiParam(value = "请求入参为JSON格式", required = true) @RequestBody JSONObject reqobj,
			HttpServletRequest request, HttpServletResponse response) {
		
		JSONObject res = new JSONObject();
		res.put("msg", ReturnCode.ERR__1);
		res.put("code", "-1");
		try {
			res.put("code", "0");
			res.put("msg", ReturnCode.ERR_0);
			//key忽略大小写
			reqobj = ToolUtil.transObject(reqobj);
			Object _params = reqobj.get("params");
			JSONArray params = new JSONArray();
			if (_params instanceof JSONObject) {
				params.add((JSONObject) _params);
			} else if (_params instanceof JSONArray) {
				params = (JSONArray) _params;
			}
			
			return new ResponseEntity(userMifiLinkService.saveUserMifiLink(params.getJSONObject(0)), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/getUserLinkInfo.json", method = { RequestMethod.POST })
	@ApiOperation(position = 1, value = "获取用户信息和绑定设备列表", notes = "获取用户信息和绑定设备列表")
	public @ResponseBody ResponseEntity<JSONObject> getUserLinkInfo(
			@ApiParam(value = "请求入参为JSON格式", required = true) @RequestBody JSONObject reqobj,
			HttpServletRequest request, HttpServletResponse response) {
		
		JSONObject res = new JSONObject();
		res.put("msg", ReturnCode.ERR__1);
		res.put("code", "-1");
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			res.put("code", "0");
			res.put("msg", ReturnCode.ERR_0);
			//key忽略大小写
			reqobj = ToolUtil.transObject(reqobj);
			Object _params = reqobj.get("params");
			JSONArray params = new JSONArray();
			if (_params instanceof JSONObject) {
				params.add((JSONObject) _params);
			} else if (_params instanceof JSONArray) {
				params = (JSONArray) _params;
			}
			JSONObject param = params.getJSONObject(0);
			logger.info(df.format(new Date()) + "[入参]：" + param.toString());
			if (!param.containsKey("userid") || StringUtils.isBlank(param.getString("userid"))) {
				String errorMessage = "|[用户标识]不能为空!";
				res.put("code", "61451");
				res.put("msg", ReturnCode.ERR_61451 + errorMessage);
				logger.info(df.format(new Date()) + "[出参]：" + res.toString());
				return new ResponseEntity(res, HttpStatus.OK);
			}
			
			return new ResponseEntity(userMifiLinkService.getUserLinkInfo(param.getString("userid")), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
			logger.info(df.format(new Date()) + "[出参]：" + res.toString());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}
	
	/**
	 * 
	 * @Description 渠道商设备用户绑定接口
	 * @param reqobj
	 * @param request
	 * @param response
	 * @return ResponseEntity<JSONObject>  
	 * @author yifang.huang
	 * @date 2016年9月27日 下午1:34:32
	 */
	@RequestMapping(value = "/userMifiActivate.json", method = { RequestMethod.POST })
	@ApiOperation(position = 1, value = "用户设备绑定", notes = "用户设备绑定")
	public @ResponseBody ResponseEntity<JSONObject> userMifiActivate(
			@ApiParam(value = "请求入参为URLEncoder处理的JSON格式", required = true) @RequestBody String reqStr,
			HttpServletRequest request, HttpServletResponse response) {
		
		JSONObject res = new JSONObject();
		res.put("msg", ReturnCode.ERR__1);
		res.put("code", "-1");
		try {
			
			JSONObject reqobj = JSONObject.fromObject(URLDecoder.decode(reqStr, "UTF-8"));
			
			// appid
			String channelCode = reqobj.getString("appid");
			
			res.put("code", "0");
			res.put("msg", ReturnCode.ERR_0);
			//key忽略大小写
			reqobj = ToolUtil.transObject(reqobj);
			Object _params = reqobj.get("params");
			JSONArray params = new JSONArray();
			if (_params instanceof JSONObject) {
				params.add((JSONObject) _params);
			} else if (_params instanceof JSONArray) {
				params = (JSONArray) _params;
			}
			
			return new ResponseEntity(userMifiLinkService.saveUserMifiActivate(params.getJSONObject(0), channelCode), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/getUserActivateInfo.json", method = { RequestMethod.POST })
	@ApiOperation(position = 1, value = "获取用户信息和绑定设备列表", notes = "获取用户信息和绑定设备列表")
	public @ResponseBody ResponseEntity<JSONObject> getUserActivateInfo(
			@ApiParam(value = "请求入参为URLEncoder处理的JSON格式", required = true) @RequestBody String reqStr,
			HttpServletRequest request, HttpServletResponse response) {

		
		JSONObject res = new JSONObject();
		res.put("msg", ReturnCode.ERR__1);
		res.put("code", "-1");
		try {
			
			JSONObject reqobj = JSONObject.fromObject(URLDecoder.decode(reqStr, "UTF-8"));
			
			// appid
			String channelCode = reqobj.getString("appid");
			
			res.put("code", "0");
			res.put("msg", ReturnCode.ERR_0);
			//key忽略大小写
			reqobj = ToolUtil.transObject(reqobj);
			Object _params = reqobj.get("params");
			JSONArray params = new JSONArray();
			if (_params instanceof JSONObject) {
				params.add((JSONObject) _params);
			} else if (_params instanceof JSONArray) {
				params = (JSONArray) _params;
			}
			
			return new ResponseEntity(userMifiLinkService.getUserActivateInfo(params.getJSONObject(0), channelCode), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}

	/**
	 * 
	 * @Description 修改用户设备使用天数
	 * @param reqStr
	 * @param request
	 * @param response
	 * @return ResponseEntity<JSONObject>  
	 * @author yifang.huang
	 * @date 2016年12月6日 上午9:56:11
	 */
	@RequestMapping(value = "/modifyUserDayPass.json", method = { RequestMethod.POST })
	@ApiOperation(position = 9, value = "修改用户设备使用天数", notes = "修改用户设备使用天数")
	public @ResponseBody ResponseEntity<JSONObject> modifyUserDayPass(
			@ApiParam(value = "请求入参为URLEncoder处理的JSON格式", required = true) @RequestBody String reqStr,
			HttpServletRequest request, HttpServletResponse response) {
		
		JSONObject res = new JSONObject();
		res.put("msg", ReturnCode.ERR__1);
		res.put("code", "-1");
		try {
			
			JSONObject reqobj = JSONObject.fromObject(URLDecoder.decode(reqStr, "UTF-8"));
			
			// appid
			String channelCode = reqobj.getString("appid");
			
			res.put("code", "0");
			res.put("msg", ReturnCode.ERR_0);
			Object _params = reqobj.get("params");
			JSONArray params = new JSONArray();
			if (_params instanceof JSONObject) {
				params.add((JSONObject) _params);
			} else if (_params instanceof JSONArray) {
				params = (JSONArray) _params;
			}
			return new ResponseEntity(userService.updateAndModifyUserDayPass(params.getJSONObject(0), channelCode), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}

}
