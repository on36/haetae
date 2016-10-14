package com.on36.haetae.manager;

import java.util.List;
import java.util.Map;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.data.Stat;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Api;
import com.on36.haetae.api.http.MethodType;
import com.on36.haetae.common.cache.CacheMap;
import com.on36.haetae.common.conf.Constant;
import com.on36.haetae.common.zk.ZKClient;
import com.on36.haetae.config.client.ConfigClient;
import com.on36.haetae.server.core.manager.EndPointManager;
import com.on36.haetae.server.core.manager.listener.ActiveListener;
import com.on36.haetae.server.core.manager.listener.InactiveListener;

/**
 * @author zhanghr
 * @date 2016年4月12日
 */
public class ConfigAgentService {

	private String app = ConfigClient.get(Constant.K_SERVER_APP_NAME,
			Constant.V_SERVER_APP_NAME);;
	private String connectString = ConfigClient.get(
			Constant.K_ZOOKEEPER_ADDRESS_URL, Constant.V_ZOOKEEPER_ADDRESS_URL);
	private ZKClient client;

	public ConfigAgentService() {
		propMap = new CacheMap<String, String>(-1, 30, 100);
		servicesMap = new CacheMap<String, List<String>>(-1, 30, 100);

		client = new ZKClient(connectString) {
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
			public void handler(String... values) {
				String root = values[0];
				String endpoint = values[1];
				String pid = values[2];
				String path = app + "/nodes" + root + checkPrefix(endpoint);

				try {
					Stat stat = client.exists(path, false);
					if (stat == null)
						client.createEphemeral(path, pid, true);
					else if (stat.getEphemeralOwner() != client
							.getSesssionId()) {
						client.delete(path);
						client.createEphemeral(path, pid, true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).addInactiveListener(new InactiveListener() {

			@Override
			public void handler(String... values) {
				String root = values[0];
				String endpoint = values[1];
				String path = app + "/nodes" + root + checkPrefix(endpoint);
				try {
					if (client.exists(path))
						client.delete(path);
				} catch (Exception e) {
					e.printStackTrace();
				}
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

	@Api(value = "/property", method = MethodType.POST)
	public void changeProperty(Context context) throws Exception {
		Map<String, String> map = context.getRequestParameters();

		if (map == null || map.isEmpty())
			throw new IllegalArgumentException(
					"Request parameters should not be null");

		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			String path = app + "/property" + checkPrefix(key);
			if (!client.exists(path))
				client.create(path, value, true);
			else
				client.setData(path, value);
		}
	}

	@Api(value = "/property", method = MethodType.PUT)
	public void addProperty(Context context) throws Exception {
		Map<String, String> map = context.getRequestParameters();

		if (map == null || map.isEmpty())
			throw new IllegalArgumentException(
					"Request parameters should not be null");

		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String path = app + "/property" + checkPrefix(key);
			if (client.exists(path))
				throw new Exception(
						"Property [" + key + "] is already existed!");
		}
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			String path = app + "/property" + checkPrefix(key);
			client.create(path, value, true);
		}
	}

	@Api("/property")
	public String getProperty(Context context) throws Exception {
		String key = context.getRequestParameter("key");
		if (key != null) {
			String path = app + "/property" + checkPrefix(key);
			String result = propMap.get(path);
			if (result == null) {
				result = client.getData2String(path, true);
				propMap.put(path, result);
			}
			return result;
		} else
			throw new IllegalArgumentException("key should not be null");
	}

	@Api("/property/:key")
	public String getPropertyByURI(Context context) throws Exception {
		String key = context.getCapturedParameter(":key");
		if (key != null) {
			String path = app + "/property" + checkPrefix(key);
			String result = propMap.get(path);
			if (result == null) {
				result = client.getData2String(path, true);
				propMap.put(path, result);
			}
			return result;
		} else
			throw new IllegalArgumentException("key should not be null");
	}

	@Api(value = "/property/:key", method = MethodType.DELETE)
	public void deleteProperty(Context context) throws Exception {
		String key = context.getCapturedParameter(":key");
		if (key != null) {
			String path = app + "/property" + checkPrefix(key);
			if (client.exists(path))
				client.delete(path);
			else
				throw new Exception("Property [" + key + "] is not existed!");
		} else
			throw new IllegalArgumentException("key should not be null");
	}

	@Api("/service")
	public List<String> getServices(Context context) throws Exception {
		String route = context.getRequestParameter("route");
		if (route != null) {
			String path = app + "/services" + checkPrefix(route);
			List<String> result = servicesMap.get(path);
			if (result == null) {
				result = client.getChildren(path, true);
				servicesMap.put(path, result);
			}
			return result;
		} else
			throw new IllegalArgumentException("route should not be null");
	}

	@Api(value = "/service", method = MethodType.PUT)
	public void putServices(Context context) throws Exception {
		String address = context.getRequestParameter("address");
		if (address != null) {
			String path = app + "/services" + checkPrefix(address);
			if (!client.exists(path))
				client.createEphemeral(path, true);
			else
				throw new Exception(
						"Node [" + address + "] is already existed!");
		} else
			throw new IllegalArgumentException("address should not be null");
	}

	@Api(value = "/service", method = MethodType.POST)
	public void registerServices(Context context) throws Exception {
		String address = context.getRequestParameter("address");
		if (address != null) {
			String path = app + "/services" + checkPrefix(address);
			Stat stat = client.exists(path, false);
			if (stat == null)
				client.createEphemeral(path, true);
			else if (stat.getEphemeralOwner() != client.getSesssionId()) {
				client.delete(path);
				client.createEphemeral(path, true);
			}
		} else
			throw new IllegalArgumentException("address should not be null");
	}

	@Api(value = "/node", method = MethodType.PUT)
	public void putNode(Context context) throws Exception {
		String address = context.getRequestParameter("node");
		String data = context.getRequestParameter("data");
		if (address != null) {
			String path = app + "/nodes" + checkPrefix(address);
			if (!client.exists(path))
				client.createEphemeral(path, data, true);
			else
				throw new Exception(
						"Node [" + address + "] is already existed!");
		} else
			throw new IllegalArgumentException("node should not be null");
	}

	@Api(value = "/node", method = MethodType.POST)
	public void postNode(Context context) throws Exception {
		String address = context.getRequestParameter("node");
		String data = context.getRequestParameter("data");
		if (address != null) {
			String path = app + "/nodes" + checkPrefix(address);
			Stat stat = client.exists(path, false);
			if (stat == null)
				client.createEphemeral(path, data, true);
			else if (stat.getEphemeralOwner() != client.getSesssionId()) {
				client.delete(path);
				client.createEphemeral(path, data, true);
			}
		} else
			throw new IllegalArgumentException("node should not be null");
	}

	@Api(value = "/node", method = MethodType.DELETE)
	public void unregisterNode(Context context) throws Exception {
		String address = context.getRequestParameter("node");
		if (address != null) {
			String path = app + "/nodes" + checkPrefix(address);
			if (client.exists(path))
				client.delete(path);
			else
				throw new Exception("Node [" + address + "] is not existed!");
		} else
			throw new IllegalArgumentException("node should not be null");
	}

	@Api(value = "/node/:node", method = MethodType.DELETE)
	public void deleteNode(Context context) throws Exception {
		String address = context.getCapturedParameter(":node");
		if (address != null) {
			String path = app + "/nodes" + checkPrefix(address);
			if (client.exists(path))
				client.delete(path);
			else
				throw new Exception("Node [" + address + "] is not existed!");
		} else
			throw new IllegalArgumentException("node should not be null");
	}

	@Api("/node")
	public List<String> getNodes(Context context) throws Exception {
		String path = app + "/nodes";
		if (client.exists(path))
			return client.getChildren(path);
		else
			throw new Exception("Node [" + app + "] is not existed!");
	}

	@Api("/node/:node")
	public int getNode(Context context) throws Exception {
		String address = context.getCapturedParameter(":node");
		if (address != null) {
			String path = app + "/nodes" + checkPrefix(address);
			if (client.exists(path))
				return client.getData2Integer(path);
			else
				throw new Exception("Node [" + address + "] is not existed!");
		} else
			throw new IllegalArgumentException("node should not be null");
	}
}
