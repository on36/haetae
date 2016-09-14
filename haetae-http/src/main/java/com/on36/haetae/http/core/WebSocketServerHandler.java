package com.on36.haetae.http.core;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AttributeKey;

/**
 * @author zhanghr
 * @date 2016年5月8日
 */
public class WebSocketServerHandler {
	private final String websocketPath;

	private static final AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY = AttributeKey
			.valueOf(WebSocketServerHandshaker.class.getName() + ".HANDSHAKER");

	public WebSocketServerHandler(String websocketPath) {
		this.websocketPath = websocketPath;
	}

	public void channelRead(final ChannelHandlerContext ctx,
			FullHttpRequest msg) {
		FullHttpRequest req = (FullHttpRequest) msg;
		try {
			if (req.getMethod() != GET) {
				sendHttpResponse(ctx, req,
						new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
				return;
			}

			final WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
					getWebSocketLocation(ctx.pipeline(), req, websocketPath),
					null, false, 65536);
			final WebSocketServerHandshaker handshaker = wsFactory
					.newHandshaker(req);
			if (handshaker == null) {
				WebSocketServerHandshakerFactory
						.sendUnsupportedVersionResponse(ctx.channel());
			} else {
				final ChannelFuture handshakeFuture = handshaker
						.handshake(ctx.channel(), req);
				handshakeFuture.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future)
							throws Exception {
						if (!future.isSuccess()) {
							ctx.fireExceptionCaught(future.cause());
						} else {
							ctx.fireUserEventTriggered(
									WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
						}
					}
				});
				ctx.channel().attr(HANDSHAKER_ATTR_KEY).set(handshaker);
			}
		} finally {
			req.release();
		}
	}

	private static void sendHttpResponse(ChannelHandlerContext ctx,
			HttpRequest req, HttpResponse res) {
		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if (!isKeepAlive(req) || res.getStatus().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	private static String getWebSocketLocation(ChannelPipeline cp,
			HttpRequest req, String path) {
		String protocol = "ws";
		if (cp.get(SslHandler.class) != null) {
			// SSL in use so use Secure WebSockets
			protocol = "wss";
		}
		return protocol + "://" + req.headers().get(Names.HOST) + path;
	}
}