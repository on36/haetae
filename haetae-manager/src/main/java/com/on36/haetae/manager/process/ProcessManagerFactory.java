package com.on36.haetae.manager.process;

import com.on36.haetae.manager.process.impl.ProcessManagerUnixImpl;
import com.on36.haetae.manager.process.impl.ProcessManagerWindowImpl;

/**
 * @author zhanghr
 * @date 2016年7月20日
 */
public class ProcessManagerFactory {

	private static ProcessManagerUnixImpl unix = new ProcessManagerUnixImpl();
	private static ProcessManagerWindowImpl window = new ProcessManagerWindowImpl();

	public static ProcessManager getProcessManager() {
		String osName = System.getProperty("os.name");
		if (osName.indexOf("Windows") == -1) {
			return unix;
		} else {
			return window;
		}
	}
}
