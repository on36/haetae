package com.on36.haetae.test;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Get;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */

public class UserService {

	@Get(value="/user/add",version="1.1")
	public String addUser(Context context) {

		return "lisi";
	}

	
	@Get("/user/remove/:id")
	public String removeUser(Context context) {

		return context.getCapturedParameter(":id");
	}
	@Get("/user/list/*/*")
	public String list(Context context) {
		
		return context.getCapturedParameter("*[0]");
	}
}
