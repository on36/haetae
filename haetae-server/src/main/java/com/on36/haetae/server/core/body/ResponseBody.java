package com.on36.haetae.server.core.body;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.SERVER;
import static io.netty.handler.codec.http.HttpHeaders.Names.DATE;
import static io.netty.handler.codec.http.HttpHeaders.Names.LAST_MODIFIED;

import com.on36.haetae.http.Version;

import io.netty.handler.codec.http.HttpResponse;

public abstract class ResponseBody {

    public abstract void send(HttpResponse resp, String contentType);
    
    public abstract void sendAndCommit(HttpResponse resp, String contentType);
    
    public abstract boolean hasContent();
    
    protected void addStandardHeaders(HttpResponse response, String responseContentType) {
        
        long time = System.currentTimeMillis();
        
        response.headers().set(CONTENT_TYPE, responseContentType);
        response.headers().set(SERVER, "Haetae/" +Version.CURRENT_VERSION+ " (netty 4.0.34)");
        response.headers().set(DATE, time);
        response.headers().set(LAST_MODIFIED, time);
    }
}
