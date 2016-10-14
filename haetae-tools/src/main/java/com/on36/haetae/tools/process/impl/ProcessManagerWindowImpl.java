package com.on36.haetae.tools.process.impl;

import java.util.ArrayList;
import java.util.List;

import com.on36.haetae.tools.process.BaseProcessManager;
import com.on36.haetae.tools.utils.ProcessUtil;

/**
 * @author zhanghr
 * @date 2016年3月20日
 */
public class ProcessManagerWindowImpl extends BaseProcessManager {

	@Override
	protected List<String> killPid(int pid) {
		List<String> commands = new ArrayList<String>();
		commands.add("cmd.exe");
		commands.add("/c");
		commands.add("taskkill /PID " + pid + " /f");
		return commands;
	}

	@Override
	protected int findPid(int port) {
		String command = "netstat -ano -p TCP|findstr " + port
				+ "|findstr LISTENING";
		String result = ProcessUtil.execAndAutoCloseble("cmd.exe", "/c",
				command);
		if (result != null && result.trim().length() > 0) {
			String[] arr = result.split(" ");
			int length = arr.length;
			if (length > 4)
				return Integer.parseInt(arr[length - 1]);
		}
		return -1;
	}
}
