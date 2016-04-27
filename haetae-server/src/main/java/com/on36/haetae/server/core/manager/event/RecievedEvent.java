package com.on36.haetae.server.core.manager.event;

import com.on36.haetae.net.udp.Message;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class RecievedEvent {

	private Message sendMessage;

	public Message getSendMessage() {
		return sendMessage;
	}

	public void setSendMessage(Message sendMessage) {
		this.sendMessage = sendMessage;
	}

}
