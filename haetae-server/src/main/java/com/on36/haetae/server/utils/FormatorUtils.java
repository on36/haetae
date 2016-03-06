package com.on36.haetae.server.utils;

import io.netty.util.CharsetUtil;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.google.gson.Gson;

public class FormatorUtils {

	private static Gson gson = new Gson();

	public static String toJson(Object src) {

		return gson.toJson(src);
	}
	
	public static <T> T fromJson(Class<T> clazz, String json) {
		
		return gson.fromJson(json, clazz);
	}

	public static String toXML(Object src) {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			XMLEncoder xmlEncoder = new XMLEncoder(out);
			xmlEncoder.writeObject(src);
			xmlEncoder.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return new String(out.toByteArray(), CharsetUtil.UTF_8);
	}
}
