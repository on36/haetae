package com.on36.haetae.server;

import com.lmax.disruptor.EventTranslator;
import com.on36.haetae.net.udp.Message;
import com.on36.haetae.net.udp.Scheduler;
import com.on36.haetae.server.core.manager.DisruptorManager;
import com.on36.haetae.server.core.manager.event.LogEvent;
import com.on36.haetae.server.core.manager.event.RecievedEvent;
import com.on36.haetae.server.core.manager.event.SendEvent;

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
	public void revieve(Message message) {
		disruptorManager.getRecievedEventDisruptor(message).publishEvent(
				new EventTranslator<RecievedEvent>() {
					@Override
					public void translateTo(RecievedEvent event, long sequence) {
						event.setSendMessage(message);
					}
				});
	}

	@Override
	public void send(Message message) {
		disruptorManager.getSendEventDisruptor().publishEvent(
				new EventTranslator<SendEvent>() {
					@Override
					public void translateTo(SendEvent event, long sequence) {
						event.setSendMessage(message);
					}
				});
	}

	@Override
	public void trace(String info) {
		disruptorManager.getLogEventDisruptor().publishEvent(
				new EventTranslator<LogEvent>() {
					@Override
					public void translateTo(LogEvent event, long sequence) {
						event.setInfo(info);
					}
				});
	}

}
