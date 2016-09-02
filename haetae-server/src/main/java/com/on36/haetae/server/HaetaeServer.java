package com.on36.haetae.server;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Get;
import com.on36.haetae.api.annotation.Post;
import com.on36.haetae.api.http.MediaType;
import com.on36.haetae.common.conf.Configuration;
import com.on36.haetae.common.conf.Constant;
import com.on36.haetae.common.log.LogLevel;
import com.on36.haetae.common.log.LoggerUtils;
import com.on36.haetae.http.Container;
import com.on36.haetae.http.RequestHandler;
import com.on36.haetae.http.Server;
import com.on36.haetae.http.core.HTTPServer;
import com.on36.haetae.net.udp.Scheduler;
import com.on36.haetae.server.core.RequestHandlerImpl;
import com.on36.haetae.server.core.container.HaetaeContainer;
import com.on36.haetae.server.core.manager.DisruptorManager;

import io.netty.handler.codec.http.HttpMethod;

/**
 * 
 * @author zhanghr
 * @date 2015年12月29日
 */
public class HaetaeServer {
	enum MODE {
		REGISTER, // 注册模式，通过基础注册服务
		CLASSES, // 类模式，通过外部CLASS定义接口注册服务
		MIX// 混合模式
	}

	private final Container container;
	private final Server server;
	private final DisruptorManager disruptorManager;
	private final Heartbeat hbThread;
	private final List<String> clazzes;
	private final ClassLoader classLoader;
	private MODE runningMode = MODE.REGISTER;

	private static Scheduler scheduler = null;
	private static ExecutorService threadPools;

	private Configuration conf = Configuration.create();

	public static Scheduler getScheduler() {
		return scheduler;
	}

	public static ExecutorService getThreadPoolExecutor() {
		return threadPools;
	}

	public HaetaeServer(int port) {
		this(port, 0, null, null, null);
	}

	public HaetaeServer(int port, int threadPoolSize) {
		this(port, threadPoolSize, null, null, null);
	}

	public HaetaeServer(int port, String rootPath) {
		this(port, 0, rootPath, null, null);
	}

	public HaetaeServer(int port, int threadPoolSize, String rootPath,
			List<String> clazzes, ClassLoader classLoader) {
		Exception excp = conf.addResource("haetae.conf");
		if (rootPath != null)
			conf.set(Constant.K_SERVER_ROOT_PATH, rootPath);

		String root = conf.getString(Constant.K_SERVER_ROOT_PATH,
				Constant.V_SERVER_ROOT_PATH);

		String rootName = root.replace("/", "");
		System.setProperty("haetae.log.name",
				"haetae-" + rootName + "-" + port);

		LoggerUtils.startAccess();

		this.clazzes = clazzes;
		this.classLoader = classLoader;
		if (this.clazzes != null)
			runningMode = MODE.CLASSES;

		threadPools = Executors.newCachedThreadPool();

		disruptorManager = new DisruptorManager(threadPools);
		scheduler = new MessageScheduler(disruptorManager);
		container = new HaetaeContainer(scheduler);
		server = new HTTPServer(port, threadPoolSize, container);
		hbThread = new Heartbeat(root, port, scheduler);

		if (excp != null)
			scheduler.trace(Configuration.class, LogLevel.WARN,
					excp.getMessage());
	}

	public void start() throws Exception {

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				scheduler.trace(this.getClass(), LogLevel.WARN,
						"Server is shutsowning...");
				close();
			}
		});
		try {
			switch (runningMode) {
			case CLASSES:
				if (clazzes == null || clazzes.size() == 0)
					throw new Exception(
							"there is no found any service class at running mode:"
									+ runningMode);
				else {
					
					LoggerUtils.start("com.on36.haetae.manager", "biz", "biz", "INFO");
					for (String classString : clazzes) {
						Class<?> clazz = classLoader.loadClass(classString);
						Method[] methods = clazz.getDeclaredMethods();
						Object object = null;

						for (Method method : methods) {
							Class<?>[] clazzs = method.getParameterTypes();
							if (clazzs.length == 1 && clazzs[0].getName()
									.equals(Context.class.getName())) {

								Post post = method.getAnnotation(Post.class);
								Get get = method.getAnnotation(Get.class);
								if (object == null)
									object = clazz.newInstance();
								if (post != null) {
									if (find(post.value()) == null)
										register(post).with(object, method);
								} else if (get != null) {
									if (find(get.value()) == null)
										register(get).with(object, method);
								}
							}
						}
					}
				}
				break;
			default:
				break;
			}

			threadPools.submit(hbThread);
			server.start();
		} catch (Exception e) {
			close();
			throw e;
		}
	}

	public void close() {
		hbThread.close();
		disruptorManager.close();
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

		if (runningMode == MODE.CLASSES)
			runningMode = MODE.MIX;

		return register(resource, "1.0", method, null);
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
	private RequestHandler register(String resource, String version,
			HttpMethod method, String contentType) {

		RequestHandlerImpl handler = new RequestHandlerImpl();
		container.addHandler(handler, method, resource, version, contentType);
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

		return register(get.value(), get.version(), HttpMethod.GET,
				MediaType.TEXT_JSON.value());
	}

	public RequestHandler register(Get get, String contentType) {

		return register(get.value(), get.version(), HttpMethod.GET,
				contentType);
	}

	/**
	 * 注册一个post服务.
	 * 
	 * @param post
	 *            服务请求的post
	 * @return
	 */
	public RequestHandler register(Post post) {

		return register(post.value(), post.version(), HttpMethod.POST,
				MediaType.TEXT_JSON.value());
	}

	public RequestHandler register(Post post, String contentType) {

		return register(post.value(), post.version(), HttpMethod.POST,
				contentType);
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
