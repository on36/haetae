package com.on36.haetae.test;

import java.lang.reflect.Method;
import java.util.List;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Get;
import com.on36.haetae.api.annotation.Post;
import com.on36.haetae.hotswap.scan.ClassPathPackageScanner;
import com.on36.haetae.server.HaetaeServer;

/**
 * @author zhanghr
 * @date 2016年1月10日
 */
public class HotswapTest {

	public static void main(String[] args) throws Exception {

		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		List<String> clazzs = ClassPathPackageScanner.scan(cl,
				"com.on36.haetae.test");

		HaetaeServer server = new HaetaeServer(8089);

		for (String classString : clazzs) {
			Class<?> clazz = cl.loadClass(classString);
			Method[] methods = clazz.getDeclaredMethods();

			Object object = null;
			for (Method method : methods) {
				Class<?>[] typeClazzs = method.getParameterTypes();
				if (typeClazzs.length == 1
						&& typeClazzs[0].getName().equals(
								Context.class.getName())) {

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
		}

		server.start();
	}
}
