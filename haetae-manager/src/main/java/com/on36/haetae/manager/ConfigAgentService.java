package com.on36.haetae.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.data.Stat;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Api;
import com.on36.haetae.api.annotation.ApiDoc;
import com.on36.haetae.api.annotation.ApiParam;
import com.on36.haetae.api.http.MediaType;
import com.on36.haetae.api.http.MethodType;
import com.on36.haetae.api.http.ParamType;
import com.on36.haetae.common.cache.CacheMap;
import com.on36.haetae.common.conf.Configuration;
import com.on36.haetae.common.conf.Constant;
import com.on36.haetae.common.zk.ZKClient;
import com.on36.haetae.config.client.HttpClient;
import com.on36.haetae.server.core.manager.EndPointManager;
import com.on36.haetae.server.core.manager.listener.ActiveListener;
import com.on36.haetae.server.core.manager.listener.InactiveListener;

/**
 * @author zhanghr
 * @date 2016年4月12日
 */
public class ConfigAgentService {

	private static Configuration config = Configuration.create();
	private String apps = String.format("/apps/%s",
			config.get(Constant.K_SERVER_APP_NAME, Constant.V_SERVER_APP_NAME));
	private String connectString = config.get(Constant.K_ZOOKEEPER_ADDRESS_URL,
			Constant.V_ZOOKEEPER_ADDRESS_URL);
	private boolean ssl = config.getBoolean(Constant.K_SERVER_SSL_ENABLED,
			Constant.V_SERVER_SSL_ENABLED);

	@SuppressWarnings("serial")
	private Map<String, String> header = new HashMap<String, String>() {
		{
			put("Content-Type", MediaType.TEXT_JSON.value());
		}
	};

	private Map<String, String> endpointMap = new HashMap<String, String>();

	private ZKClient client;

	public ConfigAgentService() {
		propMap = new CacheMap<String, String>(-1, 30, 100);
		servicesMap = new CacheMap<String, List<String>>(-1, 30, 100);

		client = new ZKClient(connectString, null, null) {
			@Override
			public void process(WatchedEvent event) {
				EventType type = event.getType();
				String path = event.getPath();
				if (type.equals(EventType.None)) {
					return;
				} else if (type.equals(EventType.NodeDeleted)) {
					nodeDeleted(path);
				} else if (type.equals(EventType.NodeDataChanged)) {
					nodeDataChanged(path);
				} else if (type.equals(EventType.NodeChildrenChanged)) {
					nodeChildrenChanged(path);
				}
			}
		};

		EndPointManager.getInstance().addActiveListener(new ActiveListener() {

			@Override
			public void handler(String endpoint) {
				String[] arr = endpoint.split("://");
				String path = null;
				if (arr.length == 2)
					path = "/apps/nodes" + checkPrefix(arr[0])
							+ checkPrefix(arr[1]);
				else if (arr.length == 1)
					path = "/apps/nodes" + checkPrefix(arr[0]);

				createEphemeral(path);

				registerServices(buildURL(arr[1], checkPrefix(arr[0])));
			}
		}).addInactiveListener(new InactiveListener() {

			@Override
			public void handler(String endpoint) {
				String[] arr = endpoint.split("://");
				String path = null;
				if (arr.length == 2)
					path = "/apps/nodes" + checkPrefix(arr[0])
							+ checkPrefix(arr[1]);
				else if (arr.length == 1)
					path = "/apps/nodes" + checkPrefix(arr[0]);

				delete(path);

				deleteServices(buildURL(arr[1], checkPrefix(arr[0])));
			}
		});
	}

	private CacheMap<String, String> propMap;
	private CacheMap<String, List<String>> servicesMap;

	/**
	 * ZNODE节点删除时，清空缓存数据
	 * 
	 * @param path
	 */
	private void nodeDeleted(String path) {
		propMap.remove(path);
		servicesMap.remove(path);
	}

