package com.on36.haetae.server.core.manager.event.handler;

import com.lmax.disruptor.EventHandler;
import com.on36.haetae.server.core.manager.EndPointManager;
import com.on36.haetae.server.core.manager.event.RecievedEvent;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class RecievedEndpointEventHandler
		implements EventHandler<RecievedEvent> {

	@Override
	public void onEvent(RecievedEvent event, long sequence, boolean endOfBatch)
			throws Exception {
		String endPoint = new String(event.getSendMessage().content(), "UTF-8");
		EndPointManager.update(endPoint);
	}

}
