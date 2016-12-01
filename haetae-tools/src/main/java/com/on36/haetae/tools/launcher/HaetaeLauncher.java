package com.on36.haetae.tools.launcher;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.on36.haetae.hotswap.IClassLoader;
import com.on36.haetae.hotswap.classloader.DirectoryClassLoader;
import com.on36.haetae.hotswap.classloader.MavenClassLoader;
import com.on36.haetae.hotswap.scan.ClassPathPackageScanner;

/**
 * @author zhanghr
 * @date 2016年3月8日
 */
public class HaetaeLauncher {

	private static String source = "directory";
	private static int port = 1025;
	private static String path = "../ext";
	private static String coords = "com.on36.haetae:haetae-manager:0.0.4-SNAPSHOT";
	private static String url = "http://192.168.153.129:8081/repository/maven-public";
	private static int centralType = 0;
	private static IClassLoader cl = new DirectoryClassLoader();

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("c", "coords", true,
				"maven coords, example: com.on36.haetae:haetae-manager:0.0.4-SNAPSHOT");
		options.addOption("pa", "path", true,
				"directory classloader jar file path, default: ../ext");
		options.addOption("u", "url", true, "maven central url");
		options.addOption("p", "port", true, "service port, default: 1025");
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

				System.setProperty("haetae.log.name", "haetae-cluster-" + port);

				cl = getClassLoader();
				ClassLoader classLoader = cl.load();
				Class<?> haetaeServerClass = classLoader
						.loadClass("com.on36.haetae.server.HaetaeServer");

				List<String> clazzs = ClassPathPackageScanner.scan(classLoader,
						"com.on36.haetae.manager");
				Object obj = haetaeServerClass
						.getConstructor(int.class, int.class, String.class,
								List.class, ClassLoader.class)
						.newInstance(port, 0, "/cluster", clazzs, classLoader);
				Method method = haetaeServerClass.getMethod("start");
				method.invoke(obj);
			}
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
		if (line.hasOption("coords")) {
			coords = line.getOptionValue("coords");
			if (coords.indexOf("-SNAPSHOT") > -1)
				centralType = 0;
			else
				centralType = 1;

			source = "maven";
		} else
			source = "directory";

		if (line.hasOption("path")) {
			path = line.getOptionValue("path");
		}
		if (line.hasOption("url")) {
			url = line.getOptionValue("url");
		}
		if (line.hasOption("type")) {
			String ct = line.getOptionValue("type");
			centralType = Integer.parseInt(ct);
		}
	}

	private static void print(HelpFormatter formatter, Options options) {
		formatter.printHelp("haetae launcher [options] ", options);
	}

	private static IClassLoader getClassLoader() throws Exception {
		if ("directory".equals(source))
			return new DirectoryClassLoader(path);
		else if ("maven".equals(source))
			return new MavenClassLoader(url, centralType, coords);
		else
			throw new IllegalArgumentException(
					"illegal value of source =" + source);
	}
}
