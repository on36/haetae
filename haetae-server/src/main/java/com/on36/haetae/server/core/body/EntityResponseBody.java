package com.on36.haetae.server.core.body;

import com.on36.haetae.api.Context;
import com.on36.haetae.server.utils.FormatorUtils;

public class EntityResponseBody extends StringResponseBody {

	public EntityResponseBody(Object entity, Context context) {
		super(translate(entity, context));
	}

	private static String translate(Object entity, Context context) {
		String body = null;
		if (entity instanceof String) {
			body = (String) entity;
		} else {
//			String contentType = context.getHeaderValue("Content-Type");
//			if (MediaType.APPLICATION_XML.value().equals(contentType)
//					|| MediaType.TEXT_XML.value().equals(contentType))
//				FormatorUtils.toXML(entity);
//			else
				body = FormatorUtils.toJson(entity);
		}
		return body;
	}
}
