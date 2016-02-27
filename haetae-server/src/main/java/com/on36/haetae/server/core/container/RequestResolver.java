package com.on36.haetae.server.core.container;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import io.netty.handler.codec.http.HttpMethod;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.on36.haetae.http.HandlerKey;
import com.on36.haetae.http.RequestHandler;
import com.on36.haetae.http.request.HttpRequestExt;
import com.on36.haetae.http.route.Route;
import com.on36.haetae.http.route.Router;
import com.on36.haetae.http.route.TreeRouter;
import com.on36.haetae.server.core.RequestHandlerImpl;

public class RequestResolver {
    
    private final Router router = new TreeRouter();
    
    private final Map<HandlerKey, RequestHandlerImpl> handlerMap = 
            new ConcurrentHashMap<HandlerKey, RequestHandlerImpl>();
    
    
    public HandlerKey addHandler(RequestHandler handler, 
            HttpMethod method, String resource, String contentType) {
        
        Route route = new Route(resource);
        HandlerKey key = new HandlerKey(method.name(), route, contentType);
        handlerMap.put(key, (RequestHandlerImpl) handler);
        router.add(route);
        return key;
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
        HandlerKey key = new HandlerKey(method, route, contentType);
        RequestHandlerImpl handler = handlerMap.get(key);
        if (handler == null) {
            resolved.errorStatus = METHOD_NOT_ALLOWED;
            return resolved;
        }
        
        resolved.handler = handler;
        resolved.route = route;
        resolved.key = key;
        return resolved;
    }

}
