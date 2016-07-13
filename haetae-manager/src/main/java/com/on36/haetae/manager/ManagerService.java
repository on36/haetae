package com.on36.haetae.manager;

import java.util.Map;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Get;
import com.on36.haetae.tools.utils.ProccesUtil;

/**
 * @author zhanghr
 * @date 2016年3月11日
 */
public class ManagerService {

	@Get("/add")
	public String add(Context context) {

		String port = context.getRequestParameter("port");
		String coords = context.getRequestParameter("coords");
		String[] args = null;

		if (port != null) {
			args = new String[1];
			args[0] = port;
		}
		Map<String, Object> result = ProccesUtil.execJava(
				"com.on36.haetae.tools.server.HaetaeServerTest", false, args);

		return result.get("message").toString();
	}

	@Get("/del")
	public String del(Context context) {
		return "del-" + context.getRequestParameter("key");
	}

	@Get("/update")
	public String update(Context context) {
		return "update-" + context.getRequestParameter("key");
	}

	@Get("/list")
	public String list(Context context) {
		StringBuffer sb = new StringBuffer("<html><body>");
		sb.append("hello<br/>");
		sb.append("world<br/>");
		sb.append("</body></html>");
		return sb.toString();
	}

}
