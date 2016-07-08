package com.on36.haetae.tools.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

	private static ExecutorService es = Executors.newCachedThreadPool();

	public static void execJava(String className, String... args) {
		List<String> list = new ArrayList<String>(4);
		list.add("java");
		list.add("-cp");
		list.add(System.getProperty("java.class.path"));
		list.add(className);
		if (args != null)
			list.addAll(Arrays.asList(args));
		exec(false, list);
	}

	public static void exec(boolean autoClosed, String... args) {
		exec(autoClosed, Arrays.asList(args));
	}

	public static void exec(final boolean autoClosed, final List<String> args) {

		Future<?> future = null;
		Process process = null;
		try {
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.redirectErrorStream(true);
			process = pb.start();
			future = es.submit(new ProcessTask(process));
			future.get(PROCCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		} catch (Exception e) {
			if (future != null) {
				future.cancel(true);
			}
		} finally {
			if (autoClosed && process != null)
				process.destroy();
			es.shutdownNow();
			// 执行完毕，强制退出
			System.exit(-1);
		}

	}

	public static void killProcess(int port) {

		String command = null;
		if (port > 0)
			command = "kill -9 `lsof -i :" + port + " |grep '(LISTEN)'| awk '{print $2}'`";
		else if (port == 0)
			command = "kill -9 `ps -au |grep java |grep com.on36.haetae.tools.server.HaetaeServerStartup |grep '(LISTEN)'| awk '{print $1}'`";
		else if (port == -1)
			command = "kill -9 `ps -au |grep java |grep com.on36.haetae.test.ServerTest |grep '(LISTEN)'| awk '{print $1}'`";
		System.out.println("executing " + command);
		exec(true, command);
	}

	static class ProcessTask implements Runnable {

		private InputStream inputStream;

		public ProcessTask(Process process) {
			this.inputStream = process.getInputStream();
		}

		@Override
		public void run() {
			try {
				String s = null;
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(inputStream));
				while ((s = bufferedReader.readLine()) != null) {
					System.out.println(s);

					// Thread.sleep(200);
					// if(inputStream.available() < 1)
					// break;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
