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

import java.net.InetSocketAddress;

import com.on36.haetae.http.Container;
import com.on36.haetae.http.Server;
import com.on36.haetae.http.Version;

public class HTTPServer implements Server {

	private InetSocketAddress socketAddress;
	private int threadPoolSize;
	private boolean ssl;
	
	private Container container;

	private Channel channel;

	public HTTPServer(int port) {
		this(port, 0, false);
	}

	public HTTPServer(int port, boolean ssl) {
		this(port, 0, ssl);
	}

	public HTTPServer(int port, int threadPoolSize, boolean ssl) {
		this.socketAddress = new InetSocketAddress(port);
		this.threadPoolSize = threadPoolSize;
		this.ssl = ssl;
	}
    
	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	public void start() throws Exception {
		
		if (channel == null || !channel.isActive()) {
			final SslContext sslCtx;
			if (ssl) {
				SelfSignedCertificate ssc = new SelfSignedCertificate("on36.com");
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
				b.option(ChannelOption.SO_BACKLOG, 1024);
				b.option(ChannelOption.SO_REUSEADDR, true);
	            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000);
	            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
//	            b.childOption(ChannelOption.AUTO_READ, false);
				b.group(bossGroup, workerGroup)
						.channel(NioServerSocketChannel.class)
						.handler(new LoggingHandler(LogLevel.DEBUG))
						.childHandler(new HttpServerInitializer(sslCtx, container));

				channel = b.bind(socketAddress).sync().channel();
				
				print(socketAddress.getPort());
				System.out.println("Server is now ready to accept connection on [" + socketAddress +"]");
				channel.closeFuture().sync();
			} finally {
				bossGroup.shutdownGracefully();
				workerGroup.shutdownGracefully();
			}
		} else {
			System.out.println("Server is already running on [" + socketAddress +"], startup abort!");
		}
	}

	public void stop() {
		if (channel != null && channel.isActive()) {
			channel.close();
		}
	}
	
	private void print(int port) {
		System.out.println("  HH        HH                           ");
		System.out.println("  HH        HH     HHHHHH        HHHHHH           HHH         HHHHHH       HHHHHHH       ");
		System.out.println("  HH        HH    HH    HH     HH      HH         HHH        HH    HH     HH      HH     ");
		System.out.println("  HH        HH           HH   HH        HH   HHHHHHHHHHHHH          HH   HH        HH    ");
		System.out.println("  HHHHHHHHHHHH     HHHHHHH    HHHHHHHHHHH         HHH         HHHHHHH    HHHHHHHHHHH     ");
		System.out.println("  HH        HH    HH    HH    HH                  HHH        HH    HH    HH              ");
		System.out.println("  HH        HH   HH      HH    HH       HH        HHH       HH      HH    HH       HH    Version: "+Version.CURRENT_VERSION);
		System.out.println("  HH        HH    HH    HH      HHH   HHH         HHH  HH    HH    HH      HHH   HHH     Port: "+port);
		System.out.println("  HH        HH     HHHHHH HH      HHHHH            HHHH       HHHHHH HH      HHHHH       Author: zhanghr");
	}
}
