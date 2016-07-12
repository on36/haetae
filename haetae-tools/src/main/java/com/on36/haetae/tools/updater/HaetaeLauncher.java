package com.on36.haetae.tools.updater;

import java.lang.reflect.Method;

import com.on36.haetae.hotswap.IClassLoader;
import com.on36.haetae.hotswap.classloader.DirectoryClassLoader;

/**
 * @author zhanghr
 * @date 2016年3月8日
 */
public class HaetaeLauncher {

	private static IClassLoader cl = new DirectoryClassLoader();

	public static void main(String[] args) {
		try {
			ClassLoader classLoader = cl.load();
			Class<?> haetaeServerClass = classLoader
					.loadClass("com.on36.haetae.manager.server.ManagerServer");
			Method method = haetaeServerClass.getMethod("main", String[].class);
			method.invoke(null, (Object) args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
