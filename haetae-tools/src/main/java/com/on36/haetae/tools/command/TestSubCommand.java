package com.on36.haetae.tools.command;

import org.apache.commons.cli.Options;

import com.on36.haetae.tools.SubCommand;
import com.on36.haetae.tools.utils.ProccesUtil;

/**
 * @author zhanghr
 * @date 2016年3月5日
 */
public class TestSubCommand implements SubCommand {

	@Override
	public String commandName() {
		return "test";
	}

	@Override
	public String commandDesc() {
		return "start a test haetae server";
	}

	@Override
	public Options buildCommandlineOptions(Options options) {
		return options;
	}

	@Override
	public void execute(String... args) {
		ProccesUtil.execJava("com.on36.haetae.test.ServerTest", args);
	}

}
