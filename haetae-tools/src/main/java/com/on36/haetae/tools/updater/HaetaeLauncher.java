package com.on36.haetae.tools.updater;

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
			ClassLoader classLoader = cl.load();
			List<String> clazzs = ClassPathPackageScanner.scan(classLoader,
					"com.on36.haetae.manager.server");

			for (String classString : clazzs) {
				Class<?> managerClazz = classLoader.loadClass(classString);
				Method method = managerClazz.getMethod("main", String[].class);
				method.invoke(null, (Object) args);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
