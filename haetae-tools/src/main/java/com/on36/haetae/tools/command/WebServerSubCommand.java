package com.on36.haetae.tools.command;

import org.apache.commons.cli.Options;

import com.on36.haetae.tools.SubCommand;
import com.on36.haetae.tools.utils.ProcessUtil;

/**
 * @author zhanghr
 * @date 2016年3月27日
 */
public class WebServerSubCommand implements SubCommand {

	@Override
	public String commandName() {
		return "web";
	}

	@Override
	public String commandDesc() {
		return "start a jetty web server";
	}

	@Override
	public Options buildCommandlineOptions(Options options) {
		options.addOption(null, "config", true, "_configFiles: conf/jetty.xml");
		options.addOption(null, "port", true, "port default: 8080");
		options.addOption(null, "path", true, "a context path");
		options.addOption(null, "log", true, "output a log file name");
		options.addOption(null, "context", true,
				"WAR file, web app dir or context xml file");
		options.addOption("h", "help", false, "help information");
		return options;
	}

	@Override
	public void execute(String... args) {
		if (args == null)
			args = new String[0];
		ProcessUtil.execWeb(true, args);
	}

}
