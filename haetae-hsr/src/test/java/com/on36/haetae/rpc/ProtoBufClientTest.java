package com.on36.haetae.rpc;

import com.google.protobuf.ByteString;
import com.on36.haetae.rpc.protobuf.Handler;
import com.on36.haetae.rpc.protobuf.MessageBuilder.Message;
import com.on36.haetae.rpc.protobuf.MessageBuilder.Result;
import com.on36.haetae.rpc.protobuf.tools.ProtoBufClient;
import com.on36.haetae.rpc.protobuf.OnActiveListener;

import io.netty.channel.ChannelHandlerContext;

public class ProtoBufClientTest {

	private static long start = 0;
	private static int TEST = 500000;

	public static void main(String[] args) {
		final ProtoBufClient client = new ProtoBufClient();

		client.setOnActiveListener(new OnActiveListener() {

			@Override
			public void active(ChannelHandlerContext ctx) {
				// TODO Auto-generated method stub
				new SendThread(client).start();
			}
		});
		try {
			client.start();
			// client.start("10.80.81.87", 10001);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static class SendThread extends Thread {
		private ProtoBufClient client;

		public SendThread(ProtoBufClient cli) {
			this.client = cli;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			int i = TEST;
			start = System.currentTimeMillis();
			Handler<Void, Result> handler = new Handler<Void, Result>() {
					private volatile int count = 0;

					@Override
					public Void process(Result message) {
						count++;
						if (count % 100000 == 0) {
							System.out.println("client : " + count + " time=" + System.currentTimeMillis());
						}
						return null;
					}
				};
			while (i-- > 0) {
				try {
					Message.Builder b = Message.newBuilder();
					b.setTopic("test_topic");
					b.setCreatedTime(System.currentTimeMillis());
					b.setContent(ByteString.copyFromUtf8(
							"jkcxiewknvnnk238874385lijfd'b . lpofdosifds-09=ijkasdfioewjivn,vxzoidou0832478654hjkajdksbv,cxgdsafffffffffffffffffffffaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaerrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr"));
					client.send(b.build(),handler);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			long end = System.currentTimeMillis();
			System.out.println("start =" + start);
			System.out.println("send total time(ms):" + (end - start));
			long opsPerSecond = (TEST * 1000L) / (System.currentTimeMillis() - start);
			System.out.println(opsPerSecond + " ops/sec");
		}
	}
}
