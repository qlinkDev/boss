/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.uu.modules.sys.service;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.persistence.Page;
import com.uu.common.service.BaseService;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.StringUtils;
import com.uu.modules.sys.dao.LogDao;
import com.uu.modules.sys.entity.Log;
import com.uu.modules.sys.entity.User;
import com.uu.modules.sys.utils.UserUtils;
import com.uu.modules.utils.Constants;

/**
 * 日志Service
 * @author admin
 * @version 2013-6-2
 */
@Service
public class LogService extends BaseService {

	@Autowired
	private LogDao logDao;
	
	public Log get(String id) {
		return logDao.get(id);
	}
	
	public Page<Log> find(Page<Log> page, Map<String, Object> paramMap) {
		StringBuffer sqlString = new StringBuffer("select t.* from sys_log t left join sys_user u on t.create_by = u.id  where 1=1"); 
		String createName = ObjectUtils.toString(paramMap.get("createByName"));
		if (StringUtils.isNotBlank(createName)){
			sqlString.append(" and u.name like ").append("'%"+createName+"%'");
		}
		
		String requestUri = ObjectUtils.toString(paramMap.get("requestUri"));
		if (StringUtils.isNotBlank(requestUri)){
			sqlString.append(" and t.request_uri like ").append("'%"+requestUri+"%'");
		}
		
		String exceptions = ObjectUtils.toString(paramMap.get("exception"));
		if (StringUtils.isNotBlank(exceptions)){
			sqlString.append(" and t.exception like ").append("'%"+exceptions+"%'");
		}
		String exception = ObjectUtils.toString(paramMap.get("type"));
		if (StringUtils.isNotBlank(exception)){
			sqlString.append(" and t.type = ").append(exception);
		}
		
		Date beginDate = DateUtils.parseDate(paramMap.get("beginDate"));
		if (beginDate == null){
			beginDate = DateUtils.setDays(new Date(), 1);
			beginDate =DateUtils.getDateStart(beginDate);
			paramMap.put("beginDate", DateUtils.formatDate(beginDate, "yyyy-MM-dd HH:mm:ss"));
		}
		Date endDate = DateUtils.parseDate(paramMap.get("endDate"));
		if (endDate == null){
			endDate = DateUtils.addDays(DateUtils.addMonths(beginDate, 1), -1);
			endDate =DateUtils.getDateEnd(endDate);
			paramMap.put("endDate", DateUtils.formatDate(endDate, "yyyy-MM-dd HH:mm:ss"));
		}
		sqlString.append(" and t.create_date between ");
		sqlString.append("'").append(DateUtils.formatDate(beginDate, "yyyy-MM-dd HH:mm:ss")).append("'");
		sqlString.append(" and ");
		sqlString.append("'").append(DateUtils.formatDate(endDate, "yyyy-MM-dd HH:mm:ss")).append("'");
		sqlString.append(" order by create_date  desc");
		return logDao.findBySql(page, sqlString.toString(), null, Log.class);
	}
	
	
	/**
	 * 保存操作日志
	 * @athor shuxin
	 * @date 2016年7月6日上午11:17:39
	 * @param request 请求参数
	 * @param operate 操作模块（例如：[模块名称]-添加、[模块名称]-修改[{}]、[模块名称]-删除[{}]）
	 * @param param 参数
	 * void 
	 */
	public  void saveOperateLog(HttpServletRequest request, String operate, String... param){
		Log  log = new Log();
		User user = UserUtils.getUser();
		log.setCreateBy(user);
		log.setCreateDate(new Date());
		String method = request.getMethod();
		log.setMethod(method);
		String requestUri = request.getRequestURI();
		log.setRequestUri(requestUri);
		String remoteAddr = request.getRemoteAddr();
		log.setRemoteAddr(remoteAddr);
		log.setType("4");
		if(param == null || param.length == 0){
			log.setException(operate);
		} else {
			StringBuffer buff = new StringBuffer();
			String[] tempStr = operate.split("[{}]");
			for (int i = 0; i < tempStr.length; i++) {
				if(!"".equals(tempStr[i])){
					if(param.length == (i/2)){
						buff.append(tempStr[i]);
					} else {
						buff.append(tempStr[i]).append(param[i/2]);
					}
				}
			}
			log.setException(buff.toString());
		}
		logDao.save(log);
	}
	
	/**
	 * 
	 * @Description 系统定时日志
	 * @param className	类名
	 * @param method 方法名
	 * @param operate
	 * @param param 
	 * @return void  
	 * @author yifang.huang
	 * @date 2017年3月6日 下午4:26:38
	 */
	public  void saveTimingLog(String className, String method, String operate, String... param){
		Log  log = new Log();
		User user = UserUtils.getUserById(Constants.SUPERUSR_ID);
		log.setCreateBy(user);
		log.setCreateDate(new Date());
		log.setMethod(method);
		log.setRequestUri(className);
		log.setRemoteAddr("127.0.0.1");
		log.setType("3");
		if(param == null || param.length == 0){
			log.setException(operate);
		} else {
			StringBuffer buff = new StringBuffer();
			String[] tempStr = operate.split("[{}]");
			for (int i = 0; i < tempStr.length; i++) {
				if(!"".equals(tempStr[i])){
					if(param.length == (i/2)){
						buff.append(tempStr[i]);
					} else {
						buff.append(tempStr[i]).append(param[i/2]);
					}
				}
			}
			log.setException(buff.toString());
		}
		logDao.save(log);
	}
	
}
