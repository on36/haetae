package com.on36.haetae.test;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Api;
import com.on36.haetae.api.annotation.ApiDoc;
import com.on36.haetae.api.annotation.ApiParam;
import com.on36.haetae.api.http.MethodType;
import com.on36.haetae.api.http.ParamType;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */

public class UserService {

//	@Api(value = "/user", method = MethodType.PUT, version = "6.1")
//	public String addUser(Context context) {
//
//		return "Put-lisi-6.1";
//	}
//
//	@Api(value = "/user", method = MethodType.POST, version = "1.1")
//	public String postUser(Context context) {
//
//		return "Post-lisi";
//	}

	@Api(value = "/user/:id", method = MethodType.DELETE)
	@ApiDoc(name = "根据用户ID删除当前用户数据", params = {
			@ApiParam(param = "id", type = ParamType.URI, desc = "用户ID", required = true),
			@ApiParam(param = "sign", desc = "数据签名", required = true),
			@ApiParam(param = "timestamp", desc = "时间戳", required = true) })
	public String deleteUser(Context context) {
		String id = context.getCapturedParameter(":id");
		return "delete-" + id;
	}

//	@Api("/user/:id")
//	public String getUser(Context context) {
//		String id = context.getCapturedParameter(":id");
//		return "get-" + id;
//	}
//
//	@Api("/user/list/*/*")
//	public String list(Context context) {
//
//		return context.getCapturedParameter("*[0]");
//	}
}
