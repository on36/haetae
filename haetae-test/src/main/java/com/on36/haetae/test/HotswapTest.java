package com.on36.haetae.test;

import io.netty.handler.codec.http.HttpMethod;

import java.lang.reflect.Method;
import java.util.List;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Path;
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
					Path path = method.getAnnotation(Path.class);
					if (path != null) {
						if (object == null)
							object = clazz.newInstance();
						if (post != null)
							server.register(path.value(), HttpMethod.POST)
									.with(object, method);
						else
							server.register(path.value(), HttpMethod.GET).with(
									object, method);
					}
				}
			}
		}

		server.start();
	}
}
