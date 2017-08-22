/** 
 * @Package com.uu.common.scheduled 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月19日 上午9:51:04 
 * @version V1.0 
 */ 
package com.uu.common.scheduled;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import com.uu.common.utils.CsvWriter;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.PropertiesLoader;
import com.uu.modules.om.condition.ConsumeRecordCondition;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.entity.ConsumeRecord;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.om.service.ConsumeRecordService;

/** 
 * @Description 消费记录生成CSV文件 定时处理
 * @author yifang.huang
 * @date 2016年4月19日 上午9:51:04 
 */
public class ConsumeToCsvFilesScheduled {
	
	public static Logger logger = LoggerFactory.getLogger(ConsumeToCsvFilesScheduled.class);

	public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	// csv文件存放路径
	public static String csvDir = "";
	static {
    	PropertiesLoader propertiesLoader = new PropertiesLoader("app.properties");
    	csvDir = propertiesLoader.getProperty("consumeRecordFiles_dir");
	}
	
	@Autowired
	private ChannelService channelService;
	
	@Autowired
	private ConsumeRecordService consumeRecordService;

	@Scheduled(cron = "0 03 * * * ?")//秒、分、时、日、月、年
	//@Scheduled(fixedRate = 1 * 60 * 1000)//1个小时钟扫一次
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void createCsvFiles(){
		
		logger.info("消费记录生成CSV文件定时处理开始！");
		
		// 取需要生成csv文件的渠道
		List<Channel> channelList = channelService.findChannelByCreateCsvFile("1");
		if (channelList!=null && channelList.size()>0) {
			ConsumeRecordCondition condition = null;
			for (Channel channel : channelList) {
				// 查询渠道对应的完成的消费记录
				condition = new ConsumeRecordCondition();
				condition.setEqChannelId(channel.getId());
				condition.setEqStatus(ConsumeRecord.Status.COMPLETED);
				condition.setLeCreateDateForDate(new Date());
				condition.setGeCreateDateForDate(getStartTime());
				List<ConsumeRecord> crList = consumeRecordService.findList(condition);
				if (crList!=null && crList.size()>0) {
					createCsvFile(channel, crList);
				}
				
				condition = null;
			}
		}
		
		logger.info("消费记录生成CSV文件定时处理结束！");
		
	}
	
	/**
	 * 
	 * @Description 生成csv文件
	 * @param channel
	 * @param crList 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年4月19日 上午10:19:59
	 */
	private void createCsvFile(Channel channel, List<ConsumeRecord> crList) {
		
		// 行数据
		List<String> data = getDataStr(crList);

		// 目录路径
		String fPath = csvDir + File.separator + channel.getChannelNameEn();
		
		// 文件名
		String fName = "SIM" + new SimpleDateFormat("yyyyMMddHH").format(new Date()) + ".csv";
		
		// 生成csv文件
		CsvWriter.createCSVFile(data, fPath, fName);
	}
	
	/**
	 * 
	 * @Description 取前一小时时间
	 * @return Date  
	 * @author yifang.huang
	 * @date 2016年4月19日 上午10:30:57
	 */
	private Date getStartTime() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.HOUR, -1);
  		return c.getTime();
	}
	
	// 转成string
	private List<String> getDataStr(List<ConsumeRecord> crList) {
		
		String dateStartStr = df.format(DateUtils.getDateStart(new Date()));
		String dateEndStr = df.format(DateUtils.getDateEnd(new Date()));
		List<String> data = new ArrayList<String>();			
		
		for (ConsumeRecord cr : crList) {
			data.add("H," + cr.getSn() + "," + cr.getSsid() + ",0.00," + cr.getMcc() + "," + dateStartStr + "," + dateStartStr + "," + dateStartStr + ",9999999999");
			data.add("D," + cr.getSn() + "," + cr.getSsid() + "," + cr.getMoney() + "," + cr.getMcc() + "," + dateStartStr + "," + df.format(cr.getLocalDate()) + "," + dateEndStr + "," + cr.getId());
			data.add("T," + cr.getSn() + "," + cr.getSsid() + "," + cr.getMoney() + "," + cr.getMcc() + "," + dateStartStr + "," + dateStartStr + "," + dateStartStr + ",9999999999");
		}
		
		return data;
		
	}
	
}
