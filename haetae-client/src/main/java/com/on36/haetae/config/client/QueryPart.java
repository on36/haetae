package com.on36.haetae.config.client;

import java.util.HashMap;

/**
 * @author zhanghr
 * @date 2016年4月26日 
 */
public class QueryPart extends HashMap<String, String> {

	private static final long serialVersionUID = 1L;

	public QueryPart(String key, String value) {
		put(key, value);
	}

	public QueryPart addQueryPart(String key, String value) {
		put(key, value);
		return this;
	}
}
