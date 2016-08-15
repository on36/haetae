package com.on36.haetae.config.agent;

import java.util.List;
import java.util.Map;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import com.on36.haeate.common.zk.ZKClient;
import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Get;
import com.on36.haetae.api.annotation.Post;

/**
 * @author zhanghr
 * @date 2016年4月12日
 */
public class ConfigAgentService {

	private String app = "/apps/crm";
	private ZKClient client;

	public ConfigAgentService() {
		db = DBMaker.newMemoryDirectDB().transactionDisable()
				.compressionEnable().closeOnJvmShutdown().make();
		propMap = db.createTreeMap(app + "/property").make();
		servicesMap = db.createTreeMap(app + "/services").make();
		
		client = new ZKClient("localhost:2181", null) {
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

	private void nodeDeleted(String path) {
		propMap.remove(path);
		servicesMap.remove(path);
	}

	private void nodeDataChanged(String path) {
		try {
			String value = client.getData2String(path);
			propMap.put(path, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void nodeChildrenChanged(String path) {
		try {
			List<String> value = client.getChildren(path);
			servicesMap.put(path, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Post("/property/set")
	public void setProperty(Context context) throws Exception {
		Map<String, String> map = context.getRequestParameters();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			String path = app + "/property/" + key;
			if (!client.exists(path))
				client.create(path, value, true);
			else
				client.setData(path, value);
		}
	}

	@Get("/property/get")
	public String getProperty(Context context) throws Exception {
		String key = context.getRequestParameter("key");
		String path = app + "/property/" + key;
		String result = propMap.get(path);
		if (result == null) {
			result = client.getData2String(path, true);
			propMap.put(path, result);
		}
		return result;
	}

	@Get("/service/get")
	public List<String> getServices(Context context) throws Exception {
		String route = context.getRequestParameter("route");
		String path = app + "/services" + route;
		List<String> result = servicesMap.get(path);
		if (result == null) {
			result = client.getChildren(path, true);
			servicesMap.put(path, result);
		}
		return result;
	}
}
