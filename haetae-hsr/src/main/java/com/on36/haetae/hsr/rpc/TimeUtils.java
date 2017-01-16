package com.on36.haetae.hsr.rpc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zhanghr
 * @date 2016年12月20日
 */
public class TimeUtils {

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyyMMddHHmmssSSSZ");

	public static String getCurrentTime() {
		return sdf.format(new Date());
	}

	public static String getCurrentDate() {

		return sdf.format(Calendar.getInstance().getTime());
	}
}
