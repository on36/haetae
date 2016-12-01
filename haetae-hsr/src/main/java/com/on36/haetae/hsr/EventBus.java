package com.on36.haetae.hsr;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * @author zhanghr
 * @date 2016年11月25日
 */
public class EventBus {

	private static final ExecutorService disruptorExecutors = Executors
			.newCachedThreadPool();

	private static final int DEFAULT_RINGBUFFER_SIZE = 128;
	private static final boolean DEFAULT_ISBLOCKING = true;

	private static Map<String, Disruptor<?>> mapDisruptor = new HashMap<String, Disruptor<?>>();

	/**
	 * 注册一个类事件监听器，默认加锁、队列大小为128，适用于对延时和吞吐量要求不高的场景.
	 * 
	 * @param clazz
	 *            类名
	 * @param listener
	 *            事件监听器执行类
	 */
	public static <T> void addListener(Class<T> clazz, EventListener<T> listener) {
		addListener(clazz, listener, DEFAULT_RINGBUFFER_SIZE, DEFAULT_ISBLOCKING);
	}

	/**
	 * 注册事件监听器，默认加锁、队列大小为128，适用于对延时和吞吐量要求不高的场景.
	 * 
	 * @param eventName
	 *            事件名
	 * @param listener
	 *            事件监听器执行类
	 */
	public static <T> void addListener(String eventName, EventListener<T> listener) {
		addListener(eventName, listener, DEFAULT_RINGBUFFER_SIZE, DEFAULT_ISBLOCKING);
	}

	/**
	 * 注册一个类的事件监听器,默认加锁，适用于对延时和吞吐量要求不高的场景.
	 * 
	 * @param clazz
	 *            类名
	 * @param listener
	 *            事件监听器执行类
	 * @param ringSize
	 *            队列大小
	 */
	public static <T> void addListener(Class<T> clazz, EventListener<T> listener,
			int ringSize) {
		addListener(clazz, listener, ringSize, DEFAULT_ISBLOCKING);
	}

	/**
	 * 注册事件监听器,默认加锁，适用于对延时和吞吐量要求不高的场景.
	 * 
	 * @param eventName
	 *            事件名
	 * @param listener
	 *            事件监听器执行类
	 * @param ringSize
	 *            队列大小
	 */
	public static <T> void addListener(String eventName, EventListener<T> listener,
			int ringSize) {
		addListener(eventName, listener, ringSize, DEFAULT_ISBLOCKING);
	}

	/**
	 * 注册一个类的事件监听器,队列大小默认为128.
	 * 
	 * @param clazz
	 *            类名
	 * @param listener
	 *            事件监听器执行类
	 * @param isBlocking
	 *            是否加锁，影响延时和吞吐量
	 */
	public static <T> void addListener(Class<T> clazz, EventListener<T> listener,
			boolean isBlocking) {
		addListener(clazz, listener, DEFAULT_RINGBUFFER_SIZE, isBlocking);
	}

	/**
	 * 注册事件监听器,队列大小默认为128.
	 * 
	 * @param eventName
	 *            事件名
	 * @param listener
	 *            事件监听器执行类
	 * @param isBlocking
	 *            是否加锁，影响延时和吞吐量
	 */
	public static <T> void addListener(String eventName, EventListener<T> listener,
			boolean isBlocking) {
		addListener(eventName, listener, DEFAULT_RINGBUFFER_SIZE, isBlocking);
	}

	/**
	 * 注册一个类的事件监听器.
	 * 
	 * @param clazz
	 *            类名
	 * @param listener
	 *            事件监听器执行类
	 * @param ringSize
	 *            队列大小
	 * @param isBlocking
	 *            是否加锁，影响延时和吞吐量
	 */
	public static <T> void addListener(Class<T> clazz, EventListener<T> listener,
			int ringSize, boolean isBlocking) {
		addListener(clazz.getName(), listener, ringSize, isBlocking);
	}

	/**
	 * 注册事件监听器.
	 * 
	 * @param eventName
	 *            事件名
	 * @param listener
	 *            事件监听器执行类
	 * @param ringSize
	 *            队列大小
	 * @param isBlocking
	 *            是否加锁，影响延时和吞吐量
	 */
	@SuppressWarnings("unchecked")
	public static <T> void addListener(String eventName, EventListener<T> listener,
			int ringSize, boolean isBlocking) {
		Disruptor<Event<T>> eventDisruptor = (Disruptor<Event<T>>) mapDisruptor
				.get(eventName.toLowerCase());
		if (eventDisruptor == null) {
			if (isBlocking)
				eventDisruptor = new Disruptor<Event<T>>(new EventFactory<T>(),
						ringSize, disruptorExecutors, ProducerType.SINGLE,
						new BlockingWaitStrategy());
			else
				eventDisruptor = new Disruptor<Event<T>>(new EventFactory<T>(),
						ringSize, disruptorExecutors, ProducerType.SINGLE,
						new YieldingWaitStrategy());

			eventDisruptor
					.handleEventsWith(new EventListenerHandler<T>(listener));
			eventDisruptor.start();

			mapDisruptor.put(eventName.toLowerCase(), eventDisruptor);
		} else {
			throw new IllegalArgumentException(String.format(
					"These is already a listener of event name [%s]",
					eventName));
		}
	}

	/**
	 * 发出指定类的事件操作
	 * 
	 * @param value
	 *            发送值对象
	 */
	public static <T> void dispatch(final T value) {
		dispatch(value.getClass().getName(), value);
	}

	/**
	 * 发出指定事件的操作
	 * 
	 * @param eventName
	 *            事件名
	 * @param value
	 *            发送值对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> void dispatch(final String eventName, final T value) {
		Disruptor<Event<T>> disruptor = (Disruptor<Event<T>>) mapDisruptor
				.get(eventName.toLowerCase());
		if (disruptor != null)
			disruptor.publishEvent(new EventTranslator<Event<T>>() {
				@Override
				public void translateTo(Event<T> event, long sequence) {
					event.setValue(value);
				}
			});
		else
			throw new IllegalArgumentException(String.format(
					"Not found any listener of event name [%s]", eventName));
	}

	/**
	 * 注销一个事务监听
	 * 
	 * @param eventName
	 *            事件名
	 */
	public static void stop(String eventName) {
		Disruptor<?> value = mapDisruptor.get(eventName);
		if (value != null) {
			value.shutdown();
			mapDisruptor.remove(eventName);
		}
	}

	/**
	 * 注销一个类的事务监听
	 * 
	 * @param clazz
	 *            类名
	 */
	public static void stop(Class<?> clazz) {
		stop(clazz.getName());
	}

	/**
	 * 关闭事件引擎
	 */
	public static void close() {
		for (Disruptor<?> value : mapDisruptor.values()) {
			value.shutdown();
		}

		disruptorExecutors.shutdown();
	}
}