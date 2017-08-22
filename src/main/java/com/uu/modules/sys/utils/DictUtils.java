/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.uu.modules.sys.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.uu.common.utils.CacheUtils;
import com.uu.common.utils.SpringContextHolder;
import com.uu.modules.sys.dao.TableUtilDao;
import com.uu.modules.sys.entity.Dict;
import com.uu.modules.sys.service.DictService;

/**
 * 字典工具类
 * 
 * @author admin
 * @version 2013-5-29
 */
public class DictUtils {

	private static DictService dictService = SpringContextHolder.getBean(DictService.class);

	// @Autowired
	private static TableUtilDao tableUtilDao = SpringContextHolder.getBean(TableUtilDao.class);

	public static final String CACHE_DICT_MAP = "dictMap";

	public static String getDictLabel(String value, String type, String defaultValue) {
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(value)) {
			for (Dict dict : getDictList(type)) {
				if (type.equals(dict.getType()) && value.equals(dict.getValue())) {
					return dict.getLabel();
				}
			}
		}
		return defaultValue;
	}

	public static String getDictValue(String label, String type, String defaultLabel) {
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(label)) {
			for (Dict dict : getDictList(type)) {
				if (type.equals(dict.getType()) && label.equals(dict.getLabel())) {
					return dict.getValue();
				}
			}
		}
		return defaultLabel;
	}

	public static List<Dict> getDictList(String type) {
		@SuppressWarnings("unchecked")
		Map<String, List<Dict>> dictMap = (Map<String, List<Dict>>) CacheUtils.get(CACHE_DICT_MAP);
		if (dictMap == null) {
			dictMap = Maps.newHashMap();
			for (Dict dict : dictService.findAllList()) {
				List<Dict> dictList = dictMap.get(dict.getType());
				if (dictList != null) {
					dictList.add(dict);
				} else {
					dictMap.put(dict.getType(), Lists.newArrayList(dict));
				}
			}
			CacheUtils.put(CACHE_DICT_MAP, dictMap);
		}

		List<Dict> dictList = dictMap.get(type);
		if (dictList == null) {
			dictList = Lists.newArrayList();
		}
		return dictList;
	}

	public static List<HashMap> getListByTable(String tableName, String valueKey, String labelKey) {
		List<HashMap> dictList = getListByTableAndWhere(tableName, valueKey, labelKey, null);
		if (dictList == null) {
			dictList = Lists.newArrayList();
		}
		return dictList;
	}

	public static List<HashMap> getListByTableAndWhere(String tableName, String valueKey, String labelKey,
			String whereCond) {
		List<HashMap> dictList = tableUtilDao.findList(tableName, valueKey, labelKey, whereCond);
		if (dictList == null) {
			dictList = Lists.newArrayList();
		}
		return dictList;
	}

	public static String getLabelByTable(String tableName, String valueKey, String labelKey, String value) {
		return getLabelByTableAndWhere(tableName,valueKey,labelKey,null,value);
	}

	public static String getLabelByTableAndWhere(String tableName, String valueKey, String labelKey, String whereCond, String value) {
		return tableUtilDao.findLabel(tableName,valueKey,labelKey,whereCond,value);
	}

}
