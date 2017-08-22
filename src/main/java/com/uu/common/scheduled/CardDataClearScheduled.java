package com.uu.common.scheduled;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.uu.common.utils.DigestUtils;
import com.uu.modules.sys.security.api.StatelessAuthcFilter;
import com.uu.modules.utils.Constants;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import net.sf.json.JSONObject;

/**
 * 卡流量清零定时任务
 * @Description 
 * @author yuxiaoyu
 * @date 2016年5月3日 下午5:47:21
 */
@Component
public class CardDataClearScheduled {

	/**
	 * 主方法
	 * @Description 
	 * @author yuxiaoyu
	 * @date 2016年5月4日 下午2:05:09
	 */
	@Scheduled(cron = "0 01 * * * ?")//秒、分、时、日、月、年
	//@Scheduled(fixedRate = 5 * 60 * 1000)
	public void clearCardData() {

		try {
			// scheduled service
			String url = Constants.SCHEDULED_SERVICE_URL;
			HttpRequest request = HttpRequest.get(url + "api/cdc/execute.json");

	    	JSONObject obj = new JSONObject();
	    	obj.put("appid", "YOUYOUMOB");
	    	obj.put("timestamp", "1345678990");
			
			// 数据参数
			Map<String, String> recordMap = new HashMap<String, String>();
			recordMap.put("method", "CardDataClearScheduled");
			JSONObject params = JSONObject.fromObject(recordMap);
			
			obj.put("params", params);
			
			String sign = DigestUtils.getSignature(obj, "hi+MFTP1Kmo96TexOXzS81xU8d+unqV9Mtz9XCxtt0jN4ykfWXQF3ru87QIqrZsw2KZWcdGGubj9Ae9BKbw==", "UTF-8");
			
			// 参数
			JSONObject jsonParam = new JSONObject();
			jsonParam.put(StatelessAuthcFilter.PARAM_APPID, obj.get("appid"));
			jsonParam.put(StatelessAuthcFilter.PARAM_NAME, params);
			jsonParam.put(StatelessAuthcFilter.PARAM_TIMESTAMP, obj.get("timestamp"));
			jsonParam.put(StatelessAuthcFilter.PARAM_SIGN, sign);

			String bodyStr = jsonParam.toString();
			System.out.println(bodyStr);
			bodyStr = URLEncoder.encode(bodyStr, "UTF-8");
			
			request.method("post");
			request.queryEncoding("UTF-8");
			request.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:44.0) Gecko/20100101 Firefox/44.0");
			request.header("Content-Type", "application/json;charset=utf-8");

			request.body(bodyStr);
			HttpResponse response = request.send();
			String result = response.bodyText();
			
			System.out.println(result);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
}
