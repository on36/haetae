package com.on36.haetae.http.route;


public interface Router {

    void add(Route route);
    
    /**
     * Returns a Route that matches the given URL path.
     * Note that the path may be expected to be an undecoded
     * URL path. This URL encoding requirement is determined
     * by the Router implementation.
     * 
     *  @param path a decoded or undecoded URL path, 
     *              depending on the Router implementation
     *  @return the matching route, or null if none is found
     */
    Route route(String path);
    
    
    void remove(String path);
}
