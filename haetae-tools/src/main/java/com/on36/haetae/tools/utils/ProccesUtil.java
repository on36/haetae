package com.on36.haetae.tools.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * @author zhanghr
 * @date 2016年3月3日
 */
public class ProccesUtil {

	public static void execJava(String className, String... args) {
		try {
			ProcessBuilder pb = new ProcessBuilder("java", "-cp",
					System.getProperty("java.class.path"), className);
			if (args != null || args.length > 0)
				pb.command().addAll(Arrays.asList(args));
			
			pb.redirectErrorStream(true);
			Process process = pb.start();
			String s = null;
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			while ((s = bufferedReader.readLine()) != null)
				System.out.println(s);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
