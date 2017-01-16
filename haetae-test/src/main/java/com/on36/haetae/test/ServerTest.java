package com.on36.haetae.test;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Api;
import com.on36.haetae.api.annotation.ApiDoc;
import com.on36.haetae.api.core.HttpHandler;
import com.on36.haetae.api.http.MethodType;
import com.on36.haetae.server.HaetaeServer;

/**
 * @author zhanghr
 * @date 2016年1月3日
 */
@SuppressWarnings("deprecation")
public class ServerTest {

	public static void main(String[] args) throws Exception {

		int port = 8080;
		int size = 4;
		if (args != null && args.length == 1)
			port = Integer.parseInt(args[0]);
		else if (args != null && args.length == 2) {
			port = Integer.parseInt(args[0]);
			size = Integer.parseInt(args[1]);
		}

		HaetaeServer server = new HaetaeServer(port, size);
		server.register("/hello").with("Hello xiongdi!").auth(false);
		server.register("/name/:name<[A-Za-z]+>").with("Hello :name");
		server.register("/multi/*/*").with("Hello *[0] *[1]");
		server.register("/greeting").with("Hello [request$User-Agent]");
		server.register("/control").with("Hello control!").every(30, TimeUnit.SECONDS, 10);
		server.register("/skip").withRedirect("http://www.baidu.com");
		server.register("/black").with("Hello black!").ban("172.31.25.40", "127.0.0.1");
		server.register("/white").with("Hello white!").permit("172.31.25.40", "127.0.0.1");
		server.register("/whitecontrol").with("Hello white!").permit("127.0.0.1", 10, 10, TimeUnit.SECONDS);
		server.register("/body", MethodType.POST).with(new HttpHandler<String>() {

			public String handle(Context context) {
				return context.getBodyAsString();
			}
		});
		server.register("/custom", MethodType.POST).with(new HttpHandler<String>() {

			public String handle(Context context) {
				return context.getRequestParameter("user");
			}
		});
		server.register("/customobject", MethodType.POST).with(new HttpHandler<String>() {

			public String handle(Context context) throws Exception {
				// User user = context.getBody(User.class);
				String jo = context.getBodyAsString();

				return jo;
			}
		});
		server.register("/timeout", MethodType.GET).timeout(1, TimeUnit.SECONDS).with(new HttpHandler<String>() {

			public String handle(Context context) throws Exception {
				// User user = context.getBody(User.class);
				Thread.sleep(2000);

				System.out.println(Thread.currentThread().getName());
				return context.request("/hello");
			}
		});
		server.register("/custombody/*/*", MethodType.POST).with(new HttpHandler<String>() {

			public String handle(Context context) throws Exception {
				return context.getCapturedParameter("*[0] *[1]");
			}
		});

		Class<?> clazz = UserService.class;
		Method[] methods = clazz.getDeclaredMethods();

		Object object = null;
		for (Method method : methods) {
			Class<?>[] clazzs = method.getParameterTypes();
			if (clazzs.length == 1 && clazzs[0].getName().equals(Context.class.getName())) {

				Api api = method.getAnnotation(Api.class);
				ApiDoc apiDoc = method.getAnnotation(ApiDoc.class);
				if (api != null) {
					if (object == null)
						object = clazz.newInstance();
					server.register(api, apiDoc).with(object, method);
				}
			}
		}
		server.start();
	}
}
