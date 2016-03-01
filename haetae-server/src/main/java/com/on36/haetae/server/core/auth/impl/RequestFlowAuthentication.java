package com.on36.haetae.server.core.auth.impl;

import io.netty.handler.codec.http.HttpResponse;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.on36.haetae.http.request.HttpRequestExt;
import com.on36.haetae.server.core.auth.IAuthentication;

public class RequestFlowAuthentication implements IAuthentication {

	private AtomicLong firstTime = new AtomicLong(0L);
	private AtomicInteger curTimes = new AtomicInteger(0);

	private long period = -1;
	private TimeUnit periodUnit = TimeUnit.SECONDS;
	private int periodTimes = -1;

	public void every(long period, TimeUnit periodUnit, int times) {

		this.periodTimes = times;
		this.period = period;
		this.periodUnit = periodUnit;
	}

	@Override
	public boolean auth(HttpRequestExt request, HttpResponse response) {
		/* validation request times */
		if (period > -1) {

			long current = System.currentTimeMillis();
			long last = firstTime.get();
			if (last == 0) {
				firstTime.set(current);
				curTimes.incrementAndGet();
			} else {
				long periodInMillis = periodUnit.toMillis(period);
				if ((current - last) < periodInMillis) {
					if (curTimes.get() < periodTimes) {
						curTimes.incrementAndGet();
					} else {

						return false;
					}
				} else {
					firstTime.set(current);
					curTimes.set(1);
				}
			}
		}
		return false;
	}

}
