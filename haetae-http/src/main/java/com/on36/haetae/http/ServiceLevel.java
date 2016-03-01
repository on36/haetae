package com.on36.haetae.http;

public enum ServiceLevel {

	LEVELS(-1),LEVELA(200), LEVELB(50),LEVELC(10),LEVELD(0);

	private int value;

	private ServiceLevel(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}
}
