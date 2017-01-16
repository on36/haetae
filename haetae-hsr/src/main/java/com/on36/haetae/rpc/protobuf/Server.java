package com.on36.haetae.rpc.protobuf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public abstract class Server<SEND, RECIEVED> implements RPC<SEND, RECIEVED> {

	private int DEFAULT_PORT = 10001;
	private Handler<SEND, RECIEVED> handler;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	private OnActiveListener onActiveListener;
	private OnInactiveListener onInactiveListener;

	public void setOnInactiveListener(OnInactiveListener onInactiveListener) {
		this.onInactiveListener = onInactiveListener;
	}

	public void setOnActiveListener(OnActiveListener onActiveListener) {
		this.onActiveListener = onActiveListener;
	}

	public void addHandler(Handler<SEND, RECIEVED> handler) {
		this.handler = handler;
	}

	@Override
	public void start(final String host, final int port) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				bossGroup = new NioEventLoopGroup(4);
				workerGroup = new NioEventLoopGroup(128);

				ServerBootstrap b = new ServerBootstrap();
				b.group(bossGroup, workerGroup);
				b.channel(NioServerSocketChannel.class);
				// b.childOption(ChannelOption.SO_KEEPALIVE, true);
				b.option(ChannelOption.SO_BACKLOG, 1024);
				b.childOption(ChannelOption.TCP_NODELAY, true);
				b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
				b.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();

						pipeline.addLast("handler", new ServerHandlerAdapter());
						addPiple(pipeline);
					}
				});

				try {
					ChannelFuture f = b.bind(host, port).sync();
					f.channel().closeFuture().sync();
				} catch (InterruptedException e) {
					quit();
					e.printStackTrace();
				}
			}

		}).start();

	}

	@Override
	public void start() {
		start("127.0.0.1", DEFAULT_PORT);
	}

	@Override
	public void quit() {
		this.bossGroup.shutdownGracefully();
		this.workerGroup.shutdownGracefully();
	}

	private class ServerHandlerAdapter extends SimpleChannelInboundHandler<RECIEVED> {

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			super.channelActive(ctx);

			if (onActiveListener != null)
				onActiveListener.active(ctx);
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			super.channelInactive(ctx);

			if (onInactiveListener != null)
				onInactiveListener.inactive(ctx);
		}

		@Override
		protected void channelRead0(ChannelHandlerContext context, RECIEVED message) throws Exception {
			if (handler != null) {
				SEND result = handler.process(message);
				if (result != null)
					context.writeAndFlush(result);
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext context, Throwable e) throws Exception {
			context.close();
		}
	}

}
