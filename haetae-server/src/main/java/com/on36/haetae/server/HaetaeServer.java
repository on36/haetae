package com.on36.haetae.server;

import io.netty.handler.codec.http.HttpMethod;

import java.util.concurrent.TimeUnit;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.core.CustomHandler;
import com.on36.haetae.http.Container;
import com.on36.haetae.http.RequestHandler;
import com.on36.haetae.http.Server;
import com.on36.haetae.http.core.HTTPServer;
import com.on36.haetae.server.core.RequestHandlerImpl;
import com.on36.haetae.server.core.container.HaetaeContainer;

/**
 * 
 * @author zhanghr
 * @date 2016年2月29日
 */
public class HaetaeServer {

	private Container container;
	private Server server;

	public HaetaeServer(int port) {
		this(port, false);
	}

	public HaetaeServer(int port, boolean ssl) {
		this(port, 0, ssl);
	}

	public HaetaeServer(int port, int threadPoolSize, boolean ssl) {
		server = new HTTPServer(port, threadPoolSize, ssl);
		container = new HaetaeContainer();
		server.setContainer(container);
	}

	public void start() {
		try {
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		if (server == null) {
			throw new IllegalStateException("server has not been started");
		}

		server.stop();
	}

	public RequestHandler register(String resource, HttpMethod method) {

		RequestHandlerImpl handler = new RequestHandlerImpl();
		container.addHandler(handler, method, resource);
		return handler;
	}

	public RequestHandler register(String resource) {

		return register(resource, HttpMethod.GET);
	}
	
	public boolean unregister(String resource) {
		
		return container.removeHandler(resource);
	}
	
	public RequestHandler find(String resource) {
		
		return container.findHandler(resource);
	}

	public static void main(String[] args) {
		HaetaeServer server = new HaetaeServer(8080);
		server.register("/hello").with("Hello xiongdi!").session(true);
		server.register("name/:name<[A-Za-z]+>").with("Hello :name");
		server.register("/multi").with("Hello *[0] *[1]");
		server.register("/greeting").with(
				"Hello [request?name] at [request$User-Agent]");
		server.register("/redis").with("Hello redis!")
				.every(30, TimeUnit.SECONDS, 10);
		server.register("/skip").withRedirect("http://www.baidu.com");
		server.register("/black").with("Hello black!").ban("172.31.25.40");
		server.register("/white").with("Hello white!").permit("172.31.25.40");
		server.register("/custom").with(new CustomHandler<String>() {

			public String handle(Context context) {
				return "custom handler";
			}
		});
		server.start();
	}
}
