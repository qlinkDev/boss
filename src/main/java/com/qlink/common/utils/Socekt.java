/**
 * 
 */
package com.qlink.common.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.qlink.common.scheduled.SimNodeStatusScheduled;
import com.qlink.modules.mifi.service.CardManageService;
import com.qlink.modules.mifi.service.MifiManageService;

public class Socekt {
	
	public static Logger logger = LoggerFactory.getLogger(SimNodeStatusScheduled.class);
	
	@Autowired
	private static CardManageService cardManageService = SpringContextHolder.getBean(CardManageService.class);
	
	@Autowired
	private static MifiManageService mifiManageService = SpringContextHolder.getBean(MifiManageService.class);
	
	/**
	 * 
	 * @Description SIM状态控制
	 * @param simBankId
	 * @param simId
	 * @param status
	 * @return Map<String,String>  
	 * @date 2016年6月6日 上午11:17:38
	 */
	public static Map<String, String> simStatusController(int simBankId, int simId, int status) {
		
		Map<String, String> result = new HashMap<String, String>();

		try {
			
			// 查询服务器IP及端口
			String ipPort = cardManageService.findServicerIpBySimBankId(simBankId);
			if (StringUtils.isBlank(ipPort)) {
				logger.info("simBankId[" + simBankId + "]onlineIp未配置!");
				result.put("code", "-1");
				result.put("msg", "simBankId[" + simBankId + "]onlineIp未配置!");
				return result;
			}
			logger.info("simStatusController通信地址：" + ipPort);
			int index = ipPort.lastIndexOf(".");
			String ip = ipPort.substring(0, index);
			int port = Integer.valueOf(ipPort.substring(index+1));

			Socket socket = new Socket(ip, port);
			// 由Socket对象得到输出流，并构造PrintWriter对象
			BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			InputStream iis = socket.getInputStream();
			OutputStream ots = socket.getOutputStream();
			// sim卡状态修改指令(16进制)DD 00 00 00 07 8A D0 00 00 01 01 02
			byte[] bytes = new byte[] {(byte)221, 00, 00, 00, 07, (byte)138, (byte)208, (byte)((simBankId & 0x00FF0000) >> 16), (byte)((simBankId & 0x0000FF00) >> 8), (byte)(simBankId & 0x000000FF), (byte)simId, (byte)status};

			ots.write(bytes);
			ots.flush();

			// 执行结果
			byte[] charBuf = new byte[4096];
			int size = 0;
			size = iis.read(charBuf, 0, 4096);
			int end = charBuf[size-2];
			result.put("code", String.valueOf(end));
			if (end == 0) {
				result.put("msg", "执行成功");
			} else {
				result.put("msg", "未找到卡");
			}
			// 输出执行结果
			for (int i = 0; i < size; i++) {
				System.out.println(charBuf[i]);
			}
			logger.info("卡[" + simBankId + ", " + simId + "],状态改为[ " + status + "],通信结果：" + ((end==0) ? "执行成功" : "未找到卡"));
			is.close(); // 关闭Socket输入流
			socket.close(); // 关闭Socket

		} catch (Exception e) {
			 // 出错，则打印出错信息
			logger.info("SIM状态控制Error");
			e.printStackTrace();
			result.put("code", "-1");
			result.put("msg", "SIM状态控制出错");
			return result;
		}

		return result;
	}

	/**
	 * 
	 * @Description SIM状态控制[批量修改]
	 * @param listMap simBankId,simId Map列表，key分别是SIMBANKID,SIMID
	 * @param status 要修改成的状态
	 * @param methodName 调用接口的方法名称，用于显示通信结果r 日志输出
	 * @date 2016年6月6日 上午11:17:38
	 */
	public static void simStatusBatchController(List<Map<String, Object>> listMap, int status, String methodName) {
		
		// 对要通信的卡按IP分组Map<ipPort, List<Map<String, Object>>>
		Map<String, List<Map<String, Object>>> mapList = grouping(listMap);
		
		for (Map.Entry<String, List<Map<String, Object>>> entry : mapList.entrySet()) {
			
			try {
				String ipPort = entry.getKey();
				logger.info("simStatusBatchController通信地址：" + ipPort);
				List<Map<String, Object>> tempListMap = entry.getValue();
				// 服务器IP及端口
				int index = ipPort.lastIndexOf(".");
				String ip = ipPort.substring(0, index);
				int port = Integer.valueOf(ipPort.substring(index+1));
				
				// 通信
				Socket socket = new Socket(ip, port);
				// 由Socket对象得到输出流，并构造PrintWriter对象
				BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				InputStream iis = socket.getInputStream();
				OutputStream ots = socket.getOutputStream();
				
				for (Map<String, Object> map : tempListMap) {
					int simBankId = Integer.valueOf(ObjectUtils.toString(map.get("SIMBANKID")));
					int simId = Integer.valueOf(ObjectUtils.toString(map.get("SIMID")));
					// sim卡状态修改指令(16进制)DD 00 00 00 07 8A D0 00 00 01 01 02
					byte[] bytes = new byte[] {(byte)221, 00, 00, 00, 07, (byte)138, (byte)208, (byte)((simBankId & 0x00FF0000) >> 16), (byte)((simBankId & 0x0000FF00) >> 8), (byte)(simBankId & 0x000000FF), (byte)simId, (byte)status};

					ots.write(bytes);
					ots.flush();

					// 执行结果
					byte[] charBuf = new byte[4096];
					int size = 0;
					size = iis.read(charBuf, 0, 4096);
					int end = charBuf[size-2];
					logger.info("Method:" + methodName + ",卡[" + simBankId + ", " + simId + "],状态改为[" + status + "],通信结果：" + ((end==0) ? "执行成功" : "未找到卡"));
				}

				is.close(); // 关闭Socket输入流
				socket.close(); // 关闭Socket
			} catch (Exception e) {
				logger.info("SIM状态控制[批量修改]Error：");
				e.printStackTrace();
			}
			
		}

	}

