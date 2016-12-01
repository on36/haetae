package com.on36.haetae.server.core.manager.event;

import com.on36.haetae.http.Container;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author zhanghr
 * @date 2016年1月30日
 */
public class HttpRequestEvent {
	private FullHttpRequest request;
	private ChannelHandlerContext context;
	private Container container;

	public HttpRequestEvent(FullHttpRequest request,
			ChannelHandlerContext context, Container container) {
		super();
		this.request = request;
		this.context = context;
		this.container = container;
	}

	public FullHttpRequest getRequest() {
		return request;
	}

	public void setRequest(FullHttpRequest request) {
		this.request = request;
	}

	public ChannelHandlerContext getContext() {
		return context;
	}

	public void setContext(ChannelHandlerContext context) {
		this.context = context;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

}
