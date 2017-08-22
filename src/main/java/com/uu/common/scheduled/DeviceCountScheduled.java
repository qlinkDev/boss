/** 
 * @Package com.uu.common.scheduled 
 * @Description 
 * @author yifang.huang
 * @date 2016年5月20日 下午2:12:02 
 * @version V1.0 
 */ 
package com.uu.common.scheduled;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.uu.common.utils.MailBody;
import com.uu.common.utils.mail.MailThread;
import com.uu.modules.mifi.entity.DeviceMonitorDetail;
import com.uu.modules.mifi.entity.MifiBasicInfo;
import com.uu.modules.mifi.service.MifiManageService;
import com.uu.modules.mifi.service.MifiTrafficService;
import com.uu.modules.sys.condition.NoticeReceiveCondition;
import com.uu.modules.sys.entity.NoticeReceive;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.service.NoticeReceiveService;
import com.uu.modules.sys.service.SystemService;
import com.uu.modules.sys.utils.DictUtils;
import com.uu.modules.utils.Constants;

/** 
 * @Description 设备状态统计 定时处理
 * @author yifang.huang
 * @date 2016年5月20日 下午2:12:02 
 */
@Component
public class DeviceCountScheduled {

	public static Logger logger = LoggerFactory.getLogger(DeviceCountScheduled.class);

	public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private MifiManageService mifiManageService;
	
	@Autowired 
	MifiTrafficService mifiTrafficService;
	
	@Autowired
	private NoticeReceiveService noticeReceiveService;
	
	@Autowired
	private SystemService systemService;

