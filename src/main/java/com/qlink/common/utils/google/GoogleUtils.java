package com.qlink.common.utils.google;

import com.qlink.common.utils.HttpRequest;

import net.sf.json.JSONObject;

public class GoogleUtils {

    /**
     * API 密钥
     */
    private static final String API_KEY = "AIzaSyCESMPfxysHukhGVpj58YBV3htA78Zsy04";
	
	/**
	 * API 地址
	 */
    private static final String API_URL = "https://www.googleapis.com/geolocation/v1/geolocate?key=" + API_KEY;
    
	public static void main(String[] args) {

		String key = "AIzaSyCESMPfxysHukhGVpj58YBV3htA78Zsy04";
		String apiUrl = "https://www.googleapis.com/geolocation/v1/geolocate?key=" + key;
		// String apiUrl = "http://ww.baidu.com";
		JSONObject jsonParam = new JSONObject();
		jsonParam.put("homeMobileCountryCode", 310);
		jsonParam.put("homeMobileNetworkCode", 410);
		jsonParam.put("radioType", "gsm");
		jsonParam.put("carrier", "Vodafone");
		jsonParam.put("considerIp", "false");

		CellTowers cellTowers = new CellTowers();
		cellTowers.setCellId("42");
		cellTowers.setLocationAreaCode("415");
		cellTowers.setMobileCountryCode("310");
		cellTowers.setMobileNetworkCode("410");
		jsonParam.put("cellTowers", cellTowers);

		System.out.println(jsonParam.toString());

		String rs = HttpRequest.sendPost(apiUrl, jsonParam.toString());
		System.out.println("#########################");
		System.out.println(rs.toString());

	}

	/**
	 * 根据移动客户端可以检测到的有关移动电话基站和 Wi-Fi 节点的信息返回位置和精度半径。
	 * 本文档描述了用于将此数据发送到服务器并将响应返回给客户端的协议。 使用 POST 通过 HTTPS 进行通信。
	 * 
	 * @param url
	 * @param jsonParam
	 * @return
	 */
	public static String mapsGeolocationAPI(JSONObject jsonParam) {

		System.out.println(jsonParam.toString());

		String rs = HttpRequest.sendPost(API_URL, jsonParam.toString());

		System.out.println("mapsGeolocationAPI 调用完毕！");

		System.out.println("mapsGeolocationAPI 结果：" + rs);

		return rs;
	}

}
