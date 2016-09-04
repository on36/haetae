package org.haetae.common.conf;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.on36.haetae.common.conf.Configuration;

/**
 * @author zhanghr
 * @date 2016年3月22日
 */
public class ConfigurationTest {

	private Configuration config = Configuration.create();

	@Test
	public void testString() {
		String value = config.getString("nihao.http", "default");
		assertEquals("default", value);
	}
	@Test
	public void testBoolean() {
		boolean value = config.getBoolean("httpServer.ssl.enabled", true);
		assertEquals(false, value);
	}
	@Test
	public void testInt() {
		int value = config.getInt("httpServer.sobacklog", 512);
		assertEquals(1024, value);
	}
	@Test
	public void testIntList() {
		List<Integer> value = config.getIntList("httpServer.ports");
		System.out.println(value);
		assertEquals(1024, value);
	}
	@Test
	public void testStringList() {
		List<String> value = config.getStringList("httpServer.hosts");
		System.out.println(value);
		assertEquals(1024, value);
	}
}
