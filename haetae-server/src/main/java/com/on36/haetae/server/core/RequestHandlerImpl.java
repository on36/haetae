package com.on36.haetae.server.core;

import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.core.HttpHandler;
import com.on36.haetae.api.manager.ContextManager;
import com.on36.haetae.common.log.LogLevel;
import com.on36.haetae.common.utils.DateUtils;
import com.on36.haetae.http.RequestHandler;
import com.on36.haetae.http.ServiceLevel;
import com.on36.haetae.http.Version;
import com.on36.haetae.net.udp.Scheduler;
import com.on36.haetae.server.HaetaeServer;
import com.on36.haetae.server.Heartbeat;
import com.on36.haetae.server.core.auth.IAuthentication;
import com.on36.haetae.server.core.auth.impl.BlackListAuthentication;
import com.on36.haetae.server.core.auth.impl.RequestFlowAuthentication;
import com.on36.haetae.server.core.auth.impl.SignatureAuthentication;
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
	private SignatureAuthentication signature = new SignatureAuthentication();
	private List<IAuthentication> authList = null;
	private boolean auth = true;
	private boolean verify = true;

	private final ExecutorService es = HaetaeServer.getThreadPoolExecutor();
	private final Scheduler scheduler = HaetaeServer.getScheduler();

	public RequestHandlerImpl() {
		this.authList = new ArrayList<IAuthentication>();
		this.authList.add(signature);
		this.authList.add(blackList);
		this.authList.add(whiteList);
		this.authList.add(requestFlow);
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

	public RequestHandler verify(boolean verify) {

		this.verify = verify;
		this.signature.setVerify(verify);
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
			if (method != null || getCustomHandler() != null) {
				future = es.submit(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						try {
							ContextManager.set(context);
							if (getCustomHandler() != null)
								return getCustomHandler().handle(context);
							else
								return method.invoke(object, context);
						} finally {
							ContextManager.destroy();
						}
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
		} catch (ExecutionException ee) {
			Throwable cause = ee.getCause();
			if (cause instanceof InvocationTargetException) {
				InvocationTargetException target = (InvocationTargetException) cause;
				throw (Exception) target.getTargetException();
			}
		} finally {

		}

		return new InterpolatedResponseBody(body, context);
	}

	public String body(Context context, String resource) throws Exception {
		long start = System.currentTimeMillis();
		((SimpleContext) context).setDeepAviable(true);
		String traceId = context.getTraceId();
		String parenId = context.getParentId();
		String spanId = context.getSpanId();
		try {
			return body(context).content();
		} finally {
			long elapsedTime = System.currentTimeMillis() - start;
			trace(context.getClientAddress(), start, elapsedTime, -1, -1,
					resource, context.getRequestParameters(), traceId, parenId,
					spanId);
			((SimpleContext) context).setDeepAviable(false);
		}
	}

	private void trace(String client, long startTime, long elapsedTime,
			int status, long length, String resource,
			Map<String, String> queryParam, String traceId, String parenId,
			String spanId) {

		Class<?> clazz = this.getClass();
		String methodName = "body";
		if (getCustomHandler() != null) {
			clazz = getCustomHandler().getClass();
			methodName = "handle";
		} else if (object != null && method != null) {
			clazz = object.getClass();
			methodName = method.getName();
		}
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(DateUtils.toString(DateUtils.toDate(startTime)));
		} catch (Exception e) {
			e.printStackTrace();
			sb.append(startTime);
		}
		sb.append(" [HAETAE] ");
		sb.append(client);
		sb.append(" -> ");
		sb.append(Heartbeat.myself());
		sb.append(" ");
		sb.append(traceId);
		sb.append(" ");
		sb.append(parenId);
		sb.append(" ");
		sb.append(spanId);
		sb.append(" ");
		sb.append(resource);
		sb.append(" ");
		if (status > 0)
			sb.append(status);
		else
			sb.append("-");
		sb.append(" ");
		if (length > 0)
			sb.append(length);
		else
			sb.append("-");
		sb.append(" ");
		sb.append(clazz.getName());
		sb.append(".");
		sb.append(methodName);
		sb.append("[");
		sb.append(queryParam);
		sb.append("] ");
		sb.append(elapsedTime);
		sb.append("ms");
		sb.append(" at Haetae server version : ");
		sb.append(Version.CURRENT_VERSION);
		scheduler.trace(RequestHandlerImpl.class, LogLevel.INFO, sb.toString());
	}

	public void stats(HttpResponse response, long elapsedTime,
			Context context) {

		if (context != null) {
			String traceId = context.getTraceId();
			String parenId = context.getParentId();
			String spanId = context.getSpanId();

			String resource = context.getPath();
			if (resource.length() > 1)
				trace(context.getClientAddress(), context.getStartHandleTime(),
						elapsedTime, response.getStatus().code(),
						context.getContentLength(), resource,
						context.getRequestParameters(), traceId, parenId,
						spanId);
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

	public boolean hasVerify() {
		return verify;
	}

	public HttpResponseStatus validation(Context context,
			HttpResponse response) {

		if (auth) {
			for (IAuthentication authentication : authList) {
				HttpResponseStatus result = authentication.auth(context,
						response);
				if (result != null)
					return result;
			}
		}
		return null;
	}

	public Statistics getStatistics() {
		return new Statistics(successHandlTimes.intValue(),
				failHandlTimes.intValue(), minElapsedTime.longValue(),
				avgElapsedTime.longValue(), maxElapsedTime.longValue(),
				requestFlow.getCurTPS(), requestFlow.getMaxTPS());
	}

}
