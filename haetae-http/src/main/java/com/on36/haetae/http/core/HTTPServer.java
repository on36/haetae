package com.on36.haetae.http.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.net.InetSocketAddress;

import com.on36.haetae.http.Container;
import com.on36.haetae.http.Server;
import com.on36.haetae.http.Version;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class HTTPServer implements Server {

	public static InternalLogger LOG = InternalLoggerFactory
			.getInstance(HTTPServer.class);
	private static Config config = ConfigFactory.load();

	private final InetSocketAddress socketAddress;
	private final int threadPoolSize;

	private final Container container;

	private Channel channel;

	public static Config getConfig() {
		return config;
	}

	public HTTPServer(int port, Container container) {
		this(port, 0, container);
	}

	public HTTPServer(int port, int threadPoolSize, Container container) {
		this.socketAddress = new InetSocketAddress(port);
		this.threadPoolSize = threadPoolSize;
		this.container = container;
	}

	public Container getContainer() {
		return container;
	}

	public InetSocketAddress getSocketAddress() {
		return socketAddress;
	}

	public void start() throws Exception {

		if (channel == null || !channel.isActive()) {
			final SslContext sslCtx;
			boolean ssl = getConfig().getBoolean("httpServer.ssl");
			if (ssl) {
				SelfSignedCertificate ssc = new SelfSignedCertificate(
						"on36.com");
				sslCtx = SslContextBuilder.forServer(ssc.certificate(),
						ssc.privateKey()).build();
			} else {
				sslCtx = null;
			}

			// Configure the server.
			EventLoopGroup bossGroup = new NioEventLoopGroup(threadPoolSize);
			EventLoopGroup workerGroup = new NioEventLoopGroup(threadPoolSize);
			try {
				ServerBootstrap b = new ServerBootstrap();
				b.option(ChannelOption.SO_BACKLOG,
						getConfig().getInt("httpServer.soBacklog"));
				b.option(ChannelOption.SO_REUSEADDR, true);
				b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
				b.option(ChannelOption.ALLOCATOR,
						PooledByteBufAllocator.DEFAULT);
				// b.childOption(ChannelOption.AUTO_READ, false);
				b.group(bossGroup, workerGroup)
						.channel(NioServerSocketChannel.class)
						.handler(new LoggingHandler(LogLevel.DEBUG))
						.childHandler(
								new HttpServerInitializer(sslCtx, container));

				channel = b.bind(socketAddress).sync().channel();

				print(socketAddress.getPort());

				System.out
						.println("Server is now ready to accept connection on ["
								+ socketAddress + "]");
				channel.closeFuture().sync();
			} finally {
				bossGroup.shutdownGracefully();
				workerGroup.shutdownGracefully();
			}
		} else {
			System.out.println("Server is already running on [" + socketAddress
					+ "], startup abort!");
		}
	}

	public void stop() {
		if (channel != null && channel.isActive()) {
			channel.close();
		}
	}

	private void print(int port) {
		System.out.println("  HH        HH");
		System.out
				.println("  HH        HH     HHHHHH        HHHHHH           HHH         HHHHHH       HHHHHHH       ");
		System.out
				.println("  HH        HH    HH    HH     HH      HH         HHH        HH    HH     HH      HH     ");
		System.out
				.println("  HH        HH           HH   HH        HH   HHHHHHHHHHHHH          HH   HH        HH    ");
		System.out
				.println("  HHHHHHHHHHHH     HHHHHHH    HHHHHHHHHHH         HHH         HHHHHHH    HHHHHHHHHHH     ");
		System.out
				.println("  HH        HH    HH    HH    HH                  HHH        HH    HH    HH              ");
		System.out
				.println("  HH        HH   HH      HH    HH       HH        HHH       HH      HH    HH       HH    Version: "
						+ Version.CURRENT_VERSION);
		System.out
				.println("  HH        HH    HH    HH      HHH   HHH         HHH  HH    HH    HH      HHH   HHH     Port: "
						+ port);
		System.out
				.println("  HH        HH     HHHHHH HH      HHHHH            HHHH       HHHHHH HH      HHHHH       Author: zhanghr");
	}
}
