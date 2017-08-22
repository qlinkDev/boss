package com.uu.modules.mifi.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;

import com.uu.common.config.Global;
import com.uu.common.mapper.JsonMapper;
import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.Socekt;
import com.uu.common.utils.excel.ImportExcel;
import com.uu.modules.mifi.dao.CardManageDao;
import com.uu.modules.mifi.dao.MifiOrderDao;
import com.uu.modules.mifi.dao.SimCardTypeDao;
import com.uu.modules.mifi.entity.CardBasicInfo;
import com.uu.modules.mifi.entity.SimCardType;
import com.uu.modules.sys.utils.UserUtils;
import com.uu.modules.utils.Constants;

import net.sf.json.JSONObject;

@Service
public class CardManageService extends BaseService {

	public static Logger logger = LoggerFactory.getLogger(CardManageService.class);

	// 卡的状态
	public static final Integer SIMCARDTYPE = 3;

	@Autowired
	private CardManageDao cardManageDao;

	@Autowired
	private SimCardTypeDao simCardTypeDao;
	
	@Autowired
	private MifiTrafficService mifiTrafficSerivce;

	@Autowired
	private MifiOrderDao mifiOrderDao;
	
	public CardBasicInfo get(String id) {
		return cardManageDao.get(id);
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
		return cardManageDao.findCardTypeStatList(page, paramMap, typeArr);
	}

	public Page<HashMap> find(Page<HashMap> page, CardBasicInfo cardBasicInfo, String[] typeArr, String[] allowedSourceArr,
			String simbankid, String simid, String simStatus) {
		StringBuffer sb = new StringBuffer(
				"select a.*,concat(b.SIMBANKID,'') as sim_bank_id,concat(b.SIMID,'') as sim_id, b.USIMSTATUS, format(b.DATACAP/1024/1024,2) DATACAP,format(b.DATAUSED/1024/1024,2) DATAUSED,b.stamp_firstactive, b.allowedmcc from card_basic_info a");
		if ("1".equals(cardBasicInfo.getInBank())) {
			sb.append(" inner join simnode b on (a.sn = hex(b.iccid) and b.usimstatus != 0) ");
		} else {
			sb.append(" left join simnode b on (a.sn = hex(b.iccid) and b.usimstatus != 0) ");
		}
		if (allowedSourceArr!=null && allowedSourceArr.length>0) {
			sb.append(" left join sim_card_type d on a.type=d.card_type");
		}
		sb.append(" where 1 = 1 ");
		
		if (allowedSourceArr!=null && allowedSourceArr.length>0) {
			this.appendAllowedSourceCriteria(allowedSourceArr, sb);
		}
		
		if (StringUtils.isNotEmpty(simbankid)) {
			sb.append(" and b.SIMBANKID = '" + simbankid  + "'");
		}
		if (StringUtils.isNotEmpty(simid)) {
			sb.append(" and b.SIMID = '" + simid + "'");
		}
		if (StringUtils.isNotBlank(simStatus)) {
			sb.append(" and b.USIMSTATUS = " + simStatus);
		}

		if (null != typeArr && 0 < typeArr.length) {
			appendTypeCriteria(typeArr, sb);
		}
		if (StringUtils.isNotEmpty(cardBasicInfo.getSn())) {
			sb.append(" and a.sn like  '%" + cardBasicInfo.getSn()  + "%'");
		}
		if (StringUtils.isNotEmpty(cardBasicInfo.getBath())) {
			sb.append(" and a.bath = '" + cardBasicInfo.getBath() + "'");
		}
		if (StringUtils.isNotBlank(cardBasicInfo.getSourceType())) {
			sb.append(" and a.source_type='" + cardBasicInfo.getSourceType() + "'");
		}

		// 入库时间
		String createDateStart = cardBasicInfo.getCreateDateStart();
		if (StringUtils.isNotBlank(createDateStart)) {
			sb.append(" and date(a.create_time) >= str_to_date('" + createDateStart + "','%Y-%m-%d %H:%i:%s')");
		}
		String createDateEnd = cardBasicInfo.getCreateDateEnd();
		if (StringUtils.isNotBlank(createDateEnd)) {
			sb.append(" and date(a.create_time) <= str_to_date('" + createDateEnd + "','%Y-%m-%d %H:%i:%s')");
		}
		
		sb.append(" order by a.create_time desc ");
		return cardManageDao.findBySql(page, sb.toString(), null, Map.class);
	}

	public List<HashMap> findForExport(CardBasicInfo cardBasicInfo, String[] typeArr, String[] allowedSourceArr,
			String simbankid, String simid) {
		StringBuffer sb = new StringBuffer(
				"select a.type, a.sn, a.pin, a.puk, a.bath, a.supplier, a.create_time, b.stamp_firstactive,  hex(b.imsi) imsi, hex(b.iccid) iccid, b.apn_info, b.allowedmcc, b.SIMBANKID simBankId, b.SIMID simId, b.USIMSTATUS, format(b.DATACAP/1024/1024, 2) dataCap, format(b.DATAUSED/1024/1024, 2) dataUsed, hex(b.mcc) mcc, c.country_name_cn, c.country_name_en from card_basic_info a");
		if ("1".equals(cardBasicInfo.getInBank())) {
			sb.append(" inner join simnode b on (a.sn = hex(b.iccid) and b.usimstatus != 0) left join mcc_def c on  hex(b.mcc)=c.mcc");
		} else {
			sb.append(" left join simnode b on (a.sn = hex(b.iccid) and b.usimstatus != 0) left join mcc_def c on  hex(b.mcc)=c.mcc");
		}
		if (allowedSourceArr!=null && allowedSourceArr.length>0) {
			sb.append(" left join sim_card_type d on a.type=d.card_type");
		}
		//sb.append(" left join simnode b on (a.sn = hex(b.iccid) and b.usimstatus != 0) left join mcc_def c on  hex(b.mcc)=c.mcc");
		sb.append(" where 1 = 1 ");
		
		if (allowedSourceArr!=null && allowedSourceArr.length>0) {
			appendAllowedSourceCriteria(allowedSourceArr, sb);
		}
		
		if (allowedSourceArr!=null && allowedSourceArr.length>0) {
			appendAllowedSourceCriteria(allowedSourceArr, sb);
		}
		if (StringUtils.isNotEmpty(simbankid)) {
			sb.append(" and b.SIMBANKID = '" + simbankid  + "'");
		}
		if (StringUtils.isNotEmpty(simid)) {
			sb.append(" and b.SIMID = '" + simid + "'");
		}
		
		if (null != typeArr && 0 < typeArr.length) {
			appendTypeCriteria(typeArr, sb);
		}
		if (StringUtils.isNotEmpty(cardBasicInfo.getSn())) {
			sb.append(" and a.sn = '" + cardBasicInfo.getSn() + "'");
		}
		if (StringUtils.isNotEmpty(cardBasicInfo.getBath())) {
			sb.append(" and a.bath = '" + cardBasicInfo.getBath() + "'");
		}

		// 入库时间
		String createDateStart = cardBasicInfo.getCreateDateStart();
		if (StringUtils.isNotBlank(createDateStart)) {
			sb.append(" and date(a.create_time) >= str_to_date('" + createDateStart + "','%Y-%m-%d %H:%i:%s')");
		}
		String createDateEnd = cardBasicInfo.getCreateDateEnd();
		if (StringUtils.isNotBlank(createDateEnd)) {
			sb.append(" and date(a.create_time) <= str_to_date('" + createDateEnd + "','%Y-%m-%d %H:%i:%s')");
		}
		
		sb.append(" order by a.create_time desc ");
		return cardManageDao.findBySql(sb.toString(), null, Map.class);
	}
	
