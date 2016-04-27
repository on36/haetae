package com.on36.haetae.net.udp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class UDPClient {

	private static final int PORT = Integer.parseInt(System.getProperty("port",
			"1984"));
	private static final String MULTI_ADDR = System.getProperty("multiAddr",
			"225.4.5.6");
	private static final int PACKET_MAX_LENGTH = Integer.parseInt(System
			.getProperty("maxLength", "4096"));

	public void send(Message message) throws Exception {
		MulticastSocket sender = new MulticastSocket();
		sender.setTimeToLive(4);
		byte[] buf = message.toBytes();
		if (buf.length > PACKET_MAX_LENGTH) {
			sender.close();
			throw new IllegalArgumentException(
					"the length of message is too large !");
		}
		InetAddress mutilAddress = InetAddress.getByName(MULTI_ADDR);
		sender.joinGroup(mutilAddress);

		DatagramPacket sendPacket = new DatagramPacket(buf, buf.length,
				mutilAddress, PORT);
		sender.send(sendPacket);
		sender.close();
	}

}
