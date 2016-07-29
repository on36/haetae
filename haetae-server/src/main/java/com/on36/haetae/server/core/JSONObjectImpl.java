package com.on36.haetae.server.core;

import java.util.List;

import com.on36.haetae.api.JSONObject;
import com.on36.haetae.common.utils.JSONUtils;

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
	public String get(String element) {
		return (String) JSONUtils.get(jsonValue, element);
	}

	@Override
	public int getInt(String element) {
		return ((Number) JSONUtils.get(jsonValue, element)).intValue();
	}

	@Override
	public long getLong(String element) {
		return ((Number) JSONUtils.get(jsonValue, element)).longValue();
	}
	
	@Override
	public float getFloat(String element) {
		return ((Number) JSONUtils.get(jsonValue, element)).floatValue();
	}

	@Override
	public boolean getBoolean(String element) {
		return (boolean) JSONUtils.get(jsonValue, element);
	}
	
	@Override
	public JSONObject getObject(String element) {
		return new JSONObjectImpl(JSONUtils.get(jsonValue, element).toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getList(String element) {
		return (List<T>) JSONUtils.get(jsonValue, element);
	}
}