	private void appendTypeCriteria(String[] typeArr, StringBuffer sb) {
		if (1 == typeArr.length && "noBind".equals(typeArr[0])) {
			sb.append(" and a.type is null");
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
				sb.append(" and (a.type in (").append(typeBuilder).append(") or a.type is null)");
			} else {
				sb.append(" and a.type in (").append(typeBuilder).append(")");
			}
		}
	}
	
	private void appendAllowedSourceCriteria(String[] allowedSourceArr, StringBuffer sb) {
		sb.append(" and (");
		for (int i=0; i<allowedSourceArr.length; i++) {
			String allowedSource = allowedSourceArr[i];
			if (i == 0) {
				if ("ALL".equals(allowedSource))
					sb.append("d.allowed_source='" + allowedSource + "'");
				else
					sb.append("d.allowed_source like '%" + allowedSource + "%'");
			} else {
				if ("ALL".equals(allowedSource))
					sb.append(" or d.allowed_source='" + allowedSource + "'");
				else
					sb.append(" or d.allowed_source like '%" + allowedSource + "%'");
			}
		}
		sb.append(")");
	}

	/**
	 * 暂不支持修改
	 * 
	 * @param list
	 * @return
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public JSONObject importFile(List<CardBasicInfo> list) {
		int successNum = 0;
		int failureNum = 0;
		StringBuilder failureMsg = new StringBuilder();
		List<String> iccidList = new ArrayList<String>(); //存储iccid
		List<Map<String, Integer>> listMap =new ArrayList<Map<String,Integer>>(); //需要通知服务的卡列表
		HashMap usedSn = new HashMap();
		for (CardBasicInfo cardBasicInfo : list) {
			if (logger.isDebugEnabled()) {
				logger.debug("--------------------cardBasicInfo:" + JsonMapper.getInstance().toJson(cardBasicInfo));
			}
			if (StringUtils.isBlank(cardBasicInfo.getSn().trim())) {
				failureMsg.append("<br/>卡号不能为空;");
				failureNum++;
				continue;
			}
			if (cardBasicInfo.getSn().trim().indexOf("E14") != -1) {
				cardBasicInfo.setSn(cardBasicInfo.getSn().trim().replace(".", "").replace("E14", ""));
			}
			if (StringUtils.isBlank(cardBasicInfo.getType().trim())) {
				failureMsg.append("<br/>卡类型不能为空;");
				failureNum++;
				continue;
			}
			if (usedSn.containsKey(cardBasicInfo.getSn().trim())) {
				failureMsg.append("<br/>卡[卡号:" + cardBasicInfo.getSn() + "]已处理,请勿重复导入;");
				failureNum++;
				continue;
			}
			usedSn.put(cardBasicInfo.getSn().trim(), cardBasicInfo.getSn().trim());
			if (cardManageDao.findSimNodeInfoBySn(cardBasicInfo.getSn().trim()) == 0) {
				failureMsg.append("<br/>卡[卡号:" + cardBasicInfo.getSn() + "]尚未插入卡箱,请插入卡箱后导入;");
				failureNum++;
				continue;
			}
			SimCardType simCardType = simCardTypeDao.finByCardType(cardBasicInfo.getType().trim());
			if (null == simCardType) {
				failureMsg.append("<br/>卡[卡类型:" + cardBasicInfo.getType() + "]未配置,请配置此卡类型后导入;");
				failureNum++;
				continue;
			}
			CardBasicInfo _cardBasicInfo = cardManageDao.findCardBasicInfoBySn(cardBasicInfo.getSn());
			if (null != _cardBasicInfo) { // 如果已入库，则修改数据
				_cardBasicInfo.setType(cardBasicInfo.getType().trim());
				if (cardBasicInfo.getActiveTime() != null)
					_cardBasicInfo.setActiveTime(cardBasicInfo.getActiveTime());
				else
					_cardBasicInfo.setActiveTime(null);
				_cardBasicInfo.setSn(cardBasicInfo.getSn().trim());
				_cardBasicInfo.setImsi(cardBasicInfo.getImsi().trim());
				_cardBasicInfo.setIccid(cardBasicInfo.getIccid().trim());
				_cardBasicInfo.setPin(cardBasicInfo.getPin());
				_cardBasicInfo.setPuk(cardBasicInfo.getPuk());
				_cardBasicInfo.setBath(cardBasicInfo.getBath());
				_cardBasicInfo.setSupplier(cardBasicInfo.getSupplier());
				_cardBasicInfo.setSourceType(simCardType.getSourceType());
				cardManageDao.save(_cardBasicInfo);
			} else {
				cardBasicInfo.setSourceType(simCardType.getSourceType());
				cardManageDao.save(cardBasicInfo);
			}
			String iccId = cardBasicInfo.getSn().trim();
			Map<String, Integer> simMap = mifiTrafficSerivce.findSimBlankIdAndSimIDByIccidAndUsimtatus(iccId, 2); //查找有效simNode
			if(!simMap.isEmpty()){
				iccidList.add(cardBasicInfo.getSn().trim());
				listMap.add(simMap);
			}
			StringBuffer sb = new StringBuffer();
			sb.append("update simnode t set t.DATACAP = ").append(Math.round(Double.parseDouble(simCardType.getDataCap()) * 1024 * 1024 * 1024));
			sb.append(",t.SOURCE_TYPE='" + simCardType.getSourceType() + "'");
			sb.append(",t.ALLOWED_SOURCE='" + simCardType.getAllowedSource() + "'");
			if (StringUtils.isNotBlank(simCardType.getValidDays())) {
				if (!simCardType.getValidDays().equals("-1"))
					sb.append(",t.SIMCARDVALIDDAY = " + simCardType.getValidDays());
				else
					sb.append(",t.SIMCARDVALIDDAY = null");
			} else {
				sb.append(",t.SIMCARDVALIDDAY = null");
			}
			if (StringUtils.isNotBlank(ObjectUtils.toString(cardBasicInfo.getActiveTime()))) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String activeTime = sdf.format(cardBasicInfo.getActiveTime());
				sb.append(",t.stamp_firstactive = str_to_date('" + activeTime + "','%Y-%m-%d')");
			}

			if (StringUtils.isNotBlank(simCardType.getApnInfo())) {
				sb.append(",t.APN_INFO = '" + simCardType.getApnInfo() + "'");
			} else {
				sb.append(",t.APN_INFO = null");
			}
			// 如果使用人员类型为ALL则为simnode表allowedmcc字段赋值为simCardType的mcc
			// 如果使用人员类型为ABROAD_TO_HOME则为simnode表allowedmcc字段赋值为simCardType的mccNickname
			if ("ALL".equals(simCardType.getUsePeopleType()))
				sb.append(",t.ALLOWEDMCC='" + simCardType.getMcc() + "'");
			else
				sb.append(",t.ALLOWEDMCC='" + simCardType.getMccNickname() + "'");
				
			// sb.append(",t.DATAUSED=0");
			sb.append(" where hex(t.ICCID) = '" + cardBasicInfo.getSn().trim() + "' and t.usimstatus!=0");
			cardManageDao.updateBySql(sb.toString(), null);
		}
	
		successNum = list.size() - failureNum;
		if (failureNum > 0) {
			failureMsg.insert(0, ",失败[" + failureNum + "]条卡数据,导入失败信息如下:");
		}
		JSONObject obj = new JSONObject();
		obj.put("successNum", successNum);
		obj.put("failureNum", failureNum);
		obj.put("failureMsg", failureMsg.toString());
		obj.put("iccids", iccidList);
		obj.put("notice", listMap);
		
		return obj;
	}

	public Page<HashMap> findSyncCardInfoList(Page<HashMap> page, Map<String, Object> paramMap) {
		StringBuffer sb = new StringBuffer();
		sb.append("select id,hex(ICCID) as ICCID, SIMBANKID,SIMID,USIMSTATUS,stamp_updated,hex(imsi) as imsi ");
		sb.append(",if(DATE_FORMAT(t.stamp_firstactive,'%Y') = '0000',null,t.stamp_firstactive) stamp_firstactive ");
		sb.append(" from simnode t where t.usimstatus != 0 ");
		sb.append(" and hex(t.iccid) != '00000000000000000000' ");
		String iccid = ObjectUtils.toString(paramMap.get("iccid"));
		if (StringUtils.isNotBlank(iccid)) {
			sb.append(" and hex(t.ICCID) = '" + iccid + "' ");
		}
		String startDate = ObjectUtils.toString(paramMap.get("startDate"));
		if (StringUtils.isNotBlank(startDate)) {
			sb.append(" and date(t.stamp_updated) >= str_to_date('" + startDate + "','%Y-%m-%d')");
		}
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(endDate)) {
			sb.append(
					" and date(t.stamp_updated) < date_add(str_to_date('" + endDate + "','%Y-%m-%d'),interval 1 day)");
		}
		sb.append(" and not exists(select 1 from card_basic_info f where f.sn = hex(t.ICCID)) ");
		sb.append(" order by t.stamp_updated desc ");
		return cardManageDao.findBySql(page, sb.toString(), null, Map.class);
	}

	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int saveSyncCardInfo(Map<String, Object> paramMap) {
		String sn = ObjectUtils.toString(paramMap.get("sn"));
		CardBasicInfo _cardBasicInfo = cardManageDao.findCardBasicInfoBySn(sn);
		if (null == _cardBasicInfo) {
			CardBasicInfo cardBasicInfo = new CardBasicInfo();
			cardBasicInfo.setSn(sn);
			cardBasicInfo.setImsi(ObjectUtils.toString(paramMap.get("imsi")));
			String stamp_firstactive = ObjectUtils.toString(paramMap.get("stamp_firstactive"));
			if (StringUtils.isNotBlank(stamp_firstactive)) {
				cardBasicInfo.setActiveTime(DateUtils.parseDate(paramMap.get("stamp_firstactive")));
				cardBasicInfo.setClearTime(cardBasicInfo.getActiveTime());
			}
			cardBasicInfo.setCreateUser(UserUtils.getUser().getLoginName());
			cardManageDao.save(cardBasicInfo);
			return 1;
		} else {
			return 0;
		}
	}

	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public List<String> saveBatchSyncCardInfo(Map<String, Object> paramMap) {
		StringBuffer sb = new StringBuffer();
		// String user = UserUtils.getUser().getLoginName();
		// sb.append("insert into
		// card_basic_info(id,status,create_time,create_user,sn, imsi) ");
		// sb.append(" select replace(uuid(),'-',''),'" + CardBasicInfo.status_1
		// + "',now(),'" + user + "', hex(t.ICCID), hex(t.imsi) ");
		sb.append(
				"select hex(t.iccid) iccid, hex(t.imsi) imsi,if(DATE_FORMAT(t.stamp_firstactive,'%Y') = '0000',null,t.stamp_firstactive) stamp_firstactive ");
		sb.append(" from simnode t ");
		sb.append(" where t.usimstatus != 0 ");
		sb.append(" and hex(t.iccid) != '00000000000000000000' ");
		String iccid = ObjectUtils.toString(paramMap.get("iccid"));
		if (StringUtils.isNotBlank(iccid)) {
			sb.append(" and hex(t.ICCID) = '" + iccid + "' ");
		}
		String startDate = ObjectUtils.toString(paramMap.get("startDate"));
		if (StringUtils.isNotBlank(startDate)) {
			sb.append(" and date(t.stamp_updated) >= str_to_date('" + startDate + "','%Y-%m-%d')");
		}
		String endDate = ObjectUtils.toString(paramMap.get("endDate"));
		if (StringUtils.isNotBlank(endDate)) {
			sb.append(
					" and date(t.stamp_updated) < date_add(str_to_date('" + endDate + "','%Y-%m-%d'),interval 1 day)");
		}
		sb.append(" and not exists(select 1 from card_basic_info f where f.sn = hex(t.ICCID)) ");
		sb.append(" and not exists(select 1 from simnode k where hex(k.ICCID) = hex(t.ICCID) ");
		sb.append(" and k.usimstatus != 0 ");
		sb.append(" and hex(k.iccid) != '00000000000000000000' ");
		sb.append(" group by hex(k.ICCID) having count(*) > 1) ");
		List<HashMap> list = cardManageDao.findBySql(sb.toString(), null, Map.class);
		List<CardBasicInfo> cardBasicInfos = new ArrayList<CardBasicInfo>();
		List<String> iccIds = new ArrayList<String>();
		for (int i=0; i<list.size(); i++) {
			HashMap map = list.get(i);
			CardBasicInfo cardBasicInfo = new CardBasicInfo();
			cardBasicInfo.setSn((String) map.get("iccid"));
			cardBasicInfo.setImsi((String) map.get("imsi"));
			cardBasicInfo.setIccid((String) map.get("iccid"));
			if (StringUtils.isNotBlank(ObjectUtils.toString(map.get("stamp_firstactive")))) {
				cardBasicInfo.setActiveTime((Date) map.get("stamp_firstactive"));
				cardBasicInfo.setClearTime(cardBasicInfo.getActiveTime());
			}
			cardBasicInfo.setCreateUser(UserUtils.getUser().getLoginName());
			cardBasicInfos.add(cardBasicInfo);
			iccIds.add(cardBasicInfo.getSn());
		}
		cardManageDao.save(cardBasicInfos);

		return iccIds;
	}

	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void save(CardBasicInfo cardBasicInfo, String oldCardType) {
		cardBasicInfo.setUpdateTime(new Date());
		cardBasicInfo.setUpdateUser(UserUtils.getUser().getLoginName());
		SimCardType simCardType = simCardTypeDao.finByCardType(cardBasicInfo.getType());
		StringBuffer sb = new StringBuffer();

		// 是否有充值流量
		String dataCap = cardBasicInfo.getDataCap();
		if (StringUtils.isBlank(dataCap)) {
			sb.append("update simnode t set t.DATACAP = ")
					.append(Math.round(Double.parseDouble(simCardType.getDataCap()) * 1024 * 1024 * 1024));
		} else {
			long dataCapLon = Math.round(Double.parseDouble(dataCap) * 1024 * 1024);
			sb.append("update simnode t set t.DATACAP = (t.DATACAP + ").append(dataCapLon).append(")");
		}

		if (StringUtils.isNotBlank(simCardType.getValidDays())) {
			if (!simCardType.getValidDays().equals("-1"))
				sb.append(",t.SIMCARDVALIDDAY = " + simCardType.getValidDays());
			else
				sb.append(",t.SIMCARDVALIDDAY = null");
		} else {
			sb.append(",t.SIMCARDVALIDDAY = null");
		}

		if (StringUtils.isNotBlank(ObjectUtils.toString(cardBasicInfo.getActiveTime()))) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String activeTime = sdf.format(cardBasicInfo.getActiveTime());
			sb.append(",t.stamp_firstactive = str_to_date('" + activeTime + "','%Y-%m-%d %H:%i:%s')");
		}

		if (StringUtils.isNotBlank(simCardType.getApnInfo())) {
			sb.append(",t.APN_INFO = '" + simCardType.getApnInfo() + "'");
		} else {
			sb.append(",t.APN_INFO = null");
		}
		
		// 如果使用人员类型为ALL则为simnode表allowedmcc字段赋值为simCardType的mcc
		// 如果使用人员类型为ABROAD_TO_HOME则为simnode表allowedmcc字段赋值为simCardType的mccNickname
		if ("ALL".equals(simCardType.getUsePeopleType()))
			sb.append(",t.ALLOWEDMCC='" + simCardType.getMcc() + "'");
		else
			sb.append(",t.ALLOWEDMCC='" + simCardType.getMccNickname() + "'");
		
		// sb.append(",t.DATAUSED=0");
		sb.append(" where t.ICCID=UNHEX('" + cardBasicInfo.getSn() + "')");
		cardManageDao.updateBySql(sb.toString(), null);
		cardManageDao.save(cardBasicInfo);
		
		// 如果卡类型发生改变，则修改card_basic_info和simnod对应的source_type值
		if (!cardBasicInfo.getType().equals(oldCardType)) {
			cardManageDao.updateSourceTypeByCardSn(cardBasicInfo.getSn(), simCardType.getSourceType());
		}
	}

	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveForApi(CardBasicInfo cardBasicInfo) {
		cardBasicInfo.setUpdateTime(new Date());
		//cardBasicInfo.setUpdateUser(UserUtils.getUser().getLoginName());
		SimCardType simCardType = simCardTypeDao.finByCardType(cardBasicInfo.getType());
		StringBuffer sb = new StringBuffer();

		// 是否有充值流量
		String dataCap = cardBasicInfo.getDataCap();
		if (StringUtils.isBlank(dataCap)) {
			sb.append("update simnode t set t.DATACAP = ")
					.append(Math.round(Double.parseDouble(simCardType.getDataCap()) * 1024 * 1024 * 1024));
		} else {
			long dataCapLon = Math.round(Double.parseDouble(dataCap) * 1024 * 1024);
			sb.append("update simnode t set t.DATACAP = (t.DATACAP + ").append(dataCapLon).append(")");
		}

		if (StringUtils.isNotBlank(simCardType.getValidDays())) {
			if (!simCardType.getValidDays().equals("-1"))
				sb.append(",t.SIMCARDVALIDDAY = " + simCardType.getValidDays());
			else
				sb.append(",t.SIMCARDVALIDDAY = null");
		} else {
			sb.append(",t.SIMCARDVALIDDAY = null");
		}

		if (StringUtils.isNotBlank(ObjectUtils.toString(cardBasicInfo.getActiveTime()))) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String activeTime = sdf.format(cardBasicInfo.getActiveTime());
			sb.append(",t.stamp_firstactive = str_to_date('" + activeTime + "','%Y-%m-%d %H:%i:%s')");
		}

		if (StringUtils.isNotBlank(simCardType.getApnInfo())) {
			sb.append(",t.APN_INFO = '" + simCardType.getApnInfo() + "'");
		} else {
			sb.append(",t.APN_INFO = null");
		}
		
		// 如果使用人员类型为ALL则为simnode表allowedmcc字段赋值为simCardType的mcc
		// 如果使用人员类型为ABROAD_TO_HOME则为simnode表allowedmcc字段赋值为simCardType的mccNickname
		if ("ALL".equals(simCardType.getUsePeopleType()))
			sb.append(",t.ALLOWEDMCC='" + simCardType.getMcc() + "'");
		else
			sb.append(",t.ALLOWEDMCC='" + simCardType.getMccNickname() + "'");
		
		// sb.append(",t.DATAUSED=0");
		sb.append(" where t.ICCID=UNHEX('" + cardBasicInfo.getSn() + "')");
		cardManageDao.updateBySql(sb.toString(), null);
		cardManageDao.getSession().merge(cardBasicInfo);
	}

	/**
	 * 根据卡类型获取需要清空已使用流量的卡
	 * 
	 * @Description
	 * @author yuxiaoyu
	 * @date 2016年5月4日 上午10:15:26
	 */
	public List<CardBasicInfo> findByType(List<SimCardType> cardTypeList) {
		List<String> typeList = new ArrayList<String>();
		String cardType;
		for (SimCardType simCardType : cardTypeList) {
			cardType = com.uu.common.utils.StringUtils.emptyIfNull(simCardType.getCardType());
			typeList.add(cardType);
		}
		DetachedCriteria dc = cardManageDao.createDetachedCriteria();
		dc.add(Restrictions.in("type", typeList));
		return cardManageDao.find(dc);
	}

	/**
	 * 
	 * @Description 根据卡类型条件查询
	 * @param cardType
	 * @return List<CardBasicInfo>
	 * @author yifang.huang
	 * @date 2016年5月18日 下午4:53:19
	 */
	public List<CardBasicInfo> findByCardType(String cardType) {

		DetachedCriteria dc = cardManageDao.createDetachedCriteria();
		dc.add(Restrictions.eq("type", cardType));

		return cardManageDao.find(dc);
	}

	/**
	 * 根据卡号清空已使用流量
	 * 
	 * @Description
	 * @author yuxiaoyu
	 * @date 2016年5月4日 上午9:59:26
	 */
	//@Transactional(readOnly = false)
	public void updateClearDataBySn(List<String> snList) {
		cardManageDao.updateClearDataBySn(snList);
	}

	/**
	 * 
	 * @Description 查询有效期预警数据
	 * @return List<Map<String, Object>>
	 * @author yifang.huang
	 * @date 2016年5月18日 上午11:11:43
	 */
	public List<Map<String, Object>> findListForValidityEarlyWarning() {

		return cardManageDao.findListForValidityEarlyWarning();
	}

	/**
	 * 
	 * @Description 查询流量预警数据
	 * @param limitData
	 *            剩余流量界限
	 * @return List<Map<String, Object>>
	 * @author yifang.huang
	 * @date 2016年5月18日 上午11:16:57
	 */
	public List<Map<String, Object>> findListForRateOfFlowEarlyWarning(long limitData) {

		return cardManageDao.findListForRateOfFlowEarlyWarning(limitData);
	}

	/**
	 * 
	 * @Description 查询主控版ID列表
	 * @return List<Map<String,Object>>
	 * @author yifang.huang
	 * @date 2016年5月27日 上午11:03:21
	 */
	public List<Map<String, Object>> findSimBankIdList() {

		return cardManageDao.findSimBankIdList();
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

		return cardManageDao.findSimIdListBySimBankId(simBankId);
	}
	/**
	 * 统计
	 * @Description 
	 * @param simBankId
	 * @return 
	 * @return List<Map<String,String>>  
	 * @author wangsai
	 * @date 2017年1月10日 下午2:08:39
	 */
	public Page<HashMap> findSimIdListBySimBankId(Page<HashMap> page, Map<String, Object> paramMap) {
		String sqlString = "SELECT * FROM ( SELECT 	sn.SIMBANKID SIMBANKID, 	sn.NWSTATUS NWSTATUS,	COUNT(*)co, 	sum(IF(sn.USIMSTATUS !=  '0', 1, 0))numb FROM 	simnode sn LEFT JOIN card_basic_info cbi ON hex(sn.ICCID)= cbi.sn WHERE 	1 = 1 ";
		String simBankId=(String) paramMap.get("simBankId");
		if(StringUtils.isNotBlank(simBankId)){
		sqlString += " AND sn.SIMBANKID = '" + simBankId + "'";
		}
		String live=(String) paramMap.get("live");
		if(live.contains("3")!=true){
		if(StringUtils.isNotBlank(live)){
		sqlString += " AND sn.NWSTATUS = '" + live + "'";
		}}
		String type=(String) paramMap.get("type");
		if(type.contains("null")!=true){
			sqlString += "AND sn.SIMBANKID IN( SELECT 	a.SIMBANKID FROM 	simnode a, 	card_basic_info c WHERE 	hex(a.ICCID)=(c.sn) AND c.type ='" + type + "')";
			}
		sqlString += "GROUP BY sn.SIMBANKID  ) AS _t";
		return cardManageDao.findBySql(page,sqlString,Map.class);
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
		
		return cardManageDao.findSimNode(simBankId, simId);
		
	}
	

	/**
	 * sim卡（短期第一次激活时间有问题的数据simnode）重置激活时间
	 * 
	 * @athor shuxin
	 * @date 2016年6月17日下午2:42:14
	 * @return JSONObject
	 */
	//@Transactional(readOnly = false)
	public JSONObject updateResetSimActiveTime(String cardTypes) {
		JSONObject tips = new JSONObject();
		List<Map<String, Object>> simNodes = cardManageDao.findValidPeriodSimList(cardTypes);
		if (simNodes != null && !simNodes.isEmpty()) { // 查出短期有效期的卡
			StringBuffer buff = new StringBuffer();
			for (Map<String, Object> map : simNodes) {
				buff.append("'");
				buff.append(map.get("iccid").toString());
				buff.append("',");
			}
			if (buff.length() > 0) { // 查出需要激活时间重置的ids
				String ids = buff.substring(0, buff.lastIndexOf(",")).toString();
				List<Map<String, Object>> simCardStatus = cardManageDao.findSimcardStatusListByIccidsAndStatus(ids,
						SIMCARDTYPE);
				if (simCardStatus != null && !simCardStatus.isEmpty()) {// 有数据，则需要更新simnode里面的第一次激活时间
					cardManageDao.batchUpdateSimNode(simCardStatus);
					tips.put("code", "1");
					tips.put("msg", "已完成" + simCardStatus.size() + "张sim卡，激活时间重置成功！");
					return tips;
				}
			} else { // 没有激活的ids
				tips.put("code", "-1");
				tips.put("msg", "重置激活时间的sim卡不存在");
				return tips;
			}
		}
		// 提示没有数据需要激活时间重置
		tips.put("code", "-1");
		tips.put("msg", "重置激活时间的sim卡不存在");
		return tips;
	}

	/**
	 * 获取iccids
	 * 
	 * @athor shuxin
	 * @date 2016年6月21日下午2:02:43
	 * @param ei
	 * @return String
	 * @throws Exception
	 */
	//@Transactional(readOnly = false)
	public JSONObject updateAndGetIccidsString(ImportExcel ei) throws Exception {
		// 执行结果
		int successNum = 0;
		int failureNum = 0;
		StringBuilder failureMsg = new StringBuilder();
		List<Map<String, String>> anpList = new ArrayList<Map<String, String>>();
		int rowNum = ei.getDataRowNum();
		int lastRowNum = ei.getLastDataRowNum();
		for (int i = rowNum; i < lastRowNum; i++) {
			Row row = ei.getRow(i);
			String iccid = ObjectUtils.toString(ei.getCellValue(row, 0));
			if (StringUtils.isNotBlank(iccid)) {
				List<Map<String, Object>> list = cardManageDao.findSimnodesByStatusAndIccids(iccid); // 数据库中查找
				if (list == null || list.isEmpty() || list.size() > 1) {
					failureMsg.append("<br/>无效的iccid：" + iccid + "{第" + (i + 1) + "行};");
					failureNum++;
					continue;
				}
				String imsi = ObjectUtils.toString(list.get(0).get("imsi")); // 获取imsi
				if (imsi.length() != 18 && imsi.length() != 15) {
					failureMsg.append("<br/>ICCID：" + iccid + " 对应的IMSI：" + imsi + " 有错误;");
					failureNum++;
					continue;
				}
				JSONObject apns = getAPNInfoByImsi(imsi);
				// 根据mcc和mnc从xml中获取
				if (!apns.isEmpty()) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("iccid", iccid);
					String apn = "";
					String apn3G = this.getApnInfo(apns.getString("mcc"), apns.getString("mnc"),
							Global.getConfig("apn_3G"));
					if (!StringUtils.isNotBlank(apn3G)) { //如果为空的话，使用mnc_back去查找
						apn3G = this.getApnInfo(apns.getString("mcc"), apns.getString("mnc_back"),
								Global.getConfig("apn_3G"));
					}
					String apn4G = this.getApnInfo(apns.getString("mcc"), apns.getString("mnc"),
							Global.getConfig("apn_4G"));
					if (!StringUtils.isNotBlank(apn4G)) { //如果为空的话，使用mnc_back去查找
						apn4G = this.getApnInfo(apns.getString("mcc"), apns.getString("mnc_back"),
								Global.getConfig("apn_4G"));
					}
					if (StringUtils.isNotBlank(apn3G) && StringUtils.isNotBlank(apn4G)) {
						apn = apn3G + ";" + apn4G;
					} else {
						if (StringUtils.isNotBlank(apn3G)) {
							apn = apn3G;
						} else {
							apn = apn4G;
						}
					}
					if (!StringUtils.isNotBlank(apn)) {
						failureMsg.append("<br/><br/>ICCID：" + iccid + " 对应的IMSI：" + imsi + "，查不到APN信息");
						failureNum++;
						continue;
					}
					map.put("apn", apn);
					anpList.add(map);
				} else {
					failureMsg.append("<br/>ICCID：" + iccid + "解析IMSI：" + imsi + " 时出错;");
					failureNum++;
					continue;
				}
			}
		}
		cardManageDao.batchUpdateSimnodeAPN(anpList); //批量修改apn信息
		successNum = lastRowNum - failureNum - 2;
		JSONObject obj = new JSONObject();
		obj.put("successNum", successNum);
		obj.put("failureNum", failureNum);
		obj.put("failureMsg", failureMsg.toString());
		return obj;
	}
	
	/**
	 * 卡数据恢复
	 * @Description 
	 * @param ei
	 * @return
	 * @throws Exception 
	 * @return JSONObject  
	 * @author yuxiaoyu
	 * @date 2016年12月29日 下午4:31:36
	 */
	//@Transactional(readOnly = false)
	public int updateRecoverySimnode(ImportExcel ei) throws Exception {
		Row row;
		String sn,stampFirstActive,dataUsed;
		int rowNum = ei.getDataRowNum();
		int lastRowNum = ei.getLastDataRowNum();
		for (int i = rowNum; i < lastRowNum; i++) {
			row = ei.getRow(i);
			sn = ObjectUtils.toString(ei.getCellValue(row, 0));
			stampFirstActive = ObjectUtils.toString(ei.getCellValue(row, 2));
			dataUsed = ObjectUtils.toString(ei.getCellValue(row, 9));
			cardManageDao.recoverySimnode(stampFirstActive, dataUsed, sn);
		}
		return lastRowNum - rowNum;
	}

	/**
	 * 通过imsi获取APN信息
	 * 
	 * @athor shuxin
	 * @date 2016年6月21日下午2:58:49
	 * @param imsi
	 * @return String
	 */
	private JSONObject getAPNInfoByImsi(String imsi) {
		String regex = "(.{2})";
		JSONObject json = new JSONObject();
		if (imsi.length() == 15) { // 15位的imsi 处理方式
			json.put("mcc", imsi.substring(0, 3));
			json.put("mnc", imsi.substring(3, 5));
			json.put("mnc_back", imsi.substring(3, 6));
			return json;
		} else { // 18位的imsi处理方式
			imsi = imsi.replaceAll(regex, "$1 ");
			StringBuffer buffer = new StringBuffer();
			if (StringUtils.isNotBlank(imsi)) {
				String[] array = imsi.split(" ");
				if (array.length > 0) {
					for (int i = 0; i < array.length; i++) {
						String[] tempStr = array[i].replaceAll("(.{1})", "$1 ").split(" ");
						buffer.append(tempStr[1]);
						buffer.append(tempStr[0]);
					}
				}
			}
			json.put("mcc", buffer.substring(3, 6));
			json.put("mnc", buffer.substring(6, 8));
			json.put("mnc_back", buffer.substring(6, 9));
			return json;
		}
	}

	/**
	 * 获取apn信息
	 * 
	 * @athor shuxin
	 * @date 2016年6月21日下午6:16:11
	 * @param mcc
	 * @param mnc
	 * @param xmlName
	 *            void
	 * @throws Exception
	 * @throws DocumentException
	 */
	@SuppressWarnings("unchecked")
	private String getApnInfo(String mcc, String mnc, String xmlName) throws Exception {
		String apn = "";
		// 创建SAXReader对象
		SAXReader reader = new SAXReader();
		// 读取文件 转换成Document
		Document document;
		document = reader.read(new DefaultResourceLoader().getResource(xmlName).getFile());
		// 获取根节点元素对象
		Element root = document.getRootElement();
		String model = "";
		if (xmlName.contains("3G")) {
			model = "3G";
		} else {
			model = "4G";
		}
		// 遍历
		List<Element> childrenElment = root.elements();
		for (Element child : childrenElment) {
			if (mcc.equals(child.attributeValue("mcc")) && mnc.equals(child.attributeValue("mnc"))) {
				StringBuffer buffer = new StringBuffer(model);
				buffer.append(",");
				buffer.append(child.attributeValue("apn") == null ? "" : child.attributeValue("apn"));
				buffer.append(",");
				buffer.append(child.attributeValue("user") == null ? "" : child.attributeValue("user"));
				buffer.append(",");
				buffer.append(child.attributeValue("password") == null ? "" : child.attributeValue("password"));
				apn = buffer.toString();
				break;
			}
		}
		return apn;
	}
	
	/**
	 * 统计所有的卡槽sim在线数量和可用数量
	 * @athor shuxin
	 * @date 2016年6月23日上午10:11:43
	 * @return
	 * List<Map<String,Object>> 
	 */
	public  Map<String, Object>  statAllSimFreeAndUsed(Map<String, Object> paramMap, String[] typeArr){
		return cardManageDao.statAllSimFreeAndUsed(paramMap, typeArr);
	}

	/**
	 * 按国家统计卡用量
	 * @Description 
	 * @param paramMap
	 * @return Map<String, Map<String, Object>> 
	 * @author yifang.huang
	 * @date 2016年10月19日 下午12:09:44
	 */
	public Map<String, Map<String, Object>> cardStatisticsByCountry(List<String> dateList, String[] mccs) {
		// 国家过滤条件
		List<String> mccList = null;
		if (null != mccs && 0 != mccs.length) {
			mccList = new ArrayList<String>();
			String[] splitMcc;
			for (int i = 0; i < mccs.length; i++) {
				if(StringUtils.isBlank(mccs[i])){
					continue;
				}
				if (mccs[i].contains(Constants.COMMA)) {
					splitMcc = mccs[i].split(Constants.COMMA);
					mccList.addAll(Arrays.asList(splitMcc));
				} else {
					mccList.add(mccs[i]);
				}
			}
		}
		// 存储最终结果的Map
		Map<String, Map<String, Object>> resultMap = new HashMap<String, Map<String, Object>>();
		// 考虑到MCC（国家）可能有逗号分隔，难以使用sql直接统计，或纯sql可能很慢，所以先按卡类型统计，然后用java计算国家统计结果
		// 按卡类型统计卡使用情况，按照国家过滤。在sql中初筛国家减少数据量。mcc不能在sql中解析，无法完全过滤
		List<Map<String, Object>> cardMapList = cardManageDao.findCardTypeStatList(mccList);
		if (cardMapList == null || cardMapList.size() == 0) {
			return resultMap;
		}
		// 将按卡类型统计卡使用情况转转换成按国家统计
		for (Map<String, Object> cardMap : cardMapList) {
			resultMap = this.statCardType(resultMap, cardMap, mccList);// 按国家统计卡用量。在java中第二次按照国家过滤，完全过滤
		}
		if (0 == resultMap.size()) {
			return resultMap;
		}
		// 依次按照日期统计各国订单量
		List<Map<String, Object>> countryOrderMapList;
		for (String dateStr : dateList) {
			countryOrderMapList = mifiOrderDao.findCountryEquipmentCount(dateStr + "00:00:00", dateStr + "23:59:59", null);
			if (null == countryOrderMapList || 0 == countryOrderMapList.size()) {
				continue;
			}
			for (Map<String, Object> countryOrderMap : countryOrderMapList) {
				this.statOrderCount(resultMap, countryOrderMap, dateStr, mccList);// 统计单日订单量。在java中第二次按照国家过滤，完全过滤
			}
		}
		return resultMap;
	}

	/**
	 * 统计卡类型流量
	 * @Description 
	 * @param resultMap
	 * @param cardMap
	 * @param mccList
	 * @return 
	 * @return Map<String,Map<String,Object>>  
	 * @author yuxiaoyu
	 * @date 2016年12月19日 下午3:49:46
	 */
	private Map<String, Map<String, Object>> statCardType(Map<String, Map<String, Object>> resultMap, Map<String, Object> cardMap, List<String> mccList) {
		Map<String, Object> map = null;
		String mcc = ObjectUtils.toString(cardMap.get(Constants.MCC));
		if (StringUtils.isBlank(mcc)) {
			return resultMap;
		}
		List<Map<String, Object>> originalMccsList = mifiOrderDao.getOriginalMccs(mcc, mccList);
		if (null == originalMccsList || 0 == originalMccsList.size()) {
			return resultMap;
		}
		String key;
		String countryNameEn = StringUtils.EMPTY;
		String countryNameEnLast = StringUtils.EMPTY;
		int usedCnt, freeCnt, block, refuse;
		for (Map<String, Object> mccsMap : originalMccsList) {
			countryNameEn = ObjectUtils.toString(mccsMap.get(Constants.COUNTRY_NAME_EN));
			if (countryNameEnLast.equals(countryNameEn)) {// 英文名重复的国家跳过
				continue;
			}
			countryNameEnLast = countryNameEn;
			key = ObjectUtils.toString(mccsMap.get(Constants.COUNTRY_CODE));
			usedCnt = Integer.valueOf(ObjectUtils.toString(cardMap.get(Constants.USEDCNT)));
			freeCnt = Integer.valueOf(ObjectUtils.toString(cardMap.get(Constants.FREECNT)));
			block = Integer.valueOf(ObjectUtils.toString(cardMap.get(Constants.BLOCK)));
			refuse = Integer.valueOf(ObjectUtils.toString(cardMap.get(Constants.REFUSE)));
			map = resultMap.get(key);
			if (map == null) {
				map = new HashMap<String, Object>();
				map.put(Constants.COUNTRYNAME, ObjectUtils.toString(mccsMap.get(Constants.COUNTRY_NAME_CN)));
			} else {
				usedCnt = usedCnt + Integer.valueOf(ObjectUtils.toString(map.get(Constants.USEDCNT)));
				freeCnt = freeCnt + Integer.valueOf(ObjectUtils.toString(map.get(Constants.FREECNT)));
				block = block + Integer.valueOf(ObjectUtils.toString(map.get(Constants.BLOCK)));
				refuse = refuse + Integer.valueOf(ObjectUtils.toString(map.get(Constants.REFUSE)));
			}
			map.put(Constants.USEDCNT, usedCnt);
			map.put(Constants.FREECNT, freeCnt);
			map.put(Constants.BLOCK, block);
			map.put(Constants.REFUSE, refuse);
			resultMap.put(key, map);
			map = null;
		}
		return resultMap;
	}
	
	/**
	 * 统计单日订单量
	 * @Description 
	 * @param resultMap
	 * @param countryOrderMap
	 * @param mccList
	 * @param dateStr
	 * @return 
	 * @return Map<String,Map<String,Object>>  
	 * @author yuxiaoyu
	 * @date 2016年12月19日 下午3:49:36
	 */
	private Map<String, Map<String, Object>> statOrderCount(Map<String, Map<String, Object>> resultMap, Map<String, Object> countryOrderMap, 
			String dateStr, List<String> mccList) {
		Map<String, Object> map = null;
		String mcc = ObjectUtils.toString(countryOrderMap.get(Constants.MCC));
		if (StringUtils.isBlank(mcc)) {
			return resultMap;
		}
		List<Map<String, Object>> originalMccsList = mifiOrderDao.getOriginalMccs(mcc, mccList);
		if (null == originalMccsList || 0 == originalMccsList.size()) {
			return resultMap;
		}
		String key;
		String equCntTemp;
		String countryNameEn = StringUtils.EMPTY;
		String countryNameEnLast = StringUtils.EMPTY;
		int equCnt;
		for (Map<String, Object> mccsMap : originalMccsList) {
			countryNameEn = ObjectUtils.toString(mccsMap.get(Constants.COUNTRY_NAME_EN));
			if (countryNameEnLast.equals(countryNameEn)) {// 英文名重复的国家跳过
				continue;
			}
			countryNameEnLast = countryNameEn;
			key = ObjectUtils.toString(mccsMap.get(Constants.COUNTRY_CODE));
			equCnt = Integer.valueOf(ObjectUtils.toString(countryOrderMap.get(Constants.EQUCNT)));
			map = resultMap.get(key);
			if (map == null) {
				map = new HashMap<String, Object>();
				map.put(Constants.COUNTRYNAME, ObjectUtils.toString(mccsMap.get(Constants.COUNTRY_NAME_CN)));
				map.put(dateStr, equCnt);
			} else {
				equCntTemp = ObjectUtils.toString(map.get(dateStr));
				if (StringUtils.isNotBlank(equCntTemp)){
					equCnt = equCnt + Integer.valueOf(equCntTemp);
				}
				map.put(dateStr, equCnt);
			}
			resultMap.put(key, map);
			map = null;
		}
		return resultMap;
	}
	
	/**
	 * 根据simBlankId和simId查找sim卡信息
	 * @athor shuxin
	 * @date 2016年7月15日上午11:09:52
	 * @param simBlankId
	 * @param simId
	 * @return
	 * Map<String,Object> 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> findSIMBySimblankIdAndsimid(Integer simBlankId, Integer simId) {
		StringBuffer sb = new StringBuffer(
				"select a.*,concat(b.SIMBANKID,'') as sim_bank_id,concat(b.SIMID,'') as sim_id, b.USIMSTATUS, format(b.DATACAP/1024/1024,2) DATACAP,format(b.DATAUSED/1024/1024,2) DATAUSED,b.stamp_firstactive from card_basic_info a");
		sb.append(" inner join simnode b on (a.sn = hex(b.iccid) and b.usimstatus != 0) ");
		sb.append(" where 1 = 1 ");
		sb.append(" and b.SIMBANKID =").append(simBlankId);
		sb.append(" and b.SIMID =").append(simId);
		List<Map<String, Object>> listMap = cardManageDao.findBySql(sb.toString(), null, Map.class);
		if(listMap.isEmpty()){
			return new HashMap<String, Object>();
		}
		return (Map<String, Object>)cardManageDao.findBySql(sb.toString(), null, Map.class).get(0);
	}

	/**
	 * 
	 * @Description 卡状态修改
	 * @param ei
	 * @return JSONObject  
	 * @author yifang.huang
	 * @date 2016年5月19日 下午2:02:41
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public JSONObject importStatusFile(ImportExcel ei) {
		
		// 执行结果
		int successNum = 0;
		int failureNum = 0;
		StringBuilder failureMsg = new StringBuilder();

		for (int i = ei.getDataRowNum(); i < ei.getLastDataRowNum(); i++) {
			Row row = ei.getRow(i);
			// SIMBANKID
			String simBankId = ObjectUtils.toString(ei.getCellValue(row, 0)).trim();
			if (StringUtils.isBlank(simBankId)) {
				failureMsg.append("<br/>SIMBANKID 能为空{第"+(i+1)+"行};");
				failureNum++;
				continue;
			}
			// SIMID
			String simId = ObjectUtils.toString(ei.getCellValue(row, 1)).trim();
			if (StringUtils.isBlank(simId)) {
				failureMsg.append("<br/>SIMID 不能为空{第"+(i+1)+"行};");
				failureNum++;
				continue;
			}
			// 卡状态
			String status = ObjectUtils.toString(ei.getCellValue(row, 2)).trim();
			if (StringUtils.isBlank(status)) {
				failureMsg.append("<br/>卡状态不能为空{第"+(i+1)+"行};");
				failureNum++;
				continue;
			}
			Integer simBankIdInt = null;
			Integer simIdInt = null;
			Integer statusInt = null;
			try {
				simBankIdInt = Integer.valueOf(simBankId);
			} catch (NumberFormatException e) {
				failureMsg.append("<br/>输入主控板数据格式错误{第"+(i+1)+"行};");
				failureNum++;
				continue;
			}
			try {
				simIdInt = Integer.valueOf(simId);
			} catch (NumberFormatException e) {
				failureMsg.append("<br/>输入卡槽编号数据格式错误{第"+(i+1)+"行};");
				failureNum++;
				continue;
			}
			try {
				statusInt = Integer.valueOf(status);
			} catch (NumberFormatException e) {
				failureMsg.append("<br/>输入状态数据格式错误{第"+(i+1)+"行};");
				failureNum++;
				continue;
			}
			
			// 如果是7，则将simnode表中的已使用流量清零
			if (statusInt == 7) {
				Map<String, Object> cardMap = cardManageDao.findSimNode(simBankId, simId);
				if (cardMap != null) {
					String iccId = ObjectUtils.toString(cardMap.get("iccId"));
					if (StringUtils.isNotBlank(iccId)) {
						List<String> iccIdList= new ArrayList<String>();
						iccIdList.add(iccId);
						cardManageDao.updateClearDataBySn(iccIdList);
					}
				}
			}
			
			Map<String, String> result = Socekt.simStatusController(simBankIdInt, simIdInt, statusInt);
			if (!result.get("code").equals("0")) {
				failureMsg.append("<br/>"+result.get("msg")+"{第"+(i+1)+"行};");
				failureNum++;
				continue;
			}
			
		}

		successNum = ei.getLastDataRowNum() - failureNum - 2;
		if (failureNum > 0) {
			failureMsg.insert(0, ",失败[" + failureNum + "]张卡状态,导入失败信息如下:");
		}
		JSONObject obj = new JSONObject();
		obj.put("successNum", successNum);
		obj.put("failureNum", failureNum);
		obj.put("failureMsg", failureMsg.toString());
		return obj;
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
		return cardManageDao.findSimNode(cardType, status, isOverFlow);
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
		return cardManageDao.findSimNode(updatDate);
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
		return cardManageDao.findSimNode(iccIdList);
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
		return cardManageDao.findSimNode(isOverFlow, isLongTermCard, haveResetTime);
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
		
		return cardManageDao.findMifiNode(ueId, simBankId, simId);
		
	}

	/**
	 * 
	 * @Description 查询过期的卡信息
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2016年9月20日 上午10:17:45
	 */
	public List<Map<String, Object>> findOverdueCard(){
		
		return cardManageDao.findOverdueCard();

	}
	
	/**
	 * 
	 * @Description 模拟卡板编号查询卡板所连接的服务IP及端口
	 * @param simBankId
	 * @return String  
	 * @author yifang.huang
	 * @date 2016年10月13日 下午2:34:38
	 */
	public String findServicerIpBySimBankId(Integer simBankId) {
		
		return cardManageDao.findServicerIpBySimBankId(simBankId);
		
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
		return cardManageDao.findExceedFlowAndStatusIs6ByIccId(iccId);
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
		return cardManageDao.findByIccid(iccid);
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
		return cardManageDao.findSimnodesByStatusAndIccids(iccid);
	}
	
	/**
	 * 
	 * @Description 修改card_basic_info表的sn_hex字段,sn_hex字段为空，且在simnode存在有效数据
	 * @return int  
	 * @author yifang.huang
	 * @date 2017年4月27日 下午3:52:03
	 */
	public int updateSnHex(){
		return cardManageDao.updateSnHex();
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
		return cardManageDao.updateSnHex(iccIds);
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
		return cardManageDao.updateSnHex(iccId);
	}
}
