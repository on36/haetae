package com.on36.haetae.server.core.manager.event;

import com.on36.haetae.common.log.LogLevel;

/**
 * @author zhanghr
 * @date 2016年1月11日
 */
public class LogEvent {

	private Object clazz;

	private String message;

	private LogLevel level;

	private Throwable excp;

	
	public LogEvent(Object clazz, String message, LogLevel level,
			Throwable excp) {
		super();
		this.clazz = clazz;
		this.message = message;
		this.level = level;
		this.excp = excp;
	}

	public Object getClazz() {
		return clazz;
	}

	public void setClazz(Object clazz) {
		this.clazz = clazz;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LogLevel getLevel() {
		return level;
	}

	public void setLevel(LogLevel level) {
		this.level = level;
	}

	public Throwable getExcp() {
		return excp;
	}

	public void setExcp(Throwable excp) {
		this.excp = excp;
	}

}
