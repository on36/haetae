package com.on36.haetae.api.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Session {

	private static final int MAX_INACTIVE_INTERVAL = 5 * 60 * 1000;// max inactive interval
	
	private final String sessionId;
	private final long time;

	private final Map<String, Object> values = new HashMap<String, Object>();

	private boolean valid = true;

	public Session() {

		this.sessionId = UUID.randomUUID().toString();
		this.time = System.currentTimeMillis();
	}

	public String getSessionId() {

		return sessionId;
	}

	public long getCreateTime() {

		return time;
	}

	public Object get(String key) {

		return values.get(key);
	}

	public void set(String key, Object value) {

		values.put(key, value);
	}

	public Set<String> getAttributeNames() {

		return values.keySet();
	}

	public void invalidate() {

		this.valid = false;
	}

	public boolean valid() {
		if (!valid)
			return false;

		long current = System.currentTimeMillis();
		if ((current - time) < MAX_INACTIVE_INTERVAL)
			return true;
		else
			return false;
	}
}
