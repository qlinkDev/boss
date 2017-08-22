/** 
 * @Package com.uu.modules.om.service 
 * @Description 
 * @author yifang.huang
 * @date 2016年12月16日 下午1:37:36 
 * @version V1.0 
 */ 
package com.uu.modules.om.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.modules.om.condition.AdvertisingCondition;
import com.uu.modules.om.dao.AdvertisingDao;
import com.uu.modules.om.dao.AdvertisingItemDao;
import com.uu.modules.om.entity.Advertising;
import com.uu.modules.om.entity.AdvertisingItem;

/** 
 * @Description 广告   业务处理类
 * @author yifang.huang
 * @date 2016年12月16日 下午1:37:36 
 */
@Service
public class AdvertisingService extends BaseService {
	
	@Autowired
	private AdvertisingDao advertisingDao;
	
	@Autowired
	private AdvertisingItemDao itemDao;

	/**
	 * 
	 * @Description 根据ID取数据
	 * @param id
	 * @return Advertising  
	 * @author yifang.huang
	 * @date 2016年12月16日 下午2:09:34
	 */
	public Advertising get(String id) {
		Advertising oldBean = advertisingDao.get(id);
		if (oldBean != null) {
			Advertising newBean = new Advertising();
			BeanUtils.copyProperties(oldBean, newBean);
			
			// 清除指定对象缓存
			advertisingDao.getSession().evict(oldBean);
			
			// 广告位列表
			newBean.setItemList(itemDao.findList(newBean.getId()));
			return newBean;
		}
		return null;
	}

	/**
	 * 
	 * @Description 广告保存
	 * @param bean
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2016年12月16日 下午2:09:25
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> save(Advertising bean) {
		
		Map<String, String> map = new HashMap<String, String>();
		String isUpdate = "0";

		// 参数
		String id = bean.getId();
		String name = bean.getName();
		String type = bean.getType();
		String sourceTypes = bean.getSourceTypes();
		String itemStrs = bean.getItemStrs();
		
		// 参数判断
		if (StringUtils.isBlank(name)) {
			map.put("status", "error");
			map.put("message", "广告名称不能为空!");
			return map;
		}
		if (StringUtils.isBlank(type)) {
			map.put("status", "error");
			map.put("message", "广告类型不能为空!");
			return map;
		}
		if (StringUtils.isBlank(sourceTypes)) {
			map.put("status", "error");
			map.put("message", "广告所属渠道不能为空!");
			return map;
		}
		
		// 增加 OR 修改
		if(StringUtils.isNotBlank(id)) {
			isUpdate = "1";
		}
		
		// 保存广告
		advertisingDao.save(bean);
		
		id = bean.getId();
		// 保存广告位信息
		if ("1".equals(isUpdate)) {  // 如果是修改先删除已有广告位数据
			itemDao.deleteByAdvertisingId(id);
		}
		String[] itemStrArr = itemStrs.split("#@");
		AdvertisingItem item = null;
		for (String itemStr : itemStrArr) {
			String[] itemArr = itemStr.split(",@");
			item = new AdvertisingItem();
			item.setAdvertisingId(id);
			item.setImgPath(itemArr[0]);
			item.setUrl(itemArr[1]);
			item.setSequence(Integer.valueOf(itemArr[2]));
			item.setShowBaiduAd(itemArr[3]);
			itemDao.save(item);
		}

		map.put("status", "success");
		map.put("id", id);
		map.put("isUpdate", isUpdate);
		map.put("message", "保存成功！");
		return map;
	}

	/**
	 * 
	 * @Description 广告上架
	 * @param paramMap
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2017年2月13日 下午3:30:42
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized Map<String, String> saveAndShelfUp(Map<String, Object> paramMap) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		// 参数
		String id = ObjectUtils.toString(paramMap.get("id"));
		
		// 参数判断
		if (StringUtils.isBlank(id)) {
			map.put("code", "-1");
			map.put("msg", "请选择要上架的广告");
			return map;
		}
		// 广告对象
		Advertising bean = advertisingDao.get(id);	
		if (bean == null) {
			map.put("code", "-1");
			map.put("msg", "广告未找到");
			return map;
		}
		bean.setShelfUpDown(Advertising.SHELF_UP);
		advertisingDao.save(bean);

		map.put("code", "1");
		map.put("msg", "广告上架成功");
		return map;
	}

	/**
	 * 
	 * @Description 广告下架
	 * @param paramMap
	 * @return Map<String,String>  
	 * @author yifang.huang
	 * @date 2017年2月13日 下午3:30:04
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized Map<String, String> saveAndShelfDown(Map<String, Object> paramMap) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		// 参数
		String id = ObjectUtils.toString(paramMap.get("id"));
		
		// 参数判断
		if (StringUtils.isBlank(id)) {
			map.put("code", "-1");
			map.put("msg", "请选择要下架的广告");
			return map;
		}
		// 广告对象
		Advertising bean = advertisingDao.get(id);	
		if (bean == null) {
			map.put("code", "-1");
			map.put("msg", "广告未找到");
			return map;
		}
		bean.setShelfUpDown(Advertising.SHELF_DOWN);
		advertisingDao.save(bean);

		map.put("code", "1");
		map.put("msg", "广告下架成功");
		return map;
	}
	
	/**
	 * 
	 * @Description 根据ID删除数据
	 * @param id 
	 * @return void  
	 * @author yifang.huang
	 * @date 2016年12月16日 下午2:09:48
	 */
	//@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void delete(String id) {
		advertisingDao.deleteById(id);
	}
	
	/**
	 * 
	 * @Description 根据查询参数取列表数据
	 * @param condition
	 * @return List<Advertising>  
	 * @author yifang.huang
	 * @date 2016年12月16日 下午2:10:01
	 */
	public List<Advertising> findList(AdvertisingCondition condition) {
		
		List<Advertising> list = advertisingDao.findList(condition);
		
		if (list==null || list.size()==0)
			return list;
		else {// 取广告位
			List<Advertising> result = new ArrayList<Advertising>();
			for (Advertising bean : list) {
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
	 * @return Page<Advertising>  
	 * @author yifang.huang
	 * @date 2016年12月16日 下午2:10:09
	 */
	public Page<Advertising> findPage(Page<Advertising> page, AdvertisingCondition condition) {
		
		Page<Advertising> result = advertisingDao.findPage(page, condition);
		List<Advertising> list = result.getList();
		
		if (list==null || list.size()==0)
			return result;
		else {// 取广告位
			List<Advertising> tempList = new ArrayList<Advertising>();
			for (Advertising bean : list) {
				bean.setItemList(itemDao.findList(bean.getId()));
				tempList.add(bean);
			}
			result.setList(tempList);
			return result;
		}
		
	}
	
}
