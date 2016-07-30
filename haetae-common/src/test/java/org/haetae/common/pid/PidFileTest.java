package org.haetae.common.pid;

import org.junit.Test;

import com.on36.haetae.common.pid.PidFile;

/**
 * @author zhanghr
 * @date 2016年3月20日
 */
public class PidFileTest {

	@Test
	public void testPidDir() {
		System.out.println(PidFile.listPidFile());
	}
}
