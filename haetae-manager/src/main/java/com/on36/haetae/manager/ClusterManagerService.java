package com.on36.haetae.manager;

import java.util.Map;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Get;
import com.on36.haetae.common.log.Logger;
import com.on36.haetae.common.log.LoggerFactory;
import com.on36.haetae.common.utils.ThrowableUtils;
import com.on36.haetae.tools.process.ProcessManagerFactory;

/**
 * @author zhanghr
 * @date 2016年3月11日
 */
public class ClusterManagerService {
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private int defaultPort = 8080;

	@Get("/add")
	public Object add(Context context) throws Exception {

		String port = context.getRequestParameter("port");
		String packageName = context.getRequestParameter("package");
		String[] args = null;

		if (port == null)
			port = defaultPort + "";

		if (port != null && packageName != null) {
			args = new String[4];
			args[0] = "-p";
			args[1] = port;
			args[2] = "-pn";
			args[3] = packageName;
		} else {
			args = new String[2];
			args[0] = "-p";
			args[1] = port;
		}
		Map<String, Object> result = ProcessManagerFactory.getProcessManager()
				.process(args);
		boolean success = (boolean) result.get("success");
		String message = result.get("message").toString();
		if (!success) {
			throw new Exception("start failed!",
					ThrowableUtils.makeThrowable(message));
		} else {
			LOG.info(message);
			defaultPort++;
		}
		return success;
	}

	@Get("/kill")
	public String del(Context context) {
		String port = context.getRequestParameter("port");
		int pid = ProcessManagerFactory.getProcessManager().killProcess(port);
		return "kill-" + pid;
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
