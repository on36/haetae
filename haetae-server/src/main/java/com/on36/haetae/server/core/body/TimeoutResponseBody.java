package com.on36.haetae.server.core.body;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class TimeoutResponseBody extends StringResponseBody {

	public TimeoutResponseBody() {
		super(null);
	}

	@Override
	public void sendAndCommit(HttpResponse response, String contentType) {
		response.setStatus(HttpResponseStatus.REQUEST_TIMEOUT);
		super.sendAndCommit(response, contentType);
	}

	protected String build(HttpResponse response, String contentType) {
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"status\":").append(response.getStatus().code())
				.append(",");
		sb.append("\"message\":\"").append(response.getStatus().reasonPhrase())
				.append("\"");
		sb.append("}");
		return sb.toString();
	}

}
