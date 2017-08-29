package main.java.com.qlink.modules.mifi.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;
import com.uu.common.persistence.Page;
import com.uu.common.persistence.Parameter;
import com.uu.modules.mifi.entity.CardBasicInfo;
import com.uu.modules.utils.Constants;

/**
 * 
 * @author wangyong
 * @date 2016年2月17日
 */
@Repository
public class CardManageDao extends BaseDao<CardBasicInfo> {

	public static Logger logger = LoggerFactory.getLogger(CardBasicInfo.class);
	
	/**
	 * 查询设备信息
	 * @param sn
	 * @return
	 */
	public CardBasicInfo findCardBasicInfoBySn(String sn){
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("sn", sn));
		List<CardBasicInfo> lists = find(dc);
		return lists.size() > 0 ? lists.get(0) : null;
	}
	
	public int findSimNodeInfoBySn(String sn){
		String str = "select hex(iccid) iccid,hex(imsi) imsi from simnode where hex(iccid) = '" + sn + "'";
		str += " and usimstatus != 0 ";
		str += " and hex(iccid) != '00000000000000000000' ";
		List<HashMap> list = this.findBySql(str, null, Map.class);
		return list.size();
	}
	
	/**
	 * 
	 * @Description 修改card_basic_info表的sn_hex字段,sn_hex字段为空，且在simnode存在有效数据
	 * @return int  
	 * @author yifang.huang
	 * @date 2017年4月27日 下午3:52:03
	 */
	public int updateSnHex(){
		String str = "update simnode a, card_basic_info b set b.sn_hex=a.ICCID where hex(a.ICCID)=b.sn and b.sn_hex is null";
		return this.updateBySql(str, null);
	}
	
	/**
	 * 
	 * @Description 修改card_basic_info表的sn_hex字段
	 * @param iccIds
	 * @return int  
	 * @author yifang.huang
	 * @date 2017年4月27日 下午3:52:03
	 */
	public int updateSnHex(List<String> iccIds){
		String str = "update card_basic_info a, simnode b set a.sn_hex=b.ICCID where a.sn=hex(b.ICCID) and hex(b.ICCID) IN (:p1)";
		return this.updateBySql(str, new Parameter(iccIds));
	}
	
	/**
	 * 
	 * @Description 修改card_basic_info表的sn_hex字段
	 * @param iccId
	 * @return int  
	 * @author yifang.huang
	 * @date 2017年4月27日 下午3:52:52
	 */
	public int updateSnHex(String iccId){
		String str = "update card_basic_info a, simnode b set a.sn_hex=b.ICCID where a.sn=hex(b.ICCID) and hex(b.ICCID)=:p1";
		return this.updateBySql(str, new Parameter(iccId));
	}
	
	/**
	 * 
	 * @Description 根据卡类型修改card_basic_info,simnode表的source_type字段
	 * @param cardType
	 * @param sourceType
	 * @return int  
	 * @author yifang.huang
	 * @date 2017年5月9日 下午3:35:01
	 */
	public int updateSourceTypeByCardType(String cardType, String sourceType) {
		// 修改card_basic_info
		String str1 = "update card_basic_info a set a.source_type=:p1 where a.type=:p2";
		this.updateBySql(str1, new Parameter(sourceType, cardType));
		// 修改simnode
		String str2 = "update simnode a, card_basic_info b set a.source_type=:p1 where a.ICCID=b.sn_hex and b.type=:p2";
		return this.updateBySql(str2, new Parameter(sourceType, cardType));
	}
	
	/**
	 * 
	 * @Description 根据卡类型修改simnode表的allowed_source字段
	 * @param cardType
	 * @param allowedSource
	 * @return int  
	 * @author yifang.huang
	 * @date 2017年5月9日 下午3:35:01
	 */
	public int updateAllowedSourceByCardType(String cardType, String allowedSource) {
		// 修改simnode
		String str = "update simnode a, card_basic_info b set a.allowed_source=:p1 where a.ICCID=b.sn_hex and b.type=:p2";
		return this.updateBySql(str, new Parameter(allowedSource, cardType));
	}
	
	/**
	 * 
	 * @Description 根据卡号card_basic_info,simnode表的source_type字段
	 * @param sn
	 * @param sourceType
	 * @return int  
	 * @author yifang.huang
	 * @date 2017年5月9日 下午3:35:01
	 */
	public int updateSourceTypeByCardSn(String sn, String sourceType) {
		// 修改card_basic_info
		String str1 = "update card_basic_info a set a.source_type=:p1 where a.sn=:p2";
		this.updateBySql(str1, new Parameter(sourceType, sn));
		// 修改simnode
		String str2 = "update simnode a set a.source_type=:p1 where hex(a.ICCID)=:p2";
		return this.updateBySql(str2, new Parameter(sourceType, sn));
	}
	
	/**
	 * 
	 * @Description 查询有效期预警数据
	 * @return List<Map<String, Object>>  
	 * @author yifang.huang
	 * @date 2016年5月18日 上午11:11:43
	 */
	public List<Map<String, Object>> findListForValidityEarlyWarning() {
		
		String sql = "select hex(sn.ICCID) ICCID, sn.SIMBANKID, sn.SIMID, sn.SIMCARDVALIDDAY, sn.stamp_firstactive FIRSTACTIVE, sn.DATACAP, sn.DATAUSED, cbi.type "
				+ "from simnode sn, card_basic_info cbi where hex(sn.ICCID)=cbi.sn and sn.SIMCARDVALIDDAY is not null and sn.stamp_firstactive!='0000-00-00 00:00:00' and sn.USIMSTATUS!=0 and TIMESTAMPDIFF(DAY, sn.stamp_firstactive, NOW())>=sn.SIMCARDVALIDDAY;";
		
		return findBySql(sql, null, Map.class);
	}
	
	/**
	 * 
	 * @Description 查询流量预警数据
	 * @param limitData 剩余流量界限
	 * @return List<Map<String, Object>>  
	 * @author yifang.huang
	 * @date 2016年5月18日 上午11:16:57
	 */
	public List<Map<String, Object>> findListForRateOfFlowEarlyWarning(long limitData) {
		
		String sql = "select hex(sn.ICCID) ICCID, sn.SIMBANKID, sn.SIMID, sn.SIMCARDVALIDDAY, sn.stamp_firstactive FIRSTACTIVE, format(max(sn.DATACAP)/1024/1024, 2) DATACAP, format(max(sn.DATAUSED)/1024/1024, 2) DATAUSED, cbi.type "
				+ "from simnode sn, card_basic_info cbi where hex(sn.ICCID)=cbi.sn and sn.USIMSTATUS!=0 and (sn.DATAUSED+"+limitData+")>=sn.DATACAP";
		
		return findBySql(sql, null, Map.class);
	}
	
	/**
	 * 
	 * @Description 查询主控版ID列表
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2016年5月27日 上午11:03:21
	 */
	public List<Map<String, Object>> findSimBankIdList() {
		
		String sql = "select distinct(simbankid) simBankId from simnode order by simbankid asc";
		
		return findBySql(sql, null, Map.class);
	}
	
	/**
	 * 
	 * @Description 根据主控版ID查询卡槽编号列表
	 * @param simBankId
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2016年5月27日 上午11:04:08
	 */
	public List<Map<String, Object>> findSimIdListBySimBankId(String simBankId) {
		
		if (StringUtils.isBlank(simBankId))
			return null;
		
		String sql = "select sn.simid simId, sn.USIMSTATUS simStatus, hex(sn.iccid) iccId, cbi.type type "
				+ "from simnode sn left join card_basic_info cbi on hex(sn.ICCID)=cbi.sn where sn.SIMBANKID='"+simBankId+"' order by sn.simid asc";
		
		return findBySql(sql, null, Map.class);
	}
	
	/**
	 * 
	 * @Description 根据主控版ID和卡槽Id查询卡槽编号列表
	 * @param simBankId
	 * @param simId
	 * @return Map<String,Object>  
	 * @author yifang.huang
	 * @date 2016年12月22日 上午10:08:09
	 */
	public Map<String, Object> findSimNode(String simBankId, String simId) {
		
		if (StringUtils.isBlank(simBankId) || StringUtils.isBlank(simId))
			return null;
		
		String sql = "select sn.USIMSTATUS simStatus, hex(sn.iccid) iccId, cbi.type type "
				+ "from simnode sn left join card_basic_info cbi on hex(sn.ICCID)=cbi.sn "
				+ "where sn.SIMBANKID='"+simBankId+"' and sn.SIMID='"+simId+"'";
		
		List<Map<String,Object>> listMap = findBySql(sql, null, Map.class);
		if (listMap!=null && listMap.size()>0)
			return listMap.get(0);
		
		return null;
	}
	
	/**
	 * 查询有效期激活时间有问题的卡槽列表
	 * 
	 * @athor shuxin
	 * @date 2016年6月17日上午11:15:34
	 * @return
	 * List<Map<String,Object>> 
	 */
	public List<Map<String, Object>> findValidPeriodSimList(String cardTypes){
		//String sql = "SELECT HEX(t.ICCID) iccid,t.stamp_firstactive FROM simnode t where t.SIMCARDVALIDDAY IS NOT NULL  AND t.USIMSTATUS != 0 AND (t.stamp_firstactive = '0000-00-00 00:00:00' or t.stamp_firstactive IS NULL)";
		String sql = "";
		if (StringUtils.isBlank(cardTypes))
			sql = "SELECT HEX(t.ICCID) iccid,t.stamp_firstactive FROM simnode t where t.SIMCARDVALIDDAY IS NOT NULL  AND t.USIMSTATUS != 0";
		else 
			sql = "SELECT HEX(t.ICCID) iccid, t.stamp_firstactive FROM simnode t, card_basic_info cbi WHERE hex(t.ICCID)=cbi.sn AND t.SIMCARDVALIDDAY IS NOT NULL AND cbi.type in('"+cardTypes+"') AND t.USIMSTATUS != 0";
		
		return findBySql(sql, null, Map.class);
	}
	
	/**
	 * 根据ids和status查找卡状态列表
	 * 
	 * @athor shuxin
	 * @date 2016年6月17日下午12:48:33
	 * @param ids
	 * @param status
	 * @return
	 * List<Map<String,Object>> 
	 */
	public List<Map<String, Object>> findSimcardStatusListByIccidsAndStatus(String ids, Integer status){
		StringBuffer buffer = new StringBuffer("SELECT t.iccid,t.stamp_created FROM (");
		buffer.append("SELECT HEX(t.ICCID) as iccid,t.stamp_created as stamp_created FROM simcardstatus t where HEX(t.ICCID) in (");
		buffer.append(ids);
		buffer.append(") AND t.USIMSTATUS = ");
		buffer.append(status);
		buffer.append(" ORDER BY t.stamp_created ASC) t GROUP BY t.iccid");
		return findBySql(buffer.toString(), null, Map.class);
	}
	
	/**
	 * 批量修改sim卡第一次激活时间
	 * @athor shuxin
	 * @date 2016年6月17日下午10:27:47
	 * @param sims
	 * void 
	 */
	public void batchUpdateSimNode(List<Map<String, Object>> sims){
		for (Map<String, Object> map : sims) {
			String ccid = map.get("iccid").toString();
			String createTime = map.get("stamp_created").toString();
			String sql = "UPDATE simnode t SET t.stamp_firstactive = '"+ createTime +"' WHERE HEX(t.ICCID) =  '"+ccid+"' AND t.USIMSTATUS != 0";
			createSqlQuery(sql, null).executeUpdate();
		}
	}
	
	/**
	 * 根据iccid和sim卡的状态（默认为0）
	 * @athor shuxin
	 * @date 2016年6月21日上午10:29:11
	 * @param iccids
	 * @return
	 * List<Map<String,Object>> 
	 */
	public List<Map<String, Object>> findSimnodesByStatusAndIccids(String iccid){
		String sql ="SELECT HEX(t.IMSI) imsi, t.SIMBANKID SIMBANKID, t.SIMID SIMID, t.stamp_firstactive activeDate FROM simnode t WHERE HEX(t.iccid)='"+iccid +"'  AND t.USIMSTATUS != 0";
		return findBySql(sql, null, Map.class);
	}
	
	/**
	 * 
	 * @Description 取卡信息
	 * @param iccIdList
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2016年10月25日 下午2:58:08
	 */
	public List<Map<String, Object>> findSimNode(List<String> iccIdList){
		String sql ="SELECT HEX(t.ICCID) iccId, t.SIMBANKID SIMBANKID, t.SIMID SIMID FROM simnode t WHERE HEX(t.iccid) IN (:p1) AND t.USIMSTATUS != 0";
		return findBySql(sql, new Parameter(iccIdList), Map.class);
	}
	
	/**
	 * 
	 * @Description 根据卡编号取超流量且状态为6的卡信息
	 * @param iccId 卡编号
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2016年10月14日 上午11:44:34
	 */
	public List<Map<String, Object>> findExceedFlowAndStatusIs6ByIccId(String iccId){
		
		StringBuffer sql = new StringBuffer("SELECT HEX(t.ICCID) iccId, t.SIMBANKID SIMBANKID, t.SIMID SIMID FROM simnode t "
				+ "WHERE t.USIMSTATUS=6 AND t.DATAUSED>t.DATACAP AND t.ICCID=UNHEX('" + iccId + "')");
		
		return findBySql(sql.toString(), null, Map.class);
	}
	
	/**
	 * 批量修改sim卡apn信息
	 * @athor shuxin
	 * @date 2016年6月22日上午9:32:28
	 * @param list
	 * void 
	 */
	public void batchUpdateSimnodeAPN(List<Map<String, String>> list){
		for (Map<String, String> map : list) {
			String iccid = map.get("iccid");
			String apn = map.get("apn");
			String sql = "UPDATE simnode t SET t.APN_INFO = '"+ apn +"' WHERE HEX(t.ICCID) =  '"+iccid+"' AND t.USIMSTATUS != 0";
			createSqlQuery(sql, null).executeUpdate();
		}
	}
	
	/**
	 * 卡数据恢复
	 * @Description 
	 * @param stampFirstActive
	 * @param dataUsed
	 * @param sn 
	 * @return void  
	 * @author yuxiaoyu
	 * @date 2016年12月30日 上午10:02:54
	 */
	public void recoverySimnode(String stampFirstActive, String dataUsed, String sn) {
		Double d = Double.parseDouble(dataUsed);
		d *= 1048576;//1024 * 1024
		String sql = "UPDATE card_basic_info a, simnode b SET b.stamp_firstactive = '" + stampFirstActive + "', b.DATAUSED = " + d
				+ " WHERE a.sn = HEX(b.ICCID) and a.sn ='" + sn + "' AND USIMSTATUS != 0";
		createSqlQuery(sql, null).executeUpdate();
	}
	
	/**
	 * 
	 * @Description 取卡信息
	 * @param cardType
	 * @param status
	 * @param isOverFlow 是否超出流量，为null表示不做是否超出流量判断
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2016年9月19日 上午11:39:03
	 */
	public List<Map<String, Object>> findSimNode(String cardType, Integer status, Boolean isOverFlow){
		
		StringBuffer sql = new StringBuffer();
		
		if (StringUtils.isNotBlank(cardType))
			sql.append("SELECT HEX(A.UEID) UEID, A.SIMBANKID, A.SIMID, (TO_DAYS(NOW())-TO_DAYS(A.STAMP_FIRSTACTIVE)) DAYS, A.SIMCARDVALIDDAY FROM SIMNODE A, CARD_BASIC_INFO B WHERE A.ICCID=B.SN_HEX AND B.TYPE='" + cardType + "'");
		else
			sql.append("SELECT HEX(A.UEID) UEID, A.SIMBANKID, A.SIMID, (TO_DAYS(NOW())-TO_DAYS(A.STAMP_FIRSTACTIVE)) DAYS, A.SIMCARDVALIDDAY FROM SIMNODE A WHERE 1=1");
			
		if (status != null)
			sql.append(" AND A.USIMSTATUS=" + status);
		
		if (isOverFlow != null) {
			if (isOverFlow)
				sql.append(" AND A.DATAUSED>A.DATACAP");
			else
				sql.append(" AND A.DATAUSED<=A.DATACAP");
		}
		
		sql.append(" AND A.USIMSTATUS!=0");
		
		return findBySql(sql.toString(), null, Map.class);
	}
	
	/**
	 * 
	 * @Description 取状态为3且stamp_updated在‘updatDate’之前的数据
	 * @param updatDate
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2016年11月22日 下午5:30:31
	 */
	public List<Map<String, Object>> findSimNode(String updatDate){
		
		String sql = "SELECT HEX(A.UEID) UEID, A.SIMBANKID, A.SIMID FROM SIMNODE A WHERE A.USIMSTATUS=3 AND A.stamp_updated<=str_to_date('" + updatDate + "','%Y-%m-%d %H:%i:%s')";
		
		return findBySql(sql.toString(), null, Map.class);
	}
	
	
	
	/**
	 * 
	 * @Description 取卡信息
	 * @param isOverFlow 是否超出流量，为null表示不做是否超出流量判断
	 * @param isLongTermCard 是否长期卡，为null表示不做是否长期卡判断
	 * @param haveResetTime 是否有流量重置时间，为null表示不做是否有流量重置时间判断
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2016年9月19日 上午11:39:03
	 */
	public List<Map<String, Object>> findSimNode(Boolean isOverFlow, Boolean isLongTermCard, Boolean haveResetTime){
		
		StringBuffer sql = new StringBuffer("SELECT HEX(A.UEID) UEID, A.SIMBANKID, A.SIMID FROM SIMNODE A, CARD_BASIC_INFO B, SIM_CARD_TYPE C WHERE A.ICCID=B.SN_HEX AND B.TYPE=C.CARD_TYPE");
		
		if (isLongTermCard != null) {
			if (isLongTermCard)
				sql.append(" AND C.VALID_DAYS=-1");
			else
				sql.append(" AND C.VALID_DAYS>-1");
		}
		
		if (haveResetTime != null) {
			if (haveResetTime)
				sql.append(" AND C.CLEAR_DAY IS NOT NULL");
			else
				sql.append(" AND C.CLEAR_DAY IS NULL");
		}
		
		if (isOverFlow != null) {
			if (isOverFlow)
				sql.append(" AND A.DATAUSED>A.DATACAP");
			else
				sql.append(" AND A.DATAUSED<=A.DATACAP");
		}
		
		sql.append(" AND A.USIMSTATUS!=0");
		
		return findBySql(sql.toString(), null, Map.class);
	}
	
	/**
	 * 
	 * @Description 根据条件取mifiNode数据
	 * @param ueId
	 * @param simBankId
	 * @param simId
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2016年9月19日 下午2:11:14
	 */
	public List<Map<String, Object>> findMifiNode(String ueId, Integer simBankId, Integer simId){
		
		StringBuffer sql = new StringBuffer("SELECT A.ID FROM MIFINODE A WHERE 1=1");
		if (ueId != null)
			sql.append(" AND HEX(A.UEID)='" + ueId + "'");
		if (simBankId != null)
			sql.append(" AND A.SIMBANKID=" + simBankId);
		if (simId != null)
			sql.append(" AND A.SIMID=" + simId);
		
		return findBySql(sql.toString(), null, Map.class);
	}

	/**
	 * 
	 * @Description 查询过期的卡信息
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2016年9月20日 上午10:17:45
	 */
	public List<Map<String, Object>> findOverdueCard(){
		
		StringBuffer sql = new StringBuffer("SELECT B.SIMBANKID, B.SIMID FROM (SELECT A.SIMBANKID, A.SIMID, "
				+ "TO_DAYS(NOW())-TO_DAYS(A.STAMP_FIRSTACTIVE) DAYS, A.SIMCARDVALIDDAY FROM SIMNODE A "
				+ "WHERE A.SIMCARDVALIDDAY IS NOT NULL AND A.STAMP_FIRSTACTIVE!='0000-00-00 00:00:00' "
				+ "AND A.USIMSTATUS!=4 AND A.USIMSTATUS!=0 AND HEX(A.IMSI)!='000000000000000000') AS B "
				+ "WHERE B.DAYS>B.SIMCARDVALIDDAY");
		
		return findBySql(sql.toString(), null, Map.class);
	}
	
	/**
	 * 
	 * @Description 根据卡板编号查询卡板所连接的服务IP及端口
	 * @param simBankId
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年10月13日 下午2:34:38
	 */
	public String findServicerIpBySimBankId(Integer simBankId) {
		
		if (simBankId == null)
			return null;
		
		String sql = "select a.ONLINEIP onlineIp from simbank_ip a where a.SIMBANKID=" + simBankId + " limit 1";
		
		List<Map<String, String>> listMap = findBySql(sql.toString(), null, Map.class);
		if (listMap!=null && listMap.size()>0) {
			Map<String, String> map = listMap.get(0);
			return map.get("onlineIp");
		}
		
		return null;
		
	}

	/**
	 * 
	 * @Description 按卡类型统计卡在线数量、可用数量、 被拒的数量、 被锁的数量
	 * @param cardType 卡类型
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2016年10月19日 下午1:44:48
	 */
	public List<Map<String, Object>> findCardTypeStatList(String cardType) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("select c.card_type cardType,c.card_type_name cardTypeName,c.mcc,sum(if(a.usimstatus='2',1,0)) as freeCnt,sum(if(a.usimstatus='3',1,0)) as usedCnt,SUM(IF(a.USIMSTATUS = '4', 1, 0)) AS block, SUM(IF(a.USIMSTATUS = '6', 1, 0)) AS refuse");
		sb.append(" FROM sim_card_type c left join card_basic_info b on c.card_type = b.type left join simnode a on b.sn_hex = a.iccid");
		sb.append(" where 1=1 and (a.usimstatus='2' or a.usimstatus='3' or a.usimstatus='4' or a.usimstatus='6')");
		
		if (StringUtils.isNotBlank(cardType)) {
			sb.append(" and c.card_type = '" + cardType + "'");
		}
		
		sb.append(" group by c.card_type,c.card_type_name");
		
		return findBySql(sb.toString(), null, Map.class);
	}
	
	/**
	 *  按卡类型统计卡在线数量、可用数量、 被拒的数量、 被锁的数量，按国家过滤
	 * @Description 
	 * @param mccList
	 * @return 
	 * @return List<Map<String,Object>>  
	 * @author yuxiaoyu
	 * @date 2016年12月19日 下午5:01:39
	 */
	public List<Map<String, Object>> findCardTypeStatList(List<String> mccList) {
		String sql = "select c.card_type cardType,c.card_type_name cardTypeName,c.mcc,sum(if(a.usimstatus='2',1,0)) as freeCnt,sum(if(a.usimstatus='3',1,0)) as usedCnt,SUM(IF(a.USIMSTATUS = '4', 1, 0)) AS block, SUM(IF(a.USIMSTATUS = '6', 1, 0)) AS refuse"
				+" FROM sim_card_type c left join card_basic_info b on c.card_type = b.type left join simnode a on b.sn_hex = a.iccid"
				+" where 1=1 and (a.usimstatus='2' or a.usimstatus='3' or a.usimstatus='4' or a.usimstatus='6') ";
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(sql);
		if (null != mccList && 0 < mccList.size()) {
			stringBuilder.append(" and (");
			for(String mcc : mccList){
				stringBuilder.append(" c.mcc like '%").append(mcc).append("%' or");
			}
			sql = stringBuilder.substring(0, stringBuilder.length() - 2);
			stringBuilder.setLength(0);
			stringBuilder.append(sql).append(")").append(" group by c.card_type,c.card_type_name");
		}else{
			stringBuilder.append(" group by c.card_type,c.card_type_name");
		}
		return findBySql(stringBuilder.toString(), null, Map.class);
	}

	/**
	 * 
	 * @Description 根据卡号查询卡信息
	 * @param iccid
	 * @return CardBasicInfo  
	 * @author yifang.huang
	 * @date 2016年12月5日 下午4:17:55
	 */
	public CardBasicInfo findByIccid(String iccid){
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("sn", iccid));
		List<CardBasicInfo> lists = find(dc);
		return lists.size() > 0 ? lists.get(0) : null;
	}

	/**
	 * 
	 * @Description 卡用量统计
	 * @param page
	 * @param paramMap
	 * @return Page<HashMap>  
	 * @author yifang.huang
	 * @date 2016年12月20日 下午4:59:42
	 */
	public Page<HashMap> findCardTypeStatList(Page<HashMap> page, Map<String, Object> paramMap, String[] typeArr) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from ( ");
		sb.append(
				"select c.card_type,c.card_type_name,c.mcc,sum(if(a.usimstatus='2',1,0)) as freeCnt,sum(if(a.usimstatus='3',1,0)) as usedCnt,SUM(IF(a.USIMSTATUS = '4', 1, 0)) AS block, SUM(IF(a.USIMSTATUS = '6', 1, 0)) AS refuse");
		sb.append(
				" FROM sim_card_type c left join card_basic_info b on c.card_type = b.type left join simnode a on b.sn_hex = a.iccid");
		sb.append(" where 1=1 and (a.usimstatus='2' or a.usimstatus='3' or a.usimstatus='4' or a.usimstatus='6') ");
		if (null != typeArr && 0 < typeArr.length) {
			appendTypeCriteria(typeArr, sb);
		}
		sb.append(" group by c.card_type,c.card_type_name ");
		sb.append(" ) as _t ");
		return findBySql(page, sb.toString(), null, Map.class);
	}
	
	private void appendTypeCriteria(String[] typeArr, StringBuffer sb) {
		if (1 == typeArr.length && "null".equals(typeArr[0])) {
			sb.append(" and c.card_type is null");
		} else {
			StringBuilder typeBuilder = new StringBuilder();
			Boolean containsNull = false;
			for (String type : typeArr) {
				if ("null".equals(type)) {
					containsNull = true;
					continue;
				}
				typeBuilder.append(Constants.SINGLE_QUOTES).append(type).append(Constants.SINGLE_QUOTES).append(Constants.COMMA);
			}
			String typeStr = typeBuilder.substring(0, typeBuilder.length() - 1);
			typeBuilder.setLength(0);
			typeBuilder.append(typeStr);
			if (containsNull) {
				sb.append(" and (c.card_type in (").append(typeBuilder).append(") or a.type is null)");
			} else {
				sb.append(" and c.card_type in (").append(typeBuilder).append(")");
			}
		}
	}
	
	/**
	 * 统计所有的卡槽sim在线数量和可用数量
	 * @athor shuxin
	 * @date 2016年6月23日上午10:11:43
	 * @return
	 * List<Map<String,Object>> 
	 */
	public  Map<String, Object> statAllSimFreeAndUsed(Map<String, Object> paramMap, String[] typeArr){
		Map<String, Object> map = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SUM(t.freeCnt) as freeCnt,SUM(t.usedCnt) as usedCnt,SUM(t.block) as block,SUM(t.refuse) as refuse FROM (");
		sb.append(" select _t.freeCnt,_t.usedCnt,_t.block,_t.refuse from ( ");
		sb.append(
				"select c.card_type,c.card_type_name,sum(if(a.usimstatus='2',1,0)) as freeCnt,sum(if(a.usimstatus='3',1,0)) as usedCnt,sum(if(a.usimstatus='4',1,0)) as block,sum(if(a.usimstatus='6',1,0)) as refuse");
		sb.append(
				" FROM sim_card_type c left join card_basic_info b on c.card_type = b.type left join simnode a on b.sn_hex = a.iccid");
		sb.append(" where 1=1 and (a.usimstatus='2' or a.usimstatus='3' or a.usimstatus='4' or a.usimstatus='6') ");
		if (null != typeArr && 0 < typeArr.length) {
			appendTypeCriteria(typeArr, sb);
		}
		sb.append(" group by c.card_type,c.card_type_name ");
		sb.append(" ) as _t  ) t");
		List<Map<String, Object>> list = findBySql(sb.toString(), null, Map.class);
		map.put("freeCnt", list.get(0).get("freeCnt"));
		map.put("usedCnt", list.get(0).get("usedCnt"));
		map.put("block", list.get(0).get("block"));
		map.put("refuse", list.get(0).get("refuse"));
		return map;
	}
	
	/**
	 * 根据卡号清空已使用流量
	 * 
	 * @Description
	 * @author yuxiaoyu
	 * @date 2016年5月4日 上午9:59:26
	 */
	public void updateClearDataBySn(List<String> snList) {
		String sql = "update simnode set dataused = 0 where USIMSTATUS!=4 and hex(iccid) in (:p1)";
		updateBySql(sql, new Parameter(snList));
	}
}
