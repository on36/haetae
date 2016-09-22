package com.on36.haetae.server.core.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class EndPointManager {

	private static Map<String, String> epMap = new ConcurrentHashMap<String, String>();

	public static void put(String channel, String endPoint) {
		epMap.put(channel, endPoint);
	}
	public static void remove(String channel) {
		epMap.remove(channel);
	}
}
