package com.on36.haetae.server.core.manager.event.handler;

import com.lmax.disruptor.EventHandler;
import com.on36.haetae.common.log.LoggerFactory;
import com.on36.haetae.server.core.manager.event.LogEvent;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class LogEventHandler implements EventHandler<LogEvent> {

	@Override
	public void onEvent(LogEvent event, long sequence, boolean endOfBatch)
			throws Exception {
		switch (event.getLevel()) {
		case ERROR:
			LoggerFactory.getLogger(event.getClazz()).error(event.getMessage(),
					event.getExcp());
			break;
		case WARN:
			LoggerFactory.getLogger(event.getClazz()).warn(event.getMessage(),
					event.getExcp());
			break;
		case DEBUG:
			LoggerFactory.getLogger(event.getClazz()).debug(event.getMessage(),
					event.getExcp());
			break;
		default:
			LoggerFactory.getLogger(event.getClazz()).info(event.getMessage(),
					event.getExcp());
			break;
		}
	}

}
