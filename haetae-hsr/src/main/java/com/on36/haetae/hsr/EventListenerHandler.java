package com.on36.haetae.hsr;

import com.lmax.disruptor.EventHandler;

/**
 * @author zhanghr
 * @param <T>
 * @date 2016年11月25日
 */
public class EventListenerHandler<T> implements EventHandler<Event<T>> {

	private EventListener<T> listener;
	private boolean isLocal;

	public EventListenerHandler(EventListener<T> listener) {
		this(listener, false);
	}

	public EventListenerHandler(EventListener<T> listener, boolean isLocal) {
		this.listener = listener;
		this.isLocal = isLocal;
	}

	@Override
	public void onEvent(Event<T> event, long sequence, boolean endOfBatch)
			throws Exception {
		if (isLocal)
			listener.doHandler(event.getValue());
		else {
			//TODO 调用远端接口
			System.out.println("hello remote");
		}
	}

}
