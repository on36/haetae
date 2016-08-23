package com.on36.haetae.server;

import com.on36.haetae.common.conf.Configuration;
import com.on36.haetae.common.conf.Constant;
import com.on36.haetae.common.utils.NetworkUtils;
import com.on36.haetae.config.client.ConfigClient;
import com.on36.haetae.http.Environment;

/**
 * @author zhanghr
 * @date 2016年1月12日
 */
public class Heartbeat implements Runnable {

	private final int port;

	private final String root;

	private boolean running = true;

	private static String mineEndPoint;

	public Heartbeat(String root, int port) {
		this.port = port;
		this.root = root;
	}

	public static String myself() {
		return mineEndPoint;
	}

	@Override
	public void run() {
		mineEndPoint = NetworkUtils.getLocalIP() + ":" + port;
		long period = Configuration.create().getLong(
				Constant.K_SERVER_HEARTBEAT_PERIOD,
				Constant.V_SERVER_HEARTBEAT_PERIOD);
		while (running) {
			try {
				Thread.sleep(period);// 休眠一次
				ConfigClient.registerNode(root + "/" + mineEndPoint,
						Environment.pid());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		running = false;
	}
}
