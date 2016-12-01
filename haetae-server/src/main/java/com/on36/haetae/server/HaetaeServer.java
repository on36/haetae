package com.on36.haetae.server;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Api;
import com.on36.haetae.api.annotation.ApiDoc;
import com.on36.haetae.api.http.MediaType;
import com.on36.haetae.api.http.MethodType;
import com.on36.haetae.common.conf.Configuration;
import com.on36.haetae.common.conf.Constant;
import com.on36.haetae.common.log.LogLevel;
import com.on36.haetae.common.log.LoggerUtils;
import com.on36.haetae.hsr.EventBus;
import com.on36.haetae.http.Container;
import com.on36.haetae.http.RequestHandler;
import com.on36.haetae.http.Scheduler;
import com.on36.haetae.http.Server;
import com.on36.haetae.http.core.HTTPServer;
import com.on36.haetae.server.core.RequestHandlerImpl;
import com.on36.haetae.server.core.container.HaetaeContainer;
import com.on36.haetae.server.core.manager.event.EndpointEvent;
import com.on36.haetae.server.core.manager.event.HttpRequestEvent;
import com.on36.haetae.server.core.manager.event.LogEvent;
import com.on36.haetae.server.core.manager.event.handler.EndpointEventListener;
import com.on36.haetae.server.core.manager.event.handler.HttpRequestEventListener;
import com.on36.haetae.server.core.manager.event.handler.LogEventListener;

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
			List<String> clazzes) {
		this(port, threadPoolSize, rootPath, clazzes,
				Thread.currentThread().getContextClassLoader());
	}

	public HaetaeServer(int port, int threadPoolSize, String rootPath,
			List<String> clazzes, ClassLoader classLoader) {
		Exception excp = conf.addResource("haetae.conf");
		if (rootPath != null)
			conf.set(Constant.K_SERVER_ROOT_PATH, rootPath);

		String root = conf.get(Constant.K_SERVER_ROOT_PATH,
				Constant.V_SERVER_ROOT_PATH);
		if (root.equals("/cluster"))
			conf.set(Constant.K_SERVER_WS_ENABLED, "true");

		String rootName = root.replace("/", "");
		System.setProperty("haetae.log.name",
				"haetae-" + rootName + "-" + port);

		LoggerUtils.startAccess();

		this.clazzes = clazzes;
		this.classLoader = classLoader;
		if (this.clazzes != null)
			runningMode = MODE.CLASSES;

		threadPools = Executors.newCachedThreadPool();

		scheduler = new MessageScheduler();
		container = new HaetaeContainer(scheduler);
		server = new HTTPServer(port, threadPoolSize, container);
		hbThread = new Heartbeat(root, port, scheduler);

		EventBus.addListener(LogEvent.class, new LogEventListener());
		EventBus.addListener(EndpointEvent.class, new EndpointEventListener());
		EventBus.addListener(HttpRequestEvent.class, new HttpRequestEventListener());
		
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
					for (String classString : clazzes) {
						if (classString.startsWith("com.on36.haetae.manager")) {
							LoggerUtils.start("com.on36.haetae.manager", "MGR",
									"biz", "INFO");
						} else {
							String packageName = classString.substring(0,
									classString.lastIndexOf("."));
							LoggerUtils.start(packageName, "biz", "biz",
									"INFO");
						}
						Class<?> clazz = classLoader.loadClass(classString);
						Method[] methods = clazz.getDeclaredMethods();
						Object object = null;

						for (Method method : methods) {
							Class<?>[] clazzs = method.getParameterTypes();
							if (clazzs.length == 1 && clazzs[0].getName()
									.equals(Context.class.getName())) {

								Api api = method.getAnnotation(Api.class);
								ApiDoc apiDoc = method
										.getAnnotation(ApiDoc.class);
								if (api != null) {
									if (object == null)
										object = clazz.newInstance();
									register(api, apiDoc).with(object, method);
								}
							} // else
								// scheduler.trace(clazz, LogLevel.WARN,
								// method.getName()
								// + " has no argurment Context");
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
	public RequestHandler register(String resource, String methodName) {

		if (runningMode == MODE.CLASSES)
			runningMode = MODE.MIX;

		return register(resource, "1.0", methodName, null);
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
	public RequestHandler register(String resource, MethodType method) {

		return register(resource, "1.0", method.value(), null);
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
			String methodName, String contentType) {
		if (runningMode == MODE.CLASSES)
			runningMode = MODE.MIX;
		RequestHandlerImpl handler = new RequestHandlerImpl();
		container.addHandler(handler, methodName, resource, version,
				contentType);
		return handler;
	}

	/**
	 * 注册一个API服务.
	 * 
	 * @param api
	 *            服务请求的api
	 * @return
	 */
	public RequestHandler register(Api api) {

		return register(api, MediaType.TEXT_JSON.value());
	}

	public RequestHandler register(Api api, ApiDoc apiDoc) {

		return register(api, apiDoc, MediaType.TEXT_JSON.value());
	}

	public RequestHandler register(Api api, String contentType) {

		return register(api, null, contentType);
	}

	/**
	 * 注册一个API服务.
	 * 
	 * @param api
	 *            服务请求的api
	 * @param apiDoc
	 *            服务请求的api doc 描述
	 * @return
	 */
	public RequestHandler register(Api api, ApiDoc apiDoc, String contentType) {

		if (runningMode == MODE.CLASSES)
			runningMode = MODE.MIX;
		RequestHandlerImpl handler = new RequestHandlerImpl(api, apiDoc);
		container.addHandler(handler, api.method().value(), api.value(),
				api.version(), contentType);
		return handler;
	}

	/**
	 * 注册一个服务,默认方法类型为GET.
	 * 
	 * @param resource
	 *            服务请求URI路径
	 * @return
	 */
	public RequestHandler register(String resource) {

		return register(resource, MethodType.GET);
	}

	/**
	 * 注销指定请求路径的所有服务.
	 * 
	 * @param resource
	 *            服务请求URI路径
	 * @param methodName
	 *            http方法名
	 * @param version
	 *            版本号
	 * @return
	 */
	public boolean unregister(String resource, String methodName,
			String version) {

		return container.removeHandler(resource, methodName, version);
	}

	public boolean unregister(String resource, String methodName) {

		return unregister(resource, methodName, null);
	}

	public boolean unregister(String resource) {

		return unregister(resource, null, null);
	}

	/**
	 * 搜索指定请求路径的服务,返回指定版本号服务,如果没有找到则返回最新版本号服务
	 * 
	 * @param resource
	 *            服务请求URI路径
	 * @param methodName
	 *            http方法名
	 * @param version
	 *            版本号
	 * @return
	 */
	public RequestHandler find(String resource, String methodName,
			String version) {

		return container.findHandler(resource, methodName, version);
	}

	/**
	 * 搜索指定请求路径的服务,默认返回最新版本.
	 * 
	 * @param resource
	 *            服务请求URI路径
	 * @param methodName
	 *            http方法名
	 * @return
	 */
	public RequestHandler find(String resource, String methodName) {

		return find(resource, methodName, null);
	}

	/**
	 * 搜索指定请求路径的服务.
	 * 
	 * @param api
	 *            服务请求URI路径
	 * @return
	 */
	public RequestHandler find(Api api) {

		return find(api.value(), api.method().value(), api.version());
	}
}
