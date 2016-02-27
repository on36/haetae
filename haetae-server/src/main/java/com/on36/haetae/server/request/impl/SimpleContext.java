package com.on36.haetae.server.request.impl;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.http.Session;
import com.on36.haetae.http.request.HttpRequestExt;
import com.on36.haetae.http.route.Route;

public class SimpleContext implements Context {

    private final HttpRequestExt request;
    
    private final Route route;
    
    private final Session session;
    
    private Map<String, String> parmMap = new HashMap<String, String>();
    
    private String path;
    
    
    public SimpleContext(HttpRequestExt request, Route route, Session session) {
        
        this.request = request;
        this.route = route;
        this.session = session;
        try {
			parse();
			path = new URI(this.request.getUri()).getPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public String getPath() {
        
        return this.path;
    }
    
    public Route getRoute() {
        
        return route;
    }
    
    private Map<String, String> parse() throws Exception {
        HttpMethod method = request.getMethod();

        if (HttpMethod.GET == method) {
            QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
            Set<Entry<String, List<String>>> sets  = decoder.parameters().entrySet();
              for(Entry<String, List<String>> entry : sets){
                parmMap.put(entry.getKey(), entry.getValue().get(0));
            };
        } else if (HttpMethod.POST == method) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
            decoder.offer((HttpContent) request);

            List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();

            for (InterfaceHttpData parm : parmList) {
                Attribute data = (Attribute) parm;
                parmMap.put(data.getName(), data.getValue());
            }
        } 

        return parmMap;
    }

	public String getRequestParameter(String param) {
		return parmMap.get(param);
	}

	public Set<String> getRequestParameterNames() {
		return parmMap.keySet();
	}

	public Session getSession() {
		return session;
	}

	public String getHeaderValue(String param) {
		return  request.headers().get(param);
	}
}
