/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.uu.modules.sys.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uu.common.web.BaseController;

/**
 * 标签Controller
 * 
 * @author admin
 * @version 2013-3-23
 */
@Controller
@RequestMapping("${adminPath}/tag")
public class TagController extends BaseController {

	/**
	 * 树结构选择标签（treeselect.tag）
	 */
	@RequiresUser
	@RequestMapping("treeselect")
	public String treeselect(HttpServletRequest request, Model model) {
		model.addAttribute("url", request.getParameter("url")); // 树结构数据URL
		model.addAttribute("extId", request.getParameter("extId")); // 排除的编号ID
		model.addAttribute("checked", request.getParameter("checked")); // 是否可复选
		model.addAttribute("selectIds", request.getParameter("selectIds")); // 指定默认选中的ID
		model.addAttribute("module", request.getParameter("module")); // 过滤栏目模型（仅针对CMS的Category树）
		return "modules/sys/tagTreeselect";
	}

	/**
	 * 图标选择标签（iconselect.tag）
	 */
	@RequiresUser
	@RequestMapping("iconselect")
	public String iconselect(HttpServletRequest request, Model model) {
		model.addAttribute("value", request.getParameter("value"));
		return "modules/sys/tagIconselect";
	}

}
