package com.on36.haetae.server.core.container;

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpResponseStatus.SERVICE_UNAVAILABLE;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.Set;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.http.MediaType;
import com.on36.haetae.api.http.Session;
import com.on36.haetae.http.Container;
import com.on36.haetae.http.RequestHandler;
import com.on36.haetae.http.request.HttpRequestExt;
import com.on36.haetae.server.core.RequestHandlerImpl;
import com.on36.haetae.server.core.SimpleContext;
import com.on36.haetae.server.core.body.ResponseBody;
import com.on36.haetae.server.core.body.StringResponseBody;
import com.on36.haetae.server.core.manager.SessionManager;

public class HaetaeContainer implements Container {

	private final RequestResolver requestResolver = new RequestResolver(this);

	private final SessionManager sessionManager = new SessionManager();
	
	public void handle(HttpRequestExt request, HttpResponse response) {

		RequestHandlerImpl handler = null;
		try {
			String responseContentType = MediaType.APPLICATION_JSON.value();
			ResponseBody responseBody = new StringResponseBody("");
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

			/* validatetion information */
			boolean isValid = handler.validation(request, response);
			if (!isValid) {
				response.setStatus(SERVICE_UNAVAILABLE);
				sendAndCommitResponse(response, responseContentType,
						responseBody);
				return;
			}

			/* create a new session if required */
			Session session = sessionManager.getSessionIfExists(request);
			boolean hasSession = handler.hasSession();
			if (hasSession && session == null) {
				session = sessionManager.newSession(response);
			}

			/* create context */
			Context context = new SimpleContext(request, resolved.route,
					session, this);

			/* set the response body */
			ResponseBody handlerBody = handler.body(context);
			if (handlerBody != null && handlerBody.hasContent()) {
				responseBody = handlerBody;
			}

			/* set the response status code */
			int responseStatus = handler.statusCode();
			if (responseStatus > -1)
				handlerStatusCode = HttpResponseStatus.valueOf(resolved.handler
						.statusCode());

			if (handlerStatusCode.code() == -1) {
				throw new RuntimeException(
						"a response status code must be specified");
			}
			response.setStatus(handlerStatusCode);

			/* set the response content type */
			String handlerContentType = resolved.key.contentType();
			if (handlerContentType != null)
				responseContentType = handlerContentType;

			/* set any headers */
			Set<SimpleImmutableEntry<String, String>> headers = handler
					.headers();
			for (SimpleImmutableEntry<String, String> header : headers) {
				response.headers().set(header.getKey(), header.getValue());
			}

			sendAndCommitResponse(response, responseContentType, responseBody);

		} catch (Throwable e) {
			response.setStatus(INTERNAL_SERVER_ERROR);
			sendAndCommitResponse(response, "text/plain",
					new StringResponseBody(e.getMessage()));
		} finally {
			long end = System.currentTimeMillis();
			long elapsedTime = end - request.getStartHandleTime();
			if (handler != null)
				handler.stats(response, elapsedTime);
		}
	}

	private void sendAndCommitResponse(HttpResponse response,
			String responseContentType, ResponseBody responseBody) {
		responseBody.sendAndCommit(response, responseContentType);
	}

	public RequestHandler findHandler(String resource) {
		return requestResolver.findHandler(resource);
	}

	public boolean removeHandler(String resource) {
		return requestResolver.removeHandler(resource);
	}

	public boolean addHandler(RequestHandler handler, HttpMethod method,
			String resource) {
		return requestResolver.addHandler(handler, method, resource);
	}

	public boolean addHandler(RequestHandler handler, String resource) {
		return addHandler(handler, HttpMethod.GET, resource);
	}

	@Override
	public List<?> getStatistics(String contentType) {
		return requestResolver.getStatistics();
	}
}
