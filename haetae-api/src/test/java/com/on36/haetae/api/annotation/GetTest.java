package com.on36.haetae.api.annotation;

public class GetTest {

	@Get
	@Path("/user/test")
	public String test() {

		return "test";
	}
}