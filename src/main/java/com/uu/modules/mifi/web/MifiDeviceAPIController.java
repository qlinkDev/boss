package com.uu.modules.mifi.web;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uu.common.web.BaseController;
import com.uu.modules.mifi.service.MifiDeviceService;
import com.uu.modules.mifi.service.MifiOrderService;
import com.uu.modules.utils.ReturnCode;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "/api/mifiDevice")
@Api(value = "/mifiDeviceAPI", description = "mifi设备")
public class MifiDeviceAPIController  extends BaseController {
	
	@Autowired
	private MifiOrderService mifiOrderService;
	
	@Autowired
	private MifiDeviceService mifiDeviceService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/deviceValidBaseInfo.json", method = { RequestMethod.POST })
	@ApiOperation(position = 6, value = "设备有效状态的基站信息", notes = "mifi设备")
	public @ResponseBody ResponseEntity<JSONObject> goods(@ApiParam(value = "请求入参为JSON格式", required = true) @RequestBody JSONObject reqObj, 
			HttpServletRequest request, HttpServletResponse response) {
		
		JSONObject res = new JSONObject();
		res.put("msg", ReturnCode.ERR__1);
		res.put("code", "-1");
		
		try {
			res.put("code", "0");
			res.put("msg", ReturnCode.ERR_0);
			Object _params = reqObj.get("params");
			JSONArray params = new JSONArray();
			if (_params instanceof JSONObject) {
				params.add((JSONObject) _params);
			} else if (_params instanceof JSONArray) {
				params = (JSONArray) _params;
			}
			return new ResponseEntity(mifiOrderService.getMDeviceBaseStationInfo(params.getJSONObject(0)), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}

	/**
	 * 
	 * @Description 修改设备ownerMcc接口
	 * @param reqStr
	 * @param request
	 * @param response
	 * @return ResponseEntity<JSONObject>  
	 * @author yifang.huang
	 * @date 2016年9月27日 下午2:40:14
	 */
	@RequestMapping(value = "/updateMifiOwnerMcc.json", method = { RequestMethod.POST })
	@ApiOperation(position = 7, value = "修改设备ownerMcc", notes = "修改设备ownerMcc")
	public @ResponseBody ResponseEntity<JSONObject> updateMifiOwnerMcc(
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
			return new ResponseEntity(mifiDeviceService.updateMifiOwnerMcc(params.getJSONObject(0), channelCode), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}

	/**
	 * 
	 * @Description homeForbidden打开或者关闭
	 * @param reqStr
	 * @param request
	 * @param response
	 * @return ResponseEntity<JSONObject>  
	 * @author yifang.huang
	 * @date 2016年9月27日 下午2:40:14
	 */
	@RequestMapping(value = "/homeForbiddenOnOrOff.json", method = { RequestMethod.POST })
	@ApiOperation(position = 8, value = "设备开关机", notes = "设备开关机")
	public @ResponseBody ResponseEntity<JSONObject> homeForbiddenOnOrOff(
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
			return new ResponseEntity(mifiDeviceService.updateAndHomeForbiddenOnOrOff(params.getJSONObject(0), channelCode), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}

	/**
	 * 
	 * @Description 设备开机确认使用
	 * @param reqStr
	 * @param request
	 * @param response
	 * @return ResponseEntity<JSONObject>  
	 * @author yifang.huang
	 * @date 2016年10月9日 下午2:24:01
	 */
	@RequestMapping(value = "/bootConfirmation.json", method = { RequestMethod.POST })
	@ApiOperation(position = 9, value = "设备开机确认使用", notes = "设备开机确认使用信息")
	public @ResponseBody ResponseEntity<JSONObject> bootConfirmation(
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
			return new ResponseEntity(mifiDeviceService.saveAndBootConfirmation(params.getJSONObject(0), channelCode), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}

	/**
	 * 
	 * @Description 设备流量统计
	 * @param reqStr
	 * @param request
	 * @param response
	 * @return ResponseEntity<JSONObject>  
	 * @author yifang.huang
	 * @date 2016年10月10日 下午5:50:34
	 */
	@RequestMapping(value = "/flowStatistcs.json", method = { RequestMethod.POST })
	@ApiOperation(position = 10, value = "设备流量统计", notes = "设备流量统计信息")
	public @ResponseBody ResponseEntity<JSONObject> flowStatistcs(
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
			return new ResponseEntity(mifiDeviceService.flowStatistcs(params.getJSONObject(0), channelCode), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}

	/**
	 * 
	 * @Description 设备版本查询
	 * @param reqStr
	 * @param request
	 * @param response
	 * @return ResponseEntity<JSONObject>  
	 * @author yifang.huang
	 * @date 2016年11月15日 下午5:51:56
	 */
	@RequestMapping(value = "/version.json", method = { RequestMethod.POST })
	@ApiOperation(position = 11, value = "设备版本", notes = "设备版本查询")
	public @ResponseBody ResponseEntity<JSONObject> version(
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
			return new ResponseEntity(mifiDeviceService.version(params.getJSONObject(0), channelCode), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}
	
}
