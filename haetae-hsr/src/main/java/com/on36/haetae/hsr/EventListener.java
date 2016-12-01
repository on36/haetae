package com.on36.haetae.hsr;

/**
 * @author zhanghr
 * @date 2016年11月25日
 */
public interface EventListener<T> {

	public void doHandler(T event);
}
