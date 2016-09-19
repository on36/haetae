package com.on36.haetae.api.http;

/**
 * @author zhanghr
 * @date 2016年3月14日
 */
public enum DataType {

	INT("INT"), STRING("STRING");

	private final String value;

	private DataType(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}
}
