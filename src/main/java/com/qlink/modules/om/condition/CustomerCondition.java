/** 
 * @Package com.uu.modules.om.condition 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月29日 下午4:32:39 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.om.condition;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.uu.modules.om.entity.Customer;

/** 
 * @Description 客户 查询条件类
 * @author yifang.huang
 * @date 2016年4月29日 下午4:32:39 
 */
public class CustomerCondition {
	
	private String eqId;									// 客户ID eq 查询值
	
	private String likeName;								// 客户姓名 like 查询值

	public void build(DetachedCriteria dc) {
		
		if (StringUtils.isNotBlank(eqId)) {
			dc.add(Restrictions.eq("id", eqId));
		}
		
		if (StringUtils.isNotBlank(likeName)) {
			dc.add(Restrictions.like("name", likeName, MatchMode.ANYWHERE));
		}
		
		// 查询标记为0（正常）的数据
		dc.add(Restrictions.eq(Customer.FIELD_DEL_FLAG, Customer.DEL_FLAG_NORMAL));
		
		// 按创建时间降序排序
		dc.addOrder(Order.desc("createDate"));

	}

	/** 
	 * @return eqId
	 */
	public String getEqId() {
		return eqId;
	}

	/** 
	 * @param eqId
	 */
	public void setEqId(String eqId) {
		this.eqId = eqId;
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

}
