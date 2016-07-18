package com.on36.haetae.net.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class PidFile extends Thread {

	private String name;
	private static FileLock lock = null;
	private static FileOutputStream pidFileOutput = null;
	private static final String DEFAULT_HOME;

	static {
		File haetaeHome = new File(System.getProperty("java.io.tmpdir"),
				".haetae");
		try {
			File tmpFile = File.createTempFile("haeatae", "discovertmp");
			File tmpDir = tmpFile.getParentFile();
			tmpFile.delete();
			haetaeHome = new File(tmpDir, ".haeatae");
			haetaeHome.mkdir();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			DEFAULT_HOME = haetaeHome.getAbsolutePath();
		}
	};

	public PidFile(String name) {
		this.name = name;
		try {
			init();
		} catch (IOException ex) {
			clean();
			System.exit(-1);
		}
	}

	public void init() throws IOException {
		String pidLong = ManagementFactory.getRuntimeMXBean().getName();
		String[] items = pidLong.split("@");
		String pid = items[0];
		String haeataePath = System.getProperty("HAETAE_HOME");
		if (haeataePath == null) {
			haeataePath = DEFAULT_HOME;
		}
		StringBuffer pidFilesb = new StringBuffer();
		String pidDir = System.getenv("HAETAE_PID_DIR");
		if (pidDir == null) {
			pidDir = haeataePath + File.separator + "prccess";
		}
		pidFilesb.append(pidDir).append(File.separator).append(name)
				.append(".pid");
		try {
			File existsFile = new File(pidDir);
			if (!existsFile.exists()) {
				boolean success = (new File(pidDir)).mkdirs();
				if (!success) {
					throw (new IOException());
				}
			}
			File pidFile = new File(pidFilesb.toString());

			pidFileOutput = new FileOutputStream(pidFile);
			pidFileOutput.write(pid.getBytes());
			pidFileOutput.flush();
			FileChannel channel = pidFileOutput.getChannel();
			PidFile.lock = channel.tryLock();
			if (PidFile.lock != null) {
				System.out.println("Initlization succeeded...");
			} else {
				throw (new IOException(
						"Can not get lock on pid file: " + pidFilesb));
			}
		} catch (IOException ex) {
			System.out
					.println("Initialization failed: can not write pid file to "
							+ pidFilesb);
			System.exit(-1);
			throw ex;

		}

	}

	public void clean() {
		String haeataePath = System.getenv("HAETAE_HOME");
		if (haeataePath == null) {
			haeataePath = DEFAULT_HOME;
		}
		StringBuffer pidFilesb = new StringBuffer();
		String pidDir = System.getenv("HAETAE_PID_DIR");
		if (pidDir == null) {
			pidDir = haeataePath + File.separator + "prccess";
		}
		pidFilesb.append(pidDir).append(File.separator).append(name)
				.append(".pid");
		String pidFileName = pidFilesb.toString();

		File pidFile = new File(pidFileName);
		if (!pidFile.exists()) {
			System.out.println("Delete pid file, No such file or directory: "
					+ pidFileName);
		} else {
			try {
				lock.release();
				pidFileOutput.close();
			} catch (IOException e) {
				System.out
						.println("Unable to release file lock: " + pidFileName);
			}
		}

		boolean result = pidFile.delete();
		if (!result) {
			System.out.println("Delete pid file failed, " + pidFileName);
		}
	}

	public void run() {
		clean();
	}
}
