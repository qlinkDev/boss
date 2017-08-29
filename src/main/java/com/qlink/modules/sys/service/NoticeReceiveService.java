/** 
 * @Package com.uu.modules.sys.service 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月18日 上午9:12:16 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.sys.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.sys.condition.NoticeReceiveCondition;
import com.uu.modules.sys.dao.NoticeReceiveDao;
import com.uu.modules.sys.entity.NoticeReceive;

/** 
 * @Description 通知接收 业务处理类
 * @author yifang.huang
 * @date 2016年4月18日 上午9:12:16 
 */
@Service
public class NoticeReceiveService extends BaseService {
	
	@Autowired
	private NoticeReceiveDao noticeReceiveDao;
	
	/**
	 * 
	 * @Description 根据ID取数据
	 * @param id
	 * @return NoticeReceive  
	 * @author yifang.huang
	 * @date 2016年4月18日 上午9:13:46
	 */
	public NoticeReceive get(String id) {
		return noticeReceiveDao.get(id);
	}
	
	/**
	 * 
	 * @Description 保存数据
	 * @param bean 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年4月18日 上午9:13:53
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void save(NoticeReceive bean) {
		
		noticeReceiveDao.save(bean);
		
	}
	
	/**
	 * 
	 * @Description 根据ID删除数据
	 * @param id 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年4月18日 上午9:14:09
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void delete(String id) {
		noticeReceiveDao.deleteById(id);
	}

	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<NoticeReceive>  
	 * @author yifang.huang
	 * @date 2016年4月18日 上午9:14:20
	 */
	public List<NoticeReceive> findListByCondition(NoticeReceiveCondition condition) {
		
		DetachedCriteria dc = noticeReceiveDao.createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return noticeReceiveDao.find(dc);
	}
	
	/**
	 * 
	 * @Description 根据查询参数取分页数据
	 * @param page
	 * @param condition
	 * @return Page<NoticeReceive>  
	 * @author yifang.huang
	 * @date 2016年4月18日 上午9:14:28
	 */
	public Page<NoticeReceive> find(Page<NoticeReceive> page, NoticeReceiveCondition condition) {
		
		DetachedCriteria dc = noticeReceiveDao.createDetachedCriteria();
		
		// build 查询条件
		condition.build(dc);
		
		return noticeReceiveDao.find(page, dc);
	}

}
