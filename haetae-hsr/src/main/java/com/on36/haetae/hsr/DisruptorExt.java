package com.on36.haetae.hsr;

import java.util.concurrent.ThreadFactory;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.EventProcessorFactory;
import com.lmax.disruptor.dsl.ProducerType;
import com.on36.haetae.rpc.thrift.Result;

/**
 * @author zhanghr
 * @date 2016年12月12日
 */
public class DisruptorExt<T> extends Disruptor<Event<T>> {

	private String connUrl = null;
	private ServiceProvider<T> sp = new ServiceProvider<T>(this);
	private boolean hasRemote = false;

	public DisruptorExt(EventFactory<T> eventFactory, int ringBufferSize,
			ThreadFactory threadFactory, ProducerType producerType,
			WaitStrategy waitStrategy) {
		this(null, eventFactory, ringBufferSize, threadFactory, producerType,
				waitStrategy);
	}

	public DisruptorExt(String connUrl, EventFactory<T> eventFactory,
			int ringBufferSize, ThreadFactory threadFactory,
			ProducerType producerType, WaitStrategy waitStrategy) {
		super(eventFactory, ringBufferSize, threadFactory, producerType,
				waitStrategy);
		this.connUrl = connUrl;
	}

	public DisruptorExt(int port, EventFactory<T> eventFactory,
			int ringBufferSize, ThreadFactory threadFactory,
			ProducerType producerType, WaitStrategy waitStrategy) {
		super(eventFactory, ringBufferSize, threadFactory, producerType,
				waitStrategy);
		sp.publishService(port);
		this.hasRemote = true;
	}

	public void publishEvent(final T value) {
		super.publishEvent(new EventTranslator<Event<T>>() {
			@Override
			public void translateTo(Event<T> event, long sequence) {
				event.setValue(value);
			}
		});
	}

	public void publishEvents(final T[] values) {
		super.publishEvents(new EventTranslatorOneArg<Event<T>, T>() {
			@Override
			public void translateTo(Event<T> event, long sequence, T value) {
				event.setValue(value);
			}
		}, values);
	}

	@SuppressWarnings("unchecked")
	public void handleEventsWith(final EventListener<?> listener) {
		EventListenerHandler<T> handlers = null;
		if (hasRemote || connUrl==null)
			handlers = new EventListenerHandler<T>((EventListener<T>) listener);
		else
			handlers = new EventListenerHandler<T>(
					(EventListener<Result>) listener, connUrl);
		super.handleEventsWith(handlers);
	}

	@Deprecated
	public void publishEvent(final EventTranslator<Event<T>> eventTranslator) {
		throw new UnsupportedOperationException("no support method!");
	}

	@Deprecated
	public <A> void publishEvent(
			final EventTranslatorOneArg<Event<T>, A> eventTranslator,
			final A arg) {
		throw new UnsupportedOperationException("no support method!");
	}

	@Deprecated
	public <A> void publishEvents(
			final EventTranslatorOneArg<Event<T>, A> eventTranslator,
			final A[] arg) {
		throw new UnsupportedOperationException("no support method!");
	}

	@SuppressWarnings("unchecked")
	@Deprecated
	public EventHandlerGroup<Event<T>> handleEventsWith(
			final EventHandler<? super Event<T>>... handlers) {
		throw new UnsupportedOperationException("no support method!");
	}

	@SuppressWarnings("unchecked")
	@Deprecated
	public EventHandlerGroup<Event<T>> handleEventsWith(
			final EventProcessorFactory<Event<T>>... eventProcessorFactories) {
		throw new UnsupportedOperationException("no support method!");
	}

	@Deprecated
	public EventHandlerGroup<Event<T>> handleEventsWith(
			final EventProcessor... processors) {
		throw new UnsupportedOperationException("no support method!");
	}

	@SuppressWarnings("unchecked")
	@Deprecated
	public EventHandlerGroup<Event<T>> handleEventsWithWorkerPool(
			final WorkHandler<Event<T>>... workHandlers) {
		throw new UnsupportedOperationException("no support method!");
	}

	@Deprecated
	public EventHandlerGroup<Event<T>> after(
			final EventProcessor... processors) {
		throw new UnsupportedOperationException("no support method!");
	}

	@SuppressWarnings("unchecked")
	@Deprecated
	public EventHandlerGroup<Event<T>> after(
			final EventHandler<Event<T>>... handlers) {
		throw new UnsupportedOperationException("no support method!");
	}

	@Override
	public RingBuffer<Event<T>> start() {
		if (this.hasRemote) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					sp.start();
				}
			}).start();
			;
		}
		return super.start();
	}

	@Override
	public void shutdown() {
		super.shutdown();
		if (hasRemote)
			sp.shutdown();
	}

}
