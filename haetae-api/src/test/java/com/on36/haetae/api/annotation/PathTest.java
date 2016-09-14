package com.on36.haetae.api.annotation;

public class PathTest {

	@Api("/user/list")
	public String test() {

		return "test";
	}

	@ApiDoc(name = "测试接口", params = { @ApiParam(key = "sign", desc = "数据签名值"),
			@ApiParam(key = "timestamp", desc = "时间戳") })
	public void test2() {

	}
}
