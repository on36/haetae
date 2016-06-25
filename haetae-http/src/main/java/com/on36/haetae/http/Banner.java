package com.on36.haetae.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.on36.haetae.net.utils.PidFile;

/**
 * @author zhanghr
 * @date 2016年1月12日
 */
public class Banner {

	public static void print(int port) {

		InputStream is = Banner.class.getClassLoader()
				.getResourceAsStream("banner.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		int i = 0;
		try {
			while ((line = reader.readLine()) != null) {
				System.out.print(line);
				if (i == 3)
					System.out.print("  Version: " + Version.CURRENT_VERSION);
				else if (i == 4)
					System.out.print("  Author: zhanghr");
				else if (i == 5)
					System.out.print("   Port: " + port);
				else if (i == 6)
					System.out.print("    Pid: " + Environment.pid());
				System.out.println();
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				new PidFile(port+"");
				reader.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// System.out.println(" __ __ __ ________ __________ __ ________");
		// System.out.println("/_/\ /_/\ | \ \ /_______/\ /_________/\ | \ \
		// /_______/\");
		// System.out.println("\:\ \:\ \ |:_\ \ \:\__:__\/ \_________\/ |:_\ \
		// \:\__:__\/");
		// System.out.println(" \:\__\:\ \ |(_)\ \ \ \______/\ \ \ \ |(_)\ \ \
		// \______/\");
		// System.out.println(" \:\__\:\ \ |: __\ \ \: __:__\/ \: \ \ |: __\ \
		// \: __:__\/ Version: "+ Version.CURRENT_VERSION);
		// System.out.println(" \:\ \\:\ \| \ `\ \ \ \/__:__/\ \: \ \ | \ `\ \ \
		// \/_____/\ Author: zhanghr");
		// System.out.println(" \_\/ \_\/|_\/ \_\/ \____:__\/ \__\/ |\/ \_\/
		// \____:__\/ Port: "+ port);
	}
}
