package main.java.com.qlink.modules.utils;

import net.sf.json.JSONObject;

import com.uu.common.persistence.DataEntity;
import com.uu.common.utils.DateUtils;
import com.uu.common.utils.StringUtils;

public class ApiUtils {
	public static void buildCommonAttributes(JSONObject jsonObj, DataEntity<?> record) {
		jsonObj.put("remarks", record.getRemarks());
		jsonObj.put("createBy", null == record.getCreateBy() ? StringUtils.EMPTY : record.getCreateBy().getId());
		if (null != record.getCreateDate()) {
			jsonObj.put("createDate", DateUtils.formatDateTime(record.getCreateDate()));
		}
		jsonObj.put("updateBy", null == record.getUpdateBy() ? StringUtils.EMPTY : record.getUpdateBy().getId());
		if (null != record.getUpdateDate()) {
			jsonObj.put("updateDate", DateUtils.formatDateTime(record.getUpdateDate()));
		}
		jsonObj.put("delFlag", record.getDelFlag());
	}
}
