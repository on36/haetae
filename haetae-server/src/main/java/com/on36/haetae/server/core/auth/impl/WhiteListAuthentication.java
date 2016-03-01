package com.on36.haetae.server.core.auth.impl;

import io.netty.handler.codec.http.HttpResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.on36.haetae.http.ServiceLevel;
import com.on36.haetae.http.request.HttpRequestExt;
import com.on36.haetae.server.core.auth.IAuthentication;

public class WhiteListAuthentication implements IAuthentication {

	private final Map<String, ServiceLevel> whiteMap = new ConcurrentHashMap<String, ServiceLevel>();
	private final Map<String, AtomicInteger> whiteStatsMap = new ConcurrentHashMap<String, AtomicInteger>();

	private final Map<String, AtomicLong> whiteFirstTime = new ConcurrentHashMap<String, AtomicLong>();
	private final Map<String, AtomicLong> whitePeriodTime = new ConcurrentHashMap<String, AtomicLong>();
	private final Map<String, TimeUnit> whiteTimeUnit = new ConcurrentHashMap<String, TimeUnit>();

	public void permit(String ip, ServiceLevel level, long period,
			TimeUnit periodUnit) {
		permit(ip, level);
		whitePeriodTime.put(ip, new AtomicLong(period));
		whiteTimeUnit.put(ip, periodUnit);
	}

	public void permit(String ip, ServiceLevel level) {
		whiteMap.put(ip, level);
		whiteStatsMap.put(ip, new AtomicInteger(0));
		whiteFirstTime.put(ip, new AtomicLong(0));
		whitePeriodTime.put(ip, new AtomicLong(1));
		whiteTimeUnit.put(ip, TimeUnit.SECONDS);
	}

	public void permit(String ip) {
		permit(ip, ServiceLevel.LEVELS);
	}

	@Override
	public boolean auth(HttpRequestExt request, HttpResponse response) {

		/* validation white list */
		if (!whiteMap.isEmpty()) {

			String remoteIp = request.getRemoteAddress().getAddress()
					.getHostAddress();
			ServiceLevel level = whiteMap.get(remoteIp);
			if (level == null || level == ServiceLevel.LEVELS)
				return true;

			long current = System.currentTimeMillis();
			long last = whiteFirstTime.get(remoteIp).longValue();
			if (last == 0) {
				whiteFirstTime.get(remoteIp).set(current);
				whiteStatsMap.get(remoteIp).incrementAndGet();
			} else {
				long periodInMillis = whiteTimeUnit.get(remoteIp).toMillis(
						whitePeriodTime.get(remoteIp).longValue());
				if ((current - last) < periodInMillis) {
					if (whiteStatsMap.get(remoteIp).longValue() < level.value())
						whiteStatsMap.get(remoteIp).incrementAndGet();
					else
						return false;
				} else {
					whiteFirstTime.get(remoteIp).set(current);
					whiteStatsMap.get(remoteIp).set(1);
				}
			}
		}

		return true;
	}

}
