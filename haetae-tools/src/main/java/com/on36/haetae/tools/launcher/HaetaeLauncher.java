package com.on36.haetae.tools.launcher;

import java.lang.reflect.Method;
import java.util.List;

import com.on36.haetae.hotswap.IClassLoader;
import com.on36.haetae.hotswap.classloader.DirectoryClassLoader;
import com.on36.haetae.hotswap.scan.ClassPathPackageScanner;

/**
 * @author zhanghr
 * @date 2016年3月8日
 */
public class HaetaeLauncher {

	private static IClassLoader cl = new DirectoryClassLoader();

	public static void main(String[] args) {
		try {
			int port = 1025;
			if (args != null && args.length >= 1)
				port = Integer.parseInt(args[0]);

			ClassLoader classLoader = cl.load();
			Class<?> haetaeServerClass = classLoader
					.loadClass("com.on36.haetae.server.HaetaeServer");

			List<String> clazzs = ClassPathPackageScanner.scan(classLoader,
					"com.on36.haetae.manager");
			clazzs.add("com.on36.haetae.config.agent.ConfigAgentService");
			Object obj = haetaeServerClass
					.getConstructor(int.class, int.class, String.class,
							List.class)
					.newInstance(port, 0, "/_cluster", clazzs);
			Method method = haetaeServerClass.getMethod("start");
			method.invoke(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
