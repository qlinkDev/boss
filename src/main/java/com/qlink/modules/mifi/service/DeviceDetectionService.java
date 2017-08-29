/** 
 * @Package com.uu.modules.mifi.service 
 * @Description 
 * @author yifang.huang
 * @date 2016年11月9日 下午3:46:31 
 * @version V1.0 
 */ 
package main.java.com.qlink.modules.mifi.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.common.utils.IdGen;
import com.uu.modules.mifi.condition.DeviceDetectionCondition;
import com.uu.modules.mifi.dao.DeviceDetectionDao;
import com.uu.modules.mifi.dao.DeviceDetectionItemDao;
import com.uu.modules.mifi.dao.MifiManageDao;
import com.uu.modules.mifi.entity.DeviceDetection;
import com.uu.modules.mifi.entity.DeviceDetectionItem;

/** 
 * @Description 设备检查   业务处理类
 * @author yifang.huang
 * @date 2016年11月9日 下午3:46:31 
 */
@Service
public class DeviceDetectionService extends BaseService {
	
	@Autowired
	private DeviceDetectionDao deviceDetectionDao;
	
	@Autowired
	private DeviceDetectionItemDao itemDao;

	@Autowired
	private MifiManageDao mifiManageDao;
	
	/**
	 * 
	 * @Description 根据ID取数据
	 * @param id
	 * @return DeviceDetection  
	 * @author yifang.huang
	 * @date 2016年11月9日 下午3:50:13
	 */
	public DeviceDetection get(String id) {
		DeviceDetection oldBean = deviceDetectionDao.get(id);
		if (oldBean != null) {
			DeviceDetection newBean = new DeviceDetection();
			BeanUtils.copyProperties(oldBean, newBean);
			
			// 清除指定对象缓存
			deviceDetectionDao.getSession().evict(oldBean);
			
			// 检查项列表
			newBean.setItemList(itemDao.findList(newBean.getId()));
			return newBean;
		}
		return null;
	}

	/**
	 * 
	 * @Description 设备检查保存
	 * @param bean 设备检查数据
	 * @param sourceType 设备所属渠道商编号
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年11月10日 上午9:47:40
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> save(DeviceDetection bean, String sourceType) {
		
		Map<String, String> map = new HashMap<String, String>();
		String isUpdate = "0";

		// 参数
		String id = bean.getId();
		String imei = bean.getImei();
		String useFlag = bean.getUseFlag();
		String itemStrs = bean.getItemStrs();
		
		// 增加 OR 修改
		if(StringUtils.isNotBlank(id)) {
			isUpdate = "1";
		}
		
		// 保存设备检查
		bean.setSourceType(sourceType);
		deviceDetectionDao.save(bean);
		
		id = bean.getId();
		// 保存检查项信息
		if ("1".equals(isUpdate)) {  // 如果是修改先删除已有检查项数据
			itemDao.deleteByDeviceDetectionId(id);
		}
		String[] itemStrArr = itemStrs.split("#");
		for (String itemStr : itemStrArr) {
			String[] itemArr = itemStr.split(",");
			DeviceDetectionItem item = new DeviceDetectionItem();
			item.setId(IdGen.uuid());
			item.setDeviceDetectionId(id);
			item.setCode(itemArr[0]);
			item.setName(itemArr[1]);
			item.setSequence(Integer.valueOf(itemArr[2]));
			item.setResult(itemArr[3]);
			item.setCreateDate(new Date());
			itemDao.save(item);
		}
		
		// 修改设备状态
		mifiManageDao.updateMifilistUseFlag(useFlag, imei);

		map.put("code", "success");
		map.put("id", id);
		map.put("isUpdate", isUpdate);
		map.put("message", "保存成功！");
		return map;
	}
	
	/**
	 * 
	 * @Description 根据ID删除数据
	 * @param id 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年11月9日 下午3:51:05
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void delete(String id) {
		deviceDetectionDao.deleteById(id);
	}
	
	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<DeviceDetection>  
	 * @author yifang.huang
	 * @date 2016年11月9日 下午3:50:29
	 */
	public List<DeviceDetection> findList(DeviceDetectionCondition condition) {
		
		List<DeviceDetection> list = deviceDetectionDao.findList(condition);
		
		if (list==null || list.size()==0)
			return list;
		else {// 取检查项
			List<DeviceDetection> result = new ArrayList<DeviceDetection>();
			for (DeviceDetection bean : list) {
				bean.setItemList(itemDao.findList(bean.getId()));
				result.add(bean);
			}
			return result;
		}
		
	}
	
	/**
	 * 
	 * @Description 根据查询参数取分页数据
	 * @param page
	 * @param condition
	 * @return Page<DeviceDetection>  
	 * @author yifang.huang
	 * @date 2016年11月9日 下午3:50:37
	 */
	public Page<DeviceDetection> findPage(Page<DeviceDetection> page, DeviceDetectionCondition condition) {
		
		Page<DeviceDetection> result = deviceDetectionDao.findPage(page, condition);
		List<DeviceDetection> list = result.getList();
		
		if (list==null || list.size()==0)
			return result;
		else {// 取检查项
			List<DeviceDetection> tempList = new ArrayList<DeviceDetection>();
			for (DeviceDetection bean : list) {
				bean.setItemList(itemDao.findList(bean.getId()));
				tempList.add(bean);
			}
			result.setList(tempList);
			return result;
		}
		
	}

}
