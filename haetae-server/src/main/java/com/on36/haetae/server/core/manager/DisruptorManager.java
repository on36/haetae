package com.on36.haetae.server.core.manager;

import java.util.concurrent.ExecutorService;

import com.lmax.disruptor.dsl.Disruptor;
import com.on36.haetae.server.core.manager.event.EndpointEvent;
import com.on36.haetae.server.core.manager.event.EndpointEventFactory;
import com.on36.haetae.server.core.manager.event.LogEvent;
import com.on36.haetae.server.core.manager.event.LogEventFactory;
import com.on36.haetae.server.core.manager.event.handler.EndpointEventHandler;
import com.on36.haetae.server.core.manager.event.handler.LogEventHandler;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class DisruptorManager {

	private final ExecutorService disruptorExecutors;

	private static final int DEFAULT_SMALL_RINGBUFFER_SIZE = 64;

	private Disruptor<EndpointEvent> endpointEventDisruptor;

	private Disruptor<LogEvent> logEventDisruptor;

	@SuppressWarnings("unchecked")
	public DisruptorManager(ExecutorService executorService) {
		this.disruptorExecutors = executorService;

		this.endpointEventDisruptor = new Disruptor<>(
				new EndpointEventFactory(), DEFAULT_SMALL_RINGBUFFER_SIZE,
				disruptorExecutors);
		this.endpointEventDisruptor
				.handleEventsWith(new EndpointEventHandler());
		this.endpointEventDisruptor.start();

		this.logEventDisruptor = new Disruptor<>(new LogEventFactory(),
				DEFAULT_SMALL_RINGBUFFER_SIZE, disruptorExecutors);
		this.logEventDisruptor.handleEventsWith(new LogEventHandler());
		this.logEventDisruptor.start();
	}

	public Disruptor<EndpointEvent> getEndpointEventDisruptor() {
		return endpointEventDisruptor;
	}

	public Disruptor<LogEvent> getLogEventDisruptor() {
		return logEventDisruptor;
	}

	public void close() {
		endpointEventDisruptor.shutdown();
		logEventDisruptor.shutdown();
	}
}
