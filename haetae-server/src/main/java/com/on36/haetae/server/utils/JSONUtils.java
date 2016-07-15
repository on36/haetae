package com.on36.haetae.server.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class JSONUtils {

	private static Gson gson = new Gson();
	private static JsonParser parser = new JsonParser();

	public static String toJson(Object src) {

		return gson.toJson(src);
	}

	public static <T> T fromJson(Class<T> clazz, String json) {

		return gson.fromJson(json, clazz);
	}

	/**
	 * 获取json属性值 ，不支持嵌套属性和数组
	 * 
	 * @param json
	 * @param elements
	 * @return
	 */
	public static Object getString(String json, String element) {
		JsonObject jo = parser.parse(json).getAsJsonObject();
		JsonElement je = jo.get(element);
		Object value = null;
		if (je.isJsonPrimitive()) {
			JsonPrimitive jp = je.getAsJsonPrimitive();
			if (jp.isBoolean())
				value = jp.getAsBoolean();
			else if (jp.isNumber())
				value = jp.getAsNumber();
			else if (jp.isString())
				value = jp.getAsString();
		} else if (je.isJsonArray()) {
			// TODO 数组解析
		}
		return value;
	}
}
