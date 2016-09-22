package com.on36.haetae.server;

import com.lmax.disruptor.EventTranslator;
import com.on36.haetae.common.log.LogLevel;
import com.on36.haetae.http.Scheduler;
import com.on36.haetae.server.core.manager.DisruptorManager;
import com.on36.haetae.server.core.manager.event.EndpointEvent;
import com.on36.haetae.server.core.manager.event.LogEvent;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class MessageScheduler implements Scheduler {

	private final DisruptorManager disruptorManager;

	public MessageScheduler(DisruptorManager disruptorManager) {
		this.disruptorManager = disruptorManager;
	}

	@Override
	public void endpoint(final String channel, final String endpoint) {
		disruptorManager.getEndpointEventDisruptor()
				.publishEvent(new EventTranslator<EndpointEvent>() {
					@Override
					public void translateTo(EndpointEvent event,
							long sequence) {
						event.setChannel(channel);
						event.setEndpoint(endpoint);
					}
				});
	}

	@Override
	public void trace(final Object clazz, final LogLevel level,
			final String message) {
		trace(clazz, level, message, null);
	}

	@Override
	public void trace(final Object clazz, final LogLevel level,
			final String message, final Throwable e) {
		disruptorManager.getLogEventDisruptor()
				.publishEvent(new EventTranslator<LogEvent>() {
					@Override
					public void translateTo(LogEvent event, long sequence) {
						event.setLevel(level);
						event.setClazz(clazz);
						event.setMessage(message);
						event.setExcp(e);
					}
				});
	}

}
