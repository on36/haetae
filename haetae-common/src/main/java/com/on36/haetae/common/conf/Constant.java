package com.on36.haetae.common.conf;

/**
 * @author zhanghr
 * @date 2016年1月16日
 */
public class Constant {

	/**
	 * 指定应用名称，默认为 /app/crm
	 */
	public static final String K_SERVER_APP_NAME = "app.name";
	public static final String V_SERVER_APP_NAME = "/apps/crm";

	/**
	 * 指定根路径地址，默认为 /services
	 */
	public static final String K_SERVER_ROOT_PATH = "httpServer.path.root";
	public static final String V_SERVER_ROOT_PATH = "/services";

	/**
	 * 指定服务是否使用https协议
	 */
	public static final String K_SERVER_SSL_ENABLED = "httpServer.ssl.enabled";
	public static final boolean V_SERVER_SSL_ENABLED = false;

	/**
	 * 指定netty线程池大小,默认为0 取CPU物理核数的两倍
	 */
	public static final String K_SERVER_THREADPOOL_SIZE = "httpServer.threadpool.size";
	public static final int V_SERVER_THREADPOOL_SIZE = 0;

	/**
	 * 指定netty套接字握手队列大小
	 */
	public static final String K_SERVER_SOBACKLOG = "httpServer.sobacklog";
	public static final int V_SERVER_SOBACKLOG = 1024;

	/**
	 * 指定连接超时，单位毫秒
	 */
	public static final String K_SERVER_CONNECTTIMEOUT_MILLIS = "httpServer.connectTimeouMillis";
	public static final int V_SERVER_CONNECTTIMEOUT_MILLIS = 5000;

	/**
	 * 指定服务心跳间隔时间，单位毫秒
	 */
	public static final String K_SERVER_HEARTBEAT_PERIOD = "httpServer.heartbeat.period";
	public static final int V_SERVER_HEARTBEAT_PERIOD = 3000;

	/**
	 * 指定服务扫描的classloader加载的类
	 */
	public static final String K_SERVER_SCAN_CLASSLOADER = "httpServer.scan.classloader";
	public static final String V_SERVER_SCAN_CLASSLOADER = "com.on36.haetae.hotswap.classloader.DirectoryClassLoader";

	/**
	 * 指定zookeeper路径地址，默认为 localhost:2181
	 */
	public static final String K_ZOOKEEPER_ADDRESS_URL = "zookeeper.address.url";
	public static final String V_ZOOKEEPER_ADDRESS_URL = "localhost:2181";

	/**
	 * 指定zookeeper安全验证digest的用户名和密码
	 */
	public static final String K_ZOOKEEPER_AUTH_DIGEST = "zookeeper.digest";
	public static final String V_ZOOKEEPER_AUTH_DIGEST = "guest:guest123";

	/**
	 * 指定zookeeper session 超时时间
	 */
	public static final String K_ZOOKEEPER_SESSION_TIMEOUT = "zookeeper.session.timeout";
	public static final int V_ZOOKEEPER_SESSION_TIMEOUT = 5000;

}
