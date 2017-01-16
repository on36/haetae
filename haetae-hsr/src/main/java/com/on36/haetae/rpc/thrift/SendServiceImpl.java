package com.on36.haetae.rpc.thrift;

import org.apache.thrift.TException;

import com.on36.haetae.hsr.DisruptorExt;
import com.on36.haetae.hsr.rpc.TimeUtils;
import com.on36.haetae.rpc.thrift.SendService.Iface;

/**
 * @author zhanghr
 * @date 2016年12月20日
 */
public class SendServiceImpl<T> implements Iface {

	private DisruptorExt<T> disruptorExt;

	public SendServiceImpl(DisruptorExt<T> disruptorExt) {
		this.disruptorExt = disruptorExt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result send(Message message) throws TException {
		String id = buildId();
		message.id = id;
		disruptorExt.publishEvent((T) message);
		Result result = new Result(id, ResultType.SUCCESS);
		return result;
	}

	public String buildId() {
		StringBuilder sb = new StringBuilder(TimeUtils.getCurrentTime());
		return sb.toString();
	}
}
