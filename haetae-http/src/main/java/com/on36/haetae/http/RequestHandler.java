package com.on36.haetae.http;

import java.util.concurrent.TimeUnit;

import com.on36.haetae.api.core.CustomHandler;

public interface RequestHandler {

    RequestHandler with(int statusCode, String contentType, String body);
    
    RequestHandler with(CustomHandler<?> customHandler);

    RequestHandler withHeader(String name, String value);
    
    RequestHandler every(long period, TimeUnit periodUnit, int times);
    
    RequestHandler ban(String...blackips);
    
    RequestHandler permit(String...whiteips);
    
    RequestHandler withTimeout(long timeout, TimeUnit timeoutUnit);
    
    RequestHandler withRedirect(String location);
    
    RequestHandler withRedirect(String location, int statusCode);
    
    RequestHandler session(boolean has);
}

