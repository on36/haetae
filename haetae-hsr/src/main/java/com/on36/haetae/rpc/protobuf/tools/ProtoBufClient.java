package com.on36.haetae.rpc.protobuf.tools;

import com.on36.haetae.rpc.protobuf.Client;
import com.on36.haetae.rpc.protobuf.MessageBuilder.Message;
import com.on36.haetae.rpc.protobuf.MessageBuilder.Result;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class ProtoBufClient extends Client<Message, Result> {

	@Override
	public void addPiple(ChannelPipeline pipeline) {
		pipeline.addFirst("protobufDecoder", new ProtobufDecoder(Result.getDefaultInstance()));
		pipeline.addFirst("protobufEncoder", new ProtobufEncoder());
		pipeline.addFirst("frameDecoder", new ProtobufVarint32FrameDecoder());
		pipeline.addFirst("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
	}

}
