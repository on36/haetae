package com.on36.haetae.api.annotation;

import com.on36.haetae.api.http.DataType;

public class PathTest {

	@Api("/user/list")
	public String test() {

		return "test";
	}

	@ApiDoc(name = "测试接口", params = {
			@ApiParam(param = "sign", desc = "数据签名值", required = true),
			@ApiParam(param = "timestamp", desc = "时间戳", dataType = DataType.INT, required = true) })
	public void test2() {

	}
}
