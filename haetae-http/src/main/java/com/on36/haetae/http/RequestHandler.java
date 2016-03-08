package com.on36.haetae.http;

import java.util.concurrent.TimeUnit;

import com.on36.haetae.api.core.CustomHandler;

public interface RequestHandler {

    RequestHandler with(String body);
    
    RequestHandler with(CustomHandler<?> customHandler);

    RequestHandler withHeader(String name, String value);
    
    RequestHandler timeout(long timeout, TimeUnit timeoutUnit);
    
    RequestHandler every(long period, TimeUnit periodUnit, int times);
    
    RequestHandler every(int times);
    
    RequestHandler auth(boolean authentication);
    
    RequestHandler unban(String...blackips);
    
    RequestHandler ban(String...blackips);
    
    RequestHandler unpermit(String...whiteips);
    
    RequestHandler permit(String...whiteips);
    
    RequestHandler permit(String ip, ServiceLevel level);
    
    RequestHandler permit(String ip, int times);
    
    RequestHandler permit(String ip, ServiceLevel level, long period, TimeUnit periodUnit);
    
    RequestHandler permit(String ip, int times, long period, TimeUnit periodUnit);
    
    RequestHandler withRedirect(String location);
    
    RequestHandler session(boolean has);
}

