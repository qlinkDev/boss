/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.qlink.modules.cms.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.shiro.SecurityUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.qlink.common.persistence.Page;
import com.qlink.common.service.BaseService;
import com.qlink.common.utils.CacheUtils;
import com.qlink.common.utils.StringUtils;
import com.qlink.modules.cms.dao.CategoryDao;
import com.qlink.modules.cms.dao.LinkDao;
import com.qlink.modules.cms.entity.Article;
import com.qlink.modules.cms.entity.Category;
import com.qlink.modules.cms.entity.Link;
import com.qlink.modules.cms.entity.Site;
import com.qlink.modules.sys.utils.UserUtils;

/**
 * 链接Service
 * @author admin
 * @version 2013-01-15
 */
@Service
public class LinkService extends BaseService {

	@Autowired
	private LinkDao linkDao;
	
	@Autowired
	private CategoryDao categoryDao;
	
	public Link get(String id) {
		return linkDao.get(id);
	}
	
	//@Transactional(readOnly = false)
	public Page<Link> updateAndFind(Page<Link> page, Link link, boolean isDataScopeFilter) {
		// 更新过期的权重，间隔为“6”个小时
		Date updateExpiredWeightDate =  (Date)CacheUtils.get("updateExpiredWeightDateByLink");
		if (updateExpiredWeightDate == null || (updateExpiredWeightDate != null 
				&& updateExpiredWeightDate.getTime() < new Date().getTime())){
			linkDao.updateExpiredWeight();
			CacheUtils.put("updateExpiredWeightDateByLink", DateUtils.addHours(new Date(), 6));
		}
		DetachedCriteria dc = linkDao.createDetachedCriteria();
		dc.createAlias("category", "category");
		dc.createAlias("category.site", "category.site");
		if (link.getCategory()!=null && StringUtils.isNotBlank(link.getCategory().getId()) && !Category.isRoot(link.getCategory().getId())){
			Category category = categoryDao.get(link.getCategory().getId());
			if (category!=null){
				dc.add(Restrictions.or(
						Restrictions.eq("category.id", category.getId()),
						Restrictions.like("category.parentIds", "%,"+category.getId()+",%")));
				dc.add(Restrictions.eq("category.site.id", category.getSite().getId()));
				link.setCategory(category);
			}else{
				dc.add(Restrictions.eq("category.site.id", Site.getCurrentSiteId()));
			}
		}else{
			dc.add(Restrictions.eq("category.site.id", Site.getCurrentSiteId()));
		}
		if (StringUtils.isNotEmpty(link.getTitle())){
			dc.add(Restrictions.like("title", "%"+link.getTitle()+"%"));
		}
		if (link.getCreateBy()!=null && StringUtils.isNotBlank(link.getCreateBy().getId())){
			dc.add(Restrictions.eq("createBy.id", link.getCreateBy().getId()));
		}
		if (isDataScopeFilter){
			dc.createAlias("category.office", "categoryOffice").createAlias("createBy", "createBy");
			dc.add(dataScopeFilter(UserUtils.getUser(), "categoryOffice", "createBy"));
		}
		dc.add(Restrictions.eq(Link.FIELD_DEL_FLAG, link.getDelFlag()));
		dc.addOrder(Order.desc("weight"));
		dc.addOrder(Order.desc("updateDate"));
		return linkDao.find(page, dc);
	}

	//@Transactional(readOnly = false)
	public void save(Link link) {
		// 如果没有审核权限，则将当前内容改为待审核状态
		if (!SecurityUtils.getSubject().isPermitted("cms:link:audit")){
			link.setDelFlag(Link.DEL_FLAG_AUDIT);
		}
		// 如果栏目不需要审核，则将该内容设为发布状态
		if (link.getCategory()!=null&&StringUtils.isNotBlank(link.getCategory().getId())){
			Category category = categoryDao.get(link.getCategory().getId());
			if (!Article.YES.equals(category.getIsAudit())){
				link.setDelFlag(Article.DEL_FLAG_NORMAL);
			}
		}
		linkDao.clear();
		linkDao.save(link);
	}
	
	//@Transactional(readOnly = false)
	public void delete(String id, Boolean isRe) {
		linkDao.updateDelFlag(id, isRe!=null&&isRe?Link.DEL_FLAG_NORMAL:Link.DEL_FLAG_DELETE);
	}
	
	/**
	 * 通过编号获取内容标题
	 */
	public List<Object[]> findByIds(String ids) {
		List<Object[]> list = Lists.newArrayList();
		Long[] idss = (Long[])ConvertUtils.convert(StringUtils.split(ids,","), Long.class);
		if (idss.length>0){
			List<Link> l = linkDao.findByIdIn(idss);
			for (Link e : l){
				list.add(new Object[]{e.getId(),StringUtils.abbr(e.getTitle(),50)});
			}
		}
		return list;
	}

}
