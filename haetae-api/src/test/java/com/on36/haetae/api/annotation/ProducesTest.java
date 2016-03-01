package com.on36.haetae.api.annotation;

import com.on36.haetae.api.http.MediaType;


public class ProducesTest {

	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public void test() {
		
	}
}