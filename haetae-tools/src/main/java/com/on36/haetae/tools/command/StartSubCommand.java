package com.on36.haetae.tools.command;

import org.apache.commons.cli.Options;

import com.on36.haetae.tools.SubCommand;
import com.on36.haetae.tools.utils.ProcessUtil;

/**
 * @author zhanghr
 * @date 2016年3月3日
 */
public class StartSubCommand implements SubCommand {

	@Override
	public String commandName() {
		return "start";
	}

	@Override
	public String commandDesc() {
		return "start a haetae server";
	}

	@Override
	public Options buildCommandlineOptions(Options options) {
		options.addOption("s", "source", true,
				"optional value:directory,maven; default: directory");
		options.addOption("c", "coords", true,
				"maven coords, example: com.ideal.shcrm:shcrm-cust-domain:1.0-SNAPSHOT");
		options.addOption("pn", "package", true,
				"service package name, example: com.ideal.shcrm.service");
		options.addOption("p", "port", true, "service port, default: 8080");
		options.addOption("r", "root", true,
				"root path name, default: /services");
		options.addOption("t", "threadPoolSize", true,
				"the size of thread pool  default: the twice maximum number of processors available to the virtual machine ");
		return options;
	}

	@Override
	public void execute(String... args) {
		if (args == null)
			args = new String[0];
		ProcessUtil.execHaetaeServer(true, args);
	}
}
