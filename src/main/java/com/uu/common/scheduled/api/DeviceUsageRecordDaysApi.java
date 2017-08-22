package com.uu.common.scheduled.api;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.uu.common.persistence.Page;
import com.uu.common.scheduled.DeviceUsageRecordDaysScheduled;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.IdGen;
import com.uu.modules.mifi.entity.MifiUsageRecord;
import com.uu.modules.mifi.entity.MifiUsageRecordLog;
import com.uu.modules.mifi.service.CardMonitorService;
import com.uu.modules.mifi.service.MifiManageService;
import com.uu.modules.mifi.service.MifiTrafficService;
import com.uu.modules.mifi.service.MifiUsageRecordLogService;
import com.uu.modules.mifi.service.MifiUsageRecordService;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.om.service.PriceService;
import com.uu.modules.utils.Constants;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 
 * @Description 设备使用记录 定时处理 接口
 * @author yifang.huang
 * @date 2017年3月24日 上午10:59:26
 */
@Controller
@RequestMapping(value = "/api/dur", produces = "application/json")
@Api(value = "/dur", description = "设备使用记录 定时处理 接口")
public class DeviceUsageRecordDaysApi {

	public static Logger logger = LoggerFactory.getLogger(DeviceUsageRecordDaysScheduled.class);

	public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	MifiTrafficService mifiTrafficService;

	@Autowired
	private ChannelService channelService;

	@Autowired
	private PriceService priceService;
	
	@Autowired
	private MifiUsageRecordService recordService;
	
	@Autowired
	private MifiUsageRecordLogService errorLogService;
	
	@Autowired
	private MifiManageService mifiManageService;
	
	@Autowired
	private CardMonitorService cardMonitorService;
	
	@RequestMapping(value = "/execute.json", method = { RequestMethod.POST })
	@ApiOperation(position = 100, value = "设备使用记录 定时处理", notes = "设备使用记录 定时处理")
	public void execute(@ApiParam(value = "请求入参为URLEncoder处理的JSON格式", required = true) @RequestBody String reqobj,
			HttpServletRequest request, HttpServletResponse response) {

		logger.info("设备使用记录定时处理开始！");
		
		createJob();
		
		logger.info("设备使用记录定时处理结束！");
	}

	private void createJob() {
		Integer size = 0;
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("beginDate", DateUtils.getPreDate());
			paramMap.put("endDate", DateUtils.getPreDate());
			Page<Object[]> p = new Page<Object[]>();
			p.setCount(-1);
			Page<Object[]> page = mifiTrafficService.exportMifiStatus4List(p, paramMap);
			List<Object[]> list = page.getList();
			
			// 渠道列表
			List<Channel> channelList = channelService.findChannelList();

			// 查询费用
			Map<String, String> mccPriceMap = getMccPriceMap(channelList);
			size = list.size();
			
			// 日期 yyyy-MM-dd
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_YEAR, -1);
			String date = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
			
			// 循环使用的变量
			String sourceTypeTemp = "", mcces = "", mcc="", price = "";
			
			List<MifiUsageRecord> records = new ArrayList<MifiUsageRecord>();
			for (int i = 0; i < size; i++) {
				Object[] objs = list.get(i);
				MifiUsageRecord record = new MifiUsageRecord();
				record.setImei(ObjectUtils.toString(objs[0]));
				record.setSourceType(ObjectUtils.toString(objs[1]));	
				record.setCardSourceType(ObjectUtils.toString(objs[23]));
				record.setOutOrderId(ObjectUtils.toString(objs[2]));	
				record.setUestatus(Integer.valueOf(ObjectUtils.toString(objs[3])));	
				record.setStampCreated(new Date( ((Timestamp)objs[4]).getTime()));	
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
					String[] mccArr = mcces.split(",");
					if (mccArr.length == 1) {
						mcc = mccArr[0];
						price = mccPriceMap.get(sourceTypeTemp + "#" + mcc);
						price = StringUtils.isBlank(price) ? mccPriceMap.get(Constants.CHANNEL_DEFAULT_VALUE + "#" + mcc) : price;
					} else {
						String priceStr = "";
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
						mifiMap = null;
						ownerMcc = null;
					}
					record.setCost(StringUtils.isBlank(price) ? null : Double.valueOf(price) );
					
					// 使用国家信息
					record.setMcc(mcc);
					HashMap<String, String> countryMap = cardMonitorService.findCountryByMcc(mcc);
					if (countryMap != null) {
						record.setCountryCode(countryMap.get("countryCode"));
						record.setCountryNameCn(countryMap.get("countryName"));
						record.setCountryNameEn(countryMap.get("countryNameEn"));
					}
				}
				sourceTypeTemp = "";
				mcces = "";
				mcc = "";
				price = "";
				
				// 统计设备流量(从simcardstatus表统计设备使用流量)
				String ueid = mifiManageService.getDeviceUeid(ObjectUtils.toString(objs[0]));
				if (StringUtils.isNotBlank(ueid)) {
					record.setDatainfo(mifiManageService.getDeviceFlow(ueid, ObjectUtils.toString(objs[23]), date + " 00:00:00", date + " 23:59:59"));
				} else {
					record.setDatainfo(0l);
				}

				records.add(record);
				
			}
			
			//保存数据
			recordService.saveMifiUsageRecords(records);
			
			//保存设备使用统计日志
			MifiUsageRecordLog errorLog =new MifiUsageRecordLog();
			errorLog.setId(IdGen.uuid());
			errorLog.setCreateDate(new Date());
			errorLog.setUpdateDate(new Date());
			errorLog.setStampCreated(DateUtils.parseDate(DateUtils.getPreDate()));
			errorLog.setResult(0); //执行失败
			errorLog.setRemarks("执行成功");
			errorLog.setCount(size);
			errorLogService.saveForApi(errorLog);
			
		} catch (Exception e) {
			//写入设备使用异常日志记录
			MifiUsageRecordLog errorLog =new MifiUsageRecordLog();
			errorLog.setId(IdGen.uuid());
			errorLog.setCreateDate(new Date());
			errorLog.setUpdateDate(new Date());
			errorLog.setStampCreated(DateUtils.parseDate(DateUtils.getPreDate()));
			errorLog.setResult(1); //执行失败
			errorLog.setRemarks("执行失败");
			errorLog.setCount(size);
			errorLogService.saveForApi(errorLog);
			e.printStackTrace();
		}
	}

	// 组装渠道的mcc对应价格map<sourceType#mcc, price>
	private Map<String, String> getMccPriceMap(List<Channel> channelList) {

		Map<String, String> map = new HashMap<String, String>();

		for (Channel channel : channelList) {
			String sourceType = channel.getChannelNameEn();
			List<Map<String, Object>> mccPirceMapList = priceService.findMccPriceMapList(channel.getId());
			if (mccPirceMapList != null && mccPirceMapList.size() > 0) {
				for (Map<String, Object> mccPriceMap : mccPirceMapList) {
					String price = ObjectUtils.toString(mccPriceMap.get("price"));
					String mcces = ObjectUtils.toString(mccPriceMap.get("mcces"));
					String[] mccArr = mcces.split(",");
					for (String mcc : mccArr) {
						map.put(sourceType + "#" + mcc, price);
					}
				}
			}
		}

		return map;
	}
}
