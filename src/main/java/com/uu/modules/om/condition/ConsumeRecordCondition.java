/** 
 * @Package com.uu.modules.om.condition 
 * @Description 
 * @author yifang.huang
 * @date 2016年3月23日 上午11:23:22 
 * @version V1.0 
 */ 
package com.uu.modules.om.condition;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.uu.common.utils.DateUtils;
import com.uu.common.utils.SpringContextHolder;
import com.uu.modules.om.entity.Channel;
import com.uu.modules.om.entity.ConsumeRecord;
import com.uu.modules.om.entity.ConsumeRecord.RecordType;
import com.uu.modules.om.entity.ConsumeRecord.Status;
import com.uu.modules.om.service.ChannelService;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.utils.UserUtils;
import com.uu.modules.utils.Constants;

/** 
 * @Description 消费记录 查询条件类
 * @author yifang.huang
 * @date 2016年3月23日 上午11:23:22 
 */
public class ConsumeRecordCondition {

	private String eqUserId;									// 用户ID eq 查询值
	
	private String eqChannelId;									// 渠道ID eq 查询值
	
	private String eqBalanceStatus;								// 结算状态 eq 查询值
	
	private String likePhone;									// 手机号码 like 查询值
	
	private RecordType eqRecordType;							// 类型 eq 查询值
		
	private Status eqStatus;									// 状态 eq 查询值
	
	private String eqSourceType;								// 来源（所属平台） eq 查询值
	
	private String geCreateDate;								// 创建时间  ge 查询值

	private String leCreateDate;								// 创建时间  le 查询值
	
	private String eqSn;										// 设备编号 eq 查询值
	
	private String gtLocalDate;									// 当地消费时间  gt 查询值

	private String ltLocalDate;									// 当地消费时间  lt 查询值
	
	private String eqCountryCode;								// 国家编号 eq 查询值
	
	private Date geCreateDateForDate;							// 创建时间  ge 查询值（日期数据类型）

	private Date leCreateDateForDate;							// 创建时间  le 查询值（日期数据类型）
	
	private String eqMcc;										// mcc eq 查询值
	
	public ConsumeRecordCondition() {
		super();
		// 初始化渠道查询值，渠道数据权限控制
		initEqChannelId();
	}
	
	public ConsumeRecordCondition(boolean isRecordQuery) {
		super();
	}

