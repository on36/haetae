package com.on36.haetae.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.on36.haetae.api.annotation.Get;
import com.on36.haetae.api.annotation.Post;
import com.on36.haetae.http.Configuration;
import com.on36.haetae.http.Container;
import com.on36.haetae.http.RequestHandler;
import com.on36.haetae.http.Server;
import com.on36.haetae.http.core.HTTPServer;
import com.on36.haetae.net.udp.Scheduler;
import com.on36.haetae.server.core.RequestHandlerImpl;
import com.on36.haetae.server.core.container.HaetaeContainer;
import com.on36.haetae.server.core.manager.DisruptorManager;
import com.on36.haetae.server.scan.ScanTask;

import io.netty.handler.codec.http.HttpMethod;

/**
 * 
 * @author zhanghr
 * @date 2015年12月29日
 */
public class HaetaeServer {

	private final Container container;
	private final Server server;
	private final DisruptorManager disruptorManager;
	private final Scheduler scheduler;
	private final MessageThread msgThread;
	private final ScanTask sanner;
	public static ExecutorService threadPools;

	private Configuration conf = Configuration.create();

	public HaetaeServer(int port) {
		this(port, 0, null);
	}

	public HaetaeServer(int port, int threadPoolSize) {
		this(port, threadPoolSize, null);
	}

	public HaetaeServer(int port, int threadPoolSize, String rootPath) {
		conf.addResource("haetae.conf");
		if (rootPath != null)
			conf.set("httpServer.path.root", rootPath);

		threadPools = Executors.newCachedThreadPool();

		container = new HaetaeContainer();
		server = new HTTPServer(port, threadPoolSize, container);
		disruptorManager = new DisruptorManager(threadPools);
		scheduler = new MessageScheduler(disruptorManager);
		msgThread = new MessageThread(scheduler);
		sanner = new ScanTask(this);
	}

	public void start() {
		try {
			server.start();
			threadPools.submit(msgThread);
			threadPools.submit(sanner);
		} catch (Exception e) {
			e.printStackTrace();

			stop();
		}
	}

	public void stop() {
		if (server == null) {
			throw new IllegalStateException("server has not been started");
		}
		disruptorManager.close();
		msgThread.close();
		sanner.close();
		threadPools.shutdown();
		server.stop();
	}

	/**
	 * 注册一个服务.
	 * 
	 * @param resource
	 *            服务请求URI路径
	 * @param method
	 *            服务请求的方法类型 如GET、POST
	 * @return
	 */
	public RequestHandler register(String resource, HttpMethod method) {

		return register(resource, "1.0", method);
	}

	/**
	 * 注册一个服务.
	 * 
	 * @param resource
	 *            服务请求URI路径
	 * @param version
	 *            服务版本号
	 * @param method
	 *            服务请求的方法类型 如GET、POST
	 * @return
	 */
	public RequestHandler register(String resource, String version,
			HttpMethod method) {

		RequestHandlerImpl handler = new RequestHandlerImpl(scheduler);
		container.addHandler(handler, method, resource, version);
		return handler;
	}

	/**
	 * 注册一个get服务.
	 * 
	 * @param get
	 *            服务请求的get
	 * @return
	 */
	public RequestHandler register(Get get) {

		return register(get.value(), get.version(), HttpMethod.GET);
	}

	/**
	 * 注册一个post服务.
	 * 
	 * @param post
	 *            服务请求的post
	 * @return
	 */
	public RequestHandler register(Post post) {

		return register(post.value(), post.version(), HttpMethod.POST);
	}

	/**
	 * 注册一个服务,默认方法类型为GET.
	 * 
	 * @param resource
	 *            服务请求URI路径
	 * @return
	 */
	public RequestHandler register(String resource) {

		return register(resource, HttpMethod.GET);
	}

	/**
	 * 注销指定请求路径的所有服务.
	 * 
	 * @param resource
	 *            服务请求URI路径
	 * @return
	 */
	public boolean unregister(String resource) {

		return container.removeHandler(resource);
	}

	/**
	 * 搜索指定请求路径的服务.
	 * 
	 * @param resource
	 *            服务请求URI路径
	 * @return
	 */
	public RequestHandler find(String resource) {

		return container.findHandler(resource);
	}
}
