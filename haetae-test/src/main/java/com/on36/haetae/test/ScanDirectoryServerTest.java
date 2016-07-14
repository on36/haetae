package com.on36.haetae.test;

import com.on36.haetae.server.HaetaeServer;

/**
 * @author zhanghr
 * @date 2016年1月22日 
 */
public class ScanDirectoryServerTest {

	public static void main(String[] args) throws Exception {
		HaetaeServer server = new HaetaeServer(8080);
		server.start();
	}
}
