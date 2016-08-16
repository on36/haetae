package com.on36.haetae.config.client;

import java.util.List;
import java.util.Map;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;

import com.on36.haetae.common.utils.JSONUtils;

/**
 * 分布式配置代理客户端访问类
 * 
 * @author zhanghr
 * @date 2016年4月14日
 */
public class ConfigClient {
	private static AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();

	private static String getURI(String path) {
		StringBuilder sb = new StringBuilder();
		sb.append("http://localhost:1984/_config");
		sb.append(path);
		return sb.toString();
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
		try {
			Response resp = asyncHttpClient.preparePost(getURI("/property/set"))
					.addQueryParam(key, value).execute().get();
			if (resp.getStatusCode() == 200)
				return true;
			else
				System.out.println(resp.getResponseBody().trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
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
			BoundRequestBuilder request = asyncHttpClient
					.preparePost(getURI("/property/set"));
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				request.addQueryParam(key, value);
			}
			Response resp = request.execute().get();
			if (resp.getStatusCode() == 200)
				return true;
			else
				System.out.println(resp.getResponseBody().trim());
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
			Response resp = asyncHttpClient.prepareGet(getURI("/property/get"))
					.addQueryParam("key", key).execute().get();
			if (resp.getStatusCode() == 200)
				return resp.getResponseBody().trim();
			else
				System.out.println(resp.getResponseBody().trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
			Response resp = asyncHttpClient.prepareGet(getURI("/service/get"))
					.addQueryParam("route", route).execute().get();
			if (resp.getStatusCode() == 200)
				return JSONUtils.fromJsonToList(String.class,
						resp.getResponseBody().trim());
			else
				System.out.println(resp.getResponseBody().trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 测试一个服务地址
	 * @param address
	 * @return
	 */
	public static boolean host(String address) {
		if (address == null)
			return false;
		try {
			Response resp = asyncHttpClient.preparePost(getURI("/host"))
					.addQueryParam("address", address).execute().get();
			if (resp.getStatusCode() == 200)
				return true;
			else
				System.out.println(resp.getResponseBody().trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
