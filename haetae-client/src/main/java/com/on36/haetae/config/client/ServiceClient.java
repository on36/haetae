package com.on36.haetae.config.client;

import java.util.HashMap;

/**
 * @author zhanghr
 * @date 2016年5月13日
 */
public class ServiceClient {

	private static String getURI(String path) {
		if (path != null && path.startsWith("http"))
			return path;
		// List<String> address = ConfigClient.getList("serice");
		StringBuilder sb = new StringBuilder();
		sb.append("http://localhost:1025/cluster");
		sb.append(path);
		return sb.toString();
	}

	/**
	 * 注册一个服务地址
	 * 
	 * @param address
	 * @return
	 */
	public static boolean registerService(String address) {
		if (address == null)
			return false;
		try {
			HttpClient.getInstance().post(getURI("/service/register"),
					new QueryPart("address", address));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 注册一个cluster manager
	 * 
	 * @param address
	 * @return
	 */
	public static boolean registerNode(String address, String data)
			throws Exception {
		if (address == null)
			return false;
		HttpClient.getInstance().post(getURI("/node"),
				new QueryPart("node", address).addQueryPart("data", data));
		return true;
	}

	/**
	 * 注册一个cluster manager
	 * 
	 * @param address
	 * @return
	 */
	public static boolean unregisterNode(String address) {
		if (address == null)
			return false;
		try {
			HttpClient.getInstance().delete(getURI("/node"),
					new QueryPart("node", address));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	static class QueryPart extends HashMap<String, String> {

		private static final long serialVersionUID = 1L;

		public QueryPart(String key, String value) {
			put(key, value);
		}

		public QueryPart addQueryPart(String key, String value) {
			put(key, value);
			return this;
		}
	}
}
