package com.on36.haetae.http;

import com.on36.haetae.common.log.LogLevel;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public interface Scheduler {

	void endpoint(String channel, String endpoint);

	void trace(Object clazz, LogLevel level, String message);

	void trace(Object clazz, LogLevel level, String message, Throwable e);
}
