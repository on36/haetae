package com.on36.haetae.http;

public enum ServiceLevel {

	LEVELS(-1),LEVEL_1000(1000), LEVEL_200(200),LEVEL_50(50),LEVEL_0(0);

	private final int value;

	private ServiceLevel(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}
}
