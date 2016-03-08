package com.on36.haetae.server.core.stats;

public class Statistics {

	private String path;
	private String method;
	
	private int successCount;
	private int failureCount;

	private long minElapsedTime;
	private long avgElapsedTime;
	private long maxElapsedTime;
	
	private int curTPS;
	private int maxTPS;

	private int maxConcurrent;

	public Statistics(int successCount, int failureCount, long minElapsedTime,
			long avgElapsedTime, long maxElapsedTime, int curTPS, int maxTPS,
			int maxConcurrent) {
		super();
		this.successCount = successCount;
		this.failureCount = failureCount;
		this.minElapsedTime = minElapsedTime;
		this.avgElapsedTime = avgElapsedTime;
		this.maxElapsedTime = maxElapsedTime;
		this.curTPS = curTPS;
		this.maxTPS = maxTPS;
		this.maxConcurrent = maxConcurrent;
	}


	public int getCurTPS() {
		return curTPS;
	}


	public void setCurTPS(int curTPS) {
		this.curTPS = curTPS;
	}


	public int getMaxTPS() {
		return maxTPS;
	}


	public void setMaxTPS(int maxTPS) {
		this.maxTPS = maxTPS;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setMaxConcurrent(int maxConcurrent) {
		this.maxConcurrent = maxConcurrent;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(int success) {
		this.successCount = success;
	}

	public int getFailureCount() {
		return failureCount;
	}

	public void setFailureCount(int failure) {
		this.failureCount = failure;
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
		sb.append("path:" + path + ", ");
		sb.append("method:" + method + ", ");
		sb.append("success:" + successCount + ", ");
		sb.append("failure:" + failureCount + ", ");
		sb.append("min elapsed time(ms):" + minElapsedTime + ", ");
		sb.append("avg elapsed time(ms):" + avgElapsedTime + ", ");
		sb.append("max elapsed time(ms):" + maxElapsedTime + ", ");
		sb.append("max Concurrent:" + maxConcurrent);
		sb.append("}");
		return sb.toString();
	}
}
