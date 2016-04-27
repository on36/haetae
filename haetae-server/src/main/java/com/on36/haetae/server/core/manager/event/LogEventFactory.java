package com.on36.haetae.server.core.manager.event;

import com.lmax.disruptor.EventFactory;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class LogEventFactory implements EventFactory<LogEvent> {

	public LogEvent newInstance() {
		return new LogEvent();
	}

}
