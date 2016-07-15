package com.on36.haetae.server.core.manager.event.handler;

import com.lmax.disruptor.EventHandler;
import com.on36.haetae.server.core.manager.event.LogEvent;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class LogEventHandler implements EventHandler<LogEvent> {

	@Override
	public void onEvent(LogEvent event, long sequence, boolean endOfBatch)
			throws Exception {
		//System.out.println(event.getInfo());
	}

}
