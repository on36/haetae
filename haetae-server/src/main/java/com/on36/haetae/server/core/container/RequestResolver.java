package com.on36.haetae.server.core.container;

import static com.on36.haetae.http.route.RouteHelper.PATH_ELEMENT_ROOT;
import static com.on36.haetae.http.route.RouteHelper.PATH_ELEMENT_SEPARATOR;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import io.netty.handler.codec.http.HttpMethod;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.on36.haetae.http.Container;
import com.on36.haetae.http.HandlerKey;
import com.on36.haetae.http.RequestHandler;
import com.on36.haetae.http.request.HttpRequestExt;
import com.on36.haetae.http.route.Route;
import com.on36.haetae.http.route.Router;
import com.on36.haetae.http.route.TreeRouter;
import com.on36.haetae.server.core.RequestHandlerImpl;
import com.on36.haetae.server.core.stats.Statistics;
import com.on36.haetae.server.core.stats.StatisticsHandler;

public class RequestResolver {

	private final Router router = new TreeRouter();

	private final Map<HandlerKey, RequestHandlerImpl> handlerMap = new ConcurrentHashMap<HandlerKey, RequestHandlerImpl>();

	private final HandlerKey rootHandlerKey;
	private final RequestHandlerImpl rootHandler;

	public RequestResolver(Container container) {
		router.add(Route.PATH_ROOT);
		rootHandlerKey = new HandlerKey(HttpMethod.GET.name(), Route.PATH_ROOT);

		rootHandler = new RequestHandlerImpl();
		rootHandler.with(new StatisticsHandler(container));
	}

	public HandlerKey addHandler(RequestHandler handler, HttpMethod method,
			String resource) {
		if (handler == null || method == null) {
			throw new IllegalArgumentException(
					"handler or method cannot be null");
		}
		if (resource == null || "".equals(resource)) {
			throw new IllegalArgumentException("resource cannot be null");
		}

		String path = resource;
		if (!resource.startsWith(PATH_ELEMENT_ROOT))
			path = PATH_ELEMENT_ROOT
					+ (resource.startsWith(PATH_ELEMENT_SEPARATOR) ? resource
							: (PATH_ELEMENT_SEPARATOR + resource));
		Route route = router.route(path);
		if (route != null) {
			throw new IllegalArgumentException("There is already the route["
					+ resource + "], adding operation is not allowed!");
		}
		route = new Route(path);
		HandlerKey key = new HandlerKey(method.name(), route);
		handlerMap.put(key, (RequestHandlerImpl) handler);
		router.add(route);
		return key;
	}

	public void removeHandler(String resource) {
		String path = resource;
		if (!resource.startsWith(PATH_ELEMENT_ROOT))
			path = PATH_ELEMENT_ROOT
					+ (resource.startsWith(PATH_ELEMENT_SEPARATOR) ? resource
							: (PATH_ELEMENT_SEPARATOR + resource));
		router.remove(path);
	}

	public ResolvedRequest resolve(HttpRequestExt request) throws Exception {

		ResolvedRequest resolved = new ResolvedRequest();
		String method = request.getMethod().name();
		String path = new URI(request.getUri()).getPath();
		String contentType = request.headers().get(CONTENT_TYPE);

		Route route = router.route(path);
		if (route == null) {
			resolved.errorStatus = NOT_FOUND;
			return resolved;
		}

		if (Route.PATH_ROOT.equals(route)) {
			resolved.handler = rootHandler;
			resolved.route = Route.PATH_ROOT;
			resolved.key = rootHandlerKey;
		} else {
			HandlerKey key = new HandlerKey(method, route);
			key.setContentType(contentType);

			RequestHandlerImpl handler = handlerMap.get(key);
			if (handler == null) {
				resolved.errorStatus = METHOD_NOT_ALLOWED;
				return resolved;
			}

			resolved.handler = handler;
			resolved.route = route;
			resolved.key = key;
		}
		return resolved;
	}

	public List<Statistics> getStatistics() {
		List<Statistics> stats = new ArrayList<Statistics>();

		for (Map.Entry<HandlerKey, RequestHandlerImpl> entry : handlerMap
				.entrySet()) {
			Statistics stat = entry.getValue().getStatistics();
			stat.setResourcePath(entry.getKey().getRoute().getResourcePath());
			stat.setMethod(entry.getKey().getMethod());
			stats.add(stat);
		}
		return stats;
	}

}
