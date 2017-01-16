package com.on36.haetae.hsr.test;

import com.on36.haetae.hsr.EventBus;
import com.on36.haetae.hsr.EventListener;
import com.on36.haetae.rpc.thrift.Message;

/**
 * @author zhanghr
 * @date 2016年12月15日
 */
public class Test {

	public static void main(String[] args) throws Exception {
		EventBus.addListener(8890, new EventListener<Message>() {

			private volatile int count = 0;
			@Override
			public void doHandler(Message event) {
				// TODO Auto-generated method stub
				count++;
				if (count % 10000 == 0)
					System.out.println("server : " + count);
			}
		}, 64, false);
	}

}
