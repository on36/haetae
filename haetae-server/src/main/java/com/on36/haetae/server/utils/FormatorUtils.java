package com.on36.haetae.server.utils;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import com.google.gson.Gson;

public class FormatorUtils {

	private static Gson gson = new Gson();

	public static String toJson(Object src) {
		
        return gson.toJson(src);
	}

	public static String toXML(Object src) {
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			XMLEncoder xmlEncoder = new XMLEncoder(out);
			xmlEncoder.writeObject(src);
			xmlEncoder.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(out != null)
				    out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return new String(out.toByteArray(), Charset.defaultCharset());
	}
}
