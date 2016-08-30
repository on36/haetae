package com.on36.haetae.common.utils;

/**
 * @author zhanghr
 * @date 2016年1月30日
 */
public class StringUtils {

	public static boolean isEmpty(String value) {
		if (value == null)
			return true;
		else if (value.trim().length() == 0)
			return true;
		else if ("null".equals(value.toLowerCase()))
			return true;
		else
			return false;
	}
}
