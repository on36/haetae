package com.on36.haetae.server.utils;

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

	private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	private static JsonParser parser = new JsonParser();

	public static String toJson(Object src) {

		return gson.toJson(src);
	}

	public static <T> T fromJson(Class<T> clazz, String json) {

		return gson.fromJson(json, clazz);
	}

	/**
	 * 通过JSON元素属性获取属性值
	 * 
	 * @param json
	 * @param elements
	 * @return
	 */
	public static Object get(String json, String element) {
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
			JsonArray jsonArray = je.getAsJsonArray();
			Iterator<JsonElement> eles = jsonArray.iterator();
			List eleValues = null;
			while (eles.hasNext()) {
				JsonElement jele = eles.next();
				if (jele.isJsonPrimitive()) {
					JsonPrimitive jpele = jele.getAsJsonPrimitive();
					if (jpele.isNumber()) {
						if(eleValues == null)
							eleValues = new ArrayList<Number>();
						Number evalue = jpele.getAsNumber();
						eleValues.add(evalue);
					} else if (jpele.isString()) {
						if(eleValues == null)
							eleValues = new ArrayList<String>();
						String evalue = jpele.getAsString();
						eleValues.add(evalue);
					}
				}
			}
			value = eleValues;
		} else if (je.isJsonObject()) {
			value = je.toString();
		}
		return value;
	}
}
