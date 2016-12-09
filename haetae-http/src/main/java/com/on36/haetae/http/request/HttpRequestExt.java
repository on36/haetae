package com.on36.haetae.http.request;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;

public class HttpRequestExt {

	private final HttpRequest request;

	private final long startHandleTime;

	private final String remoteAddress;

	private final int remotePort;

	public HttpRequestExt(HttpRequest request, String remoteAddress,
			int remotePort, long startHandleTime) {
		this.request = request;
		this.remotePort = remotePort;
		this.remoteAddress = remoteAddress;
		this.startHandleTime = startHandleTime;
	}

	public HttpRequest getRequest() {
		return request;
	}

	public long getContentLength() {
		return HttpUtil.getContentLength(request);
	}

	public long getStartHandleTime() {
		return startHandleTime;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public HttpVersion getProtocolVersion() {
		return request.protocolVersion();
	}

	public HttpHeaders headers() {
		return request.headers();
	}

	public DecoderResult getDecoderResult() {
		return request.decoderResult();
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
		return request.method();
	}

	public String getUri() {
		return request.uri();
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

}
