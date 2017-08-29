/** 
 * @Package com.uu.modules.sys.condition 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月15日 下午6:09:00 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.sys.condition;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.uu.modules.om.entity.ConsumeRecord;

/** 
 * @Description 通知接收  查询条件类
 * @author yifang.huang
 * @date 2016年4月15日 下午6:09:00 
 */
public class NoticeReceiveCondition {
	
	private String neId;

	private String likeName;
	
	private String eqType;
	
	public void build(DetachedCriteria dc) {
		
		// id ne 查询值
		if (StringUtils.isNotBlank(neId))
			dc.add(Restrictions.ne("id", neId));
		
		
		if (StringUtils.isNotBlank(likeName)) {
			dc.add(Restrictions.like("name", likeName, MatchMode.ANYWHERE));
		}
		
		if (StringUtils.isNotBlank(eqType)){
			dc.add(Restrictions.eq("type", eqType));
		}
		
		// 查询标记为0（正常）的数据
		dc.add(Restrictions.eq(ConsumeRecord.FIELD_DEL_FLAG, ConsumeRecord.DEL_FLAG_NORMAL));
		
		// 按创建时间降序排序
		dc.addOrder(Order.desc("createDate"));

	}

	/** 
	 * @return neId
	 */
	public String getNeId() {
		return neId;
	}

	/** 
	 * @param neId
	 */
	public void setNeId(String neId) {
		this.neId = neId;
	}

	/** 
	 * @return likeName
	 */
	public String getLikeName() {
		return likeName;
	}

	/** 
	 * @param likeName
	 */
	public void setLikeName(String likeName) {
		this.likeName = likeName;
	}

	/** 
	 * @return eqType
	 */
	public String getEqType() {
		return eqType;
	}

	/** 
	 * @param eqType
	 */
	public void setEqType(String eqType) {
		this.eqType = eqType;
	}
	
}
