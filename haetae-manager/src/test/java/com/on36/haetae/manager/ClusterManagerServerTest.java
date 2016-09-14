package com.on36.haetae.manager;

import java.lang.reflect.Method;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Api;
import com.on36.haetae.server.HaetaeServer;

/**
 * @author zhanghr
 * @date 2016年1月3日
 */
public class ClusterManagerServerTest {

	public static void main(String[] args) throws Exception {

		int port = 1025;
		if (args != null && args.length == 1)
			port = Integer.parseInt(args[0]);

		HaetaeServer server = new HaetaeServer(port, "/cluster");

		Class<?> clazz = ClusterManagerService.class;
		Method[] methods = clazz.getDeclaredMethods();
		Object object = null;
		for (Method method : methods) {
			Class<?>[] clazzs = method.getParameterTypes();
			if (clazzs.length == 1
					&& clazzs[0].getName().equals(Context.class.getName())) {

				Api api = method.getAnnotation(Api.class);
				if (object == null)
					object = clazz.newInstance();
				if (api != null) {
					server.register(api).with(object, method);
				}
			}
		}
		server.start();
	}
}
