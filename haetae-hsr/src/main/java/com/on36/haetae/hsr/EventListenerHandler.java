package com.on36.haetae.hsr;

import com.lmax.disruptor.EventHandler;

/**
 * @author zhanghr
 * @param <T>
 * @date 2016年11月25日
 */
public class EventListenerHandler<T> implements EventHandler<Event<T>> {

	private EventListener<T> listener;

	public EventListenerHandler(EventListener<T> listener) {
		this.listener = listener;
	}

	@Override
	public void onEvent(Event<T> event, long sequence, boolean endOfBatch)
			throws Exception {
		listener.doHandler(event.getValue());
	}

}
