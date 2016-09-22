package com.on36.haetae.server.core.manager.event;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class EndpointEvent {

	private String channel;
	private String endpoint;

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

}
