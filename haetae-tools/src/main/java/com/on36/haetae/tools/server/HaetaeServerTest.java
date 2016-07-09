package com.on36.haetae.tools.server;

import java.lang.reflect.Method;

import com.on36.haetae.hotswap.IClassLoader;
import com.on36.haetae.hotswap.classloader.DirectoryClassLoader;

/**
 * @author zhanghr
 * @date 2016年3月8日
 */
public class HaetaeServerTest {

	private static IClassLoader cl = new DirectoryClassLoader();

	public static void main(String[] args) {
		try {
			ClassLoader classLoader = cl.load();
			Class<?> haetaeServerClass = classLoader
					.loadClass("com.on36.haetae.test.ServerTest");
			Method method = haetaeServerClass.getMethod("main", String[].class);
			method.invoke(null, (Object) args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
