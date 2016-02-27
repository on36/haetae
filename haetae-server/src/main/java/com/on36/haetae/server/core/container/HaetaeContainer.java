package com.on36.haetae.server.core.container;

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Set;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.http.Session;
import com.on36.haetae.http.Container;
import com.on36.haetae.http.HandlerKey;
import com.on36.haetae.http.RequestHandler;
import com.on36.haetae.http.request.HttpRequestExt;
import com.on36.haetae.server.core.body.ResponseBody;
import com.on36.haetae.server.core.body.StringResponseBody;
import com.on36.haetae.server.request.impl.SimpleContext;

public class HaetaeContainer implements Container {

	private final RequestResolver requestResolver = new RequestResolver();

	private final SessionManager sessionManager = new SessionManager();

	public void handle(HttpRequestExt request, HttpResponse response) {
		try {
			String responseContentType = "text/plain";
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
			/* validatetion information */
			boolean isValid = resolved.handler.validation(request, response);
			if (!isValid) {
				response.setStatus(NOT_FOUND);
				sendAndCommitResponse(response, responseContentType,
						responseBody);
				return;
			}

			/* create a new session if required */
			Session session = sessionManager.getSessionIfExists(request);
			boolean hasSession = resolved.handler.hasSession();
			if (hasSession && session == null) {
				session = sessionManager.newSession(response);
			}

			/* create context */
			Context context = new SimpleContext(request, resolved.route,
					session);

			/* set the response body */
			ResponseBody handlerBody = resolved.handler.body(context);
			if (handlerBody != null && handlerBody.hasContent()) {
				responseBody = handlerBody;
			}

			int responseStatus = resolved.handler.statusCode();
			if (responseStatus > -1)
				handlerStatusCode = HttpResponseStatus.valueOf(resolved.handler
						.statusCode());

			/* set the content type */
			String handlerContentType = resolved.key.contentType();
			if (handlerContentType != null
					&& handlerContentType.trim().length() != 0) {
				responseContentType = handlerContentType;
			}
			/* set the response status code */
			if (handlerStatusCode.code() == -1) {
				throw new RuntimeException(
						"a response status code must be specified");
			}
			response.setStatus(handlerStatusCode);

			/* set any headers */
			Set<SimpleImmutableEntry<String, String>> headers = resolved.handler
					.headers();
			for (SimpleImmutableEntry<String, String> header : headers) {
				response.headers().set(header.getKey(), header.getValue());
			}

			sendAndCommitResponse(response, responseContentType, responseBody);

		} catch (Throwable e) {
			response.setStatus(INTERNAL_SERVER_ERROR);
			sendAndCommitResponse(response, "text/plain",
					new StringResponseBody(e.getMessage()));
		}
	}

	private void sendAndCommitResponse(HttpResponse response,
			String responseContentType, ResponseBody responseBody) {
		responseBody.sendAndCommit(response, responseContentType);
	}

	public HandlerKey addHandler(RequestHandler handler, HttpMethod method,
			String resource, String contentType) {
		return requestResolver.addHandler(handler, method, resource,
				contentType);
	}

	public HandlerKey addHandler(RequestHandler handler, HttpMethod method,
			String resource) {
		return addHandler(handler, method, resource, null);
	}
}
