package com.on36.haetae.udp;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public interface Scheduler {

	void revieve(Message message);

	void send(Message message);
}
