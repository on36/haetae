package com.on36.haetae.server;

import com.on36.haetae.common.conf.Constant;
import com.on36.haetae.common.log.LogLevel;
import com.on36.haetae.common.utils.NetworkUtils;
import com.on36.haetae.config.client.ConfigClient;
import com.on36.haetae.config.client.HttpClient;
import com.on36.haetae.http.Environment;
import com.on36.haetae.http.core.HTTPServer;
import com.on36.haetae.net.udp.Scheduler;

/**
 * @author zhanghr
 * @date 2016年1月12日
 */
public class Heartbeat implements Runnable {

	private final int port;

	private final String root;

	private final Scheduler scheduler;

	private boolean running = true;

	private static String mineEndPoint;

	private static int RETRIES = 10;
	private static long FAIL_NEXT_PERIOD = 30;

	public Heartbeat(String root, int port, Scheduler scheduler) {
		this.port = port;
		this.root = root;
		this.scheduler = scheduler;
	}

	public static String myself() {
		return mineEndPoint;
	}

	@Override
	public void run() {
		mineEndPoint = NetworkUtils.getInnerIP() + ":" + port;
		long period = ConfigClient.getLong(Constant.K_SERVER_HEARTBEAT_PERIOD,
				Constant.V_SERVER_HEARTBEAT_PERIOD);
		int retry = RETRIES;
		while (running) {
			try {
				Thread.sleep(period);// 休眠一次
				if (HTTPServer.RUNNING)
					ConfigClient.registerNode(root + "/" + mineEndPoint,
							Environment.pid());
			} catch (Exception e) {
				retry--;
				if (retry > 0) {
					scheduler
							.trace(this.getClass(), LogLevel.WARN,
									"Config Agent connected failed. Already tried "
											+ (RETRIES - retry) + " time(s)",
									e);
				} else {
					scheduler.trace(this.getClass(), LogLevel.WARN,
							"Config Agent connected failed. Already tried "
									+ (RETRIES - retry)
									+ " time(s), will sleep " + FAIL_NEXT_PERIOD
									+ " minutes and try again!",
							e);
					try {
						Thread.sleep(FAIL_NEXT_PERIOD * 60 * 1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
	}

	public void close() {
		running = false;
		if (HTTPServer.RUNNING)
			ConfigClient.unregisterNode(root + "/" + mineEndPoint);
		HttpClient.getInstance().close();
	}
}
