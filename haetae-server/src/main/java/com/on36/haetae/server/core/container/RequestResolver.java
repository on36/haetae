package com.on36.haetae.server.core.container;

import static com.on36.haetae.http.route.RouteHelper.PATH_ELEMENT_ROOT;
import static com.on36.haetae.http.route.RouteHelper.PATH_ELEMENT_SEPARATOR;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.SERVICE_UNAVAILABLE;

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

import io.netty.handler.codec.http.HttpMethod;

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
			String resourceKey = resource + "-" + method.name();
			if (handlerKeyMap.get(resourceKey) != null && handlerMap
					.get(handlerKeyMap.get(resourceKey)).containsKey(version))
				throw new IllegalArgumentException(
						"There is already the route[" + resource + " " + version
								+ "], adding operation is not allowed!");
		}
		route = new Route(path);
		HandlerKey key = new HandlerKey(method.name(), route, contentType);
		handlerKeyMap.put(route.getResourcePath() + "-" + method.name(), key);
		if (!handlerMap.containsKey(key))
			handlerMap.put(key, new TreeMap<String, RequestHandlerImpl>(
					new VersionComparator()));
		TreeMap<String, RequestHandlerImpl> slot = handlerMap.get(key);
		slot.put(version, (RequestHandlerImpl) handler);

		router.add(route);
		return true;
	}

	public RequestHandlerImpl findHandler(String resource, String methodName,
			String version) {
		if (resource == null || resource.trim().length() == 0) {
			throw new IllegalArgumentException("resource cannot be null");
		}
		if (methodName == null || methodName.trim().length() == 0) {
			throw new IllegalArgumentException("methodName cannot be null");
		}

		String path = resource;
		if (!resource.startsWith(PATH_ELEMENT_ROOT))
			path = PATH_ELEMENT_ROOT
					+ (resource.startsWith(PATH_ELEMENT_SEPARATOR) ? resource
							: (PATH_ELEMENT_SEPARATOR + resource));

		Route route = router.route(path);
		if (route != null) {
			String resourceKey = resource + "-" + methodName;
			return find(resourceKey, version);
		}
		return null;
	}

	private RequestHandlerImpl find(String resourceKey, String version) {
		RequestHandlerImpl handler = null;
		if (handlerKeyMap.containsKey(resourceKey)) {
			if (version == null)
				handler = handlerMap.get(handlerKeyMap.get(resourceKey))
						.lastEntry().getValue();
			else {
				handler = handlerMap.get(handlerKeyMap.get(resourceKey))
						.get(version);
				if (handler == null)
					handler = handlerMap.get(handlerKeyMap.get(resourceKey))
							.lastEntry().getValue();
			}
		}
		return handler;
	}

	public boolean removeHandler(String resource, String methodName,
			String version) {
		if (resource == null || resource.trim().length() == 0) {
			throw new IllegalArgumentException("resource cannot be null");
		}

		String path = resource;
		if (!resource.startsWith(PATH_ELEMENT_ROOT))
			path = PATH_ELEMENT_ROOT
					+ (resource.startsWith(PATH_ELEMENT_SEPARATOR) ? resource
							: (PATH_ELEMENT_SEPARATOR + resource));
		Route route = router.route(path);
		if (route != null) {
			if (methodName != null) {
				String resourceKey = resource + "-" + methodName;
				remove(path, resourceKey, version);
			} else {
				String resourceKey = resource + "-" + HttpMethod.PUT.name();
				remove(path, resourceKey, version);
				resourceKey = resource + "-" + HttpMethod.GET.name();
				remove(path, resourceKey, version);
				resourceKey = resource + "-" + HttpMethod.POST.name();
				remove(path, resourceKey, version);
				resourceKey = resource + "-" + HttpMethod.DELETE.name();
				remove(path, resourceKey, version);
			}
		}

		return true;
	}

	private void remove(String path, String resourceKey, String version) {
		if (handlerKeyMap.containsKey(resourceKey)) {
			HandlerKey key = handlerKeyMap.get(resourceKey);
			if (version == null)
				handlerMap.remove(key);
			else
				handlerMap.get(key).remove(version);
			router.remove(path);
			handlerKeyMap.remove(resourceKey);
		}
	}

	public ResolvedRequest resolve(HttpRequestExt request) throws Exception {

		ResolvedRequest resolved = new ResolvedRequest();
		String method = request.getMethod().name();
		String path = new URI(request.getUri()).getPath();
		String version = request.headers().get("VERSION");// TODO 请求服务版本号如何处理？
		String contentType = request.headers().get(CONTENT_TYPE);

		if (path.endsWith(PATH_ELEMENT_SEPARATOR))
			path = path.substring(0, (path.length() - 1));

		Route route = router.route(path);
		if (route == null) {
			resolved.errorStatus = SERVICE_UNAVAILABLE;
			resolved.warn = "Not found route " + path;
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
			String resourceKey = route.getResourcePath() + "-" + method;
			HandlerKey key = handlerKeyMap.get(resourceKey);
			RequestHandlerImpl handler = find(resourceKey, version);
			if (handler == null) {
				resolved.errorStatus = SERVICE_UNAVAILABLE;
				resolved.warn = "Not found route[" + path + "],method[" + method
						+ "],version[" + version + "]";
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
			TreeMap<String, RequestHandlerImpl> treeMap = entry.getValue();
			int size = treeMap.size();
			Statistics stat = entry.getValue().lastEntry().getValue()
					.getStatistics();
			stat.setPath(resourcePath.startsWith(PATH_ELEMENT_ROOT)
					? resourcePath.replace(PATH_ELEMENT_ROOT, "")
					: resourcePath);
			stat.setMethod(entry.getKey().getMethod());
			stat.setVersion(entry.getValue().lastEntry().getKey());
			stats.add(stat);
			if (size > 1) {
				List<Statistics> children = new ArrayList<Statistics>();
				for (Map.Entry<String, RequestHandlerImpl> versionHandler : treeMap
						.entrySet()) {
					String version = versionHandler.getKey();
					Statistics child = versionHandler.getValue()
							.getStatistics();
					child.setPath(resourcePath.startsWith(PATH_ELEMENT_ROOT)
							? resourcePath.replace(PATH_ELEMENT_ROOT, "")
							: resourcePath);
					child.setMethod(entry.getKey().getMethod());
					child.setVersion(version);
					children.add(0, child);
				}
				stat.setChildStatisticsList(children);
			}
		}
		Collections.sort(stats);
		return stats;
	}

}
