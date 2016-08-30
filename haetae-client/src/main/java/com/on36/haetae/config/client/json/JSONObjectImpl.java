package com.on36.haetae.config.client.json;

import java.util.Date;
import java.util.List;

import com.on36.haetae.api.JSONObject;
import com.on36.haetae.config.client.json.util.JSONUtils;


/**
 * @author zhanghr
 * @date 2016年3月16日
 */
public class JSONObjectImpl implements JSONObject {

	private final String jsonValue;

	public JSONObjectImpl(String json) {
		this.jsonValue = json;
	}

	@Override
	public String get(String element) {
		return JSONUtils.get(String.class, jsonValue, element);
	}

	@Override
	public int getInt(String element) {
		return JSONUtils.get(int.class, jsonValue, element);
	}

	@Override
	public long getLong(String element) {
		return JSONUtils.get(long.class, jsonValue, element);
	}

	@Override
	public float getFloat(String element) {
		return JSONUtils.get(float.class, jsonValue, element);
	}

	@Override
	public boolean getBoolean(String element) {
		return JSONUtils.get(Boolean.class, jsonValue, element);
	}
	
	@Override
	public Date getDate(String element) {
		return JSONUtils.get(Date.class, jsonValue, element);
	}

	@Override
	public JSONObject getObject(String element) {
		return new JSONObjectImpl(
				JSONUtils.get(String.class, jsonValue, element));
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, String element) {
		return JSONUtils.fromJsonToList(clazz, jsonValue, element);
	}
}
