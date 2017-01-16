package com.on36.haetae.hsr;

import com.lmax.disruptor.EventHandler;
import com.on36.haetae.rpc.thrift.Message;
import com.on36.haetae.rpc.thrift.Result;

/**
 * @author zhanghr
 * @param <T>
 * @date 2016年11月25日
 */
public class EventListenerHandler<T> implements EventHandler<Event<T>> {

	private EventListener<T> listener;
	private EventListener<Result> resultListener;
	private String conURL;
	private ServiceConsumer consumer;

	public EventListenerHandler(EventListener<T> listener) {
		this.listener = listener;
	}

	public EventListenerHandler(EventListener<Result> listener, String conURL) {
		this.resultListener = listener;
		this.conURL = conURL;
		if (this.conURL != null) {
			consumer = new ServiceConsumer(conURL);
		}
	}

	@Override
	public void onEvent(Event<T> event, long sequence, boolean endOfBatch) throws Exception {
		if (this.listener != null)
			listener.doHandler(event.getValue());
		else {
			Result result = consumer.publish((Message) event.getValue());
			resultListener.doHandler(result);
		}
	}

}
