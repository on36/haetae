package com.on36.haetae.server.core.manager.event;

import com.lmax.disruptor.EventFactory;

/**
 * @author zhanghr
 * @date 2016年3月26日 
 */
public class HttpRequestEventFactory implements EventFactory<HttpRequestEvent> {

	public HttpRequestEvent newInstance() {
		return new HttpRequestEvent();
	}
}
