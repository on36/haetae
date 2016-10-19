package com.on36.haetae.manager;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.on36.haetae.config.client.HttpClient;

/**
 * @author zhanghr
 * @date 2016年6月14日 
 */
public class ConfigAgentServiceTest {

	@Test
	public void testSet() throws Exception {
		Map<String, String> map = new HashMap<String,String>();
		map.put("mysql.usernam", "123");
		String result = HttpClient.getInstance().put("http://localhost:1025/cluster/property", map);
		System.out.println(result);
	}
	@Test
	public void testGet() throws Exception {
		Map<String, String> map = new HashMap<String,String>();
		map.put("key", "mysql.usernam");
		String result = HttpClient.getInstance().get("http://localhost:1025/cluster/property",map);
		System.out.println(result);
	}
}
