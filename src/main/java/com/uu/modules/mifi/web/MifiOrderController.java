package com.uu.modules.mifi.web;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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

import com.uu.modules.mifi.service.MifiOrderService;
import com.uu.modules.utils.ReturnCode;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author wangyong
 * @date 2016年2月4日
 */

@Controller
@RequestMapping(value = "/api/order", produces = "application/json")
@Api(value = "/order", description = "mifi订单")
public class MifiOrderController {

	public static Logger logger = LoggerFactory.getLogger(MifiOrderController.class);

	@Autowired
	MifiOrderService mifiOrderService;
	
	@RequestMapping(value = "/createOrder.json", method = { RequestMethod.POST })
	@ApiOperation(position = 1, value = "创建MIFI订单", notes = "创建MIFI订单")
	public @ResponseBody ResponseEntity<JSONObject> createOrder(
			@ApiParam(value = "请求入参为JSON格式", required = true) @RequestBody JSONObject reqobj,
			HttpServletRequest request, HttpServletResponse response) {
		JSONObject res = new JSONObject();
		res.put("msg", ReturnCode.ERR__1);
		res.put("code", "-1");
		try {
			res.put("code", "0");
			res.put("msg", ReturnCode.ERR_0);
			String appId = reqobj.getString("appid");
			Object _params = reqobj.get("params");
			JSONArray params = new JSONArray();
			if (_params instanceof JSONObject) {
				params.add((JSONObject) _params);
			} else if (_params instanceof JSONArray) {
				params = (JSONArray) _params;
			}
			return new ResponseEntity(mifiOrderService.saveAndCreateOrder(appId, params.getJSONObject(0)), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}

	@RequestMapping(value = "/deliveryOrder.json", method = { RequestMethod.POST })
	@ApiOperation(position = 2, value = "MIFI订单发货", notes = "MIFI订单发货")
	public @ResponseBody ResponseEntity<JSONObject> deliveryOrder(
			@ApiParam(value = "请求入参为JSON格式", required = true) @RequestBody JSONObject reqobj,
			HttpServletRequest request, HttpServletResponse response) {
		JSONObject res = new JSONObject();
		res.put("msg", ReturnCode.ERR__1);
		res.put("code", "-1");
		try {
			res.put("code", "0");
			res.put("msg", ReturnCode.ERR_0);
			String appId = reqobj.getString("appid");
			Object _params = reqobj.get("params");
			JSONArray params = new JSONArray();
			if (_params instanceof JSONObject) {
				params.add((JSONObject) _params);
			} else if (_params instanceof JSONArray) {
				params = (JSONArray) _params;
			}
			return new ResponseEntity(mifiOrderService.saveAndDeliveryOrder(appId, null, params.getJSONObject(0)), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}

	/**
	 * 
	 * @Description 发货接口3.0(URLEncoder参数)
	 * @param reqStr
	 * @param request
	 * @param response
	 * @return ResponseEntity<JSONObject>  
	 * @author yifang.huang
	 * @date 2016年9月22日 下午5:49:45
	 */
	@RequestMapping(value = "/deliveryOrderV3.json", method = { RequestMethod.POST })
	@ApiOperation(position = 2, value = "MIFI订单发货", notes = "MIFI订单发货")
	public @ResponseBody ResponseEntity<JSONObject> deliveryOrderV3(
			@ApiParam(value = "请求入参为URLEncoder处理的JSON格式", required = true) @RequestBody String reqStr,
			HttpServletRequest request, HttpServletResponse response) {
		
		JSONObject res = new JSONObject();
		res.put("msg", ReturnCode.ERR__1);
		res.put("code", "-1");
		try {
			
			JSONObject reqobj = JSONObject.fromObject(URLDecoder.decode(reqStr, "UTF-8"));
			
			res.put("code", "0");
			res.put("msg", ReturnCode.ERR_0);
			String appId = reqobj.getString("appid");
			Object _params = reqobj.get("params");
			JSONArray params = new JSONArray();
			if (_params instanceof JSONObject) {
				params.add((JSONObject) _params);
			} else if (_params instanceof JSONArray) {
				params = (JSONArray) _params;
			}
			return new ResponseEntity(mifiOrderService.saveAndDeliveryOrder(appId, null, params.getJSONObject(0)), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}

	/**
	 * 
	 * @Description 流量订单发货接口(URLEncoder参数)
	 * @param reqStr
	 * @param request
	 * @param response
	 * @return ResponseEntity<JSONObject>  
	 * @author yifang.huang
	 * @date 2016年9月22日 下午5:49:45
	 */
	@RequestMapping(value = "/deliveryOrderForFlow.json", method = { RequestMethod.POST })
	@ApiOperation(position = 2, value = "MIFI流量订单发货", notes = "MIFI流量订单发货")
	public @ResponseBody ResponseEntity<JSONObject> deliveryOrderForFlow(
			@ApiParam(value = "请求入参为URLEncoder处理的JSON格式", required = true) @RequestBody String reqStr,
			HttpServletRequest request, HttpServletResponse response) {
		
		JSONObject res = new JSONObject();
		res.put("msg", ReturnCode.ERR__1);
		res.put("code", "-1");
		try {
			
			JSONObject reqobj = JSONObject.fromObject(URLDecoder.decode(reqStr, "UTF-8"));
			
			res.put("code", "0");
			res.put("msg", ReturnCode.ERR_0);
			String appId = reqobj.getString("appid");
			Object _params = reqobj.get("params");
			JSONArray params = new JSONArray();
			if (_params instanceof JSONObject) {
				params.add((JSONObject) _params);
			} else if (_params instanceof JSONArray) {
				params = (JSONArray) _params;
			}
			JSONObject param = params.getJSONObject(0);
			// 判断是否有流量参数并且值是否大于0
			if (!param.containsKey("flow") || StringUtils.isBlank(param.getString("flow"))) {// 订单流量
				res.put("code", "61451");
				res.put("msg", ReturnCode.ERR_61451 + "|[订单流量]不能为空!");
				return new ResponseEntity(res, HttpStatus.OK);
			}
			String flowStr = param.getString("flow");
			int flow = 0;
			try {
				flow = Integer.valueOf(flowStr);// 订单流量格式判断
				if (flow <= 0) {
					res.put("code", "61451");
					res.put("msg", ReturnCode.ERR_61451 + "|[订单流量]必须是正整数!");
					return new ResponseEntity(res, HttpStatus.OK);
				}
			} catch (NumberFormatException e) {
				res.put("code", "61451");
				res.put("msg", ReturnCode.ERR_61451 + "|[订单流量]必须是正整数!");
				return new ResponseEntity(res, HttpStatus.OK);
			}
			
			return new ResponseEntity(mifiOrderService.saveAndDeliveryOrder(appId, flow, param), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}

	@RequestMapping(value = "/delayOrder.json", method = { RequestMethod.POST })
	@ApiOperation(position = 3, value = "MIFI订单续租", notes = "MIFI订单续租")
	public @ResponseBody ResponseEntity<JSONObject> delayOrder(
			@ApiParam(value = "请求入参为JSON格式", required = true) @RequestBody JSONObject reqobj,
			HttpServletRequest request, HttpServletResponse response) {
		JSONObject res = new JSONObject();
		res.put("msg", ReturnCode.ERR__1);
		res.put("code", "-1");
		try {
			res.put("code", "0");
			res.put("msg", ReturnCode.ERR_0);
			String appId = reqobj.getString("appid");
			Object _params = reqobj.get("params");
			JSONArray params = new JSONArray();
			if (_params instanceof JSONObject) {
				params.add((JSONObject) _params);
			} else if (_params instanceof JSONArray) {
				params = (JSONArray) _params;
			}
			return new ResponseEntity(mifiOrderService.saveAndDelayOrder(appId, params.getJSONObject(0)), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}

	/**
	 * 
	 * @Description 流量订单延期：1 流量订单延期，2设备总流量充值
	 * @param reqobj
	 * @param request
	 * @param response
	 * @return ResponseEntity<JSONObject>  
	 * @author yifang.huang
	 * @date 2017年5月26日 下午5:57:10
	 */
	@RequestMapping(value = "/delayDevice.json", method = { RequestMethod.POST })
	@ApiOperation(position = 3, value = "MIFI设备续租", notes = "MIFI设备续租")
	public @ResponseBody ResponseEntity<JSONObject> delayDevice(
			@ApiParam(value = "请求入参为JSON格式", required = true) @RequestBody JSONObject reqobj,
			HttpServletRequest request, HttpServletResponse response) {
		JSONObject res = new JSONObject();
		res.put("msg", ReturnCode.ERR__1);
		res.put("code", "-1");
		try {
			res.put("code", "0");
			res.put("msg", ReturnCode.ERR_0);
			String appId = reqobj.getString("appid");
			Object _params = reqobj.get("params");
			JSONArray params = new JSONArray();
			if (_params instanceof JSONObject) {
				params.add((JSONObject) _params);
			} else if (_params instanceof JSONArray) {
				params = (JSONArray) _params;
			}
			return new ResponseEntity(mifiOrderService.saveAndDelayDevice(appId, params.getJSONObject(0)), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}

	@RequestMapping(value = "/cancelOrder.json", method = { RequestMethod.POST })
	@ApiOperation(position = 4, value = "MIFI订单取消", notes = "MIFI订单取消")
	public @ResponseBody ResponseEntity<JSONObject> cancelOrder(
			@ApiParam(value = "请求入参为JSON格式", required = true) @RequestBody JSONObject reqobj,
			HttpServletRequest request, HttpServletResponse response) {
		JSONObject res = new JSONObject();
		res.put("msg", ReturnCode.ERR__1);
		res.put("code", "-1");
		try {
			res.put("code", "0");
			res.put("msg", ReturnCode.ERR_0);
			String appId = reqobj.getString("appid");
			Object _params = reqobj.get("params");
			JSONArray params = new JSONArray();
			if (_params instanceof JSONObject) {
				params.add((JSONObject) _params);
			} else if (_params instanceof JSONArray) {
				params = (JSONArray) _params;
			}
			return new ResponseEntity(mifiOrderService.saveAndCancelOrder(appId, params.getJSONObject(0)), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/finishOrder.json", method = { RequestMethod.POST })
	@ApiOperation(position = 5, value = "MIFI订单完成", notes = "MIFI订单完成")
	public @ResponseBody ResponseEntity<JSONObject> finishOrder(
			@ApiParam(value = "请求入参为JSON格式", required = true) @RequestBody JSONObject reqobj,
			HttpServletRequest request, HttpServletResponse response) {
		JSONObject res = new JSONObject();
		res.put("msg", ReturnCode.ERR__1);
		res.put("code", "-1");
		try {
			res.put("code", "0");
			res.put("msg", ReturnCode.ERR_0);
			String appId = reqobj.getString("appid");
			Object _params = reqobj.get("params");
			JSONArray params = new JSONArray();
			if (_params instanceof JSONObject) {
				params.add((JSONObject) _params);
			} else if (_params instanceof JSONArray) {
				params = (JSONArray) _params;
			}
			return new ResponseEntity(mifiOrderService.saveAndFinishOrder(appId, params.getJSONObject(0)), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}
	
	/**
	 * 
	 * @Description 运营商产品接口
	 * @param reqObj
	 * @param request
	 * @param response
	 * @return ResponseEntity<JSONObject>  
	 * @author yifang.huang
	 * @date 2016年5月9日 下午2:14:38
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/goods.json", method = { RequestMethod.POST })
	@ApiOperation(position = 6, value = "运营商产品", notes = "运营商产品")
	public @ResponseBody ResponseEntity<JSONObject> goods(
			@ApiParam(value = "请求入参为URLEncoder处理的JSON格式", required = true) @RequestBody String reqStr,
			HttpServletRequest request, HttpServletResponse response) {
		
		JSONObject res = new JSONObject();
		res.put("msg", ReturnCode.ERR__1);
		res.put("code", "-1");
		
		try {
			
			JSONObject reqObj = JSONObject.fromObject(URLDecoder.decode(reqStr, "UTF-8"));
			
			res.put("code", "0");
			res.put("msg", ReturnCode.ERR_0);
			Object _params = reqObj.get("params");
			JSONArray params = new JSONArray();
			if (_params instanceof JSONObject) {
				params.add((JSONObject) _params);
			} else if (_params instanceof JSONArray) {
				params = (JSONArray) _params;
			}
			return new ResponseEntity(mifiOrderService.getGoods(params.getJSONObject(0)), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}
	
	/**
	 * 
	 * @Description 订单终止
	 * @param reqobj
	 * @param request
	 * @param response
	 * @return ResponseEntity<JSONObject>  
	 * @author yifang.huang
	 * @date 2016年6月13日 上午11:04:34
	 */
	@RequestMapping(value = "/endOrder.json", method = { RequestMethod.POST })
	@ApiOperation(position = 7, value = "MIFI订单终止", notes = "MIFI订单终止")
	public @ResponseBody ResponseEntity<JSONObject> endOrder(
			@ApiParam(value = "请求入参为JSON格式", required = true) @RequestBody JSONObject reqobj,
			HttpServletRequest request, HttpServletResponse response) {
		JSONObject res = new JSONObject();
		res.put("msg", ReturnCode.ERR__1);
		res.put("code", "-1");
		try {
			res.put("code", "0");
			res.put("msg", ReturnCode.ERR_0);
			String appId = reqobj.getString("appid");
			Object _params = reqobj.get("params");
			JSONArray params = new JSONArray();
			if (_params instanceof JSONObject) {
				params.add((JSONObject) _params);
			} else if (_params instanceof JSONArray) {
				params = (JSONArray) _params;
			}
			return new ResponseEntity(mifiOrderService.saveAndEndOrder(appId, params.getJSONObject(0)), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}
	
	/**
	 * 
	 * @Description 取设备订单信息
	 * @param reqObj
	 * @param request
	 * @param response
	 * @return ResponseEntity<JSONObject>  
	 * @author yifang.huang
	 * @date 2016年9月22日 上午10:59:50
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/deviceOrderInfo.json", method = { RequestMethod.POST })
	@ApiOperation(position = 6, value = "设备订单信息", notes = "设备订单信息")
	public @ResponseBody ResponseEntity<JSONObject> deviceOrderInfo(@ApiParam(value = "请求入参为JSON格式", required = true) @RequestBody JSONObject reqObj, 
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
			return new ResponseEntity(mifiOrderService.getDeviceOrderInfo(params.getJSONObject(0)), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}
}
