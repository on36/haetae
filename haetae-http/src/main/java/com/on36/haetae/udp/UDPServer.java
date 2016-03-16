package com.on36.haetae.udp;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class UDPServer {

	private static final int PORT = Integer.parseInt(System.getProperty("port",
			"1015"));
	private static final String MULTI_ADDR = System.getProperty("multiAddr",
			"225.4.5.6");
	private static final int PACKET_MAX_LENGTH = Integer.parseInt(System
			.getProperty("maxLength", "4096"));

	private final Handler handler;
	private final DatagramChannel channel;

	public UDPServer(Handler handler) throws Exception {
		this.handler = handler;

		NetworkInterface ni = NetworkInterface.getByIndex(1);
		channel = DatagramChannel.open(StandardProtocolFamily.INET)
				.setOption(StandardSocketOptions.SO_REUSEADDR, true)
				.setOption(StandardSocketOptions.IP_MULTICAST_IF, ni)
				.setOption(StandardSocketOptions.IP_MULTICAST_TTL, 10)
				.bind(new InetSocketAddress(PORT));
		channel.configureBlocking(true);
		InetAddress group = InetAddress.getByName(MULTI_ADDR);
		channel.join(group, ni);
	}

	public void start() throws Exception {

		boolean running = true;
		// new buffer to avoid clear problems
		ByteBuffer buffer = ByteBuffer.allocate(PACKET_MAX_LENGTH);

		while (running) {
			channel.receive(buffer);
			buffer.flip();
			if (handler != null)
				handler.handle(Message.toMessage(buffer.array(), "UTF-8"));
			buffer.clear();
		}

	}

	public void close() throws Exception {
		channel.close();
	}

	public static void main(String[] args) throws Exception {

		UDPServer server = new UDPServer(new Handler() {

			@Override
			public void handle(Message message) {
				// TODO Auto-generated method stub
				System.out.println(message.content());
			}
		});

		server.start();
	}

}
