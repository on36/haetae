package com.on36.haetae.hsr.rpc;

import java.util.Random;

/**
 * @author zhanghr
 * @date 2016年12月20日
 */
public class HashUtils {
	public static final int TEST = 1000000;

	public static int additiveHash(String key) {
		int n = key.length();
		int hash = n;
		for (int i = 0; i < n; i++)
			hash += key.charAt(i);
		return hash ^ (hash >> 10) ^ (hash >> 20);
	}

	public static int rotatingHash(String key) {
		int n = key.length();
		int hash = n;
		for (int i = 0; i < n; i++)
			hash = (hash << 4) ^ (hash >> 28) ^ key.charAt(i);
		return (hash & 0x7FFFFFFF);
	}

	public static int BKDRHash(String key) {
		int prime = 13131;// 31 131 1313 13131 131313 etc..
		int hash = 0;
		int n = key.length();
		for (int i = 0; i < n; ++i)
			hash = prime * hash + key.charAt(i);
		return (hash & 0x7FFFFFFF);
	}

	public static int FNVHash(String key) {
		final int p = 16777619;
		int hash = (int) 2166136261L;
		int n = key.length();
		for (int i = 0; i < n; i++)
			hash = (hash ^ key.charAt(i)) * p;
		hash += hash << 13;
		hash ^= hash >> 7;
		hash += hash << 3;
		hash ^= hash >> 17;
		hash += hash << 5;
		return (hash & 0x7FFFFFFF);
	}

	public static int RSHash(String key) {
		int b = 378551;
		int a = 63689;
		int hash = 0;
		int n = key.length();
		for (int i = 0; i < n; i++) {
			hash = hash * a + key.charAt(i);
			a = a * b;
		}
		return (hash & 0x7FFFFFFF);
	}

	public static int JSHash(String key) {
		int hash = 1315423911;
		int n = key.length();
		for (int i = 0; i < n; i++) {
			hash ^= ((hash << 5) + key.charAt(i) + (hash >> 2));
		}
		return (hash & 0x7FFFFFFF);
	}

	// P. J. Weinberger Hash Function
	public static int PJWHash(String key) {
		int BitsInUnignedInt = 32;
		int ThreeQuarters = 24;
		int OneEighth = 4;
		int HighBits = (0xFFFFFFFF) << (BitsInUnignedInt - OneEighth);
		int hash = 0;
		int test = 0;
		int n = key.length();
		for (int i = 0; i < n; i++) {
			hash = (hash << OneEighth) + key.charAt(i);
			if ((test = hash & HighBits) != 0) {
				hash = ((hash ^ (test >> ThreeQuarters)) & (~HighBits));
			}
		}
		return (hash & 0x7FFFFFFF);
	}

	public static int ELFhash(String key) {
		int h = 0;
		int n = key.length();
		for (int i = 0; i < n; i++) {
			h = (h << 4) + key.charAt(i);
			long g = h & 0Xf0000000L;
			if (g != 0) {
				h ^= g >> 24;
				h &= ~g;
			}
		}
		return (h & 0x7FFFFFFF);
	}

	// SDBM Hash Function
	public static int SDBMHash(String key) {
		int hash = 0;
		int n = key.length();
		for (int i = 0; i < n; i++) {
			hash = key.charAt(i) + (hash << 6) + (hash << 16) - hash;
		}
		return (hash & 0x7FFFFFFF);
	}

	// DJB Hash Function
	public static int DJBHash(String key) {
		int hash = 5381;
		int n = key.length();
		for (int i = 0; i < n; i++) {
			hash += (hash << 5) + key.charAt(i);
		}
		return (hash & 0x7FFFFFFF);
	}

	// AP Hash Function
	public static int APHash(String key) {
		int hash = 0;
		int n = key.length();
		for (int i = 0; i < n; i++) {
			if ((i & 1) == 0) {
				hash ^= ((hash << 7) ^ key.charAt(i) ^ (hash >> 3));
			} else {
				hash ^= (~((hash << 11) ^ key.charAt(i) ^ (hash >> 5)));
			}
		}
		return (hash & 0x7FFFFFFF);
	}

	public static int hash(String key) {
		int hash = 0;
		int n = key.length();
		for (int i = 0; i < n; i++) {
			if ((i & 1) == 0) {
				hash ^= ((hash << 7) ^ key.charAt(i) ^ (hash >> 3));
			} else {
				hash ^= (~((hash << 11) ^ key.charAt(i) ^ (hash >> 5)));
			}
		}
		int result = (hash & 0x7FFFFFFF) % 0x64;
		if (result >= 10)
			return result;
		else
			return result + key.toUpperCase().charAt(0);
	}

	static Random random = new Random();

