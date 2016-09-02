package com.on36.haetae.common.zk.server;

import java.util.Properties;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

import com.on36.haetae.common.conf.Configuration;

public class Server {

	public static void main(String[] args) {
		start();
	}

	public static void start() {
		try {
			System.setProperty("haetae.log.name", "haetae-zookeeper");

			QuorumPeerConfig qc = new QuorumPeerConfig();
			Properties prop = Configuration.create()
					.getResource("zoo_default.conf");
			Exception exc = Configuration.create().getResource("zoo.conf",
					prop);
			if (exc != null)
				System.out.println(exc.getMessage()
						+ ", using default file [zoo_default.conf]");
			qc.parseProperties(prop);
			ServerConfig config = new ServerConfig();
			config.readFrom(qc);
			System.out.println("Starting zookeeper server..");
			ZooKeeperServerMain server = new ZooKeeperServerMain();
			server.runFromConfig(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
