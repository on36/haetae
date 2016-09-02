package com.on36.haetae.http.core;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.DATE;
import static io.netty.handler.codec.http.HttpHeaders.Names.LAST_MODIFIED;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.net.InetSocketAddress;

import com.on36.haetae.common.utils.DateUtils;
import com.on36.haetae.http.Container;
import com.on36.haetae.http.request.HttpRequestExt;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public class HttpServerHandler extends SimpleChannelInboundHandler<Object> {
	private final Container container;

	protected WebSocketServerHandshaker handshaker;
	private StringBuilder frameBuffer = null;

	public HttpServerHandler(Container container) {
		this.container = container;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if (msg instanceof HttpRequest) {
			this.handleHttpRequest(ctx, (HttpRequest) msg);
		} else if (msg instanceof WebSocketFrame) {
			this.handleWebSocketFrame(ctx, (WebSocketFrame) msg);
		}
	}

	private void handleWebSocketFrame(ChannelHandlerContext ctx,
			WebSocketFrame frame) {
		// Check for closing frame
		if (frame instanceof CloseWebSocketFrame) {
			if (frameBuffer != null) {
				handleMessageCompleted(ctx, frameBuffer.toString());
			}
			handshaker.close(ctx.channel(),
					(CloseWebSocketFrame) frame.retain());
			return;
		}

		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().writeAndFlush(
					new PongWebSocketFrame(frame.content().retain()));
			return;
		}

		if (frame instanceof PongWebSocketFrame) {
			return;
		}

		if (frame instanceof TextWebSocketFrame) {
			frameBuffer = new StringBuilder();
			frameBuffer.append(((TextWebSocketFrame) frame).text());
		} else if (frame instanceof ContinuationWebSocketFrame) {
			if (frameBuffer != null) {
				frameBuffer.append(((ContinuationWebSocketFrame) frame).text());
			}
		} else {
			throw new UnsupportedOperationException(
					String.format("%s frame types not supported",
							frame.getClass().getName()));
		}

		// Check if Text or Continuation Frame is final fragment and handle if
		// needed.
		if (frame.isFinalFragment()) {
			handleMessageCompleted(ctx, frameBuffer.toString());
			frameBuffer = null;
		}
	}

	private void handleMessageCompleted(ChannelHandlerContext ctx,
			String frameText) {
		// String response = wsMessageHandler.handleMessage(ctx, frameText);
		// if (response != null) {
		// ctx.channel().writeAndFlush(new TextWebSocketFrame(response));
		// }
	}

	private void handleHttpRequest(ChannelHandlerContext ctx,
			HttpRequest request) {

		// container.getScheduler().handleHTTPRequest(ctx, request, container);

		long start = System.currentTimeMillis();
		if (HttpHeaders.is100ContinueExpected(request)) {
			ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
		} else {

			String remoteAddress = request.headers().get("X-Forwarded-For");

			InetSocketAddress is = (InetSocketAddress) ctx.channel()
					.remoteAddress();
			int remotePort = is.getPort();
			if (remoteAddress == null) {
				remoteAddress = is.getAddress().getHostAddress();
			} else {
				remoteAddress = remoteAddress.split(",")[0].trim();
			}

			boolean keepAlive = HttpHeaders.isKeepAlive(request);
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
					NOT_FOUND, Unpooled.directBuffer());
			response.headers().set(DATE, DateUtils.getTimeZoneTime());
			HttpRequestExt httpRequestExt = new HttpRequestExt(request,
					remoteAddress, remotePort, start);
			if (container != null)
				container.handle(httpRequestExt, response);

			response.headers().set(CONTENT_LENGTH,
					response.content().readableBytes());

			response.headers().set(LAST_MODIFIED, DateUtils.getTimeZoneTime());
			if (!keepAlive) {
				ctx.write(response).addListener(ChannelFutureListener.CLOSE);
			} else {
				response.headers().set(CONNECTION, Values.KEEP_ALIVE);
				ctx.write(response);
			}
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