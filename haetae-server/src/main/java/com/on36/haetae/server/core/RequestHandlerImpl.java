package com.on36.haetae.server.core;

import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.core.CustomHandler;
import com.on36.haetae.http.RequestHandler;
import com.on36.haetae.http.request.HttpRequestExt;
import com.on36.haetae.server.core.body.EntityResonseBody;
import com.on36.haetae.server.core.body.InterpolatedResponseBody;
import com.on36.haetae.server.core.body.ResponseBody;

public class RequestHandlerImpl implements RequestHandler {

	private int statusCode = -1;
	private String contentType;
	private String body;
	private long delay = -1;
	private TimeUnit delayUnit;
	private long period = -1;
	private TimeUnit periodUnit;
	private int periodTimes = -1;
	private long timeout = -1;
	private TimeUnit timeoutUnit;
	private boolean hasSession = false;

	private Set<SimpleImmutableEntry<String, String>> headers = new HashSet<SimpleImmutableEntry<String, String>>();

	private AtomicLong firstTime = new AtomicLong(0L);
	private AtomicInteger curTimes = new AtomicInteger(0);

	private CustomHandler<?> httpHandler;
	private String[] blackList;
	private String[] whiteList;

	public RequestHandler with(int statusCode, String contentType, String body) {

		this.statusCode = statusCode;
		this.contentType = contentType;
		this.body = body;
		return this;
	}

	public RequestHandler with(CustomHandler<?> customHandler) {

		this.httpHandler = customHandler;
		return this;
	}

	public RequestHandler ban(String... blackips) {

		this.blackList = blackips;
		return this;
	}

	public RequestHandler permit(String... whiteips) {

		this.whiteList = whiteips;
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

		this.periodTimes = times;
		this.period = period;
		this.periodUnit = periodUnit;
		return this;
	}

	public RequestHandler withTimeout(long timeout, TimeUnit timeoutUnit) {

		this.timeout = timeout;
		this.timeoutUnit = timeoutUnit;
		return this;
	}

	public RequestHandler withRedirect(String location) {

		return withRedirect(location, FOUND.code());
	}

	public RequestHandler withRedirect(String location, int statusCode) {

		this.statusCode = statusCode;
		this.contentType = "text/plain";
		this.body = "";
		withHeader("Location", location);
		withHeader("Connection", "close");
		return this;
	}

	public ResponseBody body(Context context) {

		if (getCustomHandler() != null) {
			return new EntityResonseBody(getCustomHandler().handle(context));
		}

		return new InterpolatedResponseBody(body, context);
	}

	public int statusCode() {
		return statusCode;
	}

	public String contentType() {
		return contentType;
	}

	public long delay() {
		return delay;
	}

	public TimeUnit delayUnit() {
		return delayUnit;
	}

	public long period() {
		return period;
	}

	public TimeUnit periodUnit() {
		return periodUnit;
	}

	public int periodTimes() {
		return periodTimes;
	}

	public boolean hasTimeout() {
		return timeout != -1;
	}

	public long timeout() {
		return timeout;
	}

	public TimeUnit timeoutUnit() {
		return timeoutUnit;
	}

	public Set<SimpleImmutableEntry<String, String>> headers() {
		return new HashSet<SimpleImmutableEntry<String, String>>(headers);
	}

	public CustomHandler<?> getCustomHandler() {
		return httpHandler;
	}

	public boolean hasContentType() {
		return contentType != null;
	}

	public boolean hasSession() {
		return hasSession;
	}

	public boolean validation(HttpRequestExt request, HttpResponse response) {

		/* validation black list */
		if (blackList != null) {

			List<String> blackips = Arrays.asList(blackList);
			String remoteIp = request.getRemoteAddress().getAddress()
					.getHostAddress();
			if (blackips.contains(remoteIp)) {

				send(response, "Your ip is at black list");
				return false;
			}
		}

		/* validation white list */
		if (whiteList != null) {

			List<String> whiteips = Arrays.asList(whiteList);
			String remoteIp = request.getRemoteAddress().getAddress()
					.getHostAddress();
			if (!whiteips.contains(remoteIp)) {

				send(response, "Your ip is not at white list");
				return false;
			}
		}

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

						send(response, "Reached max request times");
						return false;
					}
				} else {
					firstTime.set(current);
					curTimes.set(1);
				}
			}
		}

		return true;
	}

	private void send(HttpResponse response,String message) {
		if (response instanceof HttpContent) {
			HttpContent httpContent = (HttpContent) response;
			ByteBuf content = httpContent.content();
			if (message != null) {
				content.writeBytes(message.getBytes(CharsetUtil.UTF_8));
			}
		}
	}
}
