package com.on36.haetae.server;

import com.on36.haetae.udp.Scheduler;
import com.on36.haetae.udp.UDPServer;

/**
 * @author zhanghr
 * @date 2016年3月18日
 */
public class MessageThread extends Thread {

	private UDPServer server;

	public MessageThread(Scheduler scheduler) {
		try {
			server = new UDPServer(scheduler);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			server.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
