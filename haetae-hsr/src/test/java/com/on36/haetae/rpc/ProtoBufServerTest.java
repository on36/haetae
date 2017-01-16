package com.on36.haetae.rpc;

import com.on36.haetae.rpc.protobuf.Handler;
import com.on36.haetae.rpc.protobuf.MessageBuilder.Message;
import com.on36.haetae.rpc.protobuf.MessageBuilder.Result;
import com.on36.haetae.rpc.protobuf.MessageBuilder.ResultType;
import com.on36.haetae.rpc.protobuf.tools.ProtoBufServer;

public class ProtoBufServerTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ProtoBufServer server = new ProtoBufServer();
		server.addHandler(new Handler<Result, Message>() {

			private volatile int count = 0;

			@Override
			public Result process(Message message) {
				// TODO Auto-generated method stub
				Result.Builder b = Result.newBuilder();
				b.setId(message.getId());
				b.setType(ResultType.SUCCESS);
				count++;
				if (count % 100000 == 0)
					System.out.println("server : " + count);
				return b.build();
			}
		});
		try {
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
