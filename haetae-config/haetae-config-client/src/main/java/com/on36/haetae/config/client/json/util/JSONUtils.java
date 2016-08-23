package com.on36.haetae.config.client.json.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class JSONUtils {

	private static Gson gson = new GsonBuilder()
			.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	private static JsonParser parser = new JsonParser();

	/**
	 * 将一个对象转换成JSON字符串.
	 * 
	 * @param src
	 * @return
	 */
	public static String toJson(Object src) {

		return gson.toJson(src);
	}

	/**
	 * 将一个JSON字符串解析成对象.
	 * 
	 * @param clazz
	 * @param json
	 * @return
	 */
	public static <T> T fromJson(Class<T> clazz, String json) {

		return gson.fromJson(json, clazz);
	}

	/**
	 * 将一个数组JSON解析成List对象
	 * 
	 * @param clazz
	 * @param json
	 * @return
	 */
	public static <T> List<T> fromJsonToList(Class<T> clazz, String jsonArray) {
		JsonArray jsonArrayObject = parser.parse(jsonArray).getAsJsonArray();
		Iterator<JsonElement> eles = jsonArrayObject.iterator();
		if (!eles.hasNext())
			return null;
		List<T> eleValues = new ArrayList<T>();
		while (eles.hasNext()) {
			JsonElement jele = eles.next();
			if (jele.isJsonPrimitive())
				eleValues.add(get(clazz, jele));
			else
				eleValues.add(gson.fromJson(jele, clazz));
		}
		return eleValues.size() > 0 ? eleValues : null;

	}

	/**
	 * 通过JSON元素属性获取属性值，该属性值必须是数组，针对数组解析
	 * 
	 * @param clazz
	 * @param json
	 * @param elements
	 * @return
	 */
	public static <T> List<T> fromJsonToList(Class<T> clazz, String json,
			String element) {
		JsonObject jo = parser.parse(json).getAsJsonObject();
		JsonElement je = jo.get(element);
		String jsonArray = je.getAsJsonArray().toString();

		return fromJsonToList(clazz, jsonArray);
	}

	/**
	 * 通过JSON元素属性获取属性值，不解析数组
	 * 
	 * @param json
	 * @param elements
	 * @return
	 */
	public static <T> T get(Class<T> clazz, String json, String element) {
		JsonObject jo = parser.parse(json).getAsJsonObject();
		JsonElement je = jo.get(element);
		return get(clazz, je);
	}

	private static <T> T get(Class<T> clazz, JsonElement je) {
		Object value = null;
		if (je.isJsonPrimitive()) {
			JsonPrimitive jp = je.getAsJsonPrimitive();
			if (jp.isBoolean())
				value = jp.getAsBoolean();
			else if (jp.isNumber()) {
				if (clazz.getSimpleName().equals("Integer")
						|| clazz.getSimpleName().equals("int")) {
					value = jp.getAsNumber().intValue();
				} else if (clazz.getSimpleName().toLowerCase().equals("long")) {
					value = jp.getAsNumber().longValue();
				} else if (clazz.getSimpleName().toLowerCase()
						.equals("float")) {
					value = jp.getAsNumber().floatValue();
				} else if (clazz.getSimpleName().toLowerCase()
						.equals("double")) {
					value = jp.getAsNumber().doubleValue();
				}
			} else if (jp.isString()) {
				if (clazz.getSimpleName().toLowerCase().equals("date") || clazz
						.getSimpleName().toLowerCase().equals("timestamp"))
					value = gson.fromJson(je, clazz);
				else
					value = jp.getAsString();
			}
		} else if (je.isJsonObject())
			if (clazz.getSimpleName().toLowerCase().equals("string"))
				value = je.toString();
			else
				value = gson.fromJson(je, clazz);

		return (T) value;
	}
}
