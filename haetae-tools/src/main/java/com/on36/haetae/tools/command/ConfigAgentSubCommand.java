package com.on36.haetae.tools.command;

import org.apache.commons.cli.Options;

import com.on36.haetae.tools.SubCommand;
import com.on36.haetae.tools.utils.ProcessUtil;

/**
 * @author zhanghr
 * @date 2016年3月8日
 */
public class ConfigAgentSubCommand implements SubCommand {

	@Override
	public String commandName() {
		return "agent";
	}

	@Override
	public String commandDesc() {
		return "start a haeatae config agent";
	}

	@Override
	public Options buildCommandlineOptions(Options options) {
		options.addOption("h", "help", false,
				"usage: haetae agent <arg>; default:1984");
		return options;
	}

	@Override
	public void execute(String... args) {
		if (args == null)
			args = new String[0];
		ProcessUtil.execJava("com.on36.haetae.tools.launcher.HaetaeConfigAgent",
				true, args);
	}

}
