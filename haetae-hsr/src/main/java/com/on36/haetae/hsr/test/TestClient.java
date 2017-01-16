package com.on36.haetae.hsr.test;

import java.nio.ByteBuffer;

import com.on36.haetae.hsr.EventBus;
import com.on36.haetae.hsr.EventListener;
import com.on36.haetae.rpc.thrift.Message;
import com.on36.haetae.rpc.thrift.Result;

/**
 * @author zhanghr
 * @date 2016年12月15日
 */
public class TestClient {

	private static int TEST = 100000;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		EventBus.register("tcp//localhost:8890", new EventListener<Result>() {
			private volatile int count = 0;
			@Override
			public void doHandler(Result event) {
				// TODO Auto-generated method stub
				count++;
				if (count % 10000 == 0)
					System.out.println("client : " + count);
			}
		}, 64, false);

		int i = TEST;
		long start = System.currentTimeMillis();
		while (i-- > 0)
			EventBus.dispatch(
					new Message("nihao3", ByteBuffer.wrap("12356".getBytes())));
		long end = System.currentTimeMillis();
		long opsPerSecond = (TEST * 1000L) / (end - start);
		System.out.println(opsPerSecond + " ops/sec, start=" + start +", end="+end);
		
		EventBus.close();
	}
}
