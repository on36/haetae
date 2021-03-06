package com.on36.haetae.tools.server;

import java.lang.reflect.InvocationTargetException;
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
 * @date 2016年3月3日
 */
public class HaetaeServerStartup {

	private static int port = 8080;
	private static int threadPoolSize = 0;
	private static String rootPath = "/services";
	private static String coords = "com.on36.crm:crm-cust:1.0-SNAPSHOT";
	private static String source = "directory";
	private static String path = "../ext";
	private static String url = "http://192.168.153.129:8081/repository/maven-public";
	private static int centralType = 0;
	private static String packageName = "com.on36.haetae.test";
	private static IClassLoader cl = null;

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("c", "coords", true,
				"maven coords, example: com.on36.crm:crm-cust:1.0-SNAPSHOT");
		options.addOption("pa", "path", true,
				"directory classloader jar file path, default: ../ext");
		options.addOption("u", "url", true, "maven central url");
		options.addOption("pn", "package", true,
				"service package name, example: com.ideal.shcrm.service");
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

				String rootName = rootPath.replace("/", "");
				System.setProperty("haetae.log.name",
						"haetae-" + rootName + "-" + port);

				cl = getClassLoader();
				ClassLoader classLoader = cl.load();
				Class<?> haetaeServerClass = classLoader
						.loadClass("com.on36.haetae.server.HaetaeServer");
				List<String> clazzStrings = ClassPathPackageScanner
						.scan(classLoader, packageName);
				if (clazzStrings == null || clazzStrings.size() == 0)
					throw new IllegalArgumentException(
							"There is no found any class in " + packageName
									+ " at " + coords);
				Object obj = haetaeServerClass.getConstructor(int.class,
						int.class, String.class, List.class, ClassLoader.class)
						.newInstance(port, threadPoolSize, rootPath,
								clazzStrings, classLoader);
				Method method = haetaeServerClass.getMethod("start");
				method.invoke(obj);
			}
		} catch (InvocationTargetException e) {
			e.getTargetException().printStackTrace();
			System.exit(0);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
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
		if (line.hasOption("package")) {
			packageName = line.getOptionValue("package");
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

	private static IClassLoader getClassLoader() throws Exception {
		if ("directory".equals(source))
			return new DirectoryClassLoader(path);
		else if ("maven".equals(source))
			return new MavenClassLoader(url, centralType,
					"com.on36.haetae:haetae-server:0.0.4-SNAPSHOT", coords);
		else
			throw new IllegalArgumentException(
					"illegal value of source =" + source);
	}
}