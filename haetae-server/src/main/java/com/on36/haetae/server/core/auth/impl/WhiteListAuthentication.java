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

	private final Map<String, Integer> whiteMap = new ConcurrentHashMap<String, Integer>();
	private final Map<String, AtomicInteger> whiteStatsMap = new ConcurrentHashMap<String, AtomicInteger>();

	private final Map<String, AtomicLong> whiteFirstTime = new ConcurrentHashMap<String, AtomicLong>();
	private final Map<String, AtomicLong> whitePeriodTime = new ConcurrentHashMap<String, AtomicLong>();
	private final Map<String, TimeUnit> whiteTimeUnit = new ConcurrentHashMap<String, TimeUnit>();

	public void permit(String ip, ServiceLevel level, long period,
			TimeUnit periodUnit) {
		permit(ip, level.value(), period, periodUnit);
	}

	public void permit(String ip, ServiceLevel level) {
		permit(ip, level, 1, TimeUnit.SECONDS);
	}

	public void permit(String ip, int times) {
		permit(ip, times, 1, TimeUnit.SECONDS);
	}

	public void permit(String ip, int times, long period, TimeUnit periodUnit) {
		whiteMap.put(ip, times);
		whiteStatsMap.put(ip, new AtomicInteger(0));
		whiteFirstTime.put(ip, new AtomicLong(0));
		whitePeriodTime.put(ip, new AtomicLong(period));
		whiteTimeUnit.put(ip, periodUnit);
	}

	public void unpermit(String... ips) {
		for (String ip : ips) {
			whiteMap.remove(ip);
			whiteStatsMap.remove(ip);
			whiteFirstTime.remove(ip);
			whitePeriodTime.remove(ip);
			whiteTimeUnit.remove(ip);
		}
	}

	public void permit(String... ips) {
		for (String ip : ips)
			permit(ip, ServiceLevel.LEVELS);
	}

	@Override
	public boolean auth(HttpRequestExt request, HttpResponse response) {

		/* validation white list */
		if (!whiteMap.isEmpty()) {

			String remoteIp = request.getRemoteAddress().getAddress()
					.getHostAddress();
			Integer level = whiteMap.get(remoteIp);
			if (level == null)
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
					if (whiteStatsMap.get(remoteIp).longValue() < level
							.longValue())
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