	public void build(DetachedCriteria dc) {
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		if (StringUtils.isNotBlank(eqUserId)) {
			dc.add(Restrictions.eq("userId",eqUserId));
		}
		
		if (StringUtils.isNotBlank(eqChannelId)) {
			dc.createAlias("channel", "channel");
			dc.add(Restrictions.eq("channel.id",eqChannelId));
		} 
		
		if (StringUtils.isNotBlank(eqBalanceStatus)) {
			dc.add(Restrictions.eq("balanceStatus",eqBalanceStatus));
		}
		
		if (StringUtils.isNotBlank(likePhone)) {
			dc.add(Restrictions.like("phone",likePhone, MatchMode.ANYWHERE));
			
		}
		
		if (null != eqRecordType){
			dc.add(Restrictions.eq("recordType", eqRecordType));
		}
		
		if (null != eqStatus){
			dc.add(Restrictions.eq("status", eqStatus));
		}
		
		if (StringUtils.isNotBlank(eqSourceType)){
			dc.add(Restrictions.eq("sourceType", eqSourceType));
		}

		if (StringUtils.isNotBlank(geCreateDate)) {
			try {
				dc.add(Restrictions.ge("createDate", DateUtils.getDateStart(df.parse(geCreateDate))));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		if (StringUtils.isNotBlank(leCreateDate)) {
			try {
				dc.add(Restrictions.le("createDate", DateUtils.getDateEnd(df.parse(leCreateDate))));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		if (StringUtils.isNotBlank(eqSn)){
			dc.add(Restrictions.eq("sn", eqSn));
		}

		if (StringUtils.isNotBlank(gtLocalDate)) {
			try {
				dc.add(Restrictions.gt("localDate", DateUtils.getDateStart(df.parse(gtLocalDate))));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		if (StringUtils.isNotBlank(ltLocalDate)) {
			try {
				dc.add(Restrictions.lt("localDate", DateUtils.getDateEnd(df.parse(ltLocalDate))));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		if (StringUtils.isNotBlank(eqCountryCode)){
			dc.add(Restrictions.eq("countryCode", eqCountryCode));
		}

		if (null != geCreateDateForDate) {
			dc.add(Restrictions.ge("createDate", geCreateDateForDate));
		}

		if (null != leCreateDateForDate) {
			dc.add(Restrictions.le("createDate", leCreateDateForDate));
		}
		
		if (StringUtils.isNotBlank(eqMcc)) {
			dc.add(Restrictions.eq("mcc", eqMcc));
		}
		
		// 查询标记为0（正常）的数据
		dc.add(Restrictions.eq(ConsumeRecord.FIELD_DEL_FLAG, ConsumeRecord.DEL_FLAG_NORMAL));
		
		// 按创建时间降序排序
		dc.addOrder(Order.desc("createDate"));

	}

	public String getEqUserId() {
		return eqUserId;
	}


	public void setEqUserId(String eqUserId) {
		this.eqUserId = eqUserId;
	}

	public String getEqChannelId() {
		return eqChannelId;
	}

	public void setEqChannelId(String eqChannelId) {
		this.eqChannelId = eqChannelId;
	}

	public String getEqBalanceStatus() {
		return eqBalanceStatus;
	}

	public void setEqBalanceStatus(String eqBalanceStatus) {
		this.eqBalanceStatus = eqBalanceStatus;
	}

	/** 
	 * @return likePhone
	 */
	public String getLikePhone() {
		return likePhone;
	}

	/** 
	 * @param likePhone
	 */
	public void setLikePhone(String likePhone) {
		this.likePhone = likePhone;
	}

	/** 
	 * @return eqRecordType
	 */
	public RecordType getEqRecordType() {
		return eqRecordType;
	}

	/** 
	 * @param eqRecordType
	 */
	public void setEqRecordType(RecordType eqRecordType) {
		this.eqRecordType = eqRecordType;
	}

	/** 
	 * @return eqStatus
	 */
	public Status getEqStatus() {
		return eqStatus;
	}

	/** 
	 * @param eqStatus
	 */
	public void setEqStatus(Status eqStatus) {
		this.eqStatus = eqStatus;
	}

	/** 
	 * @return eqSourceType
	 */
	public String getEqSourceType() {
		return eqSourceType;
	}

	/** 
	 * @param eqSourceType
	 */
	public void setEqSourceType(String eqSourceType) {
		this.eqSourceType = eqSourceType;
	}

	/** 
	 * @return geCreateDate
	 */
	public String getGeCreateDate() {
		return geCreateDate;
	}

	/** 
	 * @param geCreateDate
	 */
	public void setGeCreateDate(String geCreateDate) {
		this.geCreateDate = geCreateDate;
	}

	/** 
	 * @return leCreateDate
	 */
	public String getLeCreateDate() {
		return leCreateDate;
	}

	/** 
	 * @param leCreateDate
	 */
	public void setLeCreateDate(String leCreateDate) {
		this.leCreateDate = leCreateDate;
	}

	/** 
	 * @return eqSn
	 */
	public String getEqSn() {
		return eqSn;
	}

	/** 
	 * @param eqSn
	 */
	public void setEqSn(String eqSn) {
		this.eqSn = eqSn;
	}

	/** 
	 * @return gtLocalDate
	 */
	public String getGtLocalDate() {
		return gtLocalDate;
	}

	/** 
	 * @param gtLocalDate
	 */
	public void setGtLocalDate(String gtLocalDate) {
		this.gtLocalDate = gtLocalDate;
	}

	/** 
	 * @return ltLocalDate
	 */
	public String getLtLocalDate() {
		return ltLocalDate;
	}

	/** 
	 * @param ltLocalDate
	 */
	public void setLtLocalDate(String ltLocalDate) {
		this.ltLocalDate = ltLocalDate;
	}

	/** 
	 * @return eqCountryCode
	 */
	public String getEqCountryCode() {
		return eqCountryCode;
	}

	/** 
	 * @param eqCountryCode
	 */
	public void setEqCountryCode(String eqCountryCode) {
		this.eqCountryCode = eqCountryCode;
	}

	/** 
	 * @return geCreateDateForDate
	 */
	public Date getGeCreateDateForDate() {
		return geCreateDateForDate;
	}

	/** 
	 * @param geCreateDateForDate
	 */
	public void setGeCreateDateForDate(Date geCreateDateForDate) {
		this.geCreateDateForDate = geCreateDateForDate;
	}

	/** 
	 * @return leCreateDateForDate
	 */
	public Date getLeCreateDateForDate() {
		return leCreateDateForDate;
	}

	/** 
	 * @param leCreateDateForDate
	 */
	public void setLeCreateDateForDate(Date leCreateDateForDate) {
		this.leCreateDateForDate = leCreateDateForDate;
	}

	/** 
	 * @return eqMcc
	 */
	public String getEqMcc() {
		return eqMcc;
	}

	/** 
	 * @param eqMcc
	 */
	public void setEqMcc(String eqMcc) {
		this.eqMcc = eqMcc;
	}

	/**
	 * 
	 * @Description  根据当前登录用户，初始化渠道查询值
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年4月12日 下午5:36:37
	 */
	private void initEqChannelId() {
		User user = UserUtils.getUser();
		String channelNameEn = user.getChannelNameEn();
		// 用户绑定了渠道商
		if (StringUtils.isNotBlank(channelNameEn) && !Constants.CHANNEL_DEFAULT_VALUE.equals(channelNameEn)) {
			ChannelService channelService = SpringContextHolder.getBean(ChannelService.class);
			List<Channel> list = channelService.findChannelByNameEn(channelNameEn);
			if (list!=null && list.size()>0) {
				Channel channel = list.get(0);
				eqChannelId = channel.getId();
			}
		}
	}
}
