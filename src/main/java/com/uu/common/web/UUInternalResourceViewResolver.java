/** 
 * @Package com.uu.common.web 
 * @Description 
 * @author yifang.huang
 * @date 2016年4月6日 下午12:05:48 
 * @version V1.0 
 */ 
package com.uu.common.web;

import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/** 
 * @Description 视图解析重写，过滤APP请求
 * @author yifang.huang
 * @date 2016年4月6日 下午12:05:48 
 */
public class UUInternalResourceViewResolver extends InternalResourceViewResolver {

	private static final boolean jstlPresent = ClassUtils.isPresent(
			"javax.servlet.jsp.jstl.core.Config", InternalResourceViewResolver.class.getClassLoader());

	private Boolean alwaysInclude;

	private Boolean exposeContextBeansAsAttributes;

	private String[] exposedContextBeanNames;


	/**
	 * Sets the default {@link #setViewClass view class} to {@link #requiredViewClass}:
	 * by default {@link InternalResourceView}, or {@link JstlView} if the JSTL API
	 * is present.
	 */
	public UUInternalResourceViewResolver() {
		Class<?> viewClass = requiredViewClass();
		if (viewClass.equals(InternalResourceView.class) && jstlPresent) {
			viewClass = JstlView.class;
		}
		setViewClass(viewClass);
	}

	/**
	 * This resolver requires {@link InternalResourceView}.
	 */
	@Override
	protected Class<?> requiredViewClass() {
		return InternalResourceView.class;
	}


	/**
	 * Specify whether to always include the view rather than forward to it.
	 * <p>Default is "false". Switch this flag on to enforce the use of a
	 * Servlet include, even if a forward would be possible.
	 * @see InternalResourceView#setAlwaysInclude
	 */
	public void setAlwaysInclude(boolean alwaysInclude) {
		this.alwaysInclude = Boolean.valueOf(alwaysInclude);
	}

	/**
	 * Set whether to make all Spring beans in the application context accessible
	 * as request attributes, through lazy checking once an attribute gets accessed.
	 * <p>This will make all such beans accessible in plain {@code ${...}}
	 * expressions in a JSP 2.0 page, as well as in JSTL's {@code c:out}
	 * value expressions.
	 * <p>Default is "false".
	 * @see InternalResourceView#setExposeContextBeansAsAttributes
	 */
	public void setExposeContextBeansAsAttributes(boolean exposeContextBeansAsAttributes) {
		this.exposeContextBeansAsAttributes = exposeContextBeansAsAttributes;
	}

	/**
	 * Specify the names of beans in the context which are supposed to be exposed.
	 * If this is non-null, only the specified beans are eligible for exposure as
	 * attributes.
	 * @see InternalResourceView#setExposedContextBeanNames
	 */
	public void setExposedContextBeanNames(String[] exposedContextBeanNames) {
		this.exposedContextBeanNames = exposedContextBeanNames;
	}


	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		InternalResourceView view = (InternalResourceView) super.buildView(viewName);
		
		// app页面完整路径/WEB-INF/**/app/**
		if (viewName.indexOf("/app/") != -1) {
			view.setUrl(viewName);
		}
		
		if (this.alwaysInclude != null) {
			view.setAlwaysInclude(this.alwaysInclude);
		}
		if (this.exposeContextBeansAsAttributes != null) {
			view.setExposeContextBeansAsAttributes(this.exposeContextBeansAsAttributes);
		}
		if (this.exposedContextBeanNames != null) {
			view.setExposedContextBeanNames(this.exposedContextBeanNames);
		}
		view.setPreventDispatchLoop(true);
		return view;
	}
}
