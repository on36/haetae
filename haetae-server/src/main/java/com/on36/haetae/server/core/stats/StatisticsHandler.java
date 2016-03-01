package com.on36.haetae.server.core.stats;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

import java.util.List;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.core.CustomHandler;
import com.on36.haetae.http.Container;

public class StatisticsHandler implements CustomHandler<List<?>> {

	private final Container container;

	public StatisticsHandler(Container container) {
		this.container = container;
	}

	@Override
	public List<?> handle(Context context) {
		String contentType = context.getHeaderValue(CONTENT_TYPE);
		return container.getStatistics(contentType);
	}

}
