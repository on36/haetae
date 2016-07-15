package com.on36.haetae.tools.utils;

import java.io.InputStream;
import java.io.OutputStream;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhanghr
 * @date 2016年3月3日
 */
public class ProccesUtil {

	private static long PROCCESS_TIMEOUT_SECONDS = 10;
	private static String COMMAND_EXIT = "exit\n";
	private static String COMMAND_LINE = "\n";

	private static ExecutorService es = Executors.newCachedThreadPool();

	public static Map<String, Object> execJava(String className,
			boolean autoExited, String... args) {
		List<String> list = new ArrayList<String>(4);
		list.add("java");
		list.add("-cp");
		list.add(System.getProperty("java.class.path"));
		list.add(className);
		if (args != null)
			list.addAll(Arrays.asList(args));
		return exec(false, autoExited, list);
	}

	public static Map<String, Object> exec(boolean autoClosed,
			boolean autoExited, String... args) {
		return exec(autoClosed, autoExited, Arrays.asList(args));
	}

	public static Map<String, Object> exec(final boolean autoClosed,
			final boolean autoExited, final List<String> args) {

		Map<String, Object> map = new HashMap<String, Object>();
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
		map.put("success", resultCode == -1 ? true : false);
		if (resultCode == -1) {
			map.put("message", "OK");
			map.put("pid", task.pid());
			map.put("port", task.port());
		} else
			map.put("message", task.printf());
		return map;
	}

	public static Map<String, Object> killProcess(boolean autoExited,
			int port) {

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
		
		private int port = -1;
		private int pid = -1;

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
					if(pid < 0)
						pid = getElementValue(s, "Pid");
					if(port < 0)
						port = getElementValue(s, "Port");
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
		public int port() {
			return port;
		}
		public int pid() {
			return pid;
		}

		private int getElementValue(String message, String elementString) {
			Matcher m = Pattern.compile(elementString + ": " + "([0-9]*)")
					.matcher(message);
			if (m.find()) {
				return Integer.parseInt(m.group(1));
			}
			return -1;
		}
	}
}
