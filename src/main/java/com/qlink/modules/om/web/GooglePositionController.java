/** 
 * @Package com.uu.modules.om.web 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月26日 下午3:04:39 
 * @version V1.0 
 */
package main.java.com.qlink.modules.om.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uu.common.utils.google.CellTowers;
import com.uu.common.utils.google.GoogleUtils;
import com.uu.common.web.BaseController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @Description 谷歌地位 对外服务包实现
 * @author yifang.huang
 * @date 2016年4月26日 下午3:04:39
 */
@Controller
@RequestMapping(value = "${adminPath}/om/google")
public class GooglePositionController extends BaseController {

	/**
	 * 
	 * @Description 定位数据输入
	 * @param request
	 * @param response
	 * @param model
	 * @return String
	 * @author yifang.huang
	 * @date 2016年4月26日 下午3:10:47
	 */
	@RequestMapping("google")
	public String google(HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/om/google";
	}

	/**
	 * 
	 * @Description 定位
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param model
	 * @return String
	 * @author yifang.huang
	 * @date 2016年4月26日 下午3:11:34
	 */
	@RequestMapping(value = "/position.json", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, String> position(@RequestParam Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {

		String homeMobileCountryCode = ObjectUtils.toString(paramMap.get("homeMobileCountryCode"));
		String homeMobileNetworkCode = ObjectUtils.toString(paramMap.get("homeMobileNetworkCode"));
		String radioType = ObjectUtils.toString(paramMap.get("radioType"));
		String carrier = ObjectUtils.toString(paramMap.get("carrier"));
		String considerIp = ObjectUtils.toString(paramMap.get("considerIp"));
		
		// 定位参数
		JSONObject jsonParam = new JSONObject();
		if (StringUtils.isNotBlank(homeMobileCountryCode))
			jsonParam.put("homeMobileCountryCode", homeMobileCountryCode);
		if (StringUtils.isNotBlank(homeMobileNetworkCode))
			jsonParam.put("homeMobileNetworkCode", homeMobileNetworkCode);
		if (StringUtils.isNotBlank(radioType)) {
			jsonParam.put("radioType", radioType.toLowerCase());
		}
		if (StringUtils.isNotBlank(carrier))
			jsonParam.put("carrier", carrier);
		if (StringUtils.isNotBlank(considerIp))
			jsonParam.put("considerIp", considerIp);
		// 移动电话基站对象
		String haveCellTowers = ObjectUtils.toString(paramMap.get("haveCellTowers"));
		if ("1".equals(haveCellTowers)) {
			CellTowers cellTowers = new CellTowers();
			cellTowers.setCellId(ObjectUtils.toString(paramMap.get("cellId")));
			cellTowers.setLocationAreaCode(ObjectUtils.toString(paramMap.get("locationAreaCode")));
			cellTowers.setMobileCountryCode(ObjectUtils.toString(paramMap.get("mobileCountryCode")));
			cellTowers.setMobileNetworkCode(ObjectUtils.toString(paramMap.get("mobileNetworkCode")));
			JSONArray array = new JSONArray();
			array.add(cellTowers);
			jsonParam.put("cellTowers", array);
		}
		// 返回结果
		Map<String, String> map = new HashMap<String, String>();
		try {
			// 定位结果
			String result = GoogleUtils.mapsGeolocationAPI(jsonParam);
			if (result != null) {
				handleResult(map, result);
			} else {
				map.put("code", "-1");
				map.put("code", "定位失败");
			}
		} catch (Exception e) {
			map.put("code", "-1");
			map.put("code", "定位失败");
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * 
	 * @Description 显示位置
	 * @param request
	 * @param response
	 * @param model
	 * @return String
	 * @author yifang.huang
	 * @date 2016年4月26日 下午3:10:47
	 */
	@RequestMapping("showPosition")
	public String showPosition(@RequestParam String lat, @RequestParam String lng, @RequestParam String accuracy,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		
		model.addAttribute("lat", lat);
		model.addAttribute("lng", lng);
		model.addAttribute("accuracy", accuracy);
		
		return "modules/om/position";
	}

	/**
	 * 
	 * @Description 处理结果
	 * @param map
	 * @param result
	 * @return void
	 * @author yifang.huang
	 * @date 2016年4月26日 下午4:10:42
	 */
	private void handleResult(Map<String, String> map, String result) {
		JSONObject json = JSONObject.fromObject(result);
		if (result.indexOf("error") != -1) { // 错误信息
			String error = json.getString("error");
			json = JSONObject.fromObject(error);
			map.put("code", json.getString("code"));
			map.put("msg", json.getString("message"));
		} else { // 正确信息
			map.put("code", "1");
			map.put("accuracy", json.getString("accuracy"));
			String location = json.getString("location");
			json = JSONObject.fromObject(location);
			map.put("lat", json.getString("lat"));
			map.put("lng", json.getString("lng"));
		}
	}

}
