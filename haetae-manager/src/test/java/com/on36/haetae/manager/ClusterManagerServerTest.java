package com.on36.haetae.manager;

import java.util.ArrayList;
import java.util.List;

import com.on36.haetae.server.HaetaeServer;

/**
 * @author zhanghr
 * @date 2016年1月3日
 */
public class ClusterManagerServerTest {

	public static void main(String[] args) throws Exception {

		int port = 1025;
		if (args != null && args.length == 1)
			port = Integer.parseInt(args[0]);

		List<String> classes = new ArrayList<String>();
		classes.add(ClusterManagerService.class.getName());
		classes.add(ConfigAgentService.class.getName());
		HaetaeServer server = new HaetaeServer(port, 0, "/cluster", classes);
		server.start();
	}
}
