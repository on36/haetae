package com.on36.haetae.tools.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author zhanghr
 * @date 2016年3月3日
 */
public class ProcessUtil {

	private static long PROCCESS_TIMEOUT_SECONDS = 10;
	private static String COMMAND_EXIT = "exit\n";
	private static String COMMAND_LINE = "\n";
	private static int startingHeapSizeInMegabytes = 48;
	private static int maximumHeapSizeInMegabytes = 128;

	private static ExecutorService es = Executors.newCachedThreadPool();

	public static Map<String, Object> execJava(String javaRunTime,
			String className, int minHeapSize, int maxHeapSize,
			boolean autoExited, String... args) {
		List<String> list = new ArrayList<String>(4);
		list.add(javaRunTime);
		list.add(MessageFormat.format("-Xms{0}M", String.valueOf(
				minHeapSize > 0 ? minHeapSize : startingHeapSizeInMegabytes)));
		list.add(MessageFormat.format("-Xmx{0}M", String.valueOf(
				maxHeapSize > 0 ? maxHeapSize : maximumHeapSizeInMegabytes)));
		list.add("-cp");
		list.add(System.getProperty("java.class.path"));
		list.add(className);
		if (args != null)
			list.addAll(Arrays.asList(args));
		return exec(false, autoExited, list);
	}

	public static Map<String, Object> execJava(String className,
			boolean autoExited, String... args) {
		return execJava("java", className, 0, 0, autoExited, args);
	}

	public static Map<String, Object> execWeb(boolean autoExited,
			String... args) {
		return execJava("com.on36.haetae.tools.server.HaetaeWebServer",
				autoExited, args);
	}

	public static Map<String, Object> execHaetaeServer(boolean autoExited,
			String... args) {
		return execJava("com.on36.haetae.tools.server.HaetaeServerStartup",
				autoExited, args);
	}

	public static Map<String, Object> exec(boolean autoClosed,
			boolean autoExited, String... args) {
		return exec(autoClosed, autoExited, Arrays.asList(args));
	}

	public static Map<String, Object> exec(final boolean autoClosed,
			final boolean autoExited, final List<String> args) {

		int resultCode = -1;
		Future<?> future = null;
		Process process = null;
		ProcessTask task = null;
		OutputStream out = null;
		try {
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.redirectErrorStream(true);
			process = pb.start();
			out = process.getOutputStream();
			out.write(COMMAND_LINE.getBytes());
			out.write(COMMAND_EXIT.getBytes());
			out.flush();
			out.close();
			task = new ProcessTask(process);
			future = es.submit(task);
			process.waitFor(PROCCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
			resultCode = process.exitValue();
		} catch (Exception e) {
			if (future != null) {
				future.cancel(true);
			}
		} finally {
			if (autoClosed && process != null)
				process.destroy();
			// 执行完毕，强制退出
			if (autoExited) {
				es.shutdown();
				System.exit(-1);
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("message", task.printf());
		result.put("success", resultCode == -1 ? true : false);
		return result;
	}

	public static String execAndAutoCloseble(List<String> args) {
		try {
			StringBuffer sb = new StringBuffer();
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.redirectErrorStream(true);
			Process process = pb.start();
			process.waitFor();
			Scanner scanner = new Scanner(process.getInputStream());
			if (scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
			}
			scanner.close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String execAndAutoCloseble(String... args) {
		return execAndAutoCloseble(Arrays.asList(args));
	}

	public static Map<String, Object> killProcess(boolean autoExited,
			int port) {

		List<String> list = new ArrayList<String>(4);
		list.add("/bin/sh");
		list.add("-c");
		// if (port > 0)
		list.add("kill -15 `lsof -i :" + port
				+ " |grep '(LISTEN)'| awk '{print $2}'`");
		// else if (port == 0)
		// list.add(
		// "kill -15 `ps -au |grep java |grep
		// com.on36.haetae.tools.server.HaetaeServerStartup |grep '(LISTEN)'|
		// awk '{print $1}'`");
		// else if (port == -1)
		// list.add(
		// "kill -15 `ps -au |grep java |grep com.on36.haetae.test.ServerTest
		// |grep '(LISTEN)'| awk '{print $1}'`");
		return exec(true, autoExited, list);
	}

	static class ProcessTask implements Runnable {

		private InputStream inputStream;

		private StringBuffer sb = null;

		public ProcessTask(Process process) {
			this.inputStream = process.getInputStream();
			this.sb = new StringBuffer();
		}

		@Override
		public void run() {
			try {
				String s = null;
				Scanner scanner = new Scanner(inputStream);
				while (scanner.hasNextLine()) {
					s = scanner.nextLine();
					System.out.println(s);
					sb.append(s);
					sb.append(COMMAND_LINE);
				}
				scanner.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public String printf() {
			return sb.toString();
		}
	}
}
