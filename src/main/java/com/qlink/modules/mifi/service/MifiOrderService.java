package main.java.com.qlink.modules.mifi.service;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.common.utils.CsvWriter;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.IdGen;
import com.uu.common.utils.PropertiesLoader;
import com.uu.common.utils.dianji.SmsClientSend;
import com.uu.common.utils.mail.MailThread;
import com.uu.common.utils.thread.VodafoneThread;
import com.uu.modules.mifi.dao.MifiManageDao;
import com.uu.modules.mifi.dao.MifiOrderDao;
import com.uu.modules.mifi.dao.MifiOrderDetailDao;
import com.uu.modules.mifi.dao.MifiTestDao;
import com.uu.modules.mifi.dao.MifiVersionDao;
import com.uu.modules.mifi.entity.MifiOrder;
import com.uu.modules.mifi.entity.MifiOrderDetail;
import com.uu.modules.om.dao.ChannelDao;
import com.uu.modules.om.dao.CustomerDao;
import com.uu.modules.om.dao.PriceDao;
import com.uu.modules.om.dao.RegionDao;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.entity.Customer;
import com.uu.modules.om.entity.Price;
import com.uu.modules.om.entity.Region;
import com.uu.modules.sys.condition.NoticeReceiveCondition;
import com.uu.modules.sys.dao.YYKeyStoreDao;
import com.uu.modules.sys.entity.NoticeReceive;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.entity.YYKeyStore;
import com.uu.modules.sys.service.NoticeReceiveService;
import com.uu.modules.sys.utils.DictUtils;
import com.uu.modules.sys.utils.UserUtils;
import com.uu.modules.utils.Constants;
import com.uu.modules.utils.ReturnCode;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author wangyong
 * @date 2016年2月4日
 */
@Service
public class MifiOrderService extends BaseService {

	public static Logger logger = LoggerFactory.getLogger(MifiOrderService.class);

	@Autowired
	private MifiOrderDao mifiOrderDao;

	@Autowired
	private MifiOrderDetailDao mifiOrderDetailDao;

	@Autowired
	private YYKeyStoreDao keyStoreDao;

	@Autowired
	private MifiManageDao mifiManageDao;

	@Autowired
	private RegionDao regionDao;

	@Autowired
	private ChannelDao channelDao;

	@Autowired
	private PriceDao priceDao;
	
	@Autowired
	private CustomerDao customerDao;
	
	@Autowired
	private MifiTrafficService mifiTrafficService;
	
	@Autowired
	private MifiUsageRecordService mifiUsageService;

	@Autowired
	private NoticeReceiveService noticeReceiveService;
	
	@Autowired
	private MifiTestDao mifitestDao;
	
	@Autowired
	private MifiVersionDao mifiVersionDao;
	
	public MifiOrder get(String id) {
		return mifiOrderDao.get(id);
	}
	
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void save(MifiOrder bean) {
		mifiOrderDao.save(bean);
	}

	private String getSourceTypeByKeyId(String keyId) {
		
		YYKeyStore keyStore = keyStoreDao.get(keyId);
        if (keyStore == null) {

    		Criteria criteria = keyStoreDao.getSession().createCriteria(YYKeyStore.class);
    		@SuppressWarnings("unchecked")
    		List<YYKeyStore> keyStoreList = criteria
    				.add( Restrictions.eq("sourceType", keyId) )
    				.add( Restrictions.eq(YYKeyStore.FIELD_DEL_FLAG, YYKeyStore.DEL_FLAG_NORMAL))
    				.list();
    		if(!keyStoreList.isEmpty())
    			keyStore = keyStoreList.get(0);
    		
        }
		
		return keyStore.getSourceType();
	}

	private List<MifiOrder> queryMifiOrderByOutOrderId(String outOrderId, String sourceType) {
		DetachedCriteria detachedCriteria = mifiOrderDao.createDetachedCriteria();
		detachedCriteria.add(Restrictions.eq("outOrderId", outOrderId));
		detachedCriteria.add(Restrictions.eq("sourceType", sourceType));
		detachedCriteria.add(Restrictions.ne("orderStatus", MifiOrder.order_status_9));
		detachedCriteria.add(Restrictions.ne("orderStatus", MifiOrder.order_status_11));
		return mifiOrderDao.find(detachedCriteria);
	}

	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized JSONObject saveAndCreateOrder(String keyId, JSONObject param) {
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		String errorMessage = "";

		if (!param.containsKey("osn") || StringUtils.isBlank(param.getString("osn"))) {
			errorMessage = "|[订单编号]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			return resObj;
		}

		if (!param.containsKey("start_date") || StringUtils.isBlank(param.getString("start_date"))) {
			errorMessage = "|[行程开始时间]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			return resObj;
		}

		Date startDate;
		try {
			startDate = DateUtils.parseDate(param.getString("start_date"), new String[] { "yyyy-MM-dd HH:mm:ss" });
		} catch (ParseException e) {
			errorMessage = "[行程开始时间]格式必须为yyyy-MM-dd HH:mm:ss!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			return resObj;
		}

		if (!param.containsKey("end_date") || StringUtils.isBlank(param.getString("end_date"))) {
			errorMessage = "|[行程结束时间]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			return resObj;
		}

		Date endDate;
		try {
			endDate = DateUtils.parseDate(param.getString("end_date"), new String[] { "yyyy-MM-dd HH:mm:ss" });
		} catch (ParseException e) {
			errorMessage = "[行程结束时间]格式必须为yyyy-MM-dd HH:mm:ss!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			return resObj;
		}

		if (startDate.after(endDate)) {
			errorMessage = "[行程结束时间]必须大于等于[行程开始时间]!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			return resObj;
		}
		String allowedMcc = "";
		if (!param.containsKey("mccs") || StringUtils.isBlank(param.getString("mccs"))) {
			errorMessage = "|[限制使用国家]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			return resObj;
		}

		if (!param.getString("mccs").equals("*")) {
			allowedMcc = param.getString("mccs");
		}

		String equipmentCnt = "1";

		if (param.containsKey("equipment_cnt") && StringUtils.isNotBlank(param.getString("equipment_cnt"))) {
			equipmentCnt = param.getString("equipment_cnt");
		}

		String sourceType = getSourceTypeByKeyId(keyId);
		String outOrderId = param.getString("osn");
		List<MifiOrder> mifiOrderList = queryMifiOrderByOutOrderId(outOrderId, sourceType);
		if (mifiOrderList!=null && mifiOrderList.size()>0) {
			errorMessage = "[外部订单号]已经存在!";
			resObj.put("code", "-1");
			resObj.put("msg", errorMessage);
			return resObj;
		}
		MifiOrder mifiOrder = new MifiOrder();
		mifiOrder.setOrderId(IdGen.uuid());
		mifiOrder.setOrderStatus(MifiOrder.order_status_0);
		mifiOrder.setSourceType(sourceType);
		mifiOrder.setOutOrderId(outOrderId);
		mifiOrder.setOutOrderTime(new Date());
		mifiOrder.setAllowedMcc(allowedMcc);
		mifiOrder.setEquipmentCnt(equipmentCnt);
		mifiOrder.setStartDate(startDate);
		mifiOrder.setEndDate(endDate);
		//setDaysAndPriceOne(mifiOrder);
		mifiOrderDao.save(mifiOrder);
		return resObj;
	}

