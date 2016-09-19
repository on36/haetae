package org.haetae.common.cache;

import org.junit.Test;

import com.on36.haetae.common.cache.CacheMap;

/**
 * @author zhanghr
 * @date 2016年5月13日
 */
public class CacheMapTest {

	@Test
	public void test() throws Exception {
		CacheMap<String, String> cache = new CacheMap<String, String>();
		cache.put("nihao", "shijie");
		Thread.sleep(10000);
		cache.put("hello", "world");
		int i = 0;
		while (true) {
			System.out.println(i++);
			System.out.println("nihao = " + cache.get("nihao"));
			System.out.println("hello = " + cache.get("hello"));
			System.out.println("meyou = " + cache.get("meiyou"));
			Thread.sleep(5000);
		}
	}
}
