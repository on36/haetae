package com.on36.haetae.server.core.stats;

public class Statistics implements Comparable<Statistics>{

	private String path;
	private String method;

	private int successCount;
	private int failureCount;

	private long minElapsedTime;
	private long avgElapsedTime;
	private long maxElapsedTime;

	private int curRPS;
	private int maxRPS;

	//private int maxConcurrent;

	public Statistics(int successCount, int failureCount, long minElapsedTime,
			long avgElapsedTime, long maxElapsedTime, int curRPS, int maxRPS) {
		super();
		this.successCount = successCount;
		this.failureCount = failureCount;
		this.minElapsedTime = minElapsedTime;
		this.avgElapsedTime = avgElapsedTime;
		this.maxElapsedTime = maxElapsedTime;
		this.curRPS = curRPS;
		this.maxRPS = maxRPS;
	}

	public int getCurRPS() {
		return curRPS;
	}

	public void setCurRPS(int curRPS) {
		this.curRPS = curRPS;
	}

	public int getMaxRPS() {
		return maxRPS;
	}

	public void setMaxRPS(int maxRPS) {
		this.maxRPS = maxRPS;
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
		sb.append("}");
		return sb.toString();
	}

	@Override
	public int compareTo(Statistics o) {
		return getPath().compareTo(o.getPath());
	}
	
}
