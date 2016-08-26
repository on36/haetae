package com.on36.haetae.server.core.container;

import java.util.Comparator;

/**
 * @author zhanghr
 * @date 2016年1月23日
 */
public class VersionComparator implements Comparator<String> {

	@Override
	public int compare(String version1, String version2) {
		if (version1.equals(version2)) {
			return 0;
		}

		String[] version1Array = version1.split("\\.");
		String[] version2Array = version2.split("\\.");

		int index = 0;
		int minLen = Math.min(version1Array.length, version2Array.length);
		int diff = 0;

		while (index < minLen && (diff = Integer.parseInt(version1Array[index])
				- Integer.parseInt(version2Array[index])) == 0) {
			index++;
		}

		if (diff == 0) {
			for (int i = index; i < version1Array.length; i++) {
				if (Integer.parseInt(version1Array[i]) > 0) {
					return 1;
				}
			}

			for (int i = index; i < version2Array.length; i++) {
				if (Integer.parseInt(version2Array[i]) > 0) {
					return -1;
				}
			}

			return 0;
		} else {
			return diff > 0 ? 1 : -1;
		}
	}
}
