package com.on36.haetae.test;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Get;
import com.on36.haetae.api.annotation.Path;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */

public class UserService {

	@Get
	@Path("/user/add")
	public String addUser(Context context) {

		return "lisi";
	}

	@Get
	@Path("/user/remove/:id")
	public String removeUser(Context context) {

		return context.getCapturedParameter(":id");
	}
}
