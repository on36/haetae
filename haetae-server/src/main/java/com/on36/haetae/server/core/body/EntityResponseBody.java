package com.on36.haetae.server.core.body;

import com.on36.haetae.api.http.MediaType;
import com.on36.haetae.config.client.json.util.JSONUtils;

import io.netty.handler.codec.http.HttpResponse;

public class EntityResponseBody extends StringResponseBody {

	public EntityResponseBody(Object entity) {
		super(entity);
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
			translate(sb, body);
		sb.append("}");
		return sb.toString();
	}

	private boolean isJSON(String data) {
		return data.startsWith("{") && data.endsWith("}");
	}

	private void translate(StringBuilder sb, Object entity) {
		sb.append(",").append("\"result\":");
		String data = null;
		if (entity.getClass().isPrimitive())
			sb.append(entity.toString());
		else if (entity instanceof String) {
			data = (String) entity;
			if (isJSON(data))
				sb.append(data);
			else
				sb.append("\"").append(data).append("\"");
		} else {
			data = JSONUtils.toJson(entity);
			sb.append(entity.toString());
		}
	}
}
