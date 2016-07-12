package com.on36.haetae.tools.server;

import java.lang.reflect.Method;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.on36.haetae.hotswap.IClassLoader;
import com.on36.haetae.hotswap.classloader.DirectoryClassLoader;

/**
 * @author zhanghr
 * @date 2016年3月3日
 */
public class HaetaeServerStartup {

	private static int port = 8080;
	private static int threadPoolSize = 0;
	private static String rootPath = null;
	private static IClassLoader cl = new DirectoryClassLoader();

	public static void main(String[] args) {
		Options options = new Options();
		Option opt = new Option("m", "maven", true, "service port, default: 8080");
		opt.setRequired(true);
		options.addOption(opt);
		options.addOption("p", "port", true, "service port, default: 8080");
		options.addOption("r", "root", true,
				"root path name, default: /services");
		options.addOption("t", "threadPoolSize", true,
				"the size of thread pool  default: the twice maximum number of processors available to the virtual machine ");
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

				ClassLoader classLoader = cl.load();
				Class<?> haetaeServerClass = classLoader
						.loadClass("com.on36.haetae.server.HaetaeServer");
				Object obj = haetaeServerClass
						.getConstructor(int.class, int.class, String.class)
						.newInstance(port, threadPoolSize, rootPath);
				Method method = haetaeServerClass.getMethod("start");
				method.invoke(obj);
			}
		} catch (MissingOptionException e) {
			System.out.println(e.getMessage());
			print(formatter, options);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			print(formatter, options);
			System.exit(0);
		}
	}

	private static void parse(CommandLine line) {

		if (line.hasOption("port")) {
			String p = line.getOptionValue("port");
			port = Integer.parseInt(p);
		}
		if (line.hasOption("threadPoolSize")) {
			String size = line.getOptionValue("threadPoolSize");
			threadPoolSize = Integer.parseInt(size);
		}
		if (line.hasOption("root")) {
			String root = line.getOptionValue("root");

			if (!root.startsWith("/"))
				rootPath = "/" + root;
			else
				rootPath = root;
		}
	}

	private static void print(HelpFormatter formatter, Options options) {
		formatter.printHelp("haetae start [options] ", options);
	}
}