/**
 * 
 */
package com.on36.haetae.tools;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/**
 * @author zhanghr
 * @date 2016年3月3日
 */
public class CommandStartup {

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("t", "thread", true, "thread count default:2");
		options.addOption("r", "runtime", true, "run time(s)  default:30");
		options.addOption("o", "operate", true,
				"operation type (get,set)  default:get");
		options.addOption("s", "vsize", true,
				"total bytes by value  default:32 byte");
		options.addOption("h", "help", false, "help information");
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption("help")) {
				print(formatter, options);
				System.exit(0);
			} else {
				parse(line);
				print(formatter, options);
			}
		} catch (Exception e) {
			e.printStackTrace();
			print(formatter, options);
			System.exit(0);
		}
	}
	
	private static void parse(CommandLine line) {
		
//		if (line.hasOption("thread")) {
//			String tms = line.getOptionValue("thread");//读取的参数为字符串
//		}
//		if (line.hasOption("runtime")) {
//			String tms = line.getOptionValue("runtime");//读取的参数为字符串
//		}
//		if (line.hasOption("operate")) {
//		}
//		if (line.hasOption("vsize")) {
//			String value = line.getOptionValue("vsize");//读取的参数为字符串
//		}
		
	}
	
	private static void print(HelpFormatter formatter, Options options) {
		formatter.printHelp("haetae [options] ", options);
	}
}