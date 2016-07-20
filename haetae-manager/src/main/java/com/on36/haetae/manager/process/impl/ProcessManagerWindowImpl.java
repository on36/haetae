package com.on36.haetae.manager.process.impl;

import com.on36.haetae.manager.process.BaseProcessManager;
import com.on36.haetae.tools.utils.ProcessUtil;

/**
 * @author zhanghr
 * @date 2016年3月20日
 */
public class ProcessManagerWindowImpl extends BaseProcessManager {

	@Override
	public String killPid(int pid) {
		return "taskkill /PID " + pid + " /f";
	}

	@Override
	protected int findPid(int port) {
		String command = "netstat -ano -p TCP|findstr " + port
				+ "|findstr LISTENING";
		String result = ProcessUtil.execAndAutoCloseble("cmd.exe", "/c",
				command);
		if (result != null) {
			String[] arr = result.split(" ");
			int length = arr.length;
			if (length > 4)
				return Integer.parseInt(arr[length - 1]);
		}
		return -1;
	}

	public static void main(String[] args) {
		ProcessManagerWindowImpl window = new ProcessManagerWindowImpl();
		System.out.println(window.getPid(49889));
	}
}
