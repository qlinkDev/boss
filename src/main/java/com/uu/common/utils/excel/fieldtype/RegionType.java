/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.uu.common.utils.excel.fieldtype;

import com.uu.modules.om.entity.Region;

/**
 * 字段类型转换
 * @author yuxiaoyu
 * @version 2016-03-21
 */
public class RegionType {

	/**
	 * 设置对象值（导出）
	 */
	public static String setValue(Object val) {
		if (val != null && ((Region)val).getName() != null){
			return ((Region)val).getName();
		}
		return "";
	}
}
