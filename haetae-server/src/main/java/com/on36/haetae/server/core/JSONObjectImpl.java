package com.on36.haetae.server.core;

import com.on36.haetae.api.JSONObject;
import com.on36.haetae.server.utils.JSONUtils;

/**
 * @author zhanghr
 * @date 2016年7月16日
 */
public class JSONObjectImpl implements JSONObject {

	private final String jsonValue;

	public JSONObjectImpl(String json) {
		this.jsonValue = json;
	}

	@Override
	public String getString(String element) {
		return (String) JSONUtils.getString(jsonValue, element);
	}

	@Override
	public int getInt(String element) {
		return ((Number) JSONUtils.getString(jsonValue, element)).intValue();
	}

	@Override
	public long getLong(String element) {
		return ((Number) JSONUtils.getString(jsonValue, element)).longValue();
	}
	
	@Override
	public float getFloat(String element) {
		return ((Number) JSONUtils.getString(jsonValue, element)).floatValue();
	}

	@Override
	public boolean getBoolean(String element) {
		return (boolean) JSONUtils.getString(jsonValue, element);
	}


	public static void main(String[] args) {
		String json = "{'name':'zhangsan','sex':'femail','age':28.1,'success':false,'info':{'address':'shanghai'}}";
		JSONObjectImpl joi = new JSONObjectImpl(json);
		System.out.println(joi.getFloat("age"));
	}
}
