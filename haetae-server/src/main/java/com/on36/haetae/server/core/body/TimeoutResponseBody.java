package com.on36.haetae.server.core.body;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class TimeoutResponseBody extends StringResponseBody {

	public TimeoutResponseBody() {
		super("Request Timeout");
	}
	
	@Override
	public boolean hasContent() {
		return true;
	}

	@Override
	public void sendAndCommit(HttpResponse response, String contentType) {
		response.setStatus(HttpResponseStatus.REQUEST_TIMEOUT);
		super.sendAndCommit(response, contentType);
	}

}
