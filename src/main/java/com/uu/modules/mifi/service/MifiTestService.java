/** 
 * @Package com.uu.modules.mifi.service 
 * @Description 
 * @author yifang.huang
 * @date 2016年5月24日 下午4:30:11 
 * @version V1.0 
 */ 
package com.uu.modules.mifi.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.mifi.condition.MifiTestCondition;
import com.uu.modules.mifi.dao.MifiTestDao;
import com.uu.modules.mifi.entity.MifiTest;

/** 
 * @Description 测试设备与卡绑定 业务处理类
 * @author yifang.huang
 * @date 2016年5月24日 下午4:30:11 
 */
@Service
public class MifiTestService extends BaseService {
	
	@Autowired
	private MifiTestDao mifiTestDao;
	
	/**
	 * 
	 * @Description 根据ID取数据
	 * @param id
	 * @return MifiTest  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午3:09:37
	 */
	public MifiTest get(Integer id) {
		return mifiTestDao.get(id);
	}
	
	/**
	 * 
	 * @Description 保存数据
	 * @param bean 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午3:09:58
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveOrUpdate(MifiTest bean) {
		if (bean.getId() == null) {
			bean.setId(mifiTestDao.getMaxId() + 1);
			bean.setCreateDate(new Date());
		}
		mifiTestDao.getSession().saveOrUpdate(bean);
		
	}
	
	/**
	 * 
	 * @Description 根据ID删除数据
	 * @param id 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午3:10:05
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void delete(Integer id) {
		MifiTest bean = get(id);
		mifiTestDao.getSession().delete(bean);
	}

	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<MifiTest>  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午3:10:14
	 */
	public List<MifiTest> findListByCondition(MifiTestCondition condition) {
		
		return mifiTestDao.findListByCondition(condition);
		
	}
	

	/**
	 * 
	 * @Description 根据simbankid、simid、status取simnode列表
	 * @param simBankId
	 * @param simId
	 * @param status
	 * @return List<Map<String, Object>>  
	 * @author yifang.huang
	 * @date 2016年5月24日 下午4:56:30
	 */
	public List<Map<String, Object>> findSimNodeList(String simBankId, String simId, String status) {
		return mifiTestDao.findSimNodeList(simBankId, simId, status);
	}
	
	/**
	 * 
	 * @Description 根据查询参数取分页数据
	 * @param page
	 * @param condition
	 * @return Page<MifiTest>  
	 * @author yifang.huang
	 * @date 2016年5月20日 下午3:10:24
	 */
	public Page<MifiTest> find(Page<MifiTest> page, MifiTestCondition condition) {
		
		DetachedCriteria dc = mifiTestDao.createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return mifiTestDao.find(page, dc);
	}
	
	/**
	 * 根据id查询
	 * @athor shuxin
	 * @date 2016年7月11日下午3:49:32
	 * @param id
	 * @return
	 * MifiTest 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> findById(Integer id) {
		String sqlString = "SELECT cast(imei as char) as IMEI,SIMBANKID, SIMID FROM mifitest where id ="+id;
		Map<String, Object> map = (Map<String, Object>) mifiTestDao.findBySql(sqlString, null, Map.class).get(0);
		return map;
	}

}
