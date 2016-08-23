package com.on36.haetae.server.core.body;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author zhanghr
 * @date 2016年3月26日
 */
public class ErrorResponseBody extends StringResponseBody {

	public ErrorResponseBody(Throwable e) {
		super(getErrorMessage(e));
	}

	private static String getErrorMessage(Throwable e) {
		return print(e);
	}

	private static String print(Throwable e) {
		StringWriter w = new StringWriter();
		PrintWriter p = new PrintWriter(w);
		try {
			e.printStackTrace(p);
			return w.toString();
		} finally {
			p.close();
		}
	}
}
