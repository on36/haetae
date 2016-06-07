package com.on36.haetae.tools.server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import com.on36.haetae.tools.SubCommand;

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
		return null;
	}

	@Override
	public void execute(CommandLine commandLine) {
		// TODO Auto-generated method stub

	}

}
