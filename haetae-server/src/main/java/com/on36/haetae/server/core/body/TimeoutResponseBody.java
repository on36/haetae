package com.on36.haetae.server.core.body;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class TimeoutResponseBody extends StringResponseBody {

	public TimeoutResponseBody() {
		super("request time out");
	}
	
	@Override
	public boolean hasContent() {
		return true;
	}

	@Override
	public void sendAndCommit(HttpResponse response, String contentType) {
		response.setStatus(HttpResponseStatus.BAD_REQUEST);
		super.sendAndCommit(response, contentType);
	}

}
