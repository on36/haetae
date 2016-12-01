package com.on36.haetae.hsr.test;

import com.on36.haetae.hsr.EventBus;
import com.on36.haetae.hsr.EventListener;

/**
 * @author zhanghr
 * @date 2016年11月25日
 */
public class Main {

	public static void main(String[] args) {

		EventBus.addListener(LogEvent.class, new EventListener<LogEvent>() {

			@Override
			public void doHandler(LogEvent event) {
				System.out.println("log event :" + event.message);
			}
		});
		EventBus.addListener(String.class, new EventListener<String>() {
			
			@Override
			public void doHandler(String event) {
				System.out.println("String event :" + event);
			}
		});

		EventBus.addListener(SessionEvent.class,
				new EventListener<SessionEvent>() {
					@Override
					public void doHandler(SessionEvent event) {
						System.out.println("session id =" + event.sessionId);
					}

				}, 128);
		EventBus.dispatch(new LogEvent("hello world"));
		EventBus.dispatch(new SessionEvent(1024));
		EventBus.dispatch(new LogEvent("nihaoma"));
		EventBus.dispatch(new SessionEvent(1287));
		EventBus.dispatch("baobei");
		EventBus.dispatch("gunyuandian");
		EventBus.dispatch(new SessionEvent(1786));
		EventBus.dispatch(new SessionEvent(1287));
		final long start = System.currentTimeMillis();
		System.out.println("start=" + start);
		long i = 1000000;
		EventBus.addListener("test.long.1", new EventListener<String>() {

			@Override
			public void doHandler(String event) {
				// TODO Auto-generated method stub
				int count = 0;
				int i = 100000;
				while (i-- > 0)
					count += i;
				//System.out.println(event.longValue() + " : "
				//		+ (System.currentTimeMillis() - start));
			}
		}, 128, false);
		EventBus.addListener("test.long.2", new EventListener<String>() {

			@Override
			public void doHandler(String event) {
				int count = 0;
				int i = 100000;
				while (i-- > 0)
					count += i;
				//System.out.println(event.longValue() + " : "
				//		+ (System.currentTimeMillis() - start));
			}
		}, 128, false);
		while (i-- > 0) {
			if (i % 2 == 0)
				EventBus.dispatch("test.long.2", "hello");
			else
				EventBus.dispatch("test.long.1", "hello");
			if(i == 999999)
				System.out.println(System.currentTimeMillis() - start);
			if(i == 999998)
				System.out.println(System.currentTimeMillis() - start);
		}
		System.out.println(System.currentTimeMillis() - start);
		
		EventBus.close();
	}

}
