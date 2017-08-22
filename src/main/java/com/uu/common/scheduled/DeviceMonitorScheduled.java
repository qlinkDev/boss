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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.uu.common.utils.IdGen;
import com.uu.common.utils.MailBody;
import com.uu.common.utils.mail.MailThread;
import com.uu.modules.mifi.condition.DeviceMonitorDetailCondition;
import com.uu.modules.mifi.entity.DeviceMonitor;
import com.uu.modules.mifi.entity.DeviceMonitorDetail;
import com.uu.modules.mifi.entity.MifiBasicInfo;
import com.uu.modules.mifi.service.DeviceMonitorDetailService;
import com.uu.modules.mifi.service.DeviceMonitorService;
import com.uu.modules.mifi.service.MifiManageService;
import com.uu.modules.mifi.service.MifiTrafficService;
import com.uu.modules.sys.condition.NoticeReceiveCondition;
import com.uu.modules.sys.entity.NoticeReceive;
import com.uu.modules.sys.service.NoticeReceiveService;
import com.uu.modules.sys.utils.DictUtils;
import com.uu.modules.utils.Constants;

/** 
 * @Description 设备状态监控 定时处理
 * @author yifang.huang
 * @date 2016年5月20日 下午2:12:02 
 */
@Component
public class DeviceMonitorScheduled {

	public static Logger logger = LoggerFactory.getLogger(DeviceMonitorScheduled.class);

	public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private MifiManageService mifiManageService;
	
	@Autowired 
	MifiTrafficService mifiTrafficService;
	
	@Autowired
	private DeviceMonitorService dmService;
	
	@Autowired
	private DeviceMonitorDetailService dmdService;

	@Autowired
	private NoticeReceiveService noticeReceiveService;

	@Scheduled(cron = "0 0 02 * * ?")//秒、分、时、日、月、年
	//@Scheduled(fixedRate = 1 * 60 * 1000)
	// 凌晨二点执行
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void monitor() {

		logger.info("设备状态监控定时处理开始！");
		
		try {
			Date startDate = new Date();
			// 设备监控主体
			DeviceMonitor monitor = new DeviceMonitor();
			monitor.setId(IdGen.uuid());
			monitor.setCode(new SimpleDateFormat("yyyyMMddHH").format(startDate));
			monitor.setStartDate(startDate);
			
			// 循环设备状态信息
			List<MifiBasicInfo> mifiList = mifiManageService.findMifiBasicInfoList(null); // 设备列表
			if (mifiList!=null && mifiList.size()>0) {
				monitor.setDeviceCount(mifiList.size());
				checkDeviceStatus(mifiList, monitor);
			}
			monitor.setEndDate(new Date());
			
			// 保存设备主体
			dmService.save(monitor);
			
			// 发信息
			sendMessage(monitor);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		logger.info("设备状态监控定时处理结束！");

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
	private void checkDeviceStatus(List<MifiBasicInfo> mifiList, DeviceMonitor monitor) throws ParseException {

		// 其它状态到4的时间
		String statusEq0 = DictUtils.getDictValue(Constants.DICT_DEVICE_MONITOR_STATUS_EQ0_LABEL, Constants.DICT_DEVICE_MONITOR_STATUS_EQ0_TYPE, "10");
		// 4到其它状态的时间
		String statusGt0 = DictUtils.getDictValue(Constants.DICT_DEVICE_MONITOR_STATUS_GT0_LABEL, Constants.DICT_DEVICE_MONITOR_STATUS_GT0_TYPE, "65");
		int stampEq0 = Integer.valueOf(statusEq0);
		int stampGt0 = Integer.valueOf(statusGt0);

		String preDate = getPreDate();
		int statusRecordCount = 0;
		int resultCount = 0;
		for (MifiBasicInfo mifi : mifiList) {
			String imei = mifi.getImei();		
			List<Map<String, String>> statusList = mifiTrafficService.findMifiStatusList(imei, preDate, preDate);
			if (statusList!=null && statusList.size()>0) {
				statusRecordCount += statusList.size();
				List<DeviceMonitorDetail> dmdList = mifiTrafficService.checkDeviceStatus(statusList, stampEq0, stampGt0);
				if (dmdList!=null && dmdList.size()>0) {
					resultCount += dmdList.size();
					for (DeviceMonitorDetail detail : dmdList) {
						detail.setDeviceMonitorId(monitor.getId());
						detail.setImei(imei);
						dmdService.save(detail);
					}
				}
			}
		}
		
		monitor.setStatusRecordCount(statusRecordCount);
		monitor.setResultCount(resultCount);
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
	private void sendMessage(DeviceMonitor monitor) {
		// 监控详情
		if (monitor.getResultCount() <= 0)
			return;
		DeviceMonitorDetailCondition dmCondition = new DeviceMonitorDetailCondition();
		dmCondition.setEqDeviceMonitorId(monitor.getId());
		List<DeviceMonitorDetail> detailList = dmdService.findListByCondition(dmCondition);
		if (detailList==null || detailList.size()==0)
			return;
		
		// 卡监控信息接收
		NoticeReceiveCondition condition = new NoticeReceiveCondition();
		condition.setEqType("DEVICE_MONITOR");
		List<NoticeReceive> list = noticeReceiveService.findListByCondition(condition);
		if (list != null && list.size() > 0) {
			
			NoticeReceive nr = list.get(0);
			// 邮件发送
			String emails = nr.getEmails();
			if (StringUtils.isNotBlank(emails)) {
				// 邮件内容
				String msgText = MailBody.deviceMonitor(detailList);
				MailThread mThread = new MailThread("设备运行状态监控["+ getPreDate() +"]", "[游友移动]", msgText, emails);
				mThread.start();
			}
			
		}
	}

}
