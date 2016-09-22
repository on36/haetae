package com.on36.haetae.server.core;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.JSONObject;
import com.on36.haetae.api.http.MediaType;
import com.on36.haetae.api.http.MethodType;
import com.on36.haetae.api.http.Session;
import com.on36.haetae.common.log.LogLevel;
import com.on36.haetae.common.utils.ShortUUID;
import com.on36.haetae.config.client.HttpClient;
import com.on36.haetae.config.client.json.JSONObjectImpl;
import com.on36.haetae.config.client.json.util.JSONUtils;
import com.on36.haetae.http.Container;
import com.on36.haetae.http.request.HttpRequestExt;
import com.on36.haetae.http.route.Route;
import com.on36.haetae.server.HaetaeServer;
import com.on36.haetae.server.core.interpolation.ResponseBodyInterpolator;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;

public class SimpleContext implements Context {

	private static final String HEADER_TRACE_ID = "Haetae-Trace-Id";
	private static final String HEADER_PARENT_ID = "Haetae-Parent-ID";

	private final HttpRequestExt request;

	private final Route route;

	private final Session session;

	private final Container container;

	private String traceId;
	private String parentId;
	private String spanId;
	private int deep = 0;
	private boolean deepAviable = false;

	private Map<String, String> parmMap = new HashMap<String, String>();
	private Map<String, String> extraParamMap = null;
	private String extraBody = null;
	private String bodyString = null;

	private String path;
	private JSONObject jsonObject;

	public SimpleContext(HttpRequestExt request, Route route, Session session,
			Container container) {

		this.request = request;
		this.route = route;
		this.session = session;
		this.container = container;
		this.traceId = getHeaderValue(HEADER_TRACE_ID);
		this.parentId = getHeaderValue(HEADER_PARENT_ID);
		try {
			if (this.traceId == null) {
				this.traceId = ShortUUID.randomUUID();
				this.spanId = this.parentId = ShortUUID.randomUUID();
			} else {
				this.spanId = ShortUUID.randomUUID();
			}
			path = new URI(this.request.getUri()).getPath();
			parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getClientIP() {
		return request.getRemoteAddress();
	}

	public int getClientPort() {
		return request.getRemotePort();
	}

	public String getClientAddress() {
		return request.getRemoteAddress() + ":" + request.getRemotePort();
	}

	public String getTraceId() {
		return traceId;
	}

	public String getParentId() {
		return parentId;
	}

	public String getSpanId() {
		if (deepAviable)
			return spanId + "_" + deep;
		return spanId;
	}

	void setDeepAviable(boolean value) {
		this.deepAviable = value;
		if (deepAviable)
			deep++;
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
		if (deepAviable)
			return this.extraParamMap.get(param);
		return parmMap.get(param);
	}

	public Set<String> getRequestParameterNames() {
		if (deepAviable)
			return this.extraParamMap.keySet();
		return parmMap.keySet();
	}

	public Map<String, String> getRequestParameters() {
		if (deepAviable)
			return this.extraParamMap;
		return parmMap;
	}

	public Session getSession() {
		return session;
	}

	public String getHeaderValue(String param) {
		return request.headers().get(param);
	}

	public long getContentLength() {
		return request.getContentLength();
	}

	public String getContenType() {
		String contentType = request.headers().get("Content-Type");
		if (contentType == null)
			contentType = MediaType.TEXT_JSON.value();
		return contentType;
	}

	public String getBodyAsString() {
		if (deepAviable)
			return this.extraBody;

		ByteBuf content = request.content();
		if (bodyString == null && content.isReadable()) {
			bodyString = content.toString(CharsetUtil.UTF_8);
		}
		return bodyString;
	}

	@Override
	public JSONObject getBodyAsJSONObject() {
		if (jsonObject == null)
			jsonObject = new JSONObjectImpl(getBodyAsString());
		return jsonObject;
	}

	@Override
	public <T> T getBodyAsEntity(Class<T> clazz) {

		return JSONUtils.fromJson(clazz, getBodyAsString());
	}

	@Override
	public String request(String resource) throws Exception {

		return request(resource, MethodType.GET);
	}

	@Override
	public String request(String resource, MethodType method) throws Exception {

		return request(resource, method, getRequestParameters());
	}

	@Override
	public String request(String resource, MethodType method,
			Map<String, String> queryParam) throws Exception {
		this.extraParamMap = queryParam;
		RequestHandlerImpl requestHandler = (RequestHandlerImpl) container
				.findHandler(resource, method.name(), null);
		if (requestHandler != null) {
			return requestHandler.body(this, resource);
		} else {
			return HttpClient.getInstance().send(resource, method, queryParam,
					null, null);
		}
	}

	@Override
	public <T> T request(String resource, MethodType method, Class<T> clazz)
			throws Exception {
		String result = request(resource, method);
		if (result != null)
			return JSONUtils.fromJson(clazz, result);
		return null;
	}

	@Override
	public void trace(String level, String message, Throwable t) {
		StackTraceElement[] lvStacks = Thread.currentThread().getStackTrace();
		String className = lvStacks[2].getClassName();
		HaetaeServer.getScheduler().trace(className,
				LogLevel.valueOf(level.toUpperCase()), message, t);

	}

	@Override
	public void trace(String level, String message) {
		StackTraceElement[] lvStacks = Thread.currentThread().getStackTrace();
		String className = lvStacks[2].getClassName();

		int len = lvStacks.length;
		int index = 2;
		StringBuilder sb = new StringBuilder();
		while (index < len)
			sb.append("\n\tat " + lvStacks[index++].toString());
		HaetaeServer.getScheduler().trace(className,
				LogLevel.valueOf(level.toUpperCase()), message + sb.toString(),
				null);
	}
}
