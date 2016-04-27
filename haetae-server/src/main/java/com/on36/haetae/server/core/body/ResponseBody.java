package com.on36.haetae.server.core.body;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.SERVER;
import io.netty.handler.codec.http.HttpResponse;

import com.on36.haetae.http.Version;

public abstract class ResponseBody {

    public abstract void sendAndCommit(HttpResponse resp, String contentType);
    
    public abstract boolean hasContent();
    
    public abstract String content();
    
    protected void addStandardHeaders(HttpResponse response, String responseContentType) {
        response.headers().set(CONTENT_TYPE, responseContentType);
        response.headers().set(SERVER, "Haetae/" +Version.CURRENT_VERSION+ "(Netty 4.0.36.Final)");
    }
}
