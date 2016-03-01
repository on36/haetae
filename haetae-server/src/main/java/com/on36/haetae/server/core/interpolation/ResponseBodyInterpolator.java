package com.on36.haetae.server.core.interpolation;

import com.on36.haetae.api.Context;
import com.on36.haetae.interpolatd.Interpolator;
import com.on36.haetae.interpolatd.Substitutor;
import com.on36.haetae.server.core.SimpleContext;

public class ResponseBodyInterpolator {

	private static final Interpolator<Context> interpolator = new Interpolator<Context>();

	static {

		interpolator.when("[a-zA-Z0-9_]+").prefixedBy(":")
				.handleWith(new Substitutor<Context>() {
					public String substitute(String captured, Context req) {

						String path = req.getPath();
						return ((SimpleContext) req).getRoute().getNamedParameter(captured, path);
					}
				});

		interpolator.when("[0-9]+").enclosedBy("*[").and("]")
				.handleWith(new Substitutor<Context>() {
					public String substitute(String captured, Context req) {

						String path = req.getPath();
						int index = Integer.parseInt(captured);
						return ((SimpleContext) req).getRoute().getSplatParameter(index, path);
					}
				});

		interpolator.when().enclosedBy("{").and("}")
				.handleWith(new Substitutor<Context>() {
					public String substitute(String captured, Context req) {

						if (req.getSession() != null) {
							Object val = req.getSession().get(captured);
							if (val != null) {
								return val.toString();
							}
						}
						return null;
					}
				});
		interpolator.when().enclosedBy("[").and("]")
				.handleWith(new Substitutor<Context>() {
					public String substitute(String captured, Context req) {

						if (captured.startsWith("request?")) {
							return req.getRequestParameter(captured.replaceFirst("request\\?", ""));
						}

						if (captured.startsWith("request$")) {
							return req.getHeaderValue(captured.replaceFirst("request\\$", ""));
						}

						return null;
					}
				});

		interpolator.escapeWith("^");
	}

	public static String interpolate(String body, Context request) {

		return interpolator.interpolate(body, request);
	}
}
