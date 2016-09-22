package com.on36.haetae.server.core.manager.event;

import com.lmax.disruptor.EventFactory;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class EndpointEventFactory implements EventFactory<EndpointEvent> {

	public EndpointEvent newInstance() {
		return new EndpointEvent();
	}

}
