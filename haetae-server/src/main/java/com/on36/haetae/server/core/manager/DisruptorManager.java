package com.on36.haetae.server.core.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.dsl.Disruptor;
import com.on36.haetae.net.udp.Message;
import com.on36.haetae.net.udp.Message.Title;
import com.on36.haetae.server.core.manager.event.LogEvent;
import com.on36.haetae.server.core.manager.event.LogEventFactory;
import com.on36.haetae.server.core.manager.event.RecievedEvent;
import com.on36.haetae.server.core.manager.event.RecievedEventFactory;
import com.on36.haetae.server.core.manager.event.SendEvent;
import com.on36.haetae.server.core.manager.event.SendEventFactory;
import com.on36.haetae.server.core.manager.event.handler.LogEventHandler;
import com.on36.haetae.server.core.manager.event.handler.RecievedEndpointEventHandler;
import com.on36.haetae.server.core.manager.event.handler.RecievedEventHandler;
import com.on36.haetae.server.core.manager.event.handler.RecievedSessionEventHandler;
import com.on36.haetae.server.core.manager.event.handler.SendEventHandler;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class DisruptorManager {

	private ExecutorService disruptorExecutors;

	private static final int DEFAULT_SMALL_RINGBUFFER_SIZE = 64;

	private Disruptor<SendEvent> sendEventDisruptor;
	private Disruptor<RecievedEvent> recievedEndpointEventDisruptor;
	private Disruptor<RecievedEvent> recievedSessionEventDisruptor;
	private Disruptor<RecievedEvent> recievedTestEventDisruptor;

	private Disruptor<LogEvent> logEventDisruptor;

	@SuppressWarnings("unchecked")
	public DisruptorManager() {
		this.disruptorExecutors = Executors.newCachedThreadPool();

		this.sendEventDisruptor = new Disruptor<>(new SendEventFactory(),
				DEFAULT_SMALL_RINGBUFFER_SIZE, disruptorExecutors);
		this.sendEventDisruptor.handleEventsWith(new SendEventHandler());
		this.sendEventDisruptor.start();

		this.recievedEndpointEventDisruptor = new Disruptor<>(
				new RecievedEventFactory(), DEFAULT_SMALL_RINGBUFFER_SIZE,
				disruptorExecutors);
		this.recievedEndpointEventDisruptor
				.handleEventsWith(new RecievedEndpointEventHandler());
		this.recievedEndpointEventDisruptor.start();

		this.recievedSessionEventDisruptor = new Disruptor<>(
				new RecievedEventFactory(), DEFAULT_SMALL_RINGBUFFER_SIZE,
				disruptorExecutors);
		this.recievedSessionEventDisruptor
				.handleEventsWith(new RecievedSessionEventHandler());
		this.recievedSessionEventDisruptor.start();

		this.logEventDisruptor = new Disruptor<>(new LogEventFactory(),
				DEFAULT_SMALL_RINGBUFFER_SIZE, disruptorExecutors);
		this.logEventDisruptor.handleEventsWith(new LogEventHandler());
		this.logEventDisruptor.start();

		this.recievedTestEventDisruptor = new Disruptor<>(
				new RecievedEventFactory(), DEFAULT_SMALL_RINGBUFFER_SIZE,
				disruptorExecutors);
		this.recievedTestEventDisruptor
				.handleEventsWith(new RecievedEventHandler());
		this.recievedTestEventDisruptor.start();
	}

	public Disruptor<SendEvent> getSendEventDisruptor() {
		return sendEventDisruptor;
	}

	public Disruptor<LogEvent> getLogEventDisruptor() {
		return logEventDisruptor;
	}

	public Disruptor<RecievedEvent> getRecievedEventDisruptor(Message message) {
		if (Title.ENDPOINT.equals(message.title()))
			return recievedEndpointEventDisruptor;
		else if (Title.SESSSION.equals(message.title()))
			return recievedSessionEventDisruptor;
		else
			return recievedTestEventDisruptor;
	}

	public void close() {
		sendEventDisruptor.shutdown();
		recievedEndpointEventDisruptor.shutdown();
		recievedSessionEventDisruptor.shutdown();
		recievedTestEventDisruptor.shutdown();
		logEventDisruptor.shutdown();
		disruptorExecutors.shutdown();
	}
}