	@Scheduled(cron = "0 0 03 * * ?")//秒、分、时、日、月、年
	//@Scheduled(fixedRate = 1 * 60 * 1000)
	// 凌晨三点执行
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void monitor() {

		logger.info("设备状态统计定时处理开始！");
		
		try {
			
			// 循环设备状态信息
			List<MifiBasicInfo> mifiList = mifiManageService.findMifiBasicInfoList(null); // 设备列表
			countDeviceStatus(mifiList);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		logger.info("设备状态统计定时处理结束！");

	}
	
	/**
	 * 
	 * @Description 根据设备监控状态信息
	 * @param mifiList
	 * @param monitor 
	 * @return void  
	 * @author yifang.huang
	 * @throws ParseException 
	 * @date 2016年5月20日 下午5:10:44
	 */
	private void countDeviceStatus(List<MifiBasicInfo> mifiList) throws ParseException {
		
		// 其它状态到4的时间
		String statusEq0 = DictUtils.getDictValue(Constants.DICT_DEVICE_MONITOR_STATUS_EQ0_LABEL, Constants.DICT_DEVICE_MONITOR_STATUS_EQ0_TYPE, "10");
		// 4到其它状态的时间
		String statusGt0 = DictUtils.getDictValue(Constants.DICT_DEVICE_MONITOR_STATUS_GT0_LABEL, Constants.DICT_DEVICE_MONITOR_STATUS_GT0_TYPE, "65");
		int stampEq0 = Integer.valueOf(statusEq0);
		int stampGt0 = Integer.valueOf(statusGt0);

		// 邮件内容
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		String preDate = getPreDate();
		String warningInfo = "";
		for (MifiBasicInfo mifi : mifiList) {
			
			String imei = mifi.getImei();	
			List<Map<String, String>> statusList = mifiTrafficService.findMifiStatusList(imei, preDate, preDate);
			if (statusList!=null && statusList.size()>0) {
				// 联网时长、所在国家、关机时间、流量、连接最大设备数
				Map<String, Object> resultMap = mifiTrafficService.countDeviceStatus(statusList, stampGt0);
				// 设备编号
				resultMap.put("imei", imei);
				// 异常
				List<DeviceMonitorDetail> dmdList = mifiTrafficService.checkDeviceStatus(statusList, stampEq0, stampGt0);
				if (dmdList!=null && dmdList.size()>0) {
					for (DeviceMonitorDetail detail : dmdList) {
						warningInfo += "#" + (detail.getPreStatus() + "[" + df.format(detail.getPreHappenDate()) +  "]~" 
								+ detail.getNextStatus() + "[" + df.format(detail.getNextHappenDate()) +  "]");
					}
					if (StringUtils.isNotBlank(warningInfo)) {
						warningInfo = warningInfo.substring(1);
						warningInfo = warningInfo.replace("#", "<br />");
					}
					resultMap.put("warningInfo", warningInfo);
					warningInfo = "";
				}
				list.add(resultMap);
			}
		}
		
		// 按国家排序
		Collections.sort(list, new countResultComparator());
		
		// 发信息
		sendMessage(list, preDate);
		
		// 给渠道商发邮件
		sendMailToChannel(list, preDate);
	}
	
	/**
	 * 
	 * @Description 取前一天
	 * @return 
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午5:28:45
	 */
	private String getPreDate() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, -1);
		return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
	}
	
	/**
	 * 
	 * @Description 发送信息
	 * @param monitor 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年5月23日 下午1:40:12
	 */
	private void sendMessage(List<Map<String, Object>> result, String date) {
		// 统计结果
		if (result.size() == 0)
			return;
		
		// 设备统计信息接收邮箱
		NoticeReceiveCondition condition = new NoticeReceiveCondition();
		condition.setEqType("DEVICE_COUNT");
		List<NoticeReceive> list = noticeReceiveService.findListByCondition(condition);
		if (list != null && list.size() > 0) {
			
			NoticeReceive nr = list.get(0);
			// 邮件发送
			String emails = nr.getEmails();
			if (StringUtils.isNotBlank(emails)) {
				// 邮件内容
				String msgText = MailBody.deviceCount(result);
				MailThread mThread = new MailThread("设备运行状态统计信息["+ date +"]", "[游友移动]", msgText, emails);
				mThread.start();
			}

		}
	}
	
	/**
	 * 
	 * @Description 设备运行状态统计信息给渠道商发邮件
	 * @param list 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年5月25日 上午11:32:54
	 */
	private void sendMailToChannel(List<Map<String, Object>> result, String date) {
		
		Map<String, List<Map<String, Object>>> channelStatusCountMap = new HashMap<String, List<Map<String, Object>>>();
		String sourceType = null;
		List<Map<String, Object>> tempList = null;
		for (Map<String, Object> statusCount : result) {
			Map<String, String> mifi = mifiManageService.getMifilistBySn(ObjectUtils.toString(statusCount.get("imei")));
			if (mifi!=null && "1".equals(mifi.get("ownerType"))) {
				
				sourceType = mifi.get("sourceType");
				tempList = channelStatusCountMap.get(sourceType);
				if (tempList==null || tempList.size()==0) {
					tempList = new ArrayList<Map<String, Object>>();
				}
				tempList.add(statusCount);
				channelStatusCountMap.put(sourceType, tempList);
				
				tempList = null;
			}
		}
		
		// 分别给渠道商发邮件
		for (Map.Entry<String, List<Map<String, Object>>> entry : channelStatusCountMap.entrySet()) {
			User user = systemService.getUserByChannelNameEn(entry.getKey());
			if (user!=null && StringUtils.isNotBlank(user.getEmail())) {
				
				// 邮件发送
				String emails = user.getEmail();
				if (StringUtils.isNotBlank(emails)) {
					// 邮件内容
					String msgText = MailBody.deviceCountForChannel(entry.getValue());
					MailThread mThread = new MailThread("设备运行状态统计信息["+ date +"]", "[游友移动]", msgText, emails);
					mThread.start();
				}
				
			}
		}
		
	}
	
	// 按国家名排序
	class countResultComparator implements Comparator<Map<String, Object>> {

		@Override
		public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
			String country0 = ObjectUtils.toString(arg0.get("country"));
			String country1 = ObjectUtils.toString(arg1.get("country"));
			return compare(country0, country1);
		}

		public int compare(String o1, String o2) {

			String s1 = (String) o1;
			String s2 = (String) o2;
			int len1 = s1.length();
			int len2 = s2.length();
			int n = Math.min(len1, len2);
			char v1[] = s1.toCharArray();
			char v2[] = s2.toCharArray();
			int pos = 0;

			while (n-- != 0) {
				char c1 = v1[pos];
				char c2 = v2[pos];
				if (c1 != c2) {
					return c1 - c2;
				}
				pos++;
			}
			return len1 - len2;
		} 
		
	}

}
