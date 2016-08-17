package com.on36.haetae.common.conf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/***
 * 文本文件配置信息类
 * 
 * @author zhanghr
 * 
 */
public class Configuration {

	private Properties prop = null;
	private ClassLoader classLoader;

	private static class ConfigurationHolder {
		private static Configuration instance = new Configuration();
	}

	private Configuration() {

		classLoader = Configuration.class.getClassLoader();

		prop = new Properties();

		loadResource(prop, "haetae-default.conf");
	}

	public Properties getResource(String name) {
		Properties p = new Properties();
		loadResource(p, name);
		return p;
	}

	private void loadResource(Properties properties, String name) {
		try {
			properties.load(classLoader.getResourceAsStream(name));
		} catch (Exception e) {
			System.out.println("There is no found resource file of the name ["
					+ name + "]");
		}
	}

	/**
	 * 加载一个在classpath中的资源文件. 根据加载资源文件的先后顺序，新的数据将会覆盖旧的数据
	 * 
	 * @param name
	 *            资源文件名
	 * @author zhanghr
	 */
	public void addResource(String name) {
		loadResource(prop, name);
	}

	public static Configuration create() {
		return ConfigurationHolder.instance;
	}

	/**
	 * 根据key获得String值.
	 * 
	 * @param key
	 * @return
	 * @author zhanghr
	 */
	public void set(String key, String value) {
		prop.setProperty(key, value);
	}

	/**
	 * 根据key获得String值.
	 * 
	 * @param key
	 * @return
	 * @author zhanghr
	 */
	public String getString(String key) {
		return prop.getProperty(key);
	}

	/**
	 * 根据key获得String值. 如果没有找到，则使用默认值
	 * 
	 * @param key
	 * @param defaultValue
	 *            默认值
	 * @return
	 * @author zhanghr
	 */
	public String getString(String key, String defaultValue) {
		return prop.getProperty(key) == null ? defaultValue
				: prop.getProperty(key);
	}

	/**
	 * 根据key获得int值.
	 * 
	 * @param key
	 * @return
	 * @author zhanghr
	 */
	public int getInt(String key) {
		return Integer.parseInt(prop.getProperty(key));
	}

	/**
	 * 根据key获得int值. 如果没有找到，则使用默认值
	 * 
	 * @param key
	 * @param defaultValue
	 *            默认值
	 * @return
	 * @author zhanghr
	 */
	public int getInt(String key, int defaultValue) {
		return prop.getProperty(key) == null ? defaultValue
				: Integer.parseInt(prop.getProperty(key));
	}

	/**
	 * 根据key获得long值.
	 * 
	 * @param key
	 * @return
	 * @author zhanghr
	 */
	public long getLong(String key) {
		return Long.parseLong(prop.getProperty(key));
	}

	/**
	 * 根据key获得long值. 如果没有找到，则使用默认值
	 * 
	 * @param key
	 * @param defaultValue
	 *            默认值
	 * @return
	 * @author zhanghr
	 */
	public long getLong(String key, long defaultValue) {
		return prop.getProperty(key) == null ? defaultValue
				: Long.parseLong(prop.getProperty(key));
	}

	/**
	 * 根据key获得boolean值.
	 * 
	 * @param key
	 * @return
	 * @author zhanghr
	 */
	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(prop.getProperty(key));
	}

	/**
	 * 根据key获得boolean值. 如果没有找到，则使用默认值
	 * 
	 * @param key
	 * @param defaultValue
	 *            默认值
	 * @return
	 * @author zhanghr
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		return prop.getProperty(key) == null ? defaultValue
				: Boolean.parseBoolean(prop.getProperty(key));
	}

	/**
	 * 根据key获得List<String>值.
	 * 
	 * @param key
	 * @return
	 * @author zhanghr
	 */
	public List<String> getStringList(String key) {
		List<String> result = null;
		String value = getString(key);
		if (value == null)
			return result;
		result = Arrays.asList(value.split(","));
		return result;
	}

	/**
	 * 根据key获得List<Integer>值.
	 * 
	 * @param key
	 * @return
	 * @author zhanghr
	 */
	public List<Integer> getIntList(String key) {
		List<Integer> result = null;
		String value = getString(key);
		if (value == null)
			return result;
		String[] values = value.split(",");
		result = new ArrayList<Integer>(values.length);
		for (String v : values)
			result.add(Integer.parseInt(v));
		return result;
	}
}
