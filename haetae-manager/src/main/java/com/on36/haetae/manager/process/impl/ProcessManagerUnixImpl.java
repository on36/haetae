package com.on36.haetae.manager.process.impl;

import java.util.ArrayList;
import java.util.List;

import com.on36.haetae.manager.process.BaseProcessManager;
import com.on36.haetae.tools.utils.ProcessUtil;

/**
 * @author zhanghr
 * @date 2016年3月19日
 */
public class ProcessManagerUnixImpl extends BaseProcessManager {

	@Override
	protected List<String> killPid(int pid) {
		List<String> commands = new ArrayList<String>();
		commands.add("kill -9 " + pid);
		return commands;
	}

	@Override
	protected int findPid(int port) {
		String command = "lsof -i :" + port
				+ " |grep '(LISTEN)'| awk '{print $2}";
		String result = ProcessUtil.execAndAutoCloseble(command);
		if (result != null)
			return Integer.parseInt(result);
		return -1;
	}
}
