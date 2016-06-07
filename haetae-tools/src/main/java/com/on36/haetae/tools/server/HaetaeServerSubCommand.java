package com.on36.haetae.tools.server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import com.on36.haetae.server.HaetaeServer;
import com.on36.haetae.tools.SubCommand;

/**
 * @author zhanghr
 * @date 2016年3月3日
 */
public class HaetaeServerSubCommand implements SubCommand {

	private static int port = 8080;
	private static int threadPoolSize = 0;
	private static String rootPath = "/services";

	private static void parse(CommandLine line) {

		if (line.hasOption("port")) {
			String p = line.getOptionValue("port");
			port = Integer.parseInt(p);
		}
		if (line.hasOption("threadPoolSize")) {
			String size = line.getOptionValue("threadPoolSize");
			threadPoolSize = Integer.parseInt(size);
		}
		if (line.hasOption("root")) {
			rootPath = line.getOptionValue("threadPoolSize");
		}
	}

	@Override
	public String commandName() {
		// TODO Auto-generated method stub
		return "start";
	}

	@Override
	public String commandDesc() {
		// TODO Auto-generated method stub
		return "start a haetae server";
	}

	@Override
	public Options buildCommandlineOptions(Options options) {
		options.addOption("p", "port", true, "service port, default: 8080");
		options.addOption("r", "root", true,
				"root path name, default: /services");
		options.addOption("t", "threadPoolSize", true,
				"the size of thread pool  default: the twice maximum number of processors available to the virtual machine ");
		return options;
	}

	@Override
	public void execute(CommandLine commandLine) {
		// TODO Auto-generated method stub
		parse(commandLine);

		HaetaeServer server = new HaetaeServer(port, threadPoolSize);
		server.start();
	}
}
