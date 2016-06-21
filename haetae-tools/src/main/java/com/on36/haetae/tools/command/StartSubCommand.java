package com.on36.haetae.tools.command;

import org.apache.commons.cli.Options;

import com.on36.haetae.tools.SubCommand;
import com.on36.haetae.tools.utils.ProccesUtil;

/**
 * @author zhanghr
 * @date 2016年3月3日
 */
public class StartSubCommand implements SubCommand {

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
	public void execute(String... args) {
		// TODO Auto-generated method stub

		 ProccesUtil.execJava("com.on36.haetae.tools.server.HaetaeServerStartup",args);
	}
}
