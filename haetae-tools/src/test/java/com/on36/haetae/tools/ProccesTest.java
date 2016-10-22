package com.on36.haetae.tools;

import com.on36.haetae.tools.process.ProcessManagerFactory;

/**
 * @author zhanghr
 * @date 2016年3月20日
 */
public class ProccesTest {

	public static void main(String[] args) {
		ProcessManagerFactory.getProcessManager().findPid(8080);

	}
}
