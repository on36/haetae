package com.on36.haetae.http.route;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Matcher;

public class RegexRouter implements Router {

    private final Set<RegexRoute> routes = 
            new ConcurrentSkipListSet<RegexRoute>(new RegexRouteComparator());
    
    public void add(Route route) {
        routes.add(new RegexRoute(route));
    }
    
    /**
     * Returns a Route that matches the given URL path.
     * Note that the path is expected to be an undecoded URL path.
     * The router will handle any decoding that might be required.
     * 
     *  @param path an undecoded URL path
     *  @return the matching route, or null if none is found
     */
    public Route route(String path) {
    
        path = RouteHelper.urlDecodeForRouting(path);
        
        for (RegexRoute route : routes) {
            Matcher m = route.pattern().matcher(path);
            if (m.find()) {
                return route.getRoute();
            }
        }
        
        return null;
    }

	@Override
	public void remove(String path) {
	}
}
