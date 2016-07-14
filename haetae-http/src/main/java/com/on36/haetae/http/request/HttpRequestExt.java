package com.on36.haetae.http.request;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

public class HttpRequestExt {
	
	private final HttpRequest request;
	
	private final long startHandleTime;
	
	private final String remoteAddress;
	
	public HttpRequestExt(HttpRequest request, String remoteAddress, long startHandleTime) {
		this.request = request;
		this.remoteAddress = remoteAddress;
		this.startHandleTime = startHandleTime;
	}
	
	public HttpRequest getRequest() {
		return request;
	}
	
	public long getStartHandleTime() {
		return startHandleTime;
	}

	public HttpVersion getProtocolVersion() {
		return request.getProtocolVersion();
	}

	public HttpHeaders headers() {
		return request.headers();
	}

	public DecoderResult getDecoderResult() {
		return request.getDecoderResult();
	}

	public void setDecoderResult(DecoderResult result) {
		request.setDecoderResult(result);
	}

	public ByteBuf content() {
		return ((HttpContent) request).content();
	}

	public int refCnt() {
		return ((HttpContent) request).refCnt();
	}

	public boolean release() {
		return ((HttpContent) request).release();
	}

	public boolean release(int decrement) {
		return ((HttpContent) request).release(decrement);
	}

	public HttpContent copy() {
		return ((HttpContent) request).copy();
	}

	public HttpContent duplicate() {
		return ((HttpContent) request).duplicate();
	}

	public HttpContent retain() {
		return ((HttpContent) request).retain();
	}

	public HttpContent retain(int increment) {
		return ((HttpContent) request).retain(increment);
	}

	public HttpMethod getMethod() {
		return request.getMethod();
	}

	public HttpRequest setMethod(HttpMethod method) {
		return request.setMethod(method);
	}

	public String getUri() {
		return request.getUri();
	}

	public HttpRequest setUri(String uri) {
		return request.setUri(uri);
	}

	public HttpRequest setProtocolVersion(HttpVersion version) {
		return request.setProtocolVersion(version);
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

}
