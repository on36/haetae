package com.on36.haetae.rpc.protobuf;

public interface Handler<SEND,RECIEVED> {

	SEND process(RECIEVED message);
}
