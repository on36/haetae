package com.on36.haetae.server;

import io.netty.handler.codec.http.HttpMethod;

import com.on36.haetae.api.annotation.Path;
import com.on36.haetae.http.Container;
import com.on36.haetae.http.RequestHandler;
import com.on36.haetae.http.Server;
import com.on36.haetae.http.core.HTTPServer;
import com.on36.haetae.net.udp.Scheduler;
import com.on36.haetae.server.core.RequestHandlerImpl;
import com.on36.haetae.server.core.container.HaetaeContainer;
import com.on36.haetae.server.core.manager.DisruptorManager;

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

	public HaetaeServer(int port) {
		this(port, 0);
	}

	public HaetaeServer(int port, int threadPoolSize) {
		container = new HaetaeContainer();
		server = new HTTPServer(port, threadPoolSize, container);
		disruptorManager = new DisruptorManager();
		scheduler = new MessageScheduler(disruptorManager);
		msgThread = new MessageThread(scheduler);
	}

	public void start() {
		try {
			server.start();
			msgThread.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
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

		return register(resource, null, method);
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
	public RequestHandler register(String resource, String version, HttpMethod method) {
		
		RequestHandlerImpl handler = new RequestHandlerImpl(scheduler);
		container.addHandler(handler, method, resource, version);
		return handler;
	}
	/**
	 * 注册一个服务.
	 * 
	 * @param path
	 *            服务请求的path
	 * @param method
	 *            服务请求的方法类型 如GET、POST
	 * @return
	 */
	public RequestHandler register(Path path, HttpMethod method) {
		
		return register(path.value(), path.version(), method);
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
