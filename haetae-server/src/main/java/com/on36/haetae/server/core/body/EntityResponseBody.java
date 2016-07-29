package com.on36.haetae.server.core.body;

import com.on36.haetae.common.utils.JSONUtils;

public class EntityResponseBody extends StringResponseBody {

	public EntityResponseBody(Object entity) {
		super(translate(entity));
	}

	private static String translate(Object entity) {

		String body = null;
		if (entity == null)
			body = "nothing";
		else if (entity.getClass().isPrimitive())
			body = entity.toString();
		else if (entity instanceof String)
			body = (String) entity;
		else
			body = JSONUtils.toJson(entity);
		return body;
	}
}
