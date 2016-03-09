package com.on36.haetae.server.core.auth.impl;

import io.netty.handler.codec.http.HttpResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.on36.haetae.http.request.HttpRequestExt;
import com.on36.haetae.server.core.auth.IAuthentication;

public class BlackListAuthentication implements IAuthentication {

	private final static int MAX_BLACK_REQUEST_PER_IP = 100;
	private final Map<String, AtomicLong> lastTime = new ConcurrentHashMap<String, AtomicLong>();
	private final Map<String, AtomicInteger> curTimes = new ConcurrentHashMap<String, AtomicInteger>();

	private List<String> blackList = new ArrayList<String>();

	public void ban(String... blackips) {

		List<String> newBlack = Arrays.asList(blackips);
		this.blackList.addAll(newBlack);
	}

	public void unban(String... blackips) {

		List<String> newBlack = Arrays.asList(blackips);
		if (blackList != null)
			this.blackList.removeAll(newBlack);

		for (String ip : newBlack) {
			lastTime.remove(ip);
			curTimes.remove(ip);
		}
	}

	public void unbanAll() {

		if (blackList != null)
			this.blackList.clear();

		lastTime.clear();
		curTimes.clear();
	}

	@Override
	public boolean auth(HttpRequestExt request, HttpResponse response) {
		/* validation black list */
		String remoteIp = request.getRemoteAddress();
		if (blackList != null && blackList.contains(remoteIp))
			return false;

		long current = System.currentTimeMillis();
		AtomicLong ltime = lastTime.get(remoteIp);
		if (ltime == null || ltime.longValue() == 0) {
			lastTime.put(remoteIp, new AtomicLong(current));
			curTimes.put(remoteIp, new AtomicInteger(1));
		} else {
			long last = lastTime.get(remoteIp).longValue();
			if ((current - last) < 1000) {
				curTimes.get(remoteIp).incrementAndGet();
				if (curTimes.get(remoteIp).intValue() > MAX_BLACK_REQUEST_PER_IP)
					blackList.add(remoteIp);
			} else {
				lastTime.get(remoteIp).set(current);
				curTimes.get(remoteIp).set(1);
			}
		}
		return true;
	}
}