	/**
	 * 
	 * @Description mifi状态控制
	 * @param ueId
	 * @param action
	 * @param imei
	 * @return Map<String,String>  
	 * @date 2016年6月6日 上午11:23:37
	 */
	public static Map<String, String> mifiStatusController(int ueId, int action, String imei) {
		Map<String, String> result = new HashMap<String, String>();
	
		try {
			
			// 查询服务器IP及端口
			String ipPort = mifiManageService.findServicerIpBy(imei);
			if (StringUtils.isBlank(ipPort)) {
				logger.info("device[" + imei + "]onlineIp未配置!");
				result.put("code", "-1");
				result.put("msg", "device[" + imei + "]onlineIp未配置!");
				return result;
			}
			logger.info("mifiStatusController通信地址：" + ipPort);
			int index = ipPort.lastIndexOf(".");
			String ip = ipPort.substring(0, index);
			int port = Integer.valueOf(ipPort.substring(index+1));

			Socket socket = new Socket(ip, port);
			// 由Socket对象得到输出流，并构造PrintWriter对象
			BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			InputStream iis = socket.getInputStream();
			OutputStream ots = socket.getOutputStream();
			// 设备通信指令(16进制)DD 00 00 00 07 8B D0 46 00 00 01 02
			byte[] bytes = new byte[] { (byte) 221, 00, 00, 00, 07, (byte) 139, (byte) 208, (byte)((ueId & 0xFF000000) >> 24), (byte)((ueId & 0x00FF0000) >> 16), (byte)((ueId & 0x0000FF00) >> 8), (byte)(ueId & 0x000000FF), (byte)action };

			ots.write(bytes);
			ots.flush();
			
			// 执行结果
			byte[] charBuf = new byte[4096];
			int size = 0;
			size = iis.read(charBuf, 0, 4096);
			int end = charBuf[size-2];
			result.put("code", String.valueOf(end));
			if (end == 0) {
				result.put("msg", "执行成功");
			} else {
				result.put("msg", "未找到设备");
			}
			// 输出执行结果
			for (int i = 0; i < size; i++) {
				System.out.println(charBuf[i]);
			}
			is.close(); // 关闭Socket输入流
			socket.close(); // 关闭Socket
			
			logger.info("UEID[" + ueId + "],ACTION[" + action + "],IMEI[" + imei + "],通信结果：" + ((end==0) ? "执行成功" : "未找到卡"));
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("code", "-1");
			result.put("msg", "mifi状态控制出错");
			return result;
		}
		
		return result;
	}

	/**
	 * 
	 * @Description 卡信息修改通知服务
	 * @param listMap
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年7月15日 下午12:00:47
	 */
	public static Map<String, String> simUpdate(List<Map<String, Integer>> listMap) {
		
		Map<String, String> result = new HashMap<String, String>();

		if (listMap==null || listMap.size()==0) {
			result.put("code", "-1");
			result.put("msg", "无卡");
			return result;
			
		}
		
		// 对要通信的卡按IP分组Map<ipPort, List<Map<String, Object>>>
		Map<String, List<Map<String, Integer>>> mapList = groupingFor(listMap);
		
		for (Map.Entry<String, List<Map<String, Integer>>> entry : mapList.entrySet()) {
			
			try {
				String ipPort = entry.getKey();
				List<Map<String, Integer>> tempListMap = entry.getValue();
				// 服务器IP及端口
				int index = ipPort.lastIndexOf(".");
				String ip = ipPort.substring(0, index);
				int port = Integer.valueOf(ipPort.substring(index+1));
				
				// 通信
				Socket socket = new Socket(ip, port);
				// 由Socket对象得到输出流，并构造PrintWriter对象
				BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				InputStream iis = socket.getInputStream();
				OutputStream ots = socket.getOutputStream();
				// 指令一（单卡）(16进制)：DD 00 00 00 07 8C D0 00 00 26 0B 00
				// 指令二（多卡）(16进制)：DD 00 00 00 * 8C D0 00 00 26 0B D0 00 00 26 0C 00
				byte[] bytes = getBytes(tempListMap);

				ots.write(bytes);
				ots.flush();

				// 执行结果
				byte[] charBuf = new byte[4096];
				int size = 0;
				size = iis.read(charBuf, 0, 4096);
				int end = charBuf[size-2];
				result.put("code", String.valueOf(end));
				if (end == 0) {
					result.put("msg", "执行成功");
				} else {
					result.put("msg", "执行失败");
				}
				// 输出执行结果
				for (int i = 0; i < size; i++) {
					System.out.println(charBuf[i]);
				}

				is.close(); // 关闭Socket输入流
				socket.close(); // 关闭Socket
				
				logger.info("SIM数据修改通知服务,simUpdate通信地址[" + ipPort +"],通信结果：" + ((end==0) ? "执行成功" : "未找到卡"));
			} catch (Exception e) {
				logger.info("SIM数据修改通知服务器Error");// 出错，则打印出错信息
				e.printStackTrace();
			}
			
		}

		result.put("code", "0");
		result.put("msg", "SIM数据修改通知服务器执行结束");
		return result;
	}
	
