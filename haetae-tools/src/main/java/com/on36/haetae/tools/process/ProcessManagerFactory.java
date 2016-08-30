package com.on36.haetae.tools.process;

import com.on36.haetae.tools.process.impl.ProcessManagerUnixImpl;
import com.on36.haetae.tools.process.impl.ProcessManagerWindowImpl;

/**
 * @author zhanghr
 * @date 2016年3月20日
 */
public class ProcessManagerFactory {

	private static ProcessManagerUnixImpl unix = new ProcessManagerUnixImpl();
	private static ProcessManagerWindowImpl window = new ProcessManagerWindowImpl();

	public static ProcessManager getProcessManager() {
		String osName = System.getProperty("os.name");
		if (osName.indexOf("Windows") > -1) {
			return window;
		} else if (osName.indexOf("Linux") > -1) {
			return unix;
		} else
			throw new RuntimeException("not support operate system : " + osName);
	}
}
