package com.on36.haetae.server.core.manager.event.handler;

import com.lmax.disruptor.EventHandler;
import com.on36.haetae.server.core.manager.EndPointManager;
import com.on36.haetae.server.core.manager.event.EndpointEvent;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class EndpointEventHandler implements EventHandler<EndpointEvent> {

	@Override
	public void onEvent(EndpointEvent event, long sequence, boolean endOfBatch)
			throws Exception {
		String endPoint = event.getEndpoint();
		String channel = event.getChannel();
		if (endPoint != null)
			EndPointManager.getInstance().put(channel, endPoint);
		else
			EndPointManager.getInstance().remove(channel);
	}

}
