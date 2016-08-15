package com.on36.haetae.config.client;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ConfigClientTest {

	@Test
	public void testSet() {
		boolean result = ConfigClient.set("nihao", "world");
		assertEquals(true, result);
	}

	@Test
	public void testSets() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("redis.port", "6379");
		map.put("redis.threads.pool", "24");
		map.put("redis.proxy.type", "MIX");
		boolean result = ConfigClient.set(map);
		assertEquals(true, result);
	}

	@Test
	public void testGet() {
		String result = ConfigClient.get("nihao");
		assertEquals("world", result);
	}
	@Test
	public void testList() {
		List<String> result = ConfigClient.getList("/user/list/:name<[A-Za-z]+>");
		assertEquals("10.4.123.34:8080", result.get(1));
	}
}
