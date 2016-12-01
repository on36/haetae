package com.on36.haetae.server.core.manager.event.handler;

import com.on36.haetae.hsr.EventListener;
import com.on36.haetae.server.core.manager.EndPointManager;
import com.on36.haetae.server.core.manager.event.EndpointEvent;

/**
 * @author zhanghr
 * @date 2016年1月30日
 */
public class EndpointEventListener implements EventListener<EndpointEvent> {

	@Override
	public void doHandler(EndpointEvent event) {
		String endPoint = event.getEndpoint();
		String channel = event.getChannel();
		if (endPoint != null)
			EndPointManager.getInstance().put(channel, endPoint);
		else
			EndPointManager.getInstance().remove(channel);
	}

}
