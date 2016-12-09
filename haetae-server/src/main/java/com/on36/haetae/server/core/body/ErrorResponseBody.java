package com.on36.haetae.server.core.body;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.on36.haetae.api.http.MediaType;

import io.netty.handler.codec.http.HttpResponse;

/**
 * @author zhanghr
 * @date 2016年3月26日
 */
public class ErrorResponseBody extends StringResponseBody {

	public ErrorResponseBody(Throwable e) {
		super(getErrorMessage(e));
	}

	private static String getErrorMessage(Throwable e) {
		return print(e);
	}

	private static String print(Throwable e) {
		StringWriter w = new StringWriter();
		PrintWriter p = new PrintWriter(w);
		try {
			e.printStackTrace(p);
			return w.toString();
		} finally {
			p.close();
		}
	}

	@Override
	protected String build(HttpResponse response, String contentType) {
		if (MediaType.TEXT_HTML.value().equals(contentType))
			return content();

		StringBuilder sb = new StringBuilder("{");
		sb.append("\"status\":").append(response.status().code())
				.append(",");
		sb.append("\"message\":\"").append(response.status().reasonPhrase())
				.append("\"");
		if (hasContent())
			sb.append(",\"result\":\"").append(body).append("\"");
		sb.append("}");
		return sb.toString();
	}
}
