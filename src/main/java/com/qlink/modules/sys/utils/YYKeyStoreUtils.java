/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package main.java.com.qlink.modules.sys.utils;

import java.util.List;

import com.uu.common.utils.CacheUtils;
import com.uu.common.utils.SpringContextHolder;
import com.uu.modules.sys.dao.YYKeyStoreDao;
import com.uu.modules.sys.entity.YYKeyStore;

/**
 * 密钥库工具类
 * @author admin
 * @version 2013-5-29
 */
public class YYKeyStoreUtils {
	
	private static YYKeyStoreDao keyStoreDao = SpringContextHolder.getBean(YYKeyStoreDao.class);

	public static final String CACHE_KEY_STORE_LIST = "keyStoreList";
	
	public static List<YYKeyStore> getKeyStoreList() {
		@SuppressWarnings("unchecked")
		List<YYKeyStore> keyStoreList = (List<YYKeyStore>)CacheUtils.get(CACHE_KEY_STORE_LIST);
		if (keyStoreList == null) {
			keyStoreList = keyStoreDao.findAllList();
			CacheUtils.put(CACHE_KEY_STORE_LIST, keyStoreList);
		}
		
		return keyStoreList;
	}
	
}
