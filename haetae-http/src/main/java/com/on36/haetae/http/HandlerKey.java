package com.on36.haetae.http;

import com.on36.haetae.http.route.Route;

public class HandlerKey {

    private final String method;
    private final Route route;
    private final String contentType;
    
    public HandlerKey(String method, Route route, String contentType) {
        
        this.method = method;
        this.route = route;
        this.contentType = contentType;
    }
    
    public String contentType() {
        return contentType;
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
                this.route.equals(that.route) && 
                (this.contentType == null ? that.contentType == null : 
                    this.contentType.equals(that.contentType));
    }
}
