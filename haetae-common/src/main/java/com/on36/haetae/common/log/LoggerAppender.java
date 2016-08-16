package com.on36.haetae.common.log;

/**
 * 基于slf4j接口封装.
 * 
 * @author zhanghr
 * 
 */
public class LoggerAppender implements Logger {

	private org.slf4j.Logger log;

	public LoggerAppender(Class<?> name) {
		log = org.slf4j.LoggerFactory.getLogger(name);
	}

	@Override
	public void info(String message) {
		log.info(message);
	}

	@Override
	public void info(String message, Throwable t) {
		log.info(message, t);
	}

	@Override
	public void debug(String message) {
		log.debug(message);
	}

	@Override
	public void debug(String message, Throwable t) {
		log.debug(message, t);
	}

	@Override
	public void error(String message) {
		log.error(message);
	}

	@Override
	public void error(String message, Throwable t) {
		log.error(message, t);
	}

	@Override
	public void warn(String message) {
		log.warn(message);
	}

	@Override
	public void warn(String message, Throwable t) {
		log.warn(message, t);
	}
}
