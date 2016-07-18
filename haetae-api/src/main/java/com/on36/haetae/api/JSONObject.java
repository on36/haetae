package com.on36.haetae.api;

import java.util.List;

/**
 * @author zhanghr
 * @date 2016年3月16日
 */
public interface JSONObject {

	String get(String element);

	int getInt(String element);

	long getLong(String element);

	float getFloat(String element);

	boolean getBoolean(String element);
	
	public <T> List<T> getList(String element);
	
	public JSONObject getObject(String element);
}
