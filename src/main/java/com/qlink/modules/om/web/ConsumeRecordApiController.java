package main.java.com.qlink.modules.om.web;

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

import com.uu.modules.om.service.ConsumeRecordService;
import com.uu.modules.utils.ReturnCode;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author yuxiaoyu
 * @date 2016年3月29日
 */
@Controller
@RequestMapping(value = "/api/om", produces = "application/json")
@Api(value="/consume", description="消费记录相关接口")
public class ConsumeRecordApiController {

	public static Logger logger = LoggerFactory.getLogger(ConsumeRecordApiController.class);

	@Autowired
	private ConsumeRecordService consumeRecordService;
	
	/**
	 * 
	 * @Description 同步mifi平台充值记录
	 * @param reqObj
	 * @param request
	 * @param response
	 * @return ResponseEntity<JSONObject>  
	 * @author yifang.huang
	 * @date 2016年3月29日 下午3:50:28
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/recharge.json", method = { RequestMethod.POST })
	@ApiOperation(position = 1, value = "用户充值", notes = "用户充值")
	public @ResponseBody ResponseEntity<JSONObject> recharge(@ApiParam(value = "请求入参为JSON格式", required = true) @RequestBody JSONObject reqObj, 
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
			return new ResponseEntity(consumeRecordService.saveForApi(params.getJSONObject(0)), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", "61450");
			res.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		return new ResponseEntity(res, HttpStatus.OK);
	}

	/**
	 * 消费充值记录查询接口
	 * @Description 
	 * @author yuxiaoyu
	 * @date 2016年4月12日 下午5:50:09
	 */
	@RequestMapping(value = "/consumeRecord.json", method = { RequestMethod.POST })
	public @ResponseBody
	ResponseEntity<JSONObject> consumeRecord(@RequestBody JSONObject requestObj, HttpServletRequest request, HttpServletResponse response) {
		ResponseEntity<JSONObject> responseEntity;
		JSONObject responseObj = new JSONObject();
		responseObj.put("msg", ReturnCode.ERR__1);
		responseObj.put("code", "-1");
		try {
			responseObj.put("code", "0");
			responseObj.put("msg", ReturnCode.ERR_0);

			consumeRecordService.findConsumeRecordByParams(requestObj, responseObj);

		} catch (Exception e) {
			e.printStackTrace();
			responseObj.put("code", "61450");
			responseObj.put("msg", ReturnCode.ERR_61450 + ":" + e.getMessage());
		}
		responseEntity = new ResponseEntity<JSONObject>(responseObj, HttpStatus.OK);
		return responseEntity;
	}

}
