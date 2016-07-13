package com.on36.haetae.manager.server;

import java.lang.reflect.Method;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Get;
import com.on36.haetae.api.annotation.Post;
import com.on36.haetae.api.http.MediaType;
import com.on36.haetae.manager.ManagerService;
import com.on36.haetae.server.HaetaeServer;

/**
 * @author zhanghr
 * @date 2016年3月11日 
 */
public class ManagerServer {

	public static void main(String[] args) throws Exception {
		int port = 1015;
		if(args != null && args.length >= 1)
			port = Integer.parseInt(args[0]);

		HaetaeServer server = new HaetaeServer(port,"/manager");

		Class<?> clazz = ManagerService.class;
		Method[] methods = clazz.getDeclaredMethods();
		Object object = null;
		
		for (Method method : methods) {
			Class<?>[] clazzs = method.getParameterTypes();
			if (clazzs.length == 1
					&& clazzs[0].getName().equals(Context.class.getName())) {

				Post post = method.getAnnotation(Post.class);
				Get get = method.getAnnotation(Get.class);
				if (object == null)
					object = clazz.newInstance();
				if (post != null) {
					if (server.find(post.value()) == null)
						server.register(post,MediaType.TEXT_HTML.value()).with(object, method);
				} else if (get != null) {
					if (server.find(get.value()) == null)
						server.register(get,MediaType.TEXT_HTML.value()).with(object, method);
				}
			}
		}
		server.start();
	}

}
