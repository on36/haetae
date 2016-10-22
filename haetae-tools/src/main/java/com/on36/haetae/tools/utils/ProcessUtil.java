package com.on36.haetae.tools.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
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
		int pid = -1;
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
			future.get(PROCCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
			Class<?> processClass = process.getClass();
			String className = processClass.getName();
			if (className.equals("java.lang.UNIXProcess")) {
				Field pidField = processClass.getDeclaredField("pid");
				pidField.setAccessible(true);
				pid = (int) pidField.get(process);
			}
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
		String message = task.printf();
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("message", task.printf());
		if (pid > 0)
			result.put("pid", pid);
		result.put("success",
				message != null
						? (message.indexOf("Exception:") > -1 ? false
								: (resultCode == -1 ? true : false))
						: (resultCode == -1 ? true : false));
		return result;
	}

	public static String execAndAutoCloseble(List<String> args) {
		try {
			String result = null;
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.redirectErrorStream(true);
			Process process = pb.start();
			process.waitFor();
			Scanner scanner = new Scanner(process.getInputStream());
			if (scanner.hasNextLine()) {
				result = scanner.nextLine();
			}
			scanner.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String execAndAutoCloseble(String... args) {
		return execAndAutoCloseble(Arrays.asList(args));
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
