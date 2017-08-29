/** 
 * @Package com.uu.modules.mifi.service 
 * @Description 
 * @author yuxiaoyu
 * @date 2017年3月2日 下午6:11:59 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.mifi.condition.MifiUsageRecordSegmentCondition;
import com.uu.modules.mifi.dao.MifiUsageRecordSegmentDao;
import com.uu.modules.mifi.entity.MifiUsageRecordSegment;

/** 
 * @Description 
 * @author yuxiaoyu
 * @date 2017年3月2日 下午6:11:59 
 */
@Service
public class MifiUsageRecordSegmentService extends BaseService {
	public static Logger logger = LoggerFactory.getLogger(MifiUsageRecordSegmentService.class);

	@Autowired
	private MifiUsageRecordSegmentDao recordDao;

	/**
	 * 保存
	 * @Description 
	 * @param records 
	 * @return void  
	 * @author yuxiaoyu
	 * @date 2017年3月2日 下午6:13:58
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void save(List<MifiUsageRecordSegment> records) {
		recordDao.save(records);
	}
	
	public Page<MifiUsageRecordSegment> findPage(Page<MifiUsageRecordSegment> page,
			MifiUsageRecordSegmentCondition condition) {
		DetachedCriteria dc = recordDao.createDetachedCriteria();
		condition.build(dc);
		return recordDao.find(page, dc);
	}
	
	public void deleteByIds(String ids) {
		String sql = "DELETE FROM MifiUsageRecordSegment t WHERE t.id in (" + ids + ")";
		recordDao.update(sql);
	}
}
