/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package main.java.com.qlink.modules.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.service.BaseService;
import com.uu.modules.sys.dao.AreaDao;
import com.uu.modules.sys.entity.Area;
import com.uu.modules.sys.utils.UserUtils;

/**
 * 区域Service
 * @author admin
 * @version 2013-5-29
 */
@Service
public class AreaService extends BaseService {

	@Autowired
	private AreaDao areaDao;
	
	public Area get(String id) {
		return areaDao.get(id);
	}
	
	public List<Area> findAll(){
		return UserUtils.getAreaList();
	}

	//@Transactional(readOnly = false)
	public void save(Area area) {
		area.setParent(this.get(area.getParent().getId()));
		String oldParentIds = area.getParentIds(); // 获取修改前的parentIds，用于更新子节点的parentIds
		area.setParentIds(area.getParent().getParentIds()+area.getParent().getId()+",");
		areaDao.clear();
		areaDao.save(area);
		// 更新子节点 parentIds
		List<Area> list = areaDao.findByParentIdsLike("%,"+area.getId()+",%");
		for (Area e : list){
			e.setParentIds(e.getParentIds().replace(oldParentIds, area.getParentIds()));
		}
		areaDao.save(list);
		UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
	}
	
	//@Transactional(readOnly = false)
	public void delete(String id) {
		areaDao.deleteById(id, "%,"+id+",%");
		UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
	}
	
}
