package com.on36.hotswap;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.on36.haetae.hotswap.classloader.MavenClassLoader;

public class MavenClassLoaderTest {
	private static String gav = "joda-time:joda-time:[1.6,)";
	private static String className = "org.joda.time.chrono.BuddhistChronology";
	private static ClassLoader loader = null;

	public static void setup() {
		loader = MavenClassLoader.forGAVS(gav);
	}

	public static void findClass() throws ClassNotFoundException,
			NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		assertNotNull(loader);
		Class<?> buddhistChronology = loader.loadClass(className);
		assertNotNull(buddhistChronology);
		Method factoryMethod = buddhistChronology.getMethod("getInstance");
		assertNotNull(factoryMethod.invoke(null));
	}

	//@Test
	public static void mkdirClass() throws ClassNotFoundException,
			NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		assertNotNull(loader);
		Class<?> buddhistChronology = loader.loadClass(className);
		assertNotNull(buddhistChronology);
		Method factoryMethod = buddhistChronology.getMethod("getInstance");
		assertNotNull(factoryMethod.invoke(null));
	}
	
	public static void main(String[] args) throws Exception {
		MavenClassLoaderTest.setup();
		MavenClassLoaderTest.findClass();
		MavenClassLoaderTest.mkdirClass();
	}
}
