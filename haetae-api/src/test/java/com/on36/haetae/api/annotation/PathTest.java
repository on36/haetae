package com.on36.haetae.api.annotation;

import com.on36.haetae.api.http.MediaType;


public class PathTest {

	@Path("/user/list")
	@Produces(MediaType.APPLICATION_JSON)
	public String test() {
		return null;
	}
}
