package com.on36.haetae.server.core.auth.impl;

import static io.netty.handler.codec.http.HttpResponseStatus.TOO_MANY_REQUESTS;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.on36.haetae.api.Context;
import com.on36.haetae.server.core.auth.IAuthentication;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class RequestFlowAuthentication implements IAuthentication {

	private AtomicLong lastTime = new AtomicLong(0L);
	private AtomicInteger curTimes = new AtomicInteger(0);

	private AtomicLong tpsTime = new AtomicLong(0L);
	private AtomicInteger curTPS = new AtomicInteger(0);
	private AtomicInteger maxTPS = new AtomicInteger(0);

	private long period = -1;
	private TimeUnit periodUnit = TimeUnit.SECONDS;
	private int periodTimes = -1;

	public void every(long period, TimeUnit periodUnit, int times) {

		this.periodTimes = times;
		this.period = period;
		this.periodUnit = periodUnit;
	}

	public int getCurTPS() {
		long current = System.currentTimeMillis();
		long lastTPSTime = tpsTime.longValue();

		if ((current - lastTPSTime) > 1000) {
			tpsTime.set(current);
			curTPS.set(0);
		}

		return curTPS.intValue();
	}

	public int getMaxTPS() {
		return maxTPS.intValue();
	}

	@Override
	public HttpResponseStatus auth(Context context,
			HttpResponse response) {
		/* validation request times */
		long current = System.currentTimeMillis();
		if (period > -1) {
			long periodInMillis = periodUnit.toMillis(period);
			long last = lastTime.get();

			response.headers().set("X-RateLimit-Limit", periodTimes);
			long remaining = 0;
			if (last == 0)
				remaining = (long) (periodInMillis * 0.001);
			else
				remaining = (long) ((periodInMillis - (current - last))
						* 0.001);
			response.headers().set("X-RateLimit-Reset", remaining > 0
					? remaining : (long) (periodInMillis * 0.001));

			if (last == 0) {
				lastTime.set(current);
				curTimes.incrementAndGet();
			} else {
				if ((current - last) < periodInMillis) {
					if (curTimes.get() < periodTimes)
						curTimes.incrementAndGet();
					else {
						response.headers().set("X-RateLimit-Remaining", 0);
						return TOO_MANY_REQUESTS;
					}
				} else {
					lastTime.set(current);
					curTimes.set(1);
				}
			}

			response.headers().set("X-RateLimit-Remaining",
					periodTimes - curTimes.get());
		}

		long lastTPSTime = tpsTime.longValue();
		if (tpsTime.longValue() == 0) {
			tpsTime.set(current);
			curTPS.incrementAndGet();
		} else {
			if ((current - lastTPSTime) < 1000) {
				curTPS.incrementAndGet();
			} else {
				tpsTime.set(current);
				curTPS.set(1);
			}
		}

		if (maxTPS.intValue() < curTPS.intValue())
			maxTPS.set(curTPS.intValue());
		return null;
	}

}
