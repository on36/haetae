package com.on36.haetae.server.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ping {
	private static int count = 4;

	public static long isReachable(String remoteIp) {
		BufferedReader in = null;
		Runtime r = Runtime.getRuntime(); // 将要执行的ping命令,此命令是windows格式的命令
		String osName = System.getProperties().getProperty("os.name");
		String pingCmd = null;
		if (osName.startsWith("Windows")) {
			pingCmd = "cmd /c ping -n {0} {1}";
		} else if (osName.startsWith("Linux")) {
			pingCmd = "ping -c {0} {1}";
		} else {
			System.out.println("not support OS " + osName);
			return -1;
		}
		pingCmd = MessageFormat.format(pingCmd, count, remoteIp);
		try {
			// 执行命令并获取输出
			Process p = r.exec(pingCmd);
			if (p == null) {
				return -1;
			}
			in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			// 逐行检查输出,计算类似出现=23ms TTL=62字样的次数
			int connectedCount = 0;
			int time = 0;
			String line = null;
			while ((line = in.readLine()) != null) {
				int t = getCheckResult(line);
				if (t > 0)
					connectedCount++;
				time += t;
			}

			if (connectedCount > 0) {
				long avg = time / connectedCount;
				return avg;
			}
			return -1;
		} catch (Exception ex) {
			ex.printStackTrace();
			// 出现异常则返回假
			return -1;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static int getCheckResult(String line) {
		Pattern pattern = Pattern.compile(".*?(=|<)([0-9. ]*?)ms",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			int time = Integer.parseInt(matcher.group(2).trim());
			return time;
		}
		return 0;
	}

	public static void main(String[] args) {
		System.out.println(Ping.isReachable("172.31.25.411"));
	}
}
