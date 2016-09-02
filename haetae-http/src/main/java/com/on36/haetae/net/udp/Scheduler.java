package com.on36.haetae.net.udp;

import com.on36.haetae.common.log.LogLevel;
import com.on36.haetae.http.Container;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public interface Scheduler {

	void revieve(Message message);

	void send(Message message);

	void trace(Object clazz, LogLevel level, String message);

	void trace(Object clazz, LogLevel level, String message, Throwable e);

	void handleHTTPRequest(ChannelHandlerContext ctx, HttpRequest request,
			Container container);
}
