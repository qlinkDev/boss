package com.qlink.common.utils.thread;

/**
 * 
 * @Description 自定义超时异常类
 * @author yifang.huang
 * @date 2016年10月18日 上午11:14:52
 */
public class TimeoutException extends RuntimeException {

	/**
	　* 序列化号
	　*/
	private static final long serialVersionUID = -3067895734855547789L;

	public TimeoutException(String errMessage) {
		super(errMessage);
	}
}
