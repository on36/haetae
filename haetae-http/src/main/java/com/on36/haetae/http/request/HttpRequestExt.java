package com.on36.haetae.http.request;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

import java.net.InetSocketAddress;

public class HttpRequestExt implements HttpRequest,HttpContent{
	
	private final HttpRequest request;
	
	private final InetSocketAddress remoteAddress;
	
	public HttpRequestExt(HttpRequest request, InetSocketAddress remoteAddress) {
		this.request = request;
		this.remoteAddress = remoteAddress;
	}

	public HttpVersion getProtocolVersion() {
		// TODO Auto-generated method stub
		return request.getProtocolVersion();
	}

	public HttpHeaders headers() {
		// TODO Auto-generated method stub
		return request.headers();
	}

	public DecoderResult getDecoderResult() {
		// TODO Auto-generated method stub
		return request.getDecoderResult();
	}

	public void setDecoderResult(DecoderResult result) {
		// TODO Auto-generated method stub
		request.setDecoderResult(result);
	}

	public ByteBuf content() {
		// TODO Auto-generated method stub
		return ((HttpContent) request).content();
	}

	public int refCnt() {
		// TODO Auto-generated method stub
		return ((HttpContent) request).refCnt();
	}

	public boolean release() {
		// TODO Auto-generated method stub
		return ((HttpContent) request).release();
	}

	public boolean release(int decrement) {
		// TODO Auto-generated method stub
		return ((HttpContent) request).release(decrement);
	}

	public HttpContent copy() {
		// TODO Auto-generated method stub
		return ((HttpContent) request).copy();
	}

	public HttpContent duplicate() {
		// TODO Auto-generated method stub
		return ((HttpContent) request).duplicate();
	}

	public HttpContent retain() {
		// TODO Auto-generated method stub
		return ((HttpContent) request).retain();
	}

	public HttpContent retain(int increment) {
		// TODO Auto-generated method stub
		return ((HttpContent) request).retain(increment);
	}

	public HttpMethod getMethod() {
		// TODO Auto-generated method stub
		return request.getMethod();
	}

	public HttpRequest setMethod(HttpMethod method) {
		// TODO Auto-generated method stub
		return request.setMethod(method);
	}

	public String getUri() {
		// TODO Auto-generated method stub
		return request.getUri();
	}

	public HttpRequest setUri(String uri) {
		// TODO Auto-generated method stub
		return request.setUri(uri);
	}

	public HttpRequest setProtocolVersion(HttpVersion version) {
		// TODO Auto-generated method stub
		return request.setProtocolVersion(version);
	}

	public InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}
	
}
