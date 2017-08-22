/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.uu.modules.sys.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uu.common.persistence.BaseDao;

@Repository
public class TableUtilDao extends BaseDao {

	public List<HashMap> findList(String tableName, String valueKey, String labelKey, String whereCond) {
		StringBuffer sb = new StringBuffer();
		sb.append("select distinct " + labelKey + "," + valueKey + " from " + tableName + " where 1 = 1 ");
		if (StringUtils.isNotBlank(whereCond)) {
			sb.append(whereCond);
		}
		return findBySql(sb.toString(), null, Map.class);
	}

	public String findLabel(String tableName, String valueKey, String labelKey, String whereCond, String value) {
		
		if ("mcc_def".equals(tableName)) {
			if (value.indexOf(",") != -1) {
				value = value.split(",")[0];
			}
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("select distinct " + labelKey + "," + valueKey + " from " + tableName + " where 1 = 1 ");
		if (StringUtils.isNotBlank(whereCond)) {
			sb.append(whereCond);
		}
		sb.append(" and " + valueKey + " = '" + value + "'");
		List<HashMap> list = findBySql(sb.toString(), null, Map.class);
		if (list.size() == 0) {
			return "";
		} else {
			return (String) list.get(0).get(labelKey);
		}
	}
}
