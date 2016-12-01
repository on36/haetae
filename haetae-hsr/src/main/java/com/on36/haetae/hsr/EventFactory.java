package com.on36.haetae.hsr;

/**
 * @author zhanghr
 * @param <T>
 * @date 2016年11月25日
 */
public class EventFactory<T>
		implements com.lmax.disruptor.EventFactory<Event<T>> {

	public Event<T> newInstance() {
		return new Event<T>();
	}
}
