package com.on36.haetae.tools;

import org.apache.commons.cli.Options;

/**
 * @author zhanghr
 * @date 2016年3月3日
 */
public interface SubCommand {
	public String commandName();

	public String commandDesc();

	public Options buildCommandlineOptions(final Options options);

	public void execute(String...args);
}
