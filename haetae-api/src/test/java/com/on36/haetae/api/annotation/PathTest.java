package com.on36.haetae.api.annotation;


public class PathTest {

	@Get("/user/list")
	public String test() {

		return "test";
	}
}
