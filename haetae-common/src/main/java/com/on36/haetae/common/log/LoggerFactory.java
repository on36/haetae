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

	private static ConcurrentMap<String, Logger> loggerMap = new ConcurrentHashMap<String, Logger>();

	private LoggerFactory() {
	};

	public static Logger getLogger(Class<?> className) {
		Logger logger = loggerMap.get(className.getName());
		if (logger != null) {
			return logger;
		} else {
			logger = new LoggerAppender(className.getName());
			loggerMap.putIfAbsent(className.getName(), logger);
		}
		return logger;
	}

	public static Logger getLogger(String className) {
		Logger logger = loggerMap.get(className);
		if (logger != null) {
			return logger;
		} else {
			logger = new LoggerAppender(className);
			loggerMap.putIfAbsent(className, logger);
		}
		return logger;
	}

	public static Logger getLogger(Object className) {
		if (className instanceof String)
			return getLogger(className.toString());
		else
			return getLogger((Class<?>) className);
	}
}
