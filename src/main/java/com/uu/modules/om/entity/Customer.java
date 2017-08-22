/** 
 * @Package com.uu.modules.om.entity 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月29日 下午4:26:42 
 * @version V1.0 
 */ 
package com.uu.modules.om.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.uu.common.persistence.IdEntity;

/** 
 * @Description 客户 实体类
 * @author yifang.huang
 * @date 2016年4月29日 下午4:26:42 
 */
@Entity
@Table(name = "om_customer")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Customer extends IdEntity<Customer> {

	private static final long serialVersionUID = -5258120962193159004L;
	
	private String name;					// 客户姓名
	
	private String phone;					// 手机号码
	
	private String email;					// 客户邮箱
		
	private String passportNo;				// 护照号码
	
	private String passportPy;				// 护照拼音

	/** 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/** 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** 
	 * @return phone
	 */
	public String getPhone() {
		return phone;
	}

	/** 
	 * @param phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/** 
	 * @return email
	 */
	public String getEmail() {
		return email;
	}

	/** 
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/** 
	 * @return passportNo
	 */
	public String getPassportNo() {
		return passportNo;
	}

	/** 
	 * @param passportNo
	 */
	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}

	/** 
	 * @return passportPy
	 */
	public String getPassportPy() {
		return passportPy;
	}

	/** 
	 * @param passportPy
	 */
	public void setPassportPy(String passportPy) {
		this.passportPy = passportPy;
	}
	
}
