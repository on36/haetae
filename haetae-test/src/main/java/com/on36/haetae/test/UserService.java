package com.on36.haetae.test;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Delete;
import com.on36.haetae.api.annotation.Get;
import com.on36.haetae.api.annotation.Post;
import com.on36.haetae.api.annotation.Put;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */

public class UserService {

	@Put(value = "/user", version = "6.1")
	public String addUser(Context context) {

		return "Put-lisi-6.1";
	}

	@Post(value = "/user", version = "1.1")
	public String postUser(Context context) {

		return "Post-lisi";
	}

	@Delete(value = "/user/:id")
	public String deleteUser(Context context) {
		String id = context.getCapturedParameter(":id");
		return "delete-" + id;
	}

	@Get("/user/:id")
	public String getUser(Context context) {
		String id = context.getCapturedParameter(":id");
		return "get-" + id;
	}

	@Get("/user/list/*/*")
	public String list(Context context) {

		return context.getCapturedParameter("*[0]");
	}
}
