/** 
 * @Package com.uu.common.scheduled 
 * @Description 
 * @author yuxiaoyu
 * @date 2017年3月2日 下午3:50:11 
 * @version V1.0 
 */
package main.java.com.qlink.common.scheduled;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.uu.common.utils.DateUtils;
import com.uu.modules.mifi.entity.MifiUsageRecordSegment;
import com.uu.modules.mifi.entity.MifiUsageRecordSegmentLog;
import com.uu.modules.mifi.service.CardMonitorService;
import com.uu.modules.mifi.service.MifiManageService;
import com.uu.modules.mifi.service.MifiTrafficService;
import com.uu.modules.mifi.service.MifiUsageRecordSegmentLogService;
import com.uu.modules.mifi.service.MifiUsageRecordSegmentService;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.om.service.PriceService;
import com.uu.modules.sys.entity.Dict;
import com.uu.modules.sys.utils.DictUtils;
import com.uu.modules.utils.Constants;

/** 
 * @Description 
 * @author yuxiaoyu
 * @date 2017年3月2日 下午3:50:11 
 */
@Component
public class DeviceUsageRecordSegmentedScheduled {
	public static Logger logger = LoggerFactory.getLogger(DeviceUsageRecordDaysScheduled.class);

	public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	MifiTrafficService mifiTrafficService;

	@Autowired
	private ChannelService channelService;

	@Autowired
	private PriceService priceService;

	@Autowired
	private MifiUsageRecordSegmentService recordService;

	@Autowired
	private MifiUsageRecordSegmentLogService errorLogService;

	@Autowired
	private MifiManageService mifiManageService;

	@Autowired
	private CardMonitorService cardMonitorService;

	@Scheduled(cron = "0 0 03 * * ?")
	// 秒、分、时、日、月、年
	// 凌晨一点半执行
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void monitor() {

		logger.info("设备使用记录（分时间段）定时处理开始！");

		createJob();

		logger.info("设备使用记录（分时间段）定时处理结束！");

	}

	private void createJob() {
		String preDate = DateUtils.getPreDate();// 昨天
		MifiUsageRecordSegmentLog errorLog = doJob(preDate, mifiTrafficService, channelService, mifiManageService, cardMonitorService, priceService,
				recordService);
		errorLogService.save(errorLog);
	}

