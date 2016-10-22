package com.on36.haetae.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.on36.haetae.common.log.LoggerFactory;

/**
 * @author zhanghr
 * @date 2016年4月27日
 */
public class ThrowableUtils {

	public static Throwable makeThrowable(String errMsg) {
		Scanner scanner = new Scanner(errMsg);
		String line = null;
		int i = 0;
		Throwable exception = null;
		List<StackTraceElement> stes = new ArrayList<StackTraceElement>();
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();

			if (i == 0) {
				if (line.indexOf("Exception:") == -1)
					i = -1;
				else {
					String[] excs = line.split(": ");

					try {
						Class<?> clazz = Class.forName(excs[0]);
						exception = (Throwable) clazz
								.getConstructor(String.class)
								.newInstance(excs[1]);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (i > 0) {
				if (line.startsWith("\tat ")) {
					stes.add(buildStackTraceElement(line));
				} else
					break;
			}

			i++;
		}
		int len = stes.size();
		StackTraceElement[] steArr = new StackTraceElement[len];
		for (int index = 0; index < len; index++)
			steArr[index] = stes.get(index);

		exception.setStackTrace(steArr);
		scanner.close();

		return exception;
	}

	public static StackTraceElement buildStackTraceElement(String line) {
		line = line.replace("\tat ", "");
		String[] strs = line.split("\\(");
		String classMessage = strs[0].substring(0, strs[0].lastIndexOf("."));
		String methodMessage = strs[0].substring(strs[0].lastIndexOf(".") + 1);
		int lineNumber = -2;
		String fineName = null;
		if (strs[1].indexOf(":") > 0) {
			fineName = strs[1].substring(0, strs[1].indexOf(":"));
			String lineNum = strs[1].substring(strs[1].indexOf(":") + 1,
					strs[1].indexOf(")"));
			lineNumber = Integer.parseInt(lineNum);
		}
		return new StackTraceElement(classMessage, methodMessage, fineName,
				lineNumber);
	}

	public static void main(String[] args) throws Exception {
		String errMsg = "java.lang.Exception: Address[0.0.0.0/0.0.0.0:8080] already in use: bind\n"
				+ "\tat com.on36.haetae.http.core.HTTPServer.start(HTTPServer.java:137)\n"
				+ "\tat com.on36.haetae.server.HaetaeServer.start(HaetaeServer.java:139)\n"
				+ "\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n"
				+ "\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n"
				+ "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n"
				+ "\tat java.lang.reflect.Method.invoke(Method.java:498)\n"
				+ "\tat com.on36.haetae.tools.server.HaetaeServerStartup.main(HaetaeServerStartup.java:68)\n";
		System.out.println(errMsg);
		LoggerFactory.getLogger(ThrowableUtils.class).info("error",
				new Exception("start failed!",
						ThrowableUtils.makeThrowable(errMsg)));
	}
}
