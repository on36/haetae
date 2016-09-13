package com.on36.haetae.server.core.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class EndPointManager {

	private static Map<String, Long> epMap = new ConcurrentHashMap<String, Long>();

	public static void update(String endPoint) {
		long current = System.currentTimeMillis();
		epMap.put(endPoint, current);
	}
}
