package com.uu.common.utils.thread;

/**
 * 
 * @Description 本线程设置了一个超时时间,该线程开始运行后，经过指定超时时间,
 * 该线程会抛出一个未检查异常通知调用该线程的程序超时,
 * 在超时结束前可以调用该类的cancel方法取消计时
 * @author yifang.huang
 * @date 2016年10月18日 上午11:06:58
 */
public class TimeoutThread extends Thread {

	/**
	 * 计时器超时时间
	 */
	private long timeout;

	/**
	 * 计时是否被取消
	 */
	private boolean isCanceled = false;

	/**
	 * 当计时器超时时抛出的异常
	 */
	private TimeoutException timeoutException;

	/**
	 * 构造器 @param timeout 指定超时的时间
	 */
	public TimeoutThread(long timeout, TimeoutException timeoutErr) {
		super();
		this.timeout = timeout;
		this.timeoutException = timeoutErr;
		//设置本线程为守护线程
		this.setDaemon(true);
	}
	
	/**
	 * 取消计时
	 */
	public synchronized void cancel() {
		isCanceled = true;
	}
	
	/**
	　* 启动超时计时器
	　*/
	public void run() {
		try {
			Thread.sleep(timeout);
			if(!isCanceled)
			throw timeoutException;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
