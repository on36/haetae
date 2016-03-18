package com.on36.haetae.server.core.manager.event.handler;

import com.lmax.disruptor.EventHandler;
import com.on36.haetae.server.core.manager.event.RecievedEvent;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class RecievedEventHandler implements EventHandler<RecievedEvent> {

	@Override
	public void onEvent(RecievedEvent event, long sequence, boolean endOfBatch)
			throws Exception {
		// TODO Auto-generated method stub
		System.out.println(this.hashCode() + " " + new String(event.getSendMessage().content()));
	}

}
