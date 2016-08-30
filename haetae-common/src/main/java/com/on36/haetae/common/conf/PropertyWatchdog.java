package com.on36.haetae.common.conf;

/**
 * @author zhanghr
 * @date 2016年1月30日
 */
public class PropertyWatchdog extends FileWatchdog {
	public PropertyWatchdog(String filename) {
		super(filename);
	}

	public void doOnChange() {
	}
}
