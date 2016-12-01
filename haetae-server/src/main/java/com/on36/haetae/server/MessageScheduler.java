package com.on36.haetae.server;

import com.on36.haetae.common.log.LogLevel;
import com.on36.haetae.hsr.EventBus;
import com.on36.haetae.http.Scheduler;
import com.on36.haetae.server.core.manager.event.EndpointEvent;
import com.on36.haetae.server.core.manager.event.LogEvent;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class MessageScheduler implements Scheduler {

	@Override
	public void endpoint(final String channel, final String endpoint) {
		EventBus.dispatch(new EndpointEvent(channel, endpoint));
	}

	@Override
	public void trace(final Object clazz, final LogLevel level,
			final String message) {
		trace(clazz, level, message, null);
	}

	@Override
	public void trace(final Object clazz, final LogLevel level,
			final String message, final Throwable e) {
		EventBus.dispatch(new LogEvent(clazz, message, level, e));
	}

}
