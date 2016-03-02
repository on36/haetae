package com.on36.haetae.server.core;

import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.core.CustomHandler;
import com.on36.haetae.http.RequestHandler;
import com.on36.haetae.http.ServiceLevel;
import com.on36.haetae.http.request.HttpRequestExt;
import com.on36.haetae.server.core.auth.impl.BlackListAuthentication;
import com.on36.haetae.server.core.auth.impl.RequestFlowAuthentication;
import com.on36.haetae.server.core.auth.impl.WhiteListAuthentication;
import com.on36.haetae.server.core.body.EntityResonseBody;
import com.on36.haetae.server.core.body.InterpolatedResponseBody;
import com.on36.haetae.server.core.body.ResponseBody;
import com.on36.haetae.server.core.stats.Statistics;

public class RequestHandlerImpl implements RequestHandler {

	private int statusCode = -1;
	private String body;
	private boolean hasSession = false;

	private AtomicLong minElapsedTime = new AtomicLong(0);
	private AtomicLong avgElapsedTime = new AtomicLong(0);
	private AtomicLong maxElapsedTime = new AtomicLong(0);

	private AtomicInteger maxConcurrent = new AtomicInteger(0);;
	private AtomicInteger successHandlTimes = new AtomicInteger(0);;
	private AtomicInteger failHandlTimes = new AtomicInteger(0);;

	private Set<SimpleImmutableEntry<String, String>> headers = new HashSet<SimpleImmutableEntry<String, String>>();

	private CustomHandler<?> httpHandler;
	private BlackListAuthentication blackList = new BlackListAuthentication();
	private WhiteListAuthentication whiteList = new WhiteListAuthentication();
	private RequestFlowAuthentication requestFlow = new RequestFlowAuthentication();
	private boolean auth = true;

	public RequestHandler with(String body) {

		this.body = body;
		return this;
	}

	public RequestHandler with(CustomHandler<?> customHandler) {

		this.httpHandler = customHandler;
		return this;
	}

	public RequestHandler auth(boolean authentication) {

		this.auth = authentication;
		return this;
	}

	public RequestHandler unban(String... blackips) {

		blackList.unban(blackips);
		return this;
	}

	public RequestHandler ban(String... blackips) {

		blackList.ban(blackips);
		return this;
	}

	public RequestHandler unpermit(String... whiteips) {

		whiteList.unpermit(whiteips);
		return this;
	}

	public RequestHandler permit(String... whiteips) {

		whiteList.permit(whiteips);
		return this;
	}

	public RequestHandler permit(String ip, ServiceLevel level) {

		whiteList.permit(ip, level);
		return this;
	}

	public RequestHandler withHeader(String name, String value) {

		headers.add(new SimpleImmutableEntry<String, String>(name, value));
		return this;
	}

	public RequestHandler session(boolean has) {

		this.hasSession = has;
		return this;
	}

	public RequestHandler every(long period, TimeUnit periodUnit, int times) {

		requestFlow.every(period, periodUnit, times);
		return this;
	}

	public RequestHandler every(int times) {

		return every(1, TimeUnit.SECONDS, times);
	}

	public RequestHandler withRedirect(String location) {

		this.statusCode = FOUND.code();
		this.body = "";
		withHeader("Location", location);
		withHeader("Connection", "close");
		return this;
	}

	public ResponseBody body(Context context) {

		if (getCustomHandler() != null)
			return new EntityResonseBody(getCustomHandler().handle(context));

		return new InterpolatedResponseBody(body, context);
	}

	public void stats(HttpResponse response, long elapsedTime) {

		// total times
		int totalTimes = successHandlTimes.intValue()
				+ failHandlTimes.intValue();
		// total time(ms)
		long totalTime = totalTimes * avgElapsedTime.longValue() + elapsedTime;

		if (HttpResponseStatus.OK.equals(response.getStatus()))
			successHandlTimes.incrementAndGet();
		else
			failHandlTimes.incrementAndGet();

		// elapsed time
		if (minElapsedTime.longValue() == 0
				|| minElapsedTime.longValue() > elapsedTime)
			minElapsedTime.set(elapsedTime);
		if (maxElapsedTime.longValue() == 0
				|| maxElapsedTime.longValue() < elapsedTime)
			maxElapsedTime.set(elapsedTime);

		long avgTime = totalTime / (totalTimes + 1);
		avgElapsedTime.set(avgTime);
	}

	public int statusCode() {
		return statusCode;
	}

	public Set<SimpleImmutableEntry<String, String>> headers() {
		return new HashSet<SimpleImmutableEntry<String, String>>(headers);
	}

	public CustomHandler<?> getCustomHandler() {
		return httpHandler;
	}

	public boolean hasSession() {
		return hasSession;
	}

	public boolean validation(HttpRequestExt request, HttpResponse response) {

		if (auth) {
			boolean result = blackList.auth(request, response);
			if (!result)
				return result;
			result = whiteList.auth(request, response);
			if (!result)
				return result;
			result = requestFlow.auth(request, response);
			if (!result)
				return result;
		}
		return true;
	}

	public Statistics getStatistics() {
		return new Statistics(successHandlTimes.intValue(),
				failHandlTimes.intValue(), minElapsedTime.longValue(),
				avgElapsedTime.longValue(), maxElapsedTime.longValue(),
				maxConcurrent.intValue());
	}
}