	/**
	 * 
	 * @Description 发货接口
	 * @param keyId
	 * @param flow	流量订单的流量，其它订单默认为null
	 * @param param
	 * @return JSONObject  
	 * @author yifang.huang
	 * @date 2016年5月4日 下午6:38:04
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized JSONObject saveAndDeliveryOrder(String keyId, Integer flow, JSONObject param) {
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);

		String errorMessage = "";
		Map<String, String> map = new HashMap<String, String>();			// 内部方法执行结果及msg信息
		map.put("code", "1");
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(df.format(new Date()) + "[入参]：" + param.toString());

		// 订单编号判断
		if (!param.containsKey("osn") || StringUtils.isBlank(param.getString("osn"))) {
			errorMessage = "|[订单编号]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		String outOrderId = param.getString("osn");
		// 订单开始时间
		Date startDate = null;
		if (!param.containsKey("start_date") || StringUtils.isBlank(param.getString("start_date"))) {
			errorMessage = "|[行程开始时间]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		try {
			startDate = DateUtils.parseDate(param.getString("start_date"), new String[] { "yyyy-MM-dd HH:mm:ss" });
		} catch (ParseException e) {
			errorMessage = "[行程开始时间]格式必须为yyyy-MM-dd HH:mm:ss!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		// 订单结束时间
		Date endDate = null;
		if (!param.containsKey("end_date") || StringUtils.isBlank(param.getString("end_date"))) {
			errorMessage = "|[行程结束时间]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		try {
			endDate = DateUtils.parseDate(param.getString("end_date"), new String[] { "yyyy-MM-dd HH:mm:ss" });
		} catch (ParseException e) {
			errorMessage = "[行程结束时间]格式必须为yyyy-MM-dd HH:mm:ss!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		// 订单开始时间不能大于结束时间
		if (startDate.after(endDate)) {
			errorMessage = "[行程结束时间]必须大于等于[行程开始时间]!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		// 设备序列号
		JSONArray dsns;
		if (!param.containsKey("dsn")) {
			errorMessage = "|[设备序列号]必须存在!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		} else {
			dsns = param.getJSONArray("dsn");
		}
		if (dsns.size() == 0) {
			errorMessage = "|[设备序列号]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		// 设备序列号数据
		String[] imeiArr = getDsnStringArray(dsns);
		
		// 渠道编号
		String sourceType = getSourceTypeByKeyId(keyId);
		
		// 限制使用国家(多个英文逗号间隔)
		if (!param.containsKey("mccs") || StringUtils.isBlank(param.getString("mccs")) || param.getString("mccs").equals("*")) {
			errorMessage = "|[限制使用国家]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		String allowedMcc = param.getString("mccs");
		if ("ALL".equalsIgnoreCase(allowedMcc)) {
			allowedMcc = this.findAllMcces(sourceType);
			if (StringUtils.isBlank(allowedMcc)) {
				errorMessage = "|[限制使用国家]不能为空,ALL没有取到对应的MCC!";
				resObj.put("code", "61451");
				resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
		}
		
		Date nowDate = new Date();
		// 不允许订单号相同
		List<MifiOrder> mifiOrderList = queryMifiOrderByOutOrderId(outOrderId, sourceType);
		if (mifiOrderList!=null && mifiOrderList.size()>0) {
			errorMessage = "[外部订单号:" + outOrderId + "]已经存在!";
			resObj.put("code", "-1");
			resObj.put("msg", errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		
		// 判断是否需要拆单(公司产品和后付费的渠道商产品不需要拆单)
		boolean orderNeedSplit = orderNeedSplit(sourceType, map);
		if (!"1".equals(map.get("code"))) {
			resObj.put("code", "-1");
			resObj.put("msg", map.get("msg"));
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		// 渠道商
		List<Channel> cList = channelDao.findList(sourceType);
		Channel channel = cList.get(0);
		if (!"ORDER".equals(channel.getModel())) {
			errorMessage = "|[运营商:"+channel.getChannelName()+"]不是订单模式，不能下订单!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		
		// 保存imei对应的ssid
		Map<String, String> ssidMap = new HashMap<String, String>();
		
		// 根据allowedMcc取国家对应mcc集合
		Map<String, String> countryMccesMap = new HashMap<String, String>();
		map = getCountryMccesMap(allowedMcc, countryMccesMap);
		if (!"1".equals(map.get("code"))) {
			resObj.put("code", "-1");
			resObj.put("msg", map.get("msg"));
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		
		// 判断渠道是否可以下allowedMcc对应国家的订单
		map = allowedMccCheck(countryMccesMap, sourceType);
		if (!"1".equals(map.get("code"))) {
			resObj.put("code", "-1");
			resObj.put("msg", map.get("msg"));
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		
		// 下单系统限制条件判断
		map = validate(ssidMap, countryMccesMap, imeiArr, startDate, sourceType);
		if (!"1".equals(map.get("code"))) {
			resObj.put("code", "-1");
			resObj.put("msg", map.get("msg"));
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		
		// 判断设备在当前行程内设备是否已经有有效订单
		map = checkDeviceByTripDate(imeiArr, param.getString("start_date"), param.getString("end_date"));
		if (!"1".equals(map.get("code"))) {
			resObj.put("code", "-1");
			resObj.put("msg", map.get("msg"));
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		
		// 设备版本判断 
		map = mifiVersionCheck(imeiArr);
		if (!"1".equals(map.get("code"))) {
			resObj.put("code", "-1");
			resObj.put("msg", map.get("msg"));
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		} else {
			String msg = map.get("msg");
			if (StringUtils.isNotBlank(msg))
				resObj.put("msg", ObjectUtils.toString(resObj.get("msg")) + "!" + msg);
		}
		
		// 订单速度标识
		String limitSpeedFlag = null;
		if (param.containsKey("limitSpeedFlag"))
			limitSpeedFlag = param.getString("limitSpeedFlag");
		limitSpeedFlag = StringUtils.isBlank(limitSpeedFlag) ? "0" : limitSpeedFlag;
		
		// 订单客户信息
		String customerId = saveCustomer(param);
		
		if (!orderNeedSplit) {  // 公司产品和后付费的渠道商产品不需要拆单
			MifiOrder _mifiOrder = new MifiOrder();
			_mifiOrder.setOrderId(IdGen.uuid());
			_mifiOrder.setOrderStatus(MifiOrder.order_status_1);
			_mifiOrder.setSourceType(sourceType);
			_mifiOrder.setOutOrderId(outOrderId);
			_mifiOrder.setOutOrderTime(nowDate);
			_mifiOrder.setAllowedMcc(allowedMcc);
			_mifiOrder.setEquipmentCnt(String.valueOf(dsns.size()));
			_mifiOrder.setStartDate(startDate);
			_mifiOrder.setEndDate(endDate);
			_mifiOrder.setDeliveryTime(nowDate);
			_mifiOrder.setStockStatus(MifiOrder.stock_status_0);
			_mifiOrder.setLimitSpeedFlag(limitSpeedFlag);
			// 判断是否为流量订单
			if (flow!=null) {
				_mifiOrder.setOrderType("1");
				_mifiOrder.setFlow(flow);
			}
			map = setDaysAndPriceTwo(_mifiOrder);
			if (!"1".equals(map.get("code"))) {
				resObj.put("code", "-1");
				resObj.put("msg", map.get("msg"));
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
			
			List<MifiOrderDetail> mifiOrderDetails = new ArrayList<MifiOrderDetail>();
			StringBuffer snStr = new StringBuffer();
			for (int i = 0; i < dsns.size(); i++) {
				String dsn = dsns.getString(i);
				if (0 == mifiOrderDao.getMifiListCountByDsn(dsn)) {
					errorMessage = "[设备序列号:" + dsn + "]无对应设备数据!";
					resObj.put("code", "-1");
					resObj.put("msg", errorMessage);
					logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
					return resObj;
				}
				
				if (i == 0) {
					snStr.append(dsn);
				} else {
					snStr.append("," + dsn);
				}
				
				MifiOrderDetail mifiOrderDetail = new MifiOrderDetail();
				mifiOrderDetail.setOrderDetailId(IdGen.uuid());
				mifiOrderDetail.setOrderId(_mifiOrder.getOrderId());
				mifiOrderDetail.setOutOrderId(outOrderId);
				mifiOrderDetail.setDsn(dsn);
				mifiOrderDetail.setSsid(getSsidByDsn(dsn));
				mifiOrderDetail.setDeliveryTime(nowDate);
				mifiOrderDetails.add(mifiOrderDetail);
			}
			StringBuffer buffer = new StringBuffer();
			buffer.append("update mifilist set UEALLOWEDMCC = '" + allowedMcc + "' ");
			// 当前时间与订单行程开始时间为同一天需要设置 UEALLOWED 为 1,并且limit_speed_flag赋值
			if (DateUtils.orderIsBegin(startDate)) {
				buffer.append(",UEALLOWED = " + Constants.ueAllowed_y);
				mifiManageDao.updateMifiVersionsLimitSpeedFlag(limitSpeedFlag, snStr.toString());
				// 如果是流量订单，则修改mifilist的order_type,flow,start_time,end_time
				if (flow!=null) {
					buffer.append(",order_type='1',flow=" + (flow*1024*1024) + ",flow_used=0,start_time='" + param.getString("start_date") + "',end_time='" + param.getString("end_date") + "'");
				}
			}
			buffer.append(" where IMEI_6200 IN(" + snStr.toString() + ")");
			_mifiOrder.setCustomerId(customerId);
			mifiOrderDao.save(_mifiOrder);
			mifiOrderDetailDao.save(mifiOrderDetails);
			mifiOrderDao.updateBySql(buffer.toString(), null);
		} else {		 // 渠道订单
			
			try {
				// 保存订单
				map = saveOrder(channel, countryMccesMap, imeiArr, limitSpeedFlag, ssidMap, startDate, endDate, outOrderId, null, customerId, flow);
				if (!"1".equals(map.get("code"))) {
					resObj.put("code", "-1");
					resObj.put("msg", map.get("msg"));
					logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
					return resObj;
				}
				
				// 修改设备可用状态
				for (String imei : imeiArr) {
					// 当前时间与订单行程开始时间为同一天需要设置 UEALLOWED 为 1
					if (DateUtils.orderIsBegin(startDate)) {
						mifiManageDao.updateMifilist(Constants.ueAllowed_y, allowedMcc, imei);
						mifiManageDao.updateMifiVersionLimitSpeedFlag(limitSpeedFlag, imei);
						// 如果是流量订单，则修改mifilist的order_type,flow,start_time,end_time
						if (flow!=null) {
							StringBuffer buffer = new StringBuffer();
							buffer.append("update mifilist set order_type='1',flow=" + (flow*1024*1024) + ",flow_used=0,start_time='" + param.getString("start_date") + "',end_time='" + param.getString("end_date") + "'");
							buffer.append(" where IMEI_6200='" + imei + "'");
							mifiOrderDao.updateBySql(buffer.toString(), null);
						}
					}
					mifiManageDao.updateMifilistUeAllowedMcc(allowedMcc, imei);
				}
			} catch (ParseException e) {
				e.printStackTrace();
				resObj.put("code", "-1");
				resObj.put("msg", "下单失败，请与客服联系！");
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
		}

		// Vodafone卡升级
		if (DateUtils.orderIsBegin(startDate)) {
			updateVodafone(allowedMcc, imeiArr, Constants.VODAFONE_CARD_LEVEL_5);
		}

		logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
		return resObj;
	}
	
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized JSONObject saveAndDelayOrder(String keyId, JSONObject param) {
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		String errorMessage = "";
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(df.format(new Date()) + "[入参]：" + param.toString());

		if (!param.containsKey("osn") || StringUtils.isBlank(param.getString("osn"))) {
			errorMessage = "|[订单编号]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		String sourceType = getSourceTypeByKeyId(keyId);
		String outOrderId = param.getString("osn");
		List<MifiOrder> mifiOrderList = queryMifiOrderByOutOrderId(outOrderId, sourceType);
		if (mifiOrderList==null || mifiOrderList.size()==0) {
			errorMessage = "[外部订单号]对应的订单未找到,无法延期或充值流量!";
			resObj.put("code", "-1");
			resObj.put("msg", errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		if ((!param.containsKey("end_date") || StringUtils.isBlank(param.getString("end_date")))
				&& (!param.containsKey("flow") || StringUtils.isBlank(param.getString("flow")))) {
			errorMessage = "|[行程结束时间]与[充值流量]不能同时为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		Date endDate = null;
		int flow = 0;
		// 订单结束日期不为空
		if (param.containsKey("end_date") && StringUtils.isNotBlank(param.getString("end_date"))) {
			try {
				endDate = DateUtils.parseDate(param.getString("end_date"), new String[] { "yyyy-MM-dd HH:mm:ss" });
			} catch (ParseException e) {
				errorMessage = "[行程结束时间]格式必须为yyyy-MM-dd HH:mm:ss!";
				resObj.put("code", "61451");
				resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
		}
		// 充值流量不为空
		if (param.containsKey("flow") && StringUtils.isNotBlank(param.getString("flow"))) {
			String flowStr = param.getString("flow");
			try {
				flow = Integer.valueOf(flowStr);// 订单流量格式判断
				if (flow <= 0) {
					errorMessage = "|[订单流量]必须是正整数!";
					resObj.put("code", "61451");
					resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
					logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
					return resObj;
				}
			} catch (NumberFormatException e) {
				errorMessage = "|[订单流量]必须是正整数!";
				resObj.put("code", "61451");
				resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
		}
		
		Double totalMoney = 0.00;
		for (MifiOrder mifiOrder : mifiOrderList) {
			if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_3)) {
				resObj.put("code", "-1");
				resObj.put("msg", "当前订单已终止,无法延期!");
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
			if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_8)) {
				resObj.put("code", "-1");
				resObj.put("msg", "订单已完成，无法延期！");
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
			if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_9)) {
				resObj.put("code", "-1");
				resObj.put("msg", "订单已取消，无法延期！");
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
			if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_11)) {
				resObj.put("code", "-1");
				resObj.put("msg", "订单已删除，无法延期！");
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
	
			if (endDate != null) {	// 订单延期
				// 校验延期时间 不确定是否可以减短行程时间(如可以则需要修改为大于行程开始时间)
				if (Long.parseLong(DateUtils.formatDate(endDate, null).replaceAll("-", "")) <= Long
						.parseLong(DateUtils.formatDate(mifiOrder.getEndDate(), null).replaceAll("-", ""))) {
					errorMessage = "延期后的[行程结束时间]不能小于等于当前[行程结束时间]!";
					resObj.put("code", "-1");
					resObj.put("msg", errorMessage);
					logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
					return resObj;
				}
		
				Date nowDate = new Date();
		
				// 当前时间大于行程结束时间则需要更新mifilist数据
				if (Long.parseLong(DateUtils.formatDate(nowDate, null).replaceAll("-", "")) > Long
						.parseLong(DateUtils.formatDate(mifiOrder.getEndDate(), null).replaceAll("-", ""))) {
					StringBuffer buffer = new StringBuffer();
					buffer.append("update mifilist t set UEALLOWED = " + Constants.ueAllowed_y + ", UEALLOWEDMCC = '"
							+ mifiOrder.getAllowedMcc() + "'");
					buffer.append(" where IMEI_6200 in (select dsn from mifi_order_detail where order_id = '"
							+ mifiOrder.getOrderId() + "')");
					mifiOrderDao.updateBySql(buffer.toString(), null);
				}
				
				// 如果是流量订单且订单已开始，需要修改mifilist的end_time字段值
				if (MifiOrder.ORDER_TYPE_FLOW.equals(mifiOrder.getOrderType())) {
					if (Long.parseLong(DateUtils.formatDate(new Date(), "yyyy-MM-dd").replaceAll("-", "")) >= 
						Long.parseLong(DateUtils.formatDate(mifiOrder.getStartDate(), "yyyy-MM-dd").replaceAll("-", ""))) {
						StringBuffer buffer = new StringBuffer();
						buffer.append("update mifilist t set t.end_time='" + param.getString("end_date") +"'");
						buffer.append(" where IMEI_6200 in (select dsn from mifi_order_detail where order_id = '" + mifiOrder.getOrderId() + "')");
						mifiOrderDao.updateBySql(buffer.toString(), null);
					}
				}
				
				String delayRemark = (StringUtils.isBlank(mifiOrder.getDelayRemark()) ? "" : mifiOrder.getDelayRemark() + ",")
						+ DateUtils.formatDate(mifiOrder.getEndDate(), null) + "延期至" + DateUtils.formatDate(endDate, null);
				// 订单延期修改订单参考总价
				totalMoney += Double.valueOf(setTotalPrice(mifiOrder, endDate));
				mifiOrder.setEndDate(endDate);
				mifiOrder.setDelayRemark(delayRemark);
				mifiOrder.setDelayTime(new Date());
			}
			
			if (flow > 0) {	// 订单充值流量

				// 如果是流量订单且订单已开始，需要修改mifilist的end_time字段值
				if (MifiOrder.ORDER_TYPE_FLOW.equals(mifiOrder.getOrderType())) {
					if (Long.parseLong(DateUtils.formatDate(new Date(), "yyyy-MM-dd").replaceAll("-", "")) >= 
						Long.parseLong(DateUtils.formatDate(mifiOrder.getStartDate(), "yyyy-MM-dd").replaceAll("-", ""))) {
						StringBuffer buffer = new StringBuffer();
						buffer.append("update mifilist t set t.flow=(t.flow+" + flow + ")");
						buffer.append(" where IMEI_6200 in (select dsn from mifi_order_detail where order_id = '" + mifiOrder.getOrderId() + "')");
						mifiOrderDao.updateBySql(buffer.toString(), null);
					}
					
					String delayRemark = (StringUtils.isBlank(mifiOrder.getDelayRemark()) ? "" : mifiOrder.getDelayRemark() + ",")
							+ "充值流量" + flow + "M";
					mifiOrder.setFlow(mifiOrder.getFlow() + flow);
					mifiOrder.setDelayRemark(delayRemark);
					mifiOrder.setDelayTime(new Date());
				}
			}
		}

		// 预付费渠道商延期订单扣费
		if (endDate!=null && !sourceType.equals(Constants.CHANNEL_DEFAULT_VALUE)) {
			DetachedCriteria cDc = channelDao.createDetachedCriteria();
			cDc.add(Restrictions.eq("channelNameEn", sourceType));
			cDc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
			List<Channel> cList = channelDao.find(cDc);
			if (cList!=null && cList.size()>0) {
				Channel channel = cList.get(0);
				if  ("0".equals(channel.getPayType())) {		// 判断是否为预付费渠道商
					Double balance = channel.getBalance();		// 预付费运营商余额
					if (totalMoney > balance) { // 余额不足，请联系运营商
						errorMessage = "延期后的[行程结束时间]不能小于等于当前[行程结束时间]!";
						resObj.put("code", "-1");
						resObj.put("msg", "[" + channel.getChannelName() + "] 渠道余额不足，可用余额["+balance+"]，请充值！");
						return resObj;
					}
					// 修改运营商余额，保存消费记录
					channel.setBalance(balance - totalMoney);
					channelDao.getSession().saveOrUpdate(channel);
				}
			}
		}

		// 存在订单延期信息
		mifiOrderDao.save(mifiOrderList);

		logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
		return resObj;
	}

	/**
	 * 
	 * @Description 
	 * 1 接口参数imei,end_date,flow，设备编号不能为空，end_date和flow不能同时为空
	 * 2 如果end_date不为空则修改设备对应的正在进行中的订单的结束时间
	 * 3 如果flow不为空则修改设备的总流量
	 * @param keyId
	 * @param param
	 * @return JSONObject  
	 * @author yifang.huang
	 * @date 2017年5月26日 下午6:01:08
	 */
	public synchronized JSONObject saveAndDelayDevice(String keyId, JSONObject param) {
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		String errorMessage = "";
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(df.format(new Date()) + "[入参]：" + param.toString());

		if (!param.containsKey("imei") || StringUtils.isBlank(param.getString("imei"))) {
			errorMessage = "|[设备编号]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		String sourceType = getSourceTypeByKeyId(keyId);
		String imei = param.getString("imei");
		Map<String, String> mifiMap = mifiManageDao.getMifilistBySn(imei);
		if (mifiMap == null) {
			errorMessage = "设备未找到,无法延期或充值流量!";
			resObj.put("code", "-1");
			resObj.put("msg", errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		if (!sourceType.equals(mifiMap.get("sourceType"))) {
			errorMessage = "设备 [" + imei + "] 不属于当前渠道商";
			resObj.put("code", "-1");
			resObj.put("msg", errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
			
		}
		// 判断设备是否存在进行中的流量订单
		List<Map<String, String>> orderMapList = mifiOrderDao.getValidOrder(imei, MifiOrder.ORDER_TYPE_FLOW);
		if (orderMapList==null || orderMapList.size()==0) {
			errorMessage = "设备 [" + imei + "] 未找到进行中的流量订单";
			resObj.put("code", "-1");
			resObj.put("msg", errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		Map<String, String> orderMap = orderMapList.get(0);
		List<MifiOrder> orderList = queryMifiOrderByOutOrderId(orderMap.get("outOrderId"), sourceType);
		if (orderList==null || orderList.size()==0) {
			errorMessage = "设备 [" + imei + "] 未找到进行中的流量订单";
			resObj.put("code", "-1");
			resObj.put("msg", errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		MifiOrder mifiOrder = orderList.get(0);

		if ((!param.containsKey("end_date") || StringUtils.isBlank(param.getString("end_date")))
				&& (!param.containsKey("flow") || StringUtils.isBlank(param.getString("flow")))) {
			errorMessage = "|[行程结束时间]与[充值流量]不能同时为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		Date endDate = null;
		int flow = 0;
		// 订单结束日期不为空
		if (param.containsKey("end_date") && StringUtils.isNotBlank(param.getString("end_date"))) {
			try {
				endDate = DateUtils.parseDate(param.getString("end_date"), new String[] { "yyyy-MM-dd HH:mm:ss" });
			} catch (ParseException e) {
				errorMessage = "[行程结束时间]格式必须为yyyy-MM-dd HH:mm:ss!";
				resObj.put("code", "61451");
				resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
		}
		// 充值流量不为空
		if (param.containsKey("flow") && StringUtils.isNotBlank(param.getString("flow"))) {
			String flowStr = param.getString("flow");
			try {
				flow = Integer.valueOf(flowStr);// 订单流量格式判断
				if (flow <= 0) {
					errorMessage = "|[订单流量]必须是正整数!";
					resObj.put("code", "61451");
					resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
					logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
					return resObj;
				}
			} catch (NumberFormatException e) {
				errorMessage = "|[订单流量]必须是正整数!";
				resObj.put("code", "61451");
				resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
		}
		
		Double totalMoney = 0.00;
		if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_3)) {
			resObj.put("code", "-1");
			resObj.put("msg", "当前订单已终止,无法延期!");
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_8)) {
			resObj.put("code", "-1");
			resObj.put("msg", "订单已完成，无法延期！");
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_9)) {
			resObj.put("code", "-1");
			resObj.put("msg", "订单已取消，无法延期！");
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_11)) {
			resObj.put("code", "-1");
			resObj.put("msg", "订单已删除，无法延期！");
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		if (endDate != null) {	// 订单延期
			// 校验延期时间 不确定是否可以减短行程时间(如可以则需要修改为大于行程开始时间)
			if (Long.parseLong(DateUtils.formatDate(endDate, null).replaceAll("-", "")) <= Long
					.parseLong(DateUtils.formatDate(mifiOrder.getEndDate(), null).replaceAll("-", ""))) {
				errorMessage = "延期后的[行程结束时间]不能小于等于当前[行程结束时间]!";
				resObj.put("code", "-1");
				resObj.put("msg", errorMessage);
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
	
			Date nowDate = new Date();
	
			// 当前时间大于行程结束时间则需要更新mifilist数据
			if (Long.parseLong(DateUtils.formatDate(nowDate, null).replaceAll("-", "")) > Long
					.parseLong(DateUtils.formatDate(mifiOrder.getEndDate(), null).replaceAll("-", ""))) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("update mifilist t set UEALLOWED = " + Constants.ueAllowed_y + ", UEALLOWEDMCC = '"
						+ mifiOrder.getAllowedMcc() + "'");
				buffer.append(" where IMEI_6200 in (select dsn from mifi_order_detail where order_id = '"
						+ mifiOrder.getOrderId() + "')");
				mifiOrderDao.updateBySql(buffer.toString(), null);
			}
			
			// 如果是流量订单且订单已开始，需要修改mifilist的end_time字段值
			if (MifiOrder.ORDER_TYPE_FLOW.equals(mifiOrder.getOrderType())) {
				if (Long.parseLong(DateUtils.formatDate(new Date(), "yyyy-MM-dd").replaceAll("-", "")) >= 
					Long.parseLong(DateUtils.formatDate(mifiOrder.getStartDate(), "yyyy-MM-dd").replaceAll("-", ""))) {
					StringBuffer buffer = new StringBuffer();
					buffer.append("update mifilist t set t.end_time='" + param.getString("end_date") +"'");
					buffer.append(" where IMEI_6200 in (select dsn from mifi_order_detail where order_id = '" + mifiOrder.getOrderId() + "')");
					mifiOrderDao.updateBySql(buffer.toString(), null);
				}
			}
			
			String delayRemark = (StringUtils.isBlank(mifiOrder.getDelayRemark()) ? "" : mifiOrder.getDelayRemark() + ",")
					+ DateUtils.formatDate(mifiOrder.getEndDate(), null) + "延期至" + DateUtils.formatDate(endDate, null);
			// 订单延期修改订单参考总价
			totalMoney += Double.valueOf(setTotalPrice(mifiOrder, endDate));
			mifiOrder.setEndDate(endDate);
			mifiOrder.setDelayRemark(delayRemark);
			mifiOrder.setDelayTime(new Date());
		}
		
		if (flow > 0) {	// 订单充值流量

			// 如果是流量订单且订单已开始，需要修改mifilist的flow字段值
			if (MifiOrder.ORDER_TYPE_FLOW.equals(mifiOrder.getOrderType())) {
				if (Long.parseLong(DateUtils.formatDate(new Date(), "yyyy-MM-dd").replaceAll("-", "")) >= 
					Long.parseLong(DateUtils.formatDate(mifiOrder.getStartDate(), "yyyy-MM-dd").replaceAll("-", ""))) {
					StringBuffer buffer = new StringBuffer();
					buffer.append("update mifilist t set t.flow=(t.flow+" + flow + ")");
					buffer.append(" where IMEI_6200 in (select dsn from mifi_order_detail where order_id = '" + mifiOrder.getOrderId() + "')");
					mifiOrderDao.updateBySql(buffer.toString(), null);
				}
			}
		}

		// 预付费渠道商延期订单扣费
		if (endDate!=null && !sourceType.equals(Constants.CHANNEL_DEFAULT_VALUE)) {
			DetachedCriteria cDc = channelDao.createDetachedCriteria();
			cDc.add(Restrictions.eq("channelNameEn", sourceType));
			cDc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
			List<Channel> cList = channelDao.find(cDc);
			if (cList!=null && cList.size()>0) {
				Channel channel = cList.get(0);
				if  ("0".equals(channel.getPayType())) {		// 判断是否为预付费渠道商
					Double balance = channel.getBalance();		// 预付费运营商余额
					if (totalMoney > balance) { // 余额不足，请联系运营商
						errorMessage = "延期后的[行程结束时间]不能小于等于当前[行程结束时间]!";
						resObj.put("code", "-1");
						resObj.put("msg", "[" + channel.getChannelName() + "] 渠道余额不足，可用余额["+balance+"]，请充值！");
						return resObj;
					}
					// 修改运营商余额，保存消费记录
					channel.setBalance(balance - totalMoney);
					channelDao.getSession().saveOrUpdate(channel);
				}
			}
		}

		// 存在订单延期信息
		mifiOrderDao.save(mifiOrder);

		logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
		return resObj;
	}

	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized JSONObject saveAndCancelOrder(String keyId, JSONObject param) {
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		String errorMessage = "";
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(df.format(new Date()) + "[入参]：" + param.toString());

		if (!param.containsKey("osn") || StringUtils.isBlank(param.getString("osn"))) {
			errorMessage = "|[订单编号]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		String sourceType = getSourceTypeByKeyId(keyId);
		String outOrderId = param.getString("osn");
		List<MifiOrder> mifiOrderList = queryMifiOrderByOutOrderId(outOrderId, sourceType);
		if (mifiOrderList==null || mifiOrderList.size()==0) {
			errorMessage = "[外部订单号]不存在,无法取消!";
			resObj.put("code", "-1");
			resObj.put("msg", errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		
		double totalMoney = 0.00; 		// 记录取消订单的总金额，退回给渠道商
		for (MifiOrder mifiOrder : mifiOrderList) {
			if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_3)) {
				errorMessage = "当前订单已终止,无法取消!";
				resObj.put("code", "-1");
				resObj.put("msg", errorMessage);
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
			
			// 订单状态判断
			if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_3)) {
				resObj.put("code", "-1");
				resObj.put("msg", "当前订单已终止,无法取消!");
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			} 
			if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_8)) {
				resObj.put("code", "-1");
				resObj.put("msg", "订单已完成，无法取消！");
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
			if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_9)) {
				resObj.put("code", "-1");
				resObj.put("msg", "订单已取消，无法取消！");
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
			if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_11)) {
				resObj.put("code", "-1");
				resObj.put("msg", "订单已删除，无法取消！");
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
			
			//非游友移动订单 不允许在订单行程开始后进行取消
			if (!Constants.CHANNEL_DEFAULT_VALUE.equals(sourceType)) {
				if (Long.parseLong(DateUtils.formatDate(new Date(), "yyyy-MM-dd").replaceAll("-", "")) >= Long.parseLong(DateUtils.formatDate(mifiOrder.getStartDate(), "yyyy-MM-dd").replaceAll("-", ""))) {
					errorMessage = "当前订单行程已开始,无法取消!";
					resObj.put("code", "-1");
					resObj.put("msg", errorMessage);
					logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
					return resObj;
				}
			}
	
			// 当前订单已发货后并且行程已开始取消,需重置设备属性
			Date now = new Date();
			if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_1) && 
					(now.after(mifiOrder.getStartDate()) && now.before(mifiOrder.getEndDate()))) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("update mifilist t set UEALLOWED = " + Constants.ueAllowed_n + ", UEALLOWEDMCC = null ");
				// 如果是流量订单，则修改mifilist的order_type,flow,start_time,end_time
				if (mifiOrder.getOrderType().equals(MifiOrder.ORDER_TYPE_FLOW)) {
					buffer.append(",order_type='0', flow=0, flow_used=0, start_time=null, end_time=null");
				}
				buffer.append(" where IMEI_6200 in (select dsn from mifi_order_detail where order_id = '" + mifiOrder.getOrderId() + "')");
				mifiOrderDao.updateBySql(buffer.toString(), null);
				
				// 修改mifiversion的speedlimit_type='0'
				mifiOrderDao.updateBySql("update mifiversion a set a.speedlimit_type='0' where IMEI_6200 in (select dsn from mifi_order_detail where order_id = '" + mifiOrder.getOrderId() + "')", null);

				// Vodafone卡降级
				String imeis = mifiOrderDetailDao.getDsnByOrderId(mifiOrder.getOrderId());
				if (StringUtils.isNotBlank(imeis))
					updateVodafone(mifiOrder.getAllowedMcc(), imeis.split(","), Constants.VODAFONE_CARD_LEVEL_3);
				
			}
			mifiOrder.setCancelTime(new Date());
			mifiOrder.setOrderStatus(MifiOrder.order_status_9);
			mifiOrderDao.save(mifiOrder);

			totalMoney += Double.valueOf(mifiOrder.getReferenceTotalPrice());
			
		}

		// 预付费渠道商取消订单退款到帐户
		if (!sourceType.equals(Constants.CHANNEL_DEFAULT_VALUE)) {
			DetachedCriteria cDc = channelDao.createDetachedCriteria();
			cDc.add(Restrictions.eq("channelNameEn", sourceType));
			cDc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
			List<Channel> cList = channelDao.find(cDc);
			if (cList!=null && cList.size()>0) {
				Channel channel = cList.get(0);
				if  ("0".equals(channel.getPayType())) {		// 判断是否为预付费渠道商
					channel.setBalance(channel.getBalance() + totalMoney);
					channelDao.getSession().saveOrUpdate(channel);
				}
			}
		}

		logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
		return resObj;
	}

	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized JSONObject saveAndFinishOrder(String keyId, JSONObject param) {
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		String errorMessage = "";
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(df.format(new Date()) + "[入参]：" + param.toString());

		if (!param.containsKey("osn") || StringUtils.isBlank(param.getString("osn"))) {
			errorMessage = "|[订单编号]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		String sourceType = getSourceTypeByKeyId(keyId);
		String outOrderId = param.getString("osn");
		List<MifiOrder> mifiOrderList = queryMifiOrderByOutOrderId(outOrderId, sourceType);
		if (mifiOrderList==null || mifiOrderList.size()==0) {
			errorMessage = "[外部订单号]不存在,无法完成!";
			resObj.put("code", "-1");
			resObj.put("msg", errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		for (MifiOrder mifiOrder : mifiOrderList) {
			if (!mifiOrder.getOrderStatus().equals(MifiOrder.order_status_1)) {
				errorMessage = "当前订单非[已发货]状态,无法完成订单!";
				resObj.put("code", "-1");
				resObj.put("msg", errorMessage);
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
			
			// 订单已开始
			Date now = new Date();
			if (now.after(mifiOrder.getStartDate()) && now.before(mifiOrder.getEndDate())) {
				// 订单结束强制更新设备控制信息, 未完成的订单对应的设备无法再次发货
				StringBuffer buffer = new StringBuffer();
				buffer.append("update mifilist t set UEALLOWED = " + Constants.ueAllowed_n + ", UEALLOWEDMCC = 'homeForbidden' ");
				// 如果是流量订单，则修改mifilist的order_type,flow,start_time,end_time
				if (mifiOrder.getOrderType().equals(MifiOrder.ORDER_TYPE_FLOW)) {
					buffer.append(",order_type='0', flow=0, flow_used=0, start_time=null, end_time=null");
				}
				buffer.append(" where IMEI_6200 in (select dsn from mifi_order_detail where order_id = '" + mifiOrder.getOrderId() + "')");
				mifiOrderDao.updateBySql(buffer.toString(), null);
				
				// 修改mifiversion的speedlimit_type='0'
				mifiOrderDao.updateBySql("update mifiversion a set a.speedlimit_type='0' where IMEI_6200 in (select dsn from mifi_order_detail where order_id = '" + mifiOrder.getOrderId() + "')", null);
				
				// Vodafone卡降级
				String imeis = mifiOrderDetailDao.getDsnByOrderId(mifiOrder.getOrderId());
				if (StringUtils.isNotBlank(imeis))
					updateVodafone(mifiOrder.getAllowedMcc(), imeis.split(","), Constants.VODAFONE_CARD_LEVEL_3);
			}

			// 删除订单中设备已绑定卡的记录
			String sql = "delete from mifitest where imei in (select dsn from mifi_order_detail where order_id='" + mifiOrder.getOrderId() + "')";
			mifitestDao.updateBySql(sql, null);
			
			// 修改订单状态及完成时间
			mifiOrder.setFinishTime(new Date());
			mifiOrder.setOrderStatus(MifiOrder.order_status_8);
			mifiOrderDao.save(mifiOrder);
			
		}
		
		logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
		return resObj;
	}

	/**
	 * 
	 * @Description 取商品列表
	 * @param param
	 * @return JSONObject  
	 * @author yifang.huang
	 * @date 2016年5月9日 下午2:10:21
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public JSONObject getGoods(JSONObject param) {
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		String errorMessage = "";

		if (!param.containsKey("channelId") || StringUtils.isBlank(param.getString("channelId"))) {
			errorMessage = "|[运营商]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			return resObj;
		}
		String channelId = param.getString("channelId");
		// 渠道商
		List<Channel> channelList = channelDao.findList(channelId);
		if (channelList==null || channelList.size()==0) {
			errorMessage = "|[运营商]未找到!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			return resObj;
		}
		//Channel channel = channelList.get(0);
		
		// 渠道对应价格列表[根据价格取区域拼装成国家列表]
		List<Price> priceList = priceDao.findChannelPriceList(channelId);
		if (priceList!=null && priceList.size()>0) {
			JSONArray jsonArr = new JSONArray();
			JSONObject jsonObj;
			HashMap<String, String> mccesMap = null;
			for (Price price : priceList) {
				jsonObj = new JSONObject();
				Region region = price.getRegion();
				String[] countryCodeArr = region.getCountryCodes().split(",");
				String[] countryNameArr = region.getCountryNames().split(",");
				for (int i=0; i<countryCodeArr.length; i++) {
					jsonObj.put("countryCode", countryCodeArr[i]);
					jsonObj.put("countryName", countryNameArr[i]);
					mccesMap = mifiOrderDao.findMccByCountryCode(countryCodeArr[i]);
					if (mccesMap != null)
						jsonObj.put("mcces", mccesMap.get("mcces"));
					jsonObj.put("price", price.getPrice());
					jsonArr.add(jsonObj);
					mccesMap = null;
				}
				jsonObj = null;
			}
			resObj.put("data", jsonArr);
		} else {
			errorMessage = "|未找到数据!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			return resObj;
		}

		return resObj;
	}

	/**
	 * 
	 * @Description 订单终止
	 * @param keyId
	 * @param param
	 * @return JSONObject  
	 * @author yifang.huang
	 * @date 2016年6月13日 上午11:04:14
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized JSONObject saveAndEndOrder(String keyId, JSONObject param) {
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		String errorMessage = "";
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(df.format(new Date()) + "[入参]：" + param.toString());

		if (!param.containsKey("osn") || StringUtils.isBlank(param.getString("osn"))) {
			errorMessage = "|[订单编号]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		// 只有订单模式、后付费的运营商才能终止订单
		String sourceType = getSourceTypeByKeyId(keyId);
		if (sourceType.equals(Constants.CHANNEL_DEFAULT_VALUE)) {
			resObj.put("code", "-1");
			resObj.put("msg", "订单终止接口只提供给订单模式、后付费的运营商使用!");
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		DetachedCriteria cDc = channelDao.createDetachedCriteria();
		cDc.add(Restrictions.eq("channelNameEn", sourceType));
		cDc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
		List<Channel> cList = channelDao.find(cDc);
		if (cList!=null && cList.size()>0) {
			Channel channel = cList.get(0);
			// 判断是否为订单模式、后付费渠道商
			if  (!"1".equals(channel.getPayType()) || !"ORDER".equals(channel.getModel())) {	
				resObj.put("code", "-1");
				resObj.put("msg", "订单终止接口只提供给订单模式、后付费的运营商使用!");
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;	
			}
		}
		
		String outOrderId = param.getString("osn");
		List<MifiOrder> mifiOrderList = queryMifiOrderByOutOrderId(outOrderId, sourceType);
		if (mifiOrderList==null || mifiOrderList.size()==0) {
			errorMessage = "[外部订单号]不存在,无法终止!";
			resObj.put("code", "-1");
			resObj.put("msg", errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		
		for (MifiOrder mifiOrder : mifiOrderList) {
			// 订单状态判断
			if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_3)) {
				resObj.put("code", "-1");
				resObj.put("msg", "当前订单已终止,无法终止!");
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			} 
			if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_8)) {
				resObj.put("code", "-1");
				resObj.put("msg", "当前订单已完成,无法终止!");
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			} 
			if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_9)) {
				resObj.put("code", "-1");
				resObj.put("msg", "当前订单已取消,无法终止!");
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			} 
			if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_11)) {
				resObj.put("code", "-1");
				resObj.put("msg", "订单已删除，无法终止！");
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
			
			// 判断行程是否在行程内
			if (mifiOrder.getStartDate().after(new Date())) {
				resObj.put("code", "-1");
				resObj.put("msg", "当前订单行程未开始,无法终止!");
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
			if (mifiOrder.getEndDate().before(new Date())) {
				resObj.put("code", "-1");
				resObj.put("msg", "当前订单行程已结束,无法终止!");
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}

			// 当前订单行程已发货后取消,需重置设备属性
			if (mifiOrder.getOrderId().equals(MifiOrder.order_status_1)) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("update mifilist t set UEALLOWED = " + Constants.ueAllowed_n + ", UEALLOWEDMCC = 'homeForbidden' ");
				// 如果是流量订单，则修改mifilist的order_type,flow,start_time,end_time
				if (mifiOrder.getOrderType().equals(MifiOrder.ORDER_TYPE_FLOW)) {
					buffer.append(",order_type='0', flow=0, flow_used=0, start_time=null, end_time=null");
				}
				buffer.append(" where IMEI_6200 in (select dsn from mifi_order_detail where order_id = '" + mifiOrder.getOrderId() + "')");
				mifiOrderDao.updateBySql(buffer.toString(), null);
				
				// 修改mifiversion的speedlimit_type='0'
				mifiOrderDao.updateBySql("update mifiversion a set a.speedlimit_type='0' where IMEI_6200 in (select dsn from mifi_order_detail where order_id = '" + mifiOrder.getOrderId() + "')", null);
				
				// Vodafone卡降级
				String imeis = mifiOrderDetailDao.getDsnByOrderId(mifiOrder.getOrderId());
				if (StringUtils.isNotBlank(imeis))
					updateVodafone(mifiOrder.getAllowedMcc(), imeis.split(","), Constants.VODAFONE_CARD_LEVEL_3);
				
			}
			endOrderPrice(mifiOrder);	// 重新计算订单总价
			mifiOrder.setCancelTime(new Date()); // 终止时间
			mifiOrder.setEndDate(new Date());	// 修改订单行程结束时间（方便设备使用记录统计）
			mifiOrder.setOrderStatus(MifiOrder.order_status_3);
			mifiOrderDao.save(mifiOrder);

		}

		logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
		return resObj;
	}

	/**
	 * 
	 * @Description 取设备订单信息
	 * @param param
	 * @return 
	 * @return JSONObject  
	 * @author yifang.huang
	 * @date 2016年9月22日 上午11:06:16
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public JSONObject getDeviceOrderInfo(JSONObject param) {
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		String errorMessage = "";

		if (!param.containsKey("dsn") || StringUtils.isBlank(param.getString("dsn"))) {
			errorMessage = "|[设备编号]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			return resObj;
		}
		String dsn = param.getString("dsn");
		
		JSONObject jsonObj = new JSONObject();
		// 根据设备编号取订单
		List<Map<String, String>> listMap = mifiOrderDao.getValidOrder(dsn, null);
		if (listMap!=null && listMap.size()>0) {
			// 订单信息
			Map<String, String> map = listMap.get(0);
			jsonObj.put("outOrderId", map.get("outOrderId"));
			jsonObj.put("allowedMcc", map.get("allowedMcc"));
			jsonObj.put("startDate", map.get("startDate"));
			jsonObj.put("endDate", map.get("endDate"));
			
			// 客户信息
			String customerId = map.get("customerId");
			if (StringUtils.isNotBlank(customerId)) {
				Customer customer = customerDao.get(customerId);
				if (customer != null) {
					jsonObj.put("userName", customer.getName());
					jsonObj.put("phone", customer.getPhone());
					jsonObj.put("email", customer.getEmail());
					jsonObj.put("passportNo", customer.getPassportNo());
					jsonObj.put("passportPy", customer.getPassportPy());
				}
			}
		}
		resObj.put("data", jsonObj);
		
		return resObj;
	}

	public Page<HashMap> mifiOrderStockList(Page<HashMap> page, Map<String, Object> paramMap) {
		String sqlString = "select * from (";
		sqlString += "select t.source_type,t.allowed_mcc,ifnull(t.stock_status,'0') stock_status,sum(t.equipment_cnt) equipment_cnt from mifi_order t ";
		sqlString += " where 1 = 1 ";
		// 已下单 已发货
		sqlString += " and t.order_status in ('" + MifiOrder.order_status_0 + "','" + MifiOrder.order_status_1 + "')";
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			sqlString += " and t.source_type = '" + sourceType + "'";
		}
		String stockStatus = ObjectUtils.toString(paramMap.get("stockStatus"));
		if (StringUtils.isNotBlank(stockStatus)) {
			sqlString += " and t.stock_status = '" + stockStatus + "'";
		}
		String startDate = ObjectUtils.toString(paramMap.get("startDate"));
		if (StringUtils.isNotBlank(startDate)) {
			sqlString += " and date(t.start_date) >= str_to_date('" + startDate + "','%Y-%m-%d')";
		}
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(endDate)) {
			sqlString += " and date(t.start_date) <= str_to_date('" + endDate + "','%Y-%m-%d')";
		}
		String allowedMcc = ObjectUtils.toString(paramMap.get("allowedMcc"));
		if (StringUtils.isNotBlank(allowedMcc)) {
			sqlString += " and t.allowed_mcc like '%" + allowedMcc + "%'";
		}
		sqlString += " group by t.source_type,t.allowed_mcc,ifnull(t.stock_status,'0') ) as t_";
		Page<HashMap> _page = mifiOrderDao.findBySql(page, sqlString, Map.class);
		List<HashMap> list = _page.getList();
		for (int i = 0; i < list.size(); i++) {
			list.get(i).putAll(mifiOrderDao.getMccs((String) list.get(i).get("allowed_mcc")));
		}
		_page.setList(list);
		return _page;
	}

	public void updateStockSimCard(Map<String, Object> paramMap) {
		StringBuffer sb = new StringBuffer();
		sb.append("update mifi_order t set t.stock_status = '" + MifiOrder.stock_status_1 + "' ");
		sb.append(",stock_user = '" + UserUtils.getUser().getLoginName() + "' ");
		sb.append(",stock_time = now() ");
		sb.append(" where t.stock_status = '" + MifiOrder.stock_status_0 + "' ");
		// 已下单 已发货
		sb.append(" and t.order_status in ('" + MifiOrder.order_status_0 + "','" + MifiOrder.order_status_1 + "')");
		sb.append(" and t.allowed_mcc = '" + paramMap.get("allowed_mcc") + "' ");
		sb.append(" and t.source_type = '" + paramMap.get("source_type") + "' ");
		String startDate = ObjectUtils.toString(paramMap.get("startDate"));
		if (StringUtils.isNotBlank(startDate)) {
			sb.append(" and date(t.start_date) >= str_to_date('" + startDate + "','%Y-%m-%d')");
		}
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(endDate)) {
			sb.append(" and date(t.start_date) <= str_to_date('" + endDate + "','%Y-%m-%d')");
		}
		mifiOrderDao.updateBySql(sb.toString(), null);
	}

	
	public void updateStockSimCardByOrderId(String orderId) {
		StringBuffer sb = new StringBuffer();
		sb.append("update mifi_order t set t.stock_status = '" + MifiOrder.stock_status_1 + "' ");
		sb.append(",stock_user = '" + UserUtils.getUser().getLoginName() + "' ");
		sb.append(",stock_time = now() ");
		sb.append(" where t.stock_status = '" + MifiOrder.stock_status_0 + "' ");
		sb.append(" and t.order_id = '" + orderId + "' ");
		mifiOrderDao.updateBySql(sb.toString(), null);
	}

	public Page<HashMap> mifiOrderList(Page<HashMap> page, Map<String, Object> paramMap, List<Region> regionList) {
		String sqlString = "select * from mifi_order t where 1 = 1";
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			sqlString += " and t.source_type = '" + sourceType + "'";
		}
		String outOrderId = ObjectUtils.toString(paramMap.get("outOrderId"));
		if (StringUtils.isNotBlank(outOrderId)) {
			sqlString += " and t.out_order_id = '" + outOrderId.trim() + "'";
		}
		String dsn = ObjectUtils.toString(paramMap.get("dsn"));
		if (StringUtils.isBlank(outOrderId) && StringUtils.isNotBlank(dsn)) {
			String sql = processDsn(dsn);
			if (StringUtils.isNotBlank(sql))
				sqlString += sql;
		}
		String orderStatus = ObjectUtils.toString(paramMap.get("orderStatus"));
		if (StringUtils.isNotBlank(orderStatus)) {
			sqlString += " and t.order_status = '" + orderStatus + "'";
		}
		String stockStatus = ObjectUtils.toString(paramMap.get("stockStatus"));
		if (StringUtils.isNotBlank(stockStatus)) {
			sqlString += " and t.stock_status = '" + stockStatus + "'";
		}
		//行程开始时间
		String startDateBegin = ObjectUtils.toString(paramMap.get("startDateBegin"));
		if (StringUtils.isNotBlank(startDateBegin)) {
			sqlString += " and date(t.start_date) >= str_to_date('" + startDateBegin + "','%Y-%m-%d')";
		}
		String startDateEnd = ObjectUtils.toString(paramMap.get("startDateEnd"));
		if (StringUtils.isNotBlank(startDateEnd)) {
			sqlString += " and date(t.start_date) <= str_to_date('" + startDateEnd + "','%Y-%m-%d')";
		}
		//行程结束时间
		String endDateBegin = ObjectUtils.toString(paramMap.get("endDateBegin"));
		if (StringUtils.isNotBlank(endDateBegin)) {
			sqlString += " and date(t.end_date) >= str_to_date('" + endDateBegin + "','%Y-%m-%d')";
		}
		String endDateEnd = ObjectUtils.toString(paramMap.get("endDateEnd"));
		if (StringUtils.isNotBlank(endDateEnd)) {
			sqlString += " and date(t.end_date) <= str_to_date('" + endDateEnd + "','%Y-%m-%d')";
		}
		// 订单时间
		String outOrderTimeStart = ObjectUtils.toString(paramMap.get("outOrderTimeStart"));
		if (StringUtils.isNotBlank(outOrderTimeStart)) {
			sqlString += " and date(t.out_order_time) >= str_to_date('" + outOrderTimeStart + "','%Y-%m-%d %H:%i:%s')";
		}
		String outOrderTimeEnd = ObjectUtils.toString(paramMap.get("outOrderTimeEnd"));
		if (StringUtils.isNotBlank(outOrderTimeEnd)) {
			sqlString += " and date(t.out_order_time) <= str_to_date('" + outOrderTimeEnd + "','%Y-%m-%d %H:%i:%s')";
		}
		String allowedMcc = ObjectUtils.toString(paramMap.get("allowedMcc"));
		String allowedRegion = ObjectUtils.toString(paramMap.get("allowedRegion"));
		String[] mccsArr = null;
		if (StringUtils.isNotBlank(allowedRegion) && null != regionList && 0 < regionList.size()) {
			String regionMccs = StringUtils.EMPTY;
			for(Region region:regionList){
				if(allowedRegion.equals(region.getCode())){
					regionMccs = region.getMcces();
					break;
				}
			}
			if(StringUtils.isNotBlank(regionMccs)){
				mccsArr = regionMccs.split(Constants.COMMA);
			}
		}
		if (null == mccsArr || 0 == mccsArr.length) {
			if(StringUtils.isNotBlank(allowedMcc)){
				sqlString += " and t.allowed_mcc like '%" + allowedMcc + "%'";
			}
		}else{
			boolean isAllowedMccDuplicated = false;
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(" and (");
			for(String mcc: mccsArr){
				if(mcc.equals(allowedMcc)){
					isAllowedMccDuplicated = true;
				}
				stringBuilder.append(" t.allowed_mcc like '%" + mcc + "%' or ");
			}
			if(StringUtils.isNotBlank(allowedMcc) && !isAllowedMccDuplicated){
				stringBuilder.append(" t.allowed_mcc like '%" + allowedMcc + "%' or ");
			}
			int builderLength = stringBuilder.length();
			stringBuilder.delete(builderLength - 3, builderLength);
			stringBuilder.append(") ");
			sqlString += stringBuilder.toString();
		}
			
		//订单进行中
		String orderStarting = ObjectUtils.toString(paramMap.get("orderStarting"));
		if(StringUtils.isNotBlank(orderStarting)){
			sqlString += " and NOW() >= t.start_date AND NOW() <= t.end_date";
			sqlString += " and t.order_status = 1";
		}
		sqlString += " order by t.out_order_time desc ";
		Page<HashMap> _page = mifiOrderDao.findBySql(page, sqlString, Map.class);
		List<HashMap> list = _page.getList();
		for (int i = 0; i < list.size(); i++) {
			list.get(i).putAll(mifiOrderDao.getMccs((String) list.get(i).get("allowed_mcc")));
			String _orderStatus = (String) list.get(i).get("order_status");
			if (!_orderStatus.equals(MifiOrder.order_status_0)) {
				list.get(i).put("dsn", mifiOrderDetailDao.getDsnByOrderId((String) list.get(i).get("order_id")));
				list.get(i).put("ssid", mifiOrderDetailDao.getSsidByOrderId((String) list.get(i).get("order_id")));
			}
		}
		_page.setList(list);
		return _page;
	}
	
	private String processDsn(String dsn) {

		String sql = "";
		List<MifiOrderDetail> detailList = mifiOrderDetailDao.getByDsn(dsn);
		
		if (detailList==null || detailList.size()==0)
			sql = " and t.out_order_id = '000***000'";
		else if (detailList.size() == 1)
			sql = " and t.out_order_id = '" + detailList.get(0).getOutOrderId() + "'";
		else {
			String ids = "";
			for (MifiOrderDetail detail : detailList) {
				ids += "','" + detail.getOutOrderId();
			}
			ids = ids.substring(2);
			sql = " and t.out_order_id in(" + ids + "')";
		}
		
		return sql;
	}

	/**
	 * 
	 * @Description 根据查询条件取列表数据
	 * @param paramMap
	 * @return List<MifiOrder>
	 * @author yifang.huang
	 * @date 2016年4月25日 下午3:21:53
	 */
	public List<MifiOrder> mifiOrderList(Map<String, String> paramMap) {

		DetachedCriteria detachedCriteria = mifiOrderDao.createDetachedCriteria();
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			detachedCriteria.add(Restrictions.eq("sourceType", sourceType));
		}
		// 有效订单
		String validOrder = ObjectUtils.toString(paramMap.get("validOrder"));
		if (StringUtils.isNotBlank(validOrder)) {
			detachedCriteria.add(Restrictions.or(Restrictions.eq("orderStatus", "0"),
					Restrictions.or(Restrictions.eq("orderStatus", "1"), Restrictions.eq("orderStatus", "8"))));
		}
		// 订单时间
		String outOrderTimeStart = ObjectUtils.toString(paramMap.get("outOrderTimeStart"));
		if (StringUtils.isNotBlank(outOrderTimeStart)) {
			try {
				detachedCriteria.add(Restrictions.ge("outOrderTime",
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(outOrderTimeStart)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		String outOrderTimeEnd = ObjectUtils.toString(paramMap.get("outOrderTimeEnd"));
		if (StringUtils.isNotBlank(outOrderTimeEnd)) {
			try {
				detachedCriteria.add(Restrictions.le("outOrderTime",
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(outOrderTimeEnd)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		List<MifiOrder> list = mifiOrderDao.find(detachedCriteria);
		if (list != null && list.size() > 0) {
			for (MifiOrder order : list) {
				order.setDsns(mifiOrderDetailDao.getDsnByOrderId(order.getOrderId()));
				order.setSsids(mifiOrderDetailDao.getSsidByOrderId(order.getOrderId()));

				@SuppressWarnings("rawtypes")
				HashMap mccMap = mifiOrderDao.getMccs(order.getAllowedMcc());
				order.setAllowedMccCn(ObjectUtils.toString(mccMap.get("allowed_mcc_cn")));
				order.setAllowedMccEn(ObjectUtils.toString(mccMap.get("allowed_mcc_en")));
			}
		}
		return list;
	}
	/**
	 * 
	 * @Description 根据设备编号判断设备是否可用，在当前时间点是否存在有效的订单
	 * @param sn
	 * @param orderType
	 * @return boolean
	 * @author yifang.huang
	 * @date 2016年3月31日 下午1:34:57
	 */
	public List<Map<String, String>> getValidOrder(String sn, String orderType) {
		return mifiOrderDao.getValidOrder(sn, orderType);
	}

	public List<MifiOrderDetail> getOrderDetailByOrderId(String orderId) {
		return mifiOrderDetailDao.getByOrderId(orderId);
	}

	/**
	 * 
	 * @Description 根据设备编号取ssid
	 * @param dsn
	 *            设备编号
	 * @return String
	 * @author yifang.huang
	 * @date 2016年4月21日 下午3:37:00
	 */
	private String getSsidByDsn(String dsn) {
		Map<String, String> mifi = mifiManageDao.getMifilistBySn(dsn);
		if (mifi != null)
			return mifi.get("ssid");
		return null;
	}
	
	/**
	 * 
	 * @Description 保存发货接口客户信息
	 * @param param
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年9月22日 上午10:51:13
	 */
	private String saveCustomer(JSONObject param) {
		
		try {
			String userName = "", phone = "";
			if (param.containsKey("userName"))
				userName = param.getString("userName");
			if (param.containsKey("phone"))
				phone = param.getString("phone");
			
			if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(phone)) {
				
				String email = "", passportNo = "" , passportPy = "";
				if (param.containsKey("email"))
					email = param.getString("email");
				if (param.containsKey("passportNo"))
					passportNo = param.getString("passportNo");
				if (param.containsKey("passportPy"))
					passportPy = param.getString("passportPy");
				
				Customer customer = new Customer();
				customer.setId(IdGen.uuid());
				customer.setName(userName);
				customer.setPhone(phone);
				customer.setEmail(email);
				customer.setPassportNo(passportNo);
				customer.setPassportPy(passportPy);
				customer.setCreateDate(new Date());
				customer.setDelFlag("0");
				customerDao.getSession().save(customer);
				
				return customer.getId();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	private void updateVodafone(String allowedMcc, String[] imeiArr, String level) {
		try {
			// 塞班关岛，马代，马来西亚，澳门，澳洲，印度的订单对应设备副卡升到5
			String mccForViceCard5 = DictUtils.getDictValue(Constants.DICT_MCC_FOR_VODAFONE_CARD_5_LABEL, Constants.DICT_MCC_FOR_VODAFONE_CARD_5_TYPE, "");
			if (StringUtils.isNotBlank(mccForViceCard5) && com.uu.common.utils.StringUtils.mccInclude(allowedMcc, mccForViceCard5)) {
				String iccIds = "";
				Map<String, String> mifiMap = null;
				for (String imei : imeiArr) {
					mifiMap = mifiManageDao.getMifilistBySn(imei);
					if (mifiMap != null) {
						if (StringUtils.isNotBlank(mifiMap.get("vfIccId"))) {
							iccIds += "," + mifiMap.get("vfIccId");
						}
						mifiMap = null;
					}
				}
				// 升级
				if (StringUtils.isNotBlank(iccIds)) {
					VodafoneThread thread = new VodafoneThread(iccIds.substring(1), level);
					thread.start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Vodafone卡升级失败！");
		}
					
	}
	
	/****************************** 设置订单金额 TODO ****************************/
	/**
	 * 
	 * @Description 设置天数、参考单价、参考总价赋值(折单)
	 * @param order
	 * @return double
	 * @author yifang.huang
	 * @date 2016年5月4日 下午3:49:06
	 */
	private Map<String, String> setDaysAndPriceOne(MifiOrder order, Region region) {
		
		Map<String, String> map = new HashMap<String, String>();
		// 参考单价
		String unitPrice = null;
		Price price = null;
		Channel channel = null;
		DetachedCriteria cDc = channelDao.createDetachedCriteria();
		cDc.add(Restrictions.eq("channelNameEn", order.getSourceType()));
		cDc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
		List<Channel> cList = channelDao.find(cDc);
		if (cList!=null && cList.size()>0)
			channel = cList.get(0);
		else {	// 未找到设备运营商
			map.put("code", "-1");
			map.put("msg", "未找到该设备的运营商，请与客服联系！");
			return map;
		}
		// 价格
		List<Price> priceList = priceDao.findList(channel.getId(), region.getId());
		if (priceList==null || priceList.size()==0) {
			map.put("code", "-1");
			map.put("msg", "价格配置错误，请与客服联系！");
			return map;
		}
		price = priceList.get(0);

		// 行程天数
		Date startDate = order.getStartDate();
		Date endDate = order.getEndDate();
		int days = getDays(startDate, endDate);
		
		// 参考总价
		String totalPrice = null;
		if (price != null) {
			DecimalFormat df = new DecimalFormat("0.00");
			Double money = price.getPrice();
			int devices = Integer.valueOf(order.getEquipmentCnt());
			totalPrice = df.format(money * devices * days);
			unitPrice = ObjectUtils.toString(money);
		}

		order.setDays(days);
		order.setReferenceUnitPrice(unitPrice);
		order.setReferenceTotalPrice(totalPrice);

		map.put("code", "1");
		map.put("totalPrice", totalPrice);
		return map;
	}
	
	/**
	 * 
	 * @Description 设置天数、参考单价、参考总价赋值(不折单)
	 * 如果不拆单，订单的单价为各个区域价格总和
	 * @param order
	 * @return double
	 * @author yifang.huang
	 * @date 2016年5月4日 下午3:49:06
	 */
	private Map<String, String> setDaysAndPriceTwo(MifiOrder order) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		// 取运营商对象
		Channel channel = null;
		DetachedCriteria cDc = channelDao.createDetachedCriteria();
		cDc.add(Restrictions.eq("channelNameEn", order.getSourceType()));
		cDc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
		List<Channel> cList = channelDao.find(cDc);
		if (cList!=null && cList.size()>0)
			channel = cList.get(0);
		else {	// 未找到设备运营商
			map.put("code", "-1");
			map.put("msg", "未找到该设备的运营商，请与客服联系！");
			return map;
		}
		
		// 参考单价
		BigDecimal unitPrice = new BigDecimal("0");
		String mcces = order.getAllowedMcc();
		String[] mccArr = mcces.split(",");
		Set<Region> regionSet = new HashSet<Region>();// 区域集合
		for (String str : mccArr) {
			Region region = regionDao.findByMcc(str);
			regionSet.add(region);
		}
		
		if (regionSet.size() > 0) {
			for (Region bean : regionSet) {
				// 价格
				List<Price> priceList = priceDao.findList(channel.getId(), bean.getId());
				if (priceList==null || priceList.size()==0) {
					map.put("code", "-1");
					map.put("msg", "价格配置错误，请与客服联系！");
					return map;
				}
				Price price = priceList.get(0);
				if (unitPrice.doubleValue() < price.getPrice())
					unitPrice = new BigDecimal(price.getPrice());
			}
		}
		
		// 行程天数
		Date startDate = order.getStartDate();
		Date endDate = order.getEndDate();
		int days = getDays(startDate, endDate);
		
		// 参考总价
		String totalPrice = null;
		if (unitPrice.compareTo(new BigDecimal("0")) == 1) {
			DecimalFormat df = new DecimalFormat("0.00");
			int devices = Integer.valueOf(order.getEquipmentCnt());
			totalPrice = df.format(unitPrice.multiply(new BigDecimal(devices)).multiply(new BigDecimal(days)));
		}

		order.setDays(days);
		order.setReferenceUnitPrice(unitPrice.toString());
		order.setReferenceTotalPrice(totalPrice);

		map.put("code", "1");
		map.put("totalPrice", totalPrice);
		return map;
	}
	
	/**
	 * 
	 * @Description 订单延期修改订单参考总价
	 * @param order
	 * @param delayDate
	 * @return String  延期金额
	 * @author yifang.huang
	 * @date 2016年5月12日 下午3:42:04
	 */
	private String setTotalPrice(MifiOrder order, Date delayDate) {

		// 延期前订单金额
		Double tempTotalMoney = Double.valueOf(order.getReferenceTotalPrice());
		
		// 延期天数
		int delayDays = getDays(order.getStartDate(), delayDate);
		order.setDays(order.getDays() + delayDays);

		// 参考单价
		String unitPriceStr = order.getReferenceUnitPrice();
		Double unitPrice = Double.valueOf(unitPriceStr);

		// 参考总价
		int devices = Integer.valueOf(order.getEquipmentCnt());
		DecimalFormat df = new DecimalFormat("0.00");
		String totalPrice = df.format(unitPrice*devices*order.getDays());		// 延期后订单金额
		order.setReferenceTotalPrice(totalPrice);
		
		return df.format((unitPrice*devices*order.getDays() - tempTotalMoney));
	}
	
	/**
	 * 
	 * @Description 订单终止价格计算
	 * @param order
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年11月17日 上午10:50:59
	 */
	private void endOrderPrice(MifiOrder order) {

		// 延期天数
		int days = getDays(order.getStartDate(), new Date());
		order.setDays(days);

		// 参考单价
		String unitPriceStr = order.getReferenceUnitPrice();
		Double unitPrice = Double.valueOf(unitPriceStr);

		// 参考总价
		int devices = Integer.valueOf(order.getEquipmentCnt());
		DecimalFormat df = new DecimalFormat("0.00");
		String totalPrice = df.format(unitPrice * devices * days);		// 终止时订单总金额
		order.setReferenceTotalPrice(totalPrice);
		
	}
	/****************************** 设置订单金额 end ****************************/

	/****************************** 生成csv文件 TODO ****************************/
	/**
	 * 
	 * @Description 生成csv文件
	 * @param startDate
	 * @param endDate
	 * @param channel
	 * @return Map<String,String>
	 * @author yifang.huang
	 * @date 2016年4月20日 上午10:11:35
	 */
	public synchronized Map<String, String> createCsvFiles(Date startDate, Date endDate, Channel channel) {

		Map<String, String> map = new HashMap<String, String>();

		int sum = 0;
		try {
			// 文件存放路径
			PropertiesLoader propertiesLoader = new PropertiesLoader("app.properties");
			String csvDir = propertiesLoader.getProperty("consumeRecordFiles_dir");

			int flag = 0;
			Date endDateTemp = null;
			Map<String, String> paramMap = null;
			List<MifiOrder> list = null;
			while (flag < 100) {

				endDateTemp = getEndTime(startDate);

				// 查询渠道对应的完成的订单记录
				paramMap = new HashMap<String, String>();
				paramMap.put("sourceType", channel.getChannelNameEn());
				paramMap.put("outOrderTimeStart", getFullDate(startDate));
				paramMap.put("outOrderTimeEnd", getFullDate(endDateTemp));
				paramMap.put("validOrder", "validOrder");
				list = mifiOrderList(paramMap);

				// 生成文件
				if (list != null && list.size() > 0) {
					createCsvFile(endDateTemp, csvDir, channel, list);
					sum++;
				}

				paramMap = null;
				list = null;

				if (endDateTemp.getTime() == endDate.getTime())
					break;
				flag++;
				startDate = endDateTemp;
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("code", "-1");
			map.put("msg", "生成csv文件失败");
			return map;
		}

		map.put("code", "1");
		map.put("msg", "生成csv文件成功[" + sum + "个]");
		return map;

	}

	/**
	 * 
	 * @Description 生成csv文件
	 * @param fileDate
	 * @param csvDir
	 * @param channel
	 * @param orderList
	 * @return void
	 * @author yifang.huang
	 * @date 2016年4月25日 下午4:38:08
	 */
	private void createCsvFile(Date fileDate, String csvDir, Channel channel, List<MifiOrder> orderList) {

		// 行数据
		List<String> data = getDataStr(orderList);

		// 目录路径
		String fPath = csvDir + File.separator + channel.getChannelNameEn();

		// 文件名
		String fName = "ORDER" + new SimpleDateFormat("yyyyMMddHH").format(fileDate) + ".csv";

		// 生成csv文件
		CsvWriter.createCSVFile(data, fPath, fName);
	}

	/**
	 * 
	 * @Description 取后一小时时间
	 * @return Date
	 * @author yifang.huang
	 * @date 2016年4月19日 上午10:30:57
	 */
	private Date getEndTime(Date startDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		c.add(Calendar.HOUR, 1);
		return c.getTime();
	}

	// 转成string
	private List<String> getDataStr(List<MifiOrder> orderList) {

		List<String> data = new ArrayList<String>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String money = "0";
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		for (MifiOrder order : orderList) {

			// 头
			String dsns = (StringUtils.isNotBlank(order.getDsns()) ? order.getDsns().replace(",", ";")
					: order.getDsns());
			String ssids = (StringUtils.isNotBlank(order.getSsids()) ? order.getSsids().replace(",", ";")
					: order.getSsids());
			String allowedMcc = (StringUtils.isNotBlank(order.getAllowedMcc()) ? order.getAllowedMcc().replace(",", ";")
					: order.getAllowedMcc());

			data.add("H," + dsns + "," + ssids + "," + order.getReferenceTotalPrice() + "," + allowedMcc + ","
					+ df.format(order.getStartDate()) + "," + df.format(order.getStartDate()) + ","
					+ df.format(order.getStartDate()) + ",9999999999");

			// 订单详情
			List<MifiOrderDetail> list = getOrderDetailByOrderId(ObjectUtils.toString(order.getOrderId()));
			if (list != null && list.size() > 0) {
				// 计算每台设备的金额
				Double totalPrice = Double.valueOf(order.getReferenceTotalPrice());
				Integer num = Integer.valueOf(order.getEquipmentCnt());
				money = decimalFormat.format(totalPrice / num);

				for (MifiOrderDetail deatil : list) {
					// 详情
					data.add("D," + deatil.getDsn() + "," + deatil.getSsid() + "," + money + "," + allowedMcc + ","
							+ df.format(order.getStartDate()) + "," + df.format(order.getOutOrderTime()) + ","
							+ df.format(order.getEndDate()) + "," + deatil.getOrderDetailId());
				}
			}

			// 尾
			data.add("H," + dsns + "," + ssids + "," + order.getReferenceTotalPrice() + "," + allowedMcc + ","
					+ df.format(order.getStartDate()) + "," + df.format(order.getStartDate()) + ","
					+ df.format(order.getStartDate()) + ",9999999999");

			money = "0";
		}

		return data;

	}

	// 日期补上分秒
	private String getFullDate(Date date) throws ParseException {

		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH");

		String dateStr = df1.format(date);
		dateStr = dateStr + ":00:00";

		return dateStr;
	}
	/****************************** 生成csv文件 结束 ****************************/

	/****************************** 渠道商PC端订单管理 TODO ****************************/
	/**
	 * 
	 * @Description 价格预览
	 * @param paramMap
	 * @return List<Map<String,String>>  
	 * @author yifang.huang
	 * @date 2016年5月6日 下午5:45:38
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized Map<String, Object> preview(Map<String, Object> paramMap) {
		
		Map<String, Object> map = new HashMap<String, Object>();	
		List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
		
		
		// 参数
		String regionIdPrices = ObjectUtils.toString(paramMap.get("regionIdPrices"));
		String imeis = ObjectUtils.toString(paramMap.get("imeis"));
		String startDateStr = ObjectUtils.toString(paramMap.get("startDate"));
		String endDateStr = ObjectUtils.toString(paramMap.get("endDate"));
		
		// 参数判断
		if (StringUtils.isBlank(regionIdPrices)) {
			map.put("code", "-1");
			map.put("msg", "请选择产品");
			return map;
		}
		if (StringUtils.isBlank(imeis)) {
			map.put("code", "-1");
			map.put("msg", "请选择设备");
			return map;
		}
		if (StringUtils.isBlank(startDateStr)) {
			map.put("code", "-1");
			map.put("msg", "请选择出国时间");
			return map;
		}
		if (StringUtils.isBlank(endDateStr)) {
			map.put("code", "-1");
			map.put("msg", "请选择回国时间");
			return map;
		}
		// 开始时间不能等结束时间
		if (startDateStr.equals(endDateStr)) {
			map.put("code", "-1");
			map.put("msg", "出国时间不能等于回国时间");
			return map;
		}
		// 订单开始结束时间格式转换
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDateStr + " 00:00:00");
			endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDateStr + " 23:59:59");
		} catch (ParseException e1) {
			e1.printStackTrace();
			map.put("code", "-1");
			map.put("msg", "出国时间或者回国时间格式错误");
			return map;
		}
		// 开始时间不能大于结束时间
		if (startDate.after(endDate)) {
			map.put("code", "-1");
			map.put("msg", "回国时间不能早于出国时间");
			return map;
		}
		
		// 区域对应价格处理
		Map<String, String> regionIdPriceMap = new HashMap<String, String>();
		String[] regionIdPriceArr = regionIdPrices.split(",");
		String[] tempArr = null;
		for (String regionIdPrice : regionIdPriceArr) {
			tempArr = regionIdPrice.split("#");
			regionIdPriceMap.put(tempArr[0], tempArr[1]);
		}
		// 天数
		int days = getDays(startDate, endDate);
		// 设备数
		String[] imeiArr = imeis.split(",");
		int devices = imeiArr.length;

		Map<String, String> item = null;
		Region region = null;
		DecimalFormat df = new DecimalFormat("0.00");
		Double totalMoney = 0.00;
		for (Map.Entry<String, String> entry : regionIdPriceMap.entrySet()) {
			
			item = new HashMap<String, String>();
			region = regionDao.get(entry.getKey());
			
			item.put("goodsName", region.getName());
			item.put("trip", startDateStr + " 至  " + endDateStr);
			item.put("days", days + " 天 ");
			item.put("devices", String.valueOf(devices));
			item.put("totalPrice", df.format(Double.valueOf(entry.getValue()) * devices * days));
			totalMoney += (Double.valueOf(entry.getValue()) * devices * days);
			
			listMap.add(item);
			region = null;
			item = null;
		}
		
		map.put("code", "1");
		map.put("msg", "预览价格成功！");
		map.put("listMap", listMap);
		map.put("totalMoney", df.format(totalMoney));
		
		// 运营商余额
		User user = UserUtils.getUser();
		List<Channel> cList = channelDao.findList(user.getChannelNameEn());
		if (cList!=null && cList.size()>0) {
			Channel channel = cList.get(0);
			map.put("balance", channel.getBalance());
		}
		return map;
	}
	
	/**
	 * 
	 * @Description 订单创建
	 * @param paramMap
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月3日 下午2:34:55
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized Map<String, String> saveAndCreateOrder(Map<String, Object> paramMap) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		// 参数
		String countryCodes = ObjectUtils.toString(paramMap.get("countryCodes"));
		String imeis = ObjectUtils.toString(paramMap.get("imeis"));
		String startDateStr = ObjectUtils.toString(paramMap.get("startDate"));
		String endDateStr = ObjectUtils.toString(paramMap.get("endDate"));
		String limitSpeedFlag = ObjectUtils.toString(paramMap.get("limitSpeedFlag"));
		String remarks = ObjectUtils.toString(paramMap.get("remarks"));
		String userName = ObjectUtils.toString(paramMap.get("userName"));
		String phone = ObjectUtils.toString(paramMap.get("phone"));
		String email = ObjectUtils.toString(paramMap.get("email"));
		String passportNo = ObjectUtils.toString(paramMap.get("passportNo"));
		String passportPy = ObjectUtils.toString(paramMap.get("passportPy"));
		
		// 参数判断
		if (StringUtils.isBlank(countryCodes)) {
			map.put("code", "-1");
			map.put("msg", "请选择产品！");
			return map;
		}
		if (StringUtils.isBlank(imeis)) {
			map.put("code", "-1");
			map.put("msg", "请选择设备！");
			return map;
		}
		if (StringUtils.isBlank(startDateStr)) {
			map.put("code", "-1");
			map.put("msg", "请选择出国时间！");
			return map;
		}
		if (StringUtils.isBlank(endDateStr)) {
			map.put("code", "-1");
			map.put("msg", "请选择回国时间！");
			return map;
		}
		if (StringUtils.isBlank(userName)) {
			map.put("code", "-1");
			map.put("msg", "请输入客户姓名！");
			return map;
		}
		if (StringUtils.isBlank(phone)) {
			map.put("code", "-1");
			map.put("msg", "请输入客户手机号码！");
			return map;
		}
		// 开始时间不能等结束时间
		if (startDateStr.equals(endDateStr)) {
			map.put("code", "-1");
			map.put("msg", "出国时间不能等于回国时间！");
			return map;
		}
		// 订单开始结束时间格式转换
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDateStr + " 00:00:00");
			endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDateStr + " 23:59:59");
		} catch (ParseException e1) {
			e1.printStackTrace();
			map.put("code", "-1");
			map.put("msg", "出国时间或者回国时间格式错误！");
			return map;
		}
		// 开始时间不能大于结束时间
		if (startDate.after(endDate)) {
			map.put("code", "-1");
			map.put("msg", "回国时间不能早于出国时间！");
			return map;
		}
		
		// 保存客户信息
		Customer customer = new Customer();
		customer.setName(userName);
		customer.setPhone(phone);
		customer.setEmail(email);
		customer.setPassportNo(passportNo);
		customer.setPassportPy(passportPy);
		customerDao.save(customer);
		
		// 渠道
		User user = UserUtils.getUser();
		String sourceType = user.getChannelNameEn();
		
		// 保存imei对应的ssid
		Map<String, String> ssidMap = new HashMap<String, String>();

		// string to array
		String[] countryCodeArr = countryCodes.split(",");
		String[] imeiArr = imeis.split(",");

		// 判断设备在当前行程内设备是否已经有有效订单
		map = checkDeviceByTripDate(imeiArr, startDateStr, endDateStr);
		if (!"1".equals(map.get("code")))
			return map;
		
		// 取国家对应mcc的Map，所有Mcc
		Map<String, String> countryMccesMap = new HashMap<String, String>();
		map = getCountryMccesMap(countryCodeArr, countryMccesMap);
		if (!"1".equals(map.get("code")))
			return map;
		String allowedMcc = map.get("allowedMcc");
		
		// 渠道下单系统限制条件判断
		map = validate(ssidMap, countryMccesMap, imeiArr, startDate, sourceType);
		if (!"1".equals(map.get("code")))
			return map;
		
		// 渠道商
		List<Channel> channelList = channelDao.findList(sourceType);
		if (channelList==null || channelList.size()==0) {
			map.put("code", "-1");
			map.put("msg", "[" + sourceType + "] 渠道未找到，请与客服联系！");
			return map;
		}
		Channel channel = channelList.get(0);
		
		// 非订单模式的渠道商不能下订单
		if (!"ORDER".equals(channel.getModel())) {
			map.put("code", "-1");
			map.put("msg", "运营商 ["+channel.getChannelName()+"] 不是订单模式，不能下订单!");
			return map;
		}
		
		try {
			
			Date nowDate = new Date();
			String payType = channel.getPayType();
			if ("1".equals(payType)) {			// 后付费运营商不需要拆单
				MifiOrder _mifiOrder = new MifiOrder();
				String orderId = IdGen.uuid();
				_mifiOrder.setOrderId(orderId);
				_mifiOrder.setOrderStatus(MifiOrder.order_status_1);
				_mifiOrder.setSourceType(sourceType);
				_mifiOrder.setOutOrderId(orderId);
				_mifiOrder.setOutOrderTime(nowDate);
				_mifiOrder.setAllowedMcc(allowedMcc);
				_mifiOrder.setEquipmentCnt(String.valueOf(imeiArr.length));
				_mifiOrder.setStartDate(startDate);
				_mifiOrder.setEndDate(endDate);
				_mifiOrder.setDeliveryTime(nowDate);
				_mifiOrder.setStockStatus(MifiOrder.stock_status_0);
				_mifiOrder.setLimitSpeedFlag(limitSpeedFlag);
				map = setDaysAndPriceTwo(_mifiOrder);
				if (!"1".equals(map.get("code"))) {
					map.put("code", "-1");
					map.put("msg", map.get("msg"));
					return map;
				}
				
				List<MifiOrderDetail> mifiOrderDetails = new ArrayList<MifiOrderDetail>();
				StringBuffer dsns = new StringBuffer();
				for (int i=0; i<imeiArr.length; i++) {
					String dsn = imeiArr[i];
					if (0 == mifiOrderDao.getMifiListCountByDsn(dsn)) {
						map.put("code", "-1");
						map.put("msg", "设备序列号 [" + dsn + "] 无对应设备数据!");
						return map;
					}
					
					if (i == 0) {
						dsns.append(dsn);
					} else {
						dsns.append("," + dsn);
					}
					
					MifiOrderDetail mifiOrderDetail = new MifiOrderDetail();
					mifiOrderDetail.setOrderDetailId(IdGen.uuid());
					mifiOrderDetail.setOrderId(orderId);
					mifiOrderDetail.setOutOrderId(orderId);
					mifiOrderDetail.setDsn(dsn);
					mifiOrderDetail.setSsid(getSsidByDsn(dsn));
					mifiOrderDetail.setDeliveryTime(nowDate);
					mifiOrderDetails.add(mifiOrderDetail);
				}
				StringBuffer buffer = new StringBuffer();
				buffer.append("update mifilist set UEALLOWEDMCC = '" + allowedMcc + "' ");
				// 当前时间与订单行程开始时间为同一天需要设置 UEALLOWED 为 1
				if (DateUtils.orderIsBegin(startDate)) {
					buffer.append(",UEALLOWED = " + Constants.ueAllowed_y);
					mifiManageDao.updateMifiVersionsLimitSpeedFlag(limitSpeedFlag, dsns.toString());
				}
				buffer.append(" where IMEI_6200 IN(" + dsns.toString() + ")");
				_mifiOrder.setCustomerId(customer.getId());
				mifiOrderDao.save(_mifiOrder);
				mifiOrderDetailDao.save(mifiOrderDetails);
				mifiOrderDao.updateBySql(buffer.toString(), null);
			} else {
				// 保存订单
				map = saveOrder(channel, countryMccesMap, imeiArr, limitSpeedFlag, ssidMap, startDate, endDate, null, remarks, customer.getId(), null);
				if (!"1".equals(map.get("code")))
					return map;
				// 修改设备可用状态
				for (String imei : imeiArr) {
					// 当前时间与订单行程开始时间为同一天需要设置 UEALLOWED 为 1
					if (DateUtils.orderIsBegin(startDate)) {
						mifiManageDao.updateMifilistUeAllowed(Constants.ueAllowed_y, imei);
						mifiManageDao.updateMifiVersionLimitSpeedFlag(limitSpeedFlag, imei);
					}
					mifiManageDao.updateMifilistUeAllowedMcc(allowedMcc, imei);
				}
			}

			// Vodafone卡升级
			if (DateUtils.orderIsBegin(startDate)) {
				updateVodafone(allowedMcc, imeiArr, Constants.VODAFONE_CARD_LEVEL_5);
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
			map.put("code", "-1");
			map.put("msg", "下单失败，请与客服联系！");
			return map;
		}

		map.put("code", "1");
		map.put("msg", "下单成功！");
		return map;
	}

	/**
	 * 
	 * @Description 订单延期
	 * @param paramMap
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月12日 下午2:38:06
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized Map<String, String> saveAndDelayOrder(Map<String, Object> paramMap) {
		
		Map<String, String> map = new HashMap<String, String>();
		User user = UserUtils.getUser();
		String sourceType = user.getChannelNameEn();
		
		// 参数
		String orderId = ObjectUtils.toString(paramMap.get("orderId"));
		String endDateStr = ObjectUtils.toString(paramMap.get("endDate"));
		
		// 参数判断
		if (StringUtils.isBlank(orderId)) {
			map.put("code", "-1");
			map.put("msg", "请选择要延期的订单！");
			return map;
		}
		// 订单对象
		MifiOrder mifiOrder = mifiOrderDao.get(orderId);	
		if (mifiOrder == null) {
			map.put("code", "-1");
			map.put("msg", "订单未找到");
			return map;
		}
		if (StringUtils.isBlank(endDateStr)) {
			map.put("code", "-1");
			map.put("msg", "请输入延期时间！");
			return map;
		}
		// 订单延期时间格式转换
		Date endDate = null;
		try {
			endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr);
		} catch (ParseException e1) {
			e1.printStackTrace();
			map.put("code", "-1");
			map.put("msg", "延期时间格式错误");
			return map;
		}
		// 订单延期时间不能在订单原结束时间之前
		if (endDate.getTime() <= mifiOrder.getEndDate().getTime()) { 
			map.put("code", "-1");
			map.put("msg", "订单延期时间不能在订单原结束时间之前！");
			return map;
		}
		// 订单所属判断
		if (!sourceType.equals(mifiOrder.getSourceType())) {
			map.put("code", "-1");
			map.put("msg", "无操作权限，请与客服联系！");
			return map;
		}

		// 订单状态判断
		if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_3)) {
			map.put("code", "-1");
			map.put("msg", "当前订单已终止,无法延期!");
			return map;
		} 
		if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_8)) {
			map.put("code", "-1");
			map.put("msg", "订单已完成，无法延期！");
			return map;
		}
		if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_9)) {
			map.put("code", "-1");
			map.put("msg", "订单已取消，无法延期！");
			return map;
		}
		if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_11)) {
			map.put("code", "-1");
			map.put("msg", "订单已删除，无法延期！");
			return map;
		}

		// 当前时间大于行程结束时间则需要更新mifilist数据
		if (new Date().getTime()  > mifiOrder.getEndDate().getTime()) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("update mifilist t set UEALLOWED = " + Constants.ueAllowed_y + ", UEALLOWEDMCC = '" + mifiOrder.getAllowedMcc() + "'");
			buffer.append(" where IMEI_6200 in (select dsn from mifi_order_detail where order_id = '" + mifiOrder.getOrderId() + "')");
			mifiOrderDao.updateBySql(buffer.toString(), null);
		}
		String delayRemark = (StringUtils.isBlank(mifiOrder.getDelayRemark()) ? "" : mifiOrder.getDelayRemark() + ",") + DateUtils.formatDate(mifiOrder.getEndDate(), null) + "延期至" + DateUtils.formatDate(endDate, null);
		
		// 订单延期修改订单参考总价(延期金额)
		String totalPrice = setTotalPrice(mifiOrder, endDate);
		// 预付费渠道商延期订单扣费
		if (!sourceType.equals(Constants.CHANNEL_DEFAULT_VALUE)) {
			DetachedCriteria cDc = channelDao.createDetachedCriteria();
			cDc.add(Restrictions.eq("channelNameEn", sourceType));
			cDc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
			List<Channel> cList = channelDao.find(cDc);
			if (cList!=null && cList.size()>0) {
				Channel channel = cList.get(0);
				if  ("0".equals(channel.getPayType())) {		// 判断是否为预付费渠道商
					Double totalMoney = Double.valueOf(totalPrice);
					Double balance = channel.getBalance();		// 预付费运营商余额
					if (totalMoney > balance) { // 余额不足，请联系运营商
						map.put("code", "-2");
						map.put("msg", "[" + channel.getChannelName() + "] 渠道余额不足，可用余额["+balance+"]，请充值！");
						return map;
					}
					// 修改运营商余额，保存消费记录
					channel.setBalance(balance - totalMoney);
					channelDao.getSession().saveOrUpdate(channel);
				}
			}
		}
		mifiOrder.setEndDate(endDate);
		mifiOrder.setDelayRemark(delayRemark);
		mifiOrder.setDelayTime(new Date());
		mifiOrderDao.save(mifiOrder);

		map.put("code", "1");
		map.put("msg", "订单延期成功！");
		return map;
	}
	
	/**
	 * 
	 * @Description 取消订单
	 * @param paramMap
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月12日 下午2:39:00
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized Map<String, String> saveAndCancelOrder(Map<String, Object> paramMap) {
		
		Map<String, String> map = new HashMap<String, String>();
		User user = UserUtils.getUser();
		String sourceType = user.getChannelNameEn();
		
		// 参数
		String orderId = ObjectUtils.toString(paramMap.get("orderId"));
		
		// 参数判断
		if (StringUtils.isBlank(orderId)) {
			map.put("code", "-1");
			map.put("msg", "请选择要取消的订单！");
			return map;
		}
		// 订单对象
		MifiOrder mifiOrder = mifiOrderDao.get(orderId);	
		if (mifiOrder == null) {
			map.put("code", "-1");
			map.put("msg", "订单未找到");
			return map;
		}
		// 订单所属判断
		if (!sourceType.equals(mifiOrder.getSourceType())) {
			map.put("code", "-1");
			map.put("msg", "无操作权限，请与客服联系！");
			return map;
		}
		
		// 订单状态判断
		if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_3)) {
			map.put("code", "-1");
			map.put("msg", "当前订单已终止,无法取消!");
			return map;
		} 
		if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_8)) {
			map.put("code", "-1");
			map.put("msg", "订单已完成，无法取消！");
			return map;
		}
		if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_9)) {
			map.put("code", "-1");
			map.put("msg", "订单已取消，无法取消！");
			return map;
		}
		if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_11)) {
			map.put("code", "-1");
			map.put("msg", "订单已删除，无法取消！");
			return map;
		}
		
		//非游友移动订单 不允许在订单行程开始后进行取消
		if (!Constants.CHANNEL_DEFAULT_VALUE.equals(sourceType)) {
			if (Long.parseLong(DateUtils.formatDate(new Date(), "yyyy-MM-dd").replaceAll("-", "")) >= Long.parseLong(DateUtils.formatDate(mifiOrder.getStartDate(), "yyyy-MM-dd").replaceAll("-", ""))) {
				map.put("code", "-1");
				map.put("msg", "当前订单行程已开始，无法取消！");
				return map;
			}
		}
		
		// 当前订单行程已发货并已开始取消,需重置设备属性
		Date now = new Date();
		if (mifiOrder.getOrderStatus().equals(MifiOrder.order_status_1) && 
				(now.after(mifiOrder.getStartDate()) && now.before(mifiOrder.getEndDate()))) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("update mifilist t set UEALLOWED = " + Constants.ueAllowed_n + ", UEALLOWEDMCC = null ");
			// 如果是流量订单，则修改mifilist的order_type,flow,start_time,end_time
			if (mifiOrder.getOrderType().equals(MifiOrder.ORDER_TYPE_FLOW)) {
				buffer.append(",order_type='0', flow=0, flow_used=0, start_time=null, end_time=null");
			}
			buffer.append(" where IMEI_6200 in (select dsn from mifi_order_detail where order_id = '" + mifiOrder.getOrderId() + "')");
			mifiOrderDao.updateBySql(buffer.toString(), null);
			
			// 修改mifiversion的speedlimit_type='0'
			mifiOrderDao.updateBySql("update mifiversion a set a.speedlimit_type='0' where IMEI_6200 in (select dsn from mifi_order_detail where order_id = '" + mifiOrder.getOrderId() + "')", null);

			// Vodafone卡降级
			String imeis = mifiOrderDetailDao.getDsnByOrderId(mifiOrder.getOrderId());
			if (StringUtils.isNotBlank(imeis))
				updateVodafone(mifiOrder.getAllowedMcc(), imeis.split(","), Constants.VODAFONE_CARD_LEVEL_3);
		}
		mifiOrder.setCancelTime(new Date());
		mifiOrder.setOrderStatus(MifiOrder.order_status_9);
		mifiOrderDao.save(mifiOrder);

		// 预付费渠道商取消订单退款到帐户
		if (!sourceType.equals(Constants.CHANNEL_DEFAULT_VALUE)) {
			DetachedCriteria cDc = channelDao.createDetachedCriteria();
			cDc.add(Restrictions.eq("channelNameEn", sourceType));
			cDc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
			List<Channel> cList = channelDao.find(cDc);
			if (cList!=null && cList.size()>0) {
				Channel channel = cList.get(0);
				if  ("0".equals(channel.getPayType())) {		// 判断是否为预付费渠道商
					Double totalMoney = Double.valueOf(mifiOrder.getReferenceTotalPrice());
					channel.setBalance(channel.getBalance() + totalMoney);
					channelDao.getSession().saveOrUpdate(channel);
				}
			}
		}

		map.put("code", "1");
		map.put("msg", "订单取消成功！");
		return map;
	}

	/**
	 * 
	 * @Description 订单完成
	 * @param paramMap
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月12日 下午2:39:45
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized Map<String, String> saveAndFinishOrder(Map<String, Object> paramMap) {
		
		Map<String, String> map = new HashMap<String, String>();
		User user = UserUtils.getUser();
		String sourceType = user.getChannelNameEn();
		
		// 参数
		String orderId = ObjectUtils.toString(paramMap.get("orderId"));
		
		// 参数判断
		if (StringUtils.isBlank(orderId)) {
			map.put("code", "-1");
			map.put("msg", "请选择要取消的订单！");
			return map;
		}
		// 订单对象
		MifiOrder mifiOrder = mifiOrderDao.get(orderId);	
		if (mifiOrder == null) {
			map.put("code", "-1");
			map.put("msg", "订单未找到");
			return map;
		}
		// 订单所属判断
		if (!sourceType.equals(mifiOrder.getSourceType())) {
			map.put("code", "-1");
			map.put("msg", "无操作权限，请与客服联系！");
			return map;
		}
		// 订单状态判断
		if (!mifiOrder.getOrderStatus().equals(MifiOrder.order_status_1)) {
			map.put("code", "-1");
			map.put("msg", "当前订单非[已发货]状态，无法完成订单！");
			return map;
		}

		// 订单已开始
		Date now = new Date();
		if (now.after(mifiOrder.getStartDate()) && now.before(mifiOrder.getEndDate())) {
			// 订单结束强制更新设备控制信息, 未完成的订单对应的设备无法再次发货
			StringBuffer buffer = new StringBuffer();
			buffer.append("update mifilist t set UEALLOWED = " + Constants.ueAllowed_n + ", UEALLOWEDMCC = 'homeForbidden' ");
			// 如果是流量订单，则修改mifilist的order_type,flow,start_time,end_time
			if (mifiOrder.getOrderType().equals(MifiOrder.ORDER_TYPE_FLOW)) {
				buffer.append(",order_type='0', flow=0, flow_used=0, start_time=null, end_time=null");
			}
			buffer.append(" where IMEI_6200 in (select dsn from mifi_order_detail where order_id = '" + mifiOrder.getOrderId() + "')");
			mifiOrderDao.updateBySql(buffer.toString(), null);
			
			// 修改mifiversion的speedlimit_type='0'
			mifiOrderDao.updateBySql("update mifiversion a set a.speedlimit_type='0' where IMEI_6200 in (select dsn from mifi_order_detail where order_id = '" + mifiOrder.getOrderId() + "')", null);
			
			// Vodafone卡降级
			String imeis = mifiOrderDetailDao.getDsnByOrderId(mifiOrder.getOrderId());
			if (StringUtils.isNotBlank(imeis))
				updateVodafone(mifiOrder.getAllowedMcc(), imeis.split(","), Constants.VODAFONE_CARD_LEVEL_3);
		}
		
		// 删除订单中设备已绑定卡的记录
		String sql = "delete from mifitest where imei in (select dsn from mifi_order_detail where order_id='" + mifiOrder.getOrderId() + "')";
		mifitestDao.updateBySql(sql, null);
		
		// 修改订单状态及完成时间
		mifiOrder.setFinishTime(new Date());
		mifiOrder.setOrderStatus(MifiOrder.order_status_8);
		mifiOrderDao.save(mifiOrder);

		map.put("code", "1");
		map.put("msg", "设置订单完成成功！");
		return map;
	}

	/**
	 * 
	 * @Description 订单删除
	 * @param paramMap
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月12日 下午2:39:45
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized Map<String, String> deleteOrder(Map<String, Object> paramMap) {
		
		Map<String, String> map = new HashMap<String, String>();
		User user = UserUtils.getUser();
		String sourceType = user.getChannelNameEn();
		
		// 参数
		String orderId = ObjectUtils.toString(paramMap.get("orderId"));
		
		// 参数判断
		if (StringUtils.isBlank(orderId)) {
			map.put("code", "-1");
			map.put("msg", "请选择要删除的订单！");
			return map;
		}
		// 订单对象
		MifiOrder mifiOrder = mifiOrderDao.get(orderId);	
		if (mifiOrder == null) {
			map.put("code", "-1");
			map.put("msg", "订单未找到");
			return map;
		}
		// 订单所属判断
		if (!sourceType.equals(mifiOrder.getSourceType())) {
			map.put("code", "-1");
			map.put("msg", "无操作权限，请与客服联系！");
			return map;
		}
		// 订单状态判断
		if (!mifiOrder.getOrderStatus().equals(MifiOrder.order_status_9)) {
			map.put("code", "-1");
			map.put("msg", "当前订单非[已取消]状态，无法删除订单！");
			return map;
		}
		// 删除订单同时删除订单详情
		mifiOrder.setDeleteTime(new Date());
		mifiOrder.setOrderStatus(MifiOrder.order_status_11);
		mifiOrderDao.save(mifiOrder);

		map.put("code", "1");
		map.put("msg", "订单删除成功！");
		map.put("outOrderId", mifiOrder.getOutOrderId());
		return map;
	}
	
	// =================== 私有方法 ================= // 
	/**
	 * 
	 * @Description 渠道下单系统限制条件判断
	 * @param ssidMap
	 * @param countryMccesMap
	 * @param imeiArr
	 * @param startDate
	 * @param sourceType
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月5日 上午10:45:45
	 */
	private Map<String, String> validate(Map<String, String> ssidMap, Map<String, String> countryMccesMap, 
			String[] imeiArr, Date startDate, String sourceType) {
		
		Map<String, String> map = new HashMap<String, String>();

		// 判断所选设备是否属于当前渠道商
		HashMap<String, String> mifiMap = null;
		String ownerMcces = "";
		for (String imei : imeiArr) {
			
			mifiMap = mifiManageDao.getMifilistBySn(imei);
			
			if (mifiMap == null) {
				map.put("code", "-1");
				map.put("msg", "设备 [" + imei + "] 未入库");
				return map;
			}
			if (!sourceType.equals(mifiMap.get("sourceType"))) {
				map.put("code", "-1");
				map.put("msg", "设备 [" + imei + "] 不属于当前渠道商");
				return map;
			}
			if ("0".equals(mifiMap.get("useFlag"))) {
				map.put("code", "-1");
				map.put("msg", "设备 [" + imei + "] 不可使用");
				return map;
			}
			ssidMap.put(imei, mifiMap.get("ssid"));
			ownerMcces += "," + mifiMap.get("ownerMcc");
			
			mifiMap = null;
		}
		
		// 系统限制条件判断
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		String orderNoCheckDaysStr = DictUtils.getDictValue(Constants.DICT_ORDER_NOCHECK_DAYS_LABEL, Constants.DICT_ORDER_NOCHECK_DAYS_TYPE, "3");
		int orderNoCheckDays = Integer.valueOf(orderNoCheckDaysStr);
		
		calendar.add(Calendar.DAY_OF_YEAR, orderNoCheckDays);
		Date tempDate = calendar.getTime();
		if (startDate.before(tempDate)) {
			String mcces = null;
			String[] mccArr = null;
			HashMap<String, String> mccMap = null;
			for (Map.Entry<String, String> entry : countryMccesMap.entrySet()) {
				
				mcces = entry.getValue();
				mccArr = mcces.split(",");
				
				// 剩余流量控制（默认500M）
				String usableDataStr = DictUtils.getDictValue(Constants.DICT_USABLE_DATA_LABEL, Constants.DICT_USABLE_DATA_TYPE, "524288000");
				long usableData = Long.valueOf(usableDataStr);

				// 可以下订单的sim卡张数
				String canOrderSimNumStr = DictUtils.getDictValue(Constants.DICT_CANORDER_SIMNUM_LABEL, Constants.DICT_CANORDER_SIMNUM_TYPE, "10");
				int canOrderSimNum = Integer.valueOf(canOrderSimNumStr);
				
				// 可用sim卡数量
				int simnodeCount = getSimnodeCount(mccArr[0], ownerMcces, usableData);
				// 如果系统配置可下单卡数量等于0就不做卡数量判断
				if (canOrderSimNum > 0) {
					if (simnodeCount==0 || (simnodeCount-imeiArr.length)<canOrderSimNum) {
						mccMap = mifiOrderDao.findMccByCountryCode(entry.getKey());
						String msg = "[" + mccMap.get("countryName") + "] 可以使用的SIM卡数量不足";
						
						// 给卡监控人员发手机短信和邮件
						SendMessageThread thread = new SendMessageThread(msg + "，请及时处理！");
						thread.start();
						
						map.put("code", "-1");
						map.put("msg", msg + "，请与客服联系！");
						mccMap = null;
						return map;
					}
				}
				
				mcces = null;
				mccArr = null;
			}
		}
		
		map.put("code", "1");
		return map;
	}
	
	// 可用卡数量不足信息发送线程
	class SendMessageThread extends Thread {  
		
		private String content;
		
		public SendMessageThread(String content) {
			super();
			this.content = content;
		}

		public void run() {
			// 卡监控信息接收
			NoticeReceiveCondition condition = new NoticeReceiveCondition();
			condition.setEqType("CARD_MONITOR");
			List<NoticeReceive> list = noticeReceiveService.findListByCondition(condition);
			if (list != null && list.size() > 0) {
				NoticeReceive nr = list.get(0);
				// 手机号码
				String mobile = nr.getPhones();
				if (StringUtils.isNotBlank(mobile)) {
					// 获取短信内容
					String url = "http://139.129.128.71:8086/msgHttp/json/mt";
					String account = "you106";
					String password = "youyoumob123";
					String sendResult = SmsClientSend.sendSms(url, account, password, mobile, content);
					System.out.println("account sendResult  " + sendResult);
					// 获取respcode的值 成功则返回0
					JSONObject json = JSONObject.fromObject(sendResult);
					JSONArray j = json.getJSONArray("Rets");
					if (j==null || j.size()==0) {
						logger.info("可用卡数量不足-信息发送失败");
					}
					logger.info("可用卡数量不足-发送短信手机:{},短信内容：{},短信结果：{}", mobile, content, sendResult);

				}

				// 邮件发送
				String emails = nr.getEmails();
				if (StringUtils.isNotBlank(emails)) {
					MailThread mThread = new MailThread("可用卡数量不足", "[游友移动]", content, emails);
					mThread.start();
				}
				
			}
		}  
	}  

	// 取可使用的卡数据
	private int getSimnodeCount(String mcc, String ownerMcces, long usableData) {
		
		int simnodeCount = 0;
		
		// 取受限制的国家mcc
		String mcces = DictUtils.getDictValue(Constants.DICT_CARD_USE_PEOPLE_TYPE_LABEL, Constants.DICT_CARD_USE_PEOPLE_TYPE_TYPE, "");
		if (StringUtils.isBlank(mcces)) { // 是否有限制国家
			simnodeCount = mifiOrderDao.findUsableSimnodeCount(mcc, usableData);
		} else {
			if (!include(mcces, mcc)) {	// 订单前往国家不在限制国家内
				simnodeCount = mifiOrderDao.findUsableSimnodeCount(mcc, usableData);
			} else {
				if (include(ownerMcces, mcc)) { // 订单前往国家在设备所属国家内
					simnodeCount = mifiOrderDao.findUsableSimnodeCountTwo(mcc, usableData, "ALL");
				} else {
					simnodeCount = mifiOrderDao.findUsableSimnodeCountTwo(mcc, usableData, "ABROAD_TO_HOME");
				}
			}
		}
		
		
		return simnodeCount;
		
	}
	
	// mcc包含判断
	private boolean include(String ownerMcces, String mcc) {
		
		if (StringUtils.isBlank(ownerMcces))
			return false;
		
		String[] mccArr = ownerMcces.split(",");
		for (String str : mccArr) {
			if (str.equals(mcc)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * @Description 保存订单（预付费渠道商拆单，一个区域一个订单）
	 * @param channel
	 * @param countryMccesMap
	 * @param imeiArr
	 * @param limitSpeedFlag
	 * @param ssidMap
	 * @param startDate
	 * @param endDate
	 * @param outOrderId
	 * @param remarks
	 * @param customerId
	 * @param flow
	 * @throws ParseException 
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月5日 上午10:43:09
	 */
	private Map<String, String> saveOrder(Channel channel, Map<String, String> countryMccesMap, String[] imeiArr, String limitSpeedFlag,
			Map<String, String> ssidMap, Date startDate, Date endDate, String outOrderId, String remarks, String customerId, Integer flow) throws ParseException {
		
		Map<String, String> map = new HashMap<String, String>();
		
		// 临时订单、订单详情、总共消费金额
		List<MifiOrder> orderList = new ArrayList<MifiOrder>();
		List<MifiOrderDetail> detailList = new ArrayList<MifiOrderDetail>();
		double totalMoney = 0.00;
		
		// 设备数量
		int num = imeiArr.length;
		
		// 根据国家编号取区域集合
		Set<Region> regionSet = new HashSet<Region>();// 区域集合
		Map<String, String> regionMccsMap = new HashMap<String, String>();// 区域对应国家集合
		map = getRegionSet(countryMccesMap, regionSet, regionMccsMap);
		if (!"1".equals(map.get("code")))
			return map;
		
		// 生成订单
		String allowedMcc = null;
		MifiOrder order = null;
		MifiOrderDetail detail = null;
		for (Region region : regionSet) {
			
			allowedMcc = regionMccsMap.get(region.getId());
			
			// 订单
			order = new MifiOrder();
			order.setOrderId(IdGen.uuid());
			order.setOutOrderId(StringUtils.isBlank(outOrderId) ? order.getOrderId() : outOrderId);
			order.setAllowedMcc(allowedMcc);
			order.setOrderStatus(MifiOrder.order_status_1);
			order.setSourceType(channel.getChannelNameEn());
			order.setEquipmentCnt(String.valueOf(num));
			order.setStartDate(startDate);
			order.setEndDate(endDate);
			order.setStockStatus(MifiOrder.stock_status_0);
			order.setLimitSpeedFlag(limitSpeedFlag);
			order.setDeliveryTime(new Date());
			order.setOutOrderTime(new Date());
			order.setDelayRemark(remarks);
			order.setCustomerId(customerId);
			// 判断是否为流量订单
			if (flow!=null) {
				order.setOrderType("1");
				order.setFlow(flow);
			}
			map = setDaysAndPriceOne(order, region);
			if (!"1".equals(map.get("code"))) {
				return map;
			} else {
				totalMoney += Double.valueOf(map.get("totalPrice"));
			}
			orderList.add(order);

			// 订单详情
			for (String imei : imeiArr) {
				detail = new MifiOrderDetail();
				
				detail.setOrderDetailId(IdGen.uuid());
				detail.setOrderId(order.getOrderId());
				detail.setOutOrderId(order.getOutOrderId());
				detail.setDsn(imei);
				detail.setDeliveryTime(order.getDeliveryTime());
				detail.setSsid(ssidMap.get(imei));
				detailList.add(detail);

				detail = null;
			}

			allowedMcc = null;
			order = null;
		}

		// 渠道商余额判断
		String payType = channel.getPayType();				// 付费类型，0_预付费 1_后付费| 运营商类型（预付费运营商需要判断余额，后付费运营商直接扣费）
		if ("0".equals(payType)) {
			Double balance = channel.getBalance();		// 预付费运营商余额
			if (totalMoney > balance) { // 余额不足，请联系运营商
				map.put("code", "-2");
				map.put("msg", "[" + channel.getChannelName() + "] 渠道余额不足，可用余额["+balance+"]，请充值！");
				return map;
			}
			// 修改运营商余额，保存消费记录
			channel.setBalance(balance - totalMoney);
			channelDao.getSession().saveOrUpdate(channel);
		}
		
		// 保存订单
		mifiOrderDao.save(orderList);
		
		// 保存订单详情
		mifiOrderDetailDao.save(detailList);

		map.put("code", "1");
		return map;
	}
	
	/**
	 * 
	 * @Description 根据国家编号取区域集合及区域对应国家集合
	 * @param countryMccesMap
	 * @param regionSet
	 * @param regionMccsMap 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年5月4日 上午10:18:15
	 */
	private Map<String, String> getRegionSet(Map<String, String> countryMccesMap, Set<Region> regionSet, Map<String, String> regionMccsMap) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		Region region = null;
		String mcces = null;
		for (Map.Entry<String, String> entry : countryMccesMap.entrySet()) {

			region = regionDao.findByCountryCode(entry.getKey());
			
			if (region != null) {
				regionSet.add(region);
				// 国家编号对应MCC
				mcces = regionMccsMap.get(region.getId());
				if (StringUtils.isBlank(mcces))
					mcces = entry.getValue();
				else
					mcces += "," + entry.getValue();
				regionMccsMap.put(region.getId(), mcces);
				
				mcces = null;
			} else {
				map.put("code", "-1");
				map.put("msg", "国家 ["+entry.getKey()+"] 未找到对应区域，请与客服联系！");
				return map;
			}
			
			region = null;
			
		}

		map.put("code", "1");
		return map;
	}
	
	/**
	 * 
	 * @Description 根据国家编号取国家编号对应MCC集合
	 * @param countryCodeArr
	 * @param countryMccesMap
	 * @param allowedMcc
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月5日 上午10:16:37
	 */
	private Map<String, String> getCountryMccesMap(String[] countryCodeArr, Map<String, String> countryMccesMap) {
		
		Map<String, String> map = new HashMap<String, String>();

		HashMap<String, String> mccesMap = null;
		String mcces = null;
		String allowedMcc = null;
		for (String countryCode : countryCodeArr) {
			
			mccesMap = mifiOrderDao.findMccByCountryCode(countryCode);
			
			if (mccesMap != null)
				mcces = mccesMap.get("mcces");
			if (StringUtils.isBlank(mcces)) {
				map.put("code", "-1");
				map.put("msg", "国家 ["+countryCode+"] 与MCC配置错误，请与客服联系！");
				return map;
			}
			
			countryMccesMap.put(countryCode, mcces);
			

			if (StringUtils.isBlank(allowedMcc))
				allowedMcc = mcces;
			else
				allowedMcc += "," + mcces;
			
			mcces = null;
			
		}

		map.put("code", "1");
		map.put("allowedMcc", allowedMcc);
		return map;
		
	}
	
	/**
	 * 
	 * @Description 根据mcc取国家编号对应MCC集合
	 * @param mcces
	 * @param countryMccesMap
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月5日 上午10:27:17
	 */
	private Map<String, String> getCountryMccesMap(String mcces, Map<String, String> countryMccesMap) {
		
		Map<String, String> map = new HashMap<String, String>();
		String[] mccArr = mcces.split(",");
		
		Map<String, String> countryMap = null;
		String countryCode = null;
		String tempMcc = null;
		for (String mcc : mccArr) {
			
			countryMap = mifiOrderDao.findCountryByMcc(mcc);
			
			if (countryMap != null)
				countryCode = countryMap.get("countryCode");
			if (StringUtils.isBlank(countryCode)) {
				map.put("code", "-1");
				map.put("msg", "MCC ["+mcc+"] 未找到对应国家，请与客服联系！");
				return map;
			}
			
			tempMcc = countryMccesMap.get(countryCode);
			if (StringUtils.isBlank(tempMcc))
				tempMcc = mcc;
			else
				tempMcc += "," + mcc;
			countryMccesMap.put(countryCode, tempMcc);
			
			tempMcc = null;
			countryCode = null;
			countryMap = null;
			
		}
		
		map.put("code", "1");
		return map;
		
	}
	
	/**
	 * 
	 * @Description 将JSONArray类型的设备序列号转成String[]
	 * @param dsns
	 * @return String[]  
	 * @author yifang.huang
	 * @date 2016年5月5日 下午4:32:08
	 */
	private String[] getDsnStringArray(JSONArray dsns) {
		
		String[] dsnArr = new String[dsns.size()];
		
		for (int i=0; i<dsns.size(); i++) {
			dsnArr[i] = dsns.getString(i);
		}
		
		return dsnArr;
		
	}
	
	/**
	 * 
	 * @Description 是否需要拆单
	 * @param sourceType
	 * @param map
	 * @return boolean  
	 * @author yifang.huang
	 * @date 2016年5月9日 下午4:11:37
	 */
	private boolean orderNeedSplit(String sourceType, Map<String, String> map) {

		// 渠道商
		List<Channel> cList = channelDao.findList(sourceType);
		if (cList==null || cList.size()==0) {
			map.put("code", "-1");
			map.put("msg", "[" + sourceType + "] 渠道未找到，请与客服联系！");
			return false;
		}
		Channel channel = cList.get(0);
		
		// YOUYOUMOB 渠道不需要拆单
		if (Constants.CHANNEL_DEFAULT_VALUE.equals(sourceType))
			return false;

		// 预付费渠道商需要拆单
		String payType = channel.getPayType();				// 付费类型，0_预付费 1_后付费
		if ("0".equals(payType))
			return true;
		
		return false;

	}
	
	/**
	 * 
	 * @Description 计算两日期之间的天数（超过一个小时算一天）
	 * @param startDate
	 * @param endDate
	 * @return int  
	 * @author yifang.huang
	 * @date 2016年5月13日 下午5:39:49
	 */
	private int getDays(Date startDate, Date endDate) {

		int hours = Integer.valueOf(((endDate.getTime() - startDate.getTime()) / 3600000) + "");
		
		if (hours <= 0)
			return 0;
		
		int days = hours / 24;
		if ((hours%24) > 0)
			days += 1;
		
		return days;
	}
	
	/**
	 * 
	 * @Description 判断渠道是否可以下allowedMcc对应国家的订单
	 * @param countryMccesMap 根据参数mccs组装的<国家编号, 国家mcc>
	 * @param sourceType
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年10月9日 下午3:23:15
	 */
	private Map<String, String> allowedMccCheck(Map<String, String> countryMccesMap, String sourceType) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		// 渠道对应的产品列表
		List<Price> priceList = priceDao.findChannelPriceList(sourceType);
		if (priceList!=null && priceList.size()>0) {
			String countryCodes = "";
			for (Price price : priceList) {
				Region region = price.getRegion();
				countryCodes = countryCodes + "," + region.getCountryCodes();
			}
			
			String[] countryCodeArr = countryCodes.substring(1).split(",");				// 渠道可以下订单的所有国家编号数组
			List<String> countryCodeList = Arrays.asList(countryCodeArr);
			Set<String> keySet = countryMccesMap.keySet();								// 根据参数mcc组装的国家编号集合
			for (String key : keySet) {
				if (!countryCodeList.contains(key)) {
					map.put("code", "-1");
					map.put("msg", "渠道[" + sourceType + "]不能下[" + key + "]国家的订单");
					return map; 
				}
			}
		}

		map.put("code", "1");
		return map;
	}
	
	/**
	 * 
	 * @Description 设备版本检测
	 * @param imeiArr	设备编号数组
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年10月31日 下午5:38:04
	 */
	private Map<String, String> mifiVersionCheck(String[] imeiArr) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		// 设备基础版本
		String baseVersionStr = DictUtils.getDictValue(Constants.DICT_MIFI_BASE_VERSION_LABEL, Constants.DICT_MIFI_BASE_VERSION_TYPE, "");
		Double baseVersion = Double.valueOf(baseVersionStr);
		
		// 设备最新版本
		Double highestVersion = mifiVersionDao.getHighestVersion();
		
		// 遍历发货设备
		String warningMsg = "";
		for (String imei : imeiArr) {
			Double imeiVersion = mifiVersionDao.getMifiVersion(imei);
			if (imeiVersion < baseVersion) {
				map.put("code", "-1");
				map.put("msg", "设备[" + imei + "]当前版本是V" + imeiVersion + ",低于V" + baseVersionStr + "不能发货,下单失败!");
				return map;
			}
			if (imeiVersion>=baseVersion && imeiVersion<highestVersion) {
				warningMsg += "设备[" + imei + "]当前版本是V" + imeiVersion + ",低于最新V" + highestVersion + "请升级;";
			}
		}
		
		map.put("code", "1");
		map.put("msg", warningMsg);
		return map;
	}
	
	/**
	 * 
	 * @Description 取渠道所有运营国家的MCC
	 * @param sourceType
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年11月1日 下午2:21:44
	 */
	private String findAllMcces(String sourceType) {
		// 取价格列表
		List<Price> priceList = priceDao.findChannelPriceList(sourceType);
		
		if (priceList!=null && priceList.size()>0) {
			String mcces = "";
			for (Price price : priceList) {
				Region region = price.getRegion();
				if (StringUtils.isBlank(mcces))
					mcces = region.getMcces();
				else
					mcces += "," + region.getMcces();
			}
			return mcces;
		}
		
		return null;
	}
	
	/****************************** 渠道商PC端下订单 结束 ****************************/
	
	/**
	 * 根据设备sn查找设备订单
	 * @athor shuxin
	 * @date 2016年7月15日上午9:53:11
	 * @param dsn
	 * @return
	 * List<Map<String,Object>> 
	 */
	public List<Map<String, Object>> findMifiOrderListByDsn(String dsn) {
		StringBuffer buffer = new StringBuffer("select * from mifi_order t where 1 = 1");
		String sql = processDsn(dsn);
		if (StringUtils.isNotBlank(sql))
				buffer.append(sql);
		buffer.append("order by t.out_order_time desc");
		buffer.append(" limit 0,3");
		List<Map<String, Object>> list = mifiOrderDao.findBySql(buffer.toString(), null, Map.class);
		for (int i = 0; i < list.size(); i++) {
			list.get(i).putAll(mifiOrderDao.getMccs((String) list.get(i).get("allowed_mcc")));
			String _orderStatus = (String) list.get(i).get("order_status");
			if (!_orderStatus.equals(MifiOrder.order_status_0)) {
				list.get(i).put("dsn", mifiOrderDetailDao.getDsnByOrderId((String) list.get(i).get("order_id")));
				list.get(i).put("ssid", mifiOrderDetailDao.getSsidByOrderId((String) list.get(i).get("order_id")));
			}
		}
		return list;
	}
	
	/**
	 * 获取mifi设备基站信息
	 * @athor shuxin
	 * @date 2016年7月19日下午1:45:25
	 * @param param
	 * @return
	 * JSONObject 
	 */
	public JSONObject getMDeviceBaseStationInfo(JSONObject param) {
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		String errorMessage = "";
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(df.format(new Date()) + "[入参]：" + param.toString());
		
		//渠道商编号
		if (!param.containsKey("channelId ") || StringUtils.isBlank(param.getString("channelId"))) {
			errorMessage = "|[运营商]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		//设备编号
		if (!param.containsKey("imei") || StringUtils.isBlank(param.getString("imei"))) {
			errorMessage = "|[设备编号]不能为空!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		//设备编号不为空，进行设备是否入库和设备是否属于渠道商验证
		String  imei = param.getString("imei");
		String  channelName = param.getString("channelId");
		HashMap<String, String> mifiMap =  mifiManageDao.getMifilistBySn(imei);
		if (mifiMap == null) {
			errorMessage = "|设备 [" + imei + "] 未入库";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		if (!channelName.equals(mifiMap.get("sourceType"))) {
			errorMessage = "|设备 [" + imei + "] 不属于当前渠道商";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		Map<String, Object> map = mifiTrafficService.findBaseStationInfoByImei(imei);
		JSONObject	json = new JSONObject();
		if(!map.isEmpty()){
			json.put("mcc", map.get("MCC_6200"));
			json.put("mnc", map.get("MNC_6200"));
			json.put("cellid", map.get("CELLID_6200"));
			json.put("tac", map.get("TAC_6200"));
		}
		resObj.put("data", json);
		
		logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
		return resObj;
	}
	
	/**
	 * 
	 * @Description 根据行程日期判断设备是否在其它订单中
	 * @param imeiArr
	 * @param startDate
	 * @param endDate
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年8月23日 下午2:42:39
	 */
	public Map<String, String> checkDeviceByTripDate(String[] imeiArr, String startDate, String endDate) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		List<Map<String, String>> listMap = null;
		for (String imei : imeiArr) {
			listMap = mifiOrderDao.getOrderByDeviceAndTripDate(imei, startDate, endDate);
			if (listMap!=null && listMap.size()>0) {
				map.put("code", "-1");
				map.put("msg", "设备[" + imei + "]在行程日期["+startDate+"~"+endDate+"]之内已存在有效订单!");
				return map;
			}
		}
		
		map.put("code", "1");
		return map;
	}
	
	
	/**
	 * 按MCC统计订单
	 * @athor shuxin
	 * @date 2016年9月2日上午11:13:40
	 * @param paramMap(代理商和下单时间)
	 * @return
	 * List<Map<String,Object>> 
	 */
	public JSONObject statMifiOrderBYMcc(Map<String, Object> paramMap) {
		JSONObject json = new JSONObject();
		List<Region> regionList = regionDao.findAll();
		if (regionList.isEmpty()) { // 没有统计的数据
			json.put("code", -1);
			return json;
		}
		StringBuffer buffer = new StringBuffer();
		for (Region region : regionList) {
			if (region.getCountryCodes().indexOf(",") != -1) {
				String[] tempStr = region.getCountryCodes().split(",");
				for (int i = 0; i < tempStr.length; i++) {
					buffer.append("'").append(tempStr[i]).append("'").append(",");
				}
			}
			buffer.append("'").append(region.getCountryCodes()).append("'").append(",");
		}
		String countryCode = buffer.substring(0, buffer.lastIndexOf(",")).toString();
		// x轴上显示的数据
		List<Map<String, String>> mccList = mifiUsageService.findMccsByCountryCode(countryCode);
		if (mccList.isEmpty()) {// 没有统计的数据
			json.put("code", -1);
			return json;
		}
		List<String> x = new ArrayList<String>();
		List<Integer> orderTotal = new ArrayList<Integer>();
		List<Double> priceTotal = new ArrayList<Double>();
		List<Map<String, Object>> orderList = mifiOrderDao.findValidMifiOrderList(paramMap);
		int mccSize = mccList.size();
		int orderSize = orderList.size();
		int orderNum = 0;
		double orderTotalPrice = 0;
		HashMap<String, Object> totalNum = mifiOrderDao.findValidMifiOrderTotalNum(paramMap);
		HashMap<String, Object> allTotal = mifiOrderDao.findValidMifiOrderTotalPrice(paramMap);
		for (int i = 0; i < mccSize; i++) {
			Map<String, String> mccMap = mccList.get(i);
			String countryNameCn = mccMap.get("country_name_cn");
			x.add(countryNameCn);
			for (int j = 0; j < orderSize; j++) {
				Map<String, Object> orderMap = orderList.get(j);
				String allowedMccCn = ObjectUtils.toString(orderMap.get("allowed_mcc_cn"));
				Object obj = orderMap.get("reference_total_price");
				double totalPrice = Double.parseDouble(ObjectUtils.toString(obj == null ? "0.00" : obj));
				if (allowedMccCn.contains(countryNameCn)) { // 匹配国家
					++orderNum;
					orderTotalPrice += totalPrice;
				}
			}
			BigDecimal b = new BigDecimal(orderTotalPrice);
			orderTotal.add(orderNum);
			priceTotal.add(b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			orderNum = 0;
			orderTotalPrice = 0;
		}
		// 添加所有国家
		json.put("allNum", totalNum.get("totalNum"));
		json.put("allToal", "".equals(ObjectUtils.toString(allTotal.get("totalPrice")))  ? 0 : allTotal.get("totalPrice"));
		json.put("x", x.toArray());
		
		json.put("totalNum", orderTotal.toArray());
		json.put("priceTotal", priceTotal.toArray());
		MifiOrder MifiOrder = null;
		ArrayList<MifiOrder> list = null;
		list = new ArrayList<MifiOrder>();
		for (int i = 0; i < orderTotal.size(); i++) {
			Integer   q =orderTotal.get(i);
			if(q>0){
				
				MifiOrder = new MifiOrder();
				MifiOrder.setAllowedMcc(x.get(i)+"");
				MifiOrder.setEquipmentCnt(q+"");
				MifiOrder.setOutOrderId(priceTotal.get(i)+"");
			}
			if(MifiOrder!=null){
			list.add(MifiOrder);
		}
			}
		return json;
	}
	/**
	 * 导出MCC
	 * @Description 
	 * @param paramMap
	 * @return
	 * @throws ParseException 
	 * @return List<MifiOrder>  
	 * @author wangsai
	 * @date 2017年2月27日 上午10:51:26
	 */
	public List<MifiOrder> statMifiOrderBYMccExport(Map<String, Object> paramMap) throws ParseException {
		JSONObject json = new JSONObject();
		List<Region> regionList = regionDao.findAll();
		if (regionList.isEmpty()) { // 没有统计的数据
			json.put("code", -1);
			return null;
		}
		StringBuffer buffer = new StringBuffer();
		for (Region region : regionList) {
			if (region.getCountryCodes().indexOf(",") != -1) {
				String[] tempStr = region.getCountryCodes().split(",");
				for (int i = 0; i < tempStr.length; i++) {
					buffer.append("'").append(tempStr[i]).append("'").append(",");
				}
			}
			buffer.append("'").append(region.getCountryCodes()).append("'").append(",");
		}
		String countryCode = buffer.substring(0, buffer.lastIndexOf(",")).toString();
		// x轴上显示的数据
		List<Map<String, String>> mccList = mifiUsageService.findMccsByCountryCode(countryCode);
		if (mccList.isEmpty()) {// 没有统计的数据
			json.put("code", -1);
			return null;
		}
		List<String> x = new ArrayList<String>();
		List<Integer> orderTotal = new ArrayList<Integer>();
		List<Double> priceTotal = new ArrayList<Double>();
		List<Map<String, Object>> orderList = mifiOrderDao.findValidMifiOrderList(paramMap);
		int mccSize = mccList.size();
		int orderSize = orderList.size();
		int orderNum = 0;
		double orderTotalPrice = 0;
		HashMap<String, Object> totalNum = mifiOrderDao.findValidMifiOrderTotalNum(paramMap);
		HashMap<String, Object> allTotal = mifiOrderDao.findValidMifiOrderTotalPrice(paramMap);
		for (int i = 0; i < mccSize; i++) {
			Map<String, String> mccMap = mccList.get(i);
			String countryNameCn = mccMap.get("country_name_cn");
			x.add(countryNameCn);
			for (int j = 0; j < orderSize; j++) {
				Map<String, Object> orderMap = orderList.get(j);
				String allowedMccCn = ObjectUtils.toString(orderMap.get("allowed_mcc_cn"));
				Object obj = orderMap.get("reference_total_price");
				double totalPrice = Double.parseDouble(ObjectUtils.toString(obj == null ? "0.00" : obj));
				if (allowedMccCn.contains(countryNameCn)) { // 匹配国家
					++orderNum;
					orderTotalPrice += totalPrice;
				}
			}
			BigDecimal b = new BigDecimal(orderTotalPrice);
			orderTotal.add(orderNum);
			priceTotal.add(b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			orderNum = 0;
			orderTotalPrice = 0;
		}
		// 添加所有国家
		json.put("allNum", totalNum.get("totalNum"));
		json.put("allToal", "".equals(ObjectUtils.toString(allTotal.get("totalPrice")))  ? 0 : allTotal.get("totalPrice"));
		json.put("x", x.toArray());
		
		json.put("totalNum", orderTotal.toArray());
		json.put("priceTotal", priceTotal.toArray());
		MifiOrder MifiOrder = null;
		ArrayList<MifiOrder> list = null;
		String outOrderTimeStart = ObjectUtils.toString(paramMap.get("beginDate"));
		String outOrderTimeEnd = ObjectUtils.toString(paramMap.get("endDate"));
		list = new ArrayList<MifiOrder>();
		for (int i = 0; i < orderTotal.size(); i++) {
			Integer   q =orderTotal.get(i);
			MifiOrder = new MifiOrder();
			if(q>0){
				MifiOrder.setAllowedMcc(x.get(i)+"");
				MifiOrder.setEquipmentCnt(q+"");
				MifiOrder.setOutOrderId(priceTotal.get(i)+"");
				if(StringUtils.isNotBlank(outOrderTimeStart)&&StringUtils.isNotBlank(outOrderTimeEnd)){
				MifiOrder.setStockStatus(outOrderTimeStart+"到"+outOrderTimeEnd);
				}else{
				MifiOrder.setStockStatus("所有时间");
				}

			}
			if(MifiOrder!=null && MifiOrder.getAllowedMcc() !=null ){
			list.add(MifiOrder);
		}
			}
		return list;
	}
	
	/**
	 * 按年统计订单
	 * @athor shuxin
	 * @date 2016年9月5日上午11:57:11
	 * @param paramMap
	 * @return
	 * JSONObject 
	 */
	public JSONObject statMifiOrderByYear(Map<String, Object> paramMap) {
		JSONObject json = new JSONObject();
		boolean flag = true;
		String begin = ObjectUtils.toString(paramMap.get("beginDate"));
		String end = ObjectUtils.toString(paramMap.get("endDate"));
		if (!StringUtils.isNotBlank(begin) && !StringUtils.isNotBlank(end)) { // 参数校验
			json.put("code", "-1");
			json.put("msg", "请选择要统计的时间");
			return json;
		}
		int bYear = Integer.valueOf(begin);
		int eYear = Integer.valueOf(end);
		if ((eYear - bYear) > 10) {
			json.put("code", "-1");
			json.put("msg", "时间间隔不能超过10年");
			return json;
		}
		// 处理x轴上数据
		List<String> x = new ArrayList<String>();
		int dayOff = eYear - bYear;
		int length = dayOff + 1;
		for (int i = 0; i < length; i++) {
			x.add(ObjectUtils.toString(bYear));
			bYear++;
		}

		List<Integer> orderTotal = new ArrayList<Integer>(); // 订单总数
		List<Double> priceTotal = new ArrayList<Double>(); // 订单总额
		List<Map<String, Object>> orderList = mifiOrderDao.findValidMifiOrderListByYear(paramMap);
		int xSize = x.size();
		int orderSize = orderList.size();
		for (int i = 0; i < xSize; i++) {
			String year = x.get(i);
			for (int j = 0; j < orderSize; j++) {
				Map<String, Object> tempMap = orderList.get(j);
				String y = ObjectUtils.toString(tempMap.get("year"));
				Object obj = tempMap.get("prices");
				double totalPrice = Double.parseDouble(ObjectUtils.toString(obj == null ? "0.00" : obj));
				Integer orderNum = Integer.valueOf(ObjectUtils.toString(tempMap.get("num")));
				if (year.equals(y)) {
					orderTotal.add(orderNum);
					BigDecimal b = new BigDecimal(totalPrice);
					priceTotal.add(b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					flag = false;
					break;
				} else {
					flag = true;
				}
			}
			// 没有匹配到，默认初始化为0
			if (flag) {
				orderTotal.add(0);
				priceTotal.add(0.00);
			}
		}

		json.put("x", x.toArray());
		json.put("orderTotal", orderTotal.toArray());
		json.put("priceTotal", priceTotal.toArray());
		return json;
	}
	/**
	 * 导出 年
	 * @Description 
	 * @param paramMap
	 * @return
	 * @throws ParseException 
	 * @return List<MifiOrder>  
	 * @author wangsai
	 * @date 2017年2月27日 下午1:50:51
	 */
	public List<MifiOrder> statMifiOrderBYYearExport(Map<String, Object> paramMap) throws ParseException {
		List<MifiOrder> list = null;
		StringBuffer buffer = new StringBuffer("select DATE_FORMAT(t.out_order_time, '%Y') year, sum(t.reference_total_price) prices,count(DATE_FORMAT(t.out_order_time, '%Y')) num from mifi_order t where 1 = 1");
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and t.source_type = '").append(sourceType).append("'");
		}
		// 订单时间
		String outOrderTimeStart = ObjectUtils.toString(paramMap.get("beginDate"));
		if (StringUtils.isNotBlank(outOrderTimeStart)) {
			buffer.append(" and (DATE_FORMAT(t.out_order_time, '%Y')) >= '").append(outOrderTimeStart).append("'");
		}
		String outOrderTimeEnd = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(outOrderTimeEnd)) {
			buffer.append("  and (DATE_FORMAT(t.out_order_time, '%Y')) <= '").append(outOrderTimeEnd).append("'");
		}
		String allowedMcc = ObjectUtils.toString(paramMap.get("allowedMcc"));
		if (StringUtils.isNotBlank(allowedMcc)) {
			buffer.append(" and t.allowed_mcc like '%" + allowedMcc + "%'");
		}
		//订单进行中和已完成的
		buffer.append(" and ( t.order_status = 0 or  t.order_status = 1 or t.order_status =8)");
		buffer.append(" GROUP BY DATE_FORMAT(t.out_order_time, '%Y')");
		List<String[]> listObjArr = mifiOrderDao.findBySql(buffer.toString());
		if (listObjArr != null && listObjArr.size() > 0) {
			list = new ArrayList<MifiOrder>();
			MifiOrder MifiOrder = null;
			for (Object[] objs : listObjArr) {
				MifiOrder = new MifiOrder();
				if (StringUtils.isNotBlank(allowedMcc)) {
					MifiOrder.setAllowedMcc(allowedMcc);
				} else {
					MifiOrder.setAllowedMcc("全部国家");
				}
				MifiOrder.setStockStatus(ObjectUtils.toString(objs[0]));
				MifiOrder.setOutOrderId(ObjectUtils.toString(objs[1]));
				MifiOrder.setEquipmentCnt(ObjectUtils.toString(objs[2]));
				list.add(MifiOrder);
				MifiOrder = null;
			}
		}
		return list;
	}
	/**
	 *  按月查询订单总额和订单数量
	 * @athor shuxin
	 * @date 2016年9月2日下午5:14:05
	 * @param paramMap
	 * @return
	 * List<Map<String,Object>> 
	 */
	public JSONObject statMifiOrderByMonth(Map<String, Object> paramMap) {
		boolean flag = true;
		JSONObject json = new JSONObject();
		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (!StringUtils.isNotBlank(beginDate) && !StringUtils.isNotBlank(endDate)) { // 参数校验
			json.put("code", "-1");
			json.put("msg", "请选择要统计的时间");
			return json;
		}
		Integer beginYear = Integer.valueOf(beginDate.split("-")[0]);
		Integer beginMonth = Integer.valueOf(beginDate.split("-")[1]);
		Integer endYear = Integer.valueOf(endDate.split("-")[0]);
		Integer endMonth = Integer.valueOf(endDate.split("-")[1]);
		// 计算x轴上面数据
		List<String> x = new ArrayList<String>();
		if (beginYear.equals(endYear)) { // 年份相同
			if (endMonth.equals(beginMonth)) {
				x.add(beginYear + "-" + beginMonth);
			} else {
				int monthOff = endMonth - beginMonth;
				for (int i = 0; i <= monthOff; i++) {
					x.add(beginDate.split("-")[0] + "-" + beginMonth);
					beginMonth++;
				}
			}
		} else {
			if (endYear - beginYear > 1) { // 超过一年
				json.put("code", -1);
				json.put("msg", "时间间隔不能超过12个月");
				return json;
			}
			if ((((12 - beginMonth) + 1) + endMonth) > 12) { // 只跨一年，计算月份是否超过12个月
				json.put("code", -1);
				json.put("msg", "时间间隔不能超过12个月");
				return json;
			}
			int beginMonthOff = 12 - beginMonth;
			for (int i = 0; i <= beginMonthOff; i++) {
				x.add(beginDate.split("-")[0] + "-" + beginMonth);
				beginMonth++;
			}
			int endMonthOff = endMonth;
			for (int i = 1; i <= endMonthOff; i++) {
				x.add(endDate.split("-")[0] + "-" + i);
			}
		}

		List<Integer> orderTotal = new ArrayList<Integer>(); // 订单总数
		List<Double> priceTotal = new ArrayList<Double>(); // 订单总额
		List<Map<String, Object>> orderList = mifiOrderDao.findValidMifiOrderListByMonth(paramMap);
		int xSize = x.size();
		int orderSize = orderList.size();
		for (int i = 0; i < xSize; i++) {
			String year = x.get(i);
			for (int j = 0; j < orderSize; j++) {
				Map<String, Object> tempMap = orderList.get(j);
				String y = ObjectUtils.toString(tempMap.get("month"));
				Object obj = tempMap.get("prices");
				double totalPrice = Double.parseDouble(ObjectUtils.toString(obj == null ? "0.00" : obj));
				Integer orderNum = Integer.valueOf(ObjectUtils.toString(tempMap.get("num")));
				if (year.equals(y)) {
					orderTotal.add(orderNum);
					BigDecimal b = new BigDecimal(totalPrice);
					priceTotal.add(b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					flag = false;
					break;
				} else {
					flag = true;
				}
			}
			// 没有匹配到，默认初始化为0
			if (flag) {
				orderTotal.add(0);
				priceTotal.add(0.00);
			}
		}

		json.put("x", x.toArray());
		json.put("orderTotal", orderTotal.toArray());
		json.put("priceTotal", priceTotal.toArray());
		return json;
	}
	/**
	 * 导出 月
	 * @Description 
	 * @param paramMap
	 * @return
	 * @throws ParseException 
	 * @return List<MifiOrder>  
	 * @author wangsai
	 * @date 2017年2月27日 下午1:50:51
	 */
	public List<MifiOrder> statMifiOrderBYMonthExport(Map<String, Object> paramMap) throws ParseException {
		List<MifiOrder> list = null;
		StringBuffer buffer = new StringBuffer("select DATE_FORMAT(t.out_order_time, '%Y-%c') month, sum(t.reference_total_price) prices,count(DATE_FORMAT(t.out_order_time, '%Y-%m')) num from mifi_order t where 1 = 1");
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and t.source_type = '").append(sourceType).append("'");
		}
		// 订单时间
		String outOrderTimeStart = ObjectUtils.toString(paramMap.get("beginDate"));
		if (StringUtils.isNotBlank(outOrderTimeStart)) {
			buffer.append(" and (DATE_FORMAT(t.out_order_time, '%Y-%m')) >= '").append(outOrderTimeStart).append("'");
		}
		String outOrderTimeEnd = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(outOrderTimeEnd)) {
			buffer.append("  and (DATE_FORMAT(t.out_order_time, '%Y-%m')) <= '").append(outOrderTimeEnd).append("'");
		}
		String allowedMcc = ObjectUtils.toString(paramMap.get("allowedMcc"));
		if (StringUtils.isNotBlank(allowedMcc)) {
			buffer.append(" and t.allowed_mcc like '%" + allowedMcc + "%'");
		}
		//订单进行中和已完成的
		buffer.append(" and ( t.order_status = 0 or  t.order_status = 1 or t.order_status =8)");
		buffer.append(" GROUP BY DATE_FORMAT(t.out_order_time, '%Y-%m')");
		List<String[]> listObjArr = mifiOrderDao.findBySql(buffer.toString());
		if (listObjArr != null && listObjArr.size() > 0) {
			list = new ArrayList<MifiOrder>();
			MifiOrder MifiOrder = null;
			for (Object[] objs : listObjArr) {
				MifiOrder = new MifiOrder();
				if (StringUtils.isNotBlank(allowedMcc)) {
					MifiOrder.setAllowedMcc(allowedMcc);
				} else {
					MifiOrder.setAllowedMcc("全部国家");
				}
				MifiOrder.setStockStatus(ObjectUtils.toString(objs[0]));
				MifiOrder.setOutOrderId(ObjectUtils.toString(objs[1]));
				MifiOrder.setEquipmentCnt(ObjectUtils.toString(objs[2]));
				list.add(MifiOrder);
				MifiOrder = null;
			}
		}
		return list;
	}
	/**
	 *  按天查询订单总额和订单数量
	 * @athor shuxin
	 * @date 2016年9月2日下午5:14:15
	 * @param paramMap
	 * @return
	 * List<Map<String,Object>> 
	 */
	public JSONObject statMifiOrderByDay(Map<String, Object> paramMap) {
		JSONObject json = new JSONObject();
		boolean flag = true;
		String beginDate = ObjectUtils.toString(paramMap.get("beginDate"));
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		Integer beginYear = Integer.valueOf(beginDate.split("-")[0]);
		Integer beginMonth = Integer.valueOf(beginDate.split("-")[1]);
		Integer beginDay = Integer.valueOf(beginDate.split("-")[2]);
		Integer endYear = Integer.valueOf(endDate.split("-")[0]);
		Integer endMonth = Integer.valueOf(endDate.split("-")[1]);
		Integer endDay = Integer.valueOf(endDate.split("-")[2]);
		if (!StringUtils.isNotBlank(beginDate) && !StringUtils.isNotBlank(endDate)) { // 参数校验
			json.put("code", "-1");
			json.put("msg", "请选择要统计的时间");
			return json;
		}
		int dayOff;
		try {
			dayOff = DateUtils.daysBetween(beginDate, endDate);
			if ((dayOff + 1) > 31) {
				json.put("code", "-1");
				json.put("msg", "时间间隔不能超过31天");
				return json;
			}
			// 计算x轴上数据
			List<String> x = new ArrayList<String>();
			if ((endMonth - beginMonth) == 0) { // 同一月的数据处理
				for (int i = beginDay; i <= endDay; i++) {
					x.add(ObjectUtils.toString(beginYear) + "-" + ObjectUtils.toString(beginMonth) + "-"
							+ ObjectUtils.toString(i));
				}
			} else {
				// 处理开始月的数据
				Integer beginMonthOfDay = DateUtils.getDaysByYearMonth(beginYear, beginMonth);
				for (int i = beginDay; i <= beginMonthOfDay; i++) {
					x.add(ObjectUtils.toString(beginYear) + "-" + ObjectUtils.toString(beginMonth) + "-"
							+ ObjectUtils.toString(i));
				}
				// 处理结束月的数据
				for (int i = 1; i <= endDay; i++) {
					x.add(ObjectUtils.toString(endYear) + "-" + ObjectUtils.toString(endMonth) + "-"
							+ ObjectUtils.toString(i));
				}
			}

			List<Integer> orderTotal = new ArrayList<Integer>(); // 订单总数
			List<Double> priceTotal = new ArrayList<Double>(); // 订单总额
			List<Map<String, Object>> orderList = mifiOrderDao.findValidMifiOrderListByDay(paramMap);

			int xSize = x.size();
			int orderSize = orderList.size();
			for (int i = 0; i < xSize; i++) {
				String year = x.get(i);
				for (int j = 0; j < orderSize; j++) {
					Map<String, Object> tempMap = orderList.get(j);
					String y = ObjectUtils.toString(tempMap.get("day"));
					Object obj = tempMap.get("prices");
					double totalPrice = Double.parseDouble(ObjectUtils.toString(obj == null ? "0.00" : obj));
					Integer orderNum = Integer.valueOf(ObjectUtils.toString(tempMap.get("num")));
					if (year.equals(y)) {
						orderTotal.add(orderNum);
						BigDecimal b = new BigDecimal(totalPrice);
						priceTotal.add(b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						flag = false;
						break;
					} else {
						flag = true;
					}
				}
				// 没有匹配到，默认初始化为0
				if (flag) {
					orderTotal.add(0);
					priceTotal.add(0.00);
				}
			}

			json.put("x", x.toArray());
			json.put("orderTotal", orderTotal.toArray());
			json.put("priceTotal", priceTotal.toArray());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 导出 日
	 * @Description 
	 * @param paramMap
	 * @return
	 * @throws ParseException 
	 * @return List<MifiOrder>  
	 * @author wangsai
	 * @date 2017年2月27日 下午1:50:51
	 */
	public List<MifiOrder> statMifiOrderBYDayExport(Map<String, Object> paramMap) throws ParseException {
		List<MifiOrder> list = null;
		StringBuffer buffer = new StringBuffer("select DATE_FORMAT(t.out_order_time, '%Y-%c-%e') day, sum(t.reference_total_price) prices,count(DATE_FORMAT(t.out_order_time, '%Y-%m-%d')) num from mifi_order t where 1 = 1");
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			buffer.append(" and t.source_type = '").append(sourceType).append("'");
		}
		// 订单时间
		String outOrderTimeStart = ObjectUtils.toString(paramMap.get("beginDate"));
		if (StringUtils.isNotBlank(outOrderTimeStart)) {
			buffer.append(" and (DATE_FORMAT(t.out_order_time, '%Y-%m-%d')) >= '").append(outOrderTimeStart).append("'");
		}
		String outOrderTimeEnd = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(outOrderTimeEnd)) {
			buffer.append("  and (DATE_FORMAT(t.out_order_time, '%Y-%m-%d')) <= '").append(outOrderTimeEnd).append("'");
		}
		String allowedMcc = ObjectUtils.toString(paramMap.get("allowedMcc"));
		if (StringUtils.isNotBlank(allowedMcc)) {
			buffer.append(" and t.allowed_mcc like '%" + allowedMcc + "%'");
		}
		//订单进行中已下单、已发货、已完成的
		buffer.append(" and ( t.order_status = 0 or  t.order_status = 1 or t.order_status =8)");
		buffer.append(" GROUP BY DATE_FORMAT(t.out_order_time, '%Y-%m-%d')");
		List<String[]> listObjArr = mifiOrderDao.findBySql(buffer.toString());
		if (listObjArr != null && listObjArr.size() > 0) {
			list = new ArrayList<MifiOrder>();
			MifiOrder MifiOrder = null;
			for (Object[] objs : listObjArr) {
				MifiOrder = new MifiOrder();
				if (StringUtils.isNotBlank(allowedMcc)) {
					MifiOrder.setAllowedMcc(allowedMcc);
				} else {
					MifiOrder.setAllowedMcc("全部国家");
				}
				MifiOrder.setStockStatus(ObjectUtils.toString(objs[0]));
				MifiOrder.setOutOrderId(ObjectUtils.toString(objs[1]));
				MifiOrder.setEquipmentCnt(ObjectUtils.toString(objs[2]));
				list.add(MifiOrder);
				MifiOrder = null;
			}
		}
		return list;
	}
	/**
	 * 根据国家编码条件获取相应的国家中文名称和对应的国家MCC
	 * @athor shuxin
	 * @date 2016年9月7日下午3:19:08
	 * @param countryCode
	 * @return
	 * List<Map<String,String>> 
	 */
	public List<Map<String, String>> findMccsByCountryCode(String countryCode) {
		StringBuffer buffer = new StringBuffer("SELECT t.mcces country_mcc ,t.country_name_cn country_name_cn FROM (");
		buffer.append(" select group_concat(mcc) mcces, country_name_cn, country_code from mcc_def group by country_name_cn order by convert(country_name_cn using gbk) asc");
		buffer.append(") t WHERE t.country_code in (");
		buffer.append(countryCode);
		buffer.append(")");
		return mifiOrderDao.findBySql(buffer.toString(), null, Map.class);
	}
	
	/**
	 * 
	 * @Description 订单数量、金额统计
	 * @param startDate
	 * @param endDate
	 * @param sourceType
	 * @return Map<String,Object>
	 * @author yifang.huang
	 * @date 2016年9月20日 下午2:54:21
	 */
	public Map<String, Object> findOrderCount(String startDate, String endDate, String sourceType) {
		return mifiOrderDao.findOrderCount(startDate, endDate, sourceType);
	}
	
	/**
	 * 
	 * @Description 查询开始订单列表
	 * @param date
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2017年2月23日 下午5:01:25
	 */
	public List<Map<String, Object>> getStartOrderList(String date) {
		
		// 开始的订单数
		String sql = "select t1.order_id id, t1.allowed_mcc mcces from mifi_order t1 where t1.order_status='1'"
				+ " and t1.start_date=STR_TO_DATE('" + date + "', '%Y-%m-%d %H:%i:%s')";
		return mifiOrderDao.findBySql(sql, null, Map.class);

	}
	
	/**
	 * 
	 * @Description 开始订单（参数data在订单开始时间>=startDate和结束时间<endDate之间）
	 * @param date yyyy-MM-dd 00:00:00
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2017年2月23日 下午3:31:04
	 */
	public Map<String, String> updateStartOrder(String date) {
		
		Map<String, String> map = new HashMap<String, String>();
		int imeiCount = 0;
		String orderCount = "0";
		
		if (StringUtils.isBlank(date)) {
			map.put("status", "error");
			map.put("message", "参数时间不能为空");
			return map;
		}
		
		// 开始的订单数
		String sql = "select count(t1.order_id) orderCount from mifi_order t1 where t1.order_status='1' "
				+ "and t1.start_date=STR_TO_DATE('" + date + "', '%Y-%m-%d %H:%i:%s')";
		List<Map<String, Object>> listMap = mifiOrderDao.findBySql(sql.toString(), null, Map.class);
		if (listMap!=null && listMap.size()>0) {
			Map<String, Object> tempMap = listMap.get(0);
			orderCount = ObjectUtils.toString(tempMap.get("orderCount"));
		}
		
		// 修改流量订单对应的设备(mifilist)的order_type=1,flow_used=0,flow=order.flow*1024*1024,start_time=order.start_date,end_time=order.end_date
		sql = "UPDATE mifi_order t, mifi_order_detail f, mifilist l SET l.order_type='1', l.flow=(t.flow*1024*1024),"
				+ " l.flow_used=0, l.start_time=t.start_date, l.end_time=t.end_date WHERE t.order_status='1' AND t.order_type='1'"
				+ " and t.start_date=STR_TO_DATE('" + date + "', '%Y-%m-%d %H:%i:%s') AND t.order_id=f.order_id"
				+ " AND f.dsn=l.IMEI_6200 AND l.ueallowed=0";
		mifiOrderDao.updateBySql(sql, null);
		
		// 修改所有订单对应设备(mifilist)的ueallowed=1,ueallowedmcc=order.allowed_mcc;(mifiversion)的limit_speed_flag=order.limit_speed_flag
		sql = "UPDATE mifi_order t, mifi_order_detail f, mifilist l, mifiversion v SET l.ueallowed = 1, l.ueallowedmcc = t.allowed_mcc,"
				+ " v.speedlimit_type=t.limit_speed_flag WHERE t.order_status='1' AND t.order_id=f.order_id"
				+ " and t.start_date=STR_TO_DATE('" + date + "', '%Y-%m-%d %H:%i:%s') AND f.dsn=l.IMEI_6200"
				+ " AND l.IMEI_6200=v.IMEI_6200 AND l.ueallowed=0";
		imeiCount = mifiOrderDao.updateBySql(sql, null);

		map.put("status", "success");
		map.put("imeiCount", imeiCount + "");
		map.put("orderCount", orderCount);
		map.put("message", "运行成功");
		return map;
		
	}
	
	/**
	 * 
	 * @Description 查询结束订单列表（订单结束时间在>startDate和<=endDate之间）
	 * @param startDate
	 * @param endDate
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2017年2月24日 下午2:36:42
	 */
	public List<Map<String, Object>> getFinishOrderList(String startDate, String endDate) {
		
		// 开始的订单数
		String sql = "select t1.order_id id, t1.allowed_mcc mcces from mifi_order t1 where t1.order_status='1'"
				+ " and t1.end_date > STR_TO_DATE('" + startDate + "', '%Y-%m-%d %H:%i:%s')"
				+ " and t1.end_date <= STR_TO_DATE('" + endDate + "', '%Y-%m-%d %H:%i:%s')";
		return mifiOrderDao.findBySql(sql, null, Map.class);
		
	}
	
	/**
	 * 
	 * @Description 完成订单（订单结束时间在>startDate和<=endDate之间）
	 * @param startDate
	 * @param endDate
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年11月11日 下午3:39:35
	 */
	public Map<String, String> updateFinishOrder(String startDate, String endDate) {
		
		Map<String, String> map = new HashMap<String, String>();
		int imeiCount = 0;
		int testCount = 0;
		int orderCount = 0;
		
		try {
			if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
				map.put("status", "error");
				map.put("message", "开始时间、结束时间不能为空");
				return map;
			}
			
			// 修改设备UEALLOWED=0,UEALLOWEDMCC='homeForbidden'
			String sql = "update mifi_order a, mifi_order_detail b, mifilist c, mifiversion d set c.UEALLOWED=0, c.UEALLOWEDMCC='homeForbidden', d.speedlimit_type='0' "
					+ "where a.order_id=b.order_id and a.end_date>STR_TO_DATE('" + startDate + "', '%Y-%m-%d %H:%i:%s') "
					+ "and a.end_date<=STR_TO_DATE('" + endDate + "', '%Y-%m-%d %H:%i:%s') and a.order_status=1 "
					+ "and b.dsn=c.IMEI_6200 AND c.IMEI_6200=d.IMEI_6200";
			imeiCount = mifiOrderDao.updateBySql(sql, null);
			
			// 删除订单中设备已绑定卡的记录
			sql = "delete from mifitest where imei in "
					+ "(select b.dsn from mifi_order a, mifi_order_detail b where a.order_id=b.order_id "
					+ "and a.end_date>STR_TO_DATE('" + startDate + "', '%Y-%m-%d %H:%i:%s') "
					+ "and a.end_date<=STR_TO_DATE('" + endDate + "', '%Y-%m-%d %H:%i:%s') and a.order_status=1)";
			testCount = mifitestDao.updateBySql(sql, null);
			
			// 修改订单order_status=8,finish_time=NOW()
			sql = "update mifi_order a set a.order_status=8, a.finish_time=NOW() "
					+ "where a.end_date>STR_TO_DATE('" + startDate + "', '%Y-%m-%d %H:%i:%s') "
					+ "and a.end_date<=STR_TO_DATE('" + endDate + "', '%Y-%m-%d %H:%i:%s') and a.order_status=1";
			orderCount = mifiOrderDao.updateBySql(sql, null);
			
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", "error");
			map.put("message", "运行错误" + e.getMessage());
			return map;
		}

		map.put("status", "success");
		map.put("imeiCount", imeiCount + "");
		map.put("testCount", testCount + "");
		map.put("orderCount", orderCount + "");
		map.put("message", "运行成功");
		return map;
		
	}
	
	/**
	 * 
	 * @Description 查询设备订单列表
	 * @param dsn 设备编号
	 * @param date 时间点在订单行程内
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2016年12月5日 下午3:13:03
	 */
	public List<Map<String, Object>> findOrderList(String dsn, String date) {
		
		return mifiOrderDao.findOrderList(dsn, date);
		
	}
	/**
	 * 
	 * @Description 根据mcc取国家编号
	 * @param mcc
	 * @return HashMap<String,String>  
	 * @author yifang.huang
	 * @date 2016年4月15日 上午10:50:41
	 */
	public HashMap<String, String> findCountryByMcc(String mcc) {
		
		return mifiOrderDao.findCountryByMcc(mcc);
		
	}

	/**
	 * 
	 * @Description 根据订单ID取设备列表
	 * @param orderId
	 * @return String  
	 * @author yifang.huang
	 * @date 2017年2月24日 下午3:12:57
	 */
	public String getDsnByOrderId(String orderId) {
		return mifiOrderDetailDao.getDsnByOrderId(orderId);
	}

}
