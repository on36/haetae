package com.on36.haetae.http.core;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

import com.on36.haetae.http.Container;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

	private final SslContext sslCtx;
	private final Container container;
	private boolean compressior = true;

	public HttpServerInitializer(SslContext sslCtx, Container container) {
		this.sslCtx = sslCtx;
		this.container = container;
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline p = ch.pipeline();
		if (sslCtx != null) {
			p.addLast(sslCtx.newHandler(ch.alloc()));
		}
		p.addLast(new HttpServerCodec());
		p.addLast(new HttpObjectAggregator(10 * 1024));// max content length
		if (compressior)
			p.addLast(new HttpContentCompressor(1));
		p.addLast(new HttpServerHandler(container));
	}
}
