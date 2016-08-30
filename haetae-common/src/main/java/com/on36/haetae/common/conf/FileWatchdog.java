package com.on36.haetae.common.conf;

import java.io.File;

import org.apache.log4j.helpers.LogLog;

/**
 * @author zhanghr
 * @date 2016年1月30日
 */
public abstract class FileWatchdog extends Thread {

	/**
	 * The default delay between every file modification check, set to 60
	 * seconds.
	 */
	static final public long DEFAULT_DELAY = 60000;
	/**
	 * The name of the file to observe for changes.
	 */
	protected String filename;

	/**
	 * The delay to observe between every check. By default set
	 * {@link #DEFAULT_DELAY}.
	 */
	protected long delay = DEFAULT_DELAY;

	File file;
	long lastModif = 0;
	boolean warnedAlready = false;
	boolean interrupted = false;

	protected FileWatchdog(String filename) {
		super("FileWatchdog");
		this.filename = filename;
		file = new File(filename);
		setDaemon(true);
		checkAndConfigure();
	}

	/**
	 * Set the delay to observe between each check of the file changes.
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}

	abstract protected void doOnChange();

	protected void checkAndConfigure() {
		boolean fileExists;
		try {
			fileExists = file.exists();
		} catch (SecurityException e) {
			LogLog.warn("Was not allowed to read check file existance, file:["
					+ filename + "].");
			interrupted = true; // there is no point in continuing
			return;
		}

		if (fileExists) {
			long l = file.lastModified(); // this can also throw a
											// SecurityException
			if (l > lastModif) { // however, if we reached this point this
				lastModif = l; // is very unlikely.
				doOnChange();
				warnedAlready = false;
			}
		} else {
			if (!warnedAlready) {
				LogLog.debug("[" + filename + "] does not exist.");
				warnedAlready = true;
			}
		}
	}

	public void run() {
		while (!interrupted) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// no interruption expected
			}
			checkAndConfigure();
		}
	}
}
