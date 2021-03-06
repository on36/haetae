package com.on36.haetae.tools.process;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author zhanghr
 * @date 2016年3月19日
 */
public class ProcessTO {

	private String host;
	private String root;
	private int pid;
	private int port;
	private String packageName;
	private String coords;
	private long createTime;

	/**
	 * @param name
	 * @param pid
	 * @param port
	 * @param packageName
	 * @param coords
	 */
	public ProcessTO(int pid, int port, String root, String packageName,
			String coords) {
		super();
		try {
			this.host = InetAddress.getLocalHost().getHostName() + ":" + port;
			this.pid = pid;
			this.port = port;
			this.root = root;
			this.packageName = packageName;
			this.coords = coords;
			this.createTime = System.currentTimeMillis();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the root
	 */
	public String getRoot() {
		return root;
	}

	/**
	 * @param root
	 *            the root to set
	 */
	public void setRoot(String root) {
		this.root = root;
	}

	/**
	 * @return the pid
	 */
	public int getPid() {
		return pid;
	}

	/**
	 * @param pid
	 *            the pid to set
	 */
	public void setPid(int pid) {
		this.pid = pid;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @param packageName
	 *            the packageName to set
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * @return the coords
	 */
	public String getCoords() {
		return coords;
	}

	/**
	 * @param coords
	 *            the coords to set
	 */
	public void setCoords(String coords) {
		this.coords = coords;
	}

	/**
	 * @return the createTime
	 */
	public long getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

}
