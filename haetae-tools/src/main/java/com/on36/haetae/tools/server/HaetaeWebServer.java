package com.on36.haetae.tools.server;

import java.lang.reflect.Method;

/**
 * @author zhanghr
 * @date 2016年3月28日 
 */
public class HaetaeWebServer {

	public static void main(String[] args) {
		try {
			Class<?> haetaeServerClass = HaetaeWebServer.class.getClassLoader()
					.loadClass("org.eclipse.jetty.runner.Runner");
			Method method = haetaeServerClass.getMethod("main", String[].class);
			method.invoke(null, (Object) args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
