package com.on36.haetae.rpc.protobuf;

import io.netty.channel.ChannelHandlerContext;

public interface OnActiveListener {
	void active(ChannelHandlerContext ctx);
}
