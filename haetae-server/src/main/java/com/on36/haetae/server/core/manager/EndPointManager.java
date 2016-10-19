package com.on36.haetae.server.core.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.on36.haetae.server.core.manager.listener.ActiveListener;
import com.on36.haetae.server.core.manager.listener.InactiveListener;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class EndPointManager {

	private ActiveListener activeListener;
	private InactiveListener inactiveListener;

	private static Map<String, String> epMap = new ConcurrentHashMap<String, String>();

	private EndPointManager() {
	}

	private static class EndPointManagerHolder {
		private static EndPointManager instance = new EndPointManager();
	}

	public static EndPointManager getInstance() {
		return EndPointManagerHolder.instance;
	}

	public void put(String channel, String endPoint) {
		if (activeListener != null)
			activeListener.handler(endPoint);
		epMap.put(channel, endPoint);
	}

	public void remove(String channel) {
		if (inactiveListener != null) {
			String endpoint = epMap.remove(channel);
			if (endpoint != null)
				inactiveListener.handler(endpoint);
		} else
			epMap.remove(channel);
	}

	public EndPointManager addActiveListener(ActiveListener listener) {
		this.activeListener = listener;
		return this;
	}

	public EndPointManager addInactiveListener(InactiveListener listener) {
		this.inactiveListener = listener;
		return this;
	}
}
