package com.on36.haetae.rpc.protobuf;

import io.netty.channel.ChannelPipeline;

public interface RPC<SEND, RECIEVED> {
	
	void start(String host,int port);
	
	void start();
	
	void quit();
	
	void addPiple(ChannelPipeline pipeline);
}
