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
		// TODO Auto-generated method stub
		return options;
	}

	@Override
	public void execute(String... args) {
		// TODO Auto-generated method stub
		ProccesUtil.execJava("com.on36.haetae.tools.updater.HaetaeUpdater",args);
	}

}
