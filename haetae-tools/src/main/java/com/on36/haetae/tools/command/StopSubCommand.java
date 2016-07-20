package com.on36.haetae.tools.command;

import org.apache.commons.cli.Options;

import com.on36.haetae.tools.SubCommand;
import com.on36.haetae.tools.utils.ProcessUtil;

/**
 * @author zhanghr
 * @date 2016年3月3日
 */
public class StopSubCommand implements SubCommand {

	private int port = 8080;

	@Override
	public String commandName() {
		return "stop";
	}

	@Override
	public String commandDesc() {
		return "stop running server";
	}

	@Override
	public Options buildCommandlineOptions(Options options) {
		options.addOption("h", "help", false, "usage: haetae stop <arg>; default:8080");
		return options;
	}

	@Override
	public void execute(String... args) {
		if (args != null && args.length == 1)
			port = Integer.parseInt(args[0]);
		else 
			System.out.println("No specify port, using default port 8080");
		ProcessUtil.killProcess(true,port);
	}

}
