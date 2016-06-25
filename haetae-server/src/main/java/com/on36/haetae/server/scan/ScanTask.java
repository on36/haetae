package com.on36.haetae.server.scan;

import java.lang.reflect.Method;
import java.util.List;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Get;
import com.on36.haetae.api.annotation.Post;
import com.on36.haetae.hotswap.IClassLoader;
import com.on36.haetae.hotswap.scan.ClassPathPackageScanner;
import com.on36.haetae.http.Configuration;
import com.on36.haetae.server.HaetaeServer;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class ScanTask implements Runnable {

	private final String packageName;
	private final String classLoaderName;

	private final HaetaeServer server;

	private boolean running = true;

	private Configuration config = Configuration.create();

	public ScanTask(HaetaeServer server) {
		this.packageName = config.getString("httpServer.scan.packageName",
				"com.on36.crm");
		this.classLoaderName = config.getString("httpServer.scan.classLoader",
				"com.on36.haetae.hotswap.classloader.DirectoryClassLoader");
		this.server = server;
	}

	@Override
	public void run() {

		while (running) {
			try {
				Class<?> classLoaderClass = Class.forName(classLoaderName);
				IClassLoader cl = (IClassLoader) classLoaderClass.newInstance();
				ClassLoader loader = cl.load();
				List<String> clazzs = ClassPathPackageScanner.scan(loader,
						packageName);

				for (String classString : clazzs) {
					Class<?> clazz = loader.loadClass(classString);
					Method[] methods = clazz.getMethods();

					Object object = null;
					for (Method method : methods) {
						Class<?>[] typeClazzs = method.getParameterTypes();
						if (typeClazzs.length == 1 && typeClazzs[0].getName()
								.equals(Context.class.getName())) {

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
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		running = false;
	}

}
