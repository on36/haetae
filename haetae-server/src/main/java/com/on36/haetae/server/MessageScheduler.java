package com.on36.haetae.server;

import com.lmax.disruptor.EventTranslator;
import com.on36.haetae.common.log.LogLevel;
import com.on36.haetae.http.Container;
import com.on36.haetae.net.udp.Message;
import com.on36.haetae.net.udp.Scheduler;
import com.on36.haetae.server.core.manager.DisruptorManager;
import com.on36.haetae.server.core.manager.event.HttpRequestEvent;
import com.on36.haetae.server.core.manager.event.LogEvent;
import com.on36.haetae.server.core.manager.event.RecievedEvent;
import com.on36.haetae.server.core.manager.event.SendEvent;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

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
	public void revieve(final Message message) {
		disruptorManager.getRecievedEventDisruptor(message)
				.publishEvent(new EventTranslator<RecievedEvent>() {
					@Override
					public void translateTo(RecievedEvent event,
							long sequence) {
						event.setSendMessage(message);
					}
				});
	}

	@Override
	public void send(final Message message) {
		disruptorManager.getSendEventDisruptor()
				.publishEvent(new EventTranslator<SendEvent>() {
					@Override
					public void translateTo(SendEvent event, long sequence) {
						event.setSendMessage(message);
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

	@Override
	public void handleHTTPRequest(ChannelHandlerContext ctx,
			HttpRequest request, Container container) {
		disruptorManager.getHttpRequestEventDisruptor()
				.publishEvent(new EventTranslator<HttpRequestEvent>() {
					@Override
					public void translateTo(HttpRequestEvent event,
							long sequence) {
						event.setContainer(container);
						event.setContext(ctx);
						event.setRequest(request);
					}
				});
	}

}
