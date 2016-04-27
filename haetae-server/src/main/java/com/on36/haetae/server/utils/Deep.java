package com.on36.haetae.server.utils;

/**
 * @author zhanghr
 * @date 2016年1月11日
 */
public class Deep {

	private final String deep;
	private int index;

	public Deep() {
		this("1");
	}

	public Deep(String deep) {
		this.deep = deep;
	}

	public String getDeep() {
		return deep;
	}

	public String next() {
		return deep + "." + ++index;
	}

	@Override
	public String toString() {
		return getDeep();
	}
}
