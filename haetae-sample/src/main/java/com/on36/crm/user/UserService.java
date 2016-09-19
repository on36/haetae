package com.on36.crm.user;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Api;
import com.on36.haetae.api.http.MethodType;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */

public class UserService {

	@Api(value = "/user", method = MethodType.PUT)
	public String addUser(Context context) {

		return "Put-lisi-6.1";
	}

	@Api(value = "/user", method = MethodType.POST, version = "1.1")
	public String postUser(Context context) {

		return "Post-lisi";
	}

	@Api(value = "/user/:id", method = MethodType.DELETE)
	public String deleteUser(Context context) {
		String id = context.getCapturedParameter(":id");
		return "delete-" + id;
	}

	@Api("/user/:id")
	public String getUser(Context context) {
		String id = context.getCapturedParameter(":id");
		return "get-" + id;
	}

	@Api("/user/list/*/*")
	public String list(Context context) {

		return context.getCapturedParameter("*[0]");
	}
}
