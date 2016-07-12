package com.on36.haetae.tools.utils;

import java.io.InputStream;
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
public class ProccesUtil {

	private static long PROCCESS_TIMEOUT_SECONDS = 10;
	private static String COMMAND_EXIT = "exit\n";

	private static ExecutorService es = Executors.newCachedThreadPool();

	public static Map<String,Object> execJava(String className, boolean autoExited,
			String... args) {
		List<String> list = new ArrayList<String>(4);
		list.add("java");
		list.add("-cp");
		list.add(System.getProperty("java.class.path"));
		list.add(className);
		if (args != null)
			list.addAll(Arrays.asList(args));
		return exec(false, autoExited, list);
	}

	public static Map<String,Object> exec(boolean autoClosed, boolean autoExited,
			String... args) {
		return exec(autoClosed, autoExited, Arrays.asList(args));
	}

	public static Map<String,Object> exec(final boolean autoClosed,
			final boolean autoExited, final List<String> args) {

		Map<String,Object> map = new HashMap<String,Object>();
		int resultCode = -1;
		Future<?> future = null;
		Process process = null;
		ProcessTask task = null;
		try {
			if (args != null) {
				args.add("\n");
				args.add(COMMAND_EXIT);
			}
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.redirectErrorStream(true);
			process = pb.start();
			task = new ProcessTask(process);
			future = es.submit(task);
			future.get(PROCCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
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
		map.put("code", resultCode);
		map.put("message", task.printf());
		return map;
	}

	public static Map<String,Object> killProcess(boolean autoExited, int port) {

		String command = null;
		if (port > 0)
			command = "kill -9 `lsof -i :" + port
					+ " |grep '(LISTEN)'| awk '{print $2}'`";
		else if (port == 0)
			command = "kill -9 `ps -au |grep java |grep com.on36.haetae.tools.server.HaetaeServerStartup |grep '(LISTEN)'| awk '{print $1}'`";
		else if (port == -1)
			command = "kill -9 `ps -au |grep java |grep com.on36.haetae.test.ServerTest |grep '(LISTEN)'| awk '{print $1}'`";
		System.out.println("executing " + command);
		return exec(true, autoExited, command);
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
					sb.append("\n");
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
