package com.on36.haetae.server;

import com.on36.haetae.http.Configuration;
import com.on36.haetae.net.udp.Message;
import com.on36.haetae.net.udp.Message.Title;
import com.on36.haetae.net.udp.Scheduler;
import com.on36.haetae.net.utils.NetworkUtils;

/**
 * @author zhanghr
 * @date 2016年1月12日
 */
public class Heartbeat extends Thread {

	private final int port;
	private final Scheduler scheduler;

	private boolean running = true;

	private static String mineEndPoint;

	public Heartbeat(Scheduler scheduler, int port) {
		this.port = port;
		this.scheduler = scheduler;
	}

	public static String myself() {
		return mineEndPoint;
	}

	@Override
	public void run() {
		mineEndPoint = NetworkUtils.getLocalIP() + ":" + port;
		long period = Configuration.create().getLong("httpServer.heartbeat.period", 3000);
		while (running) {
			try {
				scheduler.send(Message.newMessage(Title.ENDPOINT,
						mineEndPoint.getBytes("UTF-8")));
				Thread.sleep(period);//3秒一次心跳信息
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
