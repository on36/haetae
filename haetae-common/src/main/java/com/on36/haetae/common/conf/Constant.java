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
	 * 指定服务是否使用ssl协议
	 */
	public static final String K_SERVER_SSL_ENABLED = "httpServer.ssl.enabled";
	public static final boolean V_SERVER_SSL_ENABLED = false;

	/**
	 * 指定开启websocket,默认为true
	 */
	public static final String K_SERVER_WS_ENABLED = "httpServer.ws.enabled";
	public static final boolean V_SERVER_WS_ENABLED = true;

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
	 * 指定HttpClient客户端keepalive的值,默认值:true
	 */
	public static final String K_HTTPCLIENT_KEEPALIVE = "http.client.keepalive";
	public static final boolean V_HTTPCLIENT_KEEPALIVE = true;
	/**
	 * 指定HttpClient客户端requestTimeout的值,默认值：5000
	 */
	public static final String K_HTTPCLIENT_REQUEST_TIMEOUT = "http.client.request.timeout";
	public static final int V_HTTPCLIENT_REQUEST_TIMEOUT = 5000;
	/**
	 * 指定HttpClient客户端connectionIdleTimeout的值,默认值：60000
	 */
	public static final String K_HTTPCLIENT_CONNECTION_IDLE_TIMEOUT = "http.client.connection.idle.timeout";
	public static final int V_HTTPCLIENT_CONNECTION_IDLE_TIMEOUT = 60000;
	/**
	 * 指定HttpClient客户端connectionTtl的值,默认值：-1 为不限制
	 */
	public static final String K_HTTPCLIENT_CONNECTION_TTL = "http.client.connection.ttl";
	public static final int V_HTTPCLIENT_CONNECTION_TTL = -1;

	/**
	 * 指定zookeeper路径地址，默认为 localhost:2181
	 */
	public static final String K_ZOOKEEPER_ADDRESS_URL = "zookeeper.address.url";
	public static final String V_ZOOKEEPER_ADDRESS_URL = "localhost:2181";

	/**
	 * 指定zookeeper安全验证digest的用户名和密码
	 */
	public static final String K_ZOOKEEPER_AUTH_DIGEST_ADMIN = "zookeeper.digest.admin";
	public static final String V_ZOOKEEPER_AUTH_DIGEST_ADMIN = "admin:admin123";
	/**
	 * 指定zookeeper安全验证digest的用户名和密码
	 */
	public static final String K_ZOOKEEPER_AUTH_DIGEST_GUEST = "zookeeper.digest.guest";
	public static final String V_ZOOKEEPER_AUTH_DIGEST_GUEST = "guest:guest123";

	/**
	 * 指定zookeeper session 超时时间
	 */
	public static final String K_ZOOKEEPER_SESSION_TIMEOUT = "zookeeper.session.timeout";
	public static final int V_ZOOKEEPER_SESSION_TIMEOUT = 5000;

}
