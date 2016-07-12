package com.on36.haetae.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author zhanghr
 * @date 2016年3月20日
 */
public class ProccesTest {

	public static void main(String[] args) {
		try {
			ProcessBuilder pb = new ProcessBuilder("java", "-cp",
					System.getProperty("java.class.path"),
					"com.on36.haetae.test.ServerTest","\n && exit\n");
			pb.redirectErrorStream(true);
			Process process = pb.start();
			String s = null;
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			while ((s = bufferedReader.readLine()) != null)
				System.out.println(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
