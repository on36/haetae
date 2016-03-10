package com.on36.haetae.udp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class UDPServer {

	private static final int PORT = Integer.parseInt(System.getProperty("port",
			"1015"));
	private static final String MULTI_ADDR = System.getProperty("multiAddr",
			"225.0.0.1");
	private static final int PACKET_MAX_LENGTH = Integer.parseInt(System
			.getProperty("maxLength", "4096"));

	private final Handler handler;

	public UDPServer(Handler handler) {
		this.handler = handler;
	}

	public void start() {
		byte[] buf = new byte[PACKET_MAX_LENGTH];
		MulticastSocket ms = null;
		DatagramPacket dp = null;
		boolean running = true;
		try {
			ms = new MulticastSocket(PORT);
			dp = new DatagramPacket(buf, PACKET_MAX_LENGTH);
			// ms.setSoTimeout(5000);
			InetAddress group = InetAddress.getByName(MULTI_ADDR);
			ms.joinGroup(group);
			while (running) {
				ms.receive(dp);
				if (handler != null)
					handler.handle(Message.toMessage(dp.getData(), "UTF-8"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ms.close();
		}
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
