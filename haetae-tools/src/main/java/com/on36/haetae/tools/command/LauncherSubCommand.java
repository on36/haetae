package com.on36.haetae.tools.command;

import org.apache.commons.cli.Options;

import com.on36.haetae.tools.SubCommand;
import com.on36.haetae.tools.utils.ProccesUtil;

/**
 * @author zhanghr
 * @date 2016年3月8日
 */
public class LauncherSubCommand implements SubCommand {

	@Override
	public String commandName() {
		return "launcher";
	}

	@Override
	public String commandDesc() {
		return "start a haeatae launcher";
	}

	@Override
	public Options buildCommandlineOptions(Options options) {
		options.addOption("h", "help", false, "usage: haetae launcher <arg>; default:1015");
		return options;
	}

	@Override
	public void execute(String... args) {
		if (args == null)
			args = new String[0];
		ProccesUtil.execJava("com.on36.haetae.tools.updater.HaetaeLauncher",true,
				args);
	}

}
