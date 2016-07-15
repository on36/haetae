package com.on36.haetae.api;

/**
 * @author zhanghr
 * @date 2016年3月16日
 */
public interface JSONObject {

	String getString(String element);

	int getInt(String element);

	long getLong(String element);

	float getFloat(String element);

	boolean getBoolean(String element);
}
