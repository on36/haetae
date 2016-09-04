package com.on36.haetae.api.manager;

import com.on36.haetae.api.Context;

/**
 * @author zhanghr
 * @date 2016年5月4日
 */
public class ContextManager {
	public static final String HEADER_TRACE_ID = "Haetae-Trace-Id";
	public static final String HEADER_PARENT_ID = "Haetae-Parent-ID";
	private static ThreadLocal<Context> threadLocal = new ThreadLocal<Context>();

	public static Context get() {
		return threadLocal.get();
	}

	public static void set(Context context) {
		threadLocal.set(context);
	}

	public static void destroy() {
		threadLocal.remove();
	}
}
