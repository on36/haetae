package com.on36.haetae.hsr.test;

import com.on36.haetae.hsr.EventBus;
import com.on36.haetae.hsr.EventListener;

/**
 * @author zhanghr
 * @date 2016年11月25日
 */
public class Main {

	public static void main(String[] args) {

		// EventBus.addListener(LogEvent.class, new EventListener<LogEvent>() {
		//
		// @Override
		// public void doHandler(LogEvent event) {
		// System.out.println("log event :" + event.message);
		// }
		// });
		// EventBus.addListener(SessionEvent.class,
		// new EventListener<SessionEvent>() {
		// @Override
		// public void doHandler(SessionEvent event) {
		// System.out.println("session id =" + event.sessionId);
		// }
		//
		// }, 128);
		// EventBus.dispatch(new LogEvent("hello world"));
		// EventBus.dispatch(new SessionEvent(1024));
		// EventBus.dispatch(new LogEvent("nihaoma"));
		// EventBus.dispatch(new SessionEvent(1287));
		// EventBus.dispatch("baobei");
		// EventBus.dispatch("gunyuandian");
		// EventBus.dispatch(new SessionEvent(1786));
		// EventBus.dispatch(new SessionEvent(1287));
		final long start = System.currentTimeMillis();
		System.out.println("start=" + start);
		long i = 1000000;

		EventBus.addListener("test.long.1", new EventListener<Long>() {

			@Override
			public void doHandler(Long event) {
				// TODO Auto-generated method stub
				int count = 0;
				int i = 100000;
				while (i-- > 0)
					count += i;
				if (event.longValue() == 0 || event.longValue() == 1)
					System.out.println(event.longValue() + " : "
							+ (System.currentTimeMillis() - start));
			}
		}, 128, false);
		EventBus.addListener("test.long.2", new EventListener<String>() {

			@Override
			public void doHandler(String event) {
				int count = 0;
				int i = 100000;
				while (i-- > 0)
					count += i;

				int c = Integer.parseInt(event);
				if (c == 0 || c == 1)
					System.out.println(
							c + " : " + (System.currentTimeMillis() - start));
			}
		}, 128, false);

		EventBus.addListener(SessionEvent.class,
				new EventListener<SessionEvent>() {
					@Override
					public void doHandler(SessionEvent event) {
						long c = event.sessionId;
						if (c == 0 || c == 1)
							System.out.println(c + " session : "
									+ (System.currentTimeMillis() - start));
					}

				}, 128);
		long kstart = System.currentTimeMillis();
		while (i-- > 0) {
//			if (i % 2 == 0)
				EventBus.dispatch(new SessionEvent(i));
//			else
//				EventBus.dispatch("test.long.1", i);
		}
		System.out.println(System.currentTimeMillis() - kstart);
		//EventBus.close();
	}

}
