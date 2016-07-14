package com.on36.haetae.server.core;

import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;

import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.core.HttpHandler;
import com.on36.haetae.api.http.MediaType;
import com.on36.haetae.http.RequestHandler;
import com.on36.haetae.http.ServiceLevel;
import com.on36.haetae.http.request.HttpRequestExt;
import com.on36.haetae.http.route.RouteHelper;
import com.on36.haetae.net.udp.Scheduler;
import com.on36.haetae.server.HaetaeServer;
import com.on36.haetae.server.core.auth.impl.BlackListAuthentication;
import com.on36.haetae.server.core.auth.impl.RequestFlowAuthentication;
import com.on36.haetae.server.core.auth.impl.WhiteListAuthentication;
import com.on36.haetae.server.core.body.EntityResponseBody;
import com.on36.haetae.server.core.body.InterpolatedResponseBody;
import com.on36.haetae.server.core.body.ResponseBody;
import com.on36.haetae.server.core.body.TimeoutResponseBody;
import com.on36.haetae.server.core.stats.Statistics;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class RequestHandlerImpl implements RequestHandler {

	private int statusCode = -1;
	private String body = null;
	private boolean hasSession = false;

	private Object object = null;
	private Method method = null;

	private long timeout = -1;
	private TimeUnit timeoutUnit = TimeUnit.MILLISECONDS;

	private AtomicLong minElapsedTime = new AtomicLong(0);
	private AtomicLong avgElapsedTime = new AtomicLong(0);
	private AtomicLong maxElapsedTime = new AtomicLong(0);
	private AtomicLong totalTime = new AtomicLong(0);

	private AtomicInteger successHandlTimes = new AtomicInteger(0);
	private AtomicInteger failHandlTimes = new AtomicInteger(0);

	private Set<SimpleImmutableEntry<String, String>> headers = new HashSet<SimpleImmutableEntry<String, String>>();

	private HttpHandler<?> httpHandler;
	private BlackListAuthentication blackList = new BlackListAuthentication();
	private WhiteListAuthentication whiteList = new WhiteListAuthentication();
	private RequestFlowAuthentication requestFlow = new RequestFlowAuthentication();
	private boolean auth = true;

	private final ExecutorService es = HaetaeServer.threadPools;

	private final Scheduler scheduler;

	public RequestHandlerImpl(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public RequestHandler with(String body) {

		this.body = body;
		return this;
	}

	public RequestHandler with(HttpHandler<?> customHandler) {

		this.httpHandler = customHandler;
		return this;
	}

	@Override
	public RequestHandler with(Object object, Method method) {
		if (object == null || method == null)
			throw new IllegalArgumentException(
					"Method or object cannot be null");
		Class<?>[] clazzs = method.getParameterTypes();
		if (clazzs.length == 1 && clazzs[0] == Context.class) {
			this.object = null;
			this.method = null;
			this.object = object;
			this.method = method;
		} else
			throw new IllegalArgumentException(
					"The parameter type of Method is not "
							+ Context.class.getName());

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

	public RequestHandler permit(String ip, int times) {

		whiteList.permit(ip, times);
		return this;
	}

	public RequestHandler permit(String ip, ServiceLevel level, long period,
			TimeUnit periodUnit) {

		whiteList.permit(ip, level, period, periodUnit);
		return this;
	}

	public RequestHandler permit(String ip, int times, long period,
			TimeUnit periodUnit) {

		whiteList.permit(ip, times, period, periodUnit);
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

	public RequestHandler timeout(long timeout, TimeUnit timeoutUnit) {

		this.timeout = timeout;
		this.timeoutUnit = timeoutUnit;
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

	public ResponseBody body(final Context context) throws Exception {

		Future<Object> future = null;
		try {
			if (getCustomHandler() != null || method != null) {
				future = es.submit(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						if (getCustomHandler() != null)
							return getCustomHandler().handle(context);
						else
							return method.invoke(object, context);
					}
				});
				Object result = null;
				if (timeout > 0)
					result = future.get(timeout, timeoutUnit);
				else
					result = future.get();
				return new EntityResponseBody(result);
			}
		} catch (TimeoutException | InterruptedException e) {
			future.cancel(true);
			return new TimeoutResponseBody();
		} finally {

		}

		return new InterpolatedResponseBody(body, context);
	}

	public String body(Context context, String resource) throws Exception {
		long start = System.currentTimeMillis();
		try {
			return body(context).content();
		} finally {
			long elapsedTime = System.currentTimeMillis() - start;
			String requestId = context.getRequestId();
			String deep = ((SimpleContext) context).nextDeep();

			trace(elapsedTime, requestId, resource, deep);
		}
	}

	private Object html(Object result, Context context) {
		if (result == null)
			return "";
		else if (result instanceof String) {
			String html = null;
			String contentType = context.getContenType();
			if (MediaType.APPLICATION_JSON.value().equals(contentType)
					|| MediaType.TEXT_JSON.value().equals(contentType)) {
				return result;
			} else {
				html = (String) result;
				html = html.replace("\t", "&nbsp;&nbsp;");// 替换跳格
				html = html.replace(" ", "&nbsp;");// 替换空格
				html = html.replace("\n", "<br />");// 替换空格
				StringBuffer sb = new StringBuffer("<html><body>");
				sb.append(html);
				sb.append("</body></html>");
				return sb.toString();
			}
		} else
			return result;
	}

	private void trace(long elapsedTime, String requestId, String resource,
			String deep) {
		String className = "com.on36.haetae.server.core.RequestHandlerImpl";
		String methodName = "body";
		if (getCustomHandler() != null) {
			className = getCustomHandler().getClass().getName();
			methodName = "handle";
		} else if (object != null && method != null) {
			className = object.getClass().getName();
			methodName = method.getName();
		}

		String info = requestId + "|" + deep + "|" + className + "|"
				+ methodName + "|" + resource + "|" + elapsedTime + "ms";
		scheduler.trace(info);
	}

	public void stats(HttpResponse response, long elapsedTime,
			Context context) {

		if (context != null) {
			String requestId = context.getRequestId();
			String deep = context.getRequestDeep();
			String resource = context.getPath()
					.replace(RouteHelper.PATH_ELEMENT_ROOT, "");
			if (resource.length() > 1)
				trace(elapsedTime, requestId, resource, deep);
			else
				return;
		}
		// total time(ms)
		totalTime.addAndGet(elapsedTime);

		if (HttpResponseStatus.OK.equals(response.getStatus()))
			successHandlTimes.incrementAndGet();
		else
			failHandlTimes.incrementAndGet();
		// total times
		int totalTimes = successHandlTimes.intValue()
				+ failHandlTimes.intValue();

		// elapsed time
		if (minElapsedTime.longValue() == 0
				|| minElapsedTime.longValue() > elapsedTime)
			minElapsedTime.set(elapsedTime);
		if (maxElapsedTime.longValue() == 0
				|| maxElapsedTime.longValue() < elapsedTime)
			maxElapsedTime.set(elapsedTime);

		long avgTime = totalTime.longValue() / totalTimes;
		avgElapsedTime.set(avgTime);
	}

	public int statusCode() {
		return statusCode;
	}

	public Set<SimpleImmutableEntry<String, String>> headers() {
		return new HashSet<SimpleImmutableEntry<String, String>>(headers);
	}

	public HttpHandler<?> getCustomHandler() {
		return httpHandler;
	}

	public boolean hasSession() {
		return hasSession;
	}

	public boolean hasAuth() {
		return auth;
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
				requestFlow.getCurTPS(), requestFlow.getMaxTPS());
	}

}
