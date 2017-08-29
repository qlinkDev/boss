package main.java.com.qlink.modules.mifi.condition;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.uu.common.utils.DateUtils;
import com.uu.common.utils.StringUtils;

/**
 * 设备工单查询条件类
 * 
 * @author shuxin
 * @date 2016年5月31日
 */
public class WorkOrderCondition {
	/**
	 * 工单ID
	 */
	private String wid;

	/**
	 * 多个工单IDS
	 */
	private String wids;

	/**
	 * 设备编号
	 */
	private String deviceSn;

	/**
	 * 工单问题类型：1:故障,2:退款,3:设备激活'
	 */
	private Integer problemType;

	/**
	 * 工单处理优先级： 1普通，2紧急
	 */
	private Integer level;

	/**
	 * 工单处理状态:1:等待处理;2:正在处理;3:已关闭;
	 */
	private Integer status;

	/**
	 * 开始时间
	 */
	private String startDate;

	/**
	 * 开始时间
	 */
	private String endDate;

	/**
	 * 工单创建者
	 */
	private String createBy;

	/**
	 * 渠道商编号
	 */
	private String channelSn;
	
	private Integer initTag;

	public void build(DetachedCriteria dc) {
		if (StringUtils.isNotBlank(wid)) {
			dc.add(Restrictions.eq("wid", wid));
		}

		if (StringUtils.isNotBlank(deviceSn)) {
			dc.add(Restrictions.eq("deviceSn", deviceSn));
		}

		if (null != problemType) {
			dc.add(Restrictions.eq("problemType", problemType));
		}

		if (null != level) {
			dc.add(Restrictions.eq("level", level));
		}

		if (null != status) {
			dc.add(Restrictions.eq("status", status));
		}

		if (StringUtils.isNotBlank(createBy)) {
			dc.add(Restrictions.like("createBy.name", createBy + "%"));
		}

		if (StringUtils.isNotBlank(channelSn)) {
			dc.add(Restrictions.eq("channelSn", channelSn));
		}

		if (StringUtils.isNotBlank(startDate)) {
			dc.add(Restrictions.ge("createTime", DateUtils.parseDate(startDate)));
		}

		if (StringUtils.isNotBlank(endDate)) {
			dc.add(Restrictions.le("createTime", DateUtils.parseDate(endDate)));
		}

		dc.addOrder(Order.desc("createTime"));
	}

	public String getWid() {
		return wid;
	}

	public void setWid(String wid) {
		this.wid = wid;
	}

	public String getDeviceSn() {
		return deviceSn;
	}

	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn == null ? null : deviceSn.trim();
	}

	public Integer getProblemType() {
		return problemType;
	}

	public void setProblemType(Integer problemType) {
		this.problemType = problemType;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		String timeStr = startDate;
		if (StringUtils.isNotBlank(timeStr) && timeStr.length() <= 10) {
			timeStr += " 00:00:00";
		}
		this.startDate = timeStr;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		String timeStr = endDate;
		if (StringUtils.isNotBlank(timeStr) && timeStr.length() <= 10) {
			timeStr += " 23:59:59";
		}
		this.endDate = timeStr;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getWids() {
		return wids;
	}

	public void setWids(String wids) {
		this.wids = wids;
	}

	public String getChannelSn() {
		return channelSn;
	}

	public void setChannelSn(String channelSn) {
		this.channelSn = channelSn == null ? null : channelSn.trim();
	}

	public Integer getInitTag() {
		return initTag;
	}

	public void setInitTag(Integer initTag) {
		this.initTag = initTag;
	}
	
}
