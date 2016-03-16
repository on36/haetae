package com.on36.haetae.server.scan;

import io.netty.handler.codec.http.HttpMethod;

import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.List;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Get;
import com.on36.haetae.api.annotation.Path;
import com.on36.haetae.api.annotation.Post;
import com.on36.haetae.hotswap.classloader.MavenClassLoader;
import com.on36.haetae.hotswap.scan.ClassPathPackageScanner;
import com.on36.haetae.server.HaetaeServer;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class ScanTask implements Runnable {

	private final String packageName;

	private final HaetaeServer server;

	private String[] gavs;

	private boolean running = true;

	public ScanTask(String packageName, HaetaeServer server, String... gavs) {
		this.packageName = packageName;
		this.server = server;
		this.gavs = gavs;
	}

	public void setGavs(String... gavs) {
		this.gavs = gavs;
	}

	@Override
	public void run() {

		while (running) {
			try {
				URLClassLoader loader = MavenClassLoader.forGAVS(gavs);
				List<String> clazzs = ClassPathPackageScanner.scan(loader,
						packageName);

				for (String classString : clazzs) {
					Class<?> clazz = loader.loadClass(classString);
					Method[] methods = clazz.getDeclaredMethods();

					Object object = null;
					for (Method method : methods) {
						Class<?>[] typeClazzs = method.getParameterTypes();
						if (typeClazzs.length == 1
								&& typeClazzs[0].getName().equals(
										Context.class.getName())) {

							Post post = method.getAnnotation(Post.class);
							Get get = method.getAnnotation(Get.class);
							Path path = method.getAnnotation(Path.class);
							if (path != null) {
								if (object == null)
									object = clazz.newInstance();
								if (post != null)
									server.register(path.value(),
											HttpMethod.POST).with(object,
											method);
								else if (get != null)
									server.register(path.value(),
											HttpMethod.GET)
											.with(object, method);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
