package com.on36.haetae.common.log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 日志工厂类.
 * 
 * @author zhanghr
 *
 */
public class LoggerFactory {

	private static ConcurrentMap<Class<?>, Logger> loggerMap = new ConcurrentHashMap<Class<?>, Logger>();

	private LoggerFactory() {
	};

	public static Logger getLogger(Class<?> name) {
		Logger logger = loggerMap.get(name);
		if (logger != null) {
			return logger;
		} else {
			logger = new LoggerAppender(name);
			loggerMap.putIfAbsent(name, logger);
		}
		return logger;
	}
}
