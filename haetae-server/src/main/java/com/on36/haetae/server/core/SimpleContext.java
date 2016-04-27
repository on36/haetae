package com.on36.haetae.server.core;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.http.Session;
import com.on36.haetae.http.Container;
import com.on36.haetae.http.request.HttpRequestExt;
import com.on36.haetae.http.route.Route;
import com.on36.haetae.server.core.interpolation.ResponseBodyInterpolator;
import com.on36.haetae.server.utils.Deep;
import com.on36.haetae.server.utils.FormatorUtils;
import com.on36.haetae.server.utils.ShortUUID;

public class SimpleContext implements Context {

	private static final String HEADER_REQUEST_ID = "Haetae-Request-Id";
	private static final String HEADER_REQUEST_DEEP = "Haetae-Request-Deep";

	private final HttpRequestExt request;

	private final Route route;

	private final Session session;

	private final Container container;

	private String requestId;

	private Deep deep;

	private Map<String, String> parmMap = new HashMap<String, String>();

	private String path;

	public SimpleContext(HttpRequestExt request, Route route, Session session,
			Container container) {

		this.request = request;
		this.route = route;
		this.session = session;
		this.container = container;
		this.requestId = getHeaderValue(HEADER_REQUEST_ID);
		try {
			if (this.requestId == null) {
				this.requestId = new ShortUUID.Builder().build().toString();
				this.deep = new Deep();
			} else {
				this.deep = new Deep(getHeaderValue(HEADER_REQUEST_DEEP));
			}
			path = new URI(this.request.getUri()).getPath();
			parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getRequestId() {
		return requestId;
	}

	public String getRequestDeep() {
		return deep.getDeep();
	}

	public String nextDeep() {
		return deep.next();
	}

	public long getStartHandleTime() {
		return request.getStartHandleTime();
	}

	public String getPath() {

		return this.path;
	}

	public Route getRoute() {

		return route;
	}

	private Map<String, String> parse() throws Exception {
		HttpMethod method = request.getMethod();

		QueryStringDecoder queryDecoder = new QueryStringDecoder(
				request.getUri());
		Set<Entry<String, List<String>>> sets = queryDecoder.parameters()
				.entrySet();
		for (Entry<String, List<String>> entry : sets) {
			parmMap.put(entry.getKey(), entry.getValue().get(0));
		}

		if (HttpMethod.POST == method) {
			HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(
					request.getRequest());
			decoder.offer(request.copy());

			List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();

			for (InterfaceHttpData parm : parmList) {
				Attribute data = (Attribute) parm;
				parmMap.put(data.getName(), data.getValue());
			}
		}

		return parmMap;
	}

	public String getCapturedParameter(String captured) {

		return ResponseBodyInterpolator.interpolate(captured, this);
	}

	public String getRequestParameter(String param) {
		return parmMap.get(param);
	}

	public Set<String> getRequestParameterNames() {
		return parmMap.keySet();
	}

	public Session getSession() {
		return session;
	}

	public String getHeaderValue(String param) {
		return request.headers().get(param);
	}

	public String getBodyAsString() {

		ByteBuf content = request.content();
		if (content.isReadable()) {
			return content.toString(CharsetUtil.UTF_8);
		}
		return null;
	}

	@Override
	public <T> T getBodyAsEntity(Class<T> clazz) {

		return FormatorUtils.fromJson(clazz, getBodyAsString());
	}

	@Override
	public String getURI(String resource) throws Exception {

		RequestHandlerImpl requestHandler = (RequestHandlerImpl) container
				.findHandler(resource);
		if (requestHandler != null) {
			return requestHandler.body(this, resource);
		} else {
			//TODO  增加外部访问HTTP
		}
		return null;
	}

	@Override
	public <T> T getURI(String resource, Class<T> clazz) throws Exception {

		String result = getURI(resource);
		if (result != null)
			return FormatorUtils.fromJson(clazz, result);
		return null;
	}
}
