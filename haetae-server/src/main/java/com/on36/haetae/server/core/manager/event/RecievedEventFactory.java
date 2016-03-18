package com.on36.haetae.server.core.manager.event;

import com.lmax.disruptor.EventFactory;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class RecievedEventFactory implements EventFactory<RecievedEvent> {

	public RecievedEvent newInstance() {
		return new RecievedEvent();
	}

}
