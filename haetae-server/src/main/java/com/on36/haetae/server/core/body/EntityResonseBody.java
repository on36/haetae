package com.on36.haetae.server.core.body;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

import java.io.IOException;

import com.on36.haetae.server.utils.FormatorUtils;

public class EntityResonseBody extends ResponseBody {

	private final Object entity;

	public EntityResonseBody(Object entity) {
		this.entity = entity;
	}

	@Override
	public void send(HttpResponse response, String contentType) {
		// TODO Auto-generated method stub
		try {
			printBody(response, contentType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void sendAndCommit(HttpResponse response, String contentType) {
		// TODO Auto-generated method stub
		try {
			printBody(response, contentType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean hasContent() {
		// TODO Auto-generated method stub
		return entity != null;
	}

	private void printBody(HttpResponse response, String contentType)
			throws IOException {

		addStandardHeaders(response, contentType);

		if (response instanceof HttpContent) {
			HttpContent httpContent = (HttpContent) response;
			ByteBuf content = httpContent.content();
			if (hasContent()) {
				if (entity instanceof String) {
                    String body = (String) entity; 
					content.writeBytes(body.getBytes(
							CharsetUtil.UTF_8));
				} else
					content.writeBytes(FormatorUtils.toJson(entity).getBytes(
							CharsetUtil.UTF_8));
			}
		}
	}
}
