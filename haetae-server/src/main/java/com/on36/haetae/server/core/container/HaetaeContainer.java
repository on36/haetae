package com.on36.haetae.server.core.container;

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.Set;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.http.MediaType;
import com.on36.haetae.api.http.Session;
import com.on36.haetae.common.log.LogLevel;
import com.on36.haetae.common.log.Logger;
import com.on36.haetae.common.log.LoggerFactory;
import com.on36.haetae.http.Container;
import com.on36.haetae.http.RequestHandler;
import com.on36.haetae.http.request.HttpRequestExt;
import com.on36.haetae.net.udp.Scheduler;
import com.on36.haetae.server.core.RequestHandlerImpl;
import com.on36.haetae.server.core.SimpleContext;
import com.on36.haetae.server.core.body.EntityResponseBody;
import com.on36.haetae.server.core.body.ErrorResponseBody;
import com.on36.haetae.server.core.body.ResponseBody;
import com.on36.haetae.server.core.manager.SessionManager;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class HaetaeContainer implements Container {

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private final RequestResolver requestResolver = new RequestResolver(this);

	private final SessionManager sessionManager = new SessionManager();

	private Scheduler scheduler;

	public HaetaeContainer(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void handle(HttpRequestExt request, HttpResponse response) {

		RequestHandlerImpl handler = null;
		Context context = null;
		long start = System.currentTimeMillis();
		try {
			String responseContentType = MediaType.TEXT_JSON.value();
			ResponseBody responseBody = new EntityResponseBody("");
			HttpResponseStatus handlerStatusCode = OK;

			/* validatetion route */
			ResolvedRequest resolved = requestResolver.resolve(request);
			if (resolved.errorStatus != null) {
				response.setStatus(resolved.errorStatus);
				sendAndCommitResponse(response, responseContentType,
						responseBody);
				return;
			}
			handler = resolved.handler;

			/* create a new session if required */
			boolean hasSession = handler.hasSession();
			Session session = null;
			if (hasSession) {
				session = sessionManager.getSessionIfExists(request);
				if (session == null)
					session = sessionManager.newSession(response);
			}
			/* create context */
			context = new SimpleContext(request, resolved.route, session, this);

			/* validatetion handler information */
			HttpResponseStatus validStatus = handler.validation(context,
					response);
			if (validStatus != null) {
				response.setStatus(validStatus);
				sendAndCommitResponse(response, responseContentType,
						responseBody);
				return;
			}

			/* set the response body */
			ResponseBody handlerBody = handler.body(context);
			if (handlerBody != null) {
				responseBody = handlerBody;
			}

			/* set the response status code */
			int responseStatus = handler.statusCode();
			if (responseStatus > -1)
				handlerStatusCode = HttpResponseStatus
						.valueOf(resolved.handler.statusCode());

			if (handlerStatusCode.code() == -1) {
				throw new RuntimeException(
						"a response status code must be specified");
			}
			response.setStatus(handlerStatusCode);

			/* set the response content type */
			String handlerContentType = resolved.key.contentType();
			if (handlerContentType != null)
				responseContentType = handlerContentType;
			if (resolved.contentType != null)
				responseContentType = resolved.contentType;

			/* set any headers */
			Set<SimpleImmutableEntry<String, String>> headers = handler
					.headers();
			for (SimpleImmutableEntry<String, String> header : headers) {
				response.headers().set(header.getKey(), header.getValue());
			}

			sendAndCommitResponse(response, responseContentType, responseBody);

		} catch (Throwable e) {
			scheduler.trace(this.getClass(), LogLevel.ERROR,
					context.getPath() + "-" + context.getRequestParameters(),
					e);
			response.setStatus(INTERNAL_SERVER_ERROR);
			sendAndCommitResponse(response, MediaType.TEXT_JSON.value(),
					new ErrorResponseBody(e));
		} finally {
			long end = System.currentTimeMillis();
			long elapsedTime = end - start;
			if (handler != null)
				handler.stats(response, elapsedTime, context);
		}
	}

	private void sendAndCommitResponse(HttpResponse response,
			String responseContentType, ResponseBody responseBody) {
		responseBody.sendAndCommit(response, responseContentType);
	}

	public RequestHandler findHandler(String resource, String methodName,
			String version) {
		return requestResolver.findHandler(resource, methodName, version);
	}

	public boolean removeHandler(String resource, String methodName,
			String version) {
		return requestResolver.removeHandler(resource, methodName, version);
	}

	public boolean addHandler(RequestHandler handler, String methodName,
			String resource, String version, String contentType) {
		return requestResolver.addHandler(handler, methodName, resource, version,
				contentType);
	}

	@Override
	public List<?> getStatistics() {
		return requestResolver.getStatistics();
	}
}
