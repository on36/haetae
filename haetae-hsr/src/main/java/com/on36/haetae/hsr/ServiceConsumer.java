package com.on36.haetae.hsr;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.on36.haetae.rpc.thrift.Message;
import com.on36.haetae.rpc.thrift.Result;
import com.on36.haetae.rpc.thrift.SendService;

/**
 * @author zhanghr
 * @date 2016年12月13日
 */
public class ServiceConsumer {
	public static final int TIMEOUT = 30000;

	private SendService.Client client;

	public ServiceConsumer(String conURL) {
		try {
			TTransport transport = new TFramedTransport(
					new TSocket("localhost", 8890, TIMEOUT));
			// 协议要和服务端一致
			// TProtocol protocol = new TBinaryProtocol(transport);
			TProtocol protocol = new TCompactProtocol(transport);
			// TProtocol protocol = new TJSONProtocol(transport);
			client = new SendService.Client(protocol);
			transport.open();
		} catch (TException e) {
			e.printStackTrace();
		}
	}

	public Result publish(Message message) {
		try {
			return client.send(message);
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
