package com.on36.haetae.rpc.protobuf.tools;

import com.on36.haetae.rpc.protobuf.MessageBuilder.Message;
import com.on36.haetae.rpc.protobuf.MessageBuilder.Result;
import com.on36.haetae.rpc.protobuf.Server;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class ProtoBufServer extends Server<Result, Message> {

	@Override
	public void addPiple(ChannelPipeline pipeline) {
		pipeline.addFirst("protobufDecoder", new ProtobufDecoder(Message.getDefaultInstance()));
		pipeline.addFirst("protobufEncoder", new ProtobufEncoder());
		pipeline.addFirst("frameDecoder", new ProtobufVarint32FrameDecoder());
		pipeline.addFirst("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
	}

}
