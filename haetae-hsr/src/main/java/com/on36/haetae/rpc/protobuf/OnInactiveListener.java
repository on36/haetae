package com.on36.haetae.rpc.protobuf;

import io.netty.channel.ChannelHandlerContext;

public interface OnInactiveListener {
	void inactive(ChannelHandlerContext ctx);
}
