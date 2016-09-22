package com.on36.haetae.http.core;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.DATE;
import static io.netty.handler.codec.http.HttpHeaders.Names.LAST_MODIFIED;
import static io.netty.handler.codec.http.HttpMethod.GET;
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
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.ssl.SslHandler;

public class HttpServerHandler extends SimpleChannelInboundHandler<Object> {
	private final Container container;
	private static final String WEBSOCKET_PATH = "/ws";
	private final boolean wsAliveabel;

	protected WebSocketServerHandshaker handshaker;
	private String frameBuffer = null;

	public HttpServerHandler(Container container, boolean wsAliveable) {
		this.wsAliveabel = wsAliveable;
		this.container = container;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if (msg instanceof FullHttpRequest) {
			this.handleHttpRequest(ctx, (FullHttpRequest) msg);
		} else if (msg instanceof WebSocketFrame) {
			this.handleWebSocketFrame(ctx, (WebSocketFrame) msg);
		}
	}

	private void handleWebSocketFrame(ChannelHandlerContext ctx,
			WebSocketFrame frame) {
		// Check for closing frame
		if (frame instanceof CloseWebSocketFrame) {
			handleMessageCompleted(ctx, null);
			handshaker.close(ctx.channel(),
					(CloseWebSocketFrame) frame.retain());
			return;
		} else if (frame instanceof PingWebSocketFrame) {
			ctx.channel().writeAndFlush(
					new PongWebSocketFrame(frame.content().retain()));
			return;
		} else if (frame instanceof PongWebSocketFrame) {
			return;
		} else if (frame instanceof TextWebSocketFrame) {
			frameBuffer = ((TextWebSocketFrame) frame).text();
		} else if (frame instanceof ContinuationWebSocketFrame) {
			if (frameBuffer != null) {
				frameBuffer = ((TextWebSocketFrame) frame).text();
			}
		} else {
			throw new UnsupportedOperationException(
					String.format("%s frame types not supported",
							frame.getClass().getName()));
		}

		// Check if Text or Continuation Frame is final fragment and handle if
		// needed.
		if (frame.isFinalFragment()) {
			handleMessageCompleted(ctx, frameBuffer);
			frameBuffer = null;
		}

	}

	private void handleMessageCompleted(ChannelHandlerContext ctx,
			String frameText) {
		// ctx.channel().writeAndFlush(new TextWebSocketFrame("hi hao client"));
		container.getScheduler()
				.endpoint(ctx.channel().remoteAddress().toString(), frameText);
	}

	private void handleHttpRequest(ChannelHandlerContext ctx,
			FullHttpRequest request) {

		// container.getScheduler().handleHTTPRequest(ctx, request, container);

		long start = System.currentTimeMillis();
		if (wsAliveabel && request.getMethod() == GET
				&& WEBSOCKET_PATH.equalsIgnoreCase(request.getUri())) {
			// Websocket Handshake
			WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
					getWebSocketLocation(ctx.pipeline(), request), null, true);
			handshaker = wsFactory.newHandshaker(request);
			if (handshaker == null) {
				WebSocketServerHandshakerFactory
						.sendUnsupportedVersionResponse(ctx.channel());
			} else {
				handshaker.handshake(ctx.channel(), request);
			}
		} else if (HttpHeaders.is100ContinueExpected(request)) {
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

	private static String getWebSocketLocation(ChannelPipeline cp,
			FullHttpRequest req) {
		String protocol = "ws";
		if (cp.get(SslHandler.class) != null) {
			// SSL in use so use Secure WebSockets
			protocol = "wss";
		}
		return protocol + "://" + req.headers().get(Names.HOST)
				+ WEBSOCKET_PATH;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		container.getScheduler()
				.endpoint(ctx.channel().remoteAddress().toString(), null);
		ctx.close();
	}
}