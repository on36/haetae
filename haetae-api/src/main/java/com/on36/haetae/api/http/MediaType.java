package com.on36.haetae.api.http;

public enum MediaType {

	APPLICATION_JSON("application/json"), TEXT_JSON("text/json"), APPLICATION_XML(
			"application/xml"), TEXT_XML("text/xml");

	private final String value;

	private MediaType(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}
}
