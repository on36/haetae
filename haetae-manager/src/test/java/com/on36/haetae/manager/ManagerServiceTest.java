package com.on36.haetae.manager;

import com.on36.haetae.tools.utils.ProcessUtil;

/**
 * @author zhanghr
 * @date 2016年3月12日 
 */
public class ManagerServiceTest {

	public static void main(String[] args) throws Exception {
		System.out.println(ProcessUtil.execJava(
				"com.on36.haetae.tools.server.HaetaeServerTest",false, args));
		Thread.sleep(5000);
		System.out.println(ProcessUtil.execJava(
				"com.on36.haetae.tools.server.HaetaeServerTest",false, args));
	}

}
