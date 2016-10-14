package com.on36.haetae.tools.process.impl;

import java.util.ArrayList;
import java.util.List;

import com.on36.haetae.tools.process.BaseProcessManager;
import com.on36.haetae.tools.utils.ProcessUtil;

/**
 * @author zhanghr
 * @date 2016年3月19日
 */
public class ProcessManagerUnixImpl extends BaseProcessManager {

	@Override
	protected List<String> killPid(int pid) {
		List<String> commands = new ArrayList<String>();
		commands.add("/bin/sh");
		commands.add("-c");
		commands.add("kill -15 " + pid);
		return commands;
	}

	@Override
	protected int findPid(int port) {
		String command = "lsof -i :" + port
				+ " |grep '(LISTEN)'| awk '{print $2}'";
		String result = ProcessUtil.execAndAutoCloseble("/bin/sh", "-c",
				command);
		if (result != null && result.trim().length() > 0)
			return Integer.parseInt(result);
		return -1;
	}
}
