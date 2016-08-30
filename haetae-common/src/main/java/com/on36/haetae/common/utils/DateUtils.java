package com.on36.haetae.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zhanghr
 * @date 2016年3月29日
 */
public class DateUtils {

	public static String DEFFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	public static String toString(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static String toString(Date date) {
		return toString(date, DEFFAULT_DATE_FORMAT);
	}

	public static String getTimeZoneTime() {
		SimpleDateFormat sdf = new SimpleDateFormat(DEFFAULT_DATE_FORMAT);
		Calendar cal = Calendar.getInstance();
		return sdf.format(cal.getTime());
	}

	public static long getUnixTime() {
		Calendar cal = Calendar.getInstance();
		return cal.getTimeInMillis();
	}

	public static Date toDate(Object value) throws Exception {
		if (value == null) {
			return null;
		}

		if (value instanceof Calendar) {
			return ((Calendar) value).getTime();
		}

		if (value instanceof Date) {
			return (Date) value;
		}

		long longValue = -1;

		if (value instanceof Number) {
			longValue = ((Number) value).longValue();
		}

		if (value instanceof String) {
			String strVal = (String) value;
			if (strVal.length() == 0) {
				return null;
			}

			if (strVal.indexOf('-') != -1) {
				String format;
				if (strVal.length() == "yyyy-MM-dd HH:mm:ss".length()) {
					format = "yyyy-MM-dd HH:mm:ss";
				} else if (strVal.length() == 10) {
					format = "yyyy-MM-dd";
				} else if (strVal.toUpperCase().indexOf('T') != -1
						&& strVal.toUpperCase().indexOf('Z') != -1
						&& strVal.toUpperCase().indexOf('.') != -1) {
					format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
				} else if (strVal.toUpperCase().indexOf('T') != -1
						&& strVal.toUpperCase().indexOf('.') != -1) {
					format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
				} else if (strVal.toUpperCase().indexOf('T') != -1
						&& strVal.toUpperCase().indexOf('Z') != -1) {
					format = "yyyy-MM-dd'T'HH:mm:ss'Z'";
				} else if (strVal.toUpperCase().indexOf('T') != -1) {
					format = "yyyy-MM-dd'T'HH:mm:ssZ";
				} else {
					format = "yyyy-MM-dd HH:mm:ss.SSS";
				}

				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				try {
					return (Date) dateFormat.parse(strVal);
				} catch (ParseException e) {
					throw new Exception(
							"can not cast to Date, value : " + strVal);
				}
			}

			longValue = Long.parseLong(strVal);
		}

		if (longValue < 0) {
			throw new Exception("can not cast to Date, value : " + value);
		}

		return new Date(longValue);

	}

	public static void main(String[] args) throws Exception {
		System.out.println(getTimeZoneTime());
		System.out.println(toDate("1472458160000"));
	}
}
