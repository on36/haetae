package com.on36.haetae.test;

import io.netty.handler.codec.http.HttpMethod;

import java.util.concurrent.TimeUnit;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.core.CustomHandler;
import com.on36.haetae.http.ServiceLevel;
import com.on36.haetae.server.HaetaeServer;

/**
 * @author zhanghr
 * @date 2016年1月3日 
 */
public class ServerTest {

	public static void main(String[] args) {
		
		HaetaeServer server = new HaetaeServer(8080);
		server.register("/hello").with("Hello xiongdi!").session(true);
		server.register("name/:name<[A-Za-z]+>").with("Hello :name");
		server.register("/multi/*/*").with("Hello *[0] *[1]");
		server.register("/greeting").with(
				"Hello [request$User-Agent]");
		server.register("/control").with("Hello control!")
				.every(30, TimeUnit.SECONDS, 10);
		server.register("/skip").withRedirect("http://www.baidu.com");
		server.register("/black").with("Hello black!").ban("172.31.25.40","127.0.0.1");
		server.register("/white").with("Hello white!").permit("172.31.25.40","127.0.0.1");
		server.register("/whitecontrol").with("Hello white!").permit("127.0.0.1",ServiceLevel.LEVELC);
		server.register("/body",HttpMethod.POST).with(new CustomHandler<String>() {

			public String handle(Context context) {
				return context.getRequestBodyAsString();
			}
		});
		server.register("/custom",HttpMethod.POST).with(new CustomHandler<String>() {
			
			public String handle(Context context) {
				return context.getRequestParameter("user");
			}
		});
		server.register("/custombody/*/*",HttpMethod.POST).with(new CustomHandler<String>() {
			
			public String handle(Context context) {
				return context.getCapturedParameter("*[0] *[1]");
			}
		});
		server.start();
	}

}
