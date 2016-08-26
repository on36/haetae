package com.on36.haetae.test;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * @author zhanghr
 * @date 2016年8月23日
 */
public class Test {

	@SuppressWarnings("restriction")
	public static void main(String[] args) throws Exception {
		Signal sig = new Signal("KILL");
		Signal.handle(sig, new SignalHandler() {

			@Override
			public void handle(Signal arg0) {
				System.exit(-1);
			}

		});
		Thread.sleep(15000);
	}
}
