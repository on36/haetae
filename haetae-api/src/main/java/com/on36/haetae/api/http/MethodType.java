package com.on36.haetae.api.http;

/**
 * @author zhanghr
 * @date 2016年3月14日
 */
public enum MethodType {

	PUT("PUT"), POST("POST"), GET("GET"), DELETE("DELETE");

	private final String value;

	private MethodType(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}
}
