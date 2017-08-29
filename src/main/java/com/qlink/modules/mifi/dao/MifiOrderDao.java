package main.java.com.qlink.modules.mifi.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Parameter;
import com.uu.modules.mifi.entity.MifiOrder;

/**
 * 
 * @author wangyong
 * @date 2016年2月4日
 */
@Repository
public class MifiOrderDao extends BaseDao<MifiOrder> {

	public static Logger logger = LoggerFactory.getLogger(MifiOrderDao.class);

	public long getMifiListCountByDsn(String dsn) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("select count(*) from mifilist where IMEI_6200 = '" + dsn + "'");
		return Long.parseLong(findBySql(buffer.toString()).get(0).toString());
	}

	public HashMap<String, Object> getMccs(String mcc) {
		return getMccs(mcc, null);
	}

	public HashMap<String, Object> getMccs(String mcc, List<String> mccList) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(mcc)) {
			hashMap.put("allowed_mcc_code", "ALL");
			hashMap.put("allowed_mcc_cn", "全球");
			hashMap.put("allowed_mcc_en", "the whole world");
			return hashMap;
		}
		List<Map<String, Object>> list = getOriginalMccs(mcc, mccList);
		if(null == list || 0 == list.size()){
			return hashMap;
		}
		String _countryNameEn = StringUtils.EMPTY;
		String countryCodes = StringUtils.EMPTY;
		String countryNameCns = StringUtils.EMPTY;
		String countryNameEns = StringUtils.EMPTY;
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			String countryCode = (String) map.get("country_code");
			String countryNameCn = (String) map.get("country_name_cn");
			String countryNameEn = (String) map.get("country_name_en");
			if (i == 0) {
				countryCodes += countryCode;
				countryNameCns += countryNameCn;
				countryNameEns += countryNameEn;
			} else {
				if (!_countryNameEn.equals(countryNameEn)) {
					countryCodes += "," + countryCode;
					countryNameCns += "," + countryNameCn;
					countryNameEns += "," + countryNameEn;
				}
			}
			_countryNameEn = countryNameEn;
		}
		hashMap.put("allowed_mcc_code", countryCodes);
		hashMap.put("allowed_mcc_cn", countryNameCns);
		hashMap.put("allowed_mcc_en", countryNameEns);
		return hashMap;
	}

	/**
	 * 获取MCC原始数据
	 * @Description 
	 * @param mcc
	 * @param mccList
	 * @return 
	 * @return List<HashMap<String,Object>>  
	 * @author yuxiaoyu
	 * @date 2016年12月19日 下午2:36:23
	 */
	public List<Map<String, Object>> getOriginalMccs(String mcc, List<String> mccList) {
		//生成查询条件
		StringBuilder stringBuilder = new StringBuilder();
		String[] mccs = mcc.split(",");
		for (int i = 0; i < mccs.length; i++) {
			if (null != mccList && !mccList.contains(mccs[i])) {// 按照国家筛选数据
				continue;
			}
			stringBuilder.append("'").append(mccs[i]).append("',");
		}
		if (StringUtils.isBlank(stringBuilder)) {
			return null;
		}
		String countryIn = stringBuilder.substring(0, stringBuilder.length() - 1);// 去掉最后多余的逗号
		//查询
		String sql = "select mcc,country_code,country_name_cn,country_name_en from mcc_def where mcc in(" + countryIn
				+ ") order by country_name_en desc ";
		List<Map<String, Object>> list = findBySql(sql, null, Map.class);
		return list;
	}

	/**
	 * 
	 * @Description 根据国家编号取MCC
	 * @param countryCode
	 * @return HashMap<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月3日 下午3:13:19
	 */
	public HashMap<String, String> findMccByCountryCode(String countryCode) {
		
		List<HashMap<String, String>> list = findBySql("select group_concat(mcc) mcces, country_code countryCode, country_name_cn countryName from mcc_def where country_code=:p1", new Parameter(countryCode), Map.class);
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		
		return null;
		
	}

	/**
	 * 
	 * @Description 根据国家编号取MCC
	 * @param countryCodes
	 * @return HashMap<String,String>  
	 * @author yifang.huang
	 * @date 2016年5月3日 下午3:13:19
	 */
	public HashMap<String, String> findMccByCountryCodes(String countryCodes) {
		
		StringBuffer sb = new StringBuffer("select group_concat(mcc) mcces from mcc_def");
		
		if (countryCodes.indexOf(",") != -1)
			sb.append(" where country_code in(" + countryCodes + ")");
		else 
			sb.append(" where country_code=" + countryCodes);
		
		List<HashMap<String, String>> list = findBySql(sb.toString(), null, Map.class);
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		
		return null;
		
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
		
		List<HashMap<String, String>> list = findBySql("select country_code countryCode, country_name_cn countryName from mcc_def where mcc='" + mcc + "'", null, Map.class);
		if (list!=null && list.size()>0) {
			return list.get(0);
		}
		
		return null;
		
	}

	/**
	 * 
	 * @Description 统计可用的sim卡数量
	 * @param mcc
	 * @param usableData
	 * @return int  
	 * @author yifang.huang
	 * @date 2016年5月4日 下午1:27:36
	 */
	public int findUsableSimnodeCount(String mcc, long usableData) {
		
		StringBuffer sb = new StringBuffer("select count(id) sum from simnode t where (t.DATAUSED+"+usableData+")<t.DATACAP and t.USIMSTATUS=2");
		sb.append(" and t.ALLOWEDMCC like '%" + mcc + "%'");
		sb.append(" and (t.SIMCARDVALIDDAY is null or t.stamp_firstactive='0000-00-00 00:00:00')");
		
		List<Map<String, Object>> list = findBySql(sb.toString(), null, Map.class);
		Map<String, Object> map = list.get(0);
		int size = Integer.valueOf(ObjectUtils.toString(map.get("sum")));
		sb = null;
		
		sb = new StringBuffer("select count(id) sum from simnode t where (t.DATAUSED+"+usableData+")<t.DATACAP and t.USIMSTATUS=2");
		sb.append(" and t.ALLOWEDMCC like '%" + mcc + "%'");
		sb.append(" and t.SIMCARDVALIDDAY is not null and t.stamp_firstactive!='0000-00-00 00:00:00' and datediff(NOW(), t.stamp_firstactive)<t.SIMCARDVALIDDAY");
		list = findBySql(sb.toString(), null, Map.class);
		map = list.get(0);
		size += Integer.valueOf(ObjectUtils.toString(map.get("sum")));
		sb = null;
		
		return size;
		
	}

	/**
	 * 
	 * @Description 统计可用的sim卡数量
	 * @param mcc
	 * @param usableData
	 * @param usePeopleType
	 * @return int  
	 * @author yifang.huang
	 * @date 2016年5月4日 下午1:27:36
	 */
	public int findUsableSimnodeCountTwo(String mcc, long usableData, String usePeopleType) {
		
		StringBuffer sb = new StringBuffer("select count(t.id) sum from simnode t, card_basic_info a, sim_card_type b where hex(t.iccid)=a.sn and a.type=b.card_type");
		sb.append(" and (t.DATAUSED+"+usableData+")<t.DATACAP and t.USIMSTATUS=2");
		sb.append(" and t.ALLOWEDMCC like '%" + mcc + "%'");
		sb.append(" and (t.SIMCARDVALIDDAY is null or t.stamp_firstactive='0000-00-00 00:00:00')");
		sb.append(" and b.use_people_type='" + usePeopleType + "'");
		
		List<Map<String, Object>> list = findBySql(sb.toString(), null, Map.class);
		Map<String, Object> map = list.get(0);
		int size = Integer.valueOf(ObjectUtils.toString(map.get("sum")));
		sb = null;
		
		sb = new StringBuffer("select count(t.id) sum from simnode t, card_basic_info a, sim_card_type b where hex(t.iccid)=a.sn and a.type=b.card_type");
		sb.append(" and (t.DATAUSED+"+usableData+")<t.DATACAP and t.USIMSTATUS=2");
		sb.append(" and t.ALLOWEDMCC like '%" + mcc + "%'");
		sb.append(" and t.SIMCARDVALIDDAY is not null and t.stamp_firstactive!='0000-00-00 00:00:00' and datediff(NOW(), t.stamp_firstactive)<t.SIMCARDVALIDDAY");
		sb.append(" and b.use_people_type='" + usePeopleType + "'");
		list = findBySql(sb.toString(), null, Map.class);
		map = list.get(0);
		size += Integer.valueOf(ObjectUtils.toString(map.get("sum")));
		sb = null;
		
		return size;
		
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
		
		if (StringUtils.isBlank(sn))
			return null;

		StringBuffer sql = new StringBuffer("select o.order_id orderId, o.order_status orderStatus, o.source_type sourceType, o.out_order_id outOrderId, "
				+ "o.allowed_mcc allowedMcc, DATE_FORMAT(o.out_order_time, '%Y-%m-%d %T') outOrderTime, DATE_FORMAT(o.start_date, '%Y-%m-%d %T') startDate, "
				+ "DATE_FORMAT(o.end_date, '%Y-%m-%d %T') endDate, o.customer_id customerId "
				+ "from mifi_order o, mifi_order_detail od where o.order_id=od.order_id and o.start_date<NOW() "
				+ "and o.end_date>NOW() and (o.order_status='0' or o.order_status='1')");
		if (StringUtils.isNotBlank(orderType)) {
			sql.append(" and o.order_type='" + orderType + "'");
		}
		if (StringUtils.isNotBlank(sn)) {
			sql.append(" and od.dsn='" + sn + "'");
		}

		return findBySql(sql.toString(), null, Map.class);
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
		
		if (StringUtils.isBlank(dsn))
			return null;
		
		StringBuffer sb = new StringBuffer("select a.out_order_id outOrderId, a.allowed_mcc allowedMcc, a.start_date startDate, a.end_date endDate from mifi_order a, mifi_order_detail b where a.order_id=b.order_id");
		if (StringUtils.isNotBlank(date)) {
			sb.append(" and a.start_date<=STR_TO_DATE('" + date + "', '%Y-%m-%d %H:%i:%s')");
			sb.append(" and a.end_date>STR_TO_DATE('" + date + "', '%Y-%m-%d %H:%i:%s')");
		}
		sb.append(" and a.order_status='1' and b.dsn='" + dsn + "'");
		
		return findBySql(sb.toString(), null, Map.class);
		
	}
	
	/**
	 * 
	 * @Description 根据设备编号和起始时间取订单
	 * @param imei
	 * @param startDate
	 * @param endDate
	 * @return List<Map<String,String>>  
	 * @author yifang.huang
	 * @date 2016年8月23日 下午2:37:10
	 */
	public List<Map<String, String>> getOrderByDeviceAndTripDate(String imei, String startDate, String endDate) {
		
		StringBuffer sql = new StringBuffer("SELECT o.order_id orderId, o.order_status orderStatus, o.source_type sourceType,"
				+ " o.out_order_id outOrderId, o.allowed_mcc allowedMcc, o.out_order_time outOrderTime, o.start_date startDate,"
				+ " o.end_date endDate FROM mifi_order o, mifi_order_detail od WHERE o.order_id=od.order_id AND o.order_status='1'");
		
		if (StringUtils.isNotBlank(imei))
			sql.append(" AND od.dsn='" + imei + "'");
		
		if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate))
			sql.append(" AND (start_date BETWEEN STR_TO_DATE('"+startDate+"','%Y-%m-%d %H:%i:%s') AND STR_TO_DATE('"+endDate+"','%Y-%m-%d %H:%i:%s') "
				+ "OR end_date BETWEEN STR_TO_DATE('"+startDate+"','%Y-%m-%d %H:%i:%s') AND STR_TO_DATE('"+endDate+"','%Y-%m-%d %H:%i:%s') "
				+ "OR (start_date<=STR_TO_DATE('"+startDate+"','%Y-%m-%d %H:%i:%s') AND end_date>=STR_TO_DATE('"+endDate+"','%Y-%m-%d %H:%i:%s')))");
		
		return findBySql(sql.toString(), null, Map.class);
	}
	
	/**
	 * 
	 * @Description 根据起始时间取订单
	 * @param startDate
	 * @param endDate
	 * @return List<Map<String,String>>  
	 * @author yifang.huang
	 * @date 2017年5月19日 下午3:35:02
	 */
	public List<Map<String, String>> getOrderByTripDate(String startDate, String endDate) {
		
		StringBuffer sql = new StringBuffer("SELECT o.order_id orderId, o.order_status orderStatus, o.source_type sourceType,"
				+ " o.out_order_id outOrderId, o.allowed_mcc allowedMcc, o.out_order_time outOrderTime, o.start_date startDate,"
				+ " o.end_date endDate FROM mifi_order o WHERE (o.order_status='1' or o.order_status='8')");
		
		if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate))
			sql.append(" AND (start_date BETWEEN STR_TO_DATE('"+startDate+"','%Y-%m-%d %H:%i:%s') AND STR_TO_DATE('"+endDate+"','%Y-%m-%d %H:%i:%s') "
				+ "OR end_date BETWEEN STR_TO_DATE('"+startDate+"','%Y-%m-%d %H:%i:%s') AND STR_TO_DATE('"+endDate+"','%Y-%m-%d %H:%i:%s') "
				+ "OR (start_date<=STR_TO_DATE('"+startDate+"','%Y-%m-%d %H:%i:%s') AND end_date>=STR_TO_DATE('"+endDate+"','%Y-%m-%d %H:%i:%s')))");
		
		return findBySql(sql.toString(), null, Map.class);
	}
	
	/**
	 * 根据国家统计有效订单列表
	 * @athor shuxin
	 * @date 2016年9月5日上午11:38:13
	 * @param paramMap
	 * @return
	 * List<Map<String,Object>> 
	 */
	public List<Map<String, Object>> findValidMifiOrderList(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer("select t.allowed_mcc, t.reference_total_price from mifi_order t where 1 = 1");
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
		//订单进行中和已完成的
		buffer.append(" and ( t.order_status = 0 or  t.order_status = 1 or t.order_status =8)");
		buffer.append("   order by t.start_date");
		List<Map<String, Object>> list = this.findBySql(buffer.toString(), null, Map.class);
		for (int i = 0; i < list.size(); i++) {
			list.get(i).putAll(this.getMccs((String) list.get(i).get("allowed_mcc")));
		}
		return list;
	}
	
	/**
	 * 统计订单总数
	 * @athor shuxin
	 * @date 2016年9月28日上午9:55:26
	 * @param paramMap
	 * @return
	 * List<Map<String,Object>> 
	 */
	public HashMap<String, Object> findValidMifiOrderTotalNum(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer("select count(t.allowed_mcc) totalNum from mifi_order t where 1 = 1");
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
		//订单进行中和已完成的
		buffer.append(" and ( t.order_status = 0 or  t.order_status = 1 or t.order_status =8)");
		buffer.append("   order by t.start_date");
		List<HashMap<String,  Object>> list = this.findBySql(buffer.toString(), null, Map.class);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	
	/**
	 * 统计订单总价格
	 * @athor shuxin
	 * @date 2016年9月28日上午10:02:13
	 * @param paramMap
	 * @return
	 * List<Map<String,Object>> 
	 */
	public HashMap<String, Object> findValidMifiOrderTotalPrice(Map<String, Object> paramMap) {
		StringBuffer buffer = new StringBuffer("select SUM(t.reference_total_price) totalPrice from mifi_order t where 1 = 1");
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
		//订单进行中和已完成的
		buffer.append(" and ( t.order_status = 0 or  t.order_status = 1 or t.order_status =8)");
		buffer.append("   order by t.start_date");
		List<HashMap<String, Object>> list = this.findBySql(buffer.toString(), null, Map.class);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 按年查询订单总额和订单数量
	 * @athor shuxin
	 * @date 2016年9月2日下午5:10:16
	 * @param paramMap
	 * @return
	 * List<Map<String,Object>> 
	 */
	public List<Map<String, Object>> findValidMifiOrderListByYear(Map<String, Object> paramMap) {
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
		return this.findBySql(buffer.toString(), null, Map.class);
	}
	
	/**
	 *  按月查询订单总额和订单数量
	 * @athor shuxin
	 * @date 2016年9月2日下午5:14:05
	 * @param paramMap
	 * @return
	 * List<Map<String,Object>> 
	 */
	public List<Map<String, Object>> findValidMifiOrderListByMonth(Map<String, Object> paramMap) {
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
		return this.findBySql(buffer.toString(), null, Map.class);
	}
	
	
	/**
	 *  按天查询订单总额和订单数量
	 * @athor shuxin
	 * @date 2016年9月2日下午5:14:15
	 * @param paramMap
	 * @return
	 * List<Map<String,Object>> 
	 */
	public List<Map<String, Object>> findValidMifiOrderListByDay(Map<String, Object> paramMap) {
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
		return this.findBySql(buffer.toString(), null, Map.class);
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
		
		StringBuffer sql = new StringBuffer("SELECT COUNT(A.ORDER_ID) COUNT, SUM(A.REFERENCE_TOTAL_PRICE) PRICE"
				+ " FROM MIFI_ORDER A WHERE A.ORDER_STATUS!=9 AND A.ORDER_STATUS!=11");
		
		if (StringUtils.isNotBlank(startDate))
			sql.append(" AND A.DELIVERY_TIME>=STR_TO_DATE('" + startDate + "', '%Y-%m-%d %H:%i:%s')");
		if (StringUtils.isNotBlank(endDate))
			sql.append(" AND A.DELIVERY_TIME<=STR_TO_DATE('" + endDate + "', '%Y-%m-%d %H:%i:%s')");
		if (StringUtils.isNotBlank(sourceType))
			sql.append(" AND A.SOURCE_TYPE='" + sourceType + "'");
		
		List<Map<String, Object>> listMap = findBySql(sql.toString(), null, Map.class);
		
		if (listMap!=null && listMap.size()>0)
			return listMap.get(0);

		return null;
		
	}
	
	/**
	 * 
	 * @Description 取国家设备使用数量
	 * @param startDate
	 * @param endDate
	 * @return Map<String,Object>  
	 * @author yifang.huang
	 * @date 2016年10月19日 下午3:49:59
	 */
	public List<Map<String, Object>> findCountryEquipmentCount(String startDate, String endDate, List<String> mccList) {
		if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate))
			return null;
		
		StringBuffer sql = new StringBuffer("SELECT a.allowed_mcc mcc, a.equipment_cnt equCnt FROM MIFI_ORDER A WHERE A.ORDER_STATUS=1");
		// 订单start_date在输入时间范围内 || 订单endDate在输入时间范围内 || 订单时间包含输入时间
		sql.append(" AND (start_date BETWEEN STR_TO_DATE('"+startDate+"','%Y-%m-%d %H:%i:%s') AND STR_TO_DATE('"+endDate+"','%Y-%m-%d %H:%i:%s') "
				+ "OR end_date BETWEEN STR_TO_DATE('"+startDate+"','%Y-%m-%d %H:%i:%s') AND STR_TO_DATE('"+endDate+"','%Y-%m-%d %H:%i:%s') "
				+ "OR (start_date<=STR_TO_DATE('"+startDate+"','%Y-%m-%d %H:%i:%s') AND end_date>=STR_TO_DATE('"+endDate+"','%Y-%m-%d %H:%i:%s')))");
		
		if (null != mccList && 0 < mccList.size()) {
			sql.append(" and (");
			for(int i=0;i < mccList.size();i++){
				sql.append(" a.allowed_mcc like '%").append(mccList.get(i)).append("%' or");
			}
			String sqlStr = sql.substring(0, sql.length() - 2);
			sql.setLength(0);
			sql.append(sqlStr).append(")");
		}
		return findBySql(sql.toString(), null, Map.class);
	}
	
}
