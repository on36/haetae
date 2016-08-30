package com.on36.haetae.test;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.JSONObject;
import com.on36.haetae.api.annotation.Get;
import com.on36.haetae.api.annotation.Post;
import com.on36.haetae.api.core.HttpHandler;
import com.on36.haetae.http.ServiceLevel;
import com.on36.haetae.server.HaetaeServer;

import io.netty.handler.codec.http.HttpMethod;

/**
 * @author zhanghr
 * @date 2016年1月3日
 */
public class ServerTest {

	public static void main(String[] args) throws Exception {

		int port = 8080;
		if (args != null && args.length == 1)
			port = Integer.parseInt(args[0]);

		HaetaeServer server = new HaetaeServer(port, 4);
		server.register("/hello").with("Hello xiongdi!").auth(false);
		server.register("/name/:name<[A-Za-z]+>").with("Hello :name");
		server.register("/multi/*/*").with("Hello *[0] *[1]");
		server.register("/greeting").with("Hello [request$User-Agent]");
		server.register("/control").with("Hello control!").every(30,
				TimeUnit.SECONDS, 10);
		server.register("/skip").withRedirect("http://www.baidu.com");
		server.register("/black").with("Hello black!").ban("172.31.25.40",
				"127.0.0.1");
		server.register("/white").with("Hello white!").permit("172.31.25.40",
				"127.0.0.1");
		server.register("/whitecontrol").with("Hello white!")
				.permit("127.0.0.1");
		server.register("/body", HttpMethod.POST)
				.with(new HttpHandler<String>() {

					public String handle(Context context) {
						return context.getBodyAsString();
					}
				});
		server.register("/custom", HttpMethod.POST)
				.with(new HttpHandler<String>() {

					public String handle(Context context) {
						return context.getRequestParameter("user");
					}
				});
		server.register("/customobject", HttpMethod.POST)
				.with(new HttpHandler<String>() {

					public String handle(Context context) throws Exception {
						// User user = context.getBody(User.class);
						String jo = context.getBodyAsString();

						return jo;
					}
				});
		server.register("/timeout", HttpMethod.GET).timeout(1, TimeUnit.SECONDS)
				.with(new HttpHandler<String>() {

					public String handle(Context context) throws Exception {
						// User user = context.getBody(User.class);
						Thread.sleep(2000);

						System.out.println(Thread.currentThread().getName());
						return context.getURI("/hello");
					}
				});
		server.register("/custombody/*/*", HttpMethod.POST)
				.with(new HttpHandler<String>() {

					public String handle(Context context) throws Exception {
						return context.getCapturedParameter("*[0] *[1]");
					}
				});

		Class<?> clazz = UserService.class;
		Method[] methods = clazz.getDeclaredMethods();

		for (Method method : methods) {
			Class<?>[] clazzs = method.getParameterTypes();
			if (clazzs.length == 1
					&& clazzs[0].getName().equals(Context.class.getName())) {

				Post post = method.getAnnotation(Post.class);
				Get get = method.getAnnotation(Get.class);
				if (post != null)
					server.register(post).with(clazz.newInstance(), method);
				else
					server.register(get).with(clazz.newInstance(), method);
			}
		}
		server.start();
	}
}
