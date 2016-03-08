package com.on36.haetae.server.core.body;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import java.io.IOException;

public class StringResponseBody extends ResponseBody {

    private String body;
    
    public StringResponseBody(String body) {
        this.body = body;
    }
    
    public boolean hasContent() {
        return body != null && body.trim().length() > 0;
    }
    public String content() {
    	return body;
    }

    @Override
    public void sendAndCommit(HttpResponse response, String contentType) {
        try {
			printBody(response, contentType);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
    
    private void printBody(HttpResponse response, String contentType) throws IOException {
        
        addStandardHeaders(response, contentType);
        
        if(!HttpResponseStatus.OK.equals(response.getStatus()) && (body == null || "".equals(body)) )
        	body = response.getStatus().reasonPhrase();
        
        if(response instanceof HttpContent) {
        	HttpContent httpContent = (HttpContent) response;
        	ByteBuf content = httpContent.content();
        	if(hasContent()) {
        	    content.writeBytes(body.getBytes(CharsetUtil.UTF_8));
        	}
        }
    }
}
