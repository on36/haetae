package com.on36.haetae.test;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Get;
import com.on36.haetae.common.log.Logger;
import com.on36.haetae.common.log.LoggerFactory;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */

public class UserService {
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Get(value = "/user/add", version = "1.1")
	public String addUser(Context context) {

		LOG.info("hello lisi");
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
