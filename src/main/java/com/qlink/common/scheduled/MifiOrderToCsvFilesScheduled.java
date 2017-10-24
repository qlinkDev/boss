/** 
 * @Package com.uu.common.scheduled 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月19日 上午9:51:04 
 * @version V1.0 
 */ 
package com.qlink.common.scheduled;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.springframework.transaction.annotation.Transactional;

import com.qlink.common.utils.CsvWriter;
import com.qlink.common.utils.PropertiesLoader;
import com.qlink.modules.mifi.entity.MifiOrder;
import com.qlink.modules.mifi.entity.MifiOrderDetail;
import com.qlink.modules.mifi.service.MifiOrderService;
import com.qlink.modules.om.entity.Channel;
import com.qlink.modules.om.service.ChannelService;

/** 
 * @Description MIFI订单生成CSV文件 定时处理
 * @author yifang.huang
 * @date 2016年4月19日 上午9:51:04 
 */
public class MifiOrderToCsvFilesScheduled {
	
	public static Logger logger = LoggerFactory.getLogger(MifiOrderToCsvFilesScheduled.class);

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
	private MifiOrderService mifiOrderService;

	@Scheduled(cron = "0 04 * * * ?")//秒、分、时、日、月、年
	//@Scheduled(fixedRate = 1 * 60 * 1000)//1个小时钟扫一次
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void createCsvFiles(){
		
		logger.info("MIFI订单生成CSV文件定时处理开始！");
		
		// 取需要生成csv文件的渠道
		List<Channel> channelList = channelService.findChannelByCreateCsvFile("1");
		if (channelList!=null && channelList.size()>0) {
			Map<String, String> paramMap = null;
			List<MifiOrder> list = null;
			for (Channel channel : channelList) {
				// 查询渠道对应的完成的订单记录
				paramMap = new HashMap<String, String>();
				paramMap.put("sourceType", channel.getChannelNameEn());
				paramMap.put("outOrderTimeStart", getStartTime());
				paramMap.put("outOrderTimeEnd", df.format(new Date()));
				paramMap.put("validOrder", "validOrder");
				list = mifiOrderService.mifiOrderList(paramMap);
				
				// 生成文件
				if (list!=null && list.size()>0) {
					createCsvFile(channel, list);
				}
				
				paramMap = null;
			}
		}
		
		logger.info("MIFI订单生成CSV文件定时处理结束！");
		
	}
	
	/**
	 * 
	 * @Description 生成csv文件
	 * @param channel
	 * @param orderList 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年4月19日 上午10:19:59
	 */
	private void createCsvFile(Channel channel, List<MifiOrder> orderList) {
		
		// 行数据
		List<String> data = getDataStr(orderList);

		// 目录路径
		String fPath = csvDir + File.separator + channel.getChannelNameEn();
		
		// 文件名
		String fName = "ORDER" + new SimpleDateFormat("yyyyMMddHH").format(new Date()) + ".csv";
		
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
	private String getStartTime() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.HOUR, -1);
  		return df.format(c.getTime());
	}
	
	// 转成string
	private List<String> getDataStr(List<MifiOrder> orderList) {
		
		List<String> data = new ArrayList<String>();			
		
		String money = "0";
		DecimalFormat decimalFormat = new DecimalFormat("0.00"); 
		for (MifiOrder order : orderList) {
			
			// 头
			String dsns = (StringUtils.isNotBlank(order.getDsns()) ? order.getDsns().replace(",", ";") : order.getDsns());
			String ssids = (StringUtils.isNotBlank(order.getSsids()) ? order.getSsids().replace(",", ";") : order.getSsids());
			String allowedMcc = (StringUtils.isNotBlank(order.getAllowedMcc()) ? order.getAllowedMcc().replace(",", ";") : order.getAllowedMcc());
			
			data.add("H," + dsns + "," + ssids + "," + order.getReferenceTotalPrice() + "," + allowedMcc + "," + df.format(order.getStartDate()) + "," + df.format(order.getStartDate()) + "," + df.format(order.getStartDate()) + ",9999999999");
			
			// 订单详情
			List<MifiOrderDetail> list = mifiOrderService.getOrderDetailByOrderId(ObjectUtils.toString(order.getOrderId()));
			if (list!=null && list.size()>0) {
				// 计算每台设备的金额
				Double totalPrice = Double.valueOf(order.getReferenceTotalPrice());
				Integer num = Integer.valueOf(order.getEquipmentCnt());
				money = decimalFormat.format(totalPrice / num);
				
				for (MifiOrderDetail deatil : list) {
					// 详情
					data.add("D," + deatil.getDsn() + "," + deatil.getSsid() + "," + money + "," + allowedMcc + "," + df.format(order.getStartDate()) + "," + df.format(order.getOutOrderTime()) + "," + df.format(order.getEndDate()) + "," + deatil.getOrderDetailId());
				}
			}
			
			// 尾
			data.add("H," + dsns + "," + ssids + "," + order.getReferenceTotalPrice() + "," + allowedMcc + "," + df.format(order.getStartDate()) + "," + df.format(order.getStartDate()) + "," + df.format(order.getStartDate()) + ",9999999999");
			
			money = "0";
		}
		
		return data;
		
	}
	
}
