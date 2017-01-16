package com.on36.haetae.rpc.protobuf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public abstract class Client<SEND, RECIEVED> implements RPC<SEND, RECIEVED> {

	enum Status {
		WAITING, NOTREACHED, CONNECTED, CLOSED
	}

	protected Logger LOG = LoggerFactory.getLogger(this.getClass());

	private Status NOW = Status.WAITING;
	private String DEFAULT_HOST = "127.0.0.1";
	private int DEFAULT_PORT = 10001;
	private NioEventLoopGroup group;
	private Channel ch;

	private Handler<Void, RECIEVED> handler;
	private boolean closeAfterAction = false;

	private OnActiveListener onActiveListener;
	private OnInactiveListener onInactiveListener;

	public void setCloseAfterAction(boolean closeAfterAction) {
		this.closeAfterAction = closeAfterAction;
	}

	public void setOnInactiveListener(OnInactiveListener onInactiveListener) {
		this.onInactiveListener = onInactiveListener;
	}

	public void setOnActiveListener(OnActiveListener onActiveListener) {
		this.onActiveListener = onActiveListener;
	}

	private void addHandler(Handler<Void, RECIEVED> handler) {
		this.handler = handler;
	}

	@Override
	public void start(final String host, final int port) {
		this.DEFAULT_HOST = host;
		this.DEFAULT_PORT = port;

		new Thread(new Runnable() {

			@Override
			public void run() {
				group = new NioEventLoopGroup();
				Bootstrap bootstrap = new Bootstrap();
				bootstrap.group(group);
				bootstrap.channel(NioSocketChannel.class);
				bootstrap.option(ChannelOption.TCP_NODELAY, true);
				bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000);
				bootstrap.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();

						pipeline.addLast("handler", new ClientHandlerAdapter());
						addPiple(pipeline);
					}
				});

				try {
					ChannelFuture f = bootstrap.connect(host, port).sync();
					ch = f.channel();
					ch.closeFuture().sync();
				} catch (Exception e) {
					group.shutdownGracefully();
					NOW = Status.NOTREACHED;
					e.printStackTrace();
				}
			}
		}, "ConnectionThread").start();

	}

	private void error() throws Exception {
		throw new Exception(
				"Server[" + DEFAULT_HOST + ":" + DEFAULT_PORT + "] is not reached or Connection is closed !");
	}

	@Override
	public void start() {
		start(DEFAULT_HOST, DEFAULT_PORT);
	}

	public void send(SEND message, Handler<Void, RECIEVED> handler) throws Exception {
		switch (NOW) {
		case WAITING:
		case CONNECTED:
			addHandler(handler);
			add(message);
			break;
		default:
			error();
		}
	}

	private void add(SEND message) throws Exception {
		ChannelFuture future = ch.writeAndFlush(message);
		future.syncUninterruptibly();
	}

	@Override
	public void quit() {
		group.shutdownGracefully();
		NOW = Status.CLOSED;
	}

	private class ClientHandlerAdapter extends SimpleChannelInboundHandler<RECIEVED> {

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			if (onActiveListener != null)
				onActiveListener.active(ctx);
			ch = ctx.channel();
			NOW = Status.CONNECTED;
			super.channelActive(ctx);
			System.out.println("connected!");
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			if (onInactiveListener != null)
				onInactiveListener.inactive(ctx);
			super.channelInactive(ctx);
		}

		@Override
		protected void channelRead0(ChannelHandlerContext context, RECIEVED message) throws Exception {
			if (handler != null)
				handler.process(message);
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			super.channelReadComplete(ctx);

			if (closeAfterAction)
				quit();
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext context, Throwable e) throws Exception {
			context.close();
		}
	}
}
