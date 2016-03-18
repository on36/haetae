package com.on36.haetae.server.core.manager.event.handler;

import com.lmax.disruptor.EventHandler;
import com.on36.haetae.server.core.manager.event.SendEvent;
import com.on36.haetae.udp.UDPClient;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class SendEventHandler implements EventHandler<SendEvent> {

	private UDPClient client = new UDPClient();

	@Override
	public void onEvent(SendEvent event, long sequence, boolean endOfBatch)
			throws Exception {
		client.send(event.getSendMessage());
	}

}
