package com.on36.haetae.http;

import com.on36.haetae.http.route.Route;

public class HandlerKey {

	private final String method;
	private final Route route;
	private final String version;
	private String contentType;

	public HandlerKey(String method, Route route) {
		this(method, route, null, "1.0");
	}

	public HandlerKey(String method, Route route, String contentType) {
		this(method, route, contentType, "1.0");
	}

	public HandlerKey(String method, Route route, String contentType,
			String version) {
		super();
		this.method = method;
		this.route = route;
		this.contentType = contentType;
		this.version = version;
	}

	public String contentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getMethod() {
		return method;
	}

	public Route getRoute() {
		return route;
	}

	public String getVersion() {
		return version;
	}

	@Override
	public int hashCode() {

		int hash = 1;
		hash = hash * 17 + method.hashCode();
		hash = hash * 31 + route.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object o) {

		if (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof HandlerKey))
			return false;
		HandlerKey that = (HandlerKey) o;
		return this.method.equals(that.method) && this.route.equals(that.route);
	}
}
