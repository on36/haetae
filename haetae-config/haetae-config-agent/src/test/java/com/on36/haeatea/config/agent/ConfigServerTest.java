package com.on36.haeatea.config.agent;

import java.lang.reflect.Method;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Get;
import com.on36.haetae.api.annotation.Post;
import com.on36.haetae.config.agent.ConfigAgentService;
import com.on36.haetae.server.HaetaeServer;

/**
 * @author zhanghr
 * @date 2016年8月13日 
 */
public class ConfigServerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		HaetaeServer server = new HaetaeServer(1025, "/_cluster");

		Class<?> clazz = ConfigAgentService.class;
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
						server.register(post).with(object, method);
				} else if (get != null) {
					if (server.find(get.value()) == null)
						server.register(get).with(object, method);
				}
			}
		}
		server.start();
	}

}
