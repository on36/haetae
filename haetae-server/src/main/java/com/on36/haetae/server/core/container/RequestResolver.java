package com.on36.haetae.server.core.container;

import static com.on36.haetae.http.route.RouteHelper.PATH_ELEMENT_ROOT;
import static com.on36.haetae.http.route.RouteHelper.PATH_ELEMENT_SEPARATOR;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.SERVICE_UNAVAILABLE;
import io.netty.handler.codec.http.HttpMethod;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.on36.haetae.api.http.MediaType;
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

	private final Map<HandlerKey, TreeMap<String, RequestHandlerImpl>> handlerMap = new ConcurrentHashMap<HandlerKey, TreeMap<String, RequestHandlerImpl>>();
	private final Map<String, HandlerKey> handlerKeyMap = new ConcurrentHashMap<String, HandlerKey>();

	private final HandlerKey rootHandlerKey;
	private final RequestHandlerImpl rootHandler;

	public RequestResolver(Container container) {
		router.add(Route.PATH_ROOT);
		rootHandlerKey = new HandlerKey(HttpMethod.GET.name(), Route.PATH_ROOT,
				MediaType.TEXT_HTML.value());

		rootHandler = new RequestHandlerImpl();
		rootHandler.with(new StatisticsHandler(container));
	}

	public boolean addHandler(RequestHandler handler, HttpMethod method,
			String resource, String version) {
		return addHandler(handler, method, resource, version, null);
	}

	public boolean addHandler(RequestHandler handler, HttpMethod method,
			String resource, String version, String contentType) {
		if (handler == null || method == null) {
			throw new IllegalArgumentException(
					"handler or method cannot be null");
		}
		if (resource == null || "".equals(resource)) {
			throw new IllegalArgumentException("resource cannot be null");
		}
		if (PATH_ELEMENT_SEPARATOR.equals(resource)
				|| PATH_ELEMENT_ROOT.equals(resource)) {
			throw new IllegalArgumentException(
					"resource cannot be '/' or '" + PATH_ELEMENT_ROOT + "'");
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
		HandlerKey key = new HandlerKey(method.name(), route, contentType,
				version);
		handlerKeyMap.put(route.getResourcePath() + "-" + method.name(), key);
		if (!handlerMap.containsKey(key))
			handlerMap.put(key, new TreeMap<String, RequestHandlerImpl>(
					new VersionComparator()));
		TreeMap<String, RequestHandlerImpl> slot = handlerMap.get(key);
		slot.put(version, (RequestHandlerImpl) handler);

		router.add(route);
		return true;
	}

	public RequestHandlerImpl findHandler(String resource) {

		String path = resource;
		if (!resource.startsWith(PATH_ELEMENT_ROOT))
			path = PATH_ELEMENT_ROOT
					+ (resource.startsWith(PATH_ELEMENT_SEPARATOR) ? resource
							: (PATH_ELEMENT_SEPARATOR + resource));

		Route route = router.route(path);
		if (route != null) {
			HandlerKey key = new HandlerKey(HttpMethod.GET.name(), route);
			RequestHandlerImpl handler = handlerMap.get(key).lastEntry()
					.getValue();
			if (handler == null) {
				key = new HandlerKey(HttpMethod.POST.name(), route);
				return handlerMap.get(key).lastEntry().getValue();
			} else
				return handler;
		}
		return null;
	}

	public boolean removeHandler(String resource) {
		String path = resource;
		if (!resource.startsWith(PATH_ELEMENT_ROOT))
			path = PATH_ELEMENT_ROOT
					+ (resource.startsWith(PATH_ELEMENT_SEPARATOR) ? resource
							: (PATH_ELEMENT_SEPARATOR + resource));
		Route route = router.route(path);
		if (route != null) {
			HandlerKey key = new HandlerKey(HttpMethod.GET.name(), route);
			handlerMap.remove(key);
			handlerKeyMap.remove(
					route.getResourcePath() + "-" + HttpMethod.GET.name());
			key = new HandlerKey(HttpMethod.POST.name(), route);
			handlerMap.remove(key);
			handlerKeyMap.remove(
					route.getResourcePath() + "-" + HttpMethod.POST.name());
			router.remove(path);
		}

		return true;
	}

	public ResolvedRequest resolve(HttpRequestExt request) throws Exception {

		ResolvedRequest resolved = new ResolvedRequest();
		String method = request.getMethod().name();
		String path = new URI(request.getUri()).getPath();
		String contentType = request.headers().get(CONTENT_TYPE);

		if (path.endsWith(PATH_ELEMENT_SEPARATOR))
			path = path.substring(0, (path.length() - 1));

		Route route = router.route(path);
		if (route == null) {
			resolved.errorStatus = SERVICE_UNAVAILABLE;
			return resolved;
		}

		if (Route.PATH_ROOT.equals(route)
				&& rootHandlerKey.getMethod().equals(method)) {
			resolved.handler = rootHandler;
			resolved.route = Route.PATH_ROOT;
			resolved.key = rootHandlerKey;
			if (contentType != null)
				resolved.contentType = contentType;
		} else {
			HandlerKey key = handlerKeyMap
					.get(route.getResourcePath() + "-" + method);

			RequestHandlerImpl handler = handlerMap.get(key).lastEntry()
					.getValue();
			if (handler == null) {
				resolved.errorStatus = SERVICE_UNAVAILABLE;
				return resolved;
			}
			if (contentType != null)
				resolved.contentType = contentType;

			resolved.handler = handler;
			resolved.route = route;
			resolved.key = key;
		}
		return resolved;
	}

	public List<Statistics> getStatistics() {
		List<Statistics> stats = new ArrayList<Statistics>();

		for (Map.Entry<HandlerKey, TreeMap<String, RequestHandlerImpl>> entry : handlerMap
				.entrySet()) {
			String resourcePath = entry.getKey().getRoute().getResourcePath();
			Statistics stat = entry.getValue().lastEntry().getValue()
					.getStatistics();
			stat.setPath(resourcePath.startsWith(PATH_ELEMENT_ROOT)
					? resourcePath.replace(PATH_ELEMENT_ROOT, "")
					: resourcePath);
			stat.setMethod(entry.getKey().getMethod());
			stat.setVersion(entry.getKey().getVersion());
			stats.add(stat);
		}
		Collections.sort(stats);
		return stats;
	}

}