	/**
	 * ZNODE节点数据修改时，更新缓存数据
	 * 
	 * @param path
	 */
	private void nodeDataChanged(String path) {
		try {
			String value = client.getData2String(path, true);
			propMap.put(path, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ZNODE节点的子节点变化时，更新缓存数据
	 * 
	 * @param path
	 */
	private void nodeChildrenChanged(String path) {
		try {
			List<String> value = client.getChildren(path, true);
			servicesMap.put(path, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String checkPrefix(String data) {
		if (data.startsWith("/"))
			return data;
		else
			return "/" + data;
	}

	private String buildURL(String ip, String root) {
		StringBuilder sb = new StringBuilder(ssl ? "https" : "http");
		sb.append("://");
		sb.append(ip);
		sb.append(root);
		return sb.toString();
	}

	private void registerServices(String url) {

		try {
//			String json = HttpClient.getInstance().get(url, null, header);
//			endpointMap.put(url, json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteServices(String url) {

	}

	private void createEphemeral(String path) {
		try {
			Stat stat = client.exists(path, false);
			if (stat == null)
				client.createEphemeral(path, null, true);
			else if (stat.getEphemeralOwner() != client.getSesssionId()) {
				client.delete(path);
				client.createEphemeral(path, null, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void delete(String path) {
		try {
			if (client.exists(path))
				client.delete(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Api(value = "/property/:app", method = MethodType.PUT)
	@ApiDoc(name = "指定生成对应应用的key-value属性配置", params = {
			@ApiParam(param = "app", required = true, type = ParamType.URI, desc = "应用名") })
	public void addPropertyByApp(Context context) throws Exception {
		Map<String, String> map = context.getRequestParameters();
		String app = context.getCapturedParameter(":app");

		if (map == null || map.isEmpty())
			throw new IllegalArgumentException(
					"Request parameters should not be null");

		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String path = String.format("/apps/%s", app) + "/property"
					+ checkPrefix(key);
			if (client.exists(path))
				throw new Exception(
						"Property [" + key + "] is already existed!");
		}
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			String path = String.format("/apps/%s", app) + "/property"
					+ checkPrefix(key);
			client.create(path, value, true);
		}
	}

	@Api("/property/:app")
	@ApiDoc(name = "查询指定应用的属性key的值", params = {
			@ApiParam(param = "app", required = true, type = ParamType.URI, desc = "应用名"),
			@ApiParam(param = "key", required = true, type = ParamType.REQUEST, desc = "需要查询的属性名") })
	public String getPropertyByApp(Context context) throws Exception {
		String key = context.getRequestParameter("key");
		String app = context.getCapturedParameter(":app");
		if (key != null) {
			String path = String.format("/apps/%s", app) + "/property"
					+ checkPrefix(key);
			String result = propMap.get(path);
			if (result == null) {
				result = client.getData2String(path, true);
				propMap.put(path, result);
			}
			return result;
		} else
			throw new IllegalArgumentException("app and key should not be null");
	}

	@Api("/property/:app/:key")
	@ApiDoc(name = "查询指定应用属性key的值", params = {
			@ApiParam(param = "app", required = true, type = ParamType.URI, desc = "应用名"),
			@ApiParam(param = "key", required = true, type = ParamType.URI, desc = "需要查询的属性名") })
	public String getPropertyByURIAndApp(Context context) throws Exception {
		String key = context.getCapturedParameter(":key");
		String app = context.getCapturedParameter(":app");
		if (key != null) {
			String path = String.format("/apps/%s", app) + "/property"
					+ checkPrefix(key);
			String result = propMap.get(path);
			if (result == null) {
				result = client.getData2String(path, true);
				propMap.put(path, result);
			}
			return result;
		} else
			throw new IllegalArgumentException("app and key should not be null");
	}

	@Api(value = "/property/:app/:key", method = MethodType.DELETE)
	@ApiDoc(name = "删除指定应用属性key", params = {
			@ApiParam(param = "app", required = true, type = ParamType.URI, desc = "应用名"),
			@ApiParam(param = "key", required = true, type = ParamType.URI, desc = "需要删除的属性名") })
	public void deletePropertyByURIAndApp(Context context) throws Exception {
		String key = context.getCapturedParameter(":key");
		String app = context.getCapturedParameter(":app");
		if (key != null) {
			String path = String.format("/apps/%s", app) + "/property"
					+ checkPrefix(key);
			if (client.exists(path))
				client.delete(path);
			else
				throw new Exception("Property [" + key + "] is not existed!");
		} else
			throw new IllegalArgumentException("app and key should not be null");
	}

	@Api("/service")
	public List<String> getServices(Context context) throws Exception {
		String route = context.getRequestParameter("route");
		if (route != null) {
			String path = apps + "/services" + checkPrefix(route);
			List<String> result = servicesMap.get(path);
			if (result == null) {
				result = client.getChildren(path, true);
				servicesMap.put(path, result);
			}
			return result;
		} else
			throw new IllegalArgumentException("route should not be null");
	}

	@Api("/node")
	public List<String> getNodes(Context context) throws Exception {
		String path = apps + "/nodes";
		if (client.exists(path))
			return client.getChildren(path);
		else
			throw new Exception("Node [" + apps + "] is not existed!");
	}

}
