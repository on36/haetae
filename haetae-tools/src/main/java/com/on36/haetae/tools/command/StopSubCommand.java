package com.on36.haetae.tools.command;

import org.apache.commons.cli.Options;

import com.on36.haetae.tools.SubCommand;
import com.on36.haetae.tools.utils.ProccesUtil;

/**
 * @author zhanghr
 * @date 2016年3月3日
 */
public class StopSubCommand implements SubCommand {

	@Override
	public String commandName() {
		// TODO Auto-generated method stub
		return "stop";
	}

	@Override
	public String commandDesc() {
		// TODO Auto-generated method stub
		return "stop a running server";
	}

	@Override
	public Options buildCommandlineOptions(Options options) {
		// TODO Auto-generated method stub
		options.addOption("p", "port", true, "service port, default: 8080");
		return options;
	}

	@Override
	public void execute(String... args) {
		// TODO Auto-generated method stub
	}

}