	public static String getStringByBytes(int length) {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFJKLMNOPQRSTUVWXYZ-_+{}#@!$%^&*()~";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		// int i = TEST;
		// long start = System.currentTimeMillis();
		// Map<Integer, String> map = new HashMap<>();
		// while (i-- > 0) {
		// int len = (int) (20 + Math.random() * 100);
		// map.put(BKDRHash(getStringByBytes(len)), null);
		// }
		// long opsPerSecond = (TEST * 1000L)
		// / (System.currentTimeMillis() - start);
		// System.out.println("BKDRHash: " + opsPerSecond + " ops/sec, size=" +
		// (TEST-map.size()));
		//
		// i = TEST;
		// start = System.currentTimeMillis();
		// map = new HashMap<>();
		// while (i-- > 0) {
		// int len = (int) (20 + Math.random() * 100);
		// map.put(APHash(getStringByBytes(len)), null);
		// }
		// opsPerSecond = (TEST * 1000L)
		// / (System.currentTimeMillis() - start);
		// System.out.println("APHash: " + opsPerSecond + " ops/sec, size=" +
		// (TEST-map.size()));
		//
		// i = TEST;
		// start = System.currentTimeMillis();
		// map = new HashMap<>();
		// while (i-- > 0) {
		// int len = (int) (20 + Math.random() * 100);
		// map.put(JSHash(getStringByBytes(len)), null);
		// }
		// opsPerSecond = (TEST * 1000L)
		// / (System.currentTimeMillis() - start);
		// System.out.println("JSHash: " + opsPerSecond + " ops/sec, size=" +
		// (TEST-map.size()));
		//
		// i = TEST;
		// start = System.currentTimeMillis();
		// map = new HashMap<>();
		// while (i-- > 0) {
		// int len = (int) (20 + Math.random() * 100);
		// map.put(DJBHash(getStringByBytes(len)), null);
		// }
		// opsPerSecond = (TEST * 1000L)
		// / (System.currentTimeMillis() - start);
		// System.out.println("DJBHash: " + opsPerSecond + " ops/sec, size=" +
		// (TEST-map.size()));
		//
		// i = TEST;
		// start = System.currentTimeMillis();
		// map = new HashMap<>();
		// while (i-- > 0) {
		// int len = (int) (20 + Math.random() * 100);
		// map.put(ELFhash(getStringByBytes(len)), null);
		// }
		// opsPerSecond = (TEST * 1000L)
		// / (System.currentTimeMillis() - start);
		// System.out.println("ELFhash: " + opsPerSecond + " ops/sec, size=" +
		// (TEST-map.size()));
		//
		// i = TEST;
		// start = System.currentTimeMillis();
		// map = new HashMap<>();
		// while (i-- > 0) {
		// int len = (int) (20 + Math.random() * 100);
		// map.put(RSHash(getStringByBytes(len)), null);
		// }
		// opsPerSecond = (TEST * 1000L)
		// / (System.currentTimeMillis() - start);
		// System.out.println("RSHash: " + opsPerSecond + " ops/sec, size=" +
		// (TEST-map.size()));
		//
		// i = TEST;
		// start = System.currentTimeMillis();
		// map = new HashMap<>();
		// while (i-- > 0) {
		// int len = (int) (20 + Math.random() * 100);
		// map.put(SDBMHash(getStringByBytes(len)), null);
		// }
		// opsPerSecond = (TEST * 1000L)
		// / (System.currentTimeMillis() - start);
		// System.out.println("SDBMHash: " + opsPerSecond + " ops/sec, size=" +
		// (TEST-map.size()));
		//
		// i = TEST;
		// start = System.currentTimeMillis();
		// map = new HashMap<>();
		// while (i-- > 0) {
		// int len = (int) (20 + Math.random() * 100);
		// map.put(PJWHash(getStringByBytes(len)), null);
		// }
		// opsPerSecond = (TEST * 1000L)
		// / (System.currentTimeMillis() - start);
		// System.out.println("PJWHash: " + opsPerSecond + " ops/sec, size=" +
		// (TEST-map.size()));
		//
		// i = TEST;
		// start = System.currentTimeMillis();
		// map = new HashMap<>();
		// while (i-- > 0) {
		// int len = (int) (20 + Math.random() * 100);
		// map.put(FNVHash(getStringByBytes(len)), null);
		// }
		// opsPerSecond = (TEST * 1000L)
		// / (System.currentTimeMillis() - start);
		// System.out.println("FNVHash: " + opsPerSecond + " ops/sec, size=" +
		// (TEST-map.size()));

		System.out.println(APHash("taobao"));
		System.out.println(APHash("tmail"));
		System.out.println(APHash("jindong"));
		System.out.println(APHash("yihaodian"));
		System.out.println(APHash("ebay"));
		System.out.println(APHash("weixin"));
		System.out.println(APHash("zhifubao"));
		System.out.println(APHash("koubei"));
		System.out.println(APHash("elme"));
		System.out.println(hash("taobao"));
		System.out.println(hash("tmail"));
		System.out.println(hash("jindong"));
		System.out.println(hash("yihaodian"));
		System.out.println(hash("ebay"));
		System.out.println(hash("weixin"));
		System.out.println(hash("zhifubao"));
		System.out.println(hash("koubei"));
		System.out.println(hash("elme"));
	}
}
