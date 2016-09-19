package com.on36.haetae.common.cache;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author zhanghr
 * @date 2016年5月13日
 */
public class CacheMap<K, V> {
	LoadingCache<K, V> loadingCache = null;

	public CacheMap() {
		this(8, 30, -1, 10, 200);
	}

	public CacheMap(long rexprieTime) {
		this(8, 30, rexprieTime, 10, 200);
	}

	public CacheMap(long wexprieTime, long rexprieTime) {
		this(8, wexprieTime, rexprieTime, 10, 200);
	}

	public CacheMap(long wexprieTime, long rexprieTime, int maxCapacity) {
		this(8, wexprieTime, rexprieTime, 10, maxCapacity);
	}

	/**
	 * 
	 * @param level
	 *            设置并发级别
	 * @param wexprieTime
	 *            设置写缓存后过期时间，单位为分钟 当值为-1时，写缓存后不过期，以读过期为主
	 * @param rexprieTime
	 *            设置读缓存后过期时间，单位为分钟 当值为-1时，读缓存后不过期，以写过期为主
	 * @param initialCapacity
	 *            设置缓存容器的初始容量
	 * @param maxCapacity
	 *            设置缓存最大容量
	 */
	public CacheMap(int level, long wexprieTime, long rexprieTime,
			int initialCapacity, int maxCapacity) {
		CacheBuilder<Object, Object> cachebuilder = CacheBuilder.newBuilder()
				// 设置并发级别为8，并发级别是指可以同时写缓存的线程数
				.concurrencyLevel(level)
				// 设置缓存容器的初始容量为10
				.initialCapacity(initialCapacity).recordStats()
				// 设置缓存最大容量为100，超过100之后就会按照LRU算法来移除缓存项
				.maximumSize(maxCapacity);
		if (wexprieTime > 0) // 设置写缓存后过期
			cachebuilder.expireAfterWrite(wexprieTime, TimeUnit.MINUTES);
		if (rexprieTime > 0) // 设置读缓存后过期
			cachebuilder.expireAfterAccess(rexprieTime, TimeUnit.MINUTES);

		loadingCache = cachebuilder.build(new CacheLoader<K, V>() {
			@Override
			public V load(K key) throws Exception {
				return null;
			}
		});
	}

	public void put(K key, V value) {
		loadingCache.put(key, value);
	}

	public void remove(K key) {
		loadingCache.invalidate(key);
	}

	public V get(K key) {
		try {
			return loadingCache.getUnchecked(key);
		} catch (Exception e) {
		}
		return null;
	}

	public long size() {
		return loadingCache.size();
	}

	public boolean containsKey(K key) {
		return get(key) != null;
	}

	// public Set<Map.Entry<K, V>> entrySet() {
	// return loadingCache.asMap().entrySet();
	// }
}
