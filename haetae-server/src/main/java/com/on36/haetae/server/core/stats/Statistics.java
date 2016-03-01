package com.on36.haetae.server.core.stats;

public class Statistics {

	private int success;
	private int failure;

	private long minElapsedTime;
	private long avgElapsedTime;
	private long maxElapsedTime;

	private int maxConcurrent;

	private String resourcePath;
	private String method;

	public Statistics(int success, int failure, long minElapsedTime,
			long avgElapsedTime, long maxElapsedTime, int maxConcurrent) {
		this.success = success;
		this.failure = failure;
		this.minElapsedTime = minElapsedTime;
		this.avgElapsedTime = avgElapsedTime;
		this.maxElapsedTime = maxElapsedTime;
		this.maxConcurrent = maxConcurrent;
	}

	public int getMaxConcurrent() {
		return maxConcurrent;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public void setMaxConcurrent(int maxConcurrent) {
		this.maxConcurrent = maxConcurrent;
	}

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public int getFailure() {
		return failure;
	}

	public void setFailure(int failure) {
		this.failure = failure;
	}

	public long getMinElapsedTime() {
		return minElapsedTime;
	}

	public void setMinElapsedTime(long minElapsedTime) {
		this.minElapsedTime = minElapsedTime;
	}

	public long getAvgElapsedTime() {
		return avgElapsedTime;
	}

	public void setAvgElapsedTime(long avgElapsedTime) {
		this.avgElapsedTime = avgElapsedTime;
	}

	public long getMaxElapsedTime() {
		return maxElapsedTime;
	}

	public void setMaxElapsedTime(long maxElapsedTime) {
		this.maxElapsedTime = maxElapsedTime;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("{");
		sb.append("path:" + resourcePath + ", ");
		sb.append("method:" + method + ", ");
		sb.append("success:" + success + ", ");
		sb.append("failure:" + failure + ", ");
		sb.append("min elapsed time(ms):" + minElapsedTime + ", ");
		sb.append("avg elapsed time(ms):" + avgElapsedTime + ", ");
		sb.append("max elapsed time(ms):" + maxElapsedTime + ", ");
		sb.append("max Concurrent:" + maxConcurrent);
		sb.append("}");
		return sb.toString();
	}
}
