package com.on36.haetae.server;

import com.on36.haetae.net.udp.Scheduler;
import com.on36.haetae.net.udp.UDPServer;

/**
 * @author zhanghr
 * @date 2016年3月18日
 */
public class MessageThread implements Runnable {

	private UDPServer server;

	public MessageThread(Scheduler scheduler) {
		try {
			server = new UDPServer(scheduler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			server.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
