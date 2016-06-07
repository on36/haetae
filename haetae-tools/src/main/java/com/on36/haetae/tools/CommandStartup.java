/**
 * 
 */
package com.on36.haetae.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import com.on36.haetae.tools.server.HaetaeServerSubCommand;
import com.on36.haetae.tools.server.StopSubCommand;

/**
 * @author zhanghr
 * @date 2016年3月3日
 */
public class CommandStartup {

	protected static List<SubCommand> subCommandList = new ArrayList<SubCommand>();

	public static void initCommand() {
		initCommand(new HaetaeServerSubCommand());
		initCommand(new StopSubCommand());
	}

	public static void initCommand(SubCommand command) {
		subCommandList.add(command);
	}

	public static void main(String[] args) {
		initCommand();

		try {
			switch (args.length) {
			case 0:
				printHelp();
				break;
			case 2:
				if (args[0].equals("help")) {
					SubCommand cmd = findSubCommand(args[1]);
					if (cmd != null) {
						Options options = ServerUtil
								.buildCommandlineOptions(new Options());
						options = cmd.buildCommandlineOptions(options);
						if (options != null) {
							ServerUtil.printCommandLineHelp(
									"haetae " + cmd.commandName(), options);
						}
					} else {
						notFound(args[1]);
					}
					break;
				}
			case 1:
			default:
				SubCommand cmd = findSubCommand(args[0]);
				if (cmd != null) {
					// 将main中的args转化为子命令的args（去除第一个参数）
					String[] subargs = parseSubArgs(args);

					// 解析命令行
					Options options = ServerUtil
							.buildCommandlineOptions(new Options());
					final CommandLine commandLine = ServerUtil.parseCmdLine(
							"haetae " + cmd.commandName(), subargs,
							cmd.buildCommandlineOptions(options),
							new DefaultParser());
					if (null == commandLine) {
						System.exit(-1);
						return;
					}

					cmd.execute(commandLine);
				} else {
					notFound(args[0]);
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void notFound(String cmd) {
		System.out.println("The sub command \'" + cmd + "\' not exist.");
	}

	private static String[] parseSubArgs(String[] args) {
		if (args.length > 1) {
			String[] result = new String[args.length - 1];
			for (int i = 0; i < args.length - 1; i++) {
				result[i] = args[i + 1];
			}
			return result;
		}
		return null;
	}

	private static SubCommand findSubCommand(final String name) {
		for (SubCommand cmd : subCommandList) {
			if (cmd.commandName().toUpperCase().equals(name.toUpperCase())) {
				return cmd;
			}
		}
		return null;
	}

	private static void printHelp() {
		System.out.println("The most commonly used haetae commands are:");

		for (SubCommand cmd : subCommandList) {
			System.out.printf("   %-20s %s\n", cmd.commandName(),
					cmd.commandDesc());
		}

		System.out.println(
				"\nSee 'haetae help <command>' for more information on a specific command.");
	}
}
