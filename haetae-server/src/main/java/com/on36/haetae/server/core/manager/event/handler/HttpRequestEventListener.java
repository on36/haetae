package com.on36.haetae.server.core.manager.event.handler;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.DATE;
import static io.netty.handler.codec.http.HttpHeaderNames.LAST_MODIFIED;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.net.InetSocketAddress;

import com.on36.haetae.common.utils.DateUtils;
import com.on36.haetae.hsr.EventListener;
import com.on36.haetae.http.request.HttpRequestExt;
import com.on36.haetae.server.core.manager.event.HttpRequestEvent;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpUtil;

/**
 * @author zhanghr
 * @date 2016年1月30日
 */
public class HttpRequestEventListener
		implements EventListener<HttpRequestEvent> {

	@Override
	public void doHandler(HttpRequestEvent event) {

		FullHttpRequest request = event.getRequest();
		ChannelHandlerContext ctx = event.getContext();
		long start = System.currentTimeMillis();
		String remoteAddress = request.headers().get("X-Forwarded-For");

		InetSocketAddress is = (InetSocketAddress) ctx.channel()
				.remoteAddress();
		int remotePort = is.getPort();
		if (remoteAddress == null) {
			remoteAddress = is.getAddress().getHostAddress();
		} else {
			remoteAddress = remoteAddress.split(",")[0].trim();
		}

		boolean keepAlive = HttpUtil.isKeepAlive(request);
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
				NOT_FOUND, Unpooled.directBuffer());
		response.headers().set(DATE, DateUtils.getTimeZoneTime());
		HttpRequestExt httpRequestExt = new HttpRequestExt(request,
				remoteAddress, remotePort, start);
		event.getContainer().handle(httpRequestExt, response);

		response.headers().set(CONTENT_LENGTH,
				response.content().readableBytes());

		response.headers().set(LAST_MODIFIED, DateUtils.getTimeZoneTime());
		if (!keepAlive) {
			ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		} else {
			response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			ctx.writeAndFlush(response);
		}
	}

}
