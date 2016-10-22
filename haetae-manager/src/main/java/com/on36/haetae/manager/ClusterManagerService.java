package com.on36.haetae.manager;

import java.util.Map;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Api;
import com.on36.haetae.api.annotation.ApiDoc;
import com.on36.haetae.api.annotation.ApiParam;
import com.on36.haetae.api.http.DataType;
import com.on36.haetae.api.http.MethodType;
import com.on36.haetae.common.log.Logger;
import com.on36.haetae.common.log.LoggerFactory;
import com.on36.haetae.common.utils.NetworkUtils;
import com.on36.haetae.common.utils.StringUtils;
import com.on36.haetae.common.utils.ThrowableUtils;
import com.on36.haetae.tools.process.ProcessManagerFactory;

/**
 * @author zhanghr
 * @date 2016年3月11日
 */
public class ClusterManagerService {
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private int defaultPort = 8080;
	private String defaultThreadPoolSize = "0";
	private String defaultRoot = "/services";
	private String defaultPackageName = "com.on36.haetae.test";
	private String defaultMavenUrl = "http://192.168.153.129:8081/repository/maven-public";

	@Api("/process")
	@ApiDoc(name = "根据指定信息启动一个服务", params = {
			@ApiParam(param = "port", desc = "端口号", dataType = DataType.INT, defaultValue = "8080"),
			@ApiParam(param = "root", desc = "根名称", defaultValue = "/services"),
			@ApiParam(param = "coords", desc = "maven坐标"),
			@ApiParam(param = "url", desc = "maven仓库地址", defaultValue = "http://192.168.153.129:8081/repository/maven-public"),
			@ApiParam(param = "package", desc = "包名称", defaultValue = "com.on36.haetae.test") })
	public Object add(Context context) throws Exception {

		String port = context.getRequestParameter("port");
		String root = context.getRequestParameter("root");
		String packageName = context.getRequestParameter("package");
		String threadPoolSize = context.getRequestParameter("pool");
		String coords = context.getRequestParameter("coords");
		String url = context.getRequestParameter("url");

		if (port == null) {
			while (checkPort(defaultPort)) {
				defaultPort++;
			}
			port = defaultPort + "";
		} else if (checkPort(port)) {
			throw new Exception(
					"Address already in use: bind port[" + port + "]");
		}

		if (StringUtils.isEmpty(root))
			root = defaultRoot;
		else if (!root.startsWith("/"))
			root = "/" + root;
		if (StringUtils.isEmpty(packageName))
			packageName = defaultPackageName;
		if (StringUtils.isEmpty(threadPoolSize))
			threadPoolSize = defaultThreadPoolSize;
		if (StringUtils.isEmpty(url))
			url = defaultMavenUrl;

		String[] args = null;
		if (coords == null)
			args = new String[8];
		else {
			args = new String[12];
			args[8] = "-c";
			args[9] = coords;
			args[10] = "-u";
			args[11] = url;
		}
		args[0] = "-p";
		args[1] = port;
		args[2] = "-pn";
		args[3] = packageName;
		args[4] = "-r";
		args[5] = root;
		args[6] = "-t";
		args[7] = threadPoolSize;

		Map<String, Object> result = ProcessManagerFactory.getProcessManager()
				.process(args);
		boolean success = (boolean) result.get("success");
		String message = result.get("message").toString();
		if (!success) {
			LOG.error(message);
			throw new Exception("start failed!",
					ThrowableUtils.makeThrowable(message));
		} else {
			LOG.info(message);
			defaultPort++;
		}
		StringBuilder sb = new StringBuilder(NetworkUtils.getInnerIP());
		sb.append(":");
		sb.append(port);
		sb.append(root);
		return sb.toString();
	}

	private boolean checkPort(String port) {
		return checkPort(Integer.parseInt(port));
	}

	private boolean checkPort(int port) {
		int pid = ProcessManagerFactory.getProcessManager().findPid(port);
		return pid > 0;
	}

	@Api(value = "/process", method = MethodType.DELETE)
	@ApiDoc(name = "根据端口关闭指定服务", params = {
			@ApiParam(param = "port", desc = "端口号", required = true) })
	public String del(Context context) {
		String port = context.getRequestParameter("port");
		int pid = ProcessManagerFactory.getProcessManager().killProcess(port);
		return "kill-" + pid;
	}
}