	private static byte[] getBytes(List<Map<String, Integer>> listMap) {
		
		// 初始化数据
		int len = listMap.size()*5 + 7;
		byte[] bytes = new byte[len];
		int tempLen = len - 5;
		int code = 221^7^140^208;
		bytes[0] = (byte)221;
		bytes[1] = (byte)((tempLen & 0xFF000000) >> 24);
		bytes[2] = (byte)((tempLen & 0x00FF0000) >> 16);
		bytes[3] = (byte)((tempLen & 0x0000FF00) >> 8);
		bytes[4] = (byte)(tempLen & 0x000000FF);
		bytes[5] = (byte)140;
		int i = 6;
		
		for(Map<String, Integer> map : listMap) {
			int simBankId = map.get("simBankId");
			int simId = Integer.valueOf(ObjectUtils.toString(map.get("simId")))  ;
			logger.info("simBankId:" + simBankId + ",simId:" + simId);
			code = code^simBankId^simId;
			byte[] temp = new byte[]{(byte)208, (byte)((simBankId & 0x00FF0000) >> 16), (byte)((simBankId & 0x0000FF00) >> 8), (byte)(simBankId & 0x000000FF), (byte)simId};
			System.arraycopy(temp, 0, bytes, i, 5);
			i += 5;
			simBankId = 0;
			simId = 0;
		}
		bytes[len - 1] = (byte)code;
		logger.info("bytes:" + Arrays.toString(bytes));
		return bytes;
	}
	
	// 对需要通信的卡按simBankId对应的onlineIp分组
	private static Map<String, List<Map<String, Object>>> grouping(List<Map<String, Object>> listMap) {
		
		Map<String, List<Map<String, Object>>> mapList = new HashMap<String, List<Map<String, Object>>>();

		List<Map<String, Object>> tempListMap = null;
		for (Map<String, Object> map : listMap) {
			String simBankIdStr = ObjectUtils.toString(map.get("SIMBANKID"));
			if (StringUtils.isBlank(simBankIdStr))
				continue;
			int simBankId = Integer.valueOf(simBankIdStr);
			String ipPort = cardManageService.findServicerIpBySimBankId(simBankId);   // 查询服务器IP及端口
			if (StringUtils.isNotBlank(ipPort)) {
				tempListMap = mapList.get(ipPort);
				if (tempListMap == null) {
					tempListMap = new ArrayList<Map<String, Object>>();
				}
				tempListMap.add(map);
				mapList.put(ipPort, tempListMap);
			}
		}
		
		return mapList;
		
	}
	
	// 对需要通信的卡按simBankId对应的onlineIp分组
	private static Map<String, List<Map<String, Integer>>> groupingFor(List<Map<String, Integer>> listMap) {
		
		Map<String, List<Map<String, Integer>>> mapList = new HashMap<String, List<Map<String, Integer>>>();

		List<Map<String, Integer>> tempListMap = null;
		for (Map<String, Integer> map : listMap) {
			String simBankIdStr = ObjectUtils.toString(map.get("simBankId"));
			if (StringUtils.isBlank(simBankIdStr))
				continue;
			int simBankId = Integer.valueOf(simBankIdStr);
			String ipPort = cardManageService.findServicerIpBySimBankId(simBankId);   // 查询服务器IP及端口
			if (StringUtils.isNotBlank(ipPort)) {
				tempListMap = mapList.get(ipPort);
				if (tempListMap == null) {
					tempListMap = new ArrayList<Map<String, Integer>>();
				}
				tempListMap.add(map);
				mapList.put(ipPort, tempListMap);
			}
		}
		
		return mapList;
		
	}	
	
}
