package com.on36.haetae.manager;

import java.util.List;
import java.util.Map;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.data.Stat;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Get;
import com.on36.haetae.api.annotation.Post;
import com.on36.haetae.common.conf.Constant;
import com.on36.haetae.common.zk.ZKClient;
import com.on36.haetae.config.client.ConfigClient;

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
		db = DBMaker.newMemoryDirectDB().transactionDisable()
				.compressionEnable().closeOnJvmShutdown().make();
		propMap = db.createTreeMap(app + "/property").make();
		servicesMap = db.createTreeMap(app + "/services").make();

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
	}

	private DB db;
	private BTreeMap<String, String> propMap;
	private BTreeMap<String, List<String>> servicesMap;

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

	@Post("/property/set")
	public void setProperty(Context context) throws Exception {
		Map<String, String> map = context.getRequestParameters();

		if (map == null || map.isEmpty())
			throw new Exception("Request parameters should not be null");

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

	@Get("/property/get")
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
			throw new Exception("key should not be null");
	}

	@Get("/service/get")
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
			throw new Exception("route should not be null");
	}

	@Post("/service/register")
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
			throw new Exception("address should not be null");
	}

	@Post("/node/register")
	public void registerNode(Context context) throws Exception {
		String address = context.getRequestParameter("address");
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
			throw new Exception("address should not be null");
	}

	@Post("/node/unregister")
	public void unregisterNode(Context context) throws Exception {
		String address = context.getRequestParameter("address");
		if (address != null) {
			String path = app + "/nodes" + checkPrefix(address);
			client.delete(path);
		} else
			throw new Exception("address should not be null");
	}
}
