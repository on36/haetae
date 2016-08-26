package com.on36.haetae.server.core.manager.event;

import com.on36.haetae.http.Container;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author zhanghr
 * @date 2016年3月26日
 */
public class HttpRequestEvent {

	private ChannelHandlerContext context;
	private HttpRequest request;
	private Container container;
	
	public ChannelHandlerContext getContext() {
		return context;
	}
	public void setContext(ChannelHandlerContext context) {
		this.context = context;
	}
	public HttpRequest getRequest() {
		return request;
	}
	public void setRequest(HttpRequest request) {
		this.request = request;
	}
	
	public Container getContainer() {
		return container;
	}
	
	public void setContainer(Container container) {
		this.container = container;
	}
	
}
