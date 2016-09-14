package com.on36.haetae.api.annotation;

public class GetTest {

	@Api("/user/test")
	public String test() {

		return "test";
	}
}