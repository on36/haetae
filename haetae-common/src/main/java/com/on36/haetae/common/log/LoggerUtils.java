package com.on36.haetae.common.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.on36.haetae.common.utils.StringUtils;

/**
 * @author zhanghr
 * @date 2016年5月31日
 */
public class LoggerUtils {

	public static void startAccess() {
		start("com.on36.haetae.server.core.RequestHandlerImpl", "HAETAE",
				"access", "INFO");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void start(String className, String name, String suffix,
			String level) {

		String fileName = System.getProperty("haetae.log.name", "haetae");
		String logPath = System.getProperty("haetae.log.path", "../logs");
		StringBuilder sb = new StringBuilder();
		sb.append(logPath);
		if (!logPath.endsWith("/"))
			sb.append("/");
		sb.append(fileName);
		if (!StringUtils.isEmpty(suffix))
			sb.append("-" + suffix);
		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final Configuration config = ctx.getConfiguration();
		Layout layout = PatternLayout.createLayout("%m%n", null, config, null,
				null, false, false, null, null);
		Appender appender = RollingFileAppender.createAppender(
				sb.toString() + ".log",
				sb.toString() + "-%d{yyyy-MM-dd-HH}.log", "false", name, null,
				null, null, TimeBasedTriggeringPolicy.createPolicy("1", "true"),
				null, layout, null, null, "false", null, config);
		appender.start();
		config.addAppender(appender);
		AppenderRef ref = AppenderRef.createAppenderRef(name, null, null);
		AppenderRef[] refs = new AppenderRef[] { ref };
		LoggerConfig loggerConfig = LoggerConfig.createLogger(false,
				Level.valueOf(level), className, "true", refs, null, config,
				null);

		loggerConfig.addAppender(appender, null, null);
		config.addLogger(className, loggerConfig);
		ctx.updateLoggers();
	}

	public static void stop(String className, String name) {
		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final Configuration config = ctx.getConfiguration();
		config.getAppender(name).stop();
		config.getLoggerConfig(className).removeAppender(className);
		config.removeLogger(className);
		ctx.updateLoggers();
	}

	/**
	 * 获取Logger
	 *
	 * 如果不想使用slf4j,那这里改成直接返回Log4j的Logger即可
	 * 
	 * @param jobId
	 * @return
	 */
	public static Logger createLogger(String fileName) {
		return LoggerFactory.getLogger(fileName);
	}
}