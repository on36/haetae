package com.on36.haetae.server.core.auth.impl;

import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.on36.haetae.http.ServiceLevel;
import com.on36.haetae.http.request.HttpRequestExt;
import com.on36.haetae.server.core.auth.IAuthentication;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

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

	public void permit(String... ips) {
		for (String ip : ips)
			permit(ip, ServiceLevel.LEVELS);
	}

	public void unpermitAll() {
		whiteMap.clear();
		whiteStatsMap.clear();
		whiteFirstTime.clear();
		whitePeriodTime.clear();
		whiteTimeUnit.clear();
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

	@Override
	public HttpResponseStatus auth(HttpRequestExt request,
			HttpResponse response) {

		/* validation white list */
		if (!whiteMap.isEmpty()) {

			String remoteIp = request.getRemoteAddress();
			Integer level = whiteMap.get(remoteIp);
			if (level == null)
				return FORBIDDEN;

			long current = System.currentTimeMillis();
			long last = whiteFirstTime.get(remoteIp).longValue();
			long periodInMillis = whiteTimeUnit.get(remoteIp)
					.toMillis(whitePeriodTime.get(remoteIp).longValue());

			response.headers().set("X-RateLimit-Limit", level.intValue());
			long remaining = 0;
			if (last == 0)
				remaining = (long) (periodInMillis * 0.001);
			else
				remaining = (long) ((periodInMillis - (current - last))
						* 0.001);
			response.headers().set("X-RateLimit-Reset", remaining > 0
					? remaining : (long) (periodInMillis * 0.001));

			if (last == 0) {
				whiteFirstTime.get(remoteIp).set(current);
				whiteStatsMap.get(remoteIp).incrementAndGet();
			} else {
				if ((current - last) < periodInMillis) {
					if (level.intValue() == -1 || whiteStatsMap.get(remoteIp)
							.intValue() < level.intValue())
						whiteStatsMap.get(remoteIp).incrementAndGet();
					else {
						response.headers().set("X-RateLimit-Remaining", 0);
						return FORBIDDEN;
					}
				} else {
					whiteFirstTime.get(remoteIp).set(current);
					whiteStatsMap.get(remoteIp).set(1);
				}
			}

			if (level.intValue() > 0)
				response.headers().set("X-RateLimit-Remaining",
						(level.intValue()
								- whiteStatsMap.get(remoteIp).intValue()));
			else
				response.headers().set("X-RateLimit-Remaining", -1);
		}

		return null;
	}

}
