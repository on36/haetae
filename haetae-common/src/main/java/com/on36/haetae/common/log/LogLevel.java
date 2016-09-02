package com.on36.haetae.common.log;

public enum LogLevel {
	INFO("INFO"), WARN("WARN"), ERROR("ERROR"), DEBUG("DEBUG");

	private String value;

	LogLevel(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
