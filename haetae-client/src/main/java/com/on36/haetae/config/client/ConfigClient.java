package com.on36.haetae.config.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.on36.haetae.common.conf.Configuration;
import com.on36.haetae.common.log.Logger;
import com.on36.haetae.common.log.LoggerFactory;
import com.on36.haetae.config.client.json.util.JSONUtils;

/**
 * 分布式配置代理客户端访问类
 * 
 * @author zhanghr
 * @date 2016年4月14日
 */
public class ConfigClient {

	private static Configuration config = Configuration.create();
	private static Logger LOG = LoggerFactory.getLogger(ConfigClient.class);

	private static String getURI(String path) {
		StringBuilder sb = new StringBuilder();
		sb.append("http://localhost:1025/cluster");
		sb.append(path);
		return sb.toString();
	}

	public static Configuration config() {
		return config;
	}

	/**
	 * 设置key-value键值对配置.
	 * 
	 * @param key
	 *            属性名
	 * @param value
	 *            属性值
	 * @author zhanghr
	 */
	public static boolean set(String key, String value) {
		if (key == null || value == null)
			return false;
		Map<String, String> map = new HashMap<String, String>();
		map.put(key, value);
		return set(map);
	}

	/**
	 * 批量设置key-value键值对配置.
	 * 
	 * @param map
	 *            存储key-value键值对的map
	 * @author zhanghr
	 */
	public static boolean set(Map<String, String> map) {
		if (map == null || map.isEmpty())
			return false;
		try {
			HttpClient.getInstance().post(getURI("/property/set"), map);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 根据key获得值， 如果没有找到，则使用默认值.
	 * 
	 * @param key
	 * @param defaultValue
	 *            默认值
	 * @return
	 * @author zhanghr
	 */
	public static String get(String key, String defaultValue) {
		String value = get(key);
		return value == null ? defaultValue : value;
	}

	/**
	 * 根据key获得值.
	 * 
	 * @param key
	 * @return
	 * @author zhanghr
	 */
	public static String get(String key) {
		if (key == null)
			return null;
		try {
			String result = HttpClient.getInstance()
					.get(getURI("/property/get?key=" + key));
			return result;
		} catch (Exception e) {
			LOG.warn(
					"It will get data from default resource file [haetae-default.conf,haetae.conf], cause by "
							+ e.getMessage());
		}
		return config.getString(key);
	}

	/**
	 * 根据key获得int值， 如果没有找到，则使用默认值.
	 * 
	 * @param key
	 * @param defaultValue
	 *            默认值
	 * @return
	 * @author zhanghr
	 */
	public static int getInt(String key, int defaultValue) {
		Integer value = getInt(key);
		return value == null ? defaultValue : value;
	}

	/**
	 * 根据key获得int值.
	 * 
	 * @param key
	 * @return
	 * @author zhanghr
	 */
	public static Integer getInt(String key) {
		String value = get(key);
		return value == null ? null : Integer.parseInt(value);
	}

	/**
	 * 根据key获得long值， 如果没有找到，则使用默认值.
	 * 
	 * @param key
	 * @param defaultValue
	 *            默认值
	 * @return
	 * @author zhanghr
	 */
	public static long getLong(String key, long defaultValue) {
		Long value = getLong(key);
		return value == null ? defaultValue : value;
	}

	/**
	 * 根据key获得long值.
	 * 
	 * @param key
	 * @return
	 * @author zhanghr
	 */
	public static Long getLong(String key) {
		String value = get(key);
		return value == null ? null : Long.parseLong(value);
	}

	/**
	 * 根据key获得boolean值， 如果没有找到，则使用默认值.
	 * 
	 * @param key
	 * @param defaultValue
	 *            默认值
	 * @return
	 * @author zhanghr
	 */
	public static boolean getBoolean(String key, boolean defaultValue) {
		Boolean value = getBoolean(key);
		return value == null ? defaultValue : value;
	}

	/**
	 * 根据key获得boolean值.
	 * 
	 * @param key
	 * @return
	 * @author zhanghr
	 */
	public static Boolean getBoolean(String key) {
		String value = get(key);
		return value == null ? null : Boolean.parseBoolean(value);
	}

	/**
	 * 根据key获得值.
	 * 
	 * @param key
	 * @return
	 * @author zhanghr
	 */
	public static List<String> getList(String route) {
		if (route == null)
			return null;
		try {
			String result = HttpClient.getInstance()
					.get(getURI("/service?route=" + route));
			if (result == null)
				return JSONUtils.fromJsonToList(String.class, result);
			else {
				System.out
						.println(JSONUtils.get(String.class, result, "result"));
			}
		} catch (Exception e) {
			LOG.warn(
					"It will get data from default resource file [haetae-default.conf,haetae.conf]",
					e);
		}
		return config.getStringList(route);
	}

}
