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
	public Map<String, Object> add(Context context) {

		String port = context.getRequestParameter("port");
		String packageName = context.getRequestParameter("package");
		String[] args = null;

		if (port != null && packageName != null) {
			args = new String[4];
			args[0] = "-p";
			args[1] = port;
			args[2] = "-pn";
			args[3] = packageName;
		} else if (packageName != null) {
			args = new String[2];
			args[0] = "-pn";
			args[1] = packageName;
		} else if (port != null) {
			args = new String[2];
			args[0] = "-p";
			args[1] = port;
		}
		Map<String, Object> result = ProccesUtil.execJava(
				"com.on36.haetae.tools.server.HaetaeServerStartup", false,
				args);

		return result;
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
