package com.on36.haetae.http;

import com.on36.haetae.http.route.Route;

public class HandlerKey {

    private final String method;
    private final Route route;
    private String contentType;
    
    public HandlerKey(String method, Route route) {
        
        this.method = method;
        this.route = route;
    }
    
    public String contentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
		this.contentType = contentType;
	}
    
	public String getMethod() {
		return method;
	}

	public Route getRoute() {
		return route;
	}

	@Override
    public int hashCode() {
        
        int hash = 1;
        hash = hash * 17 + method.hashCode();
        hash = hash * 31 + route.hashCode();
        hash = hash * 13 + (contentType == null ? 0 : contentType.hashCode());
        return hash;
    }
    
    @Override
    public boolean equals(Object o) {
        
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof HandlerKey)) return false;
        HandlerKey that = (HandlerKey)o;
        return 
                this.method.equals(that.method) && 
                this.route.equals(that.route);
    }
}
