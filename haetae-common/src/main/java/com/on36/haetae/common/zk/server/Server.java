package com.on36.haetae.common.zk.server;

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
			QuorumPeerConfig qc = new QuorumPeerConfig();
			qc.parseProperties(Configuration.create().getResource("zoo_sample.conf"));
			System.out.println("Loading config file [zoo_sample.conf]");
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
