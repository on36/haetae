package com.on36.haetae.tools.command;

import org.apache.commons.cli.Options;

import com.on36.haetae.tools.SubCommand;
import com.on36.haetae.tools.utils.ProcessUtil;

/**
 * @author zhanghr
 * @date 2016年8月24日 
 */
public class ZKSubCommand implements SubCommand {

	@Override
	public String commandName() {
		return "zookeeper";
	}

	@Override
	public String commandDesc() {
		return "start a local zookeeper server";
	}

	@Override
	public Options buildCommandlineOptions(Options options) {
		return options;
	}

	@Override
	public void execute(String... args) {
		if (args == null)
			args = new String[0];
		ProcessUtil.execJava("com.on36.haetae.tools.server.ZookeeperServer", true, args);
	}

}