	/** 
	 * @Description 
	 * @return 
	 * @return boolean  
	 * @author yuxiaoyu
	 * @date 2017年3月6日 上午10:58:00 
	 */
	public static MifiUsageRecordSegmentLog doJob(String schedTime, MifiTrafficService mifiTrafficService, ChannelService channelService,
			MifiManageService mifiManageService, CardMonitorService cardMonitorService, PriceService priceService,
			MifiUsageRecordSegmentService recordService) {
		Integer size = 0;
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("beginDate", schedTime);
			paramMap.put("endDate", schedTime);
			List<Dict> sourceTypeList = DictUtils.getDictList("channel_segment_useage");
			StringBuilder sourceTypeIn = new StringBuilder();
			for (Dict dict : sourceTypeList) {
				sourceTypeIn.append(Constants.SINGLE_QUOTES).append(dict.getValue()).append(Constants.SINGLE_QUOTES).append(Constants.COMMA);
			}
			sourceTypeIn.deleteCharAt(sourceTypeIn.length() - 1);
			paramMap.put("sourceTypeIn", sourceTypeIn.toString());
			List<Object[]> list = mifiTrafficService.segmentedMifiStatusList(paramMap);// 统计外设连接数量并获取设备基本信息
			size = list.size();
			if (0 == size) {
				return createUseageLog(size, schedTime, 0);
			}

			List<Channel> channelList = channelService.findChannelList();// 渠道列表
			Map<String, String> mccPriceMap = getMccPriceMap(channelList, priceService);// 查询费用
			String sourceTypeTemp = StringUtils.EMPTY, mcces = StringUtils.EMPTY, mcc = StringUtils.EMPTY, price = StringUtils.EMPTY;// 循环中使用的临时变量
			List<MifiUsageRecordSegment> records = new ArrayList<MifiUsageRecordSegment>();
			for (int i = 0; i < size; i++) {
				Object[] objs = list.get(i);
				MifiUsageRecordSegment record = new MifiUsageRecordSegment();
				record.setImei(ObjectUtils.toString(objs[0]));
				record.setSourceType(ObjectUtils.toString(objs[1]));
				record.setCardSourceType(ObjectUtils.toString(objs[23]));
				record.setOutOrderId(ObjectUtils.toString(objs[2]));
				record.setUestatus(Integer.valueOf(ObjectUtils.toString(objs[3])));
				record.setStampCreated(new Date(((Timestamp) objs[4]).getTime()));
				record.setSimBankId(Integer.valueOf(ObjectUtils.toString(objs[5])));
				record.setSimId(Integer.valueOf(ObjectUtils.toString(objs[6])));
				record.setNwstatus(Integer.valueOf(ObjectUtils.toString(objs[7])));
				record.setPowerInfo(Integer.valueOf(ObjectUtils.toString(objs[8])));
				record.setMainRejCause(ObjectUtils.toString(objs[9]));
				record.setMainMcc(ObjectUtils.toString(objs[10]));
				record.setMainMnc(ObjectUtils.toString(objs[11]));
				record.setMainTac(Integer.valueOf(ObjectUtils.toString(objs[12])));
				record.setMainCallid(Long.valueOf(ObjectUtils.toString(objs[13])));
				record.setMainRssi(Integer.valueOf(ObjectUtils.toString(objs[14])));
				record.setAdditionalMcc(ObjectUtils.toString(objs[15]));
				record.setAdditionalMnc(ObjectUtils.toString(objs[16]));
				record.setAdditionalTac(Integer.valueOf(ObjectUtils.toString(objs[17])));
				record.setAdditionalCellid(Long.valueOf(ObjectUtils.toString(objs[18])));
				record.setAdditionalRssi(Integer.valueOf(ObjectUtils.toString(objs[19])));
				record.setDevices(Integer.valueOf(ObjectUtils.toString(objs[20])));
				record.setBath(ObjectUtils.toString(objs[21]));

				// 费用（渠道商没有取youyoumob的价格）、使用国家信息
				sourceTypeTemp = ObjectUtils.toString(objs[1]);
				mcces = ObjectUtils.toString(objs[22]);
				if (StringUtils.isNotBlank(mcces)) {
					// 取出价格最高的mcc
					String[] mccArr = mcces.split(Constants.COMMA);
					if (mccArr.length == 1) {
						mcc = mccArr[0];
						price = mccPriceMap.get(sourceTypeTemp + "#" + mcc);
						price = StringUtils.isBlank(price) ? mccPriceMap.get(Constants.CHANNEL_DEFAULT_VALUE + "#" + mcc) : price;
					} else {
						String priceStr = StringUtils.EMPTY;
						Double priceDou = 0.0;
						// 过滤掉设备ownerMcc
						Map<String, String> mifiMap = mifiManageService.getMifilistBySn(ObjectUtils.toString(objs[0]));
						String ownerMcc = mifiMap.get("ownerMcc");
						for (String str : mccArr) {
							if (StringUtils.isNotBlank(ownerMcc) && ownerMcc.equals(str))
								continue;
							priceStr = mccPriceMap.get(sourceTypeTemp + "#" + str);
							priceStr = StringUtils.isBlank(priceStr) ? mccPriceMap.get(Constants.CHANNEL_DEFAULT_VALUE + "#" + str) : priceStr;
							if (StringUtils.isNotBlank(priceStr) && priceDou < Double.valueOf(priceStr)) {
								priceDou = Double.valueOf(priceStr);
								mcc = str;
							}
						}
						price = String.valueOf(priceDou);
					}
					record.setCost(StringUtils.isBlank(price) ? null : Double.valueOf(price));

					// 使用国家信息
					record.setMcc(mcc);
					HashMap<String, String> countryMap = cardMonitorService.findCountryByMcc(mcc);
					if (countryMap != null) {
						record.setCountryCode(countryMap.get("countryCode"));
						record.setCountryNameCn(countryMap.get("countryName"));
						record.setCountryNameEn(countryMap.get("countryNameEn"));
					}
				}
				sourceTypeTemp = StringUtils.EMPTY;
				mcces = StringUtils.EMPTY;
				mcc = StringUtils.EMPTY;
				price = StringUtils.EMPTY;

				// 统计设备流量(从simcardstatus表统计设备使用流量)
				String ueid = mifiManageService.getDeviceUeid(ObjectUtils.toString(objs[0]));
				if (StringUtils.isNotBlank(ueid)) {
					record.setDatainfo(mifiManageService.getDeviceFlow(ueid, ObjectUtils.toString(objs[23]), schedTime + " 00:00:00", schedTime + " 23:59:59"));
					record.setDataAfter21(mifiManageService.getDeviceFlow(ueid, ObjectUtils.toString(objs[23]), schedTime + " 21:00:00", schedTime + " 23:59:59"));
				} else {
					record.setDatainfo(0l);
					record.setDataAfter21(0l);
				}

				records.add(record);

			}

			// 保存数据
			recordService.save(records);
			return createUseageLog(size, schedTime, 0);
		} catch (Exception e) {
			e.printStackTrace();
			return createUseageLog(size, schedTime, 1);
		}
	}

	/** 
	 * 保存设备使用统计日志
	 * @Description 
	 * @param size
	 * @param preDate 
	 * @return void  
	 * @author yuxiaoyu
	 * @date 2017年3月3日 下午2:04:11 
	 */
	private static MifiUsageRecordSegmentLog createUseageLog(Integer size, String preDate, int result) {
		MifiUsageRecordSegmentLog errorLog = new MifiUsageRecordSegmentLog();
		errorLog.setStampCreated(DateUtils.parseDate(preDate));
		errorLog.setResult(result);// 执行成功
		if (0 == result) {
			errorLog.setRemarks("执行成功");
		} else {
			errorLog.setRemarks("执行失败");
		}
		errorLog.setCount(size);
		return errorLog;
	}

	/**
	 * 组装渠道的mcc对应价格map<sourceType#mcc, price>
	 * @Description 
	 * @param channelList
	 * @param priceService
	 * @return 
	 * @return Map<String,String>  
	 * @author yuxiaoyu
	 * @date 2017年3月6日 下午2:30:45
	 */
	private static Map<String, String> getMccPriceMap(List<Channel> channelList, PriceService priceService) {
		Map<String, String> map = new HashMap<String, String>();
		for (Channel channel : channelList) {
			String sourceType = channel.getChannelNameEn();
			List<Map<String, Object>> mccPirceMapList = priceService.findMccPriceMapList(channel.getId());
			if (mccPirceMapList != null && mccPirceMapList.size() > 0) {
				for (Map<String, Object> mccPriceMap : mccPirceMapList) {
					String price = ObjectUtils.toString(mccPriceMap.get("price"));
					String mcces = ObjectUtils.toString(mccPriceMap.get("mcces"));
					String[] mccArr = mcces.split(Constants.COMMA);
					for (String mcc : mccArr) {
						map.put(sourceType + "#" + mcc, price);
					}
				}
			}
		}
		return map;
	}
}
