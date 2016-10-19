package com.on36.haetae.http.core;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.on36.haetae.common.conf.Constant;
import com.on36.haetae.config.client.ConfigClient;
import com.on36.haetae.http.Banner;
import com.on36.haetae.http.Container;
import com.on36.haetae.http.Environment;
import com.on36.haetae.http.Server;
import com.on36.haetae.http.route.RouteHelper;

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

public class HTTPServer implements Server {

	private final InetSocketAddress socketAddress;
	private final int threadPoolSize;

	private final Container container;

	private Channel channel;

	public static boolean RUNNING = false;

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
			SslContext sslCtx = null;
			boolean ssl = ConfigClient.getBoolean(Constant.K_SERVER_SSL_ENABLED,
					Constant.V_SERVER_SSL_ENABLED);
			boolean ws = ConfigClient.getBoolean(Constant.K_SERVER_WS_ENABLED,
					Constant.V_SERVER_WS_ENABLED);
			if (ssl) {
				SelfSignedCertificate ssc = new SelfSignedCertificate(
						"on36.com");
				sslCtx = SslContextBuilder
						.forServer(ssc.certificate(), ssc.privateKey()).build();
			}
			// Configure the server.
			EventLoopGroup bossGroup = new NioEventLoopGroup(
					threadPoolSize > 0 ? threadPoolSize
							: ConfigClient.getInt(
									Constant.K_SERVER_THREADPOOL_SIZE,
									Constant.V_SERVER_THREADPOOL_SIZE));
			EventLoopGroup workerGroup = new NioEventLoopGroup(
					threadPoolSize > 0 ? threadPoolSize
							: ConfigClient.getInt(
									Constant.K_SERVER_THREADPOOL_SIZE,
									Constant.V_SERVER_THREADPOOL_SIZE));
			try {
				ServerBootstrap b = new ServerBootstrap();
				b.option(ChannelOption.SO_BACKLOG,
						ConfigClient.getInt(Constant.K_SERVER_SOBACKLOG,
								Constant.V_SERVER_SOBACKLOG));
				b.option(ChannelOption.SO_REUSEADDR, true);
				b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
						Constant.V_SERVER_CONNECTTIMEOUT_MILLIS);
				b.option(ChannelOption.ALLOCATOR,
						PooledByteBufAllocator.DEFAULT);
				b.childOption(ChannelOption.ALLOCATOR,
						PooledByteBufAllocator.DEFAULT);
				// b.childOption(ChannelOption.AUTO_READ, false);
				b.group(bossGroup, workerGroup)
						.channel(NioServerSocketChannel.class)
						.handler(new LoggingHandler(LogLevel.DEBUG))
						.childHandler(new HttpServerInitializer(sslCtx,
								container, ws));

				channel = b.bind(socketAddress).sync().channel();
				RUNNING = true;

				Thread.sleep(50);
				System.out.println("Starting server...");
				Banner.print(socketAddress.getPort());
				Environment.logEnv();

				System.out
						.println("Server is now ready to accept connection on ["
								+ socketAddress + RouteHelper.PATH_ELEMENT_ROOT
								+ "]");
				container.getScheduler().trace(this.getClass(),
						com.on36.haetae.common.log.LogLevel.INFO,
						"Server is now ready to accept connection on ["
								+ socketAddress + RouteHelper.PATH_ELEMENT_ROOT
								+ "]");
				System.out.close();
				System.err.close();
				channel.closeFuture().sync();
			} catch (Exception e) {
				throw new Exception(
						"Address[" + socketAddress + "] already in use: bind");
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
