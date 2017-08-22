package com.uu.modules.mifi.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.persistence.Parameter;
import com.uu.common.service.BaseService;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.IdGen;
import com.uu.common.utils.excel.ImportExcel;
import com.uu.modules.mifi.condition.MifiVersionCondition;
import com.uu.modules.mifi.dao.MifiManageDao;
import com.uu.modules.mifi.dao.MifiOrderDao;
import com.uu.modules.mifi.dao.MifiVersionDao;
import com.uu.modules.mifi.entity.MifiBasicInfo;
import com.uu.modules.mifi.entity.MifiVersion;
import com.uu.modules.om.condition.ConsumeRecordCondition;
import com.uu.modules.om.dao.ChannelDao;
import com.uu.modules.om.dao.ConsumeRecordDao;
import com.uu.modules.om.dao.PriceDao;
import com.uu.modules.om.dao.RegionDao;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.entity.ConsumeRecord;
import com.uu.modules.om.entity.ConsumeRecord.RecordType;
import com.uu.modules.om.entity.ConsumeRecord.Status;
import com.uu.modules.om.entity.Price;
import com.uu.modules.om.entity.Region;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.utils.UserUtils;
import com.uu.modules.utils.Constants;
import com.uu.modules.utils.ReturnCode;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class MifiDeviceService extends BaseService {

	public static Logger logger = LoggerFactory.getLogger(MifiDeviceService.class);

	@Autowired
	private MifiManageDao mifiManageDao;

	@Autowired
	private MifiOrderDao mifiOrderDao;
	
	@Autowired
	private ChannelDao channelDao;
	
	@Autowired
	private PriceDao priceDao;
	
	@Autowired
	private RegionDao regionDao;

	@Autowired
	private ConsumeRecordDao consumeRecordDao;
	
	@Autowired
	private MifiVersionDao mifiVersionDao;
	
	public MifiBasicInfo getMifiDevice(String id) {
		return mifiManageDao.get(id);
	}
	
	public Page<HashMap> findMifiDeviceList(Page<HashMap> page, Map<String, Object> paramMap) {
		StringBuffer sb = new StringBuffer();
		sb.append("select a.*, b.UEID_MCC as used_mcc,b.stamp_updated as last_time, b.UESTATUS last_status, format(b.datainfo/1024/1024, 2) datainfo, c.MAIN_VERSION, c.APN_3G_VERSION, c.APN_4G_VERSION, d.UEALLOWED, d.UEALLOWEDMCC, d.OWNER_MCC, d.test_ip, d.test_update_ip, d.softsim_type, d.web_portal_flag, d.order_type, d.flow, d.flow_used, d.start_time, d.end_time, cast(d.vficcid as char) vficcid "
				+ "from mifi_basic_info a left join mifinode b on a.sn=b.imei left join mifiversion c on a.sn=c.imei_6200 left join mifilist d on a.sn=d.imei_6200");
		sb.append(" where 1 = 1");
		String sn = ObjectUtils.toString(paramMap.get("sn"));
		if (StringUtils.isNotBlank(sn)) {
			sb.append(" and a.sn like '%" + sn.trim() + "%'");
		}
		String bath = ObjectUtils.toString(paramMap.get("bath"));
		if (StringUtils.isNotBlank(bath)) {
			sb.append(" and a.bath like '%" + bath + "%'");
		}
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			sb.append(" and a.source_type = '" + sourceType + "'");
		}
		
		String ownerType = ObjectUtils.toString(paramMap.get("ownerType"));
		if (StringUtils.isNotBlank(ownerType)) {
			sb.append(" and a.owner_type = '" + ownerType + "'");
		}

		String ownerMcc = ObjectUtils.toString(paramMap.get("ownerMcc"));
		if (StringUtils.isNotBlank(ownerMcc)) {
			sb.append(" and d.OWNER_MCC='" + ownerMcc + "'");
		}

		String iccId = ObjectUtils.toString(paramMap.get("iccId"));
		if (StringUtils.isNotBlank(iccId)) {
			sb.append(" and d.VFICCID='" + iccId.trim() + "'");
		}
		
		return mifiManageDao.findBySql(page, sb.toString(), null, Map.class);
	}

	public List<MifiBasicInfo> findMifiDeviceListForExport(Map<String, Object> paramMap) {
		
		List<MifiBasicInfo> list = null;
		
		StringBuffer sb = new StringBuffer();
		sb.append("select a.sn, a.imei, a.type, a.model, a.owner_type, a.ssid, a.pwd, a.source_type, a.bath, a.unique_no, a.supplier, b.OWNER_MCC "
				+ "from mifi_basic_info a left join mifilist b on a.sn=b.imei_6200");
		sb.append(" where 1 = 1");
		String sn = ObjectUtils.toString(paramMap.get("sn"));
		if (StringUtils.isNotBlank(sn)) {
			sb.append(" and a.sn like '%" + sn + "%'");
		}
		String bath = ObjectUtils.toString(paramMap.get("bath"));
		if (StringUtils.isNotBlank(bath)) {
			sb.append(" and a.bath like '%" + bath + "%'");
		}
		String sourceType = ObjectUtils.toString(paramMap.get("sourceType"));
		if (StringUtils.isNotBlank(sourceType)) {
			sb.append(" and a.source_type = '" + sourceType + "'");
		}
		
		String ownerType = ObjectUtils.toString(paramMap.get("ownerType"));
		if (StringUtils.isNotBlank(ownerType)) {
			sb.append(" and a.owner_type = '" + ownerType + "'");
		}

		String ownerMcc = ObjectUtils.toString(paramMap.get("ownerMcc"));
		if (StringUtils.isNotBlank(ownerMcc)) {
			sb.append(" and b.OWNER_MCC='" + ownerMcc + "'");
		}
		
		List<String[]> listObjArr = mifiManageDao.findBySql(sb.toString());
		if (listObjArr!=null && listObjArr.size()>0) {
			list = new ArrayList<MifiBasicInfo>();
			MifiBasicInfo info = null;
			for (Object[] objs : listObjArr) {
				info = new MifiBasicInfo();
				info.setSn(ObjectUtils.toString(objs[0]));
				info.setImei(ObjectUtils.toString(objs[1]));
				info.setType(ObjectUtils.toString(objs[2]));
				info.setModel(ObjectUtils.toString(objs[3]));
				info.setOwnerType(ObjectUtils.toString(objs[4]));
				info.setSsid(ObjectUtils.toString(objs[5]));
				info.setPwd(ObjectUtils.toString(objs[6]));
				info.setSourceType(ObjectUtils.toString(objs[7]));
				info.setBath(ObjectUtils.toString(objs[8]));
				info.setUniqueNo(ObjectUtils.toString(objs[9]));
				info.setSupplier(ObjectUtils.toString(objs[10]));
				info.setOwnerMcc(ObjectUtils.toString(objs[11]));;
				
				list.add(info);
				info = null;
			}
		}
		
		return list;
	}
	
	/**
	 * 查询表MifiList
	 * @Description 
	 * @author yuxiaoyu
	 * @date 2016年5月5日 下午2:30:28
	 */
	public List<Object[]> getMifiList() {
		String sql = "select imei_6200, owner_type, source_type from mifilist where imei_6200 not in (select sn from mifi_basic_info)";
		return mifiManageDao.findBySql(sql);
	}
	
	/**
	 * 保存
	 * @Description 
	 * @author yuxiaoyu
	 * @date 2016年5月5日 下午2:29:34
	 */
	//@Transactional(readOnly = false)
	public void save(MifiBasicInfo mifiBasicInfo) {
		
		String sourceType = mifiBasicInfo.getSourceType();
		String ownerType = Constants.CHANNEL_DEFAULT_VALUE.equals(sourceType) ? MifiBasicInfo.ownerType_0 : MifiBasicInfo.ownerType_1;
		
		String sql = "update mifi_basic_info set source_type=:p1, owner_type=:p2 where id=:p3";
		
		mifiManageDao.clear();
		mifiManageDao.updateBySql(sql, new Parameter(mifiBasicInfo.getSourceType(), ownerType, mifiBasicInfo.getId()));
		
		// 修改对应mifilist数据
		DetachedCriteria dc = channelDao.createDetachedCriteria();
		dc.add(Restrictions.eq("channelNameEn", sourceType));
		dc.add(Restrictions.eq(Channel.FIELD_DEL_FLAG, Channel.DEL_FLAG_NORMAL));// 取未删除的数据
		Channel channel = channelDao.find(dc).get(0);
		
		StringBuffer sb = new StringBuffer("update mifilist set source_type='"+sourceType+"', owner_mcc='"+channel.getMcces()+"', owner_type='"+ownerType+"'");
		/*if ("BOOT_BUTTON".equals(channel.getModel())) {
			sb.append(", UEALLOWED='1' ,UEALLOWEDMCC='ALL'");
		} else {
			sb.append(", UEALLOWED='0' ,UEALLOWEDMCC='homeForbidden'");
		}*/
		
		String testIp = mifiBasicInfo.getTestIp();
		String testUpdateIp = mifiBasicInfo.getTestUpdateIp();
		Integer softsimType = mifiBasicInfo.getSoftsimType();
		Integer webPortalFlag = mifiBasicInfo.getWebPortalFlag();
		sb.append(", TEST_IP='" + testIp + "'");
		sb.append(", TEST_UPDATE_IP='" + testUpdateIp + "'");
		sb.append(", SOFTSIM_TYPE=" + softsimType);
		sb.append(", web_portal_flag=" + webPortalFlag);
		
		sb.append(" where IMEI_6200='"+mifiBasicInfo.getSn()+"'");
		mifiManageDao.updateBySql(sb.toString(), null);
	}
	
	/**
	 * 同步
	 * @Description 
	 * @author yuxiaoyu
	 * @date 2016年5月5日 下午2:29:34
	 */
	//@Transactional(readOnly = false)
	public void updateAndSync(String sql) {
		mifiManageDao.clear();
		mifiManageDao.updateBySql(sql, null);
	}
	
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public JSONObject importFile(List<MifiBasicInfo> list) {
		int successNum = 0;
		int failureNum = 0;
		StringBuilder failureMsg = new StringBuilder();
		HashMap usedSn = new HashMap();
		for (MifiBasicInfo mifiBasicInfo : list) {
			// 去空格
			trim(mifiBasicInfo);
			
			if (StringUtils.isBlank(mifiBasicInfo.getSn())) {
				failureMsg.append("<br/>设备序列号不能为空;");
				failureNum++;
				continue;
			}
			if (mifiBasicInfo.getSn().indexOf("E14") != -1) {
				mifiBasicInfo.setSn(mifiBasicInfo.getSn().replace(".", "").replace("E14", ""));
			}
			if (StringUtils.isBlank(mifiBasicInfo.getImei()) && mifiBasicInfo.getImei().indexOf("E14") != -1) {
				mifiBasicInfo.setImei(mifiBasicInfo.getImei().replace(".", "").replace("E14", ""));
			}
			
			if(usedSn.containsKey(mifiBasicInfo.getSn())){
				failureMsg.append("<br/>设备序列号["+mifiBasicInfo.getSn()+"]已处理,请勿重复导入;");
				failureNum++;
				continue;
			}
			usedSn.put(mifiBasicInfo.getSn(), mifiBasicInfo.getSn());
			MifiBasicInfo _mifiBasicInfo = mifiManageDao.getMifiBasicInfoBySn(mifiBasicInfo.getSn());
			if (null == _mifiBasicInfo) {// 新增设备
				if (StringUtils.isBlank(mifiBasicInfo.getImei())) {
					mifiBasicInfo.setType(Constants.MIFI_TYPE_DEFAULT_VALUE);
				}
				if (StringUtils.isBlank(mifiBasicInfo.getType())) {
					mifiBasicInfo.setType(Constants.MIFI_TYPE_DEFAULT_VALUE);
				}
				if (StringUtils.isBlank(mifiBasicInfo.getModel())) {
					mifiBasicInfo.setModel(Constants.MIFI_MODEL_DEFAULT_VALUE);
				}
				if (StringUtils.isBlank(mifiBasicInfo.getOwnerType())) {
					mifiBasicInfo.setOwnerType(MifiBasicInfo.ownerType_0);
				}
				if (!mifiBasicInfo.getOwnerType().equals(MifiBasicInfo.ownerType_0)
						&& !mifiBasicInfo.getOwnerType().equals(MifiBasicInfo.ownerType_1)
						&& !mifiBasicInfo.getOwnerType().equals(MifiBasicInfo.ownerType_2)) {
					failureMsg.append("<br/>设备归属只能为[0:游友移动,1:渠道,2:个人]");
					failureNum++;
					continue;
				}
				if (StringUtils.isBlank(mifiBasicInfo.getSourceType())) {
					mifiBasicInfo.setSourceType(Constants.CHANNEL_DEFAULT_VALUE);
				}
				HashMap channelInfo = mifiManageDao.getChannelInfo(mifiBasicInfo.getSourceType());
				if (null == channelInfo) {
					failureMsg.append("<br/>设备归属渠道[" + mifiBasicInfo.getSourceType() + "]不存在,请新增此渠道后操作!");
					failureNum++;
					continue;
				}
				if (mifiBasicInfo.getSourceType().equals(Constants.CHANNEL_DEFAULT_VALUE)) {
					// 游友移动
					if (!mifiBasicInfo.getOwnerType().equals(MifiBasicInfo.ownerType_0)
							&& !mifiBasicInfo.getOwnerType().equals(MifiBasicInfo.ownerType_2)) {
						failureMsg.append("<br/>设备归属渠道为[游友移动]时,设备归属不能为[" + MifiBasicInfo.ownerType_1 + ":渠道];");
						failureNum++;
						continue;
					}
				} else {
					if (!mifiBasicInfo.getOwnerType().equals(MifiBasicInfo.ownerType_1)) {
						failureMsg.append("<br/>设备归属渠道为[" + mifiBasicInfo.getSourceType() + "]时,设备归属只能为["
								+ MifiBasicInfo.ownerType_1 + ":渠道];");
						failureNum++;
						continue;
					}
				}
				mifiManageDao.save(mifiBasicInfo);
				StringBuffer buffer = new StringBuffer();
				String mccs = (String) channelInfo.get("mcces");
				Parameter parameter = new Parameter();
				parameter.put("sn", mifiBasicInfo.getSn());
				parameter.put("owner_type", mifiBasicInfo.getOwnerType());
				parameter.put("source_type", mifiBasicInfo.getSourceType());
				parameter.put("owner_mcc", mccs);
				parameter.put("ueallowed", Constants.ueAllowed_n);
				parameter.put("UEALLOWEDMCC", "homeForbidden");
				parameter.put("iccId", mifiBasicInfo.getIccId());
				if (0 != mifiOrderDao.getMifiListCountByDsn(mifiBasicInfo.getSn())) {
					buffer.append(" update mifilist set owner_type = :owner_type , source_type = :source_type, owner_mcc = :owner_mcc, UEALLOWED=:ueallowed, UEALLOWEDMCC=:UEALLOWEDMCC, VFICCID=:iccId");
					buffer.append(" where imei_6200 = :sn ");
				}else{
					buffer.append("insert into mifilist(imei_6200,owner_type,source_type,owner_mcc,UEALLOWED,UEALLOWEDMCC,VFICCID) values(:sn,:owner_type,:source_type,:owner_mcc,:ueallowed,:UEALLOWEDMCC,:iccId)");
				}
				mifiOrderDao.updateBySql(buffer.toString(), parameter);
			} else {// 修改设备归属
				if (StringUtils.isNotBlank(mifiBasicInfo.getImei())) {
					_mifiBasicInfo.setImei(mifiBasicInfo.getImei());
				}
				if (StringUtils.isNotBlank(mifiBasicInfo.getType())) {
					_mifiBasicInfo.setType(mifiBasicInfo.getType());
				}
				if (StringUtils.isNotBlank(mifiBasicInfo.getModel())) {
					_mifiBasicInfo.setModel(mifiBasicInfo.getModel());
				}
				String ownerType = _mifiBasicInfo.getOwnerType();
				String sourceType = _mifiBasicInfo.getSourceType();
				String flag = "0";
				if (StringUtils.isNotBlank(mifiBasicInfo.getOwnerType()) && !ownerType.equals(mifiBasicInfo.getOwnerType())) {
					flag = "1";
					ownerType = mifiBasicInfo.getOwnerType();
				}
				if (StringUtils.isNotBlank(mifiBasicInfo.getSourceType()) && !sourceType.equals(mifiBasicInfo.getSourceType())) {
					flag = "0".equals(flag) ? "2" : "3";
					sourceType = mifiBasicInfo.getSourceType();
				}
				if (!"0".equals(flag)) {
					if (sourceType.equals(Constants.CHANNEL_DEFAULT_VALUE)) {
						// 游友移动
						if (!ownerType.equals(MifiBasicInfo.ownerType_0)
								&& !ownerType.equals(MifiBasicInfo.ownerType_2)) {
							failureMsg.append("<br/>设备归属渠道为[游友移动]时,设备归属不能为[" + MifiBasicInfo.ownerType_1 + "渠道];");
							failureNum++;
							continue;
						}
					} else {
						if (!ownerType.equals(MifiBasicInfo.ownerType_1)) {
							failureMsg.append(
									"<br/>设备归属渠道为[" + sourceType + "]时,设备归属只能为[" + MifiBasicInfo.ownerType_1 + ":渠道];");
							failureNum++;
							continue;
						}
					}
					HashMap channelInfo = mifiManageDao.getChannelInfo(sourceType);
					if (null == channelInfo) {
						failureMsg.append("<br/>设备归属渠道[" + sourceType + "]不存在,请新增此渠道后操作!");
						failureNum++;
						continue;
					}
					String mccs = (String) channelInfo.get("mcces");
					StringBuffer buffer = new StringBuffer();
					buffer.append(" update mifilist set owner_type = '" + ownerType + "', source_type = '" + sourceType + "', owner_mcc = '" + mccs + "', UEALLOWEDMCC='homeForbidden'");
					buffer.append(" where imei_6200 = " + _mifiBasicInfo.getSn() );
					mifiOrderDao.updateBySql(buffer.toString(), null);
				}
				_mifiBasicInfo.setOwnerType(ownerType);
				_mifiBasicInfo.setSourceType(sourceType);
				if (StringUtils.isNotBlank(mifiBasicInfo.getSsid())) {
					_mifiBasicInfo.setSsid(mifiBasicInfo.getSsid());
				}
				if (StringUtils.isNotBlank(mifiBasicInfo.getPwd())) {
					_mifiBasicInfo.setPwd(mifiBasicInfo.getPwd());
				}
				if (StringUtils.isNotBlank(mifiBasicInfo.getBath())) {
					_mifiBasicInfo.setBath(mifiBasicInfo.getBath());
				}
				if (StringUtils.isNotBlank(mifiBasicInfo.getUniqueNo())) {
					_mifiBasicInfo.setUniqueNo(mifiBasicInfo.getUniqueNo());
				}
				if (StringUtils.isNotBlank(mifiBasicInfo.getSupplier())) {
					_mifiBasicInfo.setSupplier(mifiBasicInfo.getSupplier());
				}
				mifiManageDao.save(_mifiBasicInfo);
			}
			
			// 修改版本信息
			updateMifiVersion(mifiBasicInfo);
		}
		
		successNum = list.size() - failureNum;
		if (failureNum > 0) {
			failureMsg.insert(0, ",失败[" + failureNum + "]条设备数据,导入失败信息如下:");
		}
		
		JSONObject obj = new JSONObject();
		obj.put("successNum", successNum);
		obj.put("failureNum", failureNum);
		obj.put("failureMsg", failureMsg.toString());
		return obj;
	}
	
	// 设备入库前字段去空格
	private void trim(MifiBasicInfo mifiBasicInfo) {

		if (StringUtils.isNotBlank(mifiBasicInfo.getSn())) {
			mifiBasicInfo.setSn(mifiBasicInfo.getSn().trim());
		}
		if (StringUtils.isNotBlank(mifiBasicInfo.getImei())) {
			mifiBasicInfo.setImei(mifiBasicInfo.getImei().trim());
		}
		if (StringUtils.isNotBlank(mifiBasicInfo.getType())) {
			mifiBasicInfo.setType(mifiBasicInfo.getType().trim());
		}
		if (StringUtils.isNotBlank(mifiBasicInfo.getModel())) {
			mifiBasicInfo.setModel(mifiBasicInfo.getModel().trim());
		}
		if (StringUtils.isNotBlank(mifiBasicInfo.getOwnerType())) {
			mifiBasicInfo.setOwnerType(mifiBasicInfo.getOwnerType().trim());
		}
		if (StringUtils.isNotBlank(mifiBasicInfo.getSourceType())) {
			mifiBasicInfo.setSourceType(mifiBasicInfo.getSourceType().trim());
		}
		if (StringUtils.isNotBlank(mifiBasicInfo.getSsid())) {
			mifiBasicInfo.setSsid(mifiBasicInfo.getSsid().trim());
		}
		if (StringUtils.isNotBlank(mifiBasicInfo.getPwd())) {
			mifiBasicInfo.setPwd(mifiBasicInfo.getPwd().trim());
		}
		if (StringUtils.isNotBlank(mifiBasicInfo.getBath())) {
			mifiBasicInfo.setBath(mifiBasicInfo.getBath().trim());
		}
		if (StringUtils.isNotBlank(mifiBasicInfo.getUniqueNo())) {
			mifiBasicInfo.setUniqueNo(mifiBasicInfo.getUniqueNo().trim());
		}
		if (StringUtils.isNotBlank(mifiBasicInfo.getSupplier())) {
			mifiBasicInfo.setSupplier(mifiBasicInfo.getSupplier().trim());
		}
		// 设备版本信息
		if (StringUtils.isNotBlank(mifiBasicInfo.getMAIN_VERSION())) {
			mifiBasicInfo.setMAIN_VERSION(mifiBasicInfo.getMAIN_VERSION().trim());
		}
		if (StringUtils.isNotBlank(mifiBasicInfo.getYYM_VERSION())) {
			mifiBasicInfo.setYYM_VERSION(mifiBasicInfo.getYYM_VERSION().trim());
		}
		if (StringUtils.isNotBlank(mifiBasicInfo.getYY_DAEMON_VERSION())) {
			mifiBasicInfo.setYY_DAEMON_VERSION(mifiBasicInfo.getYY_DAEMON_VERSION().trim());
		}
		if (StringUtils.isNotBlank(mifiBasicInfo.getAPN_3G_VERSION())) {
			mifiBasicInfo.setAPN_3G_VERSION(mifiBasicInfo.getAPN_3G_VERSION().trim());
		}
		if (StringUtils.isNotBlank(mifiBasicInfo.getAPN_4G_VERSION())) {
			mifiBasicInfo.setAPN_4G_VERSION(mifiBasicInfo.getAPN_4G_VERSION().trim());
		}
		if (StringUtils.isNotBlank(mifiBasicInfo.getCOPS_CONF_VERSION())) {
			mifiBasicInfo.setCOPS_CONF_VERSION(mifiBasicInfo.getCOPS_CONF_VERSION().trim());
		}
		if (StringUtils.isNotBlank(mifiBasicInfo.getYY_UPDATE_VERSION())) {
			mifiBasicInfo.setYY_UPDATE_VERSION(mifiBasicInfo.getYY_UPDATE_VERSION().trim());
		}
	}
	
	// 修改设备版本信息
	private void updateMifiVersion(MifiBasicInfo bean) {
		// 如果主版本号为空，则不做任何处理
		if (StringUtils.isBlank(bean.getMAIN_VERSION()))
			return;
		
		String imei = bean.getImei();
		MifiVersionCondition condition = new MifiVersionCondition();
		condition.setEqImei(imei);
		List<MifiVersion> mvList = mifiVersionDao.findListByCondition(condition);
		if (mvList!=null && mvList.size()>0) { // 修改
			MifiVersion mv = mvList.get(0);
			
			if (StringUtils.isNotBlank(bean.getMAIN_VERSION()))
				mv.setMAIN_VERSION(bean.getMAIN_VERSION());
			if (StringUtils.isNotBlank(bean.getYYM_VERSION())) {
				mv.setYYM_VERSION(bean.getYYM_VERSION());
			}
			if (StringUtils.isNotBlank(bean.getYY_DAEMON_VERSION())) {
				mv.setYY_DAEMON_VERSION(bean.getYY_DAEMON_VERSION());
			}
			if (StringUtils.isNotBlank(bean.getAPN_3G_VERSION())) {
				mv.setAPN_3G_VERSION(bean.getAPN_3G_VERSION());
			}
			if (StringUtils.isNotBlank(bean.getAPN_4G_VERSION())) {
				mv.setAPN_4G_VERSION(bean.getAPN_4G_VERSION());
			}
			if (StringUtils.isNotBlank(bean.getCOPS_CONF_VERSION())) {
				mv.setCOPS_CONF_VERSION(bean.getCOPS_CONF_VERSION());
			}
			if (StringUtils.isNotBlank(bean.getYY_UPDATE_VERSION())) {
				mv.setYY_UPDATE_VERSION(bean.getYY_UPDATE_VERSION());
			}
			mifiVersionDao.save(mv);
			
		} else {// 新建
			String id = mifiVersionDao.getMaxId() + "";
			MifiVersion mv = new MifiVersion();
			
			mv.setId(id);
			mv.setIMEI_6200(imei);
			mv.setMAIN_VERSION(bean.getMAIN_VERSION());
			mv.setYYM_VERSION(bean.getYYM_VERSION());
			mv.setYY_DAEMON_VERSION(bean.getYY_DAEMON_VERSION());
			mv.setAPN_3G_VERSION(bean.getAPN_3G_VERSION());
			mv.setAPN_4G_VERSION(bean.getAPN_4G_VERSION());
			mv.setCOPS_CONF_VERSION(bean.getCOPS_CONF_VERSION());
			mv.setYY_UPDATE_VERSION(bean.getYY_UPDATE_VERSION());
			mv.setStamp_created(new Date());
			mv.setStamp_update(new Date());
			mifiVersionDao.save(mv);
		}
		
	}

	/**
	 * 
	 * @Description 设备归属地修改
	 * @param ei
	 * @return JSONObject  
	 * @author yifang.huang
	 * @date 2016年5月19日 下午2:02:41
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public JSONObject importOwnerMccFile(ImportExcel ei) {
		
		// 当前登录用户
		User user = UserUtils.getUser();
		String channelNameEn = user.getChannelNameEn();

		// 执行结果
		int successNum = 0;
		int failureNum = 0;
		StringBuilder failureMsg = new StringBuilder();

		for (int i = ei.getDataRowNum(); i < ei.getLastDataRowNum(); i++) {
			Row row = ei.getRow(i);
			// 设备IMEI
			String imei = ObjectUtils.toString(ei.getCellValue(row, 0)).trim();
			if (StringUtils.isBlank(imei)) {
				failureMsg.append("<br/>设备IMEI不能为空{第"+(i+1)+"行};");
				failureNum++;
				continue;
			}
			if (imei.indexOf("E14") != -1) {
				imei = imei.replace(".", "").replace("E14", "");
			}
			// 国家编号
			String countryCode = ObjectUtils.toString(ei.getCellValue(row, 1)).trim();
			if (StringUtils.isBlank(countryCode)) {
				failureMsg.append("<br/>国家编号不能为空{第"+(i+1)+"行};");
				failureNum++;
				continue;
			}
			// 取设备
			Map<String, String> deviceMap = mifiManageDao.getMifilistBySn(imei);
			if (deviceMap == null) {
				failureMsg.append("<br/>设备["+imei+"]未入库{第"+(i+1)+"行};");
				failureNum++;
				continue;
			}
			// 判断设备是否属于当前渠道(游戏移动用户可以修改其它渠道设备归属地：目前处理方式)
			if (!channelNameEn.equals(Constants.CHANNEL_DEFAULT_VALUE) && !channelNameEn.equals(deviceMap.get("sourceType"))) {
				failureMsg.append("<br/>设备["+imei+"]不属于当前运营商["+channelNameEn+"]{第"+(i+1)+"行};");
				failureNum++;
				continue;
			}
			// 取国家
			Map<String, String> countryMap = mifiOrderDao.findMccByCountryCode(countryCode);
			if (countryMap.get("countryCode") == null) {
				failureMsg.append("<br/>国家["+countryCode+"]未找到{第"+(i+1)+"行};");
				failureNum++;
				continue;
			}
			// 修改owner_mcc,UEALLOWEDMCC为'homeForbidden',UEALLOWED为'0'
			mifiManageDao.updateMifilist(Constants.ueAllowed_n, "homeForbidden", countryMap.get("mcces"), imei);;
		}

		successNum = ei.getLastDataRowNum() - failureNum - 2;
		if (failureNum > 0) {
			failureMsg.insert(0, ",失败[" + failureNum + "]条设备数据,导入失败信息如下:");
		}
		JSONObject obj = new JSONObject();
		obj.put("successNum", successNum);
		obj.put("failureNum", failureNum);
		obj.put("failureMsg", failureMsg.toString());
		return obj;
	}
	
	/**
	 * 根据设备编号查询UEALLOWEDMCC值
	 * @athor shuxin
	 * @date 2016年7月7日下午2:29:21
	 * @param sn
	 * @return
	 * String 
	 */
	public Map<String, Object> getMifiListBySn(String sn) {
		String sql = "select UEALLOWEDMCC as mcc, UEALLOWED as allowed from mifilist where imei_6200 = "+sn;
		List<Map<String, Object>> list = mifiManageDao.findBySql(sql, null, Map.class);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mcc", list.get(0).get("mcc"));
		map.put("allowed", list.get(0).get("allowed"));
		return map;
	}
	
	/**
	 * 根据设备编号修改UEALLOWEDMCC和UEALLOWED字段值
	 * @athor shuxin
	 * @date 2016年7月8日下午12:44:49
	 * @param parameter
	 * void 
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updateMifiListForMccAndAllowBySn(Parameter parameter){
		String sql = "update mifilist set UEALLOWEDMCC = :p3, UEALLOWED =:p2 where  imei_6200 =:p1";
		mifiManageDao.updateBySql(sql, parameter);
	}
	
	/**
	 * 根据设备sn查找设备列表
	 * @athor shuxin
	 * @date 2016年7月15日上午9:28:36
	 * @param sn
	 * @return
	 * List<HashMap> 
	 */
	public List<Map<String, Object>> findMifiDeviceListBySn(String sn) {
		StringBuffer sb = new StringBuffer();
		sb.append("select a.*, b.UEID_MCC as used_mcc,b.stamp_updated as last_time, b.UESTATUS last_status, format(b.datainfo/1024/1024, 2) datainfo, c.MAIN_VERSION, c.APN_3G_VERSION, c.APN_4G_VERSION, d.UEALLOWED, d.UEALLOWEDMCC, d.OWNER_MCC ");
		sb.append("from mifi_basic_info a left join mifinode b on a.sn=b.imei left join mifiversion c on a.sn=c.imei_6200 left join mifilist d on a.sn=d.imei_6200");
		sb.append(" where 1=1 and a.sn = '" + sn + "'");
		return mifiManageDao.findBySql(sb.toString(), null, Map.class);
	}
	
	/**
	 * 
	 * @Description 根据时间查询设备的位置信息
	 * @param channelCode
	 * @param startDate
	 * @param endDate
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2016年8月26日 下午5:22:52
	 */
	public List<Map<String, Object>> fidndPositionByDate(String channelCode, String startDate, String endDate) {
		StringBuffer sb = new StringBuffer("select tt.imei, tt.glat, tt.glng, DATE_FORMAT(tt.createDate, '%Y-%m-%d %H:%i:%s') createDate from (");
		sb.append("SELECT CAST(t1.imei AS CHAR)imei, t2.gLAT glat, t2.gLNG glng, t1.stamp_created createDate");
		sb.append(" FROM mifistatus t1, cell_tower_location t2");
		sb.append(" WHERE t1.MCC_6200 = t2.MCC AND t1.MNC_6200 = t2.MNC AND t1.TAC_6200 = t2.TAC AND t1.CELLID_6200 = t2.CELLID");
		
		if (StringUtils.isNotBlank(channelCode)) {
			sb.append(" AND t1.source_type='" + channelCode + "'");
		}

		if (StringUtils.isNotBlank(startDate)) {
			sb.append(" AND t1.stamp_created>=STR_TO_DATE('" + startDate + "', '%Y-%m-%d %H:%i:%s')");
		}
		
		if (StringUtils.isNotBlank(endDate)) {
			sb.append(" AND t1.stamp_created<=STR_TO_DATE('" + endDate + "', '%Y-%m-%d %H:%i:%s')");
		}
		
		sb.append(" ORDER BY t1.stamp_created desc) tt GROUP BY tt.imei");
		
		return mifiManageDao.findBySql(sb.toString(), null, Map.class);
	}
	
	/**
	 * 
	 * @Description 根据时间查询设备的位置信息
	 * @param channelCode
	 * @param imei
	 * @param startDate
	 * @param endDate
	 * @return List<Map<String,Object>>  
	 * @author yifang.huang
	 * @date 2016年8月26日 下午5:28:14
	 */
	public List<Map<String, Object>> fidndPositionByDate(String channelCode, String imei, String startDate, String endDate) {
		StringBuffer sb = new StringBuffer("select tt.imei, tt.glat, tt.glng, DATE_FORMAT(tt.createDate, '%Y-%m-%d %H:%i:%s') createDate from (");
		sb.append("SELECT CAST(t1.imei AS CHAR)imei, t2.gLAT glat, t2.gLNG glng, t1.stamp_created createDate");
		sb.append(" FROM mifistatus t1, cell_tower_location t2");
		sb.append(" WHERE t1.MCC_6200 = t2.MCC AND t1.MNC_6200 = t2.MNC AND t1.TAC_6200 = t2.TAC AND t1.CELLID_6200 = t2.CELLID");

		if (StringUtils.isNotBlank(channelCode)) {
			sb.append(" AND t1.source_type='" + channelCode + "'");
		}
		
		if (StringUtils.isNotBlank(imei)) {
			sb.append(" AND t1.imei='" + imei + "'");
		}

		if (StringUtils.isNotBlank(startDate)) {
			sb.append(" AND t1.stamp_created>=STR_TO_DATE('" + startDate + "', '%Y-%m-%d %H:%i:%s')");
		}
		
		if (StringUtils.isNotBlank(endDate)) {
			sb.append(" AND t1.stamp_created<=STR_TO_DATE('" + endDate + "', '%Y-%m-%d %H:%i:%s')");
		}
		
		sb.append(") tt GROUP BY tt.imei, tt.glat, tt.glng ORDER BY tt.createDate ASC");

		return mifiManageDao.findBySql(sb.toString(), null, Map.class);
	}
	
	/**
	 * 
	 * @Description 修改设备ownerMcc接口
	 * @param param 请求参数
	 * @param channelCode 渠道编号
	 * @return JSONObject  
	 * @author yifang.huang
	 * @date 2016年9月27日 下午3:16:46
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public JSONObject updateMifiOwnerMcc(JSONObject param, String channelCode) {
		
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		String errorMessage = "";
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(df.format(new Date()) + "[入参]：" + param.toString());
		
		if (!param.containsKey("mifiId") || StringUtils.isBlank(param.getString("mifiId"))) {
			errorMessage = "|[mifiId]Can not be empty!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		if (!param.containsKey("ownerMcc") || StringUtils.isBlank(param.getString("ownerMcc"))) {
			errorMessage = "|[ownerMcc]Can not be empty!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		String mifiId = param.getString("mifiId");
		String ownerMcc = param.getString("ownerMcc");
		
		// 查询设备是否存在、判断所选设备是否属于当前渠道商
		HashMap<String, String> mifiMap = mifiManageDao.getMifilistBySn(mifiId);
		if (mifiMap == null) {
			errorMessage = "| [device:" + mifiId + "]not found!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		if (!channelCode.equals(mifiMap.get("sourceType"))) {
			errorMessage = "| [device:" + mifiId + "]Does not belong to the current channel!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_40035 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		
		// 修改设备ownerMcc值
		mifiManageDao.updateMifilistOwnerMcc(ownerMcc, mifiId);

		logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
		return resObj;
	}
	
	/**
	 * 
	 * @Description 设备开关机
	 * @param param 请求参数
	 * @param channelCode 渠道编号
	 * @return JSONObject  
	 * @author yifang.huang
	 * @date 2016年9月27日 下午3:16:46
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public JSONObject updateAndHomeForbiddenOnOrOff(JSONObject param, String channelCode) {
		
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		String errorMessage = "";
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(df.format(new Date()) + "[入参]：" + param.toString());
		
		if (!param.containsKey("mifiId") || StringUtils.isBlank(param.getString("mifiId"))) {
			errorMessage = "|[mifiId]Can not be empty!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		String mifiId = param.getString("mifiId");

		if (!param.containsKey("status") || StringUtils.isBlank(param.getString("status"))) {
			errorMessage = "|[status]Can not be empty!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		String status = param.getString("status");
		if (!"on".equals(status) && !"off".equals(status)) {
			errorMessage = "| [status:" + status + "]Status only 'on' and 'off' two options!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_40035 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		
		// 查询设备是否存在、判断所选设备是否属于当前渠道商
		HashMap<String, String> mifiMap = mifiManageDao.getMifilistBySn(mifiId);
		if (mifiMap == null) {
			errorMessage = "| [device:" + mifiId + "]not found!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		if (!channelCode.equals(mifiMap.get("sourceType"))) {
			errorMessage = "| [device:" + mifiId + "]Does not belong to the current channel!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_40035 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		//设备开关机修改相应数据
		List<Channel> cList = channelDao.findList(channelCode);
		Channel channel = cList.get(0);
		if ("ORDER".equals(channel.getModel())) {
			if ("on".equals(status))
				mifiManageDao.updateMifilist("1", "homeForbidden", mifiId);
			else
				mifiManageDao.updateMifilist("0", "ALL", mifiId);
			
		} else {
			if ("on".equals(status))
				mifiManageDao.updateMifilist("1", "homeForbidden", mifiId);
			else
				mifiManageDao.updateMifilist("1", "ALL", mifiId);
		}

		logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
		return resObj;
	}
	
	/**
	 * 
	 * @Description 设备开机确认使用
	 * @param param 请求参数
	 * @param channelCode 渠道编号
	 * @return JSONObject  
	 * @author yifang.huang
	 * @date 2016年10月9日 下午2:24:59
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public JSONObject saveAndBootConfirmation(JSONObject param, String channelCode) {
		
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		String errorMessage = "";
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(df.format(new Date()) + "[入参]：" + param.toString());
		
		// 设备编号检测
		if (!param.containsKey("mifiId") || StringUtils.isBlank(param.getString("mifiId"))) {// 设备编号不能为空
			errorMessage = "|[mifiId]Can not be empty!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		String mifiId = param.getString("mifiId");
		HashMap<String, String> mifiMap = mifiManageDao.getMifilistBySn(mifiId);// 查询设备是否存在
		if (mifiMap == null) {
			errorMessage = "| [device:" + mifiId + "]not found!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		if (!channelCode.equals(mifiMap.get("sourceType"))) {//判断所选设备是否属于当前渠道商
			errorMessage = "| [device:" + mifiId + "]Does not belong to the current channel!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_40035 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		String ssid = mifiMap.get("ssid");

		// 设备使用国家MCC检测
		if (!param.containsKey("mccs") || StringUtils.isBlank(param.getString("mccs"))) {// 设备使用国家MCC不能为空
			errorMessage = "|[mccs]Can not be empty!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		String mccs = param.getString("mccs");

		// 设备开机天数检测
		if (!param.containsKey("days") || StringUtils.isBlank(param.getString("days"))) {// 设备开机天数不能为空
			errorMessage = "|[days]Can not be empty!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		String daysStr = param.getString("days");
		int days = 0;
		try {
			days = Integer.valueOf(daysStr);// 开机天数格式判断
		} catch (NumberFormatException e) {
			e.printStackTrace();
			errorMessage = "|[days]Only allowed for integer!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_40035 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		if (days <= 0) {
			errorMessage = "|[days]Greater than 0!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_40035 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		
		// 取渠道对象
		List<Channel> cList = channelDao.findList(channelCode);
		Channel channel = cList.get(0);
		
		// 是否存在有效期内的开机确认记录
		ConsumeRecordCondition crCondition = new ConsumeRecordCondition(true);
		crCondition.setEqChannelId(channel.getId());
		crCondition.setEqSn(mifiId);
		crCondition.setEqMcc(mccs);
		crCondition.setEqSourceType(Constants.SOURCE_TYPE_BOOT);
		DetachedCriteria dc = consumeRecordDao.createDetachedCriteria();
		crCondition.build(dc); // build 查询条件
		List<ConsumeRecord> crList = consumeRecordDao.find(dc);
		if (crList!=null && crList.size()>0) {
			ConsumeRecord cr = crList.get(0);
			if (isEffective(cr.getCreateDate(), cr.getDays())) {	// 如果已存在有效的开机确认记录直接返回成功
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}
		}
		
		// 取运营商对应区域价格
		Map<String, String> map = getMoneyByMccs(mccs, channel.getId(), days);
		if (!"1".equals(map.get("code"))) {
			resObj.put("code", "-1");
			resObj.put("msg", ReturnCode.ERR_40035 + map.get("msg"));
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		Double money = Double.valueOf(map.get("money"));
		
		// 运营商类型（预付费运营商需要判断余额，后付费运营商直接扣费）
		String payType = channel.getPayType();				// 付费类型，0_预付费 1_后付费
		if ("0".equals(payType)) {
			Double balance = channel.getBalance();		// 预付费运营商余额
			if (money > balance) { // 余额不足，请联系运营商
				resObj.put("code", "-1");
				resObj.put("msg", "Insufficient account balance(Required deduction:'" + money + "',Current balance:'" + balance + "')！");
				logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
				return resObj;
			}

			// 修改余额
			channel.setBalance(balance - money);
			channelDao.save(channel);
			
		}

		// 保存消费记录
		String id = IdGen.uuid();
		ConsumeRecord record = new ConsumeRecord();
		record.setId(id);
		record.setChannel(channel);
		record.setRecordType(RecordType.BUY);
		record.setStatus(Status.COMPLETED);
		record.setMoney(money);
		record.setTargetName("设备开机确认");
		record.setCountryCode(map.get("countryCodes"));
		record.setCountryName(map.get("countryNames"));
		record.setMcc(mccs);
		record.setSn(mifiId);
		record.setSsid(ssid);
		record.setSourceType(Constants.SOURCE_TYPE_BOOT);
		record.setDays(days);
		record.setCreateDate(new Date());
		consumeRecordDao.getSession().save(record);
		
		// 修改设备可用状态
		mifiManageDao.updateMifilist(Constants.ueAllowed_y, "ALL", mifiId);
		resObj.put("id", id);
		
		logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
		return resObj;
	}
	
	/**
	 * 
	 * @Description 设备流量统计
	 * @param param
	 * @param channelCode
	 * @return JSONObject  
	 * @author yifang.huang
	 * @date 2016年10月10日 下午5:51:13
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public JSONObject flowStatistcs(JSONObject param, String channelCode) {
		
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		String errorMessage = "";
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(df.format(new Date()) + "[入参]：" + param.toString());
		
		// 设备编号检测
		if (!param.containsKey("mifiId") || StringUtils.isBlank(param.getString("mifiId"))) {// 设备编号不能为空
			errorMessage = "|[mifiId]Can not be empty!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		String mifiId = param.getString("mifiId");
		HashMap<String, String> mifiMap = mifiManageDao.getMifilistBySn(mifiId);// 查询设备是否存在
		if (mifiMap == null) {
			errorMessage = "| [device:" + mifiId + "]not found!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		if (!channelCode.equals(mifiMap.get("sourceType"))) {//判断所选设备是否属于当前渠道商
			errorMessage = "| [device:" + mifiId + "]Does not belong to the current channel!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_40035 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		// 统计开始时间检测(北京时间)
		Date startDate = null;
		if (!param.containsKey("startDate") || StringUtils.isBlank(param.getString("startDate"))) {
			errorMessage = "|[startDate]Can not be empty!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		String startDateStr = param.getString("startDate");
		try {
			startDate = DateUtils.parseDate(startDateStr, new String[] {"yyyy-MM-dd HH:mm:ss"});
		} catch (ParseException e) {
			errorMessage = "[startDate] format 'yyyy-MM-dd HH:mm:ss'!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		// 统计结束时间检测(北京时间)
		Date endDate = null;
		if (!param.containsKey("endDate") || StringUtils.isBlank(param.getString("endDate"))) {
			errorMessage = "|[endDate]Can not be empty!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		String endDateStr = param.getString("endDate");
		try {
			endDate = DateUtils.parseDate(param.getString("endDate"), new String[] {"yyyy-MM-dd HH:mm:ss"});
		} catch (ParseException e) {
			errorMessage = "[endDate] format 'yyyy-MM-dd HH:mm:ss'!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		
		// 统计开始时间不能比统计结束时间晚
		if (startDate.after(endDate)) {
			errorMessage = "[startDate] can not be later than [endDate]!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		
		// 根据设备使用记录统计设备流量 
		List<Map<String, String>> dataList = mifiManageDao.getDeviceFlowGroupByCountry(channelCode, mifiId, startDateStr, endDateStr);
		if (dataList!=null && dataList.size()>0) {
			JSONArray jsonArr = new JSONArray();
			JSONObject jsonObj;
			for (Map<String, String> data : dataList) {
				jsonObj = new JSONObject();
				jsonObj.put("countryCode", data.get("countryCode"));
				jsonObj.put("countryNameEn", data.get("countryNameEn"));
				jsonObj.put("dataInfo", data.get("dataInfo"));
				jsonArr.add(jsonObj);
				jsonObj = null;
			}
			resObj.put("data", jsonArr);
		} else {
			resObj.put("code", "-1");
			resObj.put("msg", "No data found!");
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
		return resObj;
	}
	
	/**
	 * 
	 * @Description 设备流量统计
	 * @param param
	 * @param channelCode
	 * @return JSONObject  
	 * @author yifang.huang
	 * @date 2016年10月10日 下午5:51:13
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public JSONObject version(JSONObject param, String channelCode) {
		
		JSONObject resObj = new JSONObject();
		resObj.put("code", "0");
		resObj.put("msg", ReturnCode.ERR_0);
		String errorMessage = "";
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(df.format(new Date()) + "[入参]：" + param.toString());
		
		// 设备编号检测
		if (!param.containsKey("mifiId") || StringUtils.isBlank(param.getString("mifiId"))) {// 设备编号不能为空
			errorMessage = "|[mifiId]Can not be empty!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		String mifiId = param.getString("mifiId");
		HashMap<String, String> mifiMap = mifiManageDao.getMifilistBySn(mifiId);// 查询设备是否存在
		if (mifiMap == null) {
			errorMessage = "| [device:" + mifiId + "]not found!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_61451 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}
		if (!channelCode.equals(mifiMap.get("sourceType"))) {//判断所选设备是否属于当前渠道商
			errorMessage = "| [device:" + mifiId + "]Does not belong to the current channel!";
			resObj.put("code", "61451");
			resObj.put("msg", ReturnCode.ERR_40035 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		// 取设备版本信息
		Double imeiVersion = mifiVersionDao.getMifiVersion(mifiId);
		if (imeiVersion == 0) {
			errorMessage = "| [device:" + mifiId + "]Version information not found!";
			resObj.put("code", "2");
			resObj.put("msg", ReturnCode.ERR_2 + errorMessage);
			logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
			return resObj;
		}

		JSONObject jsonObj = new JSONObject();
		jsonObj.put("version", imeiVersion + "");
		resObj.put("data", jsonObj);
		logger.info(df.format(new Date()) + "[出参]：" + resObj.toString());
		return resObj;
	}
	
	// 私有方法 TODO ...
	
	/**
	 * 
	 * @Description 计算开机费用
	 * @param mccs
	 * @param channelId
	 * @param days
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年10月10日 上午10:37:15
	 */
	private Map<String, String> getMoneyByMccs(String mccs, String channelId, int days) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		// 参考单价
		BigDecimal money = new BigDecimal("0");
		String countryCodes = "";
		String countryNames = "";
		String[] mccArr = mccs.split(",");
		Set<Region> regionSet = new HashSet<Region>();// 区域集合
		for (String str : mccArr) {
			Region region = regionDao.findByMcc(str);
			if (region != null)
				regionSet.add(region);
		}
		// mcc未分配区域
		if (regionSet.size() == 0) {
			map.put("code", "-1");
			map.put("msg", "Sorry this area not yet put into operation, please contact customer service!");
			return map;
		}
		
		for (Region bean : regionSet) {
			// 价格
			List<Price> priceList = priceDao.findList(channelId, bean.getId());
			if (priceList==null || priceList.size()==0) {
				map.put("code", "-1");
				map.put("msg", "Sorry this area not yet put into operation, please contact customer service!");
				return map;
			}
			Price price = priceList.get(0);
			money = money.add(new BigDecimal(price.getPrice()));
			
			countryCodes = countryCodes + "," + bean.getCountryCodes();
			countryNames = countryNames + "," + bean.getCountryNames();
		}
		
		map.put("code", "1");
		map.put("money", new DecimalFormat("0.00").format(money.multiply(new BigDecimal(days))));
		
		countryCodes = StringUtils.isNotBlank(countryCodes) ? countryCodes.substring(1) : "";
		countryNames = StringUtils.isNotBlank(countryNames) ? countryNames.substring(1) : "";
		map.put("countryCodes", countryCodes);
		map.put("countryNames", countryNames);
		
		return map;
	}
	
	/**
	 * 
	 * @Description 计算开机记录是否在有效期内
	 * @param createDate
	 * @param days
	 * @return boolean  
	 * @author yifang.huang
	 * @date 2016年10月10日 上午11:49:27
	 */
	private boolean isEffective(Date createDate, int days) {
		
		// 总小时数
		int hours = days * 24;
		
		// 已使用总小时数
		int usedHours = Integer.valueOf(((new Date().getTime() - createDate.getTime()) / 3600000) + "");
		
		if (usedHours < hours)
			return true;

		return false;
		
	}

}
