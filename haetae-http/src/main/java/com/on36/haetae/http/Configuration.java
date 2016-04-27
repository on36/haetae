package com.on36.haetae.http;

import java.util.List;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class Configuration {

	private static Config config = ConfigFactory.load();

	private static class ConfigurationHolder {
		private static Configuration instance = new Configuration();
	}

	private Configuration() {}

	public static Configuration create() {
		return ConfigurationHolder.instance;
	}

	public String getString(String path, String defaultValue) {
		String value = null;
		try {
			value = config.getString(path);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return value == null ? defaultValue : value;
	}

	public boolean getBoolean(String path, boolean defaultValue) {
		Boolean value = null;
		try {
			value = config.getBoolean(path);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return value == null ? defaultValue : value;
	}

	public int getInt(String path, int defaultValue) {
		Integer value = null;
		try {
			value = config.getInt(path);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return value == null ? defaultValue : value;
	}

	public long getLong(String path, long defaultValue) {
		Long value = null;
		try {
			value = config.getLong(path);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return value == null ? defaultValue : value;
	}

	public List<String> getStringList(String path) {
		return config.getStringList(path);
	}

	public List<Integer> getIntList(String path) {
		return config.getIntList(path);
	}

	public ConfigObject getObject(String path) {
		return config.getObject(path);
	}

	public List<? extends ConfigObject> getObjectList(String path) {
		return config.getObjectList(path);
	}
}
