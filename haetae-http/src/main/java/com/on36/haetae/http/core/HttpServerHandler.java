package com.on36.haetae.http.core;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.DATE;
import static io.netty.handler.codec.http.HttpHeaders.Names.LAST_MODIFIED;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpRequest;

import java.net.InetSocketAddress;

import com.on36.haetae.http.Container;
import com.on36.haetae.http.request.HttpRequestExt;

public class HttpServerHandler extends SimpleChannelInboundHandler<HttpRequest> {
	private final Container container;

	public HttpServerHandler(Container container) {
		this.container = container;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpRequest request) {

		long start = System.currentTimeMillis();
		if (HttpHeaders.is100ContinueExpected(request)) {
			ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
		}

		String remoteAddress = request.headers().get("X-Forwarded-For");
		if (remoteAddress == null) {
			InetSocketAddress is = (InetSocketAddress) ctx.channel()
					.remoteAddress();
			remoteAddress = is.getAddress().getHostAddress();
		} else {
			remoteAddress = remoteAddress.split(",")[0].trim();
		}
		boolean keepAlive = HttpHeaders.isKeepAlive(request);
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
				NOT_FOUND, Unpooled.directBuffer());
		response.headers().set(DATE, start);
		
		HttpRequestExt httpRequestExt = new HttpRequestExt(request,
				remoteAddress, start);
		if (container != null)
			container.handle(httpRequestExt, response);

		response.headers().set(CONTENT_LENGTH,
				response.content().readableBytes());
		response.headers().set(LAST_MODIFIED, System.currentTimeMillis());
		
		if (!keepAlive) {
			ctx.write(response).addListener(ChannelFutureListener.CLOSE);
		} else {
			response.headers().set(CONNECTION, Values.KEEP_ALIVE);
			ctx.write(response);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
	}
}