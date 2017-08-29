package main.java.com.qlink.modules.mifi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.uu.common.persistence.BaseEntity;
import com.uu.common.utils.excel.annotation.ExcelField;

/**
 * @author wangyong
 * @date 2016年1月21日
 */
@Entity
@Table(name = "sim_card_type")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SimCardType extends BaseEntity<SimCardType> {

	private static final long serialVersionUID = 1L;

	public SimCardType() {
		super();
	}

	public SimCardType(String id) {
		this();
		this.id = id;
	}

	private String id;

	@Id
	@Column(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String cardType;

	@Column(name = "card_type")
	@NotNull(message = "卡类型编码不能为空")
	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	private String cardTypeName;

	@Column(name = "card_type_name")
	@NotNull(message = "卡类型名称不能为空")
	@ExcelField(title = "卡号", align = 2, sort = 10)
	public String getCardTypeName() {
		return cardTypeName;
	}

	public void setCardTypeName(String cardTypeName) {
		this.cardTypeName = cardTypeName;
	}

	private String cardTypeDesc;

	@Column(name = "card_type_desc")
	@NotNull(message = "卡类型描述不能为空")
	public String getCardTypeDesc() {
		return cardTypeDesc;
	}

	public void setCardTypeDesc(String cardTypeDesc) {
		this.cardTypeDesc = cardTypeDesc;
	}

	private String validDays;

	@Column(name = "valid_days")
	@NotNull(message = "有效天数不能为空")
	public String getValidDays() {
		return validDays;
	}

	public void setValidDays(String validDays) {
		this.validDays = validDays;
	}
	
	private Integer clearDay;
	
	private Integer clearHour;

	/** 
	 * @return clearDay
	 */
	public Integer getClearDay() {
		return clearDay;
	}

	/** 
	 * @param clearDay
	 */
	public void setClearDay(Integer clearDay) {
		this.clearDay = clearDay;
	}

	/** 
	 * @return clearHour
	 */
	public Integer getClearHour() {
		return clearHour;
	}

	/** 
	 * @param clearHour
	 */
	public void setClearHour(Integer clearHour) {
		this.clearHour = clearHour;
	}

	private Date activeTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "active_time")
	public Date getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(Date activeTime) {
		this.activeTime = activeTime;
	}

	private String dataCap;

	@Column(name = "data_cap")
	@NotNull(message = "高速流量不能为空")
	public String getDataCap() {
		return dataCap;
	}

	public void setDataCap(String dataCap) {
		this.dataCap = dataCap;
	}

	public static final String areaType_0 = "0";// 全球卡

	public static final String areaType_1 = "1";// 区域卡

	public static final String areaType_2 = "2";// 地区卡(国家卡)

	private String areaType;

	@Column(name = "area_type")
	// @NotNull(message = "卡归属区域类型不能为空")
	public String getAreaType() {
		return areaType;
	}

	public void setAreaType(String areaType) {
		this.areaType = areaType;
	}

	@Transient
	public String getAreaTypeName() {
		if (StringUtils.isNotBlank(areaType)) {
			if (areaType.equals(areaType_0)) {
				return "全球";
			} else if (areaType.equals(areaType_1)) {
				return "区域";
			} else if (areaType.equals(areaType_2)) {
				return "地区";
			} else {
				return "未配置";
			}
		} else {
			return "未配置";
		}
	}

	private String mcc;

	@Column(name = "mcc")
	// @NotNull(message = "卡归属区域不能为空")
	public String getMcc() {
		return mcc;
	}

	public void setMcc(String mcc) {
		this.mcc = mcc;
	}
	
	private String allowedMccCn;

	@Transient
	public String getAllowedMccCn() {
		return allowedMccCn;
	}

	public void setAllowedMccCn(String allowedMccCn) {
		this.allowedMccCn = allowedMccCn;
	}

	private String createUser;

	@Column(name = "create_user")
	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	private Date createTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	private String updateUser;

	@Column(name = "update_user")
	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	private Date updateTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	private String apnInfo;

	/** 
	 * @return apnInfo
	 */
	@Column(name = "apn_info")
	@ExcelField(title = "流量(M)", align = 2, sort = 20)
	public String getApnInfo() {
		return apnInfo;
	}

	/** 
	 * @param apnInfo
	 */
	public void setApnInfo(String apnInfo) {
		this.apnInfo = apnInfo;
	}
	
	private String usePeopleType;					// 卡使用人员类型(ALL_不限制，ABROAD_TO_HOME_国外用户到国内)

	/** 
	 * @return usePeopleType
	 */
	@Column(name = "use_people_type")
	public String getUsePeopleType() {
		return usePeopleType;
	}

	/** 
	 * @param usePeopleType
	 */
	public void setUsePeopleType(String usePeopleType) {
		this.usePeopleType = usePeopleType;
	}
	
	private String mccNickname;						// mcc昵称，当usePeopleType的值为ABROAD_TO_HOME的时候，修改simnode的allowedmcc为mccNickname

	/** 
	 * @return mccNickname
	 */
	public String getMccNickname() {
		return mccNickname;
	}

	/** 
	 * @param mccNickname
	 */
	public void setMccNickname(String mccNickname) {
		this.mccNickname = mccNickname;
	}
	
	private Integer clearType; //卡清空类型（0：自定义，1：月清空）

	public Integer getClearType() {
		return clearType;
	}

	public void setClearType(Integer clearType) {
		this.clearType = clearType;
	}

	private String sourceType;					// 卡所属渠道
	private String allowedSource;				// 可以使用卡的渠道

	/** 
	 * @return sourceType
	 */
	@Column(name = "source_type")
	public String getSourceType() {
		return sourceType;
	}

	/** 
	 * @param sourceType
	 */
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	/** 
	 * @return allowedSource
	 */
	public String getAllowedSource() {
		return allowedSource;
	}

	/** 
	 * @param allowedSource
	 */
	public void setAllowedSource(String allowedSource) {
		this.allowedSource = allowedSource;
	}
	
}
