package com.on36.haetae.net.udp;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import com.on36.haetae.net.utils.NetworkUtils;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class UDPServer {

	private static final int PORT = Integer.parseInt(System.getProperty("port",
			"1984"));
	private static final String MULTI_ADDR = System.getProperty("multiAddr",
			"225.4.5.6");
	private static final int PACKET_MAX_LENGTH = Integer.parseInt(System
			.getProperty("maxLength", "4096"));

	private final Scheduler scheduler;
	private final DatagramChannel channel;

	private boolean running = true;

	public UDPServer(Scheduler scheduler) throws Exception {
		this.scheduler = scheduler;

		NetworkInterface ni = NetworkUtils.getLocalNetworkInterface();
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
		// new buffer to avoid clear problems
		ByteBuffer buffer = ByteBuffer.allocate(PACKET_MAX_LENGTH);

		while (running) {
			channel.receive(buffer);
			buffer.flip();
			if (scheduler != null)
				scheduler.revieve(Message.toMessage(buffer.array()));
			buffer.clear();
		}

	}

	public void close() throws Exception {
		running = false;
		channel.close();
	}
}
