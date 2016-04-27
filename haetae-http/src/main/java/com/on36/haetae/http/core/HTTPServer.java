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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.on36.haetae.http.Banner;
import com.on36.haetae.http.Configuration;
import com.on36.haetae.http.Container;
import com.on36.haetae.http.Environment;
import com.on36.haetae.http.Server;

public class HTTPServer implements Server {

	public static InternalLogger LOG = InternalLoggerFactory
			.getInstance(HTTPServer.class);
	private static Configuration config = Configuration.create();

	private final InetSocketAddress socketAddress;
	private final int threadPoolSize;

	private final Container container;

	private Channel channel;

	public static Configuration getConfig() {
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

	public InetSocketAddress getInetSocketAddress() {
		try {
			return new InetSocketAddress(InetAddress.getLocalHost(),
					socketAddress.getPort());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void start() throws Exception {

		if (channel == null || !channel.isActive()) {
			final SslContext sslCtx;
			boolean ssl = getConfig().getBoolean("httpServer.ssl", false);
			if (ssl) {
				SelfSignedCertificate ssc = new SelfSignedCertificate(
						"on36.com");
				sslCtx = SslContextBuilder.forServer(ssc.certificate(),
						ssc.privateKey()).build();
			} else {
				sslCtx = null;
			}
			System.out.println("Starting server...");
			// Configure the server.
			EventLoopGroup bossGroup = new NioEventLoopGroup(threadPoolSize);
			EventLoopGroup workerGroup = new NioEventLoopGroup(threadPoolSize);
			try {
				ServerBootstrap b = new ServerBootstrap();
				b.option(ChannelOption.SO_BACKLOG,
						getConfig().getInt("httpServer.soBacklog", 1024));
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

				Banner.print(socketAddress.getPort());
				Environment.logEnv();

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
}
